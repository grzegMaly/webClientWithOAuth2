package com.myproject.webclient.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class CustomerDTO {

    private String id;
    private String customerName;
    private Integer version;
    private Instant createdDate;
    private Instant lastModifiedDate;
}
