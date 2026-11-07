package com.mekongmobile.projectl4;

import com.mekongmobile.projectl4.controller.L4ApiController;
import com.mekongmobile.projectl4.entity.*;
import com.mekongmobile.projectl4.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class L4AssignmentBusinessTests {

    WarrantyTicketRepository tickets;
    TechnicianRepository technicians;
    AssignmentRepository assignments;
    AppointmentRepository appointments;
    L4ApiController controller;

    WarrantyTicket ticket;
    Technician technician;

    @BeforeEach
    void setup() {
        tickets = mock(WarrantyTicketRepository.class);
        technicians = mock(TechnicianRepository.class);
        assignments = mock(AssignmentRepository.class);
        appointments = mock(AppointmentRepository.class);

        controller = new L4ApiController(
                tickets, technicians, assignments, appointments);

        ticket = mock(WarrantyTicket.class);

        technician = new Technician();
        technician.setTechnicianId(2L);
        technician.setFullName("Nguyen Van A");
        technician.setSkill("Hardware");
        technician.setServiceArea("Can Tho");
        technician.setAvailabilityStatus("AVAILABLE");
        technician.setCurrentWorkload(0);

        when(ticket.getStatus()).thenReturn("UNASSIGNED");
        when(ticket.getRequiredSkill()).thenReturn("Hardware");
        when(ticket.getServiceArea()).thenReturn("Can Tho");

        when(tickets.findById(1L))
                .thenReturn(Optional.of(ticket));

        when(technicians.findById(2L))
                .thenReturn(Optional.of(technician));

        when(assignments.existsByTicketId(1L))
                .thenReturn(false);

        when(assignments.save(any(Assignment.class)))
                .thenAnswer(i -> i.getArgument(0));
    }

    // TC01: Phan cong thanh cong
    @Test
    void assign_validTechnician_success() {
        Assignment result = controller.assign(
                1L, new L4ApiController.AssignBody(2L));

        assertNotNull(result);
        assertEquals(1L, result.getTicketId());
        assertEquals(2L, result.getTechnicianId());

        assertEquals("ASSIGNED", result.getStatus());
        assertEquals(1, technician.getCurrentWorkload());

        verify(ticket).setStatus("ASSIGNED");
        verify(tickets).save(ticket);
        verify(technicians).save(technician);
        verify(assignments).save(any(Assignment.class));
    }

    // TC02: Khong co technician ID
    @Test
    void assign_missingTechnicianId_badRequest() {
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> controller.assign(
                        1L, new L4ApiController.AssignBody(null)));

        assertEquals(400, ex.getStatusCode().value());
    }

    // TC03: Ticket khong ton tai
    @Test
    void assign_ticketNotFound_notFound() {
        when(tickets.findById(99L))
                .thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> controller.assign(
                        99L, new L4ApiController.AssignBody(2L)));

        assertEquals(404, ex.getStatusCode().value());
    }

    // TC04: Technician khong ton tai
    @Test
    void assign_technicianNotFound_notFound() {
        when(technicians.findById(99L))
                .thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> controller.assign(
                        1L, new L4ApiController.AssignBody(99L)));

        assertEquals(404, ex.getStatusCode().value());
    }

    // TC05: Ticket da duoc phan cong
    @Test
    void assign_ticketAlreadyAssigned_conflict() {
        when(assignments.existsByTicketId(1L))
                .thenReturn(true);

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> controller.assign(
                        1L, new L4ApiController.AssignBody(2L)));

        assertEquals(409, ex.getStatusCode().value());

        verify(assignments, never()).save(any());
    }

    // TC06: Technician khong san sang
    @Test
    void assign_technicianUnavailable_conflict() {
        technician.setAvailabilityStatus("BUSY");

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> controller.assign(
                        1L, new L4ApiController.AssignBody(2L)));

        assertEquals(409, ex.getStatusCode().value());
        assertEquals(0, technician.getCurrentWorkload());
    }

    // TC07: Khong phu hop chuyen mon
    @Test
    void assign_skillMismatch_conflict() {
        technician.setSkill("Software");

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> controller.assign(
                        1L, new L4ApiController.AssignBody(2L)));

        assertEquals(409, ex.getStatusCode().value());
    }

    // TC08: Khong phu hop khu vuc
    @Test
    void assign_serviceAreaMismatch_conflict() {
        technician.setServiceArea("Ho Chi Minh");

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> controller.assign(
                        1L, new L4ApiController.AssignBody(2L)));

        assertEquals(409, ex.getStatusCode().value());
    }
}
