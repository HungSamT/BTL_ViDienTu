# BTL_ViDienTu - Ứng Dụng Ví Điện Tử Android (E-Wallet)

Dự án Bài Tập Lớn xây dựng ứng dụng mô phỏng **Ví Điện Tử (E-Wallet)** hoàn chỉnh trên hệ điều hành Android, được phát triển bằng ngôn ngữ **Java** kết hợp **Material Design** và cơ sở dữ liệu **SQLite**.

---

## 📱 Giới Thiệu Chung
Ứng dụng cung cấp các dịch vụ thanh toán và quản lý tài chính cá nhân tương tự như các ứng dụng ví điện tử phổ biến (MoMo, ZaloPay, Viettel Money). Người dùng có thể quản lý số dư, liên kết thẻ ngân hàng, nạp tiền, rút tiền, chuyển tiền an toàn thông qua mã PIN bảo mật và theo dõi lịch sử giao dịch trực quan.

---

## ✨ Các Tính Năng Nổi Bật

### 1. Xác thực & Quản lý Tài khoản
- **Đăng ký tài khoản mới**: Kiểm tra tính hợp lệ của số điện thoại, mật khẩu và thiết lập mã PIN giao dịch bảo mật (6 số).
- **Đăng nhập nhanh**: Hỗ trợ đăng nhập bằng SĐT & mật khẩu, tích hợp sẵn các nút chọn tài khoản mẫu (Mock Account) để test nhanh.
- **Quản lý phiên đăng nhập**: Sử dụng `SessionManager` (SharedPreferences) để ghi nhớ đăng nhập an toàn.
- **Đổi mật khẩu & Đổi mã PIN**: Cho phép người dùng thay đổi thông tin xác thực sau khi xác minh mật khẩu / mã PIN cũ.

### 2. Quản lý Số Dư & Trang Chủ
- Hiển thị thông tin cá nhân và số dư ví tức thời.
- Tính năng **Ẩn/Hiện số dư** bảo vệ tính riêng tư.
- Danh sách thao tác nhanh: **Nạp tiền**, **Rút tiền**, **Chuyển tiền**, **Ngân hàng**.
- Hiển thị 5 giao dịch gần nhất ngay tại màn hình chính.

### 3. Nạp Tiền & Rút Tiền (Top-up & Withdraw)
- Chọn tài khoản ngân hàng liên kết để nạp tiền vào ví hoặc rút tiền về tài khoản.
- Các nút chọn nhanh mệnh giá: 50.000 đ, 100.000 đ, 200.000 đ, 500.000 đ.
- Kiểm tra số dư thẻ (đối với nạp tiền) và số dư ví (đối với rút tiền).
- **Bảo mật giao dịch**: Yêu cầu xác thực mã PIN 6 số trước khi thực hiện.

### 4. Chuyển Tiền Giữa Các Ví
- Chuyển tiền nội bộ đến tài khoản ví khác thông qua số điện thoại.
- **Tự động tìm kiếm & kiểm tra**: Tự động nhận diện tên người nhận khi nhập đủ 10 số điện thoại.
- Ngăn chặn việc tự chuyển tiền cho chính mình hoặc chuyển số tiền vượt quá số dư hiện có.
- Nhập lời nhắn/nội dung chuyển tiền.
- Xác thực bằng mã PIN bảo mật.

### 5. Quản Lý Ngân Hàng Liên Kết
- Hỗ trợ liên kết nhiều ngân hàng lớn: Vietcombank, MB Bank, Techcombank, BIDV, ACB, VPBank,...
- Nhập số tài khoản và cấp số dư thử nghiệm (Mock balance).
- Xem danh sách thẻ/tài khoản đã liên kết và hỗ trợ hủy liên kết (xóa ngân hàng).

### 6. Lịch Sử Giao Dịch & Biên Lai Chi Tiết
- **Lọc thông minh**: Xem "Tất cả", "Tiền vào" (Nạp tiền, Nhận tiền), "Tiền ra" (Rút tiền, Chuyển tiền).
- **Tìm kiếm đa năng**: Tìm kiếm theo tên đối tác, số điện thoại, hoặc nội dung giao dịch theo thời gian thực.
- **Thống kê tự động**: Tự động tính toán tổng số tiền thu và chi theo bộ lọc hiện tại.
- **Biên lai giao dịch**: Nhấp vào từng giao dịch để xem biên lai chi tiết (Mã GD, số tiền, ngày giờ, số dư sau giao dịch, nội dung).

---

## 🛠 Công Nghệ & Thư Viện Sử Dụng

| Thành phần | Công nghệ / Thư viện |
|------------|-----------------------|
| **Ngôn ngữ** | Java |
| **Android SDK** | compileSdk 37 / minSdk 24 (Hỗ trợ Android 7.0 trở lên) |
| **Giao diện (UI)** | Material Components, RecyclerView, CardView, ConstraintLayout |
| **Cơ sở dữ liệu** | SQLite (`SQLiteOpenHelper`) quản lý các bảng Users, Transactions, LinkedBanks |
| **Lưu trữ phiên** | SharedPreferences (`SessionManager`) |
| **Định dạng tiền tệ** | DecimalFormat định dạng chuẩn tiền tệ Việt Nam (VNĐ) |

---

## 📂 Cấu Trúc Dự Án

```
app/src/main/
├── java/com/example/ewallet/
│   ├── adapter/             # Adapter hiển thị RecyclerView (TransactionAdapter, BankAdapter)
│   ├── database/            # Quản lý SQLite database (DatabaseHelper)
│   ├── model/               # Lớp thực thể dữ liệu (User, Transaction, LinkedBank)
│   ├── ui/                  # Các Activity giao diện (Login, Register, Main, Transfer, TopUp, History,...)
│   └── utils/               # Tiện ích bổ trợ (CurrencyUtils, SessionManager)
└── res/
    ├── drawable/            # Icon vector và background custom drawable
    ├── layout/              # File bố cục giao diện XML
    └── values/              # Màu sắc (colors.xml), chuỗi (strings.xml), themes
```

---

## 🚀 Hướng Dẫn Cài Đặt & Chạy Ứng Dụng

1. **Clone repository**:
   ```bash
   git clone https://github.com/HungSamT/BTL_ViDienTu.git
   ```
2. **Mở dự án**:
   - Mở **Android Studio**.
   - Chọn **Open** và dẫn tới thư mục `BTL_ViDienTu`.
   - Chờ Android Studio đồng bộ Gradle (**Sync Project with Gradle Files**).
3. **Chạy ứng dụng**:
   - Kết nối thiết bị Android thật (bật USB Debugging) hoặc bật một máy ảo (Emulator Android 7.0+).
   - Nhấn nút **Run 'app'** (`Shift + F10` hoặc biểu tượng ▶️ màu xanh).

### 🔑 Tài khoản thử nghiệm có sẵn:
| Số điện thoại | Mật khẩu | Mã PIN | Họ và tên | Số dư ban đầu |
|---------------|----------|--------|-----------|---------------|
| `0901234567`  | `123456` | `123456` | NGUYEN VAN A | 2.500.000 đ |
| `0987654321`  | `123456` | `654321` | TRAN THI B   | 1.000.000 đ |

*(Trên màn hình đăng nhập có sẵn 2 nút chọn nhanh 2 tài khoản này để test tính năng chuyển khoản giữa 2 ví rất thuận tiện)*
