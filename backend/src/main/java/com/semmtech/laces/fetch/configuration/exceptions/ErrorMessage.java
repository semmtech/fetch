package com.semmtech.laces.fetch.configuration.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
class ErrorMessage {

    //@ApiModelProperty(value = "Unique code for the message.")
    private String code;

    //@ApiModelProperty(value = "Description of the message.")
    private String message;
}