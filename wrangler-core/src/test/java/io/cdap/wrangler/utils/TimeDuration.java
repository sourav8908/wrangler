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

public class TimeDuration {
    private static final Map<String, Long> UNIT_MULTIPLIERS = new HashMap<>();
    private final long milliseconds;

    static {
        UNIT_MULTIPLIERS.put("MS", 1L);
        UNIT_MULTIPLIERS.put("S", 1000L);
        UNIT_MULTIPLIERS.put("M", 60 * 1000L);
        UNIT_MULTIPLIERS.put("H", 60 * 60 * 1000L);
        UNIT_MULTIPLIERS.put("D", 24 * 60 * 60 * 1000L);
    }

    public TimeDuration(String input) {
        input = input.trim().toUpperCase();

        String numberPart = input.replaceAll("[^0-9.]", "");
        String unitPart = input.replaceAll("[0-9.]", "");

        if (!UNIT_MULTIPLIERS.containsKey(unitPart)) {
            throw new IllegalArgumentException("Unknown time unit: " + unitPart);
        }

        double value = Double.parseDouble(numberPart);
        this.milliseconds = (long) (value * UNIT_MULTIPLIERS.get(unitPart));
    }

    public long getMilliseconds() {
        return milliseconds;
    }

    @Override
    public String toString() {
        return milliseconds + " ms";
    }
}
