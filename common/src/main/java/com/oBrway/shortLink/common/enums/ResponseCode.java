package com.oBrway.shortLink.common.enums;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.Getter;

@Getter
public enum ResponseCode {
    SUCCESS(HttpResponseStatus.OK, 0, "成功"),
    Base62_ENCODE_ERROR(HttpResponseStatus.INTERNAL_SERVER_ERROR, 20001, "Base62编码错误"),
    SHORT_LINK_GENERATE_ERROR(HttpResponseStatus.INTERNAL_SERVER_ERROR, 20002, "短链接生成错误"),
    SHORT_LINK_QUERY_ERROR(HttpResponseStatus.INTERNAL_SERVER_ERROR, 20003, "短链接查询错误"),
    Base62_DECODE_ERROR(HttpResponseStatus.INTERNAL_SERVER_ERROR, 20004, "Base62解码错误");


    private HttpResponseStatus status;
    private int code;
    private String message;

    ResponseCode(HttpResponseStatus status, int code, String msg) {
        this.status = status;
        this.code = code;
        this.message = msg;
    }

    public String getMessage() {
        return message;
    }
}