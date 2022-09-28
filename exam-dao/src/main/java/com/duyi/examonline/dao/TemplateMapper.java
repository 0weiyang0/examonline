package com.duyi.examonline.dao;

import com.duyi.examonline.domain.Template;

public interface TemplateMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Template record);

    int insertSelective(Template record);

    Template selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Template record);

    int updateByPrimaryKey(Template record);
}