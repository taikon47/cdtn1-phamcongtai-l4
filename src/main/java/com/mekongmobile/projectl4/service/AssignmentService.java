package com.mekongmobile.projectl4.service;

import com.mekongmobile.projectl4.entity.*;
import com.mekongmobile.projectl4.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AssignmentService {
    private final WarrantyTicketRepository tickets;
    private final TechnicianRepository technicians;
    private final AssignmentRepository assignments;

    public AssignmentService(WarrantyTicketRepository tickets, TechnicianRepository technicians,
                             AssignmentRepository assignments) {
        this.tickets = tickets; this.technicians = technicians; this.assignments = assignments;
    }

    @Transactional
    public Assignment assign(Long ticketId, Long technicianId) {
        if (technicianId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vui lòng chọn kỹ thuật viên.");
        WarrantyTicket ticket = tickets.findById(ticketId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy phiếu bảo hành."));
        Technician tech = technicians.findById(technicianId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy kỹ thuật viên."));
        if (assignments.existsByTicketId(ticketId) || !"UNASSIGNED".equalsIgnoreCase(ticket.getStatus()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Phiếu đã được phân công hoặc không còn ở trạng thái chờ phân công.");
        if (!"AVAILABLE".equalsIgnoreCase(tech.getAvailabilityStatus()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Kỹ thuật viên hiện không sẵn sàng.");
        if (ticket.getRequiredSkill() == null || ticket.getServiceArea() == null
                || !ticket.getRequiredSkill().equalsIgnoreCase(tech.getSkill())
                || !ticket.getServiceArea().equalsIgnoreCase(tech.getServiceArea()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Chuyên môn hoặc khu vực phục vụ không phù hợp.");

        Assignment assignment = new Assignment();
        assignment.setTicketId(ticketId);
        assignment.setTechnicianId(tech.getTechnicianId());
        ticket.setStatus("ASSIGNED");
        tickets.save(ticket);
        tech.setCurrentWorkload((tech.getCurrentWorkload() == null ? 0 : tech.getCurrentWorkload()) + 1);
        technicians.save(tech);
        return assignments.save(assignment);
    }
}
