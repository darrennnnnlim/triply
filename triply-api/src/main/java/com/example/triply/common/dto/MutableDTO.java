package com.example.triply.common.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public class MutableDTO implements Serializable {
    private LocalDateTime lastModifiedDate;
    private String lastModifiedBy;
}
