package com.semmtech.laces.fetch.configuration.dtos.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.semmtech.laces.fetch.configuration.dtos.common.BaseVisualizationDto;
import org.jooq.lambda.Unchecked;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CustomVisualizationDeserializer extends StdDeserializer<BaseVisualizationDto> {

    // the registry of unique field names to Class types
    private Map<String, Class<? extends BaseVisualizationDto>> registry;

    public CustomVisualizationDeserializer(Class<? extends BaseVisualizationDto> clazz) {
        super(clazz);
        registry = new HashMap<>();
    }

    public void register(String typeName, Class<? extends BaseVisualizationDto> clazz) {
        registry.put(typeName, clazz);
    }

    @Override
    public BaseVisualizationDto deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        ObjectNode obj = mapper.readTree(jsonParser);
        JsonNode additionalInputs = obj.path("additionalInputs");

        return registry.keySet()
                .stream()
                .filter(key -> additionalInputs.path(key).getNodeType() != JsonNodeType.MISSING)
                .map(registry::get)
                .map(Unchecked.function(clazz -> mapper.treeToValue(obj, clazz)))
                .findFirst()
                .orElse(null);
    }
}
