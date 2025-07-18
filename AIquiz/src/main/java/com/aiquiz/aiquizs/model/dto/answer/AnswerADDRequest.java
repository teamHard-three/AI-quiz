package com.aiquiz.aiquizs.model.dto.answer;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AnswerADDRequest implements Serializable {


    /**
     * 课程 id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long courserId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long questionId;
    /**
     * 用户id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long studentId;

    /**
     * 用户答案（JSON 数组）
     */
    private List<String> choices;

    private static final long serialVersionUID = 1L;
}