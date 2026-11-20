---
package: com.zenbyte.flipanimation
name: FlipAnimation (Flip Animation)
version: 2.3.0 (53)
surveyed_at: 2026-11-19
surveyed_on: máy thật Xiaomi (23021RAAEG), Android 15, 1080x2400, 440 dpi (393x873 dp)
network: có mạng (Wi-Fi, vùng Việt Nam)
status: draft   # đủ nội dung; giữ draft vì chỉ người ghi reviewed
---

# APP MẪU: FLIPANIMATION (ZENBYTE)

<!--
Báo cáo khảo sát một app mẫu (ADR-0021, docs/ops/clone-study.md). AI viết, dev xem lại.
Mỗi nhận xét dẫn về ảnh (`NN-ten`, trong captures/com.zenbyte.flipanimation/ của thư mục làm việc, không vào repo) hay dòng cây giao diện.
App viết bằng Flutter nhưng cây giao diện có đủ content-desc (nhãn trợ năng), nên phần lớn thao tác chạm theo chữ. App hiện bằng tiếng Việt theo ngôn ngữ của máy; tên trong báo cáo là chữ tiếng Việt của app.
-->

## 1. Thông tin

- FlipAnimation của ZenByte, ra mắt 05/2026, khoảng 23 nghìn lượt tải mỗi 30 ngày, điểm 4,14, "app mới đang lên" ([web-play.md](web-play.md), mục 2).
- Flutter (bloc, go_router), lưu nét vector trong sqflite. Không có SDK quảng cáo. Bán Pro qua RevenueCat ([apk-analysis.md](apk-analysis.md), mục 3).
- Không cùng mã nguồn với app nào khác trong nhóm khảo sát.
- `screens.md` chỉ có `MainActivity`: mọi màn là route Flutter trong một Activity. Danh sách màn đã bổ sung ở mục 3.
- Số liệu trên máy khác phân tích APK:
  - App hiện **115 bài hướng dẫn, 16 miễn phí**, không phải 23 bài (`23-learn-tab`). Có thể bài học dựng bằng code, nên chuỗi trong `libapp.so` không thấy hết.
  - Bảng giá có gói **Trọn đời trả một lần**, không chỉ thuê bao (`22-paywall`).

## 2. Lần mở đầu

Sau `pm clear`, có mạng:

| # | Màn hình | Thao tác của người dùng | Ảnh |
| --- | --- | --- | --- |
| 1 | Splash: nền giấy kẻ ô, logo "FlipAnimation", khẩu hiệu "Học - Vẽ - Diễn hoạt", hình vẽ tay chuyển động (ngôi sao treo, nhân vật). Khoảng 5 đến 8 giây | (chờ) | `01-launch` |
| 2 | Giới thiệu 6 trang, mỗi trang có hoạt hình minh họa, tiêu đề gạch chân đỏ, nút "Tiếp theo", nút "Bỏ qua" ở góc trên phải (trang cuối không có). Lần lượt: "Xưởng hoạt hình của bạn", "Xem nó chuyển động" (onion skin), "Sáng tạo thông minh hơn" (đối xứng, hình khối, lưới), "Đồ theo chuyển động thật" (vẽ theo video), "Thêm âm thanh", "Khoe với thế giới" (GIF, video HD, chuỗi ảnh, sách lật in được) | Bỏ qua, hoặc Tiếp theo x5 rồi "Bắt đầu" | `02-after-splash`, `03-onb3`, `04-onb6` |
| 3 | Màn chính, tab Studio, trạng thái trống: "Chưa có dự án nào" và hai nút "Bắt đầu sáng tạo", "Thử bài học miễn phí" | Bắt đầu sáng tạo | `05-after-onb` |
| 4 | Hộp thoại "Dự án mới": tên (mặc định "Hoạt hình của tôi"), khổ canvas | Tạo | `07-new-project` |
| 5 | Màn vẽ, cọ "Bút kim" chọn sẵn, có tour 9 bước (lớp phủ mờ, "Bỏ qua" được) | Bỏ qua tour, vẽ | `09-editor`, `10-editor-empty` |

**Tới nét vẽ đầu tiên: 4 thao tác** (Bỏ qua, Bắt đầu sáng tạo, Tạo, Bỏ qua tour). Đi đủ giới thiệu thì 9 thao tác. **0 quảng cáo, 0 màn trả phí, không hỏi tuổi, không xin quyền, không hộp thoại đồng ý, không yêu cầu đăng nhập.** So với FlipaClip (10 thao tác, paywall bắt buộc, hỏi tuổi): ngắn hơn hẳn.

Ghi chú:
- Bàn phím bật lên ngay khi mở "Dự án mới" và che nút Tạo (`07-new-project`). Phải ẩn bàn phím mới thấy nút. Tên dự án có sẵn nên không cần gõ.
- Tour màn vẽ 9 bước, mỗi bước khoanh vùng liên quan và có "Quay lại", "Tiếp", "Bỏ qua". Nội dung từng bước:
  1. Canvas: vẽ bằng ngón hay bút; chụm hai ngón để thu phóng; chạm hai ngón để hoàn tác.
  2. Cọ vẽ: nhấn giữ nút cọ rồi trượt lên xuống để đổi cỡ.
  3. Công cụ nhanh: trượt giá trị lên xuống.
  4. Khung hình: + để thêm khung; chạm khung đang chọn để sao chép.
  5. Âm thanh: loa sáng ở khung có âm thanh.
  6. Phát.
  7. Điều khiển khung nhìn.
  8. Thêm: hình dạng, văn bản, lớp, âm thanh, lưới; mục Canvas có onion skin.
  9. Xuất. Xem lại tour trong Cài đặt.
- Xin quyền thông báo khéo: không xin lúc mở app. Lần xuất đầu tiên xong mới hiện hộp thoại riêng của app, "Bạn muốn được nhắc nhẹ không? Chúng tôi có thể thỉnh thoảng nhắc bạn hoàn thành một hoạt hình hoặc thử bài học", nút "Để sau" và "Bật" (`34-export-done`). Chọn Để sau thì hộp thoại hệ thống không hiện.

