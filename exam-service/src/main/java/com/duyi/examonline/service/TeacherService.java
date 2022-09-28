package com.duyi.examonline.service;

import com.duyi.examonline.common.vo.PageVO;
import com.duyi.examonline.domain.Teacher;

import java.util.List;


public interface TeacherService {
    void save(Teacher teacher);
    Teacher getTeacherByName(String tname);
    Teacher getTeacherByName(String tname,String mnemonic_code);
    void updateTeacher(String tname,String mnemonic_code,Long id);
    void updatePass(Long id,String pass);
    PageVO getTeachers(int page,int rows,String tname);
    void addTeacher(String name,String mnemonic_code,String pass);
    Teacher getTeacherById(Long id);
    void deleteTeacher(Long id);
    String saves(List<Teacher> teachers);
    List<Teacher> findAll();
}
