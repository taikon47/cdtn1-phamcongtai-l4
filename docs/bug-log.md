# BUG LOG – BÀI TẬP 3
## Nhật ký Gỡ lỗi và Minh chứng Khắc phục Module L4 (Smart CRM)

- **Sinh viên:** Phạm Công Tài – MSSV: 2374802010445
- **Track:** Software Engineering (SE)
- **Repository:** https://github.com/taikon47/cdtn1-phamcongtai-l4
- **Tiêu chuẩn:** Đủ 7 dòng bắt buộc cho mỗi bug, link commit mở được và trỏ đúng nội dung sửa.

---

### BUG-01 (MAJOR) – Chấp nhận lịch hẹn có thời gian bắt đầu trong quá khứ

1. **Mã và mức độ:** `BUG-01` · Mức độ: **MAJOR**
2. **Phát hiện bởi test case:** `TC-07` / `sameDayAppointmentInPastIsRejected` trong `L4ApiControllerBusinessTests`
3. **Triệu chứng:** API chấp nhận tạo lịch hẹn có thời gian bắt đầu đã trôi qua trong ngày hôm nay mà không báo lỗi, dẫn đến việc đặt lịch hẹn không khả thi với khách hàng.
4. **Cách tái lập:** Gửi request `POST /api/v1/appointments` với `appointmentDate = LocalDate.now()` và `startTime = LocalTime.MIDNIGHT`.
5. **Nguyên nhân:** Trong `L4ApiController.createAppointment()`, hệ thống chỉ kiểm tra `appointmentDate.isBefore(LocalDate.now())` mà không kết hợp ngày và giờ thành `LocalDateTime` để so sánh với thời gian thực tế `LocalDateTime.now()`.
6. **Cách sửa:** Ghép ngày và giờ thành `LocalDateTime.of(appointmentDate, startTime)`, sau đó kiểm tra `.isBefore(LocalDateTime.now())`; nếu vi phạm ném `ResponseStatusException(HttpStatus.BAD_REQUEST, "Thời gian hẹn trong quá khứ")`.
7. **Link commit và xác nhận:** [Commit d52d315](https://github.com/taikon47/cdtn1-phamcongtai-l4/commit/d52d3158f2377b4792df66e74f41b29ad54fc6e0) — Test `sameDayAppointmentInPastIsRejected` và `TC-07` chạy PASS 100%.

---

### BUG-02 (MAJOR) – Phiếu bảo hành không đổi trạng thái sang ASSIGNED sau khi phân công

1. **Mã và mức độ:** `BUG-02` · Mức độ: **MAJOR**
2. **Phát hiện bởi test case:** `TC-01` / `assign_validTechnician_success` trong `L4AssignmentBusinessTests`
3. **Triệu chứng:** Sau khi gọi API phân công KTV thành công, danh sách phiếu bảo hành chưa phân công (`status=UNASSIGNED`) vẫn hiển thị lại phiếu vừa được gán.
4. **Cách tái lập:** Gọi `POST /api/v1/warranty-tickets/1/assignments` với technicianId=2, sau đó gọi `GET /api/v1/warranty-tickets?status=UNASSIGNED` thì phiếu 1 vẫn xuất hiện.
5. **Nguyên nhân:** Trong tầng service xử lý phân công, hệ thống chỉ tạo mới và lưu bản ghi `Assignment` vào CSDL mà quên cập nhật trường trạng thái `status` của thực thể `WarrantyTicket` từ `UNASSIGNED` sang `ASSIGNED`.
6. **Cách sửa:** Bổ sung lệnh `ticket.setStatus("ASSIGNED")` và gọi `ticketRepository.save(ticket)` trong cùng transaction, đảm bảo tính nhất quán dữ liệu giữa bảng phiếu và bảng phân công.
7. **Link commit và xác nhận:** [Commit e0b7adc](https://github.com/taikon47/cdtn1-phamcongtai-l4/commit/e0b7adcf6674a67b9eadc085255abfb8f58eb1aa) — Test `assign_validTechnician_success` và `TC-01` chạy PASS.

---

### BUG-03 (CRITICAL) – Khởi động máy sạch thất bại do thiếu cơ chế khởi tạo cấu trúc CSDL tự động

1. **Mã và mức độ:** `BUG-03` · Mức độ: **CRITICAL**
2. **Phát hiện bởi test case:** Phép thử máy trắng (`C1 White-box deployment test`)
3. **Triệu chứng:** Khi triển khai trên môi trường mới chưa có sẵn cơ sở dữ liệu, ứng dụng Spring Boot bị crash ngay khi khởi động với ngoại lệ `PSQLException: ERROR: relation "technician" does not exist`.
4. **Cách tái lập:** Chạy ứng dụng trên một PostgreSQL server mới tinh chưa được chạy script tạo bảng thủ công.
5. **Nguyên nhân:** Cấu hình `spring.jpa.hibernate.ddl-auto=validate` chỉ kiểm tra tính hợp lệ của schema mà không tự sinh bảng, trong khi dự án ban đầu chưa tích hợp công cụ quản lý migration tự động khi boot.
6. **Cách sửa:** Tích hợp Flyway migration (`spring-boot-starter-flyway`, `flyway-database-postgresql`), cấu hình `spring.flyway.baseline-on-migrate=true` và tạo script `V1__create_tables.sql` trong `src/main/resources/db/migration/`.
7. **Link commit và xác nhận:** [Commit a574414](https://github.com/taikon47/cdtn1-phamcongtai-l4/commit/a57441490938879de495dd54aee0c0eb4070f64b) — Ứng dụng tự động tạo bảng và khởi động thành công trên database sạch.

---

### BUG-04 (MAJOR) – Thiếu Endpoint Healthcheck khiến Docker Container luôn ở trạng thái Unhealthy

1. **Mã và mức độ:** `BUG-04` · Mức độ: **MAJOR**
2. **Phát hiện bởi test case:** Kiểm tra Docker Compose Healthcheck và Tiêu chí C5
3. **Triệu chứng:** Khi đóng gói Docker Compose, container `smartcrm_l4_app` sau khi khởi động thành công vẫn bị Docker đánh dấu là `unhealthy` và lệnh `curl http://localhost:8081/health` trả về mã lỗi `404 Not Found`.
4. **Cách tái lập:** Chạy `docker compose up -d` và quan sát trạng thái qua `docker compose ps`.
5. **Nguyên nhân:** Hệ thống chưa định nghĩa controller xử lý endpoint `/health` để Docker daemon và người vận hành kiểm tra độ sẵn sàng của ứng dụng và kết nối CSDL.
6. **Cách sửa:** Xây dựng `HealthController.java` lắng nghe `GET /health`, thực thi truy vấn kiểm tra kết nối PostgreSQL `SELECT 1` qua `JdbcTemplate` và phản hồi payload `{"status":"ok","db":"up"}` với HTTP code 200.
7. **Link commit và xác nhận:** Commit cập nhật BT3 trên repo — Container chuyển trạng thái `healthy` ổn định, tiêu chí C1 và C5 đạt 100%.
