# TEST REPORT – BT2 – SMART CRM L4

**Sinh viên:** Phạm Công Tài – 2374802010445  
**Dự án:** Smart CRM – Mekong Mobile  
**Module:** L4 – Warranty Assignment & Appointment  
**Ngày kiểm thử:** 08/11/2026

## 1. Môi trường kiểm thử

- Java 21.0.12.1
- Spring Boot 4.0.8
- PostgreSQL 17.11
- JUnit 5
- Mockito
- Maven
- Windows PowerShell

## 2. Tổng kết kết quả

| Chỉ tiêu | Kết quả |
|---|---:|
| Tổng số test thực thi | 10 |
| PASS | 10 |
| FAIL | 0 |
| ERROR | 0 |
| SKIPPED | 0 |
| Build | SUCCESS |

## 3. Kết quả kiểm thử chi tiết

### 3.1. L4ApiControllerBusinessTests

| ID | Test case | Kết quả |
|---|---|---|
| TC01 | Tạo lịch hẹn hợp lệ | PASS |
| TC02 | Hai lịch hẹn liền kề nhau | PASS |
| TC03 | Từ chối lịch hẹn bị trùng giờ | PASS |
| TC04 | Giờ kết thúc phải sau giờ bắt đầu | PASS |
| TC05 | Từ chối ngày hẹn trong quá khứ | PASS |
| TC06 | Hủy lịch CONFIRMED | PASS |
| TC07 | Không cho hủy lịch COMPLETED | PASS |
| TC08 | Xác nhận lịch PENDING | PASS |
| TC09 | Từ chối lịch có giờ bắt đầu đã qua trong ngày hiện tại | PASS |

Kết quả: 9/9 PASS, 0 FAIL.

### 3.2. Projectl4ApplicationTests

| ID | Test case | Kết quả |
|---|---|---|
| TC10 | Kiểm tra khởi động Spring Boot Application và tải Application Context | PASS |

Kết quả: 1/1 PASS.

## 4. Kiểm tra môi trường và cơ sở dữ liệu

Qua log thực thi:

- Kết nối PostgreSQL thành công.
- Database: projectl4.
- PostgreSQL version: 17.11.
- Flyway validated thành công 1 migration.
- Database schema version: 1.
- Hibernate khởi tạo JPA EntityManagerFactory thành công.
- Spring Boot khởi động thành công.
- Giao diện index.html được nhận diện trong static resources.

## 5. Phát hiện và xử lý lỗi

### BUG-01: Không chặn lịch hẹn có thời gian bắt đầu trong quá khứ

- Mức độ: Medium.
- Nguyên nhân: Logic cũ chỉ kiểm tra ngày, chưa kiểm tra giờ bắt đầu.
- Cách xử lý: Bổ sung kiểm tra LocalDateTime với thời gian hiện tại.
- Test xác nhận: sameDayAppointmentInPastIsRejected.

Kết quả trước sửa:
- 8 PASS.
- 1 FAIL.

Kết quả sau sửa:
- 9 PASS.
- 0 FAIL.

Commit sửa lỗi: d52d315.

## 6. Khả năng tái lập

Lệnh chạy toàn bộ kiểm thử trên Windows:

    .\mvnw.cmd clean test

Kết quả thực tế ngày 08/11/2026:

    Tests run: 10
    Failures: 0
    Errors: 0
    Skipped: 0
    BUILD SUCCESS

Thời gian thực thi: 14.796 giây.

## 7. Lưu trữ minh chứng

- Test Plan: docs/kiem-thu-bt2.md
- Test Report: docs/test-report.md
- Maven TXT/XML: docs/test-results/
- Bug Log: docs/bug-log.md
- Ảnh kiểm thử: docs/screenshots/ (cần bổ sung ảnh chụp thực tế).

## 8. Giới hạn kết quả

Kết quả 10/10 PASS xác nhận các ca kiểm thử tự động hiện có đã chạy thành công.

Chưa có đủ minh chứng để kết luận đã hoàn thành toàn bộ 5 nhóm kiểm thử theo yêu cầu BT2.

Các nhóm kiểm thử bổ sung, kiểm thử bảo mật, chất lượng dữ liệu và ảnh chụp thực tế cần tiếp tục được thực hiện và ghi nhận riêng.

## 9. Kết luận

Bộ kiểm thử hiện tại thực thi thành công 10/10 test, không có lỗi.

Chức năng kiểm tra lịch hẹn, xử lý trạng thái lịch và kiểm tra khởi động ứng dụng đã được kiểm chứng ở phạm vi các test case hiện có.

BUG-01 đã được sửa, kiểm thử xác nhận và lưu thành commit riêng trên GitHub.
