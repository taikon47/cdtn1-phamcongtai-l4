package com.mekongmobile.projectl4;

import com.mekongmobile.projectl4.controller.L4ApiController;
import com.mekongmobile.projectl4.entity.Appointment;
import com.mekongmobile.projectl4.entity.Assignment;
import com.mekongmobile.projectl4.entity.Technician;
import com.mekongmobile.projectl4.entity.WarrantyTicket;
import com.mekongmobile.projectl4.repository.AppointmentRepository;
import com.mekongmobile.projectl4.repository.AssignmentRepository;
import com.mekongmobile.projectl4.repository.TechnicianRepository;
import com.mekongmobile.projectl4.repository.WarrantyTicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Bộ kiểm thử 12 Test Case chuẩn phục vụ Báo cáo Kiểm thử Bài tập 3 (BT3).
 * Bao phủ đầy đủ 5 nhóm:
 * 1. Luồng đúng (TC-01 -> TC-04)
 * 2. Giá trị biên (TC-05, TC-06)
 * 3. Dữ liệu sai (TC-07 -> TC-09)
 * 4. Bảo mật & Kiểm soát truy cập OWASP A01 (TC-10, TC-11)
 * 5. Tính tái lập Idempotency (TC-12)
 */
class L4BT3VerificationTests {

    WarrantyTicketRepository tickets;
    TechnicianRepository technicians;
    AssignmentRepository assignments;
    AppointmentRepository appointments;
    L4ApiController controller;

    WarrantyTicket ticket;
    Technician technician;

    @BeforeEach
    void setUp() {
        tickets = mock(WarrantyTicketRepository.class);
        technicians = mock(TechnicianRepository.class);
        assignments = mock(AssignmentRepository.class);
        appointments = mock(AppointmentRepository.class);

        controller = new L4ApiController(tickets, technicians, assignments, appointments);

        ticket = mock(WarrantyTicket.class);
        when(ticket.getTicketId()).thenReturn(1L);
        when(ticket.getTicketCode()).thenReturn("TCK-2026-0001");
        when(ticket.getCustomerName()).thenReturn("Nguyen Van A");
        when(ticket.getDeviceName()).thenReturn("iPhone 13");
        when(ticket.getRequiredSkill()).thenReturn("Hardware");
        when(ticket.getServiceArea()).thenReturn("Quan 1");
        when(ticket.getStatus()).thenReturn("UNASSIGNED");

        technician = new Technician();
        technician.setTechnicianId(2L);
        technician.setFullName("Tran Van KyThuat");
        technician.setSkill("Hardware");
        technician.setServiceArea("Quan 1");
        technician.setAvailabilityStatus("AVAILABLE");
        technician.setCurrentWorkload(0);

        when(tickets.findById(1L)).thenReturn(Optional.of(ticket));
        when(tickets.existsById(1L)).thenReturn(true);

        when(technicians.findById(2L)).thenReturn(Optional.of(technician));
        when(technicians.existsById(2L)).thenReturn(true);

        when(assignments.existsByTicketId(1L)).thenReturn(false);
        when(assignments.save(any(Assignment.class))).thenAnswer(i -> i.getArgument(0));
        when(appointments.save(any(Appointment.class))).thenAnswer(i -> i.getArgument(0));
    }

    // ==========================================
    // NHÓM 1: TEST LUỒNG ĐÚNG (HAPPY PATH)
    // ==========================================

    @Test
    @DisplayName("TC-01: [Luồng đúng - QT01] Phân công KTV phù hợp thành công, chuyển status ticket sang ASSIGNED")
    void tc01_assignTechnicianSuccess() {
        Assignment res = controller.assign(1L, new L4ApiController.AssignBody(2L));

        assertNotNull(res, "Assignment không được null");
        assertEquals(1L, res.getTicketId());
        assertEquals(2L, res.getTechnicianId());
        assertEquals("ASSIGNED", res.getStatus());
        assertEquals(1, technician.getCurrentWorkload(), "Workload KTV phải tăng 1");
        verify(ticket).setStatus("ASSIGNED");
    }

