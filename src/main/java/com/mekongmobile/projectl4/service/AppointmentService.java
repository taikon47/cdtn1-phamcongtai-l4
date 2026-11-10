package com.mekongmobile.projectl4.service;

import com.mekongmobile.projectl4.entity.Appointment;
import com.mekongmobile.projectl4.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.*;
import java.util.*;

@Service
public class AppointmentService {
    private static final List<String> BLOCKING = List.of("PENDING", "CONFIRMED");
    private final AppointmentRepository appointments;
    private final WarrantyTicketRepository tickets;
    private final TechnicianRepository technicians;

    public AppointmentService(AppointmentRepository appointments, WarrantyTicketRepository tickets, TechnicianRepository technicians) {
        this.appointments = appointments; this.tickets = tickets; this.technicians = technicians;
    }
    public List<Appointment> schedule(Long technicianId) {
        if (!technicians.existsById(technicianId)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy kỹ thuật viên.");
        return appointments.findByTechnicianIdOrderByStartTimeAsc(technicianId);
    }
    public List<Appointment> findAll() { return appointments.findAllByOrderByStartTimeAsc(); }

    private record Validated(Long ticketId, Long technicianId, String type, LocalDateTime start, LocalDateTime end) {}
    private Validated validate(Long ticketId, Long technicianId, String type, LocalDate date, LocalTime start, LocalTime end) {
        if (ticketId == null || technicianId == null || type == null || date == null || start == null || end == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vui lòng nhập đầy đủ thông tin lịch hẹn.");
        String normalized = type.trim().toUpperCase(Locale.ROOT);
        if (!List.of("RECEIVE", "RETURN").contains(normalized)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Loại lịch hẹn phải là RECEIVE hoặc RETURN.");
        if (date.isBefore(LocalDate.now())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ngày hẹn không được nằm trong quá khứ.");
        if (!end.isAfter(start)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Giờ kết thúc phải sau giờ bắt đầu.");
        LocalDateTime startAt = date.atTime(start), endAt = date.atTime(end);
        if (startAt.isBefore(LocalDateTime.now())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Thời gian bắt đầu lịch hẹn không được nằm trong quá khứ.");
        if (!tickets.existsById(ticketId)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy phiếu bảo hành.");
        if (!technicians.existsById(technicianId)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy kỹ thuật viên.");
        return new Validated(ticketId, technicianId, normalized, startAt, endAt);
    }
    private boolean hasConflict(Validated v) {
        return appointments.existsByTechnicianIdAndStartTimeLessThanAndEndTimeGreaterThanAndStatusIn(v.technicianId(), v.end(), v.start(), BLOCKING);
    }
    public boolean checkConflict(Long ticketId, Long technicianId, String type, LocalDate date, LocalTime start, LocalTime end) {
        return hasConflict(validate(ticketId, technicianId, type, date, start, end));
    }
    @Transactional
    public Appointment create(Long ticketId, Long technicianId, String type, LocalDate date, LocalTime start, LocalTime end) {
        Validated v = validate(ticketId, technicianId, type, date, start, end);
        if (hasConflict(v)) throw new ResponseStatusException(HttpStatus.CONFLICT, "Kỹ thuật viên đã có lịch hẹn trong khoảng thời gian này.");
        Appointment a = new Appointment(); a.setTicketId(v.ticketId()); a.setTechnicianId(v.technicianId());
        a.setAppointmentType(v.type()); a.setStartTime(v.start()); a.setEndTime(v.end()); a.setStatus("PENDING");
        return appointments.save(a);
    }
    @Transactional
    public Appointment confirm(Long id) {
        Appointment a = appointments.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lịch hẹn."));
        if ("CONFIRMED".equalsIgnoreCase(a.getStatus())) return a;
        if (!"PENDING".equalsIgnoreCase(a.getStatus())) throw new ResponseStatusException(HttpStatus.CONFLICT, "Chỉ có thể xác nhận lịch hẹn đang chờ xác nhận.");
        boolean clash = appointments.existsByTechnicianIdAndAppointmentIdNotAndStartTimeLessThanAndEndTimeGreaterThanAndStatusIn(a.getTechnicianId(), id, a.getEndTime(), a.getStartTime(), BLOCKING);
        if (clash) throw new ResponseStatusException(HttpStatus.CONFLICT, "Kỹ thuật viên đã có lịch hẹn trong khoảng thời gian này.");
        a.setStatus("CONFIRMED"); return appointments.save(a);
    }
    @Transactional
    public Appointment cancel(Long id) {
        Appointment a = appointments.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lịch hẹn."));
        if ("CANCELLED".equalsIgnoreCase(a.getStatus())) return a;
        if ("COMPLETED".equalsIgnoreCase(a.getStatus())) throw new ResponseStatusException(HttpStatus.CONFLICT, "Không thể hủy lịch hẹn đã hoàn tất.");
        a.setStatus("CANCELLED"); return appointments.save(a);
    }
}
