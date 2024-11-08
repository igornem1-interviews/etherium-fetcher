package limechain.etherium_fetcher.api.errorhandler;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import limechain.etherium_fetcher.exception.AccountNotFoundException;
import limechain.etherium_fetcher.exception.ErrorMessage;

@ResponseBody
@ControllerAdvice
class ErrorHandlerAdvice {

    /*
     * @ResponseBody
     * 
     * @ExceptionHandler(EmployeeNotFoundException.class)
     * 
     * @ResponseStatus(HttpStatus.NOT_FOUND) String
     * employeeNotFoundHandler(EmployeeNotFoundException ex) { return
     * ex.getMessage(); }
     */

    @ExceptionHandler(value = { AccountNotFoundException.class })
    @ResponseStatus(value = HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
    public ErrorMessage resourceNotFoundException(Exception ex, WebRequest request) {
        return new ErrorMessage(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE.value(), new Date(), ex.getMessage(), request.getDescription(false));
    }

    /*
     * @ExceptionHandler(Exception.class)
     * 
     * @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR) public ErrorMessage
     * globalExceptionHandler(Exception ex, WebRequest request) { return new
     * ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), new Date(),
     * ex.getMessage(), request.getDescription(false)); }
     */

}