package com.mekongmobile.projectl4;

import com.mekongmobile.projectl4.entity.Technician;
import com.mekongmobile.projectl4.repository.TechnicianRepository;
import com.mekongmobile.projectl4.service.TechnicianService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TechnicianServiceTests {

    private TechnicianRepository repository;
    private TechnicianService service;

    @BeforeEach
    void setup() {
        repository = mock(TechnicianRepository.class);
        service = new TechnicianService(repository);

        when(repository.save(any(Technician.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    private Technician createTechnician() {
        Technician technician = new Technician();
        technician.setFullName("Nguyen Van A");
        technician.setSkill("Laptop");
        technician.setServiceArea("Quan 1");
        technician.setAvailabilityStatus("AVAILABLE");
        technician.setCurrentWorkload(0);
        return technician;
    }

    // TC01: GET all technicians
    @Test
    void getAllTechnicians_returnsList() {
        when(repository.findAll()).thenReturn(
                List.of(createTechnician(), createTechnician()));

        List<Technician> result = service.findAll();

        assertEquals(2, result.size());
        verify(repository).findAll();
    }

    // TC02: GET technician by existing ID
    @Test
    void getTechnicianById_returnsTechnician() {
        Technician technician = createTechnician();
        when(repository.findById(1L)).thenReturn(Optional.of(technician));

        Technician result = service.findById(1L);

        assertEquals("Nguyen Van A", result.getFullName());
        verify(repository).findById(1L);
    }

    // TC03: GET technician with nonexistent ID
    @Test
    void getTechnicianById_notFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> service.findById(999L));

        assertEquals(404, ex.getStatusCode().value());
    }

    // TC04: POST valid technician
    @Test
    void createTechnician_success() {
        Technician technician = createTechnician();
        technician.setTechnicianId(99L);

        Technician result = service.create(technician);

        assertNull(result.getTechnicianId());
        assertEquals("Nguyen Van A", result.getFullName());
        verify(repository).save(any(Technician.class));
    }

    // TC05: POST invalid technician
    @Test
    void createTechnician_missingRequiredFields() {
        Technician technician = new Technician();
        technician.setFullName("");
        technician.setSkill("Laptop");
        technician.setServiceArea("Quan 1");

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> service.create(technician));

        assertEquals(400, ex.getStatusCode().value());
        verify(repository, never()).save(any());
    }

    // TC06: PUT existing technician
    @Test
    void updateTechnician_success() {
        Technician existing = createTechnician();
        Technician input = createTechnician();

        input.setFullName("Tran Van B");
        input.setSkill("Mobile Hardware");
        input.setServiceArea("Quan 2");
        input.setAvailabilityStatus("BUSY");

        when(repository.findById(1L)).thenReturn(Optional.of(existing));

        Technician result = service.update(1L, input);

        assertEquals("Tran Van B", result.getFullName());
        assertEquals("Mobile Hardware", result.getSkill());
        assertEquals("Quan 2", result.getServiceArea());
        assertEquals("BUSY", result.getAvailabilityStatus());

        verify(repository).save(existing);
    }

    // TC07: PUT with invalid data
    @Test
    void updateTechnician_invalidFields() {
        Technician existing = createTechnician();
        Technician input = createTechnician();
        input.setFullName(" ");

        when(repository.findById(1L)).thenReturn(Optional.of(existing));

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> service.update(1L, input));

        assertEquals(400, ex.getStatusCode().value());
        verify(repository, never()).save(any());
    }

    // TC08: PUT nonexistent technician
    @Test
    void updateTechnician_notFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> service.update(999L, createTechnician()));

        assertEquals(404, ex.getStatusCode().value());
        verify(repository, never()).save(any());
    }

    // TC09: DELETE existing technician
    @Test
    void deleteTechnician_success() {
        Technician existing = createTechnician();
        when(repository.findById(1L)).thenReturn(Optional.of(existing));

        assertDoesNotThrow(() -> service.delete(1L));

        verify(repository).delete(existing);
    }

    // TC10: DELETE nonexistent technician
    @Test
    void deleteTechnician_notFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> service.delete(999L));

        assertEquals(404, ex.getStatusCode().value());
        verify(repository, never()).delete(any(Technician.class));
    }
}
