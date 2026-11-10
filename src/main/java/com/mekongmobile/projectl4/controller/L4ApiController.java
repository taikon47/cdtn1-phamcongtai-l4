package com.mekongmobile.projectl4.controller;

import com.mekongmobile.projectl4.entity.*;
import com.mekongmobile.projectl4.repository.*;
import com.mekongmobile.projectl4.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.time.*;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class L4ApiController {
    private final WarrantyTicketService ticketService;
    private final TechnicianRepository technicians;
    private final AssignmentService assignmentService;
    private final AppointmentService appointmentService;

    @Autowired
    public L4ApiController(WarrantyTicketService ticketService, TechnicianRepository technicians,
                           AssignmentService assignmentService, AppointmentService appointmentService) {
        this.ticketService = ticketService; this.technicians = technicians;
        this.assignmentService = assignmentService; this.appointmentService = appointmentService;
    }

    // Constructor tương thích với các unit test cũ; Spring sử dụng constructor @Autowired phía trên.
    public L4ApiController(WarrantyTicketRepository tickets, TechnicianRepository technicians,
                           AssignmentRepository assignments, AppointmentRepository appointments) {
        this(new WarrantyTicketService(tickets), technicians,
             new AssignmentService(tickets, technicians, assignments),
             new AppointmentService(appointments, tickets, technicians));
    }

    public record AssignBody(Long technicianId) {}
    public record AppointmentBody(Long ticketId, Long technicianId, String appointmentType,
                                  LocalDate appointmentDate, LocalTime startTime, LocalTime endTime) {}

    @GetMapping("/warranty-tickets")
    public List<WarrantyTicket> tickets(@RequestParam(defaultValue = "UNASSIGNED") String status) {
        return ticketService.findByStatus(status);
    }
    @GetMapping("/technicians/{technicianId}/schedule")
    public List<Appointment> schedule(@PathVariable Long technicianId) { return appointmentService.schedule(technicianId); }
    @PostMapping("/warranty-tickets/{ticketId}/assignments")
    @ResponseStatus(HttpStatus.CREATED)
    public Assignment assign(@PathVariable Long ticketId, @RequestBody AssignBody body) {
        return assignmentService.assign(ticketId, body == null ? null : body.technicianId());
    }
    @PostMapping("/appointments")
    @ResponseStatus(HttpStatus.CREATED)
    public Appointment createAppointment(@RequestBody AppointmentBody b) {
        return appointmentService.create(b == null ? null : b.ticketId(), b == null ? null : b.technicianId(),
                b == null ? null : b.appointmentType(), b == null ? null : b.appointmentDate(),
                b == null ? null : b.startTime(), b == null ? null : b.endTime());
    }
    @PostMapping("/appointments/check-conflict")
    public Map<String,Object> checkConflict(@RequestBody AppointmentBody b) {
        boolean found = appointmentService.checkConflict(b == null ? null : b.ticketId(), b == null ? null : b.technicianId(),
                b == null ? null : b.appointmentType(), b == null ? null : b.appointmentDate(),
                b == null ? null : b.startTime(), b == null ? null : b.endTime());
        return Map.of("conflict", found, "message", found ? "Kỹ thuật viên đã có lịch hẹn trong khoảng thời gian này." : "Không có lịch hẹn bị trùng.");
    }
    @PatchMapping("/appointments/{appointmentId}/confirm")
    public Appointment confirm(@PathVariable Long appointmentId) { return appointmentService.confirm(appointmentId); }
    @GetMapping("/appointments")
    public List<Appointment> getAppointments() { return appointmentService.findAll(); }
    @PatchMapping("/appointments/{appointmentId}/cancel")
    public Appointment cancelAppointment(@PathVariable Long appointmentId) { return appointmentService.cancel(appointmentId); }
}
