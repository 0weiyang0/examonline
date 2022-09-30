package com.duyi.examonline.service;

import com.duyi.examonline.common.vo.PageVO;
import com.duyi.examonline.domain.Student;

import java.util.List;
import java.util.Map;

/**
 * @author 臧娜
 * @version 1.0
 * @date 2022/9/27 10:07
 */
public interface StudentService {
    /**
     * 单一学生信息的保存
     * @param student
     */
    void save(Student student);
    /**
     * 批量导入时的数据保存
     * @param students
     * @return  具有一定格式的信息，具体信息见
     * @See  TeacherService/TeacherServiceImpl#saves
     * */
    String saves(List<Student> students);
    //查询班级
    PageVO find(int pageNo, Map condition);

    boolean addStudent(Map allParam);
    List<Student> findStudentsByClasses(Map condition);


}
