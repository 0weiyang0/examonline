package com.duyi.examonline.test;

import com.duyi.examonline.domain.Teacher;
import com.duyi.examonline.service.TeacherService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.LinkedList;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring.xml")
public class Test1 {

    @Test
    public void t1(){
        System.out.println("测试");
    }
    @Autowired
    private TeacherService teacherService;

//    初始化一个教师信息
//    @Test
//    public void testSaveTeacher(){
//        Teacher teacher = new Teacher();
//        teacher.setTname("lisi");
//        teacher.setPass("123");
//        teacherService.save(teacher);
//    }


    @Test
    public void testSplit(){
        String str = new String("1,2,3,4,5,6,");
        String[] strArr = str.split(",");
        for (int i = 0; i < strArr.length; i++) {
            String s = strArr[i];
            System.out.print(s);
        }
        System.out.println("============================");
        for (String s : strArr) {
            System.out.print(s);
        }
        System.out.println(strArr.length);
    }




}
