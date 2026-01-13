package com.oBrway.shortLink.common.exception;

import com.oBrway.shortLink.common.enums.ResponseCode;

public class Base62Exception extends BaseException {
    private static final long serialVersionUID = -1234567890123456789L;

    public Base62Exception(ResponseCode code) {
        super(code.getMessage(), code);
    }
}