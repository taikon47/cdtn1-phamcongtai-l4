# Nhật ký thay đổi thiết kế – L4

**Sinh viên:** Phạm Công Tài – 2374802010445  
**Dự án:** Smart CRM – Mekong Mobile / Track SE  
**Ngày rà soát:** 17/10/2026

## 1. Phản hồi BT1

- Điểm BT1: 7,50/10 (theo phản hồi sinh viên cung cấp).
- Giảng viên chưa chỉ rõ lỗi cụ thể cần sửa trong nhận xét được cung cấp.
- Nhóm tự rà soát tính nhất quán giữa tài liệu API và DDL để chuẩn bị triển khai BT2.

## 2. Các quyết định thiết kế

| Mã | Vấn đề phát hiện | Quyết định | Lý do / tác động |
|---|---|---|---|
| D01 | API dùng `UNASSIGNED`, DDL mặc định `WAITING_ASSIGNMENT` | Đổi mặc định trong `data/schema.sql` thành `UNASSIGNED` | API lọc đúng phiếu chưa phân công |
| D02 | API dùng `RECEIVE`/`RETURN`, DDL kiểm tra `DELIVERY`/`RECEIPT` | Đổi CHECK trong `data/schema.sql` thành `RECEIVE`/`RETURN` | Dữ liệu tạo lịch hẹn hợp lệ theo hợp đồng API |
| D03 | API mẫu trả `skills` (danh sách), DDL chỉ có `skill` (một giá trị) | Sửa ví dụ API thành `skill` (chuỗi) | Giữ phạm vi BT2 đơn giản và thống nhất DDL; mở rộng đa kỹ năng sau nếu cần |

## 3. Phạm vi giữ nguyên

- Giữ luồng L4: phân công kỹ thuật viên và lịch hẹn.
- Giữ các bảng `warranty_ticket`, `technician`, `assignment`, `appointment`.
- Không sửa bản báo cáo BT1 Word/PDF đã nộp.

## 4. Việc cần thực hiện ở BT2

- Xây dựng ứng dụng Java 21 / Spring Boot theo tài liệu thiết kế.
- Bổ sung migration, seed, CRUD và các quy tắc kiểm tra phân công/trùng lịch.
- Viết kiểm thử, cập nhật README và lưu ảnh chạy module.

> Ghi chú: Các thay đổi trên mới được đối chiếu ở mức tài liệu; chưa chạy kiểm thử tích hợp PostgreSQL/Spring Boot.


## 5. Cập nhật chức năng giao diện và API (17/10/2026)

- Bổ sung thao tác hủy lịch qua `PATCH /api/v1/appointments/{appointmentId}/cancel`; lịch hoàn tất bị chặn không cho hủy.
- Bổ sung bộ lọc danh sách lịch hẹn theo ngày, trạng thái và kỹ thuật viên ở giao diện.
- Mở rộng dashboard với số lịch đã xác nhận và lịch sắp diễn ra, bên cạnh các chỉ số hiện có.
- Bổ sung thông báo khi hủy lịch, xác nhận thao tác trước khi hủy và trạng thái đang xử lý của nút.
- Các bộ lọc là lọc hiển thị phía client; API danh sách vẫn trả toàn bộ lịch theo thời gian bắt đầu.

Các thay đổi trên cần được kiểm chứng bằng cách chạy ứng dụng với PostgreSQL, thử các trạng thái `PENDING`, `CONFIRMED`, `CANCELLED`, `COMPLETED`, và chụp ảnh màn hình thực tế trước khi nộp.


## 6. Thay đổi thiết kế và nâng cấp đóng gói ở Bài tập 3 (BT3) (13/11/2026)

Mục này ghi nhận toàn bộ các quyết định kỹ thuật và thay đổi tích lũy khi chuyển giao hệ thống từ Bài tập 2 sang Bài tập 3, tuân thủ nguyên tắc: **thay đổi gì · vì sao · ảnh hưởng tới thành phần nào**.

### 6.1. Bổ sung Endpoint Giám sát `/health` phục vụ Docker Healthcheck
- **Thay đổi gì:** Xây dựng `HealthController.java` lắng nghe `GET /health` để truy vấn kiểm tra kết nối PostgreSQL (`SELECT 1`) và trả về `{"status":"ok","db":"up"}` với mã HTTP 200.
- **Vì sao:** Docker compose cần cơ chế healthcheck tin cậy để xác định chính xác ứng dụng đã kết nối CSDL thành công trước khi coi là sẵn sàng (`healthy`), khắc phục lỗi `BUG-04`.
- **Ảnh hưởng:** `src/main/java/.../HealthController.java`, `Dockerfile`, `docker-compose.yml`, `docs/deployment-guide.md`, `docs/api-docs.md`.

### 6.2. Đóng gói Môi trường Tái lập Toàn diện với Docker & Docker Compose (Mức 3)
- **Thay đổi gì:** Xây dựng `Dockerfile` đa tầng (multi-stage build dựa trên Eclipse Temurin 21 Alpine) và tệp điều phối `docker-compose.yml` gồm 2 dịch vụ (`smartcrm_l4_app` và `smartcrm_l4_db`).
- **Vì sao:** Đạt chuẩn đầu ra CLO4 và tiêu chí CỐT LÕI C1 của BT3: chuyển từ mức tái lập 2 (cài đặt môi trường thủ công trên máy host) sang mức tái lập 3 (đóng gói toàn bộ runtime, thư viện và CSDL trong container, chạy bằng $\le$ 2 lệnh).
- **Ảnh hưởng:** Thêm mới `Dockerfile`, `docker-compose.yml`, `.dockerignore`, cập nhật `.env.example` và `README.md`.

### 6.3. Chuẩn hóa Bộ kiểm thử 12 Ca kiểm chứng BT3 (`L4BT3VerificationTests.java`)
- **Thay đổi gì:** Bổ sung lớp kiểm thử `L4BT3VerificationTests.java` chứa đúng 12 test case bám sát 5 nhóm yêu cầu của rubric BT3 (Luồng đúng, Giá trị biên, Dữ liệu sai, Bảo mật OWASP A01, Tính tái lập).
- **Vì sao:** Cung cấp bộ kiểm thử chuẩn hóa, chạy độc lập cực nhanh (~0.04s) bằng một lệnh duy nhất, giúp giảng viên dễ dàng chọn ngẫu nhiên một test case để chạy lại tại chỗ mà không gặp lỗi môi trường.
- **Ảnh hưởng:** `src/test/java/.../L4BT3VerificationTests.java`, `docs/test-plan.md`, `docs/test-report.md`.

### 6.4. Cơ chế Nạp Dữ liệu Mẫu Idempotent tự động qua `db/init/`
- **Thay đổi gì:** Chuyển các lệnh tạo bảng và nạp dữ liệu mẫu vào thư mục `db/init/` (`001_create_tables.sql` và `002_seed_danh_muc.sql`) và sử dụng mệnh đề `ON CONFLICT DO UPDATE`.
- **Vì sao:** Postgres container tự động thực thi các tệp này khi khởi động. Kỹ thuật `ON CONFLICT` đảm bảo tiêu chí kiểm chứng C8: chạy lại lần 2 không phát sinh lỗi trùng khóa chính và không nhân đôi dữ liệu.
- **Ảnh hưởng:** `db/init/`, `docker-compose.yml`, `docs/deployment-guide.md`.
