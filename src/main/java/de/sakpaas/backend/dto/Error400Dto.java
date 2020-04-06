package de.sakpaas.backend.dto;

import java.util.Date;
import lombok.Data;

@Data
public class Error400Dto {

  private final int status = 400;
  private final String error = "Bad Request";
  private Date timestamp;
  private String message;
  private String path;

}
