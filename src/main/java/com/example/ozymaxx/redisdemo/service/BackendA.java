package com.example.ozymaxx.redisdemo.service;

import com.example.ozymaxx.redisdemo.service.dto.ResponseA;
import org.springframework.stereotype.Service;

@Service
public class BackendA {

    public ResponseA method(final String value) {
        return new ResponseA(value);
    }
}
