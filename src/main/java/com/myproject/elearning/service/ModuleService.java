package com.myproject.elearning.service;

import com.myproject.elearning.domain.Course;
import com.myproject.elearning.domain.Module;
import com.myproject.elearning.repository.CourseRepository;
import com.myproject.elearning.repository.ModuleRepository;
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

    public List<Module> getModulesByCourseId(Long courseId) {
        return moduleRepository.findByCourseId(courseId);
    }

    public Module addModuleToCourse(Long courseId, Module module) {
        Course course = courseRepository.findById(courseId).orElseThrow();
        module.setCourse(course);
        return moduleRepository.save(module);
    }

    public List<Module> reorderModules(Long courseId, Map<Long, Integer> orderMapping) {
        Course course = courseRepository.findById(courseId).orElseThrow();
        List<Module> modules = course.getModules();
        modules.forEach(module -> {
            Integer newOrder = orderMapping.get(module.getId());
            if (newOrder != null) {
                module.setOrder(newOrder);
            }
        });
        return moduleRepository.saveAll(modules);
    }

    /**
     * This method should be used with caution.
     * When modules have relationships with multiple other entities.
     */
    public void deleteModulesInCourse1(Long courseId) {
        moduleRepository.deleteByCourseId(courseId);
    }

    public void deleteModulesOfCourse(Long courseId) {
        //        when orphanRemoval = true
        Course course = courseRepository.findById(courseId).orElseThrow();
        course.getModules().clear();
        courseRepository.save(course);
    }
}
