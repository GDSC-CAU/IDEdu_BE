package com.gdg.backend.common.exception.handler;

import com.gdg.backend.common.exception.GeneralException;
import com.gdg.backend.common.response.BaseErrorCode;

public class GeneralHandler extends GeneralException {
    public GeneralHandler(BaseErrorCode code) {
        super(code);
    }
}
