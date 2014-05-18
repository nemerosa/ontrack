package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.exceptions.InputException;
import net.nemerosa.ontrack.model.exceptions.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice(annotations = RestController.class)
public class UIErrorHandler {

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

    protected ResponseEntity<UIErrorMessage> getMessageResponse(HttpStatus status, String message) {
        // OK
        return new ResponseEntity<>(new UIErrorMessage(status.value(), message), status);
    }
}
