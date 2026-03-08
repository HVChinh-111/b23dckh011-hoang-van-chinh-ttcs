# Xây dựng hệ thống Blog cá nhân

### CHƯƠNG 1: GIỚI THIỆU ĐỀ TÀI

**1. Đặt vấn đề**

Trong bối cảnh công nghệ thông tin phát triển và thay đổi liên tục, việc cập nhật kiến thức mới là một yêu cầu bắt buộc đối với mỗi lập trình viên cũng như những người làm việc trong lĩnh vực kỹ thuật. Quá trình tiếp thu các ngôn ngữ, framework hay những nguyên lý thiết kế phần mềm thường diễn ra với tốc độ rất nhanh. Tuy nhiên, hệ quả của việc "học nhanh" là bộ não con người khó có thể ghi nhớ toàn bộ và tường tận mọi chi tiết kỹ thuật trong thời gian dài.

Do đó, thói quen vừa học vừa ghi chép, hệ thống hóa lại tài liệu là một giải pháp thiết yếu. Việc lưu trữ lại những bài học chuyên môn (ví dụ như các khái niệm về lập trình hướng đối tượng, cách triển khai một kiến trúc phần mềm, hay các thủ thuật tối ưu hóa cơ sở dữ liệu) không chỉ giúp củng cố kiến thức tại thời điểm học mà còn tạo ra một "bộ nhớ ngoài" đắc lực để dễ dàng tra cứu lại khi cần thiết trong công việc thực tế.

Hơn thế nữa, tri thức sẽ sinh sôi khi được chia sẻ. Nhu cầu công khai các bài viết, bài nghiên cứu cá nhân để giao lưu, trao đổi với cộng đồng cũng ngày một tăng cao. Xuất phát từ những nhu cầu thực tiễn đó, đề tài **"Xây dựng hệ thống Blog cá nhân"** được lựa chọn nghiên cứu và phát triển. Ứng dụng này sẽ cung cấp một không gian riêng tư và chuyên nghiệp để tác giả biên soạn bài viết bằng Markdown, đồng thời là nơi lưu trữ, phân loại theo chuỗi bài học (series) và chia sẻ kiến thức đến với mọi người.

**2. Mục tiêu đề tài**

Đề tài hướng tới việc xây dựng một hệ thống website blog cá nhân hoàn chỉnh với các mục tiêu cụ thể sau:

* **Về mặt tiện ích:** Tạo ra một nền tảng cho phép người quản trị (admin) dễ dàng đăng tải, quản lý các bài viết học thuật được soạn thảo dưới định dạng Markdown (định dạng tối ưu nhất cho dân kỹ thuật).
* **Về mặt trải nghiệm:** Cung cấp giao diện trực quan, dễ sử dụng cho người đọc (khách) khi truy cập để tìm kiếm, theo dõi các bài viết theo từng chuyên mục hoặc theo các chuỗi bài viết (series) liên quan với nhau.
* **Về mặt kỹ thuật:** Vận dụng và kết hợp thành thạo các kiến thức đã học về phát triển ứng dụng Web, bao gồm việc xây dựng giao diện phía người dùng (Frontend), thiết kế API và xử lý logic nghiệp vụ phía máy chủ (Backend), cũng như quản trị cơ sở dữ liệu.

**3. Phạm vi đề tài**

Do giới hạn về thời gian của môn học thực tập cơ sở, hệ thống sẽ tập trung vào các chức năng cốt lõi nhất của một blog cá nhân, bao gồm:

* **Đối với người dùng vãng lai (Guest):** Truy cập trang chủ; xem danh sách bài viết mới nhất hoặc trending; phân loại bài viết theo danh mục (Category) và chuỗi bài học (Series). Đọc chi tiết bài viết với giao diện hiển thị Markdown rõ ràng. Xem thông tin, phương thức liên hệ của chủ website
* **Đối với quản trị viên (Admin):** Có cơ chế đăng nhập bảo mật. Quản lý (Thêm, sửa, xóa, ẩn/hiện) các bài viết, danh mục và series. Trực tiếp soạn thảo bài viết bằng công cụ hỗ trợ Markdown. Cập nhập thông tin và phương thức liên hệ của bản thân

