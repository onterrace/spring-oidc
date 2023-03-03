package com.jirepos.biz.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


/** Admin 권한 접근 테스트용 컨트롤러입니다. */
@Controller
public class AdminController {

    /** admin.jsp 반환 */
    @RequestMapping("/admin/index")
    public String index() {
        return "/admin";
    }
}///~
