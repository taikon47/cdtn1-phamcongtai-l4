
package com.mekongmobile.projectl4.controller;

import com.mekongmobile.projectl4.entity.*;
import com.mekongmobile.projectl4.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1")
public class L4ApiController {

    private final WarrantyTicketRepository tickets;
    private final TechnicianRepository technicians;
    private final AssignmentRepository assignments;
    private final AppointmentRepository appointments;

    private static final List<String> BLOCKING =
            List.of("PENDING", "CONFIRMED");

    public L4ApiController(
            WarrantyTicketRepository t,
            TechnicianRepository tech,
            AssignmentRepository a,
            AppointmentRepository ap) {
        tickets = t;
        technicians = tech;
        assignments = a;
        appointments = ap;
    }

    public record AssignBody(Long technicianId) {}

    public record AppointmentBody(
            Long ticketId,
            Long technicianId,
            String appointmentType,
            LocalDate appointmentDate,
            LocalTime startTime,
            LocalTime endTime) {}

    // API1: GET /api/v1/warranty-tickets?status=UNASSIGNED
    @GetMapping("/warranty-tickets")
    public List<WarrantyTicket> tickets(
            @RequestParam(defaultValue = "UNASSIGNED") String status) {
        return tickets.findByStatusIgnoreCase(status);
    }