## 3. Bản đồ màn hình

| Màn hình | Mục đích | Vào từ đâu | Ảnh |
| --- | --- | --- | --- |
| Splash | Thương hiệu | Mở app | `01` |
| Giới thiệu 6 trang | Onboarding | Lần mở đầu | `02` đến `04` |
| Studio (tab) | Dự án, Loạt truyện, Học hoạt hình, "Gặp gỡ các họa sĩ hoạt hình" (bảng xếp hạng cộng đồng) | Tab Studio | `05`, `06`, `20`, `40` |
| Dự án mới (hộp thoại) | Hoạt hình trống hoặc Đồ theo video; tên; khổ; nền trong suốt | Bắt đầu sáng tạo, Hoạt hình mới | `07`, `08` |
| Menu dự án | Đổi tên, Nhân bản, Chuyển vào thư mục, Xóa | Nút ⋮ trên thẻ dự án | (cây giao diện) |
| Màn vẽ | Vẽ, khung, lớp, âm thanh | Tạo dự án, chạm dự án, mở bài học | `09` đến `19`, `24` đến `31` |
| Thư viện cọ | 5 nhóm cọ, cài đặt cọ, bộ cọ | Cọ vẽ | `14`, `15` |
| Màu sắc | Bảng màu, lưới, thanh trượt, hút màu | Màu sắc | `12` |
| Lớp | Danh sách lớp, nhóm, chế độ hòa trộn | Lớp | `25` |
| Thêm, Canvas | Hình dạng, Văn bản, Biến đổi, Âm thanh, Lưới, Canvas (Onion Skin, Đối xứng, Nền, Tốc độ) | Thêm | `26` đến `28` |
| Âm thanh | Hiệu ứng (21 nhóm), Âm nhạc (nhập file) | Thêm âm thanh, nút loa | `29` đến `31` |
| Phát toàn màn hình | Xem trước | Phát | `19` |
| Xuất | Định dạng, độ phân giải, nền, logo | Xuất | `32` đến `35` |
| Học (tab) | 115 bài theo 6 nhóm, lọc "Đang thực hiện" | Tab Học | `23`, `36` |
| Loạt truyện > Mochi | Danh sách tập, "Bắt đầu E01" | Studio > Loạt truyện | `37`, `38` |
| Cộng đồng (tab) | Chủ đề tuần, Nổi bật, Bảng xếp hạng, Remix | Tab Cộng đồng | `39` |
| PRO (màn trả phí) | Bán Pro | Nút PREMIUM, mọi hộp thoại "là tính năng Pro" > Nâng cấp | `22` |
| Hộp thoại "là tính năng Pro" | Mời nâng cấp khi chạm tính năng khóa | Xem mục 6 | `17`, `21`, `31`, `36` |
| Cài đặt | Giao diện, bút, bố cục, mặc định dự án, tài khoản | Nút bánh răng | (cây giao diện) |

Luồng chính: `Studio > Bắt đầu sáng tạo > Dự án mới > Tạo > Màn vẽ > (vẽ, + khung, âm thanh) > Xuất > GIF > Xuất hoàn tất > Đóng > Màn vẽ > Quay lại > Studio`.

## 4. Tính năng

**Tạo dự án** (`07`, `08`):
- Hai chế độ: "Hoạt hình trống" và "Đồ theo video" (rotoscope, 2 lần miễn phí, nút trên Studio ghi "Đồ theo video · MIỄN PHÍ 2").
- Khổ: Vuông 1:1 (1080×1080, mặc định), Dọc 9:16, Ngang 16:9 (1920×1080).
- Mục mở rộng "Nhãn dán, hiệu ứng và kích thước tùy chỉnh":
  - Nhãn dán WeChat 520×520.
  - Nhãn dán nhỏ 240×240.
  - Telegram, WhatsApp 512×512.
  - Hiệu ứng dọc 540×960.
  - Kích thước tùy chỉnh.
- Công tắc "Nền trong suốt"; dòng chữ dưới cùng hiện số px của khổ đã chọn.
- Không chọn FPS khi tạo dự án. FPS mặc định đặt trong Cài đặt.

**Danh sách dự án** (`20`, `40`):
- Thẻ có thumbnail, số khung đang xem ("2/2"), tên, "16:9, 2 khung hình · 12 FPS", nút Phát ngay trên thẻ, nút menu.
- Trên Studio, dự án nằm ở hàng cuộn ngang. "Xem tất cả" mở lưới 2 cột (168×129 dp), có đếm số dự án ("4").
- Tìm kiếm dự án. Thư mục là tính năng Pro (icon khóa).
- Bài học và tập truyện **không tính vào giới hạn 4 dự án**: sau khi mở bài học, danh sách vẫn là 4.

**Cọ** (`14`, `15`):
- Thư viện cọ dạng bảng nổi. Cột trái là nhóm; bên phải là danh sách tên cọ kèm nét mẫu.
- Khoảng 31 cọ trong 5 nhóm:
  - Phác thảo: Bút chì, Bút chì màu, Than vẽ, Bút sáp.
  - Mực: Bút kim, Bút kỹ thuật, Bút mực, Ngòi G, Ngòi Maru, Bút lông.
  - Tô màu: Màu bột, Màu nước, Cọ khô, Cọ mềm, Cọ phun, Bút dạ, Bút dạ quang, Phun sơn.
  - Hiệu ứng: Neon, Vẩy mực, Con dấu, Tram, Pixel, Chấm điểm, Hạt.
  - Cổ điển: Bút mực, Bút sáp, Than vẽ, Màu nước, Thư pháp (đều có chữ "cổ điển"), Bút mềm, Bút nét đứt.
- **Không thấy cọ nào bị khóa.**
- Có "Bộ mới hoặc thêm cọ" và "Bộ của tôi", tức xưởng tự tạo bộ cọ. Bộ có sẵn: Phác thảo, Phác thảo xanh, Nét sạch, Nét đậm, Màu, Mềm.
- Cài đặt cọ: Kích thước (px), Độ mờ, Ổn định nét (mặc định 50%).
- Cuối thư viện có ba nút Tô màu, Tẩy, Văn bản. **Văn bản khóa Pro.**

