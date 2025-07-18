package com.aiquiz.aiquizs.service.impl;

import cn.hutool.json.JSONUtil;
import com.aiquiz.aiquizs.model.entity.CourseQuestion;
import com.aiquiz.aiquizs.service.CourseQuestionService;
import org.json.JSONArray;
import org.json.JSONObject;
import com.aiquiz.aiquizs.config.AIConfigLoader;
import com.aiquiz.aiquizs.mapper.CourseContentMapper;
import com.aiquiz.aiquizs.model.entity.Course;
import com.aiquiz.aiquizs.model.entity.CourseContent;
import com.aiquiz.aiquizs.model.vo.CourseVO;
import com.aiquiz.aiquizs.service.CourseService;
import com.aiquiz.aiquizs.service.TeacherService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import okhttp3.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xslf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


@Service
@Slf4j
public class TeacherServiceImpl extends ServiceImpl<CourseContentMapper, CourseContent> implements TeacherService {
    @Autowired
    private CourseService courseService;
    @Autowired
    private CourseQuestionService courseQuestionService;
    private ExecutorService threadPool = new ThreadPoolExecutor(
            10,                      // corePoolSize 核心线程数
            15,                      // maximumPoolSize 最大线程数
            60L,                    // keepAliveTime 非核心线程空闲超时时间
            TimeUnit.SECONDS,       // 时间单位
            new ArrayBlockingQueue<>(100), // 工作队列
            Executors.defaultThreadFactory(), // 线程工厂
            new ThreadPoolExecutor.AbortPolicy() // 拒绝策略
    );
    @Override
    public int uploadCourseContent(MultipartFile file, Long courseId) throws IOException {
        String originalFilename = file.getOriginalFilename(); // 获取原始文件名
        String extension = "";

        if (originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        }


        // 创建临时文件（系统临时目录）
        File tempFile = File.createTempFile("upload_", "." + extension);
        file.transferTo(tempFile);


        switch (extension) {
            case "pdf":
                synchronized (extension.intern()) {
                    System.out.println("这是一个 PDF 文件");
                    return updPDF(tempFile, courseId);
                }


            case "pptx":
                synchronized (extension.intern()) {
                    System.out.println("这是一个 PPTX 文件");
                    return udppptx(tempFile, courseId);
                }

            default:
                System.out.println("不支持的文件类型：" + extension);
        }
        tempFile.deleteOnExit();
        return 0;
    }

