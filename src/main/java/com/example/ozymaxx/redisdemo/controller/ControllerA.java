package com.example.ozymaxx.redisdemo.controller;

import com.example.ozymaxx.redisdemo.service.BackendA;
import com.example.ozymaxx.redisdemo.service.dto.ResponseA;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController("/backendA")
public class ControllerA {

    private final BackendA backendA;

    public ControllerA(final BackendA backendA) {
        this.backendA = backendA;
    }

    @GetMapping(value = "/{value}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Cacheable(key = "#value")
    public ResponseA backendA(@PathVariable final String value) {
        return backendA.method(value);
    }
}
