package com.mekongmobile.projectl4.controller;

import com.mekongmobile.projectl4.entity.Technician;
import com.mekongmobile.projectl4.service.TechnicianService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/technicians")
public class TechnicianController {
    private final TechnicianService service;

    public TechnicianController(TechnicianService service) {
        this.service = service;
    }

    @GetMapping
    public List<Technician> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Technician getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Technician create(@RequestBody Technician technician) {
        return service.create(technician);
    }

    @PutMapping("/{id}")
    public Technician update(@PathVariable Long id,
                             @RequestBody Technician technician) {
        return service.update(id, technician);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
