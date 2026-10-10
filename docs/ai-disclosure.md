# AI DISCLOSURE

**Sinh viên:** Phạm Công Tài  
**MSSV:** 2374802010445  
**Track:** SE  
**Luồng nghiệp vụ:** L4 – Phân công kỹ thuật viên & lịch hẹn

---

## I. MỤC ĐÍCH KHAI BÁO

Tài liệu này ghi nhận việc sử dụng công cụ AI trong quá trình thực hiện project L4 – Phân công kỹ thuật viên & lịch hẹn.

AI được sử dụng với vai trò hỗ trợ giải thích yêu cầu, gợi ý cấu trúc tài liệu, rà soát tính nhất quán và hỗ trợ xây dựng một số nội dung kỹ thuật. Sinh viên trực tiếp kiểm tra, đối chiếu, chỉnh sửa và chịu trách nhiệm về toàn bộ nội dung trước khi sử dụng và nộp bài.

---

## II. BẢNG KHAI BÁO CHI TIẾT

| Công cụ AI | Dùng vào việc gì | Áp dụng ở phần nào (File / Mục) | Đã kiểm chứng & chỉnh sửa thế nào |
|---|---|---|---|
| ChatGPT | Giải thích yêu cầu bài tập, xác định luồng nghiệp vụ và phạm vi thực hiện | Phiếu phạm vi cá nhân – L4 | Đối chiếu với mô tả L4 do giảng viên cung cấp và đọc lại phạm vi trước khi sử dụng |
| ChatGPT | Gợi ý cách viết User Story và phạm vi chức năng | Phiếu phạm vi; `docs/srs.md` – Mục 3 | Đọc lại từng User Story, đối chiếu với phạm vi L4 và loại bỏ nội dung ngoài phạm vi |
| ChatGPT | Hỗ trợ xây dựng SRS gồm User Story, GWT, FR, NFR, quy tắc nghiệp vụ và bảng truy vết | `docs/srs.md` | Đối chiếu US–FR–Use Case, kiểm tra bảng truy vết không có ô trống và đọc lại các yêu cầu |
| ChatGPT | Hỗ trợ xây dựng sơ đồ Use Case và đặc tả UC4 | `docs/usecase.drawio` | Mở file bằng draw.io, kiểm tra Actor, UC1–UC8, quan hệ và luồng ngoại lệ UC4 |
| ChatGPT | Gợi ý thiết kế API, Request/Response, HTTP Status và Validation | `docs/api-contract.md` | Đối chiếu API1–API8 với US1–US8 và FR1–FR8 trong SRS; kiểm tra JSON và HTTP method |
| ChatGPT | Hỗ trợ trình bày thông tin project và trạng thái thực hiện | `README.md` | Đối chiếu cấu trúc thư mục thực tế và đánh dấu đúng các phần đã/chưa hoàn thành |
| ChatGPT | Gợi ý cấu hình các file hỗ trợ project | `.gitignore`, `.env.example` | Kiểm tra `.env` thật bị loại khỏi Git và `.env.example` chỉ chứa giá trị mẫu |
| ChatGPT | Hỗ trợ đề xuất kiến trúc hệ thống phân lớp và lập luận kiến trúc dựa trên NFR | `docs/architecture.drawio` | Đối chiếu với SRS, API Contract và NFR1–NFR3; kiểm tra các layer, luồng xử lý và phạm vi L4 trước khi sử dụng |
| ChatGPT | Hỗ trợ xây dựng ERD, xác định thực thể, PK/FK và quan hệ dữ liệu | `docs/erd.drawio` | Đối chiếu với BR1–BR6 trong SRS; kiểm tra 4 bảng WARRANTY_TICKET, TECHNICIAN, ASSIGNMENT, APPOINTMENT và các quan hệ trước khi sử dụng |
| ChatGPT | Hỗ trợ xây dựng PostgreSQL DDL từ ERD | `data/schema.sql` | Đối chiếu từng bảng, PK, FK, UNIQUE, CHECK và INDEX với ERD và các quy tắc nghiệp vụ trong SRS |
| ChatGPT | Hỗ trợ phác thảo wireframe cho luồng phân công kỹ thuật viên và lịch hẹn | `docs/wireframe.drawio` | Đối chiếu với UC1–UC8 và SRS; kiểm tra luồng từ phiếu chờ phân công → chọn KTV → tạo và xác nhận lịch hẹn |

---

## III. CAM KẾT

**"Tôi xác nhận đã đọc, hiểu và chịu trách nhiệm về toàn bộ nội dung nộp."**

**Họ và tên:** Phạm Công Tài  
**MSSV:** 2374802010445  
**Ngày:** 10/10/2026