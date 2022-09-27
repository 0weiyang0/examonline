package com.duyi.examonline.controller;
import cn.hutool.crypto.digest.DigestUtil;
import com.duyi.examonline.domain.Teacher;
import com.duyi.examonline.service.TeacherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 提供一些基本的功能处理
 */
@Controller
public class CommonController {
    @Autowired
    private TeacherService teacherService;
    private Logger logger = LoggerFactory.getLogger(CommonController.class);
    @RequestMapping("/login")
    public String toLogin(){
        return "common/login";
    }
    @RequestMapping("/common/login1")
    @ResponseBody
    public boolean toLoginMainPage(String tname, String pass, HttpSession session){
        //由于在注册的时候就会将密码加密，所以这次登录也需要加密
        pass = DigestUtil.md5Hex(pass);
        //1. 用户名是否存在
        Teacher teacher = teacherService.getTeacherByName(tname);
        if (teacher == null){
            logger.info(tname + "存在");
            return false;
        }
        //2. 密码是否正确
        if (!teacher.getPass().equals(pass)){
            logger.info(pass + "不正确");
            return false;
        }
        //3. 用户名存在，并且密码也正确的时候将用户放置到Session中
        session.setAttribute("loginTeacher",teacher);
        //注意，不正确返回false，正确返回true
        return true ;
    }
//    这里犯了一个错误就是，thymeleaf不能直接访问html网页，
//    必须访问到controller然后在转给thymeleaf引擎去处理
    @RequestMapping("/common/main.html")
    public String toMainPage(){
        return "common/main" ;
    }
//退出系统
    @RequestMapping("/common/exit")
    public String exit(HttpSession session){
        session.invalidate();
        //或者可以使用另一个方法
      //  session.removeAttribute("loginTeacher");
        return "common/login";
    }
    //超时或未登录，在拦截器拦截到的
    @RequestMapping("/common/timeout.html")
    public  String  notLogin(){
        return "common/timeout";
    }
    @RequestMapping("/common/timeout")
    public  String  timeout(){
        return "common/login";
    }
    @RequestMapping("/common/updatePwdTemplate.html")
    public String toUpdateHtml(){
        System.out.println("去找修改密码的页面了");
        return "common/updatePwdTemplate";
    }
    @RequestMapping("/common/updatePass")
    @ResponseBody
    public boolean update(String old_pass,String new_pass,HttpSession session){
        //首先判断原密码是否正确
        Teacher teacher =(Teacher) session.getAttribute("loginTeacher");
        old_pass = DigestUtil.md5Hex(old_pass);
        if (!old_pass.equals(teacher.getPass())){
            return false;
        }else{
            //原密码正确，把旧密码也加密，然后，对数据库中的密码进行更新
            new_pass = DigestUtil.md5Hex(new_pass);
            //把new_pass的数值转到数据库当中
            teacherService.updatePass(teacher.getId(),new_pass);
            return true;
        }
    }

}
