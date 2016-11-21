package org.trex.falcon.admin.web.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping(value = "/c")
public class CookieTestController {

    @RequestMapping(value = "test")
    public String test(HttpServletRequest request, HttpServletResponse response) {
        try {

            System.out.println("111111111111111111");

            Cookie cookie = new Cookie("zhanglei", "123456");
            cookie.setDomain(".4399.com");
            cookie.setMaxAge(0);
            response.addCookie(cookie);

            response.sendRedirect("/c/ttt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
