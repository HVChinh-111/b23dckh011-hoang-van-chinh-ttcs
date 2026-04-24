# Quy định chung thiết kế analysis class diagram

## A. Các bước thực hiện
Xuất phát từ use case specification. Và trích xuất theo từng UC riêng.
### Bước 1: Trích xuất Boundary Class
Mỗi giao diện xuất hiện có tương tác với người dùng -> Đề xuất thành 1 Boundary Class
**Lưu ý:**
   - Chỉ trích xuất các giao diện chính. Các giao diện như: Thông báo lỗi, thông báo thành công, xác nhận ok/cancel hay gom chung lại là các alert -> Bỏ qua, không trích xuất.

**Naming Convention:** ???
*   **Tên Lớp:** [Tên giao diện] + `View`. Viết theo PascalCase.
    *   *Ví dụ:* `LoginView`.

### Bước 2: Xác định thuộc tính cho Boundary Class
Với mỗi Boundary Class, xét các thông tin cần có để tương tác trên giao diện người dùng: Mỗi thông tin -> Đề xuất thành thuộc tính giao diện của lớp
**Lưu ý:**
   - Thuộc tính để nhập dữ liệu vào: Dùng tiền tố "in\<attributeName>"
   - Thuộc tính để hiển thị dữ liệu: Dùng tiền tố "out\<attributeName>"
   - Thuộc tính để điều khiển chức năng hay submit: "sub\<attributeName>"
   - Nếu thuộc tính thỏa mãn nhiều tính chất thì dùng kết hợp nhiều tiền tố: "inoutsub\<attributeName>"
     - Ví dụ: Menu xổ xuống thì vừa là in, vừa là out và vừa là sub vì menu xổ xuống sẽ phải lấy dữ liệu từ database để hiển thị lên màn hình(out), và người dùng có thể click để lựa chọn (sub), và người dùng lựa chọn cái gì thì sẽ có dữ liệu gửi về hệ thống (in).

### Bước 3: Trích xuất Control Class (Lớp Điều khiển)
**Mục đích:** Mô hình hóa các thuật toán nghiệp vụ phức tạp thành Control Class (Chú ý phân biệt với Controller) điều phối hoạt động giữa Boundary và Entity.
**Cách làm:**
*   Từ các Use Case Specification, tìm ra những quy trình tính toán, điều phối dữ liệu nhiều bước. Thường thì mỗi Use Case phức tạp sẽ cần 1 Control Class để quản lý luồng chạy của Use Case đó.
*   Nếu có một nghiệp vụ độc lập mà không thuộc về trách nhiệm dữ liệu cụ thể của Entity nào, nó sẽ nằm ở Control. -> Gần như đều sẽ nằm ở Control, còn những method để xử lý dữ liệu riêng trong Entity thì chưa cần thiết ở Pha Analysis này mà là pha Design.

**Naming Convention:**
*   **Tên Lớp:** Thường là Cụm động từ chỉ hành động chính của Use Case hoặc [Tên Use Case] + `Control`. Viết theo PascalCase.
    *   *Ví dụ:* `LoginControl`.

## B. Tài liệu tham khảo

1. [Video tổng quan các loại diagram: freeCodeCamp](https://www.youtube.com/watch?v=WnMQ8HlmeXc&t=579s "https://www.youtube.com/watch?v=WnMQ8HlmeXc&t=579s")
2. [Video thầy Hùng](https://www.youtube.com/watch?v=2FpGtTRCsvs&list=PLPUAeQ3rg3bI3dlECrYarT60xS-TUdCIk&index=3 "https://www.youtube.com/watch?v=2FpGtTRCsvs&list=PLPUAeQ3rg3bI3dlECrYarT60xS-TUdCIk&index=3")
3. Sách: Object-Oriented and Classical Software Engineering (2010, McGraw-Hill Education)