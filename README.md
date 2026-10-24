# SMART CRM – MEKONG MOBILE

## L4 – Phân công kỹ thuật viên & lịch hẹn

**Sinh viên:** Phạm Công Tài  
**MSSV:** 2374802010445  
**Track:** Software Engineering (SE)  
**Học phần:** Chuyên đề Tốt nghiệp 1  
**Học kỳ:** HK1 – Năm học 2026–2027

---

## 1. Mục tiêu dự án

Smart CRM – Mekong Mobile hỗ trợ quy trình tiếp nhận và xử lý phiếu bảo hành.

Luồng nghiệp vụ chính:

1. Xem danh sách phiếu bảo hành chưa được phân công.
2. Xem chi tiết phiếu bảo hành.
3. Xem danh sách kỹ thuật viên.
4. Chọn và phân công kỹ thuật viên phù hợp.
5. Tạo và quản lý lịch hẹn.
6. Kiểm tra xung đột lịch.
7. Xác nhận hoặc hủy lịch hẹn.

Trong Sprint 2, hệ thống được bổ sung frontend React và tích hợp với REST API Spring Boot để thực hiện luồng nghiệp vụ End-to-End trên dữ liệu PostgreSQL thực tế.

---

## 2. Công nghệ sử dụng

### Backend

- Java 21
- Spring Boot
- Spring Data JPA
- Maven
- Flyway

### Database

- PostgreSQL 17

### Frontend

- React 19
- Vite
- JavaScript
- HTML/CSS
- Oxlint

### Kiểm thử

- Spring Boot Test
- JUnit
- Maven Test
- Oxlint
- Vite Production Build

---

## 3. Hướng dẫn cài đặt và chạy hệ thống

### 3.1. Yêu cầu môi trường

Cài đặt các công cụ sau:

- JDK 21
- PostgreSQL 17
- Node.js
- npm

Database sử dụng:

`projectl4`

### 3.2. Clone repository

```powershell
git clone https://github.com/taikon47/cdtn1-phamcongtai-l4.git
```

### 3.3. Di chuyển vào thư mục project

```powershell
cd cdtn1-phamcongtai-l4
```

### 3.4. Kiểm tra Java

```powershell
java -version
```

### 3.5. Kiểm tra PostgreSQL

```powershell
psql --version
```

Nếu `psql` chưa được thêm vào PATH trên Windows, có thể kiểm tra trực tiếp:

```powershell
& "C:\Program Files\PostgreSQL\17\bin\psql.exe" --version
```

### 3.6. Chạy kiểm thử backend

```powershell
.\mvnw.cmd clean test
```

Kết quả kiểm thử gần nhất:

```text
Tests run: 9
Failures: 0
Errors: 0
Skipped: 0
BUILD SUCCESS
```

### 3.7. Khởi động backend

```powershell
.\mvnw.cmd spring-boot:run
```

Backend chạy tại:

`http://localhost:8081`

Giữ terminal backend đang chạy.

### 3.8. Mở terminal thứ hai và vào frontend

```powershell
cd frontend
```

### 3.9. Cài đặt dependency frontend

```powershell
npm install
```

### 3.10. Kiểm tra lint frontend

```powershell
npm run lint
```

Kết quả kiểm tra gần nhất:

```text
Found 0 warnings and 0 errors.
```

### 3.11. Build frontend

```powershell
npm run build
```

Kết quả production build gần nhất: **PASS**.

### 3.12. Khởi động frontend

```powershell
npm run dev
```

Frontend chạy tại:

`http://localhost:5173`

Vite proxy các request `/api` sang Spring Boot tại `http://localhost:8081`.

---

## 4. Cấu trúc Repository

