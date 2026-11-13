# REST API Documentation — Smart CRM Mekong Mobile (Flow L4)

- **Module:** L4 – Technician Assignment & Appointment Scheduling
- **Base URL:** `http://localhost:8081`
- **Protocol:** HTTP / JSON
- **Authentication:** Internal Dispatcher Role / Prototype session (Header: `X-User-Role: DISPATCHER`)

---

## 1. System Healthcheck Endpoint

### `GET /health`
1. **Method & Path:** `GET /health`
2. **Business Description:** Verifies that the Spring Boot application is alive and can successfully execute queries against the PostgreSQL database.
3. **Authentication:** None required (Public for Docker daemon & monitoring).
4. **Parameters & Request Body:** None.
5. **Success Response (200 OK):**
   ```json
   {
     "status": "ok",
     "db": "up"
   }
   ```
6. **Possible Error Codes:**
   - `503 Service Unavailable`: When database connection pool is exhausted or database container is down.
7. **Special Notes:** Used directly by the Docker Compose `HEALTHCHECK` directive.

---

## 2. Warranty Ticket Endpoints

### `GET /api/v1/warranty-tickets`
1. **Method & Path:** `GET /api/v1/warranty-tickets`
2. **Business Description:** Retrieves all warranty tickets filtered by status (defaulting to unassigned tickets waiting for dispatch).
3. **Authentication:** Dispatcher role.
4. **Parameters:**
   | Parameter | Type | In | Required | Description |
   |:---|:---:|:---:|:---:|:---|
   | `status` | String | Query | No | Ticket status (`UNASSIGNED`, `ASSIGNED`, `COMPLETED`). Default: `UNASSIGNED`. |
5. **Success Response (200 OK):**
   ```json
   [
     {
       "ticketId": 1,
       "ticketCode": "TCK-2026-0001",
       "customerName": "Nguyễn Văn Khách",
       "deviceName": "iPhone 13 Pro",
       "issueDescription": "Màn hình chớp nháy, liệt cảm ứng góc phải",
       "requiredSkill": "Screen",
       "serviceArea": "Can Tho",
       "status": "UNASSIGNED",
       "createdAt": "2026-11-13T22:30:00"
     },
     {
       "ticketId": 2,
       "ticketCode": "TCK-2026-0002",
       "customerName": "Lê Thị Mai",
       "deviceName": "Samsung Galaxy S23",
       "issueDescription": "Pin sạc không vào, máy nóng bất thường",
       "requiredSkill": "Battery",
       "serviceArea": "Can Tho",
       "status": "UNASSIGNED",
       "createdAt": "2026-11-13T22:30:00"
     }
   ]
   ```
6. **Possible Error Codes:**
   - `400 Bad Request`: If status filter contains invalid enum values.
7. **Special Notes:** Returns an empty JSON list `[]` if no tickets match the requested status filter.

---

### `POST /api/v1/warranty-tickets/{ticketId}/assignments`
1. **Method & Path:** `POST /api/v1/warranty-tickets/{ticketId}/assignments`
2. **Business Description:** Assigns a qualified technician to a pending warranty ticket, increments the technician's active workload, and sets ticket status to `ASSIGNED`.
3. **Authentication:** Dispatcher role.
4. **Parameters & Request Body:**
   - **Path Variable:** `ticketId` (Long, required): ID of the ticket to be assigned.
   - **Request Body (JSON):**
     | Field | Type | Required | Constraints |
     |:---|:---:|:---:|:---|
     | `technicianId` | Long | Yes | Must reference an existing, available technician. |
     ```json
     {
       "technicianId": 2
     }
     ```
5. **Success Response (201 Created):**
   ```json
   {
     "assignmentId": 2,
     "ticketId": 1,
     "technicianId": 2,
     "assignedAt": "2026-11-13T23:05:00",
     "status": "ASSIGNED"
   }
   ```
6. **Possible Error Codes:**
   - `400 Bad Request`: Missing `technicianId` or body payload.
   - `404 Not Found`: Ticket or Technician does not exist.
   - `409 Conflict`: Ticket is already assigned; technician is BUSY; technician skill does not match; or technician service area does not match ticket area.
7. **Special Notes:** Executed inside an atomic database transaction. If any compatibility check fails, no database mutation occurs.

---

## 3. Technician Endpoints

### `GET /api/v1/technicians`
1. **Method & Path:** `GET /api/v1/technicians`
2. **Business Description:** Lists all technicians along with their skills, service areas, availability, and current active workloads.
3. **Authentication:** Dispatcher role.
4. **Parameters:** None.
5. **Success Response (200 OK):**
   ```json
   [
     {
       "technicianId": 1,
       "fullName": "Nguyễn Văn An",
       "skill": "Hardware",
       "serviceArea": "Quan 1",
       "currentWorkload": 1,
       "availabilityStatus": "AVAILABLE",
       "createdAt": "2026-11-13T22:30:00"
     },
     {
       "technicianId": 2,
       "fullName": "Trần Thị Bích",
       "skill": "Software",
       "serviceArea": "Quan 1",
       "currentWorkload": 0,
       "availabilityStatus": "AVAILABLE",
       "createdAt": "2026-11-13T22:30:00"
     }
   ]
   ```
6. **Possible Error Codes:**
   - `500 Internal Server Error`: Database query failure.
7. **Special Notes:** Used by the dispatcher frontend to render technician selection cards and workload gauges.

---

### `GET /api/v1/technicians/{technicianId}/schedule`
1. **Method & Path:** `GET /api/v1/technicians/{technicianId}/schedule`
2. **Business Description:** Retrieves all active appointments (`PENDING` or `CONFIRMED`) booked for a specific technician to avoid double-booking.
3. **Authentication:** Dispatcher role.
4. **Parameters:**
   - **Path Variable:** `technicianId` (Long, required).
