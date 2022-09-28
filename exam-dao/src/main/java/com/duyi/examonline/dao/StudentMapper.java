package com.duyi.examonline.dao;
import com.duyi.examonline.domain.Student;
import java.util.List;
import java.util.Map;

public interface StudentMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Student record);

    /**
     * 用 Map 来接收查询到数据，查 grade , major , classNo
                         组合成   【年级-专业-班级】
     * @param condition
     * @return
     */
    List<Map> findClasses(Map condition);

    int insertSelective(Student record);

    Student selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Student record);

    int updateByPrimaryKey(Student record);
}