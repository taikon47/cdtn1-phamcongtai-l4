# AI DISCLOSURE – BẢNG KHAI BÁO SỬ DỤNG CÔNG CỤ AI
## Cập nhật toàn diện cho Giai đoạn 3 (Bài tập 3: Kiểm thử và Đóng gói Prototype)

- **Sinh viên:** Phạm Công Tài  
- **MSSV:** 2374802010445  
- **Track:** Software Engineering (SE)  
- **Luồng nghiệp vụ:** L4 – Phân công kỹ thuật viên & lịch hẹn
- **Học kỳ:** HK1 – Năm học 2026–2027

---

### I. MỤC ĐÍCH KHAI BÁO VÀ CHÍNH SÁCH LIÊM CHÍNH HỌC THUẬT

Tài liệu này ghi nhận trung thực và đầy đủ việc sử dụng công cụ Trí tuệ Nhân tạo (AI) trong suốt Giai đoạn 3 của học phần Chuyên đề tốt nghiệp 1.
- **Quyền hạn được phép:** Sinh viên sử dụng AI để gợi ý cấu trúc cú pháp Dockerfile/Compose, hỗ trợ tìm nguyên nhân lỗi (debugging), gợi ý mẫu bảng biểu tài liệu kỹ thuật tiếng Anh.
- **Trách nhiệm bắt buộc:** Toàn bộ mã nguồn, cấu hình Docker, các test case kiểm thử và dữ liệu trong báo cáo đều do sinh viên tự chạy thực tế trên máy tính, tự kiểm chứng tính đúng đắn và hiểu rõ 100% khi được Giảng viên chất vấn.

---

### II. BẢNG KHAI BÁO CHI TIẾT GIAI ĐOẠN 3 (BÀI TẬP 3)

| Công cụ AI | Dùng vào việc gì | Áp dụng ở tệp / hàm nào | Sinh viên đã kiểm chứng thế nào |
|:---|:---|:---|:---|
| **ChatGPT / Antigravity** | Gợi ý cấu trúc `Dockerfile` multi-stage build cho Spring Boot 4.0 (Java 21) và cấu hình `HEALTHCHECK` | `Dockerfile`, `docker-compose.yml` | Đọc tài liệu chính thức của Docker. Tự kiểm bằng lệnh `docker compose ps` để kiểm tra trạng thái `healthy`; thử dừng dịch vụ database để xác nhận ứng dụng báo lỗi kết nối có kiểm soát. |
| **ChatGPT / Antigravity** | Hỗ trợ tìm nguyên nhân và viết ca kiểm thử tái lập cho lỗi đặt lịch hẹn trong quá khứ (`BUG-01`) | `src/main/java/.../L4ApiController.java`, `L4BT3VerificationTests.java` | Sinh viên tự chạy test `TC-07` (`sameDayAppointmentInPastIsRejected`) trước khi sửa (bị FAIL) và sau khi bổ sung so sánh `LocalDateTime` (chạy PASS). |
| **ChatGPT / Antigravity** | Hỗ trợ cấu hình bộ 12 test case chuyên biệt bao phủ 5 nhóm kiểm thử chuẩn theo rubric BT3 | `src/test/java/.../L4BT3VerificationTests.java` | Chạy lệnh `./mvnw test -Dtest=L4BT3VerificationTests` trên terminal thực tế, xác nhận toàn bộ 12 test case đều PASS với thời gian thực thi 0.039 giây. |
| **ChatGPT / Antigravity** | Gợi ý khung tài liệu tiếng Anh theo chuẩn ngành (Deployment Guide 5 mục, REST API Docs 7 mục) | `docs/deployment-guide.md`, `docs/api-docs.md` | Đối chiếu từng endpoint với code Java thật; tự chạy API bằng Postman/curl và chép đúng dữ liệu JSON phản hồi thực tế vào tài liệu, không dùng dữ liệu giả định. |
| **ChatGPT** | Gợi ý giải pháp nạp dữ liệu mẫu Idempotent không gây lỗi khi chạy lại lần 2 (`ON CONFLICT DO UPDATE`) | `db/init/002_seed_danh_muc.sql` | Chạy lệnh nạp SQL hai lần liên tiếp vào PostgreSQL, kiểm tra số dòng qua `SELECT count(*) FROM technician` không bị nhân đôi (đạt chuẩn C8). |
| *— Không dùng AI —* | Toàn bộ nghiệp vụ cốt lõi L4, logic phân công kỹ thuật viên, cấu trúc cơ sở dữ liệu và bảo mật | `src/main/java/.../service/`, `src/main/resources/`, `db/init/001_create_tables.sql` | Sinh viên tự thiết kế, tự xây dựng và chịu trách nhiệm toàn bộ. |

---

### III. CAM KẾT LIÊM CHÍNH HỌC THUẬT

**"Tôi xác nhận đã đọc, hiểu và chịu trách nhiệm về toàn bộ nội dung nộp. Mọi test case và kết quả kiểm thử trong báo cáo đều đã được thực thi thật và sẵn sàng chạy lại trước mặt Giảng viên."**

**Họ và tên:** Phạm Công Tài  
**MSSV:** 2374802010445  
**Ngày hoàn thiện:** 13/11/2026
