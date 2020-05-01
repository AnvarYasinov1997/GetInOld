package com.wellcome.main.aop

import com.wellcome.main.dto.ErrorResponse
import com.wellcome.main.exception.*
import com.wellcome.main.util.functions.getMessage
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import javax.persistence.EntityNotFoundException

@RestControllerAdvice
open class RestControllerExceptionHandler {

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException::class)
    open fun entityNotFoundExceptionHandler(e: EntityNotFoundException): ErrorResponse {
        e.printStackTrace()
        return ErrorResponse(e.getMessage())
    }

    @ResponseStatus(code = HttpStatus.CONFLICT)
    @ExceptionHandler(BirthdayCampaignPatternExistException::class)
    open fun birthdayCampaignPatternExistException(e: BirthdayCampaignPatternExistException): ErrorResponse {
        e.printStackTrace()
        return ErrorResponse(e.getMessage())
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Exception::class)
    open fun exceptionHandler(e: Exception): ErrorResponse {
        e.printStackTrace()
        return ErrorResponse(e.getMessage())
    }

    @ResponseStatus(code = HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException::class)
    open fun accessDeniedExceptionHandler(e: AccessDeniedException): ErrorResponse {
        e.printStackTrace()
        return ErrorResponse(e.getMessage())
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @ExceptionHandler(NullPointerException::class)
    open fun nullPointerExceptionHandler(e: NullPointerException): ErrorResponse {
        e.printStackTrace()
        return ErrorResponse(e.getMessage())
    }

    @ResponseStatus(code = HttpStatus.OK)
    @ExceptionHandler(InstitutionExistException::class)
    open fun institutionExistExceptionHandler(e: InstitutionExistException): ErrorResponse {
        e.printStackTrace()
        return ErrorResponse(e.getMessage())
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @ExceptionHandler(LanguageUnsupportedException::class)
    open fun languageUnsupportedExceptionHandler(e: LanguageUnsupportedException): ErrorResponse {
        e.printStackTrace()
        return ErrorResponse(e.getMessage())
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @ExceptionHandler(RuntimeException::class)
    open fun runtimeException(e: RuntimeException): ErrorResponse {
        e.printStackTrace()
        return ErrorResponse(e.getMessage())
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InstitutionOfferCreationException::class)
    open fun institutionOfferCreationException(e: InstitutionOfferCreationException): ErrorResponse {
        e.printStackTrace()
        return ErrorResponse(e.getMessage())
    }

    @ResponseStatus(code = HttpStatus.OK)
    @ExceptionHandler(ConcurrentDuplicateUniqueEntityException::class)
    open fun concurrentDuplicateUniqueEntityException(e: ConcurrentDuplicateUniqueEntityException): ErrorResponse {
        e.printStackTrace()
        return ErrorResponse(e.getMessage())
    }

    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException::class)
    open fun unauthorizedException(e: UnauthorizedException): ErrorResponse {
        e.printStackTrace()
        return ErrorResponse(e.getMessage())
    }

    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(GoogleTokenParseException::class)
    open fun googleTokenParseException(e: GoogleTokenParseException): ErrorResponse {
        e.printStackTrace()
        return ErrorResponse(e.getMessage())
    }

}