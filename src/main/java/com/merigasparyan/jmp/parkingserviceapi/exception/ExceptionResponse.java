package com.merigasparyan.jmp.parkingserviceapi.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Data
@Builder
@Getter
public class ExceptionResponse {

    private String message;
    private HttpStatus status;

    @JsonProperty("status")
    public Integer getStatusCode() {
        return status.value();
    }
}
