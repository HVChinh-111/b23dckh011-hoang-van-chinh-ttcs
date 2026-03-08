# Use Case Diagram

## Tổng quan: Use Case Diagram là gì?

Use Case Diagram (Biểu đồ Use Case) là một loại biểu đồ trong UML (Unified Modeling Language) dùng để mô tả **sự tương tác giữa người dùng (hoặc các hệ thống khác) với phần mềm**. Nó không quan tâm đến việc hệ thống hoạt động bên trong như thế nào (không có code hay luồng dữ liệu chi tiết), mà chỉ tập trung vào trả lời hai câu hỏi:

1. **Ai** sử dụng hệ thống?
2. Hệ thống cung cấp **chức năng gì** cho họ?

## I. Các thành phần chính trong Use Case Diagram

### 1. System (Hệ thống)

System là thứ bạn đang xây dựng (website, app, phần mềm, quy trình kinh doanh...).

* **Cách biểu diễn:** Được vẽ bằng một hình chữ nhật lớn (gọi là System Boundary - Ranh giới hệ thống). Tên của hệ thống được đặt ở trên cùng bên trong hình chữ nhật.
* **Ý nghĩa:** Hình chữ nhật này vạch ra ranh giới rõ ràng: những gì nằm **bên trong** là chức năng mà phần mềm của bạn phải làm, những gì nằm **bên ngoài** là những yếu tố ngoại cảnh tương tác với phần mềm.

*Ví dụ:* Một hình chữ nhật lớn mang tên "Hệ thống Quản lý Ngân hàng Máu".

### 2. Actor (Tác nhân)

#### 2.1. Khái niệm

Actor là bất cứ ai hoặc bất cứ thứ gì tương tác với hệ thống. Đó có thể là:

* **Con người:** Người dùng trực tiếp thao tác (Ví dụ: Người hiến máu, Nhân viên y tế, Quản trị viên).
* **Hệ thống/Phần mềm khác:** Một hệ thống bên ngoài tự động giao tiếp với phần mềm của bạn (Ví dụ: Cổng thanh toán ngân hàng, Hệ thống gửi tin nhắn SMS tự động).
* **Thiết bị phần cứng:** (Ví dụ: Máy đo huyết áp tự động truyền dữ liệu vào hệ thống).

#### 2.2. Lưu ý quan trọng

* **Luôn nằm ngoài hệ thống:** Actor KHÔNG phải là một phần của phần mềm bạn đang viết. Họ đứng ngoài (ngoài hình chữ nhật System Boundary) và gọi các chức năng bên trong.
* **Là một "Vai trò" (Role), không phải cá nhân cụ thể:** Bạn không đặt tên actor là "Bác sĩ Nguyễn Văn A", mà phải đặt là "Bác sĩ" hoặc "Nhân viên y tế". Một người thực tế có thể đóng nhiều vai trò (actor) khác nhau tùy thời điểm.

#### 2.3. Các loại Actor

* **Primary Actors (Tác nhân chính):** Thường được đặt ở **bên trái** hệ thống. Đây là những người/hệ thống **chủ động khởi tạo** một Use Case để đạt được mục tiêu của họ.
* *Ví dụ:* "Người hiến máu" (Donor) chủ động đăng nhập vào hệ thống để đặt lịch hiến máu.


* **Secondary Actors (Tác nhân phụ):** Thường được đặt ở **bên phải** hệ thống. Đây là những người/hệ thống bị hệ thống của bạn gọi đến để **nhờ hỗ trợ hoặc cung cấp dịch vụ**.
* *Ví dụ:* Khi có người đặt lịch hiến máu thành công, hệ thống của bạn gọi đến "Hệ thống gửi SMS" (SMS Gateway) để nhờ nhắn tin xác nhận. SMS Gateway ở đây là Secondary Actor.



### 3. Use Case (Ca sử dụng / Chức năng)

Use Case đại diện cho một chức năng cụ thể hoặc một hành động mà hệ thống cung cấp để mang lại giá trị cho Actor.

* **Cách biểu diễn:** Hình bầu dục nằm bên trong System Boundary, với tên Use Case viết bên trong.
* **Cách đặt tên:** Bắt buộc phải bắt đầu bằng một **Động từ** (Ví dụ: *Đăng ký tài khoản*, *Đặt lịch hiến máu*, *Cập nhật số lượng máu*, *Xuất báo cáo*).
* **Lưu ý:** Không chia Use Case quá nhỏ cỡ như "Bấm nút lưu", "Nhập tên". Use Case phải là một quy trình mang lại kết quả trọn vẹn.

