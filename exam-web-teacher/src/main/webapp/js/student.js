var student = {}
//  批量导入学生信息   点击导入信息，出现一个模态框（共有的）
student.toImports = function (){
    var uploading = false;
    $.post('student/importsTemplate.html',null,function (view){
        $('#common-modal-title').html("导入学生信息");
        $('#common-modal-body').html(view);
        //点击提交的事件
        $('#common-modal-submit').click(function (){
            //获得文件路径
            var fileInfo = $('#import-excel').val();
            //没有上传文件
            if(!fileInfo){
                alert('请选择要上传的excel文件');
                return ;
            }
            if(uploading){
                alert("文件正在上传中，请稍候");
                return false;
            }
            $.ajax({
                url: 'student/imports',
                type: 'POST',
                cache: false,
                data: new FormData($('#student-import-form')[0]),
                processData: false,
                contentType: false,
                beforeSend: function () {
                    uploading = true;
                },
                success: function (meg) {
                    uploading = false;
                    meg = meg.replace(/\|/g,"\r\n");
                    alert(meg) ;
                    $('#common-modal').modal('hide');
                    //刷新班级表格（清空之气的数据，查询展示新的数据）
                }
            });
        });
        $('#common-modal').modal('show');
    });}
//点击上传文件
student.chanageFile = function (){
    var fileInfo = $('#import-excel').val();
    if(fileInfo.endsWith("xls") || fileInfo.endsWith("xlsx")){
        alert(fileInfo);return;
    }else{
        alert("请选择正确的文件格式")
    }
}
//清空班级信息
student.toClearClass = function (){
    $('#search-grade').val('');
    $('#search-major').val('');
    $('#search-classNo').val('');
    //之后，返回到什么也没有的状态
    $.post('student/classDefaultTemplate.html',{},function (view){
        $('#classesTemplate').replaceWith(view)
    });
}
//班级查询
/**
 * 点击查询按钮，默认从第一页查，没有pageNo参数
 * 点击分页按钮 ，从指定页查，有pageNo
 * 分页查询本身也会携带过滤条件
 * @param pageNo
 */
student.toClassQuery = function (pageNo){
    pageNo = pageNo?pageNo:1;
    var param = {
        pageNo : pageNo,
        grade : $("#search-grade").val(),
        major : $("#search-major").val(),
        classNo : $("#search-classNo").val()
    }
    $.post('student/classesTemplate',param,function (view){
        $('#classesTemplate').replaceWith(view);
    });
}
//点击分页的页数，然后在数据库进行查询
student.toPageClassQuery = function (pageNo){
    //进行的是班级信息的查询
    student.toClassQuery(pageNo);
}
//新建学生
student.toAdd = function (){
    //点击新建，找到formTemplate，放到模态框中，展示出来
    $.post('student/addStudent.html',{},function (view){
            $('#common-modal-title').html("新建学生");
            $('#common-modal-body').html(view);
        $('#common-modal-submit').click(function (){
            var param = {
                code : $('#form-code').val(),
                grade : $('#form-grade').val(),
                sname : $('#form-sname').val(),
                major : $('#form-major').val(),
                classNo : $('#form-classNo').val(),
            }
            $.post('student/addStudent',param,function (f){
                if (f == true) {
                    alert("添加成功");
                }else{
                    alert("学生学号或者学生姓名重复，请检查后在添加!");
                }
                $('#common-modal').modal('hide');
            });
        });
        $('#common-modal').modal('show');
});
}
//导出学生信息
student.toExportClasses = function(){
    var grade = $('#search-grade').val();
    var major = $('#search-major').val();
    var classNo = $('#search-classNo').val();
    var classNames = '';
    //找到被选中的复选框，挨个循环找className
    //i表示下标，从0开始，element表示className的属性值。
    $('#classGrid tbody:checkbox').each(function (i,element){
       classNames = element.val() + ',';
    });

    if(classNames == null){
        if (!grade & !major & !classNo){
            alert("请选择要导出的班级信息");
            return;
        }else{
            if (!confirm("将按照条件进行导出")){
                return;
            }
        }}
    //至此，传递给后端的要么是className,要么是grade等条件    下载不能使用Ajax
    location.href='student/exportClasses?classNames='+classNames+'&grade='+grade+'&major='+major+'&classNo='+classNo;



}