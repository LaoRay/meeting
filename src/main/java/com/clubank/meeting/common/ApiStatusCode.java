package com.clubank.meeting.common;

/**
 * 返回状态码
 */
public enum ApiStatusCode {

    SUCCESS(200),
    BAD_REQUEST(400),
    NO_AUTH(401),
    FOBIDDEN(403),
    NOT_FOUND(404),
    PARRAM_ERROR(406),
    DATA_NOT_EXIST(408),
    DATA_EXIST(410),
    DATA_INVALID(412),
    INNER_SERVER_ERROR(500);

    private int value;

    ApiStatusCode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
