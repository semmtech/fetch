package com.semmtech.laces.fetch.visualization.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonParameter {
    private static final String HEADER = "header";

    private String id;
    private String value;
    private String type;

    @JsonIgnore
    public boolean isHeader() {
        return HEADER.equalsIgnoreCase(type);
    }

    @JsonIgnore
    public boolean isNoHeader() {
        return !isHeader();
    }
}
