var teacher = {}
//分页查询
teacher.toPageTeacherQuery = function (page){
    //如果没有指定页码，默认查询第一页   过滤查询，清空查询不指定页码
    page = page?page:1;

    var param = {
        page:page,
        tname:$('#keyWord').val(),
    }

    $.post('teacher/pageTemplate.html',param,function (view){
        $('#pageTemplate').replaceWith(view)
    });

}
//搜索
teacher.toQueryTeacher = function (page){
    teacher.toPageTeacherQuery(page);
}
//取消搜索
teacher.toClearTeacher = function (page){
    $('#clearQuery').val('');
    teacher.toClearTeacher(page);
}
//添加教师
teacher.toAdd = function (){
    $.post('teacher/formTemplate.html',{},function (view){
        $('#teacher-modal-title').html("新建教师");
        $('#teacher-modal-body').html(view);
        $('#teacher-modal-submit').click(function (){
            var param = {
                tname:$('#update-form-tname').val()
            }
            $.post("teacher/addTeacher",param,function (f){
                if (f == true){
                    $('#teacher-modal').modal('hide');
                    alert("保存成功");
                    teacher.toQueryTeacher();
                }else{
                    $('#teacher-modal').modal('hide');
                    alert("该用户信息已存在");
                    teacher.toQueryTeacher();
                }
            });
        });
        $('#teacher-modal').modal('show');
    });
}
//编辑教师
teacher.toEdit = function (id){
    alert(id)
    var param = {
        id : id,
    }
        $.post('teacher/formTemplate.html',param,function (view){
        $('#teacher-modal-title').html("修改教师信息");
        $('#teacher-modal-body').html(view);
        $('#teacher-modal-submit').click(function (){
           param.tname =  $('#update-form-tname').val();
            //这个时候进行修改之后的提交。
            //异步请求，  teacher/updateTeacher,携带着教师名称的数据
              $.post('teacher/updateTeacher',param,function (f){
                  if (f == true){
                      //修改成功
                      alert("修改成功");
                      teacher.toQueryTeacher();
                  }else{
                      alert("您修改后的用户已存在，请重新修改");
                      teacher.toQueryTeacher();
                  }
              });
        });
        $('#teacher-modal').modal('show');
    });
}
//复选框全选
teacher.toCheckAll = function (){
    //通过选择器找都thead中的复选框。
    //checked变量存储的是复选框是否被选中的状态。如果被选中，则返回true，否则返回false
    // prop方法返回被选中的值
   var checked = $('#teacherGrid thead :checkbox').prop("checked");
   //tbody中的状态和thead选中的状态一致
    $('#teacherGrid  tbody :checkbox').prop("checked",checked);
}
//批量删除老师
//批量删除的意思就是  在多选框中教师的id，然后将这些id传入到后端，后端挨个删除
//在删除之前，询问是否要真的删除
teacher.batchDeletion = function (){
    if ($('#teacherGrid tbody :checked').length == 0){
        alert("请选择要删除的数据");
        return;
    }
    if (! confirm("是否要删除选中的数据")){
        return;
    }
        var ids = "";
        $('#teacherGrid tbody :checked').each(function (i,element){
          var id = element.value;
           ids += id + ",";
        });
        alert(ids)
    $.post('teacher/deleteAllTeachers',{ids:ids},function (){
            alert("删除成功");
            teacher.toQueryTeacher();
        });

}
//  批量导入教师信息   点击导入信息，出现一个模态框（共有的）
teacher.importTeachers = function (){
    var uploading = false;
    $.post('teacher/importsTemplate.html',null,function (view){
        $('#teacher-modal-title').html("导入教师信息");
        $('#teacher-modal-body').html(view);
        $('#teacher-modal-submit').click(function (){
            //获得文件路径
            var fileInfo = $('#import-excel').val();
            //没有上传文件
            if(!fileInfo){
                alert('请选择要上传的excel文件');
                return ;
            }
            //uploading是一个进度条，在还没有上传成功就一直alert
            if(uploading){
                alert("文件正在上传中，请稍候");
                return false;
            }
            $.ajax({
                url: 'teacher/imports',
                type: 'POST',
                /**
                 * 当第一次发起请求后，会把后端返回结果以缓存的形式进行存储到浏览器中；
                 * 当再次发起请求时，当cache值为true则不再发起请求，直接从缓存中读取.
                    cache 的作用一般只在 get 请求中使用。默认值为true
                 */
                cache: false,
                //   FormData 对象用于捕获 【HTML表单】
                data: new FormData($('#teacher-import-form')[0]),
                /**
                 * 代表以对象（obj）的形式上传的数据都会被转换为【字符串】的形式上传。
                 * 因为   上传文件需要二进制   所以 processData：false
                 * 默认值为true
                 */
                processData: false,
                /**
                 *  dataType:代表期望从后端收到的数据的格式，一般会有 json 、String等
                 而 contentType 则是与 dataType 相对应的：代表前端发送数据的格式.
                 默认值：application/x-www-form-urlencoded
                 代表的是 ajax 的 data 是以字符串的形式 如 id=001&password=root
                 使用这种传数据的格式，无法传输复杂的数据
                 */
                contentType: false,
                beforeSend: function () {
                    uploading = true;
                },
                success: function (meg) {
                    uploading = false;
                    meg = meg.replace(/\|/g,"\r\n");
                    alert(meg) ;
                    $('#teacher-modal').modal('hide');
                    teacher.toQueryTeacher();
                }
            });

        });
        $('#teacher-modal').modal('show');
    });}
//点击选择文件，找到文件， 然后查看格式是否正确
teacher.chanageFile = function (){
    //读到的是文件的绝对路径
   var file =   $('#import-excel').val();
   if (! (file.endsWith(".xls") || file.endsWith(".xlsx"))){
       alert("请选择正确的文件格式");
       return;
    }
    $('#fileMsg').html(file);
}