**Công cụ nhanh** (thanh nổi trên canvas):
- Theo công cụ đang chọn:
  - Cọ: cọ, Kích thước, Màu, Độ mờ, Ổn định nét.
  - Tô màu: Dung sai 32, Độ mờ, Màu.
- Mỗi giá trị kéo lên xuống để chỉnh, chạm để tinh chỉnh. Thanh gập được (`><`) và kéo đi được (tay nắm ⠿).

**Màu** (`12`):
- Ô Trước và Hiện tại; chạm Trước để quay lại màu đó.
- Bảng "Cơ bản" 12 màu và nút thêm màu hiện tại.
- Ba chế độ: Lưới, Thanh trượt, Bảng màu.
- Thanh độ mờ, mục Phối màu (bật tắt), ô HEX có nút sao chép.
- "Kiểm tra độ sáng", "Chọn màu từ khung vẽ" (hút màu).
- Chạm một ô màu là chọn và đóng bảng ngay.
- Bên trái canvas có nút tròn "Dải bảng màu" (giữ để xem mọi bảng).

**Tô màu** (`13`): chạm trong hình chữ nhật khép kín thì tô đúng vùng, mép sạch. Chưa thử vùng hở.

**Lớp** (`25`):
- Bảng lớp: "Dùng chung cho cả hoạt ảnh. Mỗi khung hình giữ bản vẽ riêng trên từng lớp". **"Hiện đang miễn phí không giới hạn lớp"**.
- Mỗi lớp có: Ẩn, Khóa, Tùy chọn, Độ mờ, Chế độ hòa trộn, Khóa alpha, Cắt theo lớp dưới (clipping), **"Giữ qua nhiều khung hình"** (một hình vẽ dùng cho nhiều khung, sửa một lần).
- Có Nhóm mới, Chọn nhiều lớp, và lớp "Giấy" luôn nằm dưới cùng.
- Vuốt lớp để ẩn, khóa, nhân bản, xóa.

**Onion skin** (`18`, `28`):
- Nút Onion ở thanh trên, chạm để đổi số khung trước: 1, rồi 2.
- Bảng cài đặt:
  - Bật tắt.
  - Khung trước 0 đến 5.
  - Khung sau 0 đến 5.
  - Độ mờ 40%.
  - Ô xem nhanh "Sách lật các khung hình lân cận".
- Khung trước hiện màu đỏ nhạt (`16-frame2-onion`).

**Thêm** (`26`):
- Hình dạng, Văn bản, Biến đổi, Âm thanh, Lưới, Canvas.
- Trong Canvas: Âm thanh, Onion, Onion Skin, Đối xứng, Lưới, Nền, Tốc độ.

**Đối xứng** có nút ngay ở thanh trên. **Biến đổi, Phóng to, Thu nhỏ, Chế độ di chuyển (bàn tay)** nằm ở cột phải canvas; cột gập được.

**Âm thanh** (`29` đến `31`):
- Hai tab: Hiệu ứng, Âm nhạc.
- Hiệu ứng: gắn vào khung đang chọn; có nút khung trước, khung sau.
  - 21 nhóm: Gần đây, Âm thanh của tôi, Va chạm (65), Tiếng vút, Vũ khí, Cơ thể & động tác, Hiệu ứng nhạc cụ, Hoạt hình, Nhà & bếp, Nhạc hiệu, Phép thuật, Phương tiện, Rùng rợn, Siêu năng lực, Sách lật & giấy, Thiên nhiên, Thể thao, Tiếng bước chân, Trò chơi, Viễn tưởng, Đồ vật, Động vật.
  - Mỗi âm có tên tiếng Việt, độ dài (ví dụ "Bóng cao su nảy, 0,14 giây"), nút nghe thử, nút Thêm. Có ô tìm kiếm.
  - **Miễn phí 1 âm thanh mỗi khung** ("Số ô âm thanh đã dùng ở khung này: 1/1"). Thêm âm thứ hai vào cùng khung thì hiện "Nhiều âm thanh mỗi khung là tính năng Pro".
  - Trên dải khung, khung có âm hiện icon loa và số; âm kéo dài thì hiện "âm thanh tiếp tục từ khung 6".
- "Âm thanh của tôi": nút Nhập, "Nhập âm thanh từ thiết bị để dùng trong mọi dự án".
- Âm nhạc: "Thêm nhạc hoặc giọng thuyết minh". Nhạc bắt đầu từ khung đang ở, có cắt và to dần, nhỏ dần. Nút "Thêm tệp âm thanh" nhận MP3, WAV, AAC, M4A. **Không có thư viện nhạc sẵn, không thấy nút ghi âm.**
- Thanh trên của màn vẽ có công tắc "Tắt tiếng".

**Cài đặt** (cây giao diện):
- Premium. Đánh giá và Gửi phản hồi (không mở).
- Tài khoản: "Đăng, thích và remix với tài khoản miễn phí", Google hoặc Apple.
- Giao diện Hệ thống, Sáng, Tối; Ngôn ngữ.
- Bút:
  - Chỉ dùng bút cảm ứng.
  - Ống hút màu khi chạm giữ: tắt, 0,3, 0,5 hoặc 0,8 giây.
  - Lớp raster (Labs): lớp GPU có cọ mềm, làm mờ, làm nhòe.
  - Lưới trong suốt.
- Mẹo trình chỉnh sửa (xem lại tour).
- Bố cục:
  - Cỡ chữ.
  - Độ tương phản cao.
  - Gọn hoặc Thoáng.
  - Thanh công cụ đặt Trái, Phải hay Dưới.
  - Đặt lại bố cục.
- Thông báo.
- Mặc định cho dự án mới: FPS 8, 12, 15, 24 hoặc 30; khổ canvas; giấy; lưới; onion skin.
- Khôi phục giao dịch, cộng đồng Reddit, phiên bản, mã hỗ trợ (không chép), chính sách.

