package com.myproject.elearning.web.rest;

import static com.myproject.elearning.web.rest.utils.ResponseUtils.wrapSuccessResponse;

import com.myproject.elearning.domain.Module;
import com.myproject.elearning.service.ModuleService;
import com.myproject.elearning.service.dto.request.ModuleReorderRequest;
import com.myproject.elearning.service.dto.response.ApiResponse;
import com.myproject.elearning.service.dto.response.PagedResponse;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    public ResponseEntity<ApiResponse<PagedResponse<Module>>> getModulesByCourseId(
            @PathVariable Long courseId,
            @PageableDefault(size = 5, page = 0, sort = "title", direction = Sort.Direction.ASC) Pageable pageable) {
        PagedResponse<Module> modules = moduleService.getModulesByCourseId(courseId, pageable);
        ApiResponse<PagedResponse<Module>> response = wrapSuccessResponse("Modules retrieved successfully", modules);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/modules")
    public ResponseEntity<ApiResponse<Void>> deleteModulesOfCourse(@PathVariable Long courseId) {
        moduleService.deleteModulesOfCourse(courseId);
        ApiResponse<Void> response = wrapSuccessResponse("Modules deleted successfully", null);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }

    @PostMapping("/modules")
    public ResponseEntity<ApiResponse<Module>> addModuleToCourse(
            @PathVariable Long courseId, @RequestBody Module module) {
        Module createdModule = moduleService.addModuleToCourse(courseId, module);
        ApiResponse<Module> response = wrapSuccessResponse("Module added successfully", createdModule);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Reorders the modules of a specific course.
     *
     * @param courseId             The ID of the course whose modules will be reordered.
     * @param moduleReorderRequest The new order of modules.
     * @return The {@link ResponseEntity} with status {@code 200 (OK)} and the reordered modules wrapped in {@link ApiResponse}.
     */
    @PostMapping("/modules/reorder")
    public ResponseEntity<ApiResponse<List<Module>>> reorderModules(
            @PathVariable Long courseId, @RequestBody ModuleReorderRequest moduleReorderRequest) {
        List<Module> reorderedModules = moduleService.reorderModules(courseId, moduleReorderRequest.getOrderMapping());
        ApiResponse<List<Module>> response = wrapSuccessResponse("Modules reordered successfully", reorderedModules);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
