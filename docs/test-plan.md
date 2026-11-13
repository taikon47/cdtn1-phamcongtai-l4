# TEST PLAN RÚT GỌN – BÀI TẬP 3
## Module L4: Phân công Kỹ thuật viên & Lịch hẹn (Smart CRM – Mekong Mobile)

- **Sinh viên thực hiện:** Phạm Công Tài
- **MSSV:** 2374802010445
- **Chuyên ngành (Track):** Software Engineering (SE)
- **Học phần:** Chuyên đề Tốt nghiệp 1 (HK1 – Năm học 2026–2027)

---

### 1. Phạm vi kiểm thử và phần chủ ý KHÔNG kiểm

#### 1.1. Phạm vi kiểm thử (In-scope)
Kiểm thử tập trung vào tính đúng đắn, bảo mật và toàn vẹn dữ liệu của luồng nghiệp vụ **L4 – Phân công Kỹ thuật viên & Lịch hẹn**:
- **Quy tắc phân công (QT-01):** Phân công KTV phù hợp với yêu cầu phiếu (khớp chuyên môn kỹ thuật `required_skill`, đúng địa bàn quản lý `service_area`, KTV ở trạng thái `AVAILABLE`), cập nhật trạng thái phiếu bảo hành thành `ASSIGNED` và tự động tăng tải công việc `current_workload` của KTV.
- **Quy tắc tạo lịch hẹn (QT-02):** Tiếp nhận/trả máy (`RECEIVE` / `RETURN`) trong tương lai, xác định thời gian bắt đầu và kết thúc hợp lệ.
- **Quy tắc xác nhận/hủy lịch hẹn (QT-03, QT-04):** Chuyển trạng thái lịch hẹn `PENDING` $\rightarrow$ `CONFIRMED` $\rightarrow$ `CANCELLED`.
- **Kiểm soát xung đột lịch:** Phát hiện và chặn kịp thời các lịch hẹn bị trùng lấn thời gian của cùng một KTV.
- **Bảo mật và toàn vẹn (OWASP Top 10:2025):** Kiểm soát truy cập phân quyền khu vực địa bàn (A01 Broken Access Control), chống gán đè trùng phiếu bảo hành, kiểm tra ràng buộc thời gian ở tầng nghiệp vụ.

#### 1.2. Phần chủ ý KHÔNG kiểm và lý do (Out-of-scope)
- **Màn hình cấu hình hệ thống / quản trị nâng cao:** Chưa phát sinh quy tắc nghiệp vụ ràng buộc ở giai đoạn prototype.
- **Kiểm thử hiệu năng tải cao (Load / Stress test):** Prototype phục vụ kiểm chứng nghiệp vụ và tính tái lập; kiểm thử hiệu năng quy mô lớn thuộc phạm vi Chuyên đề Tốt nghiệp 2.
- **Kiểm thử xâm nhập chuyên sâu (Penetration testing):** Được thay thế bằng tự đánh giá 7 mục bảo mật cơ bản theo checklist đề bài.
- **Tự động phân loại sự cố bằng AI:** Thuộc phạm vi của module Track AI (L1), không thuộc trách nhiệm của module L4.

---

### 2. Loại test và số lượng mỗi tầng

| Tầng kiểm thử | Số lượng | Tệp kiểm thử phụ trách | Ghi chú |
|:---|:---:|:---|:---|
| **Bộ kiểm chứng BT3 cốt lõi** | **12** | `tests/.../L4BT3VerificationTests.java` | 12 test case chuẩn đại diện cho 5 nhóm theo rubric BT3 |
| **Unit test nghiệp vụ phân công** | 8 | `tests/.../L4AssignmentBusinessTests.java` | Kiểm thử chi tiết ma trận tương thích chuyên môn và địa bàn |
| **Unit test nghiệp vụ lịch hẹn** | 9 | `tests/.../L4ApiControllerBusinessTests.java` | Kiểm thử biên thời gian, xung đột giờ, kiểm tra quá khứ |
| **Unit test Service quản lý KTV** | 10 | `tests/.../TechnicianServiceTests.java` | Kiểm thử CRUD và truy vấn danh sách KTV |
| **Tổng cộng toàn bộ repo** | **39** | *(Chạy bằng lệnh `./mvnw test`)* | 100% PASS trên môi trường máy sạch |

---

### 3. Tiêu chí thành công ĐO ĐƯỢC (Measurable Criteria)

Để khẳng định việc kiểm thử đạt chất lượng **ĐỦ** và tin cậy:
1. **Tỷ lệ Pass:** 100% trong số 12 test case trọng tâm và 39 test case toàn bộ hệ thống phải đạt kết quả **PASS** trong một lệnh duy nhất.
2. **Độ bao phủ quy tắc:** Mỗi quy tắc nghiệp vụ cốt lõi (QT-01 đến QT-04) đều có ít nhất 1 test case luồng đúng, 1 test case biên và 1 test case dữ liệu sai.
3. **Chỉ số lỗi tồn đọng:** **0 bug mức Critical** và **0 bug mức Major** còn mở trước khi đóng gói và gắn tag `v1.0`.
4. **Tính tái lập (Reproducibility):** Chạy lại bộ kiểm thử 2 lần liên tiếp không làm thay đổi kết quả (0 flakiness).

---

### 4. Môi trường và dữ liệu thử

- **Môi trường chạy:**
  - Container Docker Compose chạy trên Alpine Linux (`eclipse-temurin:21-jre-alpine` và `postgres:17-alpine`).
  - Hoặc trực tiếp trên máy host cài sẵn JDK 21 và Maven Wrapper.
- **Nguồn dữ liệu thử:**
  - Bộ dữ liệu mẫu chuẩn được định nghĩa tại `db/init/001_create_tables.sql` và `db/init/002_seed_danh_muc.sql` gồm: 6 kỹ thuật viên, 5 phiếu bảo hành, 1 phân công, 2 lịch hẹn.
  - Các ca test cô lập (Unit tests) sử dụng Mockito để tạo mock data sạch trước mỗi test (`@BeforeEach setup()`), đảm bảo tính độc lập và không phụ thuộc trạng thái cơ sở dữ liệu thật.