**Cộng đồng** (`39`):
- Chủ đề tuần (ví dụ "Vòi nước nhỏ giọt") và nút "Chia sẻ tác phẩm của bạn".
- Bảng xếp hạng: Tuần này, Tháng này, Đại sảnh danh vọng.
- Lọc: Đang theo dõi, Nổi bật, Chủ đề, Mới, Có thể phối lại.
- Thích. Đăng bài cần tài khoản (không thử).

## 5. Chức năng chính

### 5.1 Màn vẽ

Số đo dp lấy từ cây giao diện (`10-editor-empty`), khung 393x873 dp.

| Vùng | Vị trí, cỡ | Nội dung |
| --- | --- | --- |
| Thanh trên | y 34 đến 82, nút 48x48 | Quay lại, Hoàn tác, Làm lại, Onion (huy hiệu số khung), Đối xứng, Phát (nền đỏ khi có từ 2 khung), Lặp, (Tắt tiếng ở bài học), Xuất (viền đậm) |
| Công cụ nhanh | y 94 đến 154, nút 50x60, viên thuốc bo tròn nổi | Tay nắm, cọ, Kích thước, Màu, Độ mờ, Ổn định nét, Ẩn |
| Canvas | Rộng 393 dp, khổ theo dự án (16:9 khoảng 393x221), giữa vùng xám | Giấy trắng có lưới mờ |
| Cột trái | x 8, nút tròn 48 dp, y 411 đến 627 | Dải bảng màu, Âm thanh, Nền khung hình, Hiện hoặc ẩn thanh công cụ |
| Cột phải | x 337, thẻ dọc bo tròn | Ẩn điều khiển, Biến đổi, Phóng to, (100%), Thu nhỏ, Chế độ di chuyển |
| Thanh thời lượng | y 635 đến 665 | Nhanh, thanh trượt, Chậm, Đặt lại, nhãn "12fps" |
| Hàng thao tác khung | y 665 đến 713, nút 33 đến 42 dp | Thêm khung (+ trong ô nét đứt đỏ), Nhân bản, Xóa, Tạo khung trung gian (khóa), Thêm âm thanh, Dời lên trước, Dời ra sau, Khung trước, "1 / 12", Khung sau |
| Dải khung | y 713 đến 785, thumbnail 60 đến 84 x 64 dp | Khung đánh số ở góc (số đỏ cho khung đang chọn), icon loa trên khung có âm thanh |
| Thanh dưới | y 788 đến 856, nút 48x68 có chữ | Vẽ (tên cọ), Cọ vẽ, Tẩy, Chọn, Tô màu, Màu sắc (ô màu hiện tại), Lớp, Thêm |

- Vẽ bằng `drag`: nét liền, mượt. Cọ mặc định Bút kim 8 px đen, ổn định 50% (`11-first-strokes`).
- Chạm lại công cụ đang chọn ở thanh dưới thì mở bảng cài đặt cọ.
- Một lần, cọ "Bút mềm" (nhóm Cổ điển) không ra nét trên khung 2, dù khung 1 vẽ được (`16-frame2-onion`). Đổi sang Bút kim thì vẽ bình thường. Có thể cọ này dựa vào lực nhấn (adb gửi lực nhấn cố định), chưa rõ.
- Thoát màn vẽ bằng Back: về Studio, tự lưu, không hỏi, không quảng cáo.

### 5.2 Dòng thời gian

- Thêm khung: nút + (khung trống mới sau khung hiện tại). Nhân bản khung, Xóa, Dời trước, Dời sau.
- Chạm khung đang chọn để sao chép (theo tour).
- Không thấy chọn nhiều khung trong hàng thao tác; chưa thử nhấn giữ thumbnail.
- **Tốc độ** (Thêm > Canvas > Tốc độ):
  - FPS 8, 12 (mặc định), 15, 24, 30.
  - **Thời lượng riêng từng khung** ("Khung hình 1 / 12, ×1 · 83ms", thanh Nhanh, Chậm). Đây là cách giữ một khung lâu hơn mà không phải nhân bản.
- Phát:
  - Màn phát toàn màn hình: canvas giữa nền xám, logo FlipAnimation mờ trên canvas, viên điều khiển dưới đáy (Dừng, "2/2", Lặp) (`19-playing`).
  - Phím Back thoát khỏi phát và thoát luôn màn vẽ về Studio.
  - Khi đang phát, `uiautomator` không lấy được cây giao diện ("could not get idle state"), nên công cụ không chạm được nút Dừng.

### 5.3 Tự sinh khung giữa

- Nút "Tạo khung hình trung gian" có icon khóa tím.
- Chạm vào thì hiện hộp thoại "Tạo khung hình trung gian tự động là tính năng Pro. Vẽ các tư thế chính, FlipAnim sẽ vẽ những khung hình ở giữa cho bạn. Mở khóa với…", nút "Để sau", "Nâng cấp" (`17-inbetween`).
- **Không dùng thử được, không có lượt miễn phí**, nên chưa thấy cách hoạt động (chọn hai khung, số khung chèn, kiểu nội suy).
- Theo APK, khung giữa sinh từ nét vector (`inbetween_engine`, `inbetween_generator`).
- Bài học "Khung chính và khung trung gian" dạy quy trình vẽ tay: tư thế chính màu đỏ, khung giữa vẽ sau.

### 5.4 Giới hạn của bản miễn phí

- **4 dự án**: tạo dự án thứ 5 thì hiện hộp thoại "Nâng cấp lên Pro. Gói miễn phí cho phép tối đa 4 dự án. Nâng cấp lên Premium để có dự án không giới hạn…", nút Để sau, Nâng cấp (`21-project-limit`). Không có lựa chọn xem quảng cáo.
- **150 khung**: thêm khung thứ 151 thì app hiện thẻ nổi mời nâng cấp, tự ẩn (mục 5.5).

### 5.5 Giới hạn 150 khung

Đo lại sau khi máy được cắm lại, lúc 21:48 đến 21:53, tiếp từ 108 khung. Mỗi nhóm 5 lần chạm, trước mỗi nhóm kiểm con trỏ kẹt và màn trên cùng.