    // API3: GET /api/v1/technicians/{technicianId}/schedule
    @GetMapping("/technicians/{technicianId}/schedule")
    public List<Appointment> schedule(
            @PathVariable Long technicianId) {

        if (!technicians.existsById(technicianId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Không tìm thấy kỹ thuật viên.");
        }

        return appointments.findByTechnicianIdOrderByStartTimeAsc(
                technicianId);
    }

    // API4: POST /api/v1/warranty-tickets/{ticketId}/assignments
    @PostMapping("/warranty-tickets/{ticketId}/assignments")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public Assignment assign(
            @PathVariable Long ticketId,
            @RequestBody AssignBody body) {

        if (body == null || body.technicianId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Vui lòng chọn kỹ thuật viên.");
        }

        WarrantyTicket ticket = tickets.findById(ticketId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy phiếu bảo hành."));

        Technician tech = technicians.findById(body.technicianId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy kỹ thuật viên."));

        if (assignments.existsByTicketId(ticketId)
                || !"UNASSIGNED".equalsIgnoreCase(ticket.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Phiếu đã được phân công hoặc không còn ở trạng thái chờ phân công.");
        }

        if (!"AVAILABLE".equalsIgnoreCase(
                tech.getAvailabilityStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Kỹ thuật viên hiện không sẵn sàng.");
        }

        if (!ticket.getRequiredSkill().equalsIgnoreCase(tech.getSkill())
                || !ticket.getServiceArea().equalsIgnoreCase(
                        tech.getServiceArea())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Chuyên môn hoặc khu vực phục vụ không phù hợp.");
        }

        Assignment assignment = new Assignment();
        assignment.setTicketId(ticketId);
        assignment.setTechnicianId(tech.getTechnicianId());

        ticket.setStatus("ASSIGNED");
        tickets.save(ticket);

        tech.setCurrentWorkload(
                (tech.getCurrentWorkload() == null
                        ? 0 : tech.getCurrentWorkload()) + 1);
        technicians.save(tech);

        return assignments.save(assignment);
    }

    private AppointmentBody validate(AppointmentBody b) {
        if (b == null
                || b.ticketId() == null
                || b.technicianId() == null
                || b.appointmentType() == null
                || b.appointmentDate() == null
                || b.startTime() == null
                || b.endTime() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Vui lòng nhập đầy đủ thông tin lịch hẹn.");
        }

        String type = b.appointmentType()
                .trim().toUpperCase(Locale.ROOT);

        if (!List.of("RECEIVE", "RETURN").contains(type)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Loại lịch hẹn phải là RECEIVE hoặc RETURN.");
        }

        if (b.appointmentDate().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Ngày hẹn không được nằm trong quá khứ.");
        }

        if (!b.endTime().isAfter(b.startTime())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Giờ kết thúc phải sau giờ bắt đầu.");
        }

        if (!tickets.existsById(b.ticketId())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Không tìm thấy phiếu bảo hành.");
        }

        if (!technicians.existsById(b.technicianId())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Không tìm thấy kỹ thuật viên.");
        }

        return new AppointmentBody(
                b.ticketId(),
                b.technicianId(),
                type,
                b.appointmentDate(),
                b.startTime(),
                b.endTime());
    }

    private boolean conflict(AppointmentBody b) {
        LocalDateTime start =
                b.appointmentDate().atTime(b.startTime());
        LocalDateTime end =
                b.appointmentDate().atTime(b.endTime());

        return appointments
                .existsByTechnicianIdAndStartTimeLessThanAndEndTimeGreaterThanAndStatusIn(
                        b.technicianId(), end, start, BLOCKING);
    }

    // API5: POST /api/v1/appointments
    @PostMapping("/appointments")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public Appointment createAppointment(
            @RequestBody AppointmentBody input) {

        AppointmentBody b = validate(input);

        if (conflict(b)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Kỹ thuật viên đã có lịch hẹn trong khoảng thời gian này.");
        }

        Appointment a = new Appointment();
        a.setTicketId(b.ticketId());
        a.setTechnicianId(b.technicianId());
        a.setAppointmentType(b.appointmentType());
        a.setStartTime(b.appointmentDate().atTime(b.startTime()));
        a.setEndTime(b.appointmentDate().atTime(b.endTime()));
        a.setStatus("PENDING");

        return appointments.save(a);
    }

    // API6: POST /api/v1/appointments/check-conflict
    @PostMapping("/appointments/check-conflict")
    public Map<String, Object> checkConflict(
            @RequestBody AppointmentBody input) {

        AppointmentBody b = validate(input);
        boolean found = conflict(b);

        return Map.of(
                "conflict", found,
                "message", found
                        ? "Kỹ thuật viên đã có lịch hẹn trong khoảng thời gian này."
                        : "Không có lịch hẹn bị trùng.");
    }

    // API7: PATCH /api/v1/appointments/{appointmentId}/confirm
    @PatchMapping("/appointments/{appointmentId}/confirm")
    @Transactional
    public Appointment confirm(
            @PathVariable Long appointmentId) {

        Appointment a = appointments.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy lịch hẹn."));

        if ("CONFIRMED".equalsIgnoreCase(a.getStatus())) {
            return a;
        }

        if (!"PENDING".equalsIgnoreCase(a.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Chỉ có thể xác nhận lịch hẹn đang chờ xác nhận.");
        }

        boolean clash = appointments
                .existsByTechnicianIdAndAppointmentIdNotAndStartTimeLessThanAndEndTimeGreaterThanAndStatusIn(
                        a.getTechnicianId(),
                        appointmentId,
                        a.getEndTime(),
                        a.getStartTime(),
                        BLOCKING);

        if (clash) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Kỹ thuật viên đã có lịch hẹn trong khoảng thời gian này.");
        }

        a.setStatus("CONFIRMED");
        return appointments.save(a);
    }

    // API8: GET /api/v1/appointments
    @GetMapping("/appointments")
    public List<Appointment> getAppointments() {
        return appointments.findAllByOrderByStartTimeAsc();
    }

    // API9: PATCH /api/v1/appointments/{appointmentId}/cancel
    @PatchMapping("/appointments/{appointmentId}/cancel")
    @Transactional
    public Appointment cancelAppointment(
            @PathVariable Long appointmentId) {

        Appointment a = appointments.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy lịch hẹn."));

        if ("CANCELLED".equalsIgnoreCase(a.getStatus())) {
            return a;
        }

        if ("COMPLETED".equalsIgnoreCase(a.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Không thể hủy lịch hẹn đã hoàn tất.");
        }

        a.setStatus("CANCELLED");
        return appointments.save(a);
    }
}
