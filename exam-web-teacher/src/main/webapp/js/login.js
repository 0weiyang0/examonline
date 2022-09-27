var login = {}   //login是一个对象
//获取tname和pass，这是要传递给后端的数据

//这里是对象的方法，对方法的定义
login.toLogin = function (){
    //param是一个数组
    var param = {
        tname : $('#tname').val(),
        pass : $('#pass').val()
    }
//function(f)是一个回调函数，带着返回来的数据
    $.post('common/login1',param,function (f){
        if (f == true){
            location.href='common/main.html';
        }else{
            $('#msg').html('用户名或密码错误');
            $('#pass').val('');
        }
    });
}


