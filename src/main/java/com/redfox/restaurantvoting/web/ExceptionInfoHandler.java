package com.redfox.restaurantvoting.web;

import com.redfox.restaurantvoting.error.DataDisabledException;
import com.redfox.restaurantvoting.error.ErrorType;
import com.redfox.restaurantvoting.error.NotFoundException;
import com.redfox.restaurantvoting.util.validation.Validations;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@ControllerAdvice
@AllArgsConstructor
@Slf4j
public class ExceptionInfoHandler {
    private final MessageSourceAccessor messageSourceAccessor;

    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView wrongRequest(HttpServletRequest request, NoHandlerFoundException exception) {
        return logAndGetExceptionView(request, exception, false, ErrorType.WRONG_REQUEST, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ModelAndView notFoundException(HttpServletRequest request, NotFoundException exception) {
        log.error("NotFoundException at request " + request.getRequestURL(), exception);
        return logAndGetExceptionView(request, exception, false, ErrorType.DATA_NOT_FOUND, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(DataDisabledException.class)
    public ModelAndView dataDisabledException(HttpServletRequest request, DataDisabledException exception) {
        log.error("DataDisabledException at request " + request.getRequestURL(), exception);
        return logAndGetExceptionView(request, exception, false, ErrorType.DATA_DISABLED, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView accessDeniedException(HttpServletRequest request, AccessDeniedException exception) {
        log.error("AccessDeniedException at request " + request.getRequestURL(), exception);
        return logAndGetExceptionView(request, exception, true, ErrorType.FORBIDDEN, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest request, Exception exception) {
        log.error("Exception at request " + request.getRequestURL(), exception);
        return logAndGetExceptionView(request, exception, true, ErrorType.APP_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ModelAndView logAndGetExceptionView(HttpServletRequest request, Exception exception, boolean logException, ErrorType errorType, HttpStatus httpStatus) {
        Throwable rootCause = Validations.logAndGetRootCause(log, request, exception, logException, errorType);
        ModelAndView mav = new ModelAndView("exception",
                Map.of("exception", rootCause, "message", Validations.getMessage(rootCause),
                        "messageType", messageSourceAccessor.getMessage(errorType.getErrorCode()),
                        "status", httpStatus));
        mav.setStatus(httpStatus);

        //  Interceptor is not invoked, put user
        AuthUser authorizedUser = SecurityUtil.safeGet();
        if (authorizedUser != null) {
            mav.addObject("user", authorizedUser.getUser());
        }
        return mav;
    }
}
