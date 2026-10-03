# SMART CRM – MEKONG MOBILE

## L4 – Phân công kỹ thuật viên & lịch hẹn

**Sinh viên:** Phạm Công Tài  
**MSSV:** 2374802010445  
**Track:** SE  
**Học phần:** Chuyên đề Tốt nghiệp 1  
**Học kỳ:** HK1 – Năm học 2026–2027  

---

## 1. Mục tiêu

Xây dựng chức năng hỗ trợ nhân viên điều phối phân công kỹ thuật viên và quản lý lịch hẹn trong hệ thống Smart CRM – Mekong Mobile.

Hệ thống hỗ trợ:

- Xem các phiếu bảo hành đang cần phân công.
- Xem thông tin tay nghề, địa bàn và khối lượng công việc của kỹ thuật viên.
- Kiểm tra lịch làm việc của kỹ thuật viên.
- Phân công kỹ thuật viên cho phiếu bảo hành.
- Tạo lịch hẹn giao/nhận máy.
- Kiểm tra và ngăn chặn trùng lịch.
- Xác nhận lịch hẹn và kết quả phân công.
- Xem danh sách lịch hẹn.

---

## 2. Yêu cầu môi trường

### Công nghệ dự kiến

| Thành phần | Công nghệ |
|---|---|
| Ngôn ngữ | Java 21 |
| Framework | Spring Boot |
| Cơ sở dữ liệu | PostgreSQL |
| Quản lý mã nguồn | Git / GitHub |
| Thiết kế sơ đồ | draw.io |
| IDE | Visual Studio Code |

### Yêu cầu cài đặt

- JDK 21
- PostgreSQL
- Git
- Visual Studio Code hoặc IDE hỗ trợ Java
- Trình duyệt web
- draw.io / diagrams.net

---

## 3. Phạm vi

Nhân viên điều phối tiếp nhận phiếu bảo hành cần xử lý, lựa chọn và phân công kỹ thuật viên phù hợp dựa trên tay nghề, địa bàn và khối lượng công việc hiện tại, sau đó đặt lịch hẹn giao/nhận máy, kiểm tra trùng lịch và kết thúc khi lịch hẹn cùng kỹ thuật viên phụ trách được xác nhận.

---

## 4. Cấu trúc Repository

```text
PROJECTL4/
│
├── data/
├── docs/
│   ├── ai-disclosure.md
│   ├── api-contract.md
│   ├── srs.md
│   └── usecase.drawio
│
├── src/
├── tests/
│
├── .env.example
├── .gitignore
└── README.md
```

### Ý nghĩa

- `docs/`: tài liệu phân tích và đặc tả.
- `src/`: mã nguồn chương trình.
- `tests/`: mã kiểm thử.
- `data/`: dữ liệu mô phỏng.
- `.env.example`: mẫu các biến môi trường.
- `.gitignore`: các file/thư mục không đưa lên Git.
- `README.md`: giới thiệu và trạng thái project.

---

## 5. Tài liệu

| File | Nội dung |
|---|---|
| `docs/srs.md` | Đặc tả yêu cầu phần mềm rút gọn |
| `docs/usecase.drawio` | Use Case Diagram và đặc tả UC4 |
| `docs/api-contract.md` | Hợp đồng API dành cho Track SE |
| `docs/ai-disclosure.md` | Khai báo sử dụng công cụ AI |

---

## 6. Trạng thái hiện tại

### Đã hoàn thành

- [x] Chọn Track SE.
- [x] Chọn luồng L4 – Phân công kỹ thuật viên & lịch hẹn.
- [x] Xác định phạm vi bài toán.
- [x] Xây dựng User Story.
- [x] Phân loại yêu cầu theo MoSCoW.
- [x] Viết Acceptance Criteria theo Given–When–Then.
- [x] Xác định FR và NFR.
- [x] Hoàn thành SRS rút gọn.
- [x] Hoàn thành Use Case Diagram.
- [x] Đặc tả chi tiết UC4 – Phân công kỹ thuật viên.
- [x] Xây dựng API Contract.
- [x] Tạo bảng khai báo sử dụng AI.

### Chưa hoàn thành

- [ ] Xây dựng mã nguồn Spring Boot.
- [ ] Thiết kế và khởi tạo cơ sở dữ liệu PostgreSQL.
- [ ] Cài đặt các API.
- [ ] Viết unit test/integration test.
- [ ] Kiểm thử chức năng với dữ liệu mô phỏng.

---

## 7. Dữ liệu

Project dự kiến sử dụng khoảng **100 bản ghi dữ liệu mô phỏng** phục vụ quá trình phát triển và kiểm thử.

---

## 8. Ghi chú

Các tài liệu yêu cầu và thiết kế có thể tiếp tục được cập nhật trong các buổi tiếp theo của học phần.

Việc sử dụng công cụ AI được khai báo tại:

`docs/ai-disclosure.md`