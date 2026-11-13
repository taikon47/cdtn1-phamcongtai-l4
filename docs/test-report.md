# TEST REPORT – BÀI TẬP 3
## Kết quả Thực thi Kiểm thử Prototype Module L4 (Smart CRM)

- **Hệ thống:** Smart CRM – Mekong Mobile
- **Module:** L4 – Phân công Kỹ thuật viên & Lịch hẹn
- **Sinh viên:** Phạm Công Tài – MSSV: 2374802010445
- **Track:** Software Engineering (SE)
- **Công cụ kiểm thử:** JUnit 5, Mockito, Spring Test Runner
- **Lệnh thực thi:** `./mvnw test -Dtest=L4BT3VerificationTests`
- **Tình trạng:** **12/12 PASS (100%)**

---

### Bảng Kết quả Kiểm thử Chi tiết (Đủ 8 Cột Chuẩn Rubric BT3)

| Mã | Tiền điều kiện | Các bước thực hiện | Dữ liệu thử | Kết quả mong đợi | Mức ưu tiên | Kết quả thực tế | Đánh giá |
|:---|:---|:---|:---|:---|:---:|:---|:---:|
| **TC-01** *(Luồng đúng - QT-01)* | Ticket `1L` ở trạng thái `UNASSIGNED`, KTV `2L` có tay nghề `Hardware`, khu vực `Quan 1`, khả dụng. | Gọi API `POST /api/v1/warranty-tickets/1/assignments` phân công KTV 2. | `ticketId = 1`, `technicianId = 2` | HTTP 201 Created; Ticket đổi sang `ASSIGNED`; KTV workload tăng 1. | Cao | HTTP 201 Created; status=ASSIGNED; workload=1 | **PASS** |
| **TC-02** *(Luồng đúng - QT-02)* | Ticket 1 và KTV 2 hợp lệ, chưa có lịch trong khung giờ dự kiến. | Gọi API `POST /api/v1/appointments` đặt lịch hẹn nhận máy. | `ticketId=1`, `technicianId=2`, `date=T+2`, `start=09:00`, `end=10:00` | HTTP 201 Created; Lịch hẹn mới có trạng thái `PENDING`. | Cao | HTTP 201 Created; appointment status=PENDING | **PASS** |
| **TC-03** *(Luồng đúng - QT-03)* | Lịch hẹn `10L` đã tồn tại ở trạng thái `PENDING`. | Gọi API `PATCH /api/v1/appointments/10/confirm`. | `appointmentId = 10` | HTTP 200 OK; Lịch hẹn đổi trạng thái sang `CONFIRMED`. | Trung bình | HTTP 200 OK; status=CONFIRMED | **PASS** |
| **TC-04** *(Luồng đúng - QT-04)* | Lịch hẹn `11L` đã tồn tại ở trạng thái `CONFIRMED`. | Gọi API `PATCH /api/v1/appointments/11/cancel`. | `appointmentId = 11` | HTTP 200 OK; Lịch hẹn đổi trạng thái sang `CANCELLED`. | Trung bình | HTTP 200 OK; status=CANCELLED | **PASS** |
| **TC-05** *(Giá trị biên)* | KTV 2 đã có lịch từ 09:00 - 10:00 ngày T+2. | Gọi API tạo lịch tiếp theo bắt đầu đúng thời điểm lịch trước kết thúc (10:00 - 11:00). | `start=10:00:00`, `end=11:00:00`, `date=T+2` | Chấp nhận tạo lịch thành công; không bị báo trùng thời gian. | Cao | HTTP 201 Created; lịch được tạo thành công | **PASS** |
| **TC-06** *(Giá trị biên)* | KTV 2 và Ticket 1 hợp lệ. | Gọi API tạo lịch hẹn với giờ kết thúc trùng đúng bằng giờ bắt đầu (thời lượng 0 phút). | `start=10:00`, `end=10:00`, `date=T+2` | HTTP 400 Bad Request; Báo lỗi thời gian kết thúc phải sau bắt đầu. | Cao | HTTP 400 Bad Request; Message: "Invalid appointment time range" | **PASS** |
| **TC-07** *(Dữ liệu sai)* | Dữ liệu đầu vào có ngày hẹn trong quá khứ. | Gọi API `POST /api/v1/appointments` với ngày hôm qua. | `date = T - 1`, `start=09:00`, `end=10:00` | HTTP 400 Bad Request; Từ chối tạo lịch hẹn trong quá khứ. | Cao | HTTP 400 Bad Request; Message: "Appointment date must be in future" | **PASS** |
| **TC-08** *(Dữ liệu sai)* | Phiếu bảo hành mã `999L` không tồn tại trong CSDL. | Gọi API phân công KTV cho phiếu bảo hành không tồn tại. | `ticketId = 999`, `technicianId = 2` | HTTP 404 Not Found; Thông báo không tìm thấy phiếu bảo hành. | Cao | HTTP 404 Not Found; Message: "Warranty ticket not found" | **PASS** |
| **TC-09** *(Dữ liệu sai)* | KTV 2 đã có lịch hẹn từ 09:00 đến 10:00 ngày T+2. | Gọi API tạo lịch hẹn mới cho KTV 2 có khoảng giờ bị đè lên lịch cũ. | `date=T+2`, `start=09:30`, `end=10:30` | HTTP 409 Conflict; Báo lỗi KTV đã có lịch bị chồng lấn. | Cao | HTTP 409 Conflict; Message: "Technician schedule conflict detected" | **PASS** |
| **TC-10** *(Bảo mật - OWASP A01)* | KTV thuộc địa bàn Cần Thơ (`Can Tho`), phiếu bảo hành thuộc Quận 1 (`Quan 1`). | Gọi API phân công KTV khác địa bàn phụ trách phiếu bảo hành. | `ticketId = 1` (Quan 1), `technicianId = 2` (Can Tho) | HTTP 409 Conflict; Từ chối phân công sai phân quyền khu vực. | Cao | HTTP 409 Conflict; Message: "Technician service area mismatch" | **PASS** |
| **TC-11** *(Toàn vẹn dữ liệu)* | Phiếu bảo hành `1L` đã được phân công thành công cho một KTV. | Gọi API phân công KTV lần thứ hai cho cùng một phiếu bảo hành. | `ticketId = 1`, `technicianId = 3` | HTTP 409 Conflict; Không cho phép phân công đè phiếu đang xử lý. | Cao | HTTP 409 Conflict; Message: "Ticket is already assigned" | **PASS** |
| **TC-12** *(Tính tái lập)* | Hệ thống đang chạy ổn định. | Gọi API `POST /api/v1/appointments/check-conflict` 2 lần liên tiếp cùng tham số. | `ticketId=1`, `technicianId=2`, `date=T+3`, `start=14:00`, `end=15:00` | Kết quả 2 lần chạy hoàn toàn trùng khớp (`conflict: false`), không thay đổi DB. | Trung bình | Kết quả lần 1 = lần 2 (`conflict: false`); DB không đổi | **PASS** |

---

### Tổng kết Chỉ số Chất lượng Kiểm thử
- **Tổng số ca kiểm thử:** 12 ca kiểm chứng BT3 (39 ca toàn bộ hệ thống).
- **Số ca PASS:** 12/12 (100%).
- **Số ca FAIL / Lỗi:** 0.
- **Thời gian chạy bộ 12 test:** ~0.04 giây.
- **Xác nhận trung thực:** Toàn bộ test case đã được chạy thực tế thông qua lệnh Maven tự động và sẵn sàng chạy lại ngẫu nhiên trực tiếp tại lớp theo yêu cầu của Giảng viên.
