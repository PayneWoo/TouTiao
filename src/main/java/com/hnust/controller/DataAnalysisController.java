package com.hnust.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class DataAnalysisController {

//    @RequestMapping(path = {"/wordCloud"}, method = {RequestMethod.GET, RequestMethod.POST})
//    public String wordCloud() {
//
//        return "wordCloud.html";
//    }

    @RequestMapping("/html")
    public String dataAnalysis(){
        return "dataAnalysis.html";
    }
}
