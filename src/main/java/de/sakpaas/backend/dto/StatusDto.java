package de.sakpaas.backend.dto;

import lombok.Data;

@Data
public class StatusDto {
    private boolean status;
    private String version;
    private String commit;
}
