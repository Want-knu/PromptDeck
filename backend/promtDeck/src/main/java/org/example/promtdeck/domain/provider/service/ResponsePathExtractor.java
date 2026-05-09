package org.example.promtdeck.domain.provider.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.promtdeck.global.common.ErrorCode;
import org.example.promtdeck.global.exception.CustomException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ResponsePathExtractor {

    private final ObjectMapper objectMapper;

    public String extract(String responseBody, String responsePath) {
        if (!StringUtils.hasText(responseBody) || !StringUtils.hasText(responsePath)) {
            return null;
        }

        try {
            JsonNode current = objectMapper.readTree(responseBody);

            for (PathToken token : parse(responsePath)) {
                current = token.apply(current);

                if (current == null || current.isMissingNode() || current.isNull()) {
                    return null;
                }
            }

            if (current.isValueNode()) {
                return current.asText();
            }

            return objectMapper.writeValueAsString(current);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_RESPONSE_PATH);
        }
    }

    private List<PathToken> parse(String responsePath) {
        List<PathToken> tokens = new ArrayList<>();

        for (String segment : responsePath.split("\\.")) {
            if (!StringUtils.hasText(segment)) {
                throw new CustomException(ErrorCode.INVALID_RESPONSE_PATH);
            }

            int cursor = 0;
            int bracketIndex = segment.indexOf('[');

            if (bracketIndex > 0) {
                tokens.add(PathToken.field(segment.substring(0, bracketIndex)));
                cursor = bracketIndex;
            } else if (bracketIndex < 0) {
                tokens.add(PathToken.field(segment));
                continue;
            }

            while (cursor < segment.length()) {
                if (segment.charAt(cursor) != '[') {
                    throw new CustomException(ErrorCode.INVALID_RESPONSE_PATH);
                }

                int end = segment.indexOf(']', cursor);

                if (end < 0) {
                    throw new CustomException(ErrorCode.INVALID_RESPONSE_PATH);
                }

                try {
                    tokens.add(PathToken.index(Integer.parseInt(segment.substring(cursor + 1, end))));
                } catch (NumberFormatException e) {
                    throw new CustomException(ErrorCode.INVALID_RESPONSE_PATH);
                }

                cursor = end + 1;
            }
        }

        return tokens;
    }

    private record PathToken(String field, Integer index) {

        static PathToken field(String field) {
            return new PathToken(field, null);
        }

        static PathToken index(Integer index) {
            return new PathToken(null, index);
        }

        JsonNode apply(JsonNode node) {
            if (field != null) {
                return node.path(field);
            }

            if (!node.isArray() || index < 0 || index >= node.size()) {
                return null;
            }

            return node.get(index);
        }
    }
}
