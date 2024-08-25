package com.example.linkcargo.global.response.exception.handler;

import com.example.linkcargo.global.response.code.BaseErrorCode;
import com.example.linkcargo.global.response.exception.GeneralException;

public class FCMTokenHandler extends GeneralException {

    public FCMTokenHandler(BaseErrorCode code) {
        super(code);
    }
}
