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

package io.cdap.wrangler.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for parsing byte size strings like "10KB", "1.5MB", or "2GB" into byte values.
 */
public class ByteSize {
    private static final Map<String, Long> UNIT_MULTIPLIERS = new HashMap<>();
    private final long bytes;

    static {
        UNIT_MULTIPLIERS.put("B", 1L);
        UNIT_MULTIPLIERS.put("KB", 1024L);
        UNIT_MULTIPLIERS.put("MB", 1024L * 1024);
        UNIT_MULTIPLIERS.put("GB", 1024L * 1024 * 1024);
    }

    public ByteSize(String input) {
        input = input.trim().toUpperCase();

        String numberPart = input.replaceAll("[^0-9.]", "");
        String unitPart = input.replaceAll("[0-9.]", "");

        if (!UNIT_MULTIPLIERS.containsKey(unitPart)) {
            throw new IllegalArgumentException("Unknown unit: " + unitPart);
        }

        double value = Double.parseDouble(numberPart);
        this.bytes = (long) (value * UNIT_MULTIPLIERS.get(unitPart));
    }

    public long getBytes() {
        return bytes;
    }

    @Override
    public String toString() {
        return bytes + " bytes";
    }
}
