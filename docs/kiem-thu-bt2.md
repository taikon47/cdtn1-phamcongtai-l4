# TEST PLAN – BT2 – SMART CRM L4

**Sinh viên:** Phạm Công Tài – 2374802010445
**Dự án:** Smart CRM – Mekong Mobile
**Module:** L4 – Warranty Assignment & Appointment
**Nền tảng:** Java 21, Spring Boot 4.0.8, PostgreSQL 17

## 1. Mục tiêu kiểm thử

Kiểm tra tính đúng đắn của chức năng phân công kỹ thuật viên,
quản lý lịch hẹn, xác nhận và hủy lịch.

Đảm bảo dữ liệu hợp lệ, xử lý dữ liệu sai và kết quả có thể tái lập.

## 2. Môi trường kiểm thử

- Java 21
- Spring Boot 4.0.8
- PostgreSQL 17
- JUnit 5
- Mockito
- Maven
- Windows 11

## 3. Phân loại kiểm thử

### Nhóm 1 – Luồng đúng (Happy Path)

| ID | Test case | Kết quả mong đợi |
|---|---|---|
| TC01 | Tạo lịch hợp lệ | PENDING |
| TC02 | Phân công kỹ thuật viên phù hợp | ASSIGNED |
| TC03 | Xác nhận lịch đang PENDING | CONFIRMED |
| TC04 | Hủy lịch CONFIRMED | CANCELLED |

### Nhóm 2 – Kiểm thử biên (Boundary)

| ID | Test case | Kết quả mong đợi |
|---|---|---|
| TC05 | Hai lịch liền kề nhau | Cho phép |
| TC06 | Giờ kết thúc bằng giờ bắt đầu | HTTP 400 |
| TC07 | Ngày hẹn trong quá khứ | HTTP 400 |
| TC08 | Hai lịch có khoảng thời gian chồng lấn | HTTP 409 |

### Nhóm 3 – Dữ liệu sai (Invalid Input)

| ID | Test case | Kết quả mong đợi |
|---|---|---|
| TC09 | Thiếu thông tin lịch hẹn | HTTP 400 |
| TC10 | Loại lịch không hợp lệ | HTTP 400 |
| TC11 | Ticket không tồn tại | HTTP 404 |
| TC12 | Technician không tồn tại | HTTP 404 |

### Nhóm 4 – Bảo mật và chất lượng dữ liệu

| ID | Test case | Kết quả mong đợi |
|---|---|---|
| TC13 | Phân công trùng ticket | HTTP 409 |
| TC14 | Workload không được âm | Database từ chối |
| TC15 | Dữ liệu chuỗi chứa ký tự SQL injection | Không thực thi SQL ngoài ý muốn |
| TC16 | Xóa kỹ thuật viên đang được tham chiếu | Không tạo dữ liệu mồ côi |

### Nhóm 5 – Tái lập (Reproducibility)

| ID | Test case | Kết quả mong đợi |
|---|---|---|
| TC17 | Chạy lại bộ test nhiều lần | Kết quả nhất quán |
| TC18 | Kiểm tra trạng thái ticket sau phân công | ASSIGNED |
| TC19 | Đối chiếu workload với assignment thực tế | Hai giá trị nhất quán |
| TC20 | Chạy toàn bộ test bằng Maven | Kết quả được ghi nhận |

## 4. Công cụ kiểm thử

- JUnit 5: kiểm thử tự động.
- Mockito: giả lập Repository.
- Spring Boot Test: kiểm tra khởi động ứng dụng.
- PostgreSQL: kiểm tra dữ liệu thực tế.
- PowerShell: thực thi lệnh kiểm thử.

## 5. Lệnh chạy kiểm thử

Windows PowerShell:

    .\mvnw.cmd clean test

## 6. Nguyên tắc ghi nhận kết quả

- PASS: Test thực thi và đạt kết quả mong đợi.
- FAIL: Test thực thi nhưng kết quả không đạt.
- BLOCKED: Không thể thực hiện do thiếu điều kiện.
- PLANNED: Chưa thực hiện.

Không ghi PASS khi chưa chạy test thực tế.

## 7. Minh chứng

Lưu ảnh kết quả thực tế tại:

    docs/screenshots/

Lưu báo cáo Maven tại:

    docs/test-results/

## 8. Tiêu chí hoàn thành

- Có đủ 5 nhóm kiểm thử.
- Có Test Report ghi kết quả thực tế.
- Có Bug Log và commit tương ứng với bug được xác nhận.
- Có ảnh minh chứng cho từng test case được báo cáo.
- Có thể chạy lại toàn bộ test bằng một lệnh.
