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
- Xác nhận hoặc hủy lịch hẹn; không cho hủy lịch đã hoàn tất.
- Lọc danh sách lịch hẹn theo ngày, trạng thái và kỹ thuật viên.
- Theo dõi thống kê tổng quan: phiếu chờ phân công, kỹ thuật viên, tổng lịch, lịch chờ xác nhận, lịch đã xác nhận và lịch sắp diễn ra.
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

## 6. Chạy nhanh sau khi giải nén

Yêu cầu: JDK 21, PostgreSQL đang chạy, database `projectl4` đã được khởi tạo và cấu hình kết nối đúng trong `src/main/resources/application.properties`.

Mở PowerShell tại thư mục `PROJECTL4` và chạy một lệnh:

```powershell
powershell -ExecutionPolicy Bypass -File .\kiem-tra-va-chay.ps1
```

Script sẽ chạy `clean test package`; nếu kiểm thử thành công, ứng dụng sẽ khởi động bằng Spring Boot. Mở `http://localhost:8081/`. Nếu cổng 8081 đã được dùng, hãy đóng ứng dụng đang chạy trước khi chạy lại script.

## 7. Trạng thái hiện tại

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

### Các phần bổ sung trong bản cập nhật L4

- **Hủy lịch hẹn:** `PATCH /api/v1/appointments/{appointmentId}/cancel`; lịch đã hoàn tất không thể bị hủy.
- **Lọc lịch hẹn:** lọc tại giao diện theo ngày, trạng thái và kỹ thuật viên; không thay đổi dữ liệu gốc từ API.
- **Thống kê tổng quan:** tổng số lịch, lịch chờ xác nhận, lịch đã xác nhận và lịch sắp diễn ra được tính từ dữ liệu API thật.
- **Phản hồi giao diện:** có xác nhận trước khi hủy, trạng thái đang xử lý và thông báo thành công/lỗi.

Lưu ý: cần chạy ứng dụng với PostgreSQL của máy và tự chụp ảnh màn hình thực tế để bổ sung vào báo cáo trước khi nộp. Không xem các ảnh placeholder trong bản báo cáo là bằng chứng kiểm thử.

## 9. Kiểm thử BT2 (bản bàn giao)

Chạy `./mvnw.cmd clean test` trên Windows sau khi đặt `JAVA_HOME` trỏ tới JDK 21. Thư mục `src/test/java` chứa bài kiểm tra khởi động Spring Boot và 8 bài kiểm tra quy tắc nghiệp vụ độc lập với database: tạo lịch hợp lệ, khoảng giờ sát nhau, trùng giờ, giờ không hợp lệ, ngày quá khứ, hủy lịch đã xác nhận, từ chối hủy lịch hoàn tất và xác nhận lịch chờ.

**Lưu ý:** Bài kiểm tra sát giờ dùng repository mock để mô phỏng không trùng lịch; chưa phải integration test xác minh truy vấn SQL thực tế. Test khởi động ứng dụng yêu cầu PostgreSQL theo `application.properties`. Cần chạy lại trên máy nộp bài và lưu log thực tế.

**Đính chính tiến độ:** Các checklist "chưa hoàn thành" tại mục 7 phía trên phản ánh trạng thái BT1 trước khi triển khai. Trong BT2 hiện đã có Java/Spring Boot, PostgreSQL/Flyway, REST API và bộ test tại `src/test/java`.
