package com.redfox.restaurantvoting.web;

import com.redfox.restaurantvoting.error.AppException;
import com.redfox.restaurantvoting.error.DataConflictException;
import com.redfox.restaurantvoting.error.DataDisabledException;
import com.redfox.restaurantvoting.error.ErrorType;
import com.redfox.restaurantvoting.error.restaurant.DishRefConstraintViolationException;
import com.redfox.restaurantvoting.error.restaurant.RestaurantConstraintViolationException;
import com.redfox.restaurantvoting.error.vote.AlreadyVotedException;
import com.redfox.restaurantvoting.error.vote.DeadlineException;
import com.redfox.restaurantvoting.error.vote.NotVotedException;
import com.redfox.restaurantvoting.util.validation.Validations;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import java.util.Map;
import java.util.stream.Stream;

import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.MESSAGE;

@RestControllerAdvice(annotations = RestController.class)
@Order(Ordered.HIGHEST_PRECEDENCE + 5)
@AllArgsConstructor
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    public static final String EXCEPTION_DUPLICATE_EMAIL = "exception.user.duplicateEmail";
    public static final String EXCEPTION_DUPLICATE_NAME_AND_ADDRESS = "exception.restaurant.duplicateNameAndAddress";
    public static final String EXCEPTION_DUPLICATE_NAME_AND_DATE = "exception.menu-item.duplicateNameAndDate";
    public static final String EXCEPTION_DUPLICATE_DISH_REF_AND_DATE = "exception.menu-item.duplicateDishRefAndDate";

    private final MessageSourceAccessor messageSourceAccessor;

    private final ErrorAttributes errorAttributes;

    @ExceptionHandler(AppException.class)
    public ResponseEntity<?> appException(WebRequest request, AppException exception) {
        log.error("ApplicationException: {}", exception.getMessage());
        return createResponseEntity(request, exception.getOptions(), exception.getStatus(), ErrorType.DATA_ERROR);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> entityNotFoundException(WebRequest request, EntityNotFoundException exception) {
        log.error("EntityNotFoundException: {}", exception.getMessage());
        return createResponseEntity(request, ErrorAttributeOptions.of(MESSAGE), HttpStatus.UNPROCESSABLE_ENTITY, ErrorType.DATA_NOT_FOUND);
    }

    @ExceptionHandler(DataConflictException.class)
    public ResponseEntity<?> dataConflictException(WebRequest request, DataConflictException exception) {
        log.error("DataConflictException: {}", exception.getMessage());
        return createResponseEntity(request, ErrorAttributeOptions.of(MESSAGE), HttpStatus.CONFLICT, ErrorType.DATA_ERROR);
    }

    @ExceptionHandler(DataDisabledException.class)
    public ResponseEntity<?> dataDisabledException(WebRequest request, DataDisabledException exception) {
        log.error("DataDisabledException: {}", exception.getMessage());
        return createResponseEntity(request, ErrorAttributeOptions.of(MESSAGE), HttpStatus.CONFLICT, ErrorType.DATA_DISABLED);
    }

    @ExceptionHandler(DeadlineException.class)
    public ResponseEntity<?> deadlineException(WebRequest request, DeadlineException exception) {
        log.error("DeadlineException: {}", exception.getMessage());
        return createResponseEntity(request, ErrorAttributeOptions.of(MESSAGE), HttpStatus.CONFLICT, ErrorType.DEADLINE);
    }

    @ExceptionHandler(NotVotedException.class)
    public ResponseEntity<?> notVotedException(WebRequest request, NotVotedException exception) {
        log.error("NotVotedException: {}", exception.getMessage());
        return createResponseEntity(request, ErrorAttributeOptions.of(MESSAGE), HttpStatus.CONFLICT, ErrorType.NOT_VOTED);
    }

    @ExceptionHandler(AlreadyVotedException.class)
    public ResponseEntity<?> alreadyVotedException(WebRequest request, AlreadyVotedException exception) {
        log.error("AlreadyVotedException: {}", exception.getMessage());
        return createResponseEntity(request, ErrorAttributeOptions.of(MESSAGE), HttpStatus.CONFLICT, ErrorType.ALREADY_VOTED);
    }

    @ExceptionHandler(RestaurantConstraintViolationException.class)
    public ResponseEntity<?> restaurantConstraintViolationException(WebRequest request, RestaurantConstraintViolationException exception) {
        log.error("RestaurantConstraintViolationException: {}", exception.getMessage());
        return createResponseEntity(request, ErrorAttributeOptions.of(MESSAGE), HttpStatus.CONFLICT, ErrorType.RESTAURANT_CONSTRAINT_VIOLATION);
    }

    @ExceptionHandler(DishRefConstraintViolationException.class)
    public ResponseEntity<?> dishRefConstraintViolationException(WebRequest request, DishRefConstraintViolationException exception) {
        log.error("DishRefConstraintViolationException: {}", exception.getMessage());
        return createResponseEntity(request, ErrorAttributeOptions.of(MESSAGE), HttpStatus.CONFLICT, ErrorType.DISH_REF_CONSTRAINT_VIOLATION);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> conflict(WebRequest request, DataIntegrityViolationException exception) {
        log.error("DataIntegrityViolationException: {}", exception.getMessage());
        return createResponseEntity(request, ErrorAttributeOptions.of(MESSAGE), HttpStatus.CONFLICT, ErrorType.DATA_ERROR);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> accessDeniedException(WebRequest request, AccessDeniedException exception) {
        log.error("AccessDeniedException: {}", exception.getMessage());
        return createResponseEntity(request, ErrorAttributeOptions.of(MESSAGE), HttpStatus.FORBIDDEN, ErrorType.FORBIDDEN);
    }

    @NonNull
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(@NonNull Exception exception, Object body, @NonNull HttpHeaders headers, @NonNull HttpStatus status, @NonNull WebRequest request) {
        log.error("Exception", exception);
        super.handleExceptionInternal(exception, body, headers, status, request);
        return createResponseEntity(request, ErrorAttributeOptions.of(), status, ErrorType.APP_ERROR, Validations.getRootCause(exception).getMessage());
    }

    @NonNull
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, @NonNull HttpHeaders headers, @NonNull HttpStatus status, @NonNull WebRequest request) {
        return handleBindingErrors(exception.getBindingResult(), request);
    }

    @NonNull
    @Override
    protected ResponseEntity<Object> handleBindException(BindException exception, @NonNull HttpHeaders headers, @NonNull HttpStatus status, @NonNull WebRequest request) {
        return handleBindingErrors(exception.getBindingResult(), request);
    }

    private ResponseEntity<Object> handleBindingErrors(BindingResult result, WebRequest request) {
        Stream<String> fieldDetails = result.getFieldErrors().stream()
                .map(fieldError -> String.format("[%s] %s", fieldError.getField(), messageSourceAccessor.getMessage(fieldError)));
        Stream<String> objectDetails = result.getGlobalErrors().stream()
                .map(objectError -> String.format("[%s] %s", objectError.getObjectName(), messageSourceAccessor.getMessage(objectError)));
        String[] details = Stream.concat(fieldDetails, objectDetails).toArray(String[]::new);
        return createResponseEntity(request, ErrorAttributeOptions.defaults(), HttpStatus.UNPROCESSABLE_ENTITY, ErrorType.VALIDATION_ERROR, details);
    }

    @SuppressWarnings("unchecked")
    private <T> ResponseEntity<T> createResponseEntity(WebRequest request, ErrorAttributeOptions options, HttpStatus status, ErrorType errorType, String... details) {
        Map<String, Object> body = errorAttributes.getErrorAttributes(request, options);
        if (details.length != 0) {
            body.put("details", details);
        }
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", messageSourceAccessor.getMessage(errorType.getErrorCode()));
        return (ResponseEntity<T>) ResponseEntity.status(status).body(body);
    }
}
