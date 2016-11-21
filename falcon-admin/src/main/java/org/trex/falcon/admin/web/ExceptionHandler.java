package org.trex.falcon.admin.web;

import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class ExceptionHandler implements HandlerExceptionResolver {

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ex.printStackTrace();
        if (this.isAjaxRequest(request)) {
            System.out.println("Ajax请求错误！！！！！！！！！！！！！！");
        } else {
            System.out.println("普通请求错误！！！！！！！！！！！！！！");
        }

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("exception", ex);
        return new ModelAndView("error/500", model);
    }

    /**
     * 判断是否是Ajax请求
     */
    private boolean isAjaxRequest(HttpServletRequest request) {
        String header = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equals(header) ? true : false;
    }

}
