package com.neu.webserver.exception;

import com.neu.webserver.exception.auth.AlreadyExistsException;
import com.neu.webserver.exception.auth.MissingRegistryInformationException;
import com.neu.webserver.exception.search.*;
import com.neu.webserver.exception.user.IncorrectPasswordException;
import com.neu.webserver.protocol.exception.ErrorMessage;
import com.neu.webserver.repository.media.MediaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.NoSuchElementException;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MediaRepository mediaRepository;

    @ExceptionHandler(value = {
            AlreadyExistsException.class,
            MissingRegistryInformationException.class,
            IncorrectPasswordException.class,
            InvalidDownloadRequestParameterException.class
    })
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorMessage badRequestException(Exception e) {
        log.info(e.getMessage());
        return new ErrorMessage(e.getMessage());
    }

    @ExceptionHandler(value = AuthenticationException.class)
    @ResponseStatus(code = HttpStatus.FORBIDDEN)
    public ErrorMessage forbiddenException(Exception e) {
        return new ErrorMessage(e.getMessage());
    }

    @ExceptionHandler(value = NoSuchElementException.class)
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    public ErrorMessage unauthorizedException(Exception e) {
        return new ErrorMessage(e.getMessage());
    }

    @ExceptionHandler(value = {
            NoHandlerHandlesChainPackageException.class,
            UnableConnectYouTubeServiceException.class,
            UnableStartDownloadingException.class
    })
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Server internal error")
    public void internalErrorException(Exception e) {
        log.error(Arrays.toString(e.getStackTrace()));
    }


    @ExceptionHandler(value = {
            DownloadInterruptedException.class,
            DownloadTimeoutException.class
    })
    @ResponseStatus(code = HttpStatus.REQUEST_TIMEOUT, reason = "Unable to process download")
    public void timeoutRequestException(Exception e) {
        log.error(e.getMessage());
    }

}
