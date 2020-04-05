package com.leyou.common.exception.controller;

import com.leyou.common.exception.pojo.ExceptionResult;
import com.leyou.common.exception.pojo.LyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class LyExceptionController {

    /*表示当前处理器，只处理LyException异常*/
    @ExceptionHandler(LyException.class)
    public ResponseEntity<ExceptionResult> handlerLyException(LyException e){
        return ResponseEntity.status(e.getStatus()).body(new ExceptionResult(e));
    }

}
