package hanghaeboard.api.exception;

import hanghaeboard.api.ApiResponse;
import hanghaeboard.api.exception.exception.AuthorityException;
import hanghaeboard.api.exception.exception.InvalidPasswordException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        ObjectError objectError = ex.getAllErrors().get(0);

        return ApiResponse.of(HttpStatus.BAD_REQUEST, objectError.getDefaultMessage());
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public ApiResponse<Object> bindException(BindException e) {
        ObjectError objectError = e.getAllErrors().get(0);

        return ApiResponse.of(HttpStatus.BAD_REQUEST, objectError.getDefaultMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ApiResponse<Object> entityNotFoundExceptions(EntityNotFoundException e) {
        return ApiResponse.of(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidPasswordException.class)
    public ApiResponse<Object> invalidPasswordExceptions(InvalidPasswordException e) {
        return ApiResponse.of(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponse<Object> illegalArgumentException(IllegalArgumentException e){
        return ApiResponse.of(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicateKeyException.class)
    public ApiResponse<Object> duplicateKeyException(DuplicateKeyException e){
        return ApiResponse.of(HttpStatus.CONFLICT, e.getMessage());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(ExpiredJwtException.class)
    public ApiResponse<Object> expiredJwtException(ExpiredJwtException e){
        return ApiResponse.of(HttpStatus.UNAUTHORIZED, "JWT 토큰이 만료되었습니다.");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MalformedJwtException.class)
    public ApiResponse<Object> malformedJwtException(MalformedJwtException e){
        return ApiResponse.of(HttpStatus.BAD_REQUEST, "잘못된 JWT 토큰입니다.");
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(SignatureException.class)
    public ApiResponse<Object> signatureException(SignatureException e){
        return ApiResponse.of(HttpStatus.FORBIDDEN, "JWT 토큰 서명이 올바르지 않습니다.");
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(JwtException.class)
    public ApiResponse<Object> jwtException(JwtException e){
        return ApiResponse.of(HttpStatus.UNAUTHORIZED, "유효하지 않은 JWT 토큰입니다.");
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AuthorityException.class)
    public ApiResponse<Object> authorityException(AuthorityException e){
        return ApiResponse.of(HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ApiResponse<Object> exception(Exception e){
        return ApiResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, "서버에 예상치 못한 오류가 발생했습니다.");
    }

}
