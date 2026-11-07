# TEST REPORT – BT2 – SMART CRM L4

## 1. Kết quả kiểm thử

Lệnh: .\mvnw.cmd clean test

| Test suite | Tổng test | PASS | FAIL |
|---|---:|---:|---:|
| L4ApiControllerBusinessTests | 9 | 9 | 0 |
| L4AssignmentBusinessTests | 8 | 8 | 0 |
| TechnicianServiceTests | 10 | 10 | 0 |
| Projectl4ApplicationTests | 1 | 1 | 0 |
| TOTAL | 28 | 28 | 0 |

- Tests run: 28
- Failures: 0
- Errors: 0
- Skipped: 0
- BUILD SUCCESS

## 2. Môi trường kiểm thử

- Java 21.0.12.1
- Spring Boot 4.0.8
- PostgreSQL 17.11
- JUnit 5
- Mockito
- Maven
- Windows PowerShell

## 3. Kiểm tra cơ sở dữ liệu

- PostgreSQL connection: SUCCESS
- Flyway migration validation: SUCCESS
- Database schema version: 1
- JPA EntityManagerFactory: SUCCESS
- Spring Boot Application Context: SUCCESS

## 4. Kết luận

Toàn bộ 28 test hiện có đã thực thi thành công.
Không có lỗi kiểm thử.

Kết quả xác nhận các chức năng được kiểm thử trong phạm vi hiện tại. Các kiểm thử giao diện, bảo mật và tích hợp bổ sung vẫn cần được thực hiện riêng.