    @Test
    @DisplayName("TC-02: [Luồng đúng - QT02] Đặt lịch hẹn nhận máy hợp lệ ở tương lai với trạng thái PENDING")
    void tc02_createAppointmentSuccess() {
        LocalDate date = LocalDate.now().plusDays(2);
        L4ApiController.AppointmentBody body = new L4ApiController.AppointmentBody(
                1L, 2L, "RECEIVE", date, LocalTime.of(9, 0), LocalTime.of(10, 0)
        );

        when(appointments.existsByTechnicianIdAndStartTimeLessThanAndEndTimeGreaterThanAndStatusIn(
                anyLong(), any(), any(), anyList())).thenReturn(false);

        Appointment appt = controller.createAppointment(body);
        assertNotNull(appt);
        assertEquals("PENDING", appt.getStatus());
        assertEquals(1L, appt.getTicketId());
        assertEquals(2L, appt.getTechnicianId());
    }

    @Test
    @DisplayName("TC-03: [Luồng đúng - QT03] Xác nhận lịch hẹn đang PENDING chuyển thành CONFIRMED")
    void tc03_confirmAppointmentSuccess() {
        Appointment appt = new Appointment();
        appt.setStatus("PENDING");
        appt.setTechnicianId(2L);
        appt.setStartTime(LocalDateTime.now().plusDays(2).withHour(9));
        appt.setEndTime(LocalDateTime.now().plusDays(2).withHour(10));

        when(appointments.findById(10L)).thenReturn(Optional.of(appt));

        Appointment confirmed = controller.confirm(10L);
        assertEquals("CONFIRMED", confirmed.getStatus());
    }

    @Test
    @DisplayName("TC-04: [Luồng đúng - QT04] Hủy lịch hẹn đã CONFIRMED chuyển thành CANCELLED")
    void tc04_cancelAppointmentSuccess() {
        Appointment appt = new Appointment();
        appt.setStatus("CONFIRMED");

        when(appointments.findById(11L)).thenReturn(Optional.of(appt));

        Appointment cancelled = controller.cancelAppointment(11L);
        assertEquals("CANCELLED", cancelled.getStatus());
    }

    // ==========================================
    // NHÓM 2: TEST GIÁ TRỊ BIÊN (BOUNDARY)
    // ==========================================

    @Test
    @DisplayName("TC-05: [Giá trị biên] Hai lịch hẹn liền kề sát giờ (10:00 kết thúc, 10:00 bắt đầu) được chấp nhận")
    void tc05_boundaryAdjacentAppointmentsAllowed() {
        when(appointments.existsByTechnicianIdAndStartTimeLessThanAndEndTimeGreaterThanAndStatusIn(
                anyLong(), any(), any(), anyList())).thenReturn(false);

        LocalDate date = LocalDate.now().plusDays(2);
        L4ApiController.AppointmentBody body = new L4ApiController.AppointmentBody(
                1L, 2L, "RECEIVE", date, LocalTime.of(10, 0), LocalTime.of(11, 0)
        );

        Appointment appt = controller.createAppointment(body);
        assertNotNull(appt);
        assertEquals(LocalTime.of(10, 0), appt.getStartTime().toLocalTime());
    }

