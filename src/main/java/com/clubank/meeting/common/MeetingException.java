package com.clubank.meeting.common;

public class MeetingException extends RuntimeException {
    public MeetingException() {
        super();
    }

    public MeetingException(String message) {
        super(message);
    }

    public MeetingException(String message, Throwable cause) {
        super(message, cause);
    }

    public MeetingException(Throwable cause) {
        super(cause);
    }

    protected MeetingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
