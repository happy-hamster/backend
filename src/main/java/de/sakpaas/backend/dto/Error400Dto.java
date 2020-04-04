package de.sakpaas.backend.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Error400Dto {

    private Date timestamp;
    private int status;
    private List<String> messages;

}
