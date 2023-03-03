package com.jirepos.oauth2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Teams Tab App 테스트를 위한 컨트롤러이다. 
 */
@Controller
@RequestMapping("/teams")
public class TeamsServiceController {
    /**
     * 구성 가능한 Teams Tab app의 구성 페이지를 반환한다.
     */
    @GetMapping("/teams-configuration")
    public String configPage(Model model) throws Exception {
        return "teams-configuration";
    }
    /**
     * Teams Tab app의 컨텐츠 페이지를 반환한다.
     */
    @GetMapping("/teams-content/{color}")
    public String contentPage(Model model, String id, @PathVariable("color") String color) throws Exception {
        model.addAttribute("color", color);
        return "teams-content";
    }
    
}
