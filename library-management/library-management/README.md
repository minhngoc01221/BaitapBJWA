# Library Management

Ứng dụng Spring Boot MVC quản lý sách, thành viên và quy trình mượn trả sách theo đề bài junior developer.

## Công nghệ

- Java 17
- Spring Boot 3.3.5
- Spring MVC, Thymeleaf
- Spring Data JPA, Hibernate
- MariaDB / MySQL trong XAMPP
- Flyway
- Jakarta Validation
- JUnit 5, Mockito

## Cách chạy

1. Mở **XAMPP Control Panel**, bấm **Start** ở dòng **MySQL**. Mặc định ứng dụng kết nối đến `127.0.0.1:3306`, tài khoản `root`, mật khẩu trống. Apache chỉ cần bật khi bạn muốn dùng phpMyAdmin.

2. Mở thư mục chứa `pom.xml` trong IntelliJ IDEA, chọn JDK 17 và bấm **Reload All Maven Projects** để tải driver database.

3. Chạy lớp `LibraryManagementApplication` bằng nút Run cạnh hàm `main`. Ứng dụng tự tạo database `library_db` nếu chưa tồn tại; Flyway tự tạo bảng và dữ liệu mẫu.

Nếu muốn tạo database thủ công, bật Apache và mở `http://localhost/phpmyadmin`, chọn tab SQL rồi chạy:

```sql
CREATE DATABASE library_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Nếu MySQL trong XAMPP dùng cổng khác hoặc bạn đã đặt mật khẩu, cấu hình biến môi trường trong IntelliJ tại **Run > Edit Configurations > Environment variables**:

```text
DB_PORT=3307;DB_USERNAME=root;DB_PASSWORD=mat_khau_cua_ban
```

Chỉ thêm các biến cần đổi. Có thể đổi máy chủ bằng `DB_HOST`. Các giá trị mặc định nằm trong `src/main/resources/application.yml`.

Bạn cũng có thể chạy ứng dụng bằng terminal:

```bash
./mvnw spring-boot:run
```

Trên Windows:

```bat
mvnw.cmd spring-boot:run
```

4. Mở trình duyệt tại `http://localhost:8080`.

Nếu gặp lỗi kết nối database, kiểm tra dòng MySQL trong XAMPP đang màu xanh và cổng khớp với `DB_PORT` (mặc định 3306). Nếu cổng web 8080 bị chiếm, dừng phiên chạy cũ hoặc đặt biến môi trường `SERVER_PORT=8081` rồi truy cập `http://localhost:8081`.

Flyway sẽ tự tạo schema và seed dữ liệu mẫu gồm 5 thể loại, 20 sách, 10 thành viên và 5 phiếu mượn.

## Chức năng chính

- Quản lý thể loại sách: tạo, sửa, xóa mềm, không cho trùng tên.
- Quản lý sách: tạo, sửa, xóa mềm, tìm kiếm theo tên, tác giả, ISBN hoặc thể loại.
- Quản lý thành viên: tạo, sửa, khóa và mở khóa, kiểm tra email hợp lệ và không trùng.
- Mượn sách: tạo phiếu nhiều đầu sách, kiểm tra tồn kho, trừ số lượng còn lại trong transaction.
- Trả sách: trả từng dòng hoặc toàn bộ phiếu, cộng lại tồn kho, tính phạt 5.000 VND mỗi ngày cho mỗi cuốn trả muộn.
- Báo cáo: phiếu quá hạn, top 5 sách được mượn nhiều nhất, số lượt mượn theo thành viên.

## Route chính

- `/books`
- `/categories`
- `/members`
- `/borrowings`
- `/reports`
- `/reports/overdue`

## Kiểm thử

```bash
./mvnw test
```

Unit test hiện kiểm tra nghiệp vụ tạo phiếu mượn và chặn thành viên bị khóa.
