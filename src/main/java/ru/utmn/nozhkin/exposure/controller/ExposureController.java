package ru.utmn.nozhkin.exposure.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExposureController {
    @GetMapping("/project-name")
    public String getProjectName() {
        return "Exposure";
    }
}
