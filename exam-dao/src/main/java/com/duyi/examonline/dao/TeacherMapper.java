package com.duyi.examonline.dao;

import com.duyi.examonline.common.vo.PageVO;
import com.duyi.examonline.domain.Teacher;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TeacherMapper {

    int insert(Teacher record);

    Teacher getTeacherByName(@Param("tname") String tname);

    Teacher getTeacherByName(@Param("tname") String tname,@Param("mnemonic_code") String mnemonic_code);

    void updatePass(@Param("id") Long id, @Param("pass") String pass);

    Long total(@Param("tname") String name);

    List<Teacher> getTeachers(@Param("start")int start, @Param("length") int length, @Param("tname") String tname);

    void addTeacher(@Param("tname") String tname,@Param("mnemonic_code") String mnemonic_code,@Param("pass")String pass);

    void updateTeacher(@Param("tname")String tname,@Param("mnemonic_code")String mnemonic_code,@Param("id") Long id);

    int deleteByPrimaryKey(Long id);

    List<Teacher> findAll();

    int insertSelective(Teacher record);

    Teacher selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Teacher record);

    int updateByPrimaryKey(Teacher record);
}