**4. Công nghệ sử dụng**

Để đáp ứng các yêu cầu về hiệu năng, tính mở rộng và trải nghiệm người dùng, hệ thống được xây dựng dựa trên các công nghệ sau:

* **Frontend (Giao diện người dùng):** Sử dụng **ReactJS**. Đây là một thư viện JavaScript phổ biến giúp xây dựng giao diện người dùng theo dạng các component độc lập, mang lại trải nghiệm mượt mà, tốc độ phản hồi nhanh (Single Page Application) và rất phù hợp để xử lý giao diện hiển thị Markdown động.
* **Backend (Xử lý nghiệp vụ):** Sử dụng **Java Spring Boot**. Nền tảng này cung cấp cấu trúc kiến trúc vững chắc, bảo mật cao và tối ưu cho việc xây dựng các RESTful API phục vụ cho quá trình giao tiếp dữ liệu với Frontend.
* **Hệ quản trị Cơ sở dữ liệu:** Sử dụng **MySQL**. Là một hệ quản trị cơ sở dữ liệu quan hệ mạnh mẽ, lưu trữ và truy xuất các dữ liệu có cấu trúc một cách an toàn và hiệu quả (bao gồm thông tin người dùng, bài viết, chuyên mục).

Chào bạn, bản nháp Chương 1 bạn chỉnh sửa rất mạch lạc, đầy đủ và bám sát vào đúng trọng tâm của một hệ thống blog cá nhân thực tế. Việc bổ sung thêm tính năng xem/cập nhật thông tin tác giả và hiển thị bài viết thịnh hành (trending) sẽ giúp blog trở nên chuyên nghiệp và kết nối với người đọc tốt hơn.

Dựa trên những điều chỉnh của bạn ở Chương 1, mình đã cập nhật lại toàn bộ **Chương 2** để đảm bảo tính nhất quán của báo cáo. Đặc biệt, bảng thuật ngữ đã được trình bày lại theo đúng định dạng bạn yêu cầu.

---

### CHƯƠNG 2: TÀI LIỆU PHA YÊU CẦU CỦA HỆ THỐNG

#### 1. Bảng thuật ngữ

| Thuật ngữ (Tiếng Anh) | Thuật ngữ (Tiếng Việt) | Ý nghĩa |
| --- | --- | --- |
| **Markdown** *(n)* | Ngôn ngữ đánh dấu | Là một ngôn ngữ đánh dấu văn bản nhẹ, cho phép người viết sử dụng các ký tự thông thường để định dạng văn bản (in đậm, in nghiêng, chèn khối mã nguồn). Hệ thống sẽ tự động render thành HTML trực quan. |
| **Category** *(n)* | Chuyên mục | Là nhóm phân loại bài viết theo một chủ đề, lĩnh vực kỹ thuật hoặc ngôn ngữ lập trình cụ thể (Ví dụ: Lập trình Web, Database, Java Core). |
| **Series** *(n)* | Chuỗi bài | Là tập hợp các bài viết có tính tuần tự, liên kết chặt chẽ với nhau để tạo thành một lộ trình hướng dẫn chi tiết từ cơ bản đến nâng cao. |
| **Post** *(n)* | Bài viết | Đơn vị nội dung cơ bản của blog, chứa kiến thức, chia sẻ hoặc tài liệu học thuật do tác giả biên soạn. |
| **Trending** *(adj)* | Thịnh hành / Nổi bật | Trạng thái của các bài viết nhận được nhiều sự quan tâm, lượt xem lớn hoặc được đánh giá cao trong một khoảng thời gian nhất định. |
| **Profile** *(n)* | Hồ sơ cá nhân | Khu vực lưu trữ thông tin giới thiệu về tác giả và các phương thức liên hệ (Email, GitHub, LinkedIn,...). |
| **Admin** *(n)* | Quản trị viên | Là chủ sở hữu của website, người có toàn quyền đăng nhập để quản lý nội dung, danh mục, chuỗi bài và thông tin cá nhân. |
| **Guest / Reader** *(n)* | Khách vãng lai | Là những người truy cập vào hệ thống website để đọc tài liệu, tìm kiếm kiến thức mà không cần tài khoản đăng nhập. |

