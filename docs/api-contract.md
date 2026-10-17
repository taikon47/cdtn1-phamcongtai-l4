# API CONTRACT – L4: PHÂN CÔNG KỸ THUẬT VIÊN & LỊCH HẸN

**Sinh viên:** Phạm Công Tài  
**MSSV:** 2374802010445  
**Track:** SE  
**Base URL:** `/api/v1`

**Quy ước BT2:** Mỗi kỹ thuật viên có một tay nghề chính (`skill`) theo DDL hiện tại; nếu cần nhiều tay nghề sẽ mở rộng mô hình ở giai đoạn sau.

---

## 1. Mục đích

API phục vụ luồng L4 – Phân công kỹ thuật viên & lịch hẹn, hỗ trợ nhân viên điều phối xem phiếu bảo hành, tìm kỹ thuật viên phù hợp, kiểm tra lịch, phân công kỹ thuật viên, tạo lịch hẹn và kiểm tra trùng lịch.

---

## 2. Danh sách Endpoint

| Mã | Method | Endpoint | Chức năng | Liên kết |
|---|---|---|---|---|
| API1 | GET | `/warranty-tickets?status=UNASSIGNED` | Xem phiếu bảo hành cần phân công | US1 / FR1 |
| API2 | GET | `/technicians` | Xem danh sách và thông tin kỹ thuật viên | US2 / FR2 |
| API3 | GET | `/technicians/{technicianId}/schedule` | Kiểm tra lịch kỹ thuật viên | US3 / FR3 |
| API4 | POST | `/warranty-tickets/{ticketId}/assignments` | Phân công kỹ thuật viên | US4 / FR4 |
| API5 | POST | `/appointments` | Tạo lịch hẹn giao/nhận máy | US5 / FR5 |
| API6 | POST | `/appointments/check-conflict` | Kiểm tra trùng lịch | US6 / FR6 |
| API7 | PATCH | `/appointments/{appointmentId}/confirm` | Xác nhận lịch hẹn và phân công | US7 / FR7 |
| API8 | GET | `/appointments` | Xem danh sách lịch hẹn | US8 / FR8 |

---

## 3. Chi tiết API

### API1 – Xem phiếu bảo hành cần phân công

**Request**

```http
GET /api/v1/warranty-tickets?status=UNASSIGNED
```

**Response – 200 OK**

```json
[
  {
    "ticketId": 1001,
    "customerName": "Nguyen Van A",
    "deviceName": "Smartphone X",
    "issue": "Man hinh khong hien thi",
    "area": "Quan 1",
    "status": "UNASSIGNED"
  }
]
```

**HTTP Status**

| Status | Ý nghĩa |
|---|---|
| 200 | Lấy danh sách thành công |
| 400 | Tham số không hợp lệ |
| 500 | Lỗi hệ thống |

---

### API2 – Xem thông tin kỹ thuật viên

**Request**

```http
GET /api/v1/technicians
```

**Response – 200 OK**

```json
[
  {
    "technicianId": 201,
    "fullName": "Tran Van B",
    "skill": "SCREEN",
    "area": "Quan 1",
    "currentWorkload": 3
  }
]
```

**HTTP Status**

| Status | Ý nghĩa |
|---|---|
| 200 | Lấy danh sách thành công |
| 500 | Lỗi hệ thống |

---

### API3 – Kiểm tra lịch kỹ thuật viên

**Request**

```http
GET /api/v1/technicians/201/schedule?date=2026-10-05
```

**Response – 200 OK**

```json
{
  "technicianId": 201,
  "date": "2026-10-05",
  "appointments": [
    {
      "startTime": "09:00",
      "endTime": "10:00"
    }
  ]
}
```

**HTTP Status**

| Status | Ý nghĩa |
|---|---|
| 200 | Lấy lịch thành công |
| 400 | Ngày không hợp lệ |
| 404 | Không tìm thấy kỹ thuật viên |
| 500 | Lỗi hệ thống |

---

### API4 – Phân công kỹ thuật viên

**Request**

```http
POST /api/v1/warranty-tickets/1001/assignments
Content-Type: application/json
```

```json
{
  "technicianId": 201
}
```

**Response – 201 Created**

```json
{
  "ticketId": 1001,
  "technicianId": 201,
  "status": "ASSIGNED",
  "message": "Phan cong ky thuat vien thanh cong"
}
```

**HTTP Status**

| Status | Ý nghĩa |
|---|---|
| 201 | Phân công thành công |
| 400 | Dữ liệu không hợp lệ |
| 404 | Không tìm thấy phiếu hoặc kỹ thuật viên |
| 409 | Kỹ thuật viên không khả dụng hoặc không đáp ứng điều kiện phân công |
| 500 | Lỗi hệ thống |

---

### API5 – Tạo lịch hẹn giao/nhận máy

**Request**

```http
POST /api/v1/appointments
Content-Type: application/json
```

```json
{
  "ticketId": 1001,
  "technicianId": 201,
  "appointmentType": "RECEIVE",
  "appointmentDate": "2026-10-05",
  "startTime": "10:00",
  "endTime": "11:00"
}
```

**Response – 201 Created**

