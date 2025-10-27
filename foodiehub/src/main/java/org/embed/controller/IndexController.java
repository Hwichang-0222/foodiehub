package org.embed.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/") // 루트 리소스
public class IndexController {
    
    @GetMapping
    public String getIndex() {
        return "index";
    }
}
