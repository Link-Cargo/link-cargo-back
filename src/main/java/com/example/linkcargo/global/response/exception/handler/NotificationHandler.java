package com.example.linkcargo.global.response.exception.handler;

import com.example.linkcargo.global.response.code.BaseErrorCode;
import com.example.linkcargo.global.response.exception.GeneralException;

public class NotificationHandler extends GeneralException {

    public NotificationHandler(BaseErrorCode code) {
        super(code);
    }
}
