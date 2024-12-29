package com.aventstack.chainlp;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping(value = {"", "/", "/projects/**", "/settings"})
    public String index() {
        return "index.html";
    }

}
