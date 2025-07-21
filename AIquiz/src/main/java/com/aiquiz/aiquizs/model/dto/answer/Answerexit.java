package com.aiquiz.aiquizs.model.dto.answer;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
public class Answerexit implements Serializable {


    /**
     * 课程 id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long courserId;
    private int page;
    private static final long serialVersionUID = 1L;
}