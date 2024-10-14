package com.myproject.elearning.web.rest;

import com.myproject.elearning.domain.Module;
import com.myproject.elearning.service.ModuleService;
import com.myproject.elearning.service.dto.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing modules.
 */
@RestController
@RequestMapping("/api/v1/")
public class ModuleController {
    private final ModuleService moduleService;

    public ModuleController(ModuleService moduleService) {
        this.moduleService = moduleService;
    }

    /**
     * Creates a new template module that does not belong to any course.
     *
     * @param module The template module to create.
     * @return A {@link ResponseEntity} with status {@code 201 (Created)} and the created template module wrapped in {@link ApiResponse}.
     */
    @PostMapping("/modules")
    public ResponseEntity<ApiResponse<Module>> createModule(@Valid @RequestBody Module module) {
        Module newModule = moduleService.createModule(module);
        ApiResponse<Module> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Module created successfully");
        response.setData(newModule);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/modules/{id}")
    public ResponseEntity<ApiResponse<Module>> getModule(@PathVariable(name = "id") Long id) {
        Module module = moduleService.getModule(id);
        ApiResponse<Module> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Module retrieved successfully");
        response.setData(module);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/modules")
    public ResponseEntity<ApiResponse<List<Module>>> getAllModules() {
        List<Module> modules = moduleService.getAllModules();
        ApiResponse<List<Module>> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Modules retrieved successfully");
        response.setData(modules);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/modules")
    public ResponseEntity<ApiResponse<Module>> updateModule(@RequestBody Module module) {
        Module updatedModule = moduleService.updateModule(module);
        ApiResponse<Module> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Module updated successfully");
        response.setData(updatedModule);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/modules/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteModule(@PathVariable(name = "id") Long id) {
        moduleService.deleteModule(id);
        ApiResponse<Void> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Module deleted successfully");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}
