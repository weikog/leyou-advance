package com.leyou.common.exception.pojo;

import lombok.Getter;

@Getter
public class LyException extends RuntimeException {
    private Integer status;

    public LyException(Integer status, String message) {
        super(message);
        this.status = status;
    }

    public LyException(ExceptionEnum ee) {
        super(ee.getMessage());
        this.status = ee.getStatus();
    }
}
