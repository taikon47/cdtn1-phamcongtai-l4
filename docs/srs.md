# SRS RÚT GỌN – L4: PHÂN CÔNG KỸ THUẬT VIÊN & LỊCH HẸN

**Sinh viên:** Phạm Công Tài  
**MSSV:** 2374802010445  
**Track:** SE  
**Luồng nghiệp vụ:** L4 – Phân công kỹ thuật viên & lịch hẹn

---

## 1. GIỚI THIỆU VÀ PHẠM VI

### 1.1. Mục tiêu

Hệ thống hỗ trợ nhân viên điều phối phân công kỹ thuật viên xử lý phiếu bảo hành và quản lý lịch hẹn giao/nhận máy. Việc lựa chọn kỹ thuật viên dựa trên tay nghề, địa bàn, khối lượng công việc hiện tại và lịch làm việc nhằm hạn chế tình trạng phân công không phù hợp hoặc trùng lịch.

### 1.2. Phạm vi

Nhân viên điều phối tiếp nhận phiếu bảo hành cần xử lý, lựa chọn và phân công kỹ thuật viên phù hợp dựa trên tay nghề, địa bàn và khối lượng công việc hiện tại, sau đó đặt lịch hẹn giao/nhận máy, kiểm tra trùng lịch và kết thúc khi lịch hẹn cùng kỹ thuật viên phụ trách được xác nhận.

### 1.3. Ngoài phạm vi

Hệ thống trong phạm vi L4 không thực hiện:

- Quản lý kho linh kiện.
- Xử lý thanh toán.
- Quản lý bán hàng.
- Dự báo khách hàng rời bỏ.
- Tự động phân loại lỗi bằng AI.
- Thực hiện nghiệp vụ sửa chữa thiết bị.

### 1.4. Bảng thuật ngữ

| Thuật ngữ | Giải thích |
|---|---|
| KTV | Kỹ thuật viên |
| Phiếu bảo hành | Phiếu ghi nhận yêu cầu bảo hành cần được xử lý |
| Nhân viên điều phối | Người thực hiện việc lập lịch và phân công kỹ thuật viên |
| Lịch hẹn | Thời gian dự kiến giao/nhận máy hoặc thực hiện công việc liên quan đến phiếu bảo hành |
| Trùng lịch | Hai hoặc nhiều lịch của cùng một kỹ thuật viên bị chồng lấn thời gian |
| Tay nghề | Khả năng/chuyên môn của kỹ thuật viên phù hợp với công việc cần xử lý |
| Địa bàn | Khu vực mà kỹ thuật viên có thể được phân công |
| Khối lượng công việc | Số lượng công việc hiện tại đã được giao cho kỹ thuật viên |

---

## 2. CÁC BÊN LIÊN QUAN VÀ VAI TRÒ

| Bên liên quan | Vai trò |
|---|---|
| Nhân viên điều phối | Xem phiếu bảo hành, xem thông tin kỹ thuật viên, kiểm tra lịch, phân công kỹ thuật viên và tạo lịch hẹn |
| Kỹ thuật viên | Người được phân công xử lý phiếu bảo hành theo lịch đã xác nhận |
| Khách hàng | Người có thiết bị cần bảo hành và có lịch giao/nhận máy |

---

## 3. YÊU CẦU CHỨC NĂNG

### 3.1. User Story

| Mã | User Story | MoSCoW |
|---|---|---|
| US1 | Là nhân viên điều phối, tôi muốn xem các phiếu bảo hành đang cần phân công để xác định các yêu cầu cần bố trí kỹ thuật viên xử lý. | MUST |
| US2 | Là nhân viên điều phối, tôi muốn xem thông tin tay nghề, địa bàn và khối lượng công việc hiện tại của kỹ thuật viên để lựa chọn kỹ thuật viên phù hợp với yêu cầu bảo hành. | MUST |
| US3 | Là nhân viên điều phối, tôi muốn kiểm tra lịch làm việc của kỹ thuật viên để tránh phân công kỹ thuật viên vào khoảng thời gian đã có lịch. | MUST |
| US4 | Là nhân viên điều phối, tôi muốn phân công kỹ thuật viên phù hợp cho phiếu bảo hành để xác định người chịu trách nhiệm xử lý yêu cầu. | MUST |
| US5 | Là nhân viên điều phối, tôi muốn đặt lịch hẹn giao hoặc nhận máy để xác định thời gian giao/nhận thiết bị với khách hàng. | MUST |
| US6 | Là nhân viên điều phối, tôi muốn hệ thống kiểm tra trùng lịch hẹn trước khi xác nhận để tránh xung đột lịch. | MUST |
| US7 | Là nhân viên điều phối, tôi muốn xác nhận lịch hẹn và kết quả phân công kỹ thuật viên để hoàn tất quá trình bố trí kỹ thuật viên xử lý phiếu bảo hành. | SHOULD |
| US8 | Là nhân viên điều phối, tôi muốn xem danh sách lịch hẹn đã tạo để theo dõi các lịch đã được sắp xếp. | COULD |

