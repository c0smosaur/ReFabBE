package cleanie.repatch.common.exception.handler;

import static cleanie.repatch.common.exception.model.ExceptionCode.INVALID_REQUEST;
import static cleanie.repatch.common.exception.model.ExceptionCode.INTERNAL_SERVER_ERROR;

import cleanie.repatch.common.exception.*;
import cleanie.repatch.common.exception.model.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Objects;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final HttpServletRequest request;

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            @NonNull final MethodArgumentNotValidException e,
            @NonNull final HttpHeaders headers,
            @NonNull final HttpStatusCode status,
            @NonNull final WebRequest request
    ) {
        log.warn(e.getMessage(), e);

        final String errorMessage = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
        return ResponseEntity.badRequest()
                .body(new ExceptionResponse(INVALID_REQUEST.getCode(), errorMessage));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionResponse> handleBadRequestException(final BadRequestException e) {
        log.warn(e.getMessage(), e);

        return ResponseEntity.badRequest()
                .body(new ExceptionResponse(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(UnAuthorizedAccessException.class)
    public ResponseEntity<ExceptionResponse> handleUnAuthorizedAccessException(final UnAuthorizedAccessException e) {
        log.warn(e.getMessage(), e);

        return ResponseEntity.status(401)
                .body(new ExceptionResponse(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleEntityNotFoundException(final EntityNotFoundException e) {
        log.warn(e.getMessage(), e);

        return ResponseEntity.status(404)
                .body(new ExceptionResponse(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(OAuthApiException.class)
    public ResponseEntity<ExceptionResponse> handleOAuthApiException(final OAuthApiException e) {
        // TODO: 프론트엔드 구현 완료 시, 에러 로그를 남기도록 변경 필요
        log.warn(e.getMessage(), e);

        return ResponseEntity.internalServerError()
                .body(new ExceptionResponse(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(DiscordWebhookException.class)
    public ResponseEntity<ExceptionResponse> handleDiscordWebhookException(final DiscordWebhookException e) {
        log.warn(e.getMessage(), e);

        return ResponseEntity.internalServerError()
                .body(new ExceptionResponse(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(final Exception e) {
        log.error(e.getMessage(), e);

        return ResponseEntity.internalServerError()
                .body(new ExceptionResponse(INTERNAL_SERVER_ERROR.getCode(), INTERNAL_SERVER_ERROR.getMessage()));
    }

}
