package com.jirepos.biz.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


/** 사용자 권한 테스트용 컨트롤러입니다. */
@Controller
@RequestMapping("/board")
public class BoardController {

    /** board.jsp 반환 */
    @GetMapping("/index")
    public String index() {
        return "board";
    }

}///~
