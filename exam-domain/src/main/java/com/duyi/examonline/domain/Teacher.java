package com.duyi.examonline.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data@AllArgsConstructor
public class Teacher {
    private Long id;

    private String tname;

    private String mnemonicCode;

    private String pass;

    private String yl1;

    private String yl2;

    private String yl3;

    private String yl4;

    private Date createTime;

    private Date updateTime;

}