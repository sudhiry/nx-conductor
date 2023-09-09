package com.netflix.conductor.api.controller;


import com.netflix.conductor.schema.metadata.tasks.TaskDef;
import com.netflix.conductor.schema.metadata.workflow.WorkflowDef;
import com.netflix.conductor.schema.metadata.BulkResponse;
import com.netflix.conductor.schema.metadata.workflow.WorkflowDefSummary;
import com.netflix.conductor.service.MetadataService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/metadata")
public class MetadataResource {

    private final MetadataService metadataService;

    public MetadataResource(MetadataService metadataService) {
        super();
        this.metadataService = metadataService;
    }

    @PostMapping("/workflow")
    @Operation(summary = "Create a new workflow definition")
    public void create(@RequestBody WorkflowDef workflowDef) {
        metadataService.registerWorkflowDef(workflowDef);
    }

    @PostMapping("/workflow/validate")
    @Operation(summary = "Validates a new workflow definition")
    public void validate(@RequestBody WorkflowDef workflowDef) {
        metadataService.validateWorkflowDef(workflowDef);
    }

    @PutMapping("/workflow")
    @Operation(summary = "Create or update workflow definition")
    public BulkResponse update(@RequestBody List<WorkflowDef> workflowDefs) {
        return metadataService.updateWorkflowDef(workflowDefs);
    }

    @Operation(summary = "Retrieves workflow definition along with blueprint")
    @GetMapping("/workflow/{name}")
    public WorkflowDef get(
            @PathVariable("name") String name,
            @RequestParam(value = "version", required = false) Integer version) {
        return metadataService.getWorkflowDef(name, version);
    }

    @Operation(summary = "Retrieves all workflow definition along with blueprint")
    @GetMapping("/workflow")
    public List<WorkflowDef> getAll() {
        return metadataService.getWorkflowDefs();
    }

    @Operation(summary = "Returns workflow names and versions only (no definition bodies)")
    @GetMapping("/workflow/names-and-versions")
    public Map<String, ? extends Iterable<WorkflowDefSummary>> getWorkflowNamesAndVersions() {
        return metadataService.getWorkflowNamesAndVersions();
    }

    @Operation(summary = "Returns only the latest version of all workflow definitions")
    @GetMapping("/workflow/latest-versions")
    public List<WorkflowDef> getAllWorkflowsWithLatestVersions() {
        return metadataService.getWorkflowDefsLatestVersions();
    }

    @DeleteMapping("/workflow/{name}/{version}")
    @Operation(
            summary =
                    "Removes workflow definition. It does not remove workflows associated with the definition.")
    public void unregisterWorkflowDef(
            @PathVariable("name") String name, @PathVariable("version") Integer version) {
        metadataService.unregisterWorkflowDef(name, version);
    }

    @PostMapping("/taskdefs")
    @Operation(summary = "Create new task definition(s)")
    public void registerTaskDef(@RequestBody List<TaskDef> taskDefs) {
        metadataService.registerTaskDef(taskDefs);
    }

    @PutMapping("/taskdefs")
    @Operation(summary = "Update an existing task")
    public void registerTaskDef(@RequestBody TaskDef taskDef) {
        metadataService.updateTaskDef(taskDef);
    }

    @GetMapping(value = "/taskdefs")
    @Operation(summary = "Gets all task definition")
    public List<TaskDef> getTaskDefs() {
        return metadataService.getTaskDefs();
    }

    @GetMapping("/taskdefs/{tasktype}")
    @Operation(summary = "Gets the task definition")
    public TaskDef getTaskDef(@PathVariable("tasktype") String taskType) {
        return metadataService.getTaskDef(taskType);
    }

    @DeleteMapping("/taskdefs/{tasktype}")
    @Operation(summary = "Remove a task definition")
    public void unregisterTaskDef(@PathVariable("tasktype") String taskType) {
        metadataService.unregisterTaskDef(taskType);
    }

}
