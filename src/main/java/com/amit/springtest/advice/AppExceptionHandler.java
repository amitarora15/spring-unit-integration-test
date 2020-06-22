package com.amit.springtest.advice;

import com.amit.springtest.bean.ErrorVo;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class AppExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public List<ErrorVo> handleIllegalArgumentException(IllegalArgumentException exception) {
        List<ErrorVo> errors = new ArrayList<>();
        ErrorVo vo = new ErrorVo(exception.getClass().getCanonicalName());
        vo.setMessage(exception.getMessage());
        errors.add(vo);
        return errors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public List<ErrorVo> handleValidationException(MethodArgumentNotValidException exception) {
        List<ErrorVo> errors = new ArrayList<>();
        return exception.getBindingResult().getFieldErrors().stream().
                map(e -> new ErrorVo(e.getField(), e.getDefaultMessage(), MethodArgumentNotValidException.class.getCanonicalName())).
                collect(Collectors.toList());
    }

}