- **Giới hạn đúng 150 khung mỗi dự án.** Chạm + ở khung thứ 150 thì số khung không tăng, và app hiện một **thẻ thông báo nổi** ở đầu canvas (`43-frame-limit-150`):
  - Chữ: "Gói miễn phí giới hạn 150 khung hình mỗi dự án. Nâng cấp PRO để không giới hạn."
  - Có icon tài liệu, nút X "Đóng" và nút chữ "Nâng cấp" màu xanh.
- Thẻ này không phải hộp thoại chặn: không có lớp mờ, vẫn vẽ được. Thẻ **tự ẩn sau vài giây**, nên lần đầu chụp không kịp; chạm + lần nữa thì nó hiện lại.
- Không có rewarded hay cách mở khác. Không chạm "Nâng cấp".
- Đây là cách mời mua nhẹ nhất trong app, khác hộp thoại "là tính năng Pro" của các giới hạn khác.
- Thanh khung hiện "43/150"; thẻ dự án trên Studio hiện "16:9, 150 khung hình · 12 FPS".
- Tập E01 của loạt Mochi có 78 khung. Chưa kiểm bài học có bị giới hạn 150 khung không.

### 5.6 Xuất (`32` đến `35`)

Đầu bảng ghi "12 khung hình ở 12 FPS · 1080 × 1080 · 1.0 giây".

Định dạng:
- GIF.
- Video ("Video HD cho mạng xã hội").
- **PNG động (trong suốt)**, tức APNG: "Viền trong suốt mềm, thời gian chính xác, lặp lại".
- ZIP (Tất cả khung hình), 1 lần miễn phí.
- PNG (khung này).
- **Bản in** ("In và cắt sổ lật của bạn"), 1 lần miễn phí.

Tùy chọn:
- Độ phân giải: Gốc, 75%, 50%, 25%, hoặc kích thước chính xác 240, 512, 520 px, 540×960.
- Bản 1080×1080 ra GIF 720×720 và video 1080×1080. Video 1080 miễn phí với dự án ngắn này.
- Nền trong suốt (không giấy, không lưới).
- Hình nền.
- **Logo FlipAnimation bật sẵn.** Tắt thì hiện "Xuất không logo là tính năng Pro…".

Xuất thử GIF (bài Quả bóng nảy, 12 khung):
- Màn chờ có vòng phần trăm, "1 / 12", "Đang xuất...", nút Hủy. Mất khoảng 15 giây.
- Xong thì hiện hộp thoại nhắc (mục 2), rồi màn "Xuất hoàn tất": xem trước GIF, tên file `flipanimation_<số>.gif`, "GIF · 429.0 KB".
- Màn hoàn tất có:
  - Đăng lên cộng đồng FlipAnimation.
  - Ghi chú "TikTok, YouTube và Instagram nhận video, không nhận GIF", nút "Xuất dạng Video".
  - Chia sẻ.
  - Lưu vào Tệp.
- Không chạm Chia sẻ, Đăng, Lưu vào Tệp. `ax.sh newfiles` không thấy file mới trong Pictures, DCIM (mục 10): app giữ file trong bộ nhớ riêng tới khi người dùng chia sẻ hay lưu.
- **Không quảng cáo trước, trong hay sau khi xuất.**

### 5.7 Bài học và loạt truyện

Đây là phần nhóm muốn học cho `:feature:lessons`.

**Tab Học** (`23-learn-tab`):
- Đầu tab: "Học hoạt hình", "Vẽ theo đường dẫn màu đỏ, tạo hoạt hình của bạn!", "115 bài hướng dẫn", "16 miễn phí".
- Hàng thẻ nhóm cuộn ngang (136x157 dp, hình minh họa vẽ tay). Lọc "Tất cả" và "Đang thực hiện" (có đếm, ví dụ "Đang thực hiện, 1").
- Danh sách theo nhóm, mỗi nhóm có tiêu đề, số bài, một dòng mô tả:

  | Nhóm | Số bài | Mô tả |
  | --- | --- | --- |
  | Nền tảng hoạt hình | 12 | Học các nguyên lý hoạt hình cơ bản: Quả bóng nảy, Con lắc đung đưa, Biến đổi hình khối, Ngôi sao xoay, Chuyển động sóng, Vào chậm và ra chậm, Vòng cung, Cái đuôi theo đà... |
  | 12 Nguyên lý hoạt hình | 14 | Thời gian là trọng lượng, Lấy đà, Khung chính và khung trung gian, Cường điệu, Nén và giãn, Dàn cảnh, Hành động phụ... |
  | Hình khối và đồ vật | 11 | Trái tim đập, Bông hoa lớn lên, Hành tinh, Ngọn nến, Đồng hồ, Bánh răng, Đồng xu lật... |
  | Hoạt hình nhân vật | 14 | Bàn tay vẫy, Khuôn mặt chớp mắt, Người đi, Mèo nhảy, Người chạy, Gương mặt trò chuyện... |
  | Hiệu ứng đặc biệt | 12 | Pháo hoa, Mưa, Tia sét, Khói, Lửa trại, Vụ nổ, Tuyết... |
  | Cảnh ngắn | 52 | Bình minh, Cá bơi, Chim bay, Lá rơi, Tên lửa... (cảnh hoàn chỉnh, nhiều chuyển động chồng lớp) |

- **Thẻ bài học** (353x124 dp):
  - Thumbnail giấy kẻ ô có đường dẫn đỏ, nhãn số khung ("12 khung hình").
  - Tên, mô tả một dòng, chip độ khó (Cơ bản, Trung bình, Nâng cao), tem "MIỄN PHÍ" kiểu con dấu.
  - Bài khóa ghi "Đã khóa. Mở khóa với Pro".
  - Bài miễn phí rải đều trong các nhóm (2 đến 3 bài mỗi nhóm), không dồn ở đầu.
- **Bài khóa**: chạm thì hiện "Thư viện bài học đầy đủ là tính năng Pro. Bài học này thuộc thư viện đầy đủ: hơn 100 hoạt hình có hướng dẫn để đồ theo…" (`36-locked-lesson`).

