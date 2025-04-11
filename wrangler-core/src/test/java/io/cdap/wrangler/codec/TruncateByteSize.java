/*
 * Copyright © 2025 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.cdap.wrangler.codec;

import io.cdap.wrangler.api.Arguments;
import io.cdap.wrangler.api.Directive;
import io.cdap.wrangler.api.ExecutorContext;
import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.api.DirectiveExecutionException;
import io.cdap.wrangler.api.parser.UsageDefinition;
import io.cdap.wrangler.api.parser.TokenType;
import io.cdap.wrangler.utils.ByteSize;

import java.util.ArrayList;
import java.util.List;

/**
 * Directive that filters rows based on whether a specified column value (in bytes) exceeds a threshold.
 */

public class TruncateByteSize implements Directive {
    private String column;
    private long threshold;

    @Override
    public UsageDefinition define() {
        UsageDefinition.Builder builder = UsageDefinition.builder("truncate-bytes");
        builder.define("column", TokenType.COLUMN_NAME);
        builder.define("threshold", TokenType.TEXT);
        return builder.build();
    }
    
    
/**
 * Parses and sets the column and byte-size threshold for the directive.
 */

    @Override
    public void initialize(Arguments arguments) {
        this.column = arguments.value("column");
        try {
            String thresholdStr = arguments.value("threshold");
            this.threshold = new ByteSize(thresholdStr).getBytes();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse threshold value: " + e.getMessage());
        }
    }

    /**
 * Filters rows based on whether the column value is below the byte threshold.
 */

    @Override
    public List<Row> execute(List<Row> rows, ExecutorContext context) throws DirectiveExecutionException {
        List<Row> output = new ArrayList<>();

        for (Row row : rows) {
            Object value = row.getValue(column);
            if (value instanceof Number) {
                long size = ((Number) value).longValue();
                if (size <= threshold) {
                    output.add(row);
                }
            } else {
                throw new DirectiveExecutionException("Column '" + column + "' is not numeric.");
            }
        }

        return output;
    }

    @Override
    public void destroy() {
        // no-op
    }
}