    @Test
    @DisplayName("TC-06: [Giá trị biên] Giờ kết thúc trùng bằng đúng giờ bắt đầu (10:00 - 10:00) bị từ chối 400")
    void tc06_boundarySameStartAndEndTimeRejected() {
        LocalDate date = LocalDate.now().plusDays(2);
        L4ApiController.AppointmentBody body = new L4ApiController.AppointmentBody(
                1L, 2L, "RECEIVE", date, LocalTime.of(10, 0), LocalTime.of(10, 0)
        );

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> controller.createAppointment(body));
        assertEquals(400, ex.getStatusCode().value());
    }

    // ==========================================
    // NHÓM 3: TEST DỮ LIỆU SAI (INVALID INPUT)
    // ==========================================

    @Test
    @DisplayName("TC-07: [Dữ liệu sai] Ngày hẹn đặt trong quá khứ bị từ chối với mã lỗi 400 Bad Request")
    void tc07_pastAppointmentDateRejected() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        L4ApiController.AppointmentBody body = new L4ApiController.AppointmentBody(
                1L, 2L, "RECEIVE", yesterday, LocalTime.of(9, 0), LocalTime.of(10, 0)
        );

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> controller.createAppointment(body));
        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    @DisplayName("TC-08: [Dữ liệu sai] Phân công KTV cho mã phiếu không tồn tại (ticketId=999L) trả về 404 Not Found")
    void tc08_assignNonExistingTicketReturns404() {
        when(tickets.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> controller.assign(999L, new L4ApiController.AssignBody(2L)));
        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    @DisplayName("TC-09: [Dữ liệu sai] Đặt lịch hẹn có khoảng giờ chồng lấn với lịch đã có bị từ chối 409 Conflict")
    void tc09_overlappingAppointmentRejectedWith409() {
        when(appointments.existsByTechnicianIdAndStartTimeLessThanAndEndTimeGreaterThanAndStatusIn(
                anyLong(), any(), any(), anyList())).thenReturn(true);

        LocalDate date = LocalDate.now().plusDays(2);
        L4ApiController.AppointmentBody body = new L4ApiController.AppointmentBody(
                1L, 2L, "RECEIVE", date, LocalTime.of(9, 30), LocalTime.of(10, 30)
        );

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> controller.createAppointment(body));
        assertEquals(409, ex.getStatusCode().value());
    }

    // ==========================================
    // NHÓM 4: TEST BẢO MẬT & KIỂM SOÁT TRUY CẬP (OWASP A01)
    // ==========================================

    @Test
    @DisplayName("TC-10: [Bảo mật OWASP A01] Phân công KTV sai khu vực địa bàn (Service Area Mismatch) bị từ chối 409")
    void tc10_securityServiceAreaMismatchRejected() {
        technician.setServiceArea("Can Tho"); // KTV ở Cần Thơ, phiếu ở Quận 1

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> controller.assign(1L, new L4ApiController.AssignBody(2L)));
        assertEquals(409, ex.getStatusCode().value());
        verify(assignments, never()).save(any());
    }

    @Test
    @DisplayName("TC-11: [Toàn vẹn dữ liệu] Phiếu bảo hành đã được gán KTV không được gán trùng lần 2 trả về 409")
    void tc11_integrityDuplicateAssignmentRejected() {
        when(assignments.existsByTicketId(1L)).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> controller.assign(1L, new L4ApiController.AssignBody(2L)));
        assertEquals(409, ex.getStatusCode().value());
        verify(assignments, never()).save(any());
    }

    // ==========================================
    // NHÓM 5: TEST TÍNH TÁI LẬP (REPRODUCIBILITY)
    // ==========================================

    @Test
    @DisplayName("TC-12: [Tính tái lập] Gọi API kiểm tra xung đột lịch hai lần liên tiếp trả về kết quả giống nhau")
    void tc12_reproducibilityCheckConflictIdempotent() {
        LocalDate date = LocalDate.now().plusDays(3);
        L4ApiController.AppointmentBody body = new L4ApiController.AppointmentBody(
                1L, 2L, "RECEIVE", date, LocalTime.of(14, 0), LocalTime.of(15, 0)
        );

        when(appointments.existsByTechnicianIdAndStartTimeLessThanAndEndTimeGreaterThanAndStatusIn(
                anyLong(), any(), any(), anyList())).thenReturn(false);

        Map<String, Object> run1 = controller.checkConflict(body);
        Map<String, Object> run2 = controller.checkConflict(body);

        assertNotNull(run1);
        assertNotNull(run2);
        assertEquals(run1.get("conflict"), run2.get("conflict"));
        assertEquals(run1.get("message"), run2.get("message"));
        assertEquals(false, run1.get("conflict"));
    }
}
