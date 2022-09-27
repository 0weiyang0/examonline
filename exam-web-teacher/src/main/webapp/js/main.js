var main={}
main.exit = function (){
    if (!confirm("是否确认退出?")){
        return
    }
    location.href='common/exit';
};
main.toUpdate=function (){
    //获取模板，利用ajax来发送请求
    $.post('common/updatePwdTemplate.html',{},function (view){
      //将得到的页面内容放到modal.body中
        $('#modal-body').html(view);
    });
    //获取模态窗口
    $('#myModal').modal('show');
};
main.updatePwd =  function (){
    var old_pass = $('#old-pass').val();
    var new_pass = $('#new-pass').val();
    var re_pass = $('#re-pass').val();
    alert(old_pass+"<--->"+new_pass+"<--->"+re_pass)
    //判断  新密码和确认密码是否一致。如果不一致，则返回
    if (new_pass != re_pass){
        alert("两次密码不一致");
        return;
    }

    // 一致的话，则通过ajax的方式访问修改密码
    var param = {
        old_pass:old_pass,
        new_pass:new_pass
    }
    $.post('common/updatePass',param,function (f){
        if (f == false){
            alert("原密码不正确");
            return;
        }else{
            alert("修改密码成功");
            $('#myModal').modal('hide');
        }

    });



}

main.showDialog = function(config){
    $('#common-modal-title').html(config.title);
    $('#common-modal-body').html(config.content);
    $('#common-modal-submit').off('click').click(function(){
        config.submit();
    });
    $('#common-modal').modal('show') ;
}