**Mở bài học** (Quả bóng nảy, `24-lesson-intro`):
- **Vào thẳng màn vẽ, không có màn giới thiệu.** Dự án dựng sẵn 12 khung.
- Mỗi khung có **đường dẫn màu đỏ** (hình tròn ở vị trí của khung đó), đường mặt đất, và **biểu đồ khoảng cách** (timing chart) màu xanh ở bên phải.
- Âm thanh đã gắn sẵn ở khung 6 (lúc bóng chạm đất).
- Onion skin bật 2 khung trước.
- Người học tô lại đường đỏ trên từng khung. Đường dẫn không phải một lớp: bảng Lớp chỉ có "Layer 1" và "Giấy" (`25-lesson-layers`).
- Thanh trên có thêm công tắc Tắt tiếng.
- Thoát ra thì bài nằm ở "Đang thực hiện". Không thấy chấm điểm hay màn "hoàn thành" (chưa vẽ hết 12 khung).
- Bài học không tính vào 4 dự án.

**Loạt truyện** (`37-series-mochi`, `38-episode-open`):
- Ba loạt trên Studio:
  - Mochi: "loạt mới", 8 tập, 3 miễn phí.
  - Giải cứu nhà vua: 20 tập, 2 miễn phí.
  - Stickman Cơ bản: 10 tập, 2 miễn phí.
- Thẻ loạt (163x168 dp) giống tờ giấy có kẹp, góc ghi "1/8", băng tên dán chéo kiểu washi tape, tem "MỚI".
- Trang loạt Mochi:
  - Breadcrumb "Loạt truyện / Mochi", tên, "Mùa 1 · 8 tập".
  - Mô tả "Một chú mèo nhỏ tròn xoe học một nguyên lý hoạt hình trong mỗi chuyến phiêu lưu".
  - Nút chính "Bắt đầu E01. Boing!".
  - Danh sách tập: E01 Boing! (nảy), E02 Cú nhảy lớn, E03 Đuổi bắt bướm (đường cong), E04 Ngày mưa, E05 Hang chuột (thời điểm), E06 Bay cùng bóng bay, E07 Bữa ăn khuya, E08 Bất ngờ sinh nhật. Mỗi tập một câu chuyện ngắn gắn với một nguyên lý và có độ khó.
- Mở tập E01: màn vẽ với **78 khung**. Nhân vật Mochi vẽ sẵn màu xám, cảnh nền (mây, cờ). Người học vẽ quả bóng theo đường đỏ. Âm thanh gắn sẵn ở nhiều khung.
- Cách dẫn người mới:
  - Nút "Thử bài học miễn phí" ngay ở trạng thái trống.
  - Bài miễn phí đầu tiên ở đầu tab Học.
  - Loạt truyện có nhân vật để giữ chân.
  - Mọi bài dùng chung màn vẽ thật, nên học xong là đã quen công cụ.

## 6. Kiếm tiền

| Loại quảng cáo | Vị trí | Thời điểm | Tần suất | Thời gian chờ đóng | Ảnh |
| --- | --- | --- | --- | --- | --- |
| (không có) | | | | | |

**Không có quảng cáo nào.** Đã kiểm ở:
- Splash, onboarding, Studio, Học, Cộng đồng, màn vẽ, lúc phát, xuất, thoát màn vẽ.
- Mở và đóng nhiều bài học.
- Phiên dài khoảng 45 phút, nhiều lần thoát và mở màn vẽ.

Khớp với APK: không SDK quảng cáo, không quyền `AD_ID`, không có hộp thoại đồng ý quảng cáo. **Về màn hình chính 35 giây rồi mở lại** (21:46:45, `42-resume-35s`): app trở lại đúng tab Studio. Không có quảng cáo, không có paywall, không có splash.

**Gói Pro** (`22-paywall`, giá vùng Việt Nam):
- **Trọn đời 140.000 ₫, trả một lần**, chọn sẵn. Nhãn "GIÁ TRỊ NHẤT" và tem "SIÊU HỜI"; chữ "gói đăng ký" bị gạch ngang, thay bằng "một lần".
- **Hàng tuần 28.000 ₫/tuần.**
- Không có gói tháng hay năm, **không có dùng thử**.
- Nút "Nâng cấp - 140.000 ₫". Dưới nút: Khôi phục giao dịch, Điều khoản, Quyền riêng tư, chú thích thanh toán qua Google Play.
- Quyền lợi (lưới thẻ có icon vẽ tay):
  - Video Rotoscope ("Dự án tham khảo video không giới hạn", thẻ lớn ở đầu).
  - Tất cả bài học.
  - Không watermark.
  - Dự án không giới hạn.
  - Lớp không giới hạn.
  - Tất cả loạt truyện.
  - Tạo khung hình trung gian tự động.
  - Công cụ văn bản.
  - Thư mục.
- Paywall không hiện tự động lúc nào. Chỉ vào được từ:
  - Nút "PREMIUM" vương miện trên thanh tab.
  - Mục Premium trong Cài đặt.
  - Nút "Nâng cấp" của các hộp thoại "là tính năng Pro".
- Đóng bằng X góc trên phải, hiện ngay.

**Hộp thoại mời mua**:
- Cùng một mẫu: tiêu đề "<tính năng> là tính năng Pro", một câu nói lợi ích cụ thể, "Để sau" và "Nâng cấp".
- Hiện khi chạm:
  - Khung giữa tự động.
  - Dự án thứ 5.
  - Âm thanh thứ hai trong một khung.
  - Tắt logo.
  - Bài học khóa.
  - (theo icon khóa) Văn bản, Thư mục.
- Không có rewarded hay cách mở bằng quảng cáo.

Bảng khóa:

| Miễn phí | Pro |
| --- | --- |
| Mọi cọ (khoảng 31), tô màu, lớp không giới hạn ("hiện đang miễn phí"), onion skin, đối xứng, hình dạng | Văn bản, Thư mục |
| 4 dự án, 150 khung mỗi dự án (đã thấy cả hai) | Không giới hạn |
| 16 bài học, 7 tập truyện | 115 bài, 38 tập |
| Thư viện hiệu ứng âm thanh đầy đủ, 1 âm mỗi khung; nhập nhạc | Nhiều âm mỗi khung |
| GIF, video, APNG, PNG khung; ZIP và Bản in 1 lần; có logo | Không logo, ZIP và Bản in không giới hạn |
| Đồ theo video 2 lần | Không giới hạn |
| (không) | Khung giữa tự động |

