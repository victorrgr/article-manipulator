package com.web.scraper.standard;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class StandardErrorHandler {

    private final Log log;

    public StandardErrorHandler() {
        this.log = LogFactory.getLog(getClass());
    }

    private void logHandledException(Exception e, HttpServletRequest request) {
        this.log.error(
                "Handled '" + e.toString() + "' " +
                "AT: '" + request.getRequestURI() + "' " +
                "FROM: '" + request.getRemoteAddr() + "/" + request.getRemoteHost() + "'");
        this.log.error(e.getMessage(), e);
    }

    @ExceptionHandler({ Exception.class })
    public void handleException(HttpServletRequest request, Exception e) {
        this.logHandledException(e, request);
    }
}
