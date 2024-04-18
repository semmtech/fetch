package com.semmtech.laces.fetch.configuration.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TypeAlias("AddOn")
@Document(collection = "configurations")
public class AddOnEntity implements Identifiable {
    @Id
    private String id;
    private String name;
    private String description;
    private String displayName;
    private String targetType;

    @JsonProperty(value = "isActive")
    private boolean active;
    @JsonProperty(value = "isSimpleFeedback")
    private boolean simpleFeedback;

    private Date startDate;
    private Date endDate;
    @DBRef
    private SparqlEndpointEntity sparqlEndpoint;
    private String dataTarget;

    @JsonProperty(value = "visualize")
    @ApiModelProperty(required = true)
    private VisualizationEntity visualization;

    @JsonProperty(value = "import")
    private ImportEntity importConfiguration;

    @JsonIgnore
    public List<FilterFieldEntity> getFilterFields() {
        if (visualization != null) {
            return visualization.getFilterFields();
        }
        return new ArrayList<>();
    }

    @JsonIgnore
    public boolean hasQuery(String queryId) {
        if (visualization != null && visualization.hasQuery(queryId)) {
            return true;
        }
        if (importConfiguration != null && importConfiguration.hasQuery(queryId)) {
            return true;
        }
        return false;
    }

    @JsonIgnore
    public boolean hasTarget(String targetId) {
        if (importConfiguration != null && importConfiguration.hasTarget(targetId)) {
            return true;
        }
        if (visualization != null && visualization.hasTarget(targetId)) {
            return true;
        }
        return false;
    }

}
