package com.myproject.elearning.web.rest;

import com.myproject.elearning.domain.Module;
import com.myproject.elearning.service.ModuleService;
import com.myproject.elearning.service.dto.ApiResponse;
import com.myproject.elearning.service.dto.ModuleReorderDTO;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing modules within courses
 */
@RestController
@RequestMapping("/api/v1/courses/{courseId}/")
public class CourseModuleController {
    private final ModuleService moduleService;

    public CourseModuleController(ModuleService moduleService) {
        this.moduleService = moduleService;
    }

    @GetMapping("/modules")
    public ResponseEntity<ApiResponse<List<Module>>> getModulesByCourseId(@PathVariable Long courseId) {
        List<Module> modules = moduleService.getModulesByCourseId(courseId);
        ApiResponse<List<Module>> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Modules retrieved successfully");
        response.setData(modules);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/modules")
    public ResponseEntity<ApiResponse<Void>> deleteModulesOfCourse(@PathVariable Long courseId) {
        moduleService.deleteModulesOfCourse(courseId);
        ApiResponse<Void> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Modules deleted successfully");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }

    @PostMapping("/modules")
    public ResponseEntity<ApiResponse<Module>> addModuleToCourse(
            @PathVariable Long courseId, @RequestBody Module module) {
        Module createdModule = moduleService.addModuleToCourse(courseId, module);
        ApiResponse<Module> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Module added successfully");
        response.setData(createdModule);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Reorders the modules of a specific course.
     *
     * @param courseId   The ID of the course whose modules will be reordered.
     * @param reorderDTO The new order of modules.
     * @return The {@link ResponseEntity} with status {@code 200 (OK)} and the reordered modules wrapped in {@link ApiResponse}.
     */
    @PostMapping("/modules/reorder")
    public ResponseEntity<ApiResponse<List<Module>>> reorderModules(
            @PathVariable Long courseId, @RequestBody ModuleReorderDTO reorderDTO) {
        List<Module> reorderedModules = moduleService.reorderModules(courseId, reorderDTO.getOrderMapping());
        ApiResponse<List<Module>> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Modules reordered successfully");
        response.setData(reorderedModules);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