---

## II. Các mối quan hệ (Relationships)

Đây là phần quan trọng nhất để kết nối các thành phần lại với nhau. Có 4 loại mối quan hệ chính:

### 1. Association (Liên kết)

* **Ý nghĩa:** Thể hiện việc Actor nào được quyền sử dụng Use Case nào.
* **Ký hiệu:** Một đường thẳng nét liền nối giữa Actor và Use Case. Không có mũi tên (hoặc mũi tên chỉ hướng luồng tương tác chính).
* *Ví dụ:* Vẽ đường thẳng từ actor "Nhân viên y tế" đến use case "Cập nhật số lượng máu".

### 2. Include (Bao hàm / Bắt buộc)

* **Ý nghĩa:** Use Case A `<<include>>` Use Case B nghĩa là để hoàn thành A, thì **bắt buộc** phải thực hiện B. Nó dùng để gộp các bước lặp đi lặp lại thành một Use Case dùng chung (tái sử dụng).
* **Ký hiệu:** Đường nét đứt có mũi tên chỉ từ Use Case gốc **HƯỚNG VỀ** Use Case được bao hàm, kèm theo chữ `<<include>>`.
* *Ví dụ:* Để "Cập nhật số lượng máu" (A) hoặc "Xuất báo cáo" (C), nhân viên y tế đều bắt buộc phải "Đăng nhập" (B).
* *A `<<include>>` B* và *C `<<include>>` B*.



### 3. Extend (Mở rộng / Tùy chọn)

* **Ý nghĩa:** Use Case B `<<extend>>` Use Case A nghĩa là B là một chức năng phụ, chức năng bổ sung và **chỉ xảy ra trong một số điều kiện nhất định** khi đang thực hiện A. A vẫn có thể hoàn thành bình thường mà không cần B.
* **Ký hiệu:** Đường nét đứt có mũi tên chỉ từ Use Case mở rộng **HƯỚNG VỀ** Use Case gốc, kèm theo chữ `<<extend>>`. *(Lưu ý: Ngược hướng mũi tên với Include).*
* *Ví dụ:* Trong quá trình "Khám sức khỏe hiến máu" (A), nếu người hiến không đủ tiêu chuẩn, hệ thống sẽ có thêm bước "Ghi nhận lý do từ chối" (B). Việc từ chối này không phải lúc nào cũng xảy ra.
* *B `<<extend>>` A*.



**Bảng so sánh Include và Extend (Rất dễ nhầm lẫn):**

| Tiêu chí | `<<include>>` (Bao hàm) | `<<extend>>` (Mở rộng) |
| --- | --- | --- |
| **Tính chất** | Bắt buộc phải có. | Tùy chọn (có điều kiện mới xảy ra). |
| **Hướng mũi tên** | Chĩa **vào** chức năng phụ. <br>

<br>*(Gốc $\rightarrow$ Phụ)* | Chĩa **về** chức năng gốc. <br>

<br>*(Phụ $\rightarrow$ Gốc)* |
| **Chức năng gốc có tự hoạt động được không?** | **Không.** Thiếu chức năng phụ thì gốc bị lỗi/chưa hoàn thiện. | **Có.** Chức năng gốc vẫn hoàn thành bình thường dù không gọi chức năng phụ. |

### 4. Generalization (Tổng quát hóa / Kế thừa)

Sử dụng khái niệm kế thừa giống như trong Lập trình hướng đối tượng (OOP). Ký hiệu là đường nét liền với mũi tên tam giác rỗng.

* **Actor Generalization (Kế thừa giữa các Actor):** * *Ví dụ:* Actor "Người dùng đã xác thực" và "Người dùng ẩn danh" đều kế thừa từ Actor "Người dùng chung". Họ chia sẻ các quyền cơ bản (xem thông tin), nhưng người đã xác thực có thêm quyền đặc biệt.
* **Use Case Generalization (Kế thừa giữa các Use Case):**
* *Ví dụ:* Use Case "Thanh toán viện phí" là Use case cha. Hai Use Case con là "Thanh toán bằng Tiền mặt" và "Thanh toán bằng Chuyển khoản" sẽ kế thừa nó và định nghĩa cách thực hiện chi tiết.

