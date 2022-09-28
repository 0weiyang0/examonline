package com.duyi.examonline.common.interceptors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor  implements HandlerInterceptor {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //这里是是被拦截之后所做的事情
        Object teacher    = request.getSession().getAttribute("loginTeacher");
      if (teacher == null){
          //说明还没有登录，那么这个时候，让他转到登录页面
          //但是，在转到登录页面之前，应该告诉用户去登录
          //所以，这个时候，还应该有个超时界面
          logger.info(request.getRequestURI()+" interceptoed");
          request.getRequestDispatcher("/common/timeout.html").forward(request,response);
          return false;
      }
        logger.info(request.getRequestURI()+" past");
        return true;
    }
}
