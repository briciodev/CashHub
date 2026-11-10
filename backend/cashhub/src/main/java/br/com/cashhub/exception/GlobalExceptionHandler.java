package br.com.cashhub.exception;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MovimentacaoNaoEncontradaException.class)
    public ResponseEntity<Map<String, Object>> tratarNaoEncontrado(
            MovimentacaoNaoEncontradaException ex,
            HttpServletRequest request) {

        Map<String, Object> resposta = new LinkedHashMap<>();

        resposta.put("timestamp", Instant.now());
        resposta.put("status", 404);
        resposta.put("error", "Not Found");
        resposta.put("message", ex.getMessage());
        resposta.put("path", request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(resposta);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> tratarValidacao(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> erros = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        erro -> erro.getField(),
                        erro -> erro.getDefaultMessage() == null
                                ? "Valor inválido."
                                : erro.getDefaultMessage(),
                        (primeiro, segundo) -> primeiro
                ));

        Map<String, Object> resposta = new LinkedHashMap<>();

        resposta.put("timestamp", Instant.now());
        resposta.put("status", 400);
        resposta.put("error", "Bad Request");
        resposta.put("message", "Os dados enviados são inválidos.");
        resposta.put("fields", erros);
        resposta.put("path", request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(resposta);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> tratarErroInterno(
            Exception ex,
            HttpServletRequest request) {

        Map<String, Object> resposta = new LinkedHashMap<>();

        resposta.put("timestamp", Instant.now());
        resposta.put("status", 500);
        resposta.put("error", "Internal Server Error");
        resposta.put("message", "Ocorreu um erro interno no servidor.");
        resposta.put("path", request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(resposta);
    }
}

