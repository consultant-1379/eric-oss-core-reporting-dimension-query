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
package com.ericsson.oss.air.cardq.exceptions;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.SocketTimeoutException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;

import com.ericsson.oss.air.cardq.AbstractTestSetup;
import com.ericsson.oss.air.cardq.api.model.ProblemDetails;
import com.ericsson.oss.air.cardq.error.InternalRuntimeException;
import com.ericsson.oss.air.cardq.error.MandatoryParameterException;
import com.ericsson.oss.air.cardq.error.handlers.ApplicationExceptionHandler;

@ExtendWith(MockitoExtension.class)
public class ApplicationExceptionHandlerTests extends AbstractTestSetup {

    ApplicationExceptionHandler applicationExceptionHandler = new ApplicationExceptionHandler();

    @Test
    public void shouldHandleAnyException() {
        try {
            throw new RuntimeException("test");
        } catch (Exception exception) {
            ResponseEntity<ProblemDetails> objectResponseEntity = applicationExceptionHandler.handleAll(exception);
            ProblemDetails problemDetails = objectResponseEntity.getBody();
            assertThat(problemDetails.getTitle()).isEqualTo("Error occurred RuntimeException");
            assertThat(problemDetails.getStatus()).isEqualTo(500);
            assertThat(problemDetails.getDetail()).isEqualTo("test");
        }
    }

    @Test
    public void shouldHandleMandatoryParameterException() {
        try {
            throw new MandatoryParameterException("test");
        } catch (MandatoryParameterException exception) {
            ResponseEntity<ProblemDetails> objectResponseEntity = applicationExceptionHandler.handleMandatoryParameterException(exception);
            ProblemDetails problemDetails = objectResponseEntity.getBody();
            assertThat(problemDetails.getTitle()).isEqualTo("Error occurred MandatoryParameterException");
            assertThat(problemDetails.getStatus()).isEqualTo(400);
            assertThat(problemDetails.getDetail()).isEqualTo("test");
        }
    }

    @Test
    public void shouldHandleResourceAccessException() {
        try {
            throw new ResourceAccessException("Connect timed out", new SocketTimeoutException());
        } catch (ResourceAccessException exception) {
            ResponseEntity<ProblemDetails> objectResponseEntity = applicationExceptionHandler.handleTimeoutExceptions(exception);
            ProblemDetails problemDetails = objectResponseEntity.getBody();
            assertThat(problemDetails.getTitle()).isEqualTo("Connect Timed Out, ResourceAccessException");
            assertThat(problemDetails.getStatus()).isEqualTo(500);
            assertThat(problemDetails.getDetail()).contains("Connection Timed Out",
                                                            "Retried:",
                                                            "Reason: Connect timed out");
        }
    }

    @Test
    public void shouldHandleIllegalArgumentException() {
        try {
            throw new IllegalArgumentException("Missing or empty query field: [augmentationFields, inputFields]");
        } catch (IllegalArgumentException exception) {
            ResponseEntity<ProblemDetails> objectResponseEntity = applicationExceptionHandler.handleIllegalArgumentException(exception);
            ProblemDetails problemDetails = objectResponseEntity.getBody();
            assertThat(problemDetails.getTitle()).isEqualTo("Malformed Request, IllegalArgumentException");
            assertThat(problemDetails.getStatus()).isEqualTo(400);
            assertThat(problemDetails.getDetail())
                    .contains("Malformed Request, Reason: Missing or empty query field: [augmentationFields, inputFields]");
        }
    }

    @Test
    public void shouldhandleServiceUnavailableException() {
        try {
            throw new TransientDataAccessResourceException("Error Message");
        } catch (final TransientDataAccessResourceException exception) {
            final ResponseEntity<ProblemDetails> objectResponseEntity = this.applicationExceptionHandler.handleServiceUnavailableException(exception);
            final ProblemDetails problemDetails = objectResponseEntity.getBody();
            assertThat(problemDetails.getTitle()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase());
            assertThat(problemDetails.getStatus()).isEqualTo(503);
            assertThat(problemDetails.getDetail()).contains("Service is unavailable",
                                                            "Retried:",
                                                            "Reason: Error Message");
        }
    }

    @Test
    public void shouldHandleInternalRuntimeException() {
        try {
            throw new InternalRuntimeException("Missing dynamic attribute: displayType, in Entity Collection: Entity-1");
        } catch (InternalRuntimeException exception) {
            ResponseEntity<ProblemDetails> objectResponseEntity = applicationExceptionHandler.handleInternalRuntimeException(exception);
            ProblemDetails problemDetails = objectResponseEntity.getBody();
            assertThat(problemDetails.getTitle()).isEqualTo("Internal Server Error, InternalRuntimeException");
            assertThat(problemDetails.getStatus()).isEqualTo(500);
            assertThat(problemDetails.getDetail())
                    .contains("Missing dynamic attribute: displayType, in Entity Collection: Entity-1");
        }
    }
}
