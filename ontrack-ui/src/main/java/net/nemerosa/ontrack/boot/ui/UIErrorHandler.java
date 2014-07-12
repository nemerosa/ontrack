package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.exceptions.InputException;
import net.nemerosa.ontrack.model.exceptions.NotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @Autowired
    public UIErrorHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
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

    protected ResponseEntity<UIErrorMessage> getMessageResponse(HttpStatus status, String message) {
        // OK
        return new ResponseEntity<>(new UIErrorMessage(status.value(), message), status);
    }
}
