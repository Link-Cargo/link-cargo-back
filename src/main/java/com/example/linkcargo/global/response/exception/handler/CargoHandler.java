package com.example.linkcargo.global.response.exception.handler;

import com.example.linkcargo.global.response.code.BaseErrorCode;
import com.example.linkcargo.global.response.exception.GeneralException;

public class CargoHandler extends GeneralException {

    public CargoHandler(BaseErrorCode code) {
        super(code);
    }
}
