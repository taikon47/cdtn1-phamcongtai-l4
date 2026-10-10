-- L4 - Phan cong ky thuat vien & lich hen
-- PostgreSQL DDL
-- Bám ERD/SRS: WARRANTY_TICKET, TECHNICIAN, ASSIGNMENT, APPOINTMENT

CREATE TABLE warranty_ticket (
    ticket_id BIGSERIAL PRIMARY KEY,
    ticket_code VARCHAR(30) NOT NULL UNIQUE,
    customer_name VARCHAR(120) NOT NULL,
    device_name VARCHAR(120) NOT NULL,
    issue_description TEXT,
    required_skill VARCHAR(100) NOT NULL,
    service_area VARCHAR(100) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'WAITING_ASSIGNMENT',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE technician (
    technician_id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(120) NOT NULL,
    skill VARCHAR(100) NOT NULL,
    service_area VARCHAR(100) NOT NULL,
    current_workload INTEGER NOT NULL DEFAULT 0 CHECK (current_workload >= 0),
    availability_status VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE assignment (
    assignment_id BIGSERIAL PRIMARY KEY,
    ticket_id BIGINT NOT NULL UNIQUE,
    technician_id BIGINT NOT NULL,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(30) NOT NULL DEFAULT 'ASSIGNED',
    CONSTRAINT fk_assignment_ticket
        FOREIGN KEY (ticket_id) REFERENCES warranty_ticket(ticket_id),
    CONSTRAINT fk_assignment_technician
        FOREIGN KEY (technician_id) REFERENCES technician(technician_id)
);

CREATE TABLE appointment (
    appointment_id BIGSERIAL PRIMARY KEY,
    ticket_id BIGINT NOT NULL,
    technician_id BIGINT NOT NULL,
    appointment_type VARCHAR(20) NOT NULL
        CHECK (appointment_type IN ('DELIVERY', 'RECEIPT')),
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_appointment_time CHECK (end_time > start_time),
    CONSTRAINT fk_appointment_ticket
        FOREIGN KEY (ticket_id) REFERENCES warranty_ticket(ticket_id),
    CONSTRAINT fk_appointment_technician
        FOREIGN KEY (technician_id) REFERENCES technician(technician_id)
);

-- Hỗ trợ NFR1: tra cứu danh sách/lịch theo các trường thường dùng.
CREATE INDEX idx_ticket_status
    ON warranty_ticket(status);

CREATE INDEX idx_technician_skill_area
    ON technician(skill, service_area);

CREATE INDEX idx_appointment_technician_time
    ON appointment(technician_id, start_time, end_time);

CREATE INDEX idx_appointment_ticket
    ON appointment(ticket_id);

-- BR4/NFR2:
-- Kiểm tra chồng lấn lịch của cùng technician_id phải được thực hiện
-- trong AppointmentService trước khi xác nhận/lưu, trong cùng transaction.
-- Điều kiện overlap:
-- new_start < existing_end AND new_end > existing_start.
