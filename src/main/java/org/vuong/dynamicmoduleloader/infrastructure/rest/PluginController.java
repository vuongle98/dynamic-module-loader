package org.vuong.dynamicmoduleloader.infrastructure.rest;

import org.springframework.web.bind.annotation.*;
import org.vuong.dynamicmoduleloader.application.service.PluginRuntimeService;
import org.vuong.dynamicmoduleloader.domain.model.Plugin;

import java.util.*;

@RestController
@RequestMapping("/api/plugins")
public class PluginController {

    private final PluginRuntimeService pluginRuntimeService;

    public PluginController(PluginRuntimeService pluginRuntimeService) {
        this.pluginRuntimeService = pluginRuntimeService;
    }

    @PostMapping("/upload")
    public Plugin uploadPlugin(@RequestParam String className, @RequestBody String javaCode) throws Exception {
        return pluginRuntimeService.compileAndRegister(className, javaCode);
    }

    @GetMapping
    public Collection<Plugin> list() {
        return pluginRuntimeService.getAllPlugins();
    }

    @GetMapping("/{name}")
    public Plugin get(@PathVariable String name) {
        return pluginRuntimeService.getPlugin(name);
    }

    @DeleteMapping("/{name}")
    public String delete(@PathVariable String name) {
        pluginRuntimeService.removePlugin(name);
        return "Deleted plugin: " + name;
    }
}