## 7. Giao diện

- **Phong cách giấy và mực vẽ tay**:
  - Nền giấy ngà kẻ ô.
  - Nút và thẻ viền đen vẽ tay hơi lệch, có bóng đổ cứng lệch xuống dưới phải.
  - Tiêu đề gạch chân bằng nét bút đỏ.
  - Tem "MIỄN PHÍ" kiểu con dấu; băng dán washi; kẹp giấy trên thẻ.
  - Hình minh họa nhân vật que, linh vật tròn.
- Màu, đọc từ ảnh (`05-after-onb`, `22-paywall`):
  - Đỏ san hô cho nút chính ("Bắt đầu sáng tạo", "Tiếp theo", "Nâng cấp", Phát) và gạch chân.
  - Tím cho nhãn PRO, khóa và vương miện.
  - Chữ đen nâu đậm.
  - Nền giấy kem nhạt.
  - Mã hex chưa lấy bằng `ax.sh color`; cần lấy khi dựng Figma.
- Phông:
  - Tiêu đề đậm, chữ hơi tròn (logo vẽ tay hai màu đỏ, đen).
  - Nội dung sans thường.
  - APK có Pangolin, Balsamiq Sans, Bangers, Amatic SC, Pacifico.
- Icon nét mảnh, viền đen, có nét vẽ tay.
- Studio:
  - Thanh trên: logo 152x33 dp; cụm nút Tìm kiếm, Thư mục, Cài đặt 44 dp trong khung viền đen bo tròn.
  - Tab chữ Studio, Học, Cộng đồng (nhãn "MỚI" đỏ), gạch chân đỏ dưới tab đang chọn; nút PREMIUM vương miện tím bên phải (67x44 dp).
  - Trạng thái trống: avatar tròn, tiêu đề 23 dp, mô tả, hai nút chính 329x56 dp (đỏ đặc và trắng viền).
  - Mỗi mục: tiêu đề có icon, đường kẻ ngang, "Xem tất cả ›" đỏ; hàng thẻ cuộn ngang.
- Hộp thoại "là tính năng Pro": thẻ giấy giữa màn, tiêu đề 2 dòng, mô tả 3 dòng, "Để sau" (chữ) và "Nâng cấp" (nút đỏ) 48 dp.
- Paywall: logo + nhãn PRO, nút đóng tròn 50 dp; lưới quyền lợi 2 cột (171 dp); hai thẻ giá 171x142 dp (thẻ chọn viền đậm); nút chính 353x55 dp.
- Màn vẽ: xem bảng ở mục 5.1. Hai thanh công cụ (nhanh và dưới) cùng kiểu viên thuốc viền đen.
- Có chế độ tối, độ tương phản cao, chỉnh cỡ chữ, đặt thanh công cụ trái, phải hay dưới (Cài đặt).

## 8. Điểm mạnh, điểm yếu

Mạnh:
- **Vào vẽ cực nhanh**: 4 thao tác, không paywall, không hỏi tuổi, không quảng cáo, không xin quyền lúc mở.
- **Không quảng cáo, bản miễn phí hào phóng**: mọi cọ, lớp không giới hạn, thư viện âm thanh đầy đủ, video 1080. Mời mua nhẹ nhàng: mỗi hộp thoại nói rõ lợi ích, có "Để sau".
- **Gói trọn đời 140.000 ₫** bên cạnh gói tuần: hợp người dùng ghét thuê bao.
- **Bài học là điểm khác biệt lớn nhất**:
  - 115 bài theo 6 nhóm có mạch nhỏ dần, cộng 3 loạt truyện có nhân vật.
  - Mỗi bài là dự án thật có đường dẫn đỏ, biểu đồ khoảng cách, âm thanh sẵn; mở ra là vẽ.
  - Ngôn ngữ bài học dạy đúng nguyên lý (khoảng cách, lấy đà, nén giãn).
- Tour màn vẽ 9 bước, bỏ qua được, xem lại được.
- Âm thanh gắn theo khung, rất hợp hoạt hình lật trang; tên âm thanh dịch tiếng Việt.
- Thời lượng riêng từng khung; lớp "giữ qua nhiều khung hình".
- Xuất APNG nền trong, "Bản in" sổ lật, khổ nhãn dán Telegram, WeChat.
- Hỏi quyền thông báo sau khi người dùng đã xuất xong, bằng hộp thoại riêng giải thích lý do.
- Bản địa hóa tiếng Việt tự nhiên, đầy đủ.

Yếu:
- Màn vẽ dày nút: 4 hàng điều khiển dưới canvas (thời lượng, thao tác khung, dải khung, thanh dưới), cùng cột trái và cột phải. Canvas 16:9 chỉ còn khoảng 25% chiều cao màn.
- Khung giữa tự động khóa hoàn toàn, không thử được lần nào: người dùng không biết nó tốt thế nào trước khi trả tiền.
- Phím Back khi đang phát thoát luôn màn vẽ.
- Bàn phím che nút Tạo trong hộp thoại Dự án mới.
- Không chọn FPS khi tạo dự án (phải vào Thêm > Canvas > Tốc độ).
- Xuất GIF chậm, khoảng 1 giây mỗi khung 1080×1080.
- Không có thư viện nhạc sẵn, không ghi âm.
- Gói tuần 28.000 ₫, tức khoảng 1,46 triệu ₫ một năm, đắt so với gói trọn đời. Đây có thể là chủ ý để đẩy người mua sang trọn đời.

## 9. Chưa xem được

