package com.duyi.examonline.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.extra.pinyin.PinyinUtil;
import com.duyi.examonline.common.vo.PageVO;
import com.duyi.examonline.dao.TeacherMapper;
import com.duyi.examonline.domain.Teacher;
import com.duyi.examonline.service.TeacherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TeacherServiceImpl implements TeacherService {
    @Autowired
    private TeacherMapper teacherMapper;
   // getLogger(TeacherServiceImpl.class)   里面的参数表明的是哪个类出现的日志
    private Logger log =  LoggerFactory.getLogger(TeacherServiceImpl.class);
   //对教师信息进行保存
    public void save(Teacher teacher) {
       String mnemonicCode =  PinyinUtil.getPinyin(teacher.getTname(),"");
       teacher.setMnemonicCode(mnemonicCode);

       //验证，确保教师名称和助记码是不重复的    利用数据库的唯一性，也可以逻辑实现

        //将密码进行加密：使用md5
        String pass = DigestUtil.md5Hex(teacher.getPass());
        teacher.setPass(pass);

        //使用mapper进行保存
        try {
            teacherMapper.insert(teacher);
        }catch (Exception e){
            //用户名或助记码重复
            log.info("用户名或助记码重复");
        }
    }
    public Teacher getTeacherByName(String tname) {
        return teacherMapper.getTeacherByName(tname);
    }

    @Override
    public Teacher getTeacherByName(String tname, String mnemonic_code) {
        return teacherMapper.getTeacherByName(tname,mnemonic_code);
    }

    public void updateTeacher(String tname, String mnemonic_code,Long id) {
        teacherMapper.updateTeacher(tname,mnemonic_code,id);
    }

    public void updatePass(Long id, String pass) {
        teacherMapper.updatePass(id,pass);
    }
    /**
     * @param page  表式从前端中传递过来的页码   比如第一页，第二页
     * @param rows   表示想要一页中的数据数量，比如1页有5条数据
     * @param tname  表示前端传递过来的过滤条件
     * @return
     */
    @Override
    public PageVO getTeachers(int page, int rows, String tname) {
        //1. 判断传递过来的 page 是否复合条件，来复合代码的健壮性
        //确保上下限是为了确保开始在数据库中查找的数据合法
        //确保下限
        if (page < 1){
            page = 1;
        }
        //确保上限
        //从数据库中查找一共有多少条数据，然后除以每页的条数
        long total = teacherMapper.total(tname);
        //计算上限
        int max = (int)(total%rows==0?(total/rows):(total/rows+1));
        max = Math.max(max,1);
        //如果page超出了上线，则用最大值为page
        if (page > max){
            page = max;
        }
        //这里计算limit的start和count
        //第一页         0      4
        //第二页         5      9
        //  select * from t_teacher limit start count;    count 为行数
        int start = (page-1)*rows;
        int length = rows;
        // 2. 总的查询    过滤查询     分页查询
       List<Teacher> teachers =  teacherMapper.getTeachers(start,length,tname);
        Map<String,Object> condition = new HashMap<>();
        condition.put("tname",tname);
       PageVO pageVO = new PageVO(page,rows,total,max,start,start+length-1,teachers,condition);
        return pageVO;
    }
    @Override
    public void addTeacher(String tname,String mnemonic_code,String pass) {
        teacherMapper.addTeacher(tname,mnemonic_code,pass);
    }

    @Override
    public Teacher getTeacherById(Long id) {
        return teacherMapper.selectByPrimaryKey(id);
    }

    @Override
    public void deleteTeacher(Long id) {
        teacherMapper.deleteByPrimaryKey(id);
    }

    /**
     *
     * @param teachers
     * @return        共保存【x】条记录 |
     *                 成功【y】条记录 |
     *                 失败【z】条记录 |
     *                 【xxx1】已存在，保存失败 |
     */
    public String saves(List<Teacher> teachers) {
        String meg = "";//这个是要返回去的信息
        int successCount = 0;//成功条数
        int failCount = 0;//失败条数
        for (Teacher teacher : teachers) {
            try{
                String mnemonic_code = PinyinUtil.getPinyin(teacher.getTname(),"");
                String pass = DigestUtil.md5Hex("123");
                teacherMapper.addTeacher(teacher.getTname(),mnemonic_code,pass);
                successCount ++ ;
            }catch (Exception e){
                meg += teacher.getTname() + "已存在，保存失败 |";
                failCount ++ ;
            }
        }
        meg = "共保存【"+teachers.size()+"】条记录 | 成功【"+successCount+"】" +
                "条记录 |失败"+failCount+"条记录 |" + meg;
        return meg;
    }

    @Override
    public List<Teacher> findAll() {
        return teacherMapper.findAll();
    }
}
