package io.dataease.dto.chart;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Author gin
 * @Date 2021/3/1 3:51 下午
 */
@Getter
@Setter
public class Series {
    private String name;
    private String type;
    private String stack;
    private String shaft; // principal(主轴) or auxiliary（副轴）
    private List<Object> data;
}
