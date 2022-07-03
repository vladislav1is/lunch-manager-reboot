package com.redfox.restaurantvoting.util.validation;

import com.redfox.restaurantvoting.HasId;
import com.redfox.restaurantvoting.error.ErrorType;
import com.redfox.restaurantvoting.error.IllegalRequestDataException;
import com.redfox.restaurantvoting.error.NotFoundException;
import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.lang.NonNull;

import javax.servlet.http.HttpServletRequest;

@UtilityClass
public class Validations {

    public static void checkNew(HasId bean) {
        if (!bean.isNew()) {
            throw new IllegalRequestDataException(bean.getClass().getSimpleName() + " must be new (id=null)");
        }
    }

    // Conservative when you reply, but accept liberally (http://stackoverflow.com/a/32728226/548473)
    public static void assureIdConsistent(HasId bean, int id) {
        if (bean.isNew()) {
            bean.setId(id);
        } else if (bean.id() != id) {
            throw new IllegalRequestDataException(bean.getClass().getSimpleName() + " must has id=" + id);
        }
    }

    public static void checkModification(boolean isEmpty, int id) {
        if (isEmpty) {
            throw new NotFoundException("Entity with id=" + id + " not found");
        }
    }

    public static void checkModification(boolean isEmpty, String email) {
        if (isEmpty) {
            throw new NotFoundException("Entity with email=" + email + " not found");
        }
    }

    // https://stackoverflow.com/a/65442410/548473
    @NonNull
    public static Throwable getRootCause(@NonNull Throwable t) {
        Throwable rootCause = NestedExceptionUtils.getRootCause(t);
        return rootCause != null ? rootCause : t;
    }

    public static String getMessage(Throwable e) {
        return e.getLocalizedMessage() != null ? e.getLocalizedMessage() : e.getClass().getName();
    }

    public static Throwable logAndGetRootCause(Logger log, HttpServletRequest request, Exception exception, boolean logStackTrace, ErrorType errorType) {
        Throwable rootCause = Validations.getRootCause(exception);
        if (logStackTrace) {
            log.error(errorType + " at request " + request.getRequestURL(), rootCause);
        } else {
            log.warn("{} at request  {}: {}", errorType, request.getRequestURL(), rootCause.toString());
        }
        return rootCause;
    }
}