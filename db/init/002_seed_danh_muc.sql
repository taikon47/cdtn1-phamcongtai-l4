-- =========================================================================
-- BÀI TẬP 3 - ĐÓNG GÓI PROTOTYPE - SMART CRM MEKONG MOBILE (LUỒNG L4)
-- Script 002: Nạp dữ liệu mẫu tự động (Idempotent - không nhân đôi dữ liệu khi chạy lại)
-- Thỏa mãn tiêu chí kiểm chứng C8: Chạy lại lần 2 không lỗi, không trùng lặp
-- =========================================================================

-- 1. Dữ liệu Kỹ thuật viên (6 kỹ thuật viên đa dạng tay nghề và khu vực)
INSERT INTO technician (technician_id, full_name, skill, service_area, current_workload, availability_status, created_at)
VALUES
  (1, 'Nguyễn Văn An', 'Hardware', 'Quan 1', 1, 'AVAILABLE', CURRENT_TIMESTAMP),
  (2, 'Trần Thị Bích', 'Software', 'Quan 1', 0, 'AVAILABLE', CURRENT_TIMESTAMP),
  (3, 'Lê Hoàng Cường', 'Hardware', 'Binh Thanh', 1, 'AVAILABLE', CURRENT_TIMESTAMP),
  (4, 'Phạm Minh Đức', 'Screen', 'Can Tho', 0, 'AVAILABLE', CURRENT_TIMESTAMP),
  (5, 'Vũ Thu Hà', 'Battery', 'Can Tho', 1, 'AVAILABLE', CURRENT_TIMESTAMP),
  (6, 'Đặng Quốc Huy', 'Hardware', 'Quan 1', 5, 'BUSY', CURRENT_TIMESTAMP)
ON CONFLICT (technician_id) DO UPDATE SET
  full_name = EXCLUDED.full_name,
  skill = EXCLUDED.skill,
  service_area = EXCLUDED.service_area,
  current_workload = EXCLUDED.current_workload,
  availability_status = EXCLUDED.availability_status;

-- Cập nhật sequence cho technician
SELECT setval('technician_technician_id_seq', (SELECT MAX(technician_id) FROM technician));

-- 2. Dữ liệu Phiếu bảo hành (5 phiếu mẫu: 4 chờ phân công, 1 đã phân công)
INSERT INTO warranty_ticket (ticket_id, ticket_code, customer_name, device_name, issue_description, required_skill, service_area, status, created_at)
VALUES
  (1, 'TCK-2026-0001', 'Nguyễn Văn Khách', 'iPhone 13 Pro', 'Màn hình chớp nháy, liệt cảm ứng góc phải', 'Screen', 'Can Tho', 'UNASSIGNED', CURRENT_TIMESTAMP),
  (2, 'TCK-2026-0002', 'Lê Thị Mai', 'Samsung Galaxy S23', 'Pin sạc không vào, máy nóng bất thường', 'Battery', 'Can Tho', 'UNASSIGNED', CURRENT_TIMESTAMP),
  (3, 'TCK-2026-0003', 'Trần Hữu Nghĩa', 'Xiaomi 13 Ultra', 'Treo logo khởi động, không vào được Android', 'Software', 'Quan 1', 'UNASSIGNED', CURRENT_TIMESTAMP),
  (4, 'TCK-2026-0004', 'Phạm Thanh Tùng', 'iPad Pro M2', 'Không nhận phím cứng và cổng sạc USB-C', 'Hardware', 'Quan 1', 'UNASSIGNED', CURRENT_TIMESTAMP),
  (5, 'TCK-2026-0005', 'Đỗ Mỹ Linh', 'MacBook Air M1', 'Không lên nguồn sau khi sập nguồn đột ngột', 'Hardware', 'Binh Thanh', 'ASSIGNED', CURRENT_TIMESTAMP)
ON CONFLICT (ticket_code) DO UPDATE SET
  customer_name = EXCLUDED.customer_name,
  device_name = EXCLUDED.device_name,
  issue_description = EXCLUDED.issue_description,
  required_skill = EXCLUDED.required_skill,
  service_area = EXCLUDED.service_area,
  status = EXCLUDED.status;

-- Cập nhật sequence cho warranty_ticket
SELECT setval('warranty_ticket_ticket_id_seq', (SELECT MAX(ticket_id) FROM warranty_ticket));

-- 3. Dữ liệu Phân công (1 phân công mẫu cho phiếu 5)
INSERT INTO assignment (assignment_id, ticket_id, technician_id, assigned_at, status)
VALUES
  (1, 5, 3, CURRENT_TIMESTAMP, 'ASSIGNED')
ON CONFLICT (ticket_id) DO UPDATE SET
  technician_id = EXCLUDED.technician_id,
  status = EXCLUDED.status;

SELECT setval('assignment_assignment_id_seq', (SELECT MAX(assignment_id) FROM assignment));

-- 4. Dữ liệu Lịch hẹn (2 lịch hẹn mẫu)
INSERT INTO appointment (appointment_id, ticket_id, technician_id, appointment_type, start_time, end_time, status, created_at)
VALUES
  (1, 5, 3, 'RECEIVE', CURRENT_TIMESTAMP + INTERVAL '1 day' + INTERVAL '9 hours', CURRENT_TIMESTAMP + INTERVAL '1 day' + INTERVAL '10 hours', 'CONFIRMED', CURRENT_TIMESTAMP),
  (2, 4, 1, 'RECEIVE', CURRENT_TIMESTAMP + INTERVAL '2 days' + INTERVAL '14 hours', CURRENT_TIMESTAMP + INTERVAL '2 days' + INTERVAL '15 hours', 'PENDING', CURRENT_TIMESTAMP)
ON CONFLICT (appointment_id) DO UPDATE SET
  status = EXCLUDED.status,
  start_time = EXCLUDED.start_time,
  end_time = EXCLUDED.end_time;

SELECT setval('appointment_appointment_id_seq', (SELECT MAX(appointment_id) FROM appointment));
