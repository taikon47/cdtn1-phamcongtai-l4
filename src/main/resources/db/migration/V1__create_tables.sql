CREATE TABLE technician (
    technician_id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(120) NOT NULL,
    skill VARCHAR(100) NOT NULL,
    service_area VARCHAR(100) NOT NULL,
    current_workload INTEGER NOT NULL DEFAULT 0,
    availability_status VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE warranty_ticket (
    ticket_id BIGSERIAL PRIMARY KEY,
    ticket_code VARCHAR(30) NOT NULL UNIQUE,
    customer_name VARCHAR(120) NOT NULL,
    device_name VARCHAR(120) NOT NULL,
    issue_description TEXT,
    required_skill VARCHAR(100) NOT NULL,
    service_area VARCHAR(100) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'UNASSIGNED',
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE assignment (
    assignment_id BIGSERIAL PRIMARY KEY,
    ticket_id BIGINT NOT NULL UNIQUE,
    technician_id BIGINT NOT NULL,
    assigned_at TIMESTAMP NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ASSIGNED'
);

CREATE TABLE appointment (
    appointment_id BIGSERIAL PRIMARY KEY,
    ticket_id BIGINT NOT NULL,
    technician_id BIGINT NOT NULL,
    appointment_type VARCHAR(20) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL
);
