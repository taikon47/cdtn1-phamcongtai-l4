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
