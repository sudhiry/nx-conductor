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
package com.netflix.conductor.common.metadata.workflow;


import com.netflix.conductor.common.constraints.NoSemiColonConstraint;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Objects;

@Data
public class WorkflowDefSummary implements Comparable<WorkflowDefSummary> {

    @NotEmpty(message = "WorkflowDef name cannot be null or empty")
    @NoSemiColonConstraint(
            message = "Workflow name cannot contain the following set of characters: ':'")
    private String name;

    private int version = 1;

    private Long createTime;

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getVersion());
    }

    @Override
    public String toString() {
        return "WorkflowDef{name='" + name + ", version=" + version + "}";
    }

    @Override
    public int compareTo(WorkflowDefSummary o) {
        int res = this.name.compareTo(o.name);
        if (res != 0) {
            return res;
        }
        res = Integer.compare(this.version, o.version);
        return res;
    }
}
