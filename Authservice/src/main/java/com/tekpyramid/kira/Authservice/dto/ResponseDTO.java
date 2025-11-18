package com.tekpyramid.kira.Authservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDTO {
    private HttpStatus httpStatus;
    private String message;
    private Object data;
}
