package com.polymath.jobboard.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PageImplDeserializer extends JsonDeserializer<PageImpl<?>> {

    @Override
    public PageImpl<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode root = mapper.readTree(p);

        // Extract content
        JsonNode contentNode = root.get("content");
        List<?> content = mapper.convertValue(contentNode, List.class);

        // Extract pageable information
        JsonNode pageableNode = root.get("pageable");
        int pageNumber = pageableNode.get("pageNumber").asInt();
        int pageSize = pageableNode.get("pageSize").asInt();
        JsonNode sortNode = pageableNode.get("sort");

        // Extract sort information
        List<Sort.Order> orders = new ArrayList<>();
        if (sortNode != null && sortNode.isArray()) {
            for (JsonNode orderNode : sortNode) {
                String property = orderNode.get("property").asText();
                String direction = orderNode.get("direction").asText();
                orders.add(new Sort.Order(Sort.Direction.fromString(direction), property));
            }
        }
        Sort sort = Sort.by(orders);

        // Create PageRequest
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);

        // Extract total elements
        long totalElements = root.get("totalElements").asLong();

        return new PageImpl<>(content, pageRequest, totalElements);
    }
}