```text
PROJECTL4/
├── data/
├── docs/
│   ├── ai-disclosure.md
│   ├── api-contract.md
│   ├── architecture.drawio
│   ├── erd.drawio
│   ├── kiem-thu-bt2.md
│   ├── srs.md
│   ├── thay-doi-thiet-ke.md
│   ├── usecase.drawio
│   └── wireframe.drawio
│
├── frontend/
│   ├── src/
│   │   ├── api/
│   │   │   └── client.js
│   │   ├── components/
│   │   │   └── DataState.jsx
│   │   ├── pages/
│   │   │   ├── TicketList.jsx
│   │   │   ├── TicketDetail.jsx
│   │   │   └── TicketReceive.jsx
│   │   ├── App.jsx
│   │   └── main.jsx
│   ├── package.json
│   └── vite.config.js
│
├── src/
│   ├── main/
│   └── test/
├── tests/
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

---

## 5. Chức năng chính

### 5.1. Backend REST API

Hệ thống hiện hỗ trợ:

- Lấy danh sách phiếu bảo hành.
- Lấy danh sách kỹ thuật viên.
- Lấy thông tin kỹ thuật viên.
- Xem lịch làm việc của kỹ thuật viên.
- Phân công kỹ thuật viên cho phiếu bảo hành.
- Kiểm tra xung đột lịch.
- Tạo lịch hẹn.
- Xem danh sách lịch hẹn.
- Xác nhận lịch hẹn.
- Hủy lịch hẹn.

### 5.2. Frontend Sprint 2

Frontend React gồm 3 màn hình chính:

1. **Danh sách** – hiển thị các phiếu bảo hành chưa được phân công.
2. **Chi tiết** – hiển thị thông tin chi tiết của phiếu được chọn.
3. **Tiếp nhận** – lựa chọn và phân công kỹ thuật viên phù hợp.

Các request HTTP của frontend được tập trung tại:

`frontend/src/api/client.js`

Component:

`frontend/src/components/DataState.jsx`

xử lý các trạng thái dữ liệu:

- Loading
- Empty
- Error

Điều này giúp giao diện không bị treo hoặc hiển thị sai khi API đang tải dữ liệu, không có dữ liệu hoặc xảy ra lỗi.

---

## 6. Tiến độ hiện tại

### Đã hoàn thành

- [x] Xây dựng backend Java Spring Boot.
- [x] Kết nối PostgreSQL.
- [x] Quản lý schema bằng Flyway.
- [x] Xây dựng REST API cho module L4.
- [x] Quản lý phiếu bảo hành.
- [x] Quản lý kỹ thuật viên.
- [x] Phân công kỹ thuật viên.
- [x] Kiểm tra điều kiện nghiệp vụ khi phân công.
- [x] Tạo và quản lý lịch hẹn.
- [x] Kiểm tra xung đột lịch.
- [x] Xác nhận và hủy lịch hẹn.
- [x] Xây dựng frontend bằng React + Vite.
- [x] Xây dựng 3 màn hình Danh sách / Chi tiết / Tiếp nhận.
- [x] Kết nối frontend với REST API thật qua `src/api/client.js`.
- [x] Xây dựng `DataState` cho Loading / Empty / Error.
- [x] Kiểm tra Empty state.
- [x] Kiểm tra Error/Conflict flow.
- [x] Kiểm tra End-to-End với PostgreSQL thực tế.
- [x] Frontend lint: 0 warnings, 0 errors.
- [x] Frontend production build PASS.
- [x] Backend: 9 tests PASS.

### Kết quả End-to-End

Luồng nghiệp vụ đã được kiểm tra:

```text
PostgreSQL
    ↓
Spring Boot REST API
    ↓
React – Danh sách
    ↓
React – Chi tiết
    ↓
React – Tiếp nhận
    ↓
Phân công kỹ thuật viên
    ↓
Spring Boot cập nhật PostgreSQL
```

Kết quả kiểm chứng:

- Phiếu: `WT-2026-003`
- Trạng thái: `UNASSIGNED → ASSIGNED`
- `ticket_id = 3`
- `technician_id = 3`
- Assignment status: `ASSIGNED`
- Kỹ thuật viên: `Le Minh Khoa`
- `current_workload = 1`

Luồng ngoại lệ cũng đã được kiểm tra: khi lựa chọn kỹ thuật viên không phù hợp với điều kiện nghiệp vụ, backend trả về Conflict và frontend hiển thị trạng thái Error thay vì bị treo.

---

## 7. Kiểm thử

### 7.1. Backend

Lệnh kiểm thử:

```powershell
.\mvnw.cmd clean test
```

Kết quả gần nhất:

```text
Tests run: 9
Failures: 0
Errors: 0
Skipped: 0
BUILD SUCCESS
```

Trong đó có 8 bài kiểm tra quy tắc nghiệp vụ và 1 bài kiểm tra Spring context.

Bộ kiểm thử bao gồm luồng hợp lệ và các trường hợp biên/ngoại lệ của module L4.

### 7.2. Frontend lint

```powershell
npm run lint
```

Kết quả:

```text
Found 0 warnings and 0 errors.
```

### 7.3. Frontend production build

```powershell
npm run build
```

Kết quả: **PASS**.

---

## 8. Tài liệu và minh chứng

Các tài liệu được lưu tại thư mục `docs/`, bao gồm:

- SRS
- API Contract
- Architecture Diagram
- ERD
- Wireframe
- Use Case
- Nhật ký thay đổi thiết kế
- Kiểm thử BT2
- AI Disclosure
- Ảnh minh chứng Sprint 2

Ảnh minh chứng Sprint 2 cần thể hiện đầy đủ luồng:

1. Màn hình danh sách phiếu bảo hành.
2. Màn hình chi tiết phiếu.
3. Màn hình tiếp nhận và phân công.
4. Kết quả End-to-End sau khi phân công thành công.

---

## 9. Git Convention

Repository sử dụng Conventional Commits.

Ví dụ:

```text
feat: add React warranty workflow
fix: handle frontend data states
test: verify L4 business rules
docs: update Sprint 2 progress
```

Repository:

`https://github.com/taikon47/cdtn1-phamcongtai-l4`

---

## 10. Ghi chú

Không commit các thư mục được sinh tự động:

```text
target/
frontend/node_modules/
frontend/dist/
frontend/.vite/
```

Khai báo sử dụng công cụ AI được lưu tại:

`docs/ai-disclosure.md`