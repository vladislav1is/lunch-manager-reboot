package com.redfox.restaurantvoting.web;

import com.redfox.restaurantvoting.error.ErrorType;
import com.redfox.restaurantvoting.util.validation.Validations;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@ControllerAdvice
@AllArgsConstructor
@Slf4j
public class ExceptionInfoHandler {
    private final MessageSourceAccessor messageSourceAccessor;

    @ExceptionHandler(Exception.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest request, Exception exception) throws Exception {
        log.error("Exception at request " + request.getRequestURL(), exception);
        Throwable rootCause = Validations.getRootCause(exception);

        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ModelAndView mav = new ModelAndView("exception",
                Map.of("exception", rootCause, "message", Validations.getMessage(rootCause),
                        "messageType", messageSourceAccessor.getMessage(ErrorType.APP_ERROR.getErrorCode()),
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
