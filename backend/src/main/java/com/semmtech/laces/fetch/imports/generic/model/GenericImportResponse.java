package com.semmtech.laces.fetch.imports.generic.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class GenericImportResponse {
    @ApiModelProperty(value = "The description of the step to allow the user to identify which steps went well and which went wrong.")
    private String importStep;
    @ApiModelProperty(value = "Indicates success of this step")
    private boolean success;
    @ApiModelProperty(value = "When the import got aborted, this contains a list of descriptions of things that went wrong during the import.")
    private List<String> errors;
    @ApiModelProperty(value = "When the import finished, but some things may possibly affect the data consistency, this will contain a list of warnings.")
    private List<String> warnings;
    @ApiModelProperty(value = "This would be the message we prefer to see. All went well.")
    private String successMessage;
}