- **Khung giữa tự động**: khóa Pro, không có lượt thử. Cần mua để xem.
- **Đồ theo video** (rotoscope): còn 2 lượt miễn phí nhưng cần chọn video trong máy; không làm để tránh mở thư viện ảnh.
- Công cụ Chọn, Biến đổi, Hình dạng, Lưới, Nền, Đối xứng (chỉ thấy nút); Văn bản (Pro); Thư mục (Pro); xưởng tạo bộ cọ.
- Nhập âm thanh, nhập nhạc (cần chọn file); "Lưu vào Tệp", Chia sẻ, Đăng lên cộng đồng (bị cấm).
- Hoàn thành trọn một bài học (màn kết thúc, nếu có).
- Chọn nhiều khung, nhấn giữ thumbnail.
- Tài khoản, đăng bài cộng đồng, remix (cần đăng nhập).
- Chế độ tối, lớp raster (Labs).
- Mã hex chính xác của màu.

**Góp ý cho công cụ:**
1. `seq` tách đối số theo dấu cách: `seq "taptext Bắt đầu sáng tạo"` chạm chữ "Bắt" và trúng dòng mô tả "Bắt đầu từ khung vẽ trống…", không trúng nút. `seq "taptext Tiếp theo"` chạy đúng chỉ vì trùng. `seq` nên giữ phần còn lại của bước làm một đối số, hoặc nhận ngoặc kép.
2. Khi `uiautomator dump` lỗi, `tree` và `guard.py` in traceback Python (`ParseError: no element found`) thay vì một dòng báo lỗi. Bộ chặn vẫn từ chối đúng (mã 4), nhưng nên bắt lỗi này.
3. Màn phát hoạt hình của Flutter không bao giờ "idle", nên không lấy được cây và không chạm được nút Dừng; chỉ `back` thoát được, mà `back` lại thoát luôn màn vẽ. Nên có lệnh chạm theo tọa độ khi cây lỗi, chỉ cho phép khi activity là của app mẫu, không phải activity quảng cáo, và người giao việc cho phép.
4. Cần một lệnh lặp có kiểm tra, ví dụ `ax.sh repeat N "<bước>" --until-unchanged`. Vòng lặp tự viết bằng bash phải gọi `tree` sau mỗi lần chạm, nên tốn khoảng 6,7 giây mỗi lần.
5. `ax.sh` nên đọc `dumpsys input` khi một lần chạm không làm màn hình đổi, và báo con trỏ kẹt bằng mã riêng.

## 10. Sự cố trong lúc khảo sát

`incidents.log` có 1 dòng mới (21:24):
- Vòng thêm khung dừng ở 108 khung vì một lần chạm không làm bộ đếm tăng.
- Cây giao diện sau đó không còn dòng bộ đếm khung.
- `dumpsys input` thấy con trỏ id=0 kẹt trên cửa sổ InputMethod và `miui-gesture`, giống sự cố con trỏ kẹt ở lần khảo sát FlipaClip. Đã dừng chạm theo lời giao việc.
- Ngay sau đó adb mất thiết bị (mã 5). Chờ khoảng 4 phút không thấy lại, nên dừng phiên.
- Người đã cắm lại máy và đóng các app. Ba phần còn thiếu làm lại lúc 21:46 đến 21:54 (mục 5.5, 6, và phần dưới).

Khác:
- Không có lần chạm nào vào quảng cáo (app không có quảng cáo).
- Không mở Play, trình duyệt hay app khác. Không có gì tự tải hay cài.
- App không văng.
- Cọ "Bút mềm" một lần không vẽ được trên khung 2 (mục 5.1). Có thể do lực nhấn của adb, không phải lỗi của app.
- Không mua, không dùng thử, không đăng nhập, không chia sẻ, không đăng gì.
- Đã xuất 1 GIF (`flipanimation_<số>.gif`, 429 KB) vào bộ nhớ riêng của app, chưa lưu ra thư viện.

Dữ liệu app để lại trên máy, mất khi `pm clear`:
- 4 dự án: "Hoạt hình của tôi" khoảng 108 khung, và 3 bản sao.
- Bài "Quả bóng nảy" đang làm dở.

File để lại trên máy (`ax.sh newfiles`, 21:54): **"không có file mới trên máy"**.

**Bổ sung sau khi người cắm lại máy (21:40 đến 21:45): sự cố thanh toán, dừng phiên.**
- Lúc cắm lại, màn trên cùng là Google Play "Quét thẻ quà tặng". Dưới nó là trang "Đổi mã" của luồng thanh toán Play, mở trong task của FlipAnimation. Không chạm gì trong hai màn này; chỉ `back` 2 lần rồi `home`. Mở lại app thì trang thanh toán Play vẫn nằm trên cùng.
- Logcat cho thấy diễn biến:
  - 21:19:21: lần chạm giả lập cuối của `ax.sh` (vòng thêm khung).
  - 21:19:53: app mở `ProxyBillingActivity`, rồi bảng mua hàng của Play.
  - 21:19:57 và 21:20:05: mở "thêm phương thức thanh toán".
  - Từ 21:20:39: mở "Quét thẻ quà tặng" nhiều lần.
- Các lần chạm từ 21:20:58 tới 21:21:44 không có dòng "Input event injection", nên không do `ax.sh` gửi. Có cả cử chỉ vuốt từ cạnh màn hình. Đây là chạm vật lý hoặc chạm ma; cùng lúc đó `dumpsys input` thấy con trỏ kẹt.
- Không thấy log mua thành công hay kích hoạt Pro. Máy không có phương thức thanh toán, nên luồng dừng ở bước thêm thẻ hoặc đổi mã.
- **Cần người**:
  - Kiểm tra lịch sử đơn hàng của tài khoản Google trên máy.
  - Đóng luồng thanh toán Play đang mở.
  - Xem màn hình có bị chạm ma không (đã thấy con trỏ kẹt 2 lần trong ngày).
- Sau khi người đóng các app (21:46), làm nốt ba phần với quy tắc chặt hơn: trước mỗi nhóm 5 lần chạm, kiểm con trỏ kẹt và màn trên cùng. Cả phiên làm lại không thấy con trỏ kẹt, màn trên cùng luôn là `MainActivity` của app, không có màn thanh toán nào.
- Dữ liệu app trên máy hiện có dự án "Hoạt hình của tôi" 150 khung.
