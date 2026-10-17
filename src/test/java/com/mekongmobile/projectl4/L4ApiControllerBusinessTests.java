package com.mekongmobile.projectl4;

import com.mekongmobile.projectl4.controller.L4ApiController;
import com.mekongmobile.projectl4.entity.Appointment;
import com.mekongmobile.projectl4.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.time.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** Isolated business-rule tests; do not require PostgreSQL. */
class L4ApiControllerBusinessTests {
    WarrantyTicketRepository tickets;
    TechnicianRepository technicians;
    AssignmentRepository assignments;
    AppointmentRepository appointments;
    L4ApiController controller;

    @BeforeEach void setup() {
        tickets = mock(WarrantyTicketRepository.class);
        technicians = mock(TechnicianRepository.class);
        assignments = mock(AssignmentRepository.class);
        appointments = mock(AppointmentRepository.class);
        controller = new L4ApiController(tickets, technicians, assignments, appointments);
        when(tickets.existsById(1L)).thenReturn(true);
        when(technicians.existsById(2L)).thenReturn(true);
        when(appointments.save(any(Appointment.class))).thenAnswer(i -> i.getArgument(0));
    }

    private L4ApiController.AppointmentBody body(LocalTime start, LocalTime end) {
        return new L4ApiController.AppointmentBody(1L, 2L, "RECEIVE", LocalDate.now().plusDays(2), start, end);
    }

    @Test void happyCase_createsPendingAppointment() {
        Appointment a = controller.createAppointment(body(LocalTime.of(9,0), LocalTime.of(10,0)));
        assertEquals("PENDING", a.getStatus());
        assertEquals(1L, a.getTicketId());
        assertEquals(2L, a.getTechnicianId());
        verify(appointments).save(any(Appointment.class));
    }

    @Test void boundaryCase_adjacentSlotIsAccepted() {
        // A previous appointment ends exactly when the new one starts; the repository reports no overlap.
        when(appointments.existsByTechnicianIdAndStartTimeLessThanAndEndTimeGreaterThanAndStatusIn(anyLong(), any(), any(), anyList())).thenReturn(false);
        Appointment a = controller.createAppointment(body(LocalTime.of(10,0), LocalTime.of(11,0)));
        assertNotNull(a);
    }

    @Test void invalidCase_overlappingSlotIsRejected() {
        when(appointments.existsByTechnicianIdAndStartTimeLessThanAndEndTimeGreaterThanAndStatusIn(anyLong(), any(), any(), anyList())).thenReturn(true);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> controller.createAppointment(body(LocalTime.of(9,30), LocalTime.of(10,30))));
        assertEquals(409, ex.getStatusCode().value());
        verify(appointments, never()).save(any());
    }

    @Test void invalidCase_endMustBeAfterStart() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> controller.createAppointment(body(LocalTime.of(11,0), LocalTime.of(11,0))));
        assertEquals(400, ex.getStatusCode().value());
    }

    @Test void invalidCase_pastDateIsRejected() {
        var b = new L4ApiController.AppointmentBody(1L,2L,"RETURN",LocalDate.now().minusDays(1),LocalTime.of(9,0),LocalTime.of(10,0));
        assertEquals(400, assertThrows(ResponseStatusException.class, () -> controller.createAppointment(b)).getStatusCode().value());
    }

    @Test void cancelConfirmedAppointment_changesStatus() {
        Appointment a = new Appointment(); a.setStatus("CONFIRMED");
        when(appointments.findById(3L)).thenReturn(Optional.of(a));
        assertEquals("CANCELLED", controller.cancelAppointment(3L).getStatus());
    }

    @Test void cancelCompletedAppointment_isRejected() {
        Appointment a = new Appointment(); a.setStatus("COMPLETED");
        when(appointments.findById(3L)).thenReturn(Optional.of(a));
        assertEquals(409, assertThrows(ResponseStatusException.class,
                () -> controller.cancelAppointment(3L)).getStatusCode().value());
    }

    @Test void confirmPendingAppointment_changesStatus() {
        Appointment a = new Appointment(); a.setStatus("PENDING"); a.setTechnicianId(2L);
        a.setStartTime(LocalDateTime.now().plusDays(2)); a.setEndTime(LocalDateTime.now().plusDays(2).plusHours(1));
        when(appointments.findById(3L)).thenReturn(Optional.of(a));
        assertEquals("CONFIRMED", controller.confirm(3L).getStatus());
    }
}