```json
{
  "appointmentId": 501,
  "ticketId": 1001,
  "technicianId": 201,
  "status": "PENDING",
  "message": "Tao lich hen thanh cong"
}
```

**HTTP Status**

| Status | Ý nghĩa |
|---|---|
| 201 | Tạo lịch thành công |
| 400 | Dữ liệu lịch hẹn không hợp lệ |
| 404 | Không tìm thấy phiếu hoặc kỹ thuật viên |
| 409 | Lịch bị trùng |
| 500 | Lỗi hệ thống |

---

### API6 – Kiểm tra trùng lịch

**Request**

```http
POST /api/v1/appointments/check-conflict
Content-Type: application/json
```

```json
{
  "technicianId": 201,
  "appointmentDate": "2026-10-05",
  "startTime": "10:00",
  "endTime": "11:00"
}
```

**Response – 200 OK**

```json
{
  "conflict": false,
  "message": "Khong phat hien trung lich"
}
```

**Ví dụ khi bị trùng**

```json
{
  "conflict": true,
  "message": "Ky thuat vien da co lich trong khoang thoi gian nay"
}
```

**HTTP Status**

| Status | Ý nghĩa |
|---|---|
| 200 | Kiểm tra thành công |
| 400 | Thời gian không hợp lệ |
| 404 | Không tìm thấy kỹ thuật viên |
| 500 | Lỗi hệ thống |

---

### API7 – Xác nhận lịch hẹn và phân công

**Request**

```http
PATCH /api/v1/appointments/501/confirm
```

**Response – 200 OK**

```json
{
  "appointmentId": 501,
  "status": "CONFIRMED",
  "message": "Xac nhan lich hen thanh cong"
}
```

**HTTP Status**

| Status | Ý nghĩa |
|---|---|
| 200 | Xác nhận thành công |
| 404 | Không tìm thấy lịch hẹn |
| 409 | Lịch không thể xác nhận do xung đột |
| 500 | Lỗi hệ thống |

---

### API8 – Xem danh sách lịch hẹn

**Request**

```http
GET /api/v1/appointments
```

**Response – 200 OK**

```json
[
  {
    "appointmentId": 501,
    "ticketId": 1001,
    "technicianId": 201,
    "appointmentDate": "2026-10-05",
    "startTime": "10:00",
    "endTime": "11:00",
    "status": "CONFIRMED"
  }
]
```

**HTTP Status**

| Status | Ý nghĩa |
|---|---|
| 200 | Lấy danh sách thành công |
| 500 | Lỗi hệ thống |

---

## 4. Validation

| Trường | Kiểu dữ liệu | Bắt buộc | Quy tắc |
|---|---|---|---|
| `ticketId` | Long | Có | Số nguyên dương và phiếu phải tồn tại |
| `technicianId` | Long | Có | Số nguyên dương và kỹ thuật viên phải tồn tại |
| `appointmentType` | String | Có | Chỉ nhận `RECEIVE` hoặc `RETURN` |
| `appointmentDate` | Date | Có | Định dạng `YYYY-MM-DD`, không được trước ngày hiện tại |
| `startTime` | Time | Có | Định dạng `HH:mm` |
| `endTime` | Time | Có | Định dạng `HH:mm` và phải sau `startTime` |
| `status` | String | Tùy API | Chỉ nhận các trạng thái được hệ thống quy định |

---

## 5. Cấu trúc phản hồi lỗi

Các API sử dụng cấu trúc lỗi thống nhất:

```json
{
  "timestamp": "2026-10-03T10:30:00",
  "status": 409,
  "error": "CONFLICT",
  "message": "Ky thuat vien da co lich trong khoang thoi gian nay",
  "path": "/api/v1/appointments"
}
```

---

## 6. Liên kết yêu cầu – API

| User Story | FR | API |
|---|---|---|
| US1 | FR1 | API1 |
| US2 | FR2 | API2 |
| US3 | FR3 | API3 |
| US4 | FR4 | API4 |
| US5 | FR5 | API5 |
| US6 | FR6 | API6 |
| US7 | FR7 | API7 |
| US8 | FR8 | API8 |

## API bổ sung cho quản lý lịch hẹn

### Hủy lịch hẹn
- **Method:** `PATCH`
- **Path:** `/api/v1/appointments/{appointmentId}/cancel`
- **Thành công:** trả về đối tượng lịch hẹn với `status = CANCELLED`.
- **Quy tắc:** không tìm thấy ID trả về 404; lịch `COMPLETED` không thể hủy và trả về 409; lịch đã `CANCELLED` có thể gọi lại an toàn.

### Bộ lọc lịch hẹn
Giao diện lọc danh sách API `GET /api/v1/appointments` theo ngày bắt đầu, trạng thái và kỹ thuật viên. Bộ lọc được áp dụng phía trình duyệt, không sửa dữ liệu hoặc trạng thái trong cơ sở dữ liệu.

### Thống kê tổng quan
Các thẻ thống kê được tính từ dữ liệu API:
- Tổng lịch hẹn đã ghi nhận.
- Lịch chờ xác nhận (`PENDING`).
- Lịch đã xác nhận (`CONFIRMED`).
- Lịch sắp diễn ra: thời điểm bắt đầu chưa qua và trạng thái không phải `CANCELLED` hoặc `COMPLETED`.
