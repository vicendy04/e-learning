package com.myproject.elearning.web.rest;

import com.myproject.elearning.domain.Module;
import com.myproject.elearning.service.ModuleService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for modules
 */
@RestController
@RequestMapping("/api/v1/")
public class ModuleController {
    private final ModuleService moduleService;

    public ModuleController(ModuleService moduleService) {
        this.moduleService = moduleService;
    }

    @PostMapping("/modules")
    public ResponseEntity<Module> createModule(@Valid @RequestBody Module module) {
        Module newModule = moduleService.createModule(module);
        return ResponseEntity.status(HttpStatus.CREATED).body(newModule);
    }

    @GetMapping("/modules/{id}")
    public ResponseEntity<Module> getModule(@PathVariable(name = "id") Long id) {
        Module module = moduleService.getModule(id);
        return ResponseEntity.status(HttpStatus.OK).body(module);
    }

    @GetMapping("/modules")
    public ResponseEntity<List<Module>> getAllModules() {
        List<Module> modules = moduleService.getAllModules();
        return ResponseEntity.status(HttpStatus.OK).body(modules);
    }

    @PutMapping("/modules")
    public ResponseEntity<Module> updateModule(@RequestBody Module module) {
        Module updatedModule = moduleService.updateModule(module);
        return ResponseEntity.status(HttpStatus.OK).body(updatedModule);
    }

    @DeleteMapping("/modules/{id}")
    public ResponseEntity<Void> deleteModule(@PathVariable(name = "id") Long id) {
        moduleService.deleteModule(id);
        return ResponseEntity.noContent().build();
    }
}
