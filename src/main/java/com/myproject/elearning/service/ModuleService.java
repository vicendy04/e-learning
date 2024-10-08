package com.myproject.elearning.service;

import com.myproject.elearning.domain.Module;
import com.myproject.elearning.repository.ModuleRepository;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Service class for managing modules.
 */
@Service
public class ModuleService {
    private final ModuleRepository moduleRepository;

    public ModuleService(ModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }

    public Module createModule(Module module) {
        return moduleRepository.save(module);
    }

    public Module getModule(Long id) {
        return moduleRepository.findById(id).orElseThrow();
    }

    public Module updateModule(Module module) {
        Module currentModule = moduleRepository.findById(module.getId()).orElseThrow();
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
}
