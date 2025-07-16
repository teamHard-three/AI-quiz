package com.aiquiz.aiquizs.service.impl;

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


@Service
@Slf4j
public class TeacherServiceImpl extends ServiceImpl<CourseContentMapper, CourseContent> implements TeacherService {
    @Autowired
    private CourseService courseService;
    @Override
    public boolean uploadCourseContent(MultipartFile file, Long courseId) throws IOException {
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
        return true;
    }
    private Boolean updPDF(File file, Long courseId) throws IOException {

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
        return true;
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

    private Boolean udppptx(File file, Long courseId) throws IOException {

        StringBuilder result = new StringBuilder();

        XMLSlideShow ppt = new XMLSlideShow(new FileInputStream(file));

        int pageIndex = 1;
        for (XSLFSlide slide : ppt.getSlides()) {
            Tesseract tess  = new Tesseract();
            File tessDataFolder = ResourceUtils.getFile("classpath:tessdata");
            tess.setDatapath(tessDataFolder.getAbsolutePath());  // 注意：必须是tessdata目录，不是文件
            tess.setLanguage("chi_sim");
            result.append("===== 第 ").append(pageIndex).append(" 页 =====\n");

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

        return true;
    }
}
