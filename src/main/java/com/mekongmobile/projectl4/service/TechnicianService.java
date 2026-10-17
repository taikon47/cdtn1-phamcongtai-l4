package com.mekongmobile.projectl4.service;

import com.mekongmobile.projectl4.entity.Technician;
import com.mekongmobile.projectl4.repository.TechnicianRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.List;

@Service
public class TechnicianService {
    private final TechnicianRepository repository;

    public TechnicianService(TechnicianRepository repository) {
        this.repository = repository;
    }

    public List<Technician> findAll() {
        return repository.findAll();
    }

    public Technician findById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Technician not found"));
    }

    public Technician create(Technician technician) {
        technician.setTechnicianId(null);
        validate(technician);
        return repository.save(technician);
    }

    public Technician update(Long id, Technician input) {
        Technician existing = findById(id);
        validate(input);
        existing.setFullName(input.getFullName());
        existing.setSkill(input.getSkill());
        existing.setServiceArea(input.getServiceArea());
        existing.setAvailabilityStatus(input.getAvailabilityStatus());
        return repository.save(existing);
    }

    public void delete(Long id) {
        Technician existing = findById(id);
        repository.delete(existing);
    }

    private void validate(Technician t) {
        if (t.getFullName() == null || t.getFullName().isBlank()
            || t.getSkill() == null || t.getSkill().isBlank()
            || t.getServiceArea() == null || t.getServiceArea().isBlank()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Missing required technician fields");
        }
    }
}