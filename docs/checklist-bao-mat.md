# CHECKLIST BẢO MẬT 7 MỤC – BÀI TẬP 3
## Tự đánh giá An toàn ứng dụng theo chuẩn OWASP Top 10:2025

- **Hệ thống:** Smart CRM – Mekong Mobile (Module L4)
- **Sinh viên:** Phạm Công Tài – MSSV: 2374802010445
- **Track:** Software Engineering (SE)
- **Quy tắc:** Mỗi mục ghi đúng **ĐÃ LÀM / CHƯA LÀM / KHÔNG ÁP DỤNG** kèm đúng MỘT DÒNG MINH CHỨNG kiểm chứng được.

---

### Bảng Tự Đánh Giá 7 Mục Bảo Mật

| STT | Mục kiểm tra bảo mật (Theo Đề bài BT3) | Trạng thái | Minh chứng thực tế |
|:---:|:---|:---:|:---|
| **1** | **Không có secret nào trong repo** (mật khẩu, khoá API, chuỗi kết nối, token) | **ĐÃ LÀM** | `.env` được chặn tại dòng 13 của `.gitignore`; chạy `git log --all --oneline -- .env` cho kết quả rỗng 100%; toàn bộ biến mật đọc qua biến môi trường trong `src/main/resources/application.properties`. |
| **2** | **Dữ liệu vào được kiểm ở tầng ứng dụng**, không chỉ dựa vào ràng buộc CSDL | **ĐÃ LÀM** | Bộ 3 lệnh grep kiểm tra: không có nối chuỗi SQL trong Java backend (dùng Spring Data JPA tham số hóa); kiểm tra nghiệp vụ ngày/giờ hẹn tại `L4ApiController.java` (minh chứng qua test `TC-06`, `TC-07` trả về `400 Bad Request`). |
| **3** | **Kiểm soát truy cập theo chủ sở hữu** và phân quyền khu vực (OWASP A01) | **ĐÃ LÀM** | Kiểm soát phân quyền địa bàn hoạt động giữa KTV và Phiếu bảo hành trong `AssignmentService.java`; minh chứng test case `TC-10` kiểm tra KTV sai khu vực bị từ chối trả về `HTTP 409 Conflict`. |
| **4** | **Mật khẩu và dữ liệu nhạy cảm được băm hoặc mã hoá** (OWASP A04, A07) | **KHÔNG ÁP DỤNG** | Prototype nghiệp vụ L4 hiện tại chỉ tập trung vào phân công và lịch hẹn cho nhân viên điều phối nội bộ, không lưu trữ người dùng cá nhân có mật khẩu trực tiếp trong CSDL (dữ liệu nhân sự định danh qua mã nhân viên). |
| **5** | **Thông báo lỗi không để lộ cấu trúc bên trong** (vết ngăn xếp, tên bảng, chuỗi kết nối) | **ĐÃ LÀM** | Mọi ngoại lệ nghiệp vụ được đóng gói qua `ResponseStatusException` của Spring Boot; không in `e.printStackTrace()` ra client; test gửi request sai `TC-08` chỉ trả về mã `404` và message ngắn gọn, không lộ stack trace. |
| **6** | **Nhật ký hệ thống không ghi mật khẩu, token hay dữ liệu cá nhân không cần thiết** (A09) | **ĐÃ LÀM** | Nhật ký ứng dụng cấu hình qua Logback/Spring Boot chỉ ghi mã định danh (ticketId, technicianId, appointmentId), không ghi số điện thoại hay thông tin nhạy cảm của khách hàng; lệnh `docker compose logs app \| grep -iE "password\|token"` cho kết quả rỗng. |
| **7** | **Phụ thuộc bên ngoài được ghim phiên bản và đã kiểm cảnh báo lỗ hổng cơ bản** (A03) | **ĐÃ LÀM** | Ghim cố định phiên bản các dependency trong `pom.xml` (`spring-boot-starter-parent` bản `4.0.8`, `flyway-database-postgresql` bản tương thích); kiểm tra cảnh báo phụ thuộc an toàn. |

---

### Kết luận Đánh giá CLO5 (Đạo đức và Trách nhiệm Nghề nghiệp)
- Hệ thống được rà soát nghiêm túc bằng các lệnh thực thi thực tế trên terminal.
- Không có bất kỳ bí mật (secret) hoặc mật khẩu thật nào bị commit vào lịch sử Git của repository công khai.
- Các mục không thuộc phạm vi của prototype được ghi rõ **KHÔNG ÁP DỤNG** kèm lý do kỹ thuật trung thực, tuân thủ đúng quy định đánh giá của Bộ môn.