    @Override
    public CourseQuestion getQuestion(Long courseId) throws IOException {
        // 根据课程ID查询题目列表
        List<CourseQuestion> questions = courseQuestionService.list(new LambdaQueryWrapper<CourseQuestion>()
                .eq(CourseQuestion::getCourseId, courseId)
                .eq(CourseQuestion::getIsDelete, 0)); // 不查已删除
        String question = "";
        for ( CourseQuestion courseQuestion : questions ) {
            question += courseQuestion.getQuestion() + "\n";
        }
        String GENERATE_QUESTION_SYSTEM_MESSAGE = "你是一位严谨的出题专家，我会给你如下信息：\n" +
                question +
                "请你根据上述信息,如果你无法理解上述信息请返回空json，按照以下步骤来出题：\n" +
                "1. 要求：请你严格按照在所有的题目中挑选出10道有难度有深度，符合教师教学应提问的,不要重复，十道问题\n" +
                "2. 严格按照下面的 json 格式输出题目和选项\n" +

                "[{\"options\":[{\"score\":0,\"value\":\"选项内容\",\"key\":\"A\"},{\"score\":0,\"value\":\"\",\"key\":\"B\"},{\"score\":0,\"value\":\"\",\"key\":\"C\"},{\"score\":1,\"value\":\"\",\"key\":\"D\"}],\"title\":\"题目标题\"}]\n" +

                "title 是题目，options 是选项，score 取值1或者0,正确答案为1,错误答案为0,每个选项的 key 按照英文字母序（比如 A、B、C、D）以此类推，value 是选项内容\n" +
                "3. 检查题目是否包含序号，若包含序号则去除序号\n" +
                "4. 返回的题目列表格式必须为 JSON 数组";



        String url = "https://openrouter.ai/api/v1/chat/completions";
        String apiKey = AIConfigLoader.getApiKey();

        JSONArray messages = new JSONArray();
        messages.put(new JSONObject()
                .put("role", "user")
                .put("content", GENERATE_QUESTION_SYSTEM_MESSAGE));

        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build();

        JSONObject body = new JSONObject()
                .put("model", "deepseek/deepseek-chat-v3-0324:free")
                .put("messages", messages)
                .put("stream", false)   // 非流式
                .put("temperature", 0.7);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(body.toString(), MediaType.parse("application/json")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new RuntimeException("请求失败: " + response);

            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                String responseStr = responseBody.string();
                JSONObject result = new JSONObject(responseStr);

                // 取出AI回答文本
                String answer = result.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");

                System.out.println("AI回答：" + answer);
                //删除原有题目
                courseQuestionService.remove(new LambdaQueryWrapper<CourseQuestion>()
                        .eq(CourseQuestion::getCourseId, courseId));
                // 保存到数据库
                String json = answer.replace("json", "");// 去除json
                CourseQuestion courseQuestion = new CourseQuestion();
                courseQuestion.setCourseId(courseId);
                courseQuestion.setQuestion(json);  // 这里要确认字段类型是String类型
                courseQuestionService.save(courseQuestion);


                return courseQuestion;
            }
        }
        return new CourseQuestion();
    }
    @Override
    public Boolean createQuestion(String page, Long courseid) {
        // 根据课程ID查询课程内容
        CourseContent courseContent = this.getOne(new LambdaQueryWrapper<CourseContent>()
                .eq(CourseContent::getCourseId, courseid)); // 获取最新的课程内容
        String text = courseContent.getContent();
        if (text == null || text.isEmpty()) {
            log.error("课程内容为空，无法生成题目");
            return null;
        }

        String[] pages = text.split("===== 第 \\d+ 页 =====");
        for (int i = 0; i < pages.length; i++) {
            final int index = i; // lambda 中不能使用非 final 的 i
            threadPool.submit(() -> {
        String content = pages[index];
        String GENERATE_QUESTION_SYSTEM_MESSAGE = "你是一位严谨的出题专家，我会给你如下信息：\n" +
                content +
                "请你根据上述信息,如果你无法理解上述信息请返回空json，按照以下步骤来出题：\n" +
                "1. 要求：一轮十道选择题，请根据内容,彻底理解传入信息,严格符合教师所讲知识点，有深度有广度。不要包含序号，每题的选项数为4个且只有一个唯一答案，题目不能重复\n" +
                "2. 严格按照下面的 json 格式输出题目和选项\n" +

                "[{\"options\":[{\"score\":0,\"value\":\"选项内容\",\"key\":\"A\"},{\"score\":0,\"value\":\"\",\"key\":\"B\"},{\"score\":0,\"value\":\"\",\"key\":\"C\"},{\"score\":1,\"value\":\"\",\"key\":\"D\"}],\"title\":\"题目标题\"}]\n" +

                "title 是题目，options 是选项，score 取值1或者0,正确答案为1,错误答案为0,每个选项的 key 按照英文字母序（比如 A、B、C、D）以此类推，value 是选项内容\n" +
                "3. 检查题目是否包含序号，若包含序号则去除序号\n" +
                "4. 返回的题目列表格式必须为 JSON 数组";

        System.out.println("第 "+index+"页内容由 " + Thread.currentThread().getName() + " 处理：" + GENERATE_QUESTION_SYSTEM_MESSAGE);

        String url = "https://openrouter.ai/api/v1/chat/completions";
        String apiKey = AIConfigLoader.getApiKey();

        JSONArray messages = new JSONArray();
        messages.put(new JSONObject()
                .put("role", "user")
                .put("content", GENERATE_QUESTION_SYSTEM_MESSAGE));

        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build();

        JSONObject body = new JSONObject()
                .put("model", "deepseek/deepseek-chat-v3-0324:free")
                .put("messages", messages)
                .put("stream", false)   // 非流式
                .put("temperature", 0.7);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(body.toString(), MediaType.parse("application/json")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new RuntimeException("请求失败: " + response);

            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                String responseStr = responseBody.string();
                JSONObject result = new JSONObject(responseStr);

                // 取出AI回答文本
                String answer = result.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");

                System.out.println("AI回答：" + answer);

                // 保存到数据库
                CourseQuestion courseQuestion = new CourseQuestion();
                courseQuestion.setCourseId(courseid);
                courseQuestion.setQuestion(answer);  // 这里要确认字段类型是String类型
                courseQuestionService.save(courseQuestion);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



    }
            );}return true;
    }

    private int updPDF(File file, Long courseId) throws IOException {

                StringBuilder result = new StringBuilder();
        PDDocument doc = PDDocument.load(file);

        int pageCount = doc.getNumberOfPages();
        PDFRenderer renderer = new PDFRenderer(doc);

        for (int i = 0; i < pageCount; i++) {
            Tesseract tess  = new Tesseract();
            File tessDataFolder = ResourceUtils.getFile("classpath:tessdata");
            tess.setDatapath(tessDataFolder.getAbsolutePath());  // 注意：必须是tessdata目录，不是文件
            tess.setLanguage("chi_sim");

            // 1. 提取当前页文字
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage(i + 1); // 页码是从 1 开始的
            stripper.setEndPage(i + 1);
            String pageText = stripper.getText(doc);

            // 2. 渲染当前页为图片
            BufferedImage image = renderer.renderImageWithDPI(i, 150); // 不是300

            // 3. 做 OCR 提取图片中文字
            BufferedImage rgbImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g = rgbImage.createGraphics();
            g.drawImage(image, 0, 0, null);
            g.dispose();
            image.flush(); // 加上这一行
            String ocrText = "";
            try {
                ocrText = tess.doOCR(rgbImage);
            } catch (Exception e) {
                ocrText = "[OCR失败: " + e.getMessage() + "]";
            }
            // 4. 合并结果
            result.append("===== 第 ").append(i + 1).append(" 页 =====\n");
            result.append("【文字内容】\n").append(pageText).append("\n");
            result.append("【图片OCR文字】\n").append(ocrText).append("\n\n");
        }

        doc.close();

        // 将结果保存到数据库或文件中
        CourseContent courseContent = new CourseContent();
        courseContent.setCourseId(courseId);
        courseContent.setContent(result.toString());
        // mybatisplus 存入数据库
        this.save(courseContent);
        return pageCount;
    }

    @Override
    public List<CourseVO> getCourseList(Long id) {
        // 根据教师ID查询课程列表
        List<Course> teachercourse = courseService.list(new LambdaQueryWrapper<Course>()
                .eq(Course::getTeacherId, id)
                .eq(Course::getIsDelete, 0));// 不查已删除
        // 将Course转换为CourseVO//hutool包
        List<CourseVO> courseVOList =new ArrayList<>();
        for (Course course : teachercourse) {
            CourseVO courseVO = new CourseVO();
            courseVO.setId(course.getId());
            courseVO.setName(course.getName());
            courseVO.setDescription(course.getDescription());

            // 其他属性根据需要设置
            courseVOList.add(courseVO);

        }
           return courseVOList;
    }

    private int udppptx(File file, Long courseId) throws IOException {

        StringBuilder result = new StringBuilder();

        XMLSlideShow ppt = new XMLSlideShow(new FileInputStream(file));

        int pageIndex = 0;
        for (XSLFSlide slide : ppt.getSlides()) {
            Tesseract tess  = new Tesseract();
            File tessDataFolder = ResourceUtils.getFile("classpath:tessdata");
            tess.setDatapath(tessDataFolder.getAbsolutePath());  // 注意：必须是tessdata目录，不是文件
            tess.setLanguage("chi_sim");
            result.append("===== 第 ").append(pageIndex+1).append(" 页 =====\n");

            // 1. 提取文字
            StringBuilder slideText = new StringBuilder();
            for (XSLFShape shape : slide.getShapes()) {
                if (shape instanceof XSLFTextShape textShape) {
                    slideText.append(textShape.getText()).append("\n");
                }
            }
            result.append("【文字内容】\n").append(slideText).append("\n");

            // 2. 提取图片 OCR
            StringBuilder imageText = new StringBuilder();
            for (XSLFShape shape : slide.getShapes()) {
                if (shape instanceof XSLFPictureShape picShape) {
                    BufferedImage img = ImageIO.read(new ByteArrayInputStream(picShape.getPictureData().getData()));
                    if (img != null) {
                        BufferedImage rgbImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
                        Graphics2D g = rgbImage.createGraphics();
                        g.drawImage(img, 0, 0, null);
                        g.dispose();

                        try {
                            String ocr = tess.doOCR(rgbImage);
                            imageText.append(ocr).append("\n");
                        } catch (Exception e) {
                            imageText.append("[OCR 失败: ").append(e.getMessage()).append("]\n");
                        }
                    }
                }
            }
            result.append("【图片OCR文字】\n").append(imageText).append("\n\n");

            pageIndex++;
        }
        ppt.close();
        // 将结果保存到数据库或文件中
        CourseContent courseContent = new CourseContent();
        courseContent.setCourseId(courseId);
        courseContent.setContent(result.toString());
        // mybatisplus 存入数据库
        this.save(courseContent);

        return pageIndex;
    }
}