#### 2. Mô hình nghiệp vụ bằng ngôn ngữ tự nhiên

**2.1. Mục tiêu và phạm vi hệ thống**

* **Mục tiêu:** Xây dựng một website cá nhân để lưu trữ, hệ thống hóa và chia sẻ kiến thức chuyên môn. Hệ thống tối ưu hóa trải nghiệm soạn thảo mã nguồn bằng Markdown và tạo kênh kết nối giữa tác giả với cộng đồng lập trình viên thông qua phần thông tin liên hệ.
* **Phạm vi:** Trọng tâm của phần mềm là hệ thống quản trị nội dung (CMS) cơ bản tập trung vào Bài viết, Chuyên mục, Series; xử lý hiển thị bài viết mới/thịnh hành và trang thông tin liên hệ cá nhân.

**2.2. Ai có thể sử dụng phần mềm?**
Hệ thống phục vụ hai nhóm đối tượng chính:

1. **Quản trị viên (Admin):** Tác giả của blog.
2. **Khách (Guest):** Bất kỳ ai truy cập vào blog thông qua mạng Internet.

**2.3. Người dùng có những chức năng gì?**

* **Với Admin:** Đăng nhập hệ thống; Quản lý (Thêm, sửa, xóa) Chuyên mục và Series; Soạn thảo, xuất bản và quản lý Bài viết; Cập nhật thông tin giới thiệu và phương thức liên hệ của bản thân.
* **Với Guest:** Xem trang chủ với danh sách bài viết mới nhất và thịnh hành; Xem danh sách bài theo Chuyên mục/Series; Đọc chi tiết bài viết (giao diện Markdown render); Xem thông tin tác giả và phương thức liên hệ.

**2.4. Mỗi chức năng hoạt động ra sao?**

* **Luồng hoạt động của Guest:** Khi truy cập, Guest sẽ thấy Trang chủ hiển thị nổi bật các bài viết mới cập nhật, thông tin tác giả và các bài đang trending. Guest có thể duyệt qua thanh menu để lọc bài viết theo Chuyên mục hoặc Series, tìm kiếm bài viết. Khi nhấn vào bài viết, nội dung Markdown sẽ được hiển thị rõ ràng với các khối mã (code block) dễ đọc.
* **Luồng hoạt động của Admin:** Admin truy cập trang đăng nhập ẩn và xác thực. Tại Dashboard, Admin có thể cập nhật hồ sơ cá nhân của mình. Khi học được kiến thức mới, Admin tạo Bài viết, gõ nội dung bằng Markdown, chọn trạng thái (Lưu nháp hoặc Xuất bản) và gắn vào Chuyên mục/Series tương ứng. Các thay đổi này sẽ lập tức phản ánh lên giao diện của Guest sau khi lưu.

**2.5. Những thông tin/ đối tượng mà hệ thống cần xử lý**

Hệ thống thao tác với 4 thực thể dữ liệu chính:

1. **Thông tin tác giả (Profile/Users):** Tài khoản đăng nhập, Mật khẩu (mã hóa), Họ tên, Ảnh đại diện, Giới thiệu ngắn, Link liên hệ (Github, Facebook, LinkedIn).
2. **Bài viết (Posts):** Tiêu đề, Chuỗi URL (Slug), Nội dung Markdown, Ngày tạo, Lượt xem (để tính trending), Trạng thái (Draft/Published).
3. **Chuyên mục (Categories):** Tên chuyên mục, Mô tả.
4. **Chuỗi bài (Series):** Tên chuỗi, Mô tả lộ trình.

