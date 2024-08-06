package com.example.linkcargo.global.response.exception.handler;

import com.example.linkcargo.global.response.code.BaseErrorCode;
import com.example.linkcargo.global.response.exception.GeneralException;

public class PortHandler extends GeneralException {

    public PortHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
