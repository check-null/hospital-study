package com.sub.common.exception;

import com.sub.common.result.Result;
import com.sub.common.result.ResultCodeEnum;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result<Boolean> error(Exception e) {
        e.printStackTrace();
        return Result.fail();
    }

    @ExceptionHandler(YyghException.class)
    public Result<Boolean> error(YyghException e) {
        e.printStackTrace();
        return Result.fail();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Map<String, Object>> valid(MethodArgumentNotValidException valid) {
        BindingResult result = valid.getBindingResult();
        if (result.hasErrors()) {
            FieldError error = result.getFieldError();
            if (error == null) {
                return null;
            }
            String field = error.getField();
            String message = error.getDefaultMessage();
            Map<String, Object> map = new HashMap<>(16);
            map.put("field", field);
            map.put("msg", message);
            return Result.build(666, "?", map);
        }
        return Result.ok();
    }
}
