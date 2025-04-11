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

import io.cdap.wrangler.TestingRig;
import io.cdap.wrangler.api.Row;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class AggregateStatsTest {

    @Test
    public void testAggregateStats() throws Exception {
        List<Row> rows = new ArrayList<>();
        rows.add(new Row("data_transfer_size", "1MB").add("response_time", "2s"));
        rows.add(new Row("data_transfer_size", "512KB").add("response_time", "1s"));

        String[] recipe = new String[] {
            "aggregate-stats :data_transfer_size :response_time total_size_mb total_time_sec"
        };

        List<Row> results = TestingRig.execute(recipe, rows);

        Assert.assertEquals(1, results.size());

        Row result = results.get(0);
        double totalMB = (1 * 1024 * 1024 + 512 * 1024) / (1024.0 * 1024.0);
        double totalSec = 3.0;

        Assert.assertEquals(totalMB, (Double) result.getValue("total_size_mb"), 0.001);
        Assert.assertEquals(totalSec, (Double) result.getValue("total_time_sec"), 0.001);
    }
}
