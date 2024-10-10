package com.myproject.elearning.web.rest;

import com.myproject.elearning.domain.Module;
import com.myproject.elearning.service.ModuleService;
import java.util.List;
import java.util.Map;
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
    public ResponseEntity<List<Module>> getModulesForCourse(@PathVariable Long courseId) {
        List<Module> modules = moduleService.getModulesForCourse(courseId);
        return ResponseEntity.status(HttpStatus.OK).body(modules);
    }

    @PostMapping("/modules/reorder")
    public ResponseEntity<List<Module>> reorderModules(
            @PathVariable Long courseId, @RequestBody List<Map<Long, Integer>> orderMapping) {
        List<Module> reorderedModules = moduleService.reorderModulesForCourse(courseId, orderMapping);
        return ResponseEntity.status(HttpStatus.OK).body(reorderedModules);
    }
}