### 3.2. Acceptance Criteria – Given/When/Then

#### US1 – Xem phiếu bảo hành cần phân công

**AC1**
- Given: Hệ thống có các phiếu bảo hành chưa được phân công.
- When: Nhân viên điều phối mở danh sách phiếu cần phân công.
- Then: Hệ thống hiển thị các phiếu bảo hành chưa được phân công.

**AC2 – Ngoại lệ**
- Given: Không có phiếu bảo hành nào đang chờ phân công.
- When: Nhân viên điều phối mở danh sách.
- Then: Hệ thống hiển thị thông báo không có phiếu cần phân công.

#### US2 – Xem thông tin kỹ thuật viên

**AC1**
- Given: Hệ thống có dữ liệu kỹ thuật viên.
- When: Nhân viên điều phối xem danh sách kỹ thuật viên.
- Then: Hệ thống hiển thị thông tin tay nghề, địa bàn và khối lượng công việc hiện tại.

**AC2 – Ngoại lệ**
- Given: Không có kỹ thuật viên phù hợp với yêu cầu.
- When: Nhân viên điều phối tìm kỹ thuật viên.
- Then: Hệ thống thông báo không tìm thấy kỹ thuật viên phù hợp.

#### US3 – Kiểm tra lịch kỹ thuật viên

**AC1**
- Given: Kỹ thuật viên đã có dữ liệu lịch làm việc.
- When: Nhân viên điều phối chọn kỹ thuật viên và khoảng thời gian.
- Then: Hệ thống hiển thị trạng thái thời gian đó còn trống hay đã có lịch.

**AC2 – Ngoại lệ**
- Given: Kỹ thuật viên đã có lịch trong khoảng thời gian được chọn.
- When: Nhân viên điều phối kiểm tra lịch.
- Then: Hệ thống cảnh báo khoảng thời gian bị trùng.

#### US4 – Phân công kỹ thuật viên

**AC1**
- Given: Phiếu bảo hành chưa có kỹ thuật viên phụ trách và kỹ thuật viên được chọn phù hợp.
- When: Nhân viên điều phối thực hiện phân công.
- Then: Hệ thống lưu kỹ thuật viên phụ trách cho phiếu bảo hành.

**AC2 – Ngoại lệ**
- Given: Kỹ thuật viên không phù hợp hoặc không khả dụng.
- When: Nhân viên điều phối thực hiện phân công.
- Then: Hệ thống không xác nhận phân công và hiển thị thông báo phù hợp.

#### US5 – Đặt lịch hẹn giao/nhận máy

**AC1**
- Given: Phiếu bảo hành đã có thông tin cần thiết.
- When: Nhân viên điều phối nhập ngày, giờ và loại lịch hẹn giao/nhận máy.
- Then: Hệ thống ghi nhận thông tin lịch hẹn.

**AC2 – Ngoại lệ**
- Given: Thời gian lịch hẹn không hợp lệ.
- When: Nhân viên điều phối tạo lịch.
- Then: Hệ thống từ chối lưu và thông báo dữ liệu cần chỉnh sửa.

#### US6 – Kiểm tra trùng lịch

**AC1**
- Given: Kỹ thuật viên đã có một hoặc nhiều lịch hẹn.
- When: Nhân viên điều phối tạo lịch hẹn mới.
- Then: Hệ thống kiểm tra lịch mới có chồng lấn với lịch hiện tại hay không.

