package com.duyi.examonline.service.impl;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.extra.pinyin.PinyinUtil;
import com.duyi.examonline.common.Constant;
import com.duyi.examonline.common.vo.PageVO;
import com.duyi.examonline.dao.StudentMapper;
import com.duyi.examonline.domain.Student;
import com.duyi.examonline.service.StudentService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

/**
 * @author 臧娜
 * @version 1.0
 * @date 2022/9/27 10:17
 */
@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentMapper studentMapper;
    public void save(Student student) {
        //完善学生信息  [助记码 和 密码]
        student.setMnemonicCode(PinyinUtil.getPinyin(student.getSname(),""));
        student.setPass(DigestUtil.md5Hex("123"));
        studentMapper.insert(student);
    }
    public String saves(List<Student> students) {
        String msg = "";
        int successCount = 0;
        int failCount = 0;
        for (Student student : students) {
            try {
                this.save(student);
                successCount ++ ;
            }catch (Exception e){
                //出现重复
                msg += student.getSname()+"已存在，没有保存成功";
            }

        }
        msg = "共保存【"+students.size()+"】条记录 | 成功【"+successCount+"】" +
                "条记录 |失败"+failCount+"条记录 |" + msg;
         return msg;
    }
    public PageVO find(int pageNo, Map condition) {
        //分页插件
        PageHelper.startPage(pageNo, Constant.DEFAULT_ROWS);
        List<Map> classes = studentMapper.findClasses(condition);
        PageInfo pageInfo = new PageInfo(classes);
        return new PageVO(pageInfo.getPageNum(),
                pageInfo.getPageSize(),
                pageInfo.getTotal(),
                pageInfo.getNavigatePages(),
                (int)pageInfo.getStartRow(),
                (int)pageInfo.getEndRow(),
                pageInfo.getList(),
                condition);
    }
    public boolean addStudent(Map allParam) {
        int grade = Integer.parseInt((String)allParam.get("grade"));
        String major = (String)allParam.get("major");
        String classNo = (String)allParam.get("classNo");
        String sname = (String)allParam.get("sname");
        String code = (String)allParam.get("code");
        //根据名字生成助记码
        String mnemonicCode =  PinyinUtil.getPinyin(sname,"");
        //生成默认密码
        String pass = DigestUtil.md5Hex("123");
        Student student = new Student(null,code,sname,mnemonicCode,pass,grade,major,
                classNo,null,null,null,null,null,null);
        try{
            studentMapper.insert(student);
        }catch (DuplicateKeyException e){
            return false;
        }
        return true;
    }
}