5. **Success Response (200 OK):**
   ```json
   [
     {
       "appointmentId": 1,
       "ticketId": 5,
       "technicianId": 3,
       "appointmentType": "RECEIVE",
       "startTime": "2026-11-14T09:00:00",
       "endTime": "2026-11-14T10:30:00",
       "status": "CONFIRMED",
       "createdAt": "2026-11-13T22:30:00"
     }
   ]
   ```
6. **Possible Error Codes:**
   - `404 Not Found`: If technician does not exist.
7. **Special Notes:** Cancelled appointments (`CANCELLED`) are filtered out to keep calendar views uncluttered.

---

## 4. Appointment Scheduling Endpoints

### `POST /api/v1/appointments`
1. **Method & Path:** `POST /api/v1/appointments`
2. **Business Description:** Books a new receiving or returning appointment with customer; verifies non-overlapping time slot for the selected technician.
3. **Authentication:** Dispatcher role.
4. **Parameters & Request Body:**
   | Field | Type | Required | Constraints |
   |:---|:---:|:---:|:---|
   | `ticketId` | Long | Yes | Must be a valid ticket ID |
   | `technicianId` | Long | Yes | Must be a valid technician ID |
   | `appointmentType` | String | Yes | `RECEIVE` or `RETURN` |
   | `appointmentDate` | LocalDate | Yes | Must be today or in the future |
   | `startTime` | LocalTime | Yes | Must be before `endTime` |
   | `endTime` | LocalTime | Yes | Must be after `startTime` |

   **Request Body (JSON):**
   ```json
   {
     "ticketId": 1,
     "technicianId": 2,
     "appointmentType": "RECEIVE",
     "appointmentDate": "2026-11-15",
     "startTime": "09:00:00",
     "endTime": "10:00:00"
   }
   ```
5. **Success Response (201 Created):**
   ```json
   {
     "appointmentId": 3,
     "ticketId": 1,
     "technicianId": 2,
     "appointmentType": "RECEIVE",
     "startTime": "2026-11-15T09:00:00",
     "endTime": "2026-11-15T10:00:00",
     "status": "PENDING",
     "createdAt": "2026-11-13T23:10:00"
   }
   ```
6. **Possible Error Codes:**
   - `400 Bad Request`: `startTime` $\ge$ `endTime`, appointment time is in the past, or invalid type.
   - `404 Not Found`: Ticket or Technician does not exist.
   - `409 Conflict`: Technician has an overlapping appointment in the requested interval.
7. **Special Notes:** Newly created appointments default to `PENDING` until finalized with customer.

---

### `POST /api/v1/appointments/check-conflict`
1. **Method & Path:** `POST /api/v1/appointments/check-conflict`
2. **Business Description:** Pre-validation endpoint to check whether an appointment slot conflicts with a technician's existing schedule without persisting any data (Idempotent).
3. **Authentication:** Dispatcher role.
4. **Parameters & Request Body:** Same body schema as `POST /api/v1/appointments`.
5. **Success Response (200 OK):**
   ```json
   {
     "conflict": false,
     "message": "Không có lịch hẹn bị trùng."
   }
   ```
   *(Or if conflict detected)*
   ```json
   {
     "conflict": true,
     "message": "Kỹ thuật viên đã có lịch hẹn trong khoảng thời gian này."
   }
   ```
6. **Possible Error Codes:**
   - `400 Bad Request`: End time before start time, or date in the past.
7. **Special Notes:** Calling this endpoint multiple times produces identical results and causes zero database side-effects (validated in test `TC-12`).

---

### `PATCH /api/v1/appointments/{appointmentId}/confirm`
1. **Method & Path:** `PATCH /api/v1/appointments/{appointmentId}/confirm`
2. **Business Description:** Confirms a `PENDING` appointment after customer verbal/SMS confirmation.
3. **Authentication:** Dispatcher role.
4. **Parameters:** `appointmentId` (Long, Path Variable).
5. **Success Response (200 OK):**
   ```json
   {
     "appointmentId": 2,
     "ticketId": 4,
     "technicianId": 1,
     "appointmentType": "RECEIVE",
     "startTime": "2026-11-15T14:00:00",
     "endTime": "2026-11-15T15:00:00",
     "status": "CONFIRMED",
     "createdAt": "2026-11-13T22:30:00"
   }
   ```
6. **Possible Error Codes:**
   - `404 Not Found`: Appointment does not exist.
   - `409 Conflict`: Appointment has already been cancelled or completed.
7. **Special Notes:** Only `PENDING` appointments can transition to `CONFIRMED`.

---

### `PATCH /api/v1/appointments/{appointmentId}/cancel`
1. **Method & Path:** `PATCH /api/v1/appointments/{appointmentId}/cancel`
2. **Business Description:** Cancels an active appointment (`PENDING` or `CONFIRMED`) upon customer cancellation or device withdrawal.
3. **Authentication:** Dispatcher role.
4. **Parameters:** `appointmentId` (Long, Path Variable).
5. **Success Response (200 OK):**
   ```json
   {
     "appointmentId": 2,
     "ticketId": 4,
     "technicianId": 1,
     "appointmentType": "RECEIVE",
     "startTime": "2026-11-15T14:00:00",
     "endTime": "2026-11-15T15:00:00",
     "status": "CANCELLED",
     "createdAt": "2026-11-13T22:30:00"
   }
   ```
6. **Possible Error Codes:**
   - `404 Not Found`: Appointment ID does not exist.
   - `409 Conflict`: Attempting to cancel an appointment that was already `COMPLETED`.
7. **Special Notes:** Freeing up the technician slot immediately allows other appointments to be scheduled in that timeframe.
