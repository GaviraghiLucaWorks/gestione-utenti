package com.intesigroup.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleInvalidRole(HttpMessageNotReadableException ex) {
        if (ex.getCause() instanceof InvalidFormatException formatEx) {
            if (formatEx.getTargetType().isEnum()) {
                String invalidValue = formatEx.getValue().toString();
                String fieldName = formatEx.getPath().get(0).getFieldName();
                Class<?> enumType = formatEx.getTargetType();
                Object[] validValues = enumType.getEnumConstants();

                Map<String, String> error = new HashMap<>();
                error.put("error", "Valore non valido per '" + fieldName + "': '" + invalidValue + "'.");
                error.put("messaggio", "I ruoli validi sono: " + Arrays.toString(validValues));

                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
            }
        }

        return new ResponseEntity<>(Map.of("errore", "Richiesta malformata"), HttpStatus.BAD_REQUEST);
    }
}