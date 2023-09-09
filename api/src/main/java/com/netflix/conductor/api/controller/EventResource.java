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
package com.netflix.conductor.api.controller;

import com.netflix.conductor.schema.metadata.events.EventHandler;
import com.netflix.conductor.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/event")
public class EventResource {

    private final EventService eventService;

    public EventResource(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    @Operation(summary = "Add a new event handler.")
    public void addEventHandler(@RequestBody EventHandler eventHandler) {
        eventService.addEventHandler(eventHandler);
    }

    @PutMapping
    @Operation(summary = "Update an existing event handler.")
    public void updateEventHandler(@RequestBody EventHandler eventHandler) {
        eventService.updateEventHandler(eventHandler);
    }

    @DeleteMapping("/{name}")
    @Operation(summary = "Remove an event handler")
    public void removeEventHandlerStatus(@PathVariable("name") String name) {
        eventService.removeEventHandlerStatus(name);
    }

    @GetMapping
    @Operation(summary = "Get all the event handlers")
    public List<EventHandler> getEventHandlers() {
        return eventService.getEventHandlers();
    }

    @GetMapping("/{event}")
    @Operation(summary = "Get event handlers for a given event")
    public List<EventHandler> getEventHandlersForEvent(
            @PathVariable("event") String event,
            @RequestParam(value = "activeOnly", defaultValue = "true", required = false)
                    boolean activeOnly) {
        return eventService.getEventHandlersForEvent(event, activeOnly);
    }
}