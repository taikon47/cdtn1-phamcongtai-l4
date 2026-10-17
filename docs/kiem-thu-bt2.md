# Kế hoạch kiểm thử BT2 – L4

## Kiểm thử tự động

- `Projectl4ApplicationTests`: kiểm tra khởi động ứng dụng và kết nối môi trường PostgreSQL.
- `L4ApiControllerBusinessTests`: 8 ca kiểm thử ở cấp controller với repository giả lập (Mockito).

| Ca | Tình huống | Kỳ vọng |
|---|---|---|
| TC01 | Tạo lịch hợp lệ | PENDING |
| TC02 | Khung giờ sát nhau, repository trả về không trùng | Cho phép |
| TC03 | Repository báo lịch trùng | HTTP 409 |
| TC04 | Giờ kết thúc bằng giờ bắt đầu | HTTP 400 |
| TC05 | Ngày hẹn trong quá khứ | HTTP 400 |
| TC06 | Hủy lịch CONFIRMED | CANCELLED |
| TC07 | Hủy lịch COMPLETED | HTTP 409 |
| TC08 | Xác nhận lịch PENDING, không xung đột | CONFIRMED |

Lệnh Windows: `./mvnw.cmd clean test`. Cần đặt `JAVA_HOME` đúng và PostgreSQL hoạt động cho bài test khởi động.

## Kiểm tra thủ công

1. Mở `http://localhost:8081/`, xác nhận kết nối API.
2. Tạo lịch mới, xem trạng thái chờ xác nhận.
3. Xác nhận lịch, kiểm tra trạng thái chuyển sang đã xác nhận.
4. Tạo lịch trùng giờ cùng kỹ thuật viên, kiểm tra thông báo từ chối.
5. Hủy lịch đã xác nhận, kiểm tra trạng thái đã hủy và nút thao tác biến mất.
6. Lọc lịch theo ngày, trạng thái và kỹ thuật viên.

**Minh chứng:** Người thực hiện cần chụp màn hình thật và lưu tại `docs/` trước khi nộp. Không sử dụng ảnh thiết kế thay cho ảnh chạy ứng dụng.
