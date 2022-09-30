package com.duyi.examonline.controller;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.duyi.examonline.common.vo.PageVO;
import com.duyi.examonline.domain.Student;
import com.duyi.examonline.service.StudentService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 臧娜
 * @version 1.0
 * @date 2022/9/26 17:50
 */
@Controller
@RequestMapping("/student")
public class StudentController {
    @Autowired
    private StudentService studentService;
    // 点击学生管理所请求的url。由于学生数据较多，所以不显示默认数据
    @RequestMapping("/student.html")
    public String studentList(){
        return "student/studentList";
    }
    @RequestMapping("/importsTemplate.html")
    public  String importTemplate(){
        return "student/importsTemplate";
    }
    //下载模板
    @RequestMapping("/downTemplate")
    public ResponseEntity<byte[]> downTemplate() throws IOException {
      InputStream is =  Thread.currentThread().getContextClassLoader().getResourceAsStream("files/student.xlsx");
      byte[] bs = new byte[is.available()];
      IOUtils.read(is,bs);
        HttpHeaders headers = new HttpHeaders();
        headers.add("content-disposition","attachment;filename=student.xlsx");
        headers.add("content-type","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        return new ResponseEntity<byte[]>(bs,headers, HttpStatus.OK);
    }
    //导入模板
    @RequestMapping(value = "/imports",produces = "text/html;charset=utf-8")
    @ResponseBody
    public  String imports(MultipartFile excel) throws IOException {
        //解析学生文件上传的文件
       ExcelReader reader = ExcelUtil.getReader(excel.getInputStream());
       //默认读取第一张表的数据，但我们除了第一章表的数据其他的都读
        reader.addHeaderAlias("学号","code");
        reader.addHeaderAlias("姓名","sname");
        //承装所有表格的数据
        List<Student> studentList = new ArrayList<Student>();
        //得到所有表的名字   集合类型
        List<String> sheetNames =  reader.getSheetNames();
        //遍历，得到没一张表的名字
        for (int i = 1; i < sheetNames.size(); i++) {
            String sheetName = sheetNames.get(i);
            reader.setSheet(sheetName);
            List<Student> students =  reader.readAll(Student.class);
            //info为  [年级,专业,班级]
            String[] info = sheetName.split("-");
            int grade = Integer.parseInt(info[0]);
            String major = info[1];
            String classNo = info[2];
            for (Student student : students) {
                student.setPass("123");
                student.setGrade(grade);
                student.setMajor(major);
                student.setClassNo(classNo);
             //   studentList.add(student);
            }
            //studentList.add(student);逻辑上可以用下面一行代码搞定
            //  意思是：   将一个子集合里面的数据放入大集合中
            studentList.addAll(students);
        }
        String msg = studentService.saves(studentList);
        return msg;
    }
    @RequestMapping("/classDefaultTemplate.html")
    public String clearClasses(){
        return "student/classesTemplate";
    }
    /**
     * Map
     * @param pageNo
     * @param condition  装载4个参数，不过pageNo单独拿出来
     *                   @RequestParam    表示装载【请求参数】
     * @return
     */
    @RequestMapping("/classesTemplate")
    public String toClaaesTemplate(int pageNo, @RequestParam Map condition, Model model){
        PageVO pageVO = studentService.find(pageNo,condition);
        model.addAttribute("pageVO",pageVO);
        return "student/classesTemplate";
    }
    @RequestMapping("/addStudent.html")
    public String toAddStudent(){
        return "student/formTemplate";
    }
    @RequestMapping("/addStudent")
    @ResponseBody
    public boolean addStudent(@RequestParam Map allParam){
      return studentService.addStudent(allParam);
    }
    //导出学生信息
    @RequestMapping("/student/exportClasses")
    public ResponseEntity<byte[]> exportClasses(@RequestParam Map condition){
        //此时查出来的数据是按照条件查出来所有的
        //但是我们希望是根据  【年级-专业-班级】来分班
     List<Student> studentList = studentService.findStudentsByClasses(condition);
     //获取excel
       ExcelWriter excelWriter =  ExcelUtil.getWriter(true);
     //设置excel的表头
        excelWriter.addHeaderAlias("code","学号");
        excelWriter.addHeaderAlias("sname","姓名");
        excelWriter.addHeaderAlias("mnemonic_code","助记码");
        excelWriter.addHeaderAlias("create_time","创建时间");
        //说明excel表格的内容只设置写了别名的
        excelWriter.setOnlyAlias(true);
        //装当前班级的学生
        List<Student> currStudents = new ArrayList<>();
        //存储当前班级的名称，第一作为sheet的名字，第二作为判断学生是否为此班级的条件
        String currClassName = "";
        for (Student student : studentList){
            //获取当前的班级名称
          String  className = student.getGrade()+"-"+student.getMajor()+"-"+student.getClassNo();
            if (currClassName.equals("")){
              currClassName = className;
              currStudents.add(student);
              continue;
            }
            if (className.equals(currClassName)){
                currStudents.add(student);
                continue;
            }
            if (! className.equals(currClassName)){
                excelWriter.setSheet(currClassName);
                excelWriter.write(currStudents);
                //当前学生所在的班级名字是另一个班级的名字，并且将集合中的内容清空
                currClassName =  className;
                currStudents.clear();
                //将当前学生装进去
                currStudents.add(student);
            }
        }
        excelWriter.setSheet(currClassName);
        excelWriter.write(currStudents);

        //  之后返回就行了，这里有个缓冲区的概念，flush用于关闭close流之前
        ByteArrayOutputStream bs  = new ByteArrayOutputStream();
        excelWriter.flush(bs);
        excelWriter.close();

        byte[] b = bs.toByteArray();
        HttpHeaders headers = new HttpHeaders();
        //说明作为一个附属让用户下载，它还有一个属性值，是inline，说明直接展示在浏览器中
        //而attachment 的意思是作为附件下载
        headers.add("cosntent-dispoition","attachment;filename=teachers.xlsx");
        headers.add("content-type","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        return new ResponseEntity<byte[]>(b,headers, HttpStatus.OK);
    }

}
