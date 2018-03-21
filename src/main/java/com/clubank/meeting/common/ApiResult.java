package com.clubank.meeting.common;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 返回结果
 */
@Getter
@Setter
@Builder
public class ApiResult {

    private int code;

    private String msg;

    private Object data;

    public ApiResult() {
        this.code = ApiStatusCode.SUCCESS.getValue();
        this.msg = ApiStatusCode.SUCCESS.toString();
    }

    public ApiResult(Object data) {
        this.code = ApiStatusCode.SUCCESS.getValue();
        this.msg = ApiStatusCode.SUCCESS.toString();
        this.data = data;
    }

    public ApiResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ApiResult(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
}
