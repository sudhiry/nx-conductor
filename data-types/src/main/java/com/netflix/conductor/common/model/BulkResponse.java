/*
 * Copyright 2020 Netflix, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.netflix.conductor.common.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

/**
 * Response object to return a list of succeeded entities and a map of failed ones, including error
 * message, for the bulk request.
 */
@Data
@NoArgsConstructor
public class BulkResponse {

    /** Key - entityId Value - error message processing this entity */
    private final Map<String, String> bulkErrorResults = new HashMap<>();

    private final List<String> bulkSuccessfulResults = new ArrayList<>();

    private final String message = "Bulk Request has been processed.";

    public void appendSuccessResponse(String id) {
        bulkSuccessfulResults.add(id);
    }

    public void appendFailedResponse(String id, String errorMessage) {
        bulkErrorResults.put(id, errorMessage);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BulkResponse that)) {
            return false;
        }
        return Objects.equals(bulkSuccessfulResults, that.bulkSuccessfulResults)
                && Objects.equals(bulkErrorResults, that.bulkErrorResults);
    }

}
