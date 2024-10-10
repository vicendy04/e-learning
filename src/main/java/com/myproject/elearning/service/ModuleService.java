package com.myproject.elearning.service;

import com.myproject.elearning.domain.Course;
import com.myproject.elearning.domain.Module;
import com.myproject.elearning.repository.CourseRepository;
import com.myproject.elearning.repository.ModuleRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

/**
 * Service class for managing modules.
 */
@Service
public class ModuleService {
    private final ModuleRepository moduleRepository;
    private final CourseRepository courseRepository;

    public ModuleService(ModuleRepository moduleRepository, CourseRepository courseRepository) {
        this.moduleRepository = moduleRepository;
        this.courseRepository = courseRepository;
    }

    /**
     * @param module The module which may or may not have an associated courseId. This parameter is optional.
     *               If the module does not have a courseId, it is considered a template for now.
     * @return {@link Module}
     */
    public Module createModule(Module module) {
        if (module.getCourse() != null && module.getCourse().getId() != null) {
            Course course =
                    courseRepository.findById(module.getCourse().getId()).orElseThrow();
            module.setCourse(course);
        }
        return moduleRepository.save(module);
    }

    public Module getModule(Long id) {
        return moduleRepository.findById(id).orElseThrow();
    }

    public Module updateModule(Module module) {
        Module currentModule = moduleRepository.findById(module.getId()).orElseThrow();
        if (module.getCourse() != null && module.getCourse().getId() != null) {
            Course course =
                    courseRepository.findById(module.getCourse().getId()).orElseThrow();
            module.setCourse(course);
        }
        currentModule.setTitle(module.getTitle());
        currentModule.setDescription(module.getDescription());
        currentModule.setOrder(module.getOrder());
        currentModule.setCourse(module.getCourse());
        return moduleRepository.save(currentModule);
    }

    public void deleteModule(Long id) {
        Module module = moduleRepository.findById(id).orElseThrow();
        moduleRepository.delete(module);
    }

    public List<Module> getAllModules() {
        return moduleRepository.findAll();
    }

    public List<Module> getModulesForCourse(Long courseId) {
        return moduleRepository.findByCourseId(courseId);
    }

    public List<Module> reorderModulesForCourse(Long courseId, List<Map<Long, Integer>> orderMapping) {
        List<Module> modules = getModulesForCourse(courseId);

        // version 1
        //        modules.forEach(module -> {
        //            orderMapping.forEach(map -> {
        //                Long moduleId = map.keySet().iterator().next();
        //                Integer newOrder = map.values().iterator().next();
        //                if (module.getId().equals(moduleId)) {
        //                    module.setOrder(newOrder);
        //                }
        //            });
        //        });

        // version 2
        Map<Long, Integer> orderLookupTable = new HashMap<>();
        orderMapping.forEach(orderLookupTable::putAll);
        modules.forEach(module -> {
            if (orderLookupTable.containsKey(module.getId())) {
                module.setOrder(orderLookupTable.get(module.getId()));
            }
        });

        return moduleRepository.saveAll(modules);
    }
}
