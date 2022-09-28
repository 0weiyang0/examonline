package com.duyi.examonline.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data@AllArgsConstructor
public class Student {
    private Long id;

    private String code;

    private String sname;

    private String mnemonicCode;

    private String pass;

    private Integer grade;

    private String major;

    private String classNo;

    private String yl1;

    private String yl2;

    private String yl3;

    private String yl4;

    private Date createTime;

    private Date updateTime;
}