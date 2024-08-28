/*******************************************************************************
 * COPYRIGHT Ericsson 2024
 *
 *
 *
 * The copyright to the computer program(s) herein is the property of
 *
 * Ericsson Inc. The programs may be used and/or copied only with written
 *
 * permission from Ericsson Inc. or in accordance with the terms and
 *
 * conditions stipulated in the agreement/contract under which the
 *
 * program(s) have been supplied.
 ******************************************************************************/

package com.ericsson.oss.air.cardq.error.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.ericsson.oss.air.cardq.api.model.ProblemDetails;
import com.ericsson.oss.air.cardq.error.InternalRuntimeException;
import com.ericsson.oss.air.cardq.error.MandatoryParameterException;
import com.ericsson.oss.air.security.utils.logging.AuditLogFactory;
import com.ericsson.oss.air.security.utils.logging.AuditLogger;

@ControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationExceptionHandler.class);
    private static final AuditLogger AUDIT_LOG = AuditLogFactory.getLogger(ApplicationExceptionHandler.class);
    private static final String ERROR_OCCURRED_WITH_MESSAGE = "Error occurred {} with error message: {}";
    private static final String ERROR_OCCURRED_STACK_TRACE = "Stack trace:";

    @Value("${cts.retry.maxAttempts}")
    private String retryAttempts;

    @Value("${spring.neo4j.retry.maxAttempts}")
    private String neo4jRetryAttempts;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetails> handleAll(Throwable throwable) {
        logMessage(throwable);
        ProblemDetails problemDetails = new ProblemDetails()
                .title("Error occurred " + throwable.getClass().getSimpleName())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .detail(throwable.getMessage());
        return new ResponseEntity<>(problemDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(MandatoryParameterException.class)
    public final ResponseEntity<ProblemDetails> handleMandatoryParameterException(MandatoryParameterException ex) {
        logMessage(ex);
        ProblemDetails problemDetails = new ProblemDetails()
                .title("Error occurred " + ex.getClass().getSimpleName())
                .status(HttpStatus.BAD_REQUEST.value())
                .detail(ex.getMessage());
        return new ResponseEntity<>(problemDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceAccessException.class)
    public final ResponseEntity<ProblemDetails> handleTimeoutExceptions(ResourceAccessException ex) {
        logMessage(ex);
        AUDIT_LOG.error(ERROR_OCCURRED_WITH_MESSAGE, ex.getClass().getSimpleName(), ex.getMessage());
        String message = String.format("Connection Timed Out, Retried: %s, Reason: %s", retryAttempts, ex.getMessage());
        ProblemDetails problemDetails = new ProblemDetails()
                .title("Connect Timed Out, " + ex.getClass().getSimpleName())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .detail(message);
        return new ResponseEntity<>(problemDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public final ResponseEntity<ProblemDetails> handleIllegalArgumentException(IllegalArgumentException ex) {
        logMessage(ex);
        String message = String.format("Malformed Request, Reason: %s", ex.getMessage());
        ProblemDetails problemDetails = new ProblemDetails()
                .title("Malformed Request, " + ex.getClass().getSimpleName())
                .status(HttpStatus.BAD_REQUEST.value())
                .detail(message);
        return new ResponseEntity<>(problemDetails, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle service unavailable exception response entity. When neo4j is unavailable, returns 503
     *
     * @param ex the exception
     * @return the response entity
     */
    @ExceptionHandler(TransientDataAccessResourceException.class)
    public final ResponseEntity<ProblemDetails> handleServiceUnavailableException(final TransientDataAccessResourceException ex) {
        this.logMessage(ex);

        final String message = String.format("Service is unavailable, Retried: %s times, Reason: %s", this.neo4jRetryAttempts, ex.getMessage());

        final ProblemDetails problemDetails = new ProblemDetails(HttpStatus.SERVICE_UNAVAILABLE.value(), message)
                .title(HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase());
        return new ResponseEntity<>(problemDetails, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(InternalRuntimeException.class)
    public ResponseEntity<ProblemDetails> handleInternalRuntimeException(InternalRuntimeException ex) {
        logMessage(ex);
        ProblemDetails problemDetails = new ProblemDetails()
                .title("Internal Server Error, " + ex.getClass().getSimpleName())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .detail(ex.getMessage());
        return new ResponseEntity<>(problemDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void logMessage(Throwable throwable) {
        String simpleName = throwable.getClass().getSimpleName();
        LOG.error(ERROR_OCCURRED_WITH_MESSAGE, simpleName, throwable.getMessage());
        LOG.debug(ERROR_OCCURRED_STACK_TRACE, throwable);
    }

}
