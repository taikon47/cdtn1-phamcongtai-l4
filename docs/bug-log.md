# BUG LOG – BT2 – SMART CRM L4

## BUG-01 – Không chặn lịch hẹn có thời gian bắt đầu trong quá khứ

- Mức độ: Medium
- Trạng thái: FIXED
- File lỗi: L4ApiController.java
- File kiểm thử: L4ApiControllerBusinessTests.java
- Test case: sameDayAppointmentInPastIsRejected

### Kết quả trước khi sửa
- Tests run: 9
- PASS: 8
- FAIL: 1
- Lỗi: API chấp nhận lịch có thời gian bắt đầu đã qua trong ngày hiện tại.

### Nguyên nhân
Logic cũ chỉ kiểm tra ngày hẹn có nằm trong quá khứ hay không, chưa kiểm tra thời gian bắt đầu.

### Cách sửa
Bổ sung kiểm tra LocalDateTime giữa ngày hẹn và giờ bắt đầu với thời gian hiện tại.

### Kết quả sau khi sửa
- Tests run: 9
- PASS: 9
- FAIL: 0
- ERROR: 0
- BUILD SUCCESS
