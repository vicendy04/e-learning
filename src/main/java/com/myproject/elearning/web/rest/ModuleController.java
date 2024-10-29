package com.myproject.elearning.web.rest;

import static com.myproject.elearning.web.rest.utils.ResponseUtils.wrapSuccessResponse;

import com.myproject.elearning.domain.Module;
import com.myproject.elearning.dto.response.ApiResponse;
import com.myproject.elearning.dto.response.PagedResponse;
import com.myproject.elearning.service.ModuleService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing modules.
 */
@RestController
@RequestMapping("/api/v1/modules")
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
    @PostMapping("")
    public ResponseEntity<ApiResponse<Module>> createModule(@Valid @RequestBody Module module) {
        Module newModule = moduleService.createModule(module);
        ApiResponse<Module> response = wrapSuccessResponse("Module created successfully", newModule);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Module>> getModule(@PathVariable(name = "id") Long id) {
        Module module = moduleService.getModule(id);
        ApiResponse<Module> response = wrapSuccessResponse("Module retrieved successfully", module);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse<PagedResponse<Module>>> getAllModules(
            @PageableDefault(size = 5, page = 0, sort = "title", direction = Sort.Direction.ASC) Pageable pageable) {
        PagedResponse<Module> modules = moduleService.getAllModules(pageable);
        ApiResponse<PagedResponse<Module>> response = wrapSuccessResponse("Modules retrieved successfully", modules);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("")
    public ResponseEntity<ApiResponse<Module>> updateModule(@RequestBody Module module) {
        Module updatedModule = moduleService.updateModule(module);
        ApiResponse<Module> response = wrapSuccessResponse("Module updated successfully", updatedModule);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteModule(@PathVariable(name = "id") Long id) {
        moduleService.deleteModule(id);
        ApiResponse<Void> response = wrapSuccessResponse("Module deleted successfully", null);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}
