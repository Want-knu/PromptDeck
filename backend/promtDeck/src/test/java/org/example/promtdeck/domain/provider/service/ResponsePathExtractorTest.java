package org.example.promtdeck.domain.provider.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.promtdeck.global.exception.CustomException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResponsePathExtractorTest {

    private final ResponsePathExtractor extractor = new ResponsePathExtractor(new ObjectMapper());

    @Test
    void extractsNestedTextByDotAndArrayPath() {
        String body = """
                {
                  "candidates": [
                    {
                      "content": {
                        "parts": [
                          { "text": "hello" }
                        ]
                      }
                    }
                  ]
                }
                """;

        String extracted = extractor.extract(body, "candidates[0].content.parts[0].text");

        assertThat(extracted).isEqualTo("hello");
    }

    @Test
    void returnsNullWhenPathDoesNotExist() {
        String extracted = extractor.extract("{\"output_text\":\"hello\"}", "missing.value");

        assertThat(extracted).isNull();
    }

    @Test
    void rejectsInvalidPathSyntax() {
        assertThatThrownBy(() -> extractor.extract("{\"output_text\":\"hello\"}", "output_text["))
                .isInstanceOf(CustomException.class);
    }
}
