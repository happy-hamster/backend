package de.sakpaas.backend.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Error400Dto {

    private Date timestamp;
    private final int status = 400;
    private final String error = "Bad Request";
    private String message;
    private String path;

}
