package com.duyi.examonline.controller;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.extra.pinyin.PinyinUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.duyi.examonline.common.Constant;
import com.duyi.examonline.common.vo.PageVO;
import com.duyi.examonline.domain.Teacher;
import com.duyi.examonline.service.TeacherService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
/**
 * @author 臧娜
 * @version 1.0
 * @date 2022/9/22 16:36
 */
@Controller
public class TeacherController {
    @Autowired
    private TeacherService teacherService;
    //这个是在点击教师管理所映射的类，所以没有t_name信息
    @RequestMapping("/teacher/teacherList.html")
    public String toTeacherList(Model model){
        //在这里查询教师信息，然后返回给teacherList页面
       PageVO pageVO = teacherService.getTeachers(Constant.DEFAULT_PAGE,Constant.DEFAULT_ROWS,null);
       model.addAttribute("pageVO",pageVO);
       return "teacher/teacherList";
    }
    @RequestMapping("/teacher/pageTemplate.html")
    public String toPageTemplate(int page , String tname,Model model){
        PageVO pageVO = teacherService.getTeachers(page, Constant.DEFAULT_ROWS,tname) ;
        model.addAttribute("pageVO",pageVO) ;
        //只访问teacher.html中 【 id = pageTemplate 】  标签部分
        return "teacher/teacherList::#pageTemplate" ;
    }
    @RequestMapping("/teacher/formTemplate.html")
    public String toAddTeacher(Long id,Model model){
        System.out.println("此时修改的id为： " + id);
        if (id != null){
            //说明是修改操作，则在数据库中根据id查找该数据
          Teacher teacher = teacherService.getTeacherById(id);
          model.addAttribute("id",teacher.getId());
        }
        return "teacher/formTemplate";
    }
    @RequestMapping("/teacher/addTeacher")
    @ResponseBody
    public boolean addTeacher(String tname){
        //在数据库中添加数据
        //添加助记码
        //在这里我们设置一个默认的密码，然后存储的数据库中
        String mnemonic_code = PinyinUtil.getPinyin(tname,"");
        String pass = DigestUtil.md5Hex("123");
        try{
            teacherService.addTeacher(tname,mnemonic_code,pass);
        }catch (Exception e){
            System.out.println("已存在【"+tname+"】用户");
            return false;
        }
        return true;
    }
    @RequestMapping("/teacher/updateTeacher")
    @ResponseBody
    public boolean updateTeacher(String tname,Long id){
        System.out.println(id);
        String mnemonic_code =  PinyinUtil.getPinyin(tname,"");
        //在数据库中查看是否有重名的
        Teacher teacher =  teacherService.getTeacherByName(tname,mnemonic_code);
        if (teacher == null){
            //说明没有重名的，可以修改 tname和mnemonic  放进去
            teacherService.updateTeacher(tname,mnemonic_code,id);
            return true;
        }
        return false;
    }
    @RequestMapping("/teacher/deleteAllTeachers")
    @ResponseBody
    public void deleteAllTeachers(String ids){
       String[] idArray =   ids.split(",");
        System.out.println(idArray);
        for (String id : idArray) {
            teacherService.deleteTeacher(Long.parseLong(id));
        }
    }
    //找teacher/importsTemplate.html模板的方法
    @RequestMapping("/teacher/importsTemplate.html")
    public String importsTemplate(){
        return "teacher/importsTemplate";
    }
    //文件的下载
    @RequestMapping("/teacher/downTemplate")
    public ResponseEntity<byte[]> downloadTemplate() throws IOException {
        InputStream is = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("files/teacher.xlsx");
        byte[] bytes = new byte[is.available()];
        IOUtils.read(is,bytes);//从输入流中读取字节数，放到bytes中
        HttpHeaders headers = new HttpHeaders();
        //说明作为一个附属让用户下载，它还有一个属性值，是inline，说明直接展示在浏览器中
        //而attachment 的意思是作为附件下载
        headers.add("content-disposition","attachment;filename=teachers.xlsx");
        headers.add("content-type","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        return new ResponseEntity<byte[]>(bytes,headers, HttpStatus.OK);
    }
    //文件上传进行保存老师的数据
    @RequestMapping(value="/teacher/imports",produces = "text/html;charset=utf-8")
    @ResponseBody
    public String saves(MultipartFile excel) throws IOException {
        /**
         * 刚开始这里发生了一个异常MultipartFile file
         * 是因为 方法中的参数  MultipartFile excel必须和input中的name一致
         * */
        //使用hutool工具提供的对poi的封装
       ExcelReader excelReader = ExcelUtil.getReader(excel.getInputStream());
       //这个excelReader  对excel进行读取
        excelReader.addHeaderAlias("教师名称","tname");
        List<Teacher> teachers = excelReader.readAll(Teacher.class);
        String meg = teacherService.saves(teachers);
        return  meg;
    }
    //教师信息导出
    @RequestMapping("/teacher/exportTeachers")
    public ResponseEntity<byte[]> exportTeachers(){
       List<Teacher> teachers =  teacherService.findAll();
       //利用hutool工具
       ExcelWriter excelWriter = ExcelUtil.getWriter(true);
       excelWriter.addHeaderAlias("tname","教师名称");
       excelWriter.addHeaderAlias("mnemonicCode","助记码");
       excelWriter.addHeaderAlias("createTime","创建时间");
       excelWriter.setOnlyAlias(true);
       excelWriter.write(teachers);
       //创建一个字节数组输出流
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        //将Excel输出到输出流
        excelWriter.flush(os);
        excelWriter.close();
        byte[] bs = os.toByteArray();
        HttpHeaders headers = new HttpHeaders();
        //说明作为一个附属让用户下载，它还有一个属性值，是inline，说明直接展示在浏览器中
        //而attachment 的意思是作为附件下载
        headers.add("cosntent-dispoition","attachment;filename=teachers.xlsx");
        headers.add("content-type","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        return new ResponseEntity<byte[]>(bs,headers, HttpStatus.OK);
    }


}