**AC2 – Ngoại lệ**
- Given: Lịch mới bị chồng lấn với lịch hiện có.
- When: Nhân viên điều phối xác nhận lịch.
- Then: Hệ thống không cho xác nhận và thông báo trùng lịch.

### 3.3. Yêu cầu chức năng (FR)

| Mã | Yêu cầu chức năng |
|---|---|
| FR1 | Hệ thống cho phép nhân viên điều phối xem danh sách phiếu bảo hành cần phân công. |
| FR2 | Hệ thống cho phép xem thông tin tay nghề, địa bàn và khối lượng công việc hiện tại của kỹ thuật viên. |
| FR3 | Hệ thống cho phép kiểm tra lịch làm việc của kỹ thuật viên. |
| FR4 | Hệ thống cho phép phân công kỹ thuật viên cho phiếu bảo hành. |
| FR5 | Hệ thống cho phép tạo lịch hẹn giao/nhận máy. |
| FR6 | Hệ thống phải kiểm tra trùng lịch trước khi xác nhận lịch hẹn. |
| FR7 | Hệ thống cho phép xác nhận lịch hẹn và kết quả phân công. |
| FR8 | Hệ thống cho phép xem danh sách lịch hẹn đã tạo. |

---

## 4. YÊU CẦU PHI CHỨC NĂNG

| Mã | Yêu cầu phi chức năng |
|---|---|
| NFR1 | 95% yêu cầu xem danh sách phiếu, kỹ thuật viên và lịch hẹn phải có thời gian phản hồi không quá 2 giây trong điều kiện thử nghiệm với 100 bản ghi mô phỏng. |
| NFR2 | Hệ thống phải kiểm tra dữ liệu bắt buộc trước khi lưu và không cho phép xác nhận lịch hẹn nếu phát hiện trùng lịch của cùng một kỹ thuật viên. |
| NFR3 | Các API thuộc phạm vi L4 phải trả về mã trạng thái HTTP phù hợp và phản hồi lỗi có thông báo để xác định nguyên nhân khi dữ liệu đầu vào không hợp lệ. |

---

## 5. RÀNG BUỘC VÀ QUY TẮC NGHIỆP VỤ

### 5.1. Ràng buộc

- Prototype sử dụng Java 21.
- Framework dự kiến: Spring Boot.
- Cơ sở dữ liệu: PostgreSQL.
- Mã nguồn được quản lý bằng Git/GitHub.
- Dữ liệu sử dụng trong prototype là dữ liệu mô phỏng.

### 5.2. Quy tắc nghiệp vụ

**BR1.** Một phiếu bảo hành tại một thời điểm chỉ có một kỹ thuật viên phụ trách trong phạm vi phân công hiện tại.

**BR2.** Kỹ thuật viên được lựa chọn phải phù hợp với yêu cầu về tay nghề và địa bàn của công việc.

**BR3.** Khi lựa chọn kỹ thuật viên, nhân viên điều phối phải có thông tin về khối lượng công việc hiện tại của kỹ thuật viên.

**BR4.** Không được xác nhận hai lịch bị chồng lấn thời gian cho cùng một kỹ thuật viên.

**BR5.** Lịch hẹn phải gắn với phiếu bảo hành và kỹ thuật viên được phân công.

**BR6.** Chỉ xác nhận kết quả phân công khi các thông tin bắt buộc của lịch hẹn hợp lệ.

---

## 6. BẢNG TRUY VẾT YÊU CẦU

| FR | User Story | Use Case | MoSCoW |
|---|---|---|---|
| FR1 | US1 | UC1 – Xem phiếu bảo hành cần phân công | MUST |
| FR2 | US2 | UC2 – Xem thông tin kỹ thuật viên | MUST |
| FR3 | US3 | UC3 – Kiểm tra lịch kỹ thuật viên | MUST |
| FR4 | US4 | UC4 – Phân công kỹ thuật viên | MUST |
| FR5 | US5 | UC5 – Tạo lịch hẹn giao/nhận máy | MUST |
| FR6 | US6 | UC6 – Kiểm tra trùng lịch | MUST |
| FR7 | US7 | UC7 – Xác nhận lịch hẹn và phân công | SHOULD |
| FR8 | US8 | UC8 – Xem danh sách lịch hẹn | COULD |s