**2.6. Quan hệ giữa các đối tượng**

* Một **Chuyên mục** chứa *nhiều* **Bài viết**. Một **Bài viết** cũng có thể thuộc *nhiều* **Chuyên mục**.
* Một **Series** chứa *nhiều* **Bài viết**. Một **Bài viết** có thể thuộc hoặc *không thuộc* **Series** nào.
* Một **Admin** sở hữu *nhiều* **Bài viết** và quản lý *một* **Hồ sơ cá nhân (Profile)** duy nhất.

#### 3. Mô hình nghiệp vụ bằng UML

**3.1. Xác định các actor của hệ thống**

* **Guest (Khách):** Tác nhân đại diện cho người đọc (không cần xác thực).
* **Admin (Quản trị viên):** Tác nhân đại diện cho chủ sở hữu hệ thống. (Admin sẽ kế thừa mọi quyền hạn của Guest).

**3.2. Các use case cho từng actor**

* **Actor: Guest**
* `UC01: Xem trang chủ (bài viết mới nhất, bài viết trending, thông tin tác giả)`
* `UC02: Lọc bài viết theo Chuyên mục / Series`
* `UC03: Tìm kiếm bài viết`
* `UC04: Xem chi tiết bài viết`


* **Actor: Admin**
* `UC05: Đăng nhập hệ thống`
* `UC06: Quản lý Chuyên mục (Thêm, sửa, xóa)`
* `UC07: Quản lý Series (Thêm, sửa, xóa)`
* `UC08: Quản lý Bài viết (Viết Markdown, sửa, xóa, ẩn/hiện, lưu bản nháp)`
* `UC09: Cập nhật thông tin cá nhân và phương thức liên hệ`



#### 4. Bảng yêu cầu người dùng

| ID | Mô tả yêu cầu | Độ ưu tiên |
| --- | --- | --- |
| **FR01** | Khách truy cập có thể xem danh sách bài viết mới nhất, bài viết thịnh hành (trending) và tìm kiếm bài viết trên trang chủ. | Cao |
| **FR02** | Khách truy cập có thể lọc và tìm kiếm các bài viết theo từng Chuyên mục hoặc Series cấu trúc sẵn. | Cao |
| **FR03** | Khách truy cập có thể đọc chi tiết bài viết với giao diện Markdown đã được render hoàn chỉnh (hiển thị rõ cấu trúc text, code block, hình ảnh). | Cao |
| **FR04** | Khách truy cập có thể xem thông tin giới thiệu về tác giả và các phương thức liên hệ (Facebook, Github, LinkedIn) trên trang chủ | Trung bình |
| **FR05** | Hệ thống cung cấp cơ chế đăng nhập bảo mật dành riêng cho Quản trị viên (Admin). | Cao |
| **FR06** | Quản trị viên có trình soạn thảo văn bản hỗ trợ cú pháp Markdown để tạo mới, chỉnh sửa bài viết. | Cao |
| **FR07** | Quản trị viên có thể thiết lập trạng thái bài viết: Lưu nháp (Draft) hoặc Xuất bản (Published). | Cao |
| **FR08** | Quản trị viên có thể gắn bài viết vào một Chuyên mục và/hoặc một Series cụ thể khi đăng bài. | Cao |
| **FR09** | Quản trị viên có quyền thêm mới, cập nhật tên/mô tả hoặc xóa các Chuyên mục và Series. | Cao |
| **FR10** | Quản trị viên có thể chỉnh sửa, cập nhật thông tin tiểu sử cá nhân và các đường dẫn liên hệ của mình trên hệ thống. | Trung bình |
