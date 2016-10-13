package com.tomtom.sosnicki.okienczak.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.NoSuchElementException;

@ControllerAdvice
public class GeneralRestControllerAdvice {

    private final Logger log = LoggerFactory.getLogger(GeneralRestControllerAdvice.class);

    @ResponseBody
    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String handleNoSuchElementException(NoSuchElementException ex) {
        log.warn("NoSuchElementException handled.", ex);
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("IllegalArgumentException handled.", ex);
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(IndexOutOfBoundsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String handleIndexOutOfBounds(IndexOutOfBoundsException ex) {
        log.warn("IndexOutOfBounds handled.", ex);
        return ex.getMessage();
    }

}
