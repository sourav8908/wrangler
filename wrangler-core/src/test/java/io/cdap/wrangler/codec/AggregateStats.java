/*
 * Copyright © 2025 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

 package io.cdap.wrangler.codec;

 import java.util.ArrayList;
 import java.util.List;
 
 import io.cdap.wrangler.api.Arguments;
 import io.cdap.wrangler.api.Directive;
 import io.cdap.wrangler.api.DirectiveExecutionException;
 import io.cdap.wrangler.api.ExecutorContext;
 import io.cdap.wrangler.api.Row;
 import io.cdap.wrangler.api.parser.TokenType;
 import io.cdap.wrangler.api.parser.UsageDefinition;
 import io.cdap.wrangler.utils.ByteSize;
 import io.cdap.wrangler.utils.TimeDuration;
 
 /**
  * Aggregates byte size and time duration values across rows and outputs the totals.
  */
 public class AggregateStats implements Directive {
   private String sizeColumn;
   private String timeColumn;
   private String outSizeColumn;
   private String outTimeColumn;
 
   private long totalBytes = 0;
   private long totalMillis = 0;
 
   @Override
   public UsageDefinition define() {
     UsageDefinition.Builder builder = UsageDefinition.builder("aggregate-stats");
     builder.define("sizeCol", TokenType.COLUMN_NAME);
     builder.define("timeCol", TokenType.COLUMN_NAME);
     builder.define("outSize", TokenType.TEXT);
     builder.define("outTime", TokenType.TEXT);
     return builder.build();
   }
 
   @Override
   public void initialize(Arguments arguments) {
     sizeColumn = arguments.value("sizeCol");
     timeColumn = arguments.value("timeCol");
     outSizeColumn = arguments.value("outSize");
     outTimeColumn = arguments.value("outTime");
   }
 
   @Override
   public List<Row> execute(List<Row> rows, ExecutorContext context)
       throws DirectiveExecutionException {
     for (Row row : rows) {
       Object sizeVal = row.getValue(sizeColumn);
       Object timeVal = row.getValue(timeColumn);
 
       try {
         long sizeBytes = new ByteSize(sizeVal.toString()).getBytes();
         long timeMillis = new TimeDuration(timeVal.toString()).getMilliseconds();
 
         totalBytes += sizeBytes;
         totalMillis += timeMillis;
       } catch (Exception e) {
         throw new DirectiveExecutionException("Failed to parse values: " + e.getMessage());
       }
     }
 
     double totalMB = totalBytes / (1024.0 * 1024.0);
     double totalSec = totalMillis / 1000.0;
 
     Row result = new Row();
     result.add(outSizeColumn, totalMB);
     result.add(outTimeColumn, totalSec);
 
     List<Row> results = new ArrayList<>();
     results.add(result);
     return results;
   }
 
   @Override
   public void destroy() {
     // no-op
   }
 }
 