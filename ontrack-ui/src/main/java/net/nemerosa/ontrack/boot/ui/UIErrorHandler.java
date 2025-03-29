package net.nemerosa.ontrack.boot.ui;

import jakarta.servlet.http.HttpServletRequest;
import net.nemerosa.ontrack.model.exceptions.InputException;
import net.nemerosa.ontrack.model.exceptions.NotFoundException;
import net.nemerosa.ontrack.model.structure.NameDescription;
import net.nemerosa.ontrack.model.support.ApplicationLogEntry;
import net.nemerosa.ontrack.model.support.ApplicationLogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@ControllerAdvice(annotations = RestController.class)
public class UIErrorHandler {

    private final MessageSource messageSource;
    private final ApplicationLogService applicationLogService;

    @Autowired
    public UIErrorHandler(MessageSource messageSource, ApplicationLogService applicationLogService) {
        this.messageSource = messageSource;
        this.applicationLogService = applicationLogService;
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseBody
    public ResponseEntity<UIErrorMessage> onInputException(NotFoundException ex) {
        // Returns a message to display to the user
        String message = ex.getMessage();
        // OK
        return getMessageResponse(HttpStatus.NOT_FOUND, message);
    }

    @ExceptionHandler(InputException.class)
    @ResponseBody
    public ResponseEntity<UIErrorMessage> onInputException(InputException ex) {
        // Returns a message to display to the user
        String message = ex.getMessage();
        // OK
        return getMessageResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<UIErrorMessage> onValidationException(MethodArgumentNotValidException ex) {
        // Field errors to messages
        List<String> messages = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> {
                    String defaultMessage = fieldError.getDefaultMessage();
                    if (StringUtils.isNotBlank(defaultMessage)) {
                        return defaultMessage;
                    } else {
                        return messageSource.getMessage(fieldError, Locale.ENGLISH);
                    }
                })
                .collect(Collectors.toList());
        // Returned message
        String message;
        if (messages.size() > 1) {
            message = messages.stream().map(s -> "* " + s + "\n").collect(Collectors.joining(""));
        } else {
            message = messages.get(0);
        }
        // OK
        return getMessageResponse(
                HttpStatus.BAD_REQUEST,
                message
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public ResponseEntity<UIErrorMessage> onAccessDeniedException() {
        // Logs the error?
        // Returns a 403 error
        return getMessageResponse(HttpStatus.FORBIDDEN, "Not authorized.");
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<UIErrorMessage> onAnyException(HttpServletRequest request, Exception ex) {
        // Not for access denied
        if (ex instanceof AccessDeniedException) {
            throw (AccessDeniedException) ex;
        }
        // Returns a message to display to the user
        String message = "An error has occurred.";
        // Logs the error in the application
        applicationLogService.log(
                ApplicationLogEntry.error(
                        ex,
                        NameDescription.nd(
                                "ui-error",
                                "Error display to the user"
                        ),
                        request.getServletPath()
                )
        );
        // OK
        return getMessageResponse(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    protected ResponseEntity<UIErrorMessage> getMessageResponse(HttpStatus status, String message) {
        // OK
        return new ResponseEntity<>(new UIErrorMessage(status.value(), message), status);
    }
}
