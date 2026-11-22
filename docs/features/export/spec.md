---
feature: export
status: changed
owner: Nhật
product_owner: Nhật
requests:
  - requests/2026-11-21-ban-dau.md
design: design/README.md
created: 2026-11-21
confirmed: 2026-11-21
---

# SPEC: XUẤT HOẠT HÌNH

## 1. Mục tiêu

- **Người dùng được gì:** biến hoạt hình vừa vẽ thành một video MP4, một GIF hay một chuỗi ảnh PNG, lưu thẳng vào thư viện ảnh của máy và chia sẻ ngay. Bản miễn phí có watermark; bỏ watermark, chuỗi PNG và nền trong suốt mở bằng đúng một lần xem quảng cáo cho lần xuất đó.
- **Vì sao làm bây giờ:** xuất là tính năng bắt buộc, mọi app mẫu đều có (bản đề xuất mục 5). Đây là lúc người dùng có thành quả để giữ và khoe, và là chỗ có quảng cáo có thưởng mà người dùng tự chọn xem.
- **Đo kết quả bằng gì:** tỷ lệ lần xuất thành công trên lần bắt đầu xuất, tỷ lệ lần xuất lỗi, và số lần mở tùy chọn thêm bằng quảng cáo có thưởng (sự kiện ở mục 6). Mức mong đợi chưa có trong bản đề xuất.

## 2. Phạm vi

**Trong phạm vi:**

- Màn Xuất mở từ nút "Export" trên màn vẽ và từ nút "Export my animation" trên màn Hoàn thành bài học (thiết kế của feature lessons).
- Thẻ tóm tắt dự án: ảnh khung đầu, tên, số khung, tốc độ khung, thời lượng, tỷ lệ khung và độ phân giải.
- Chọn định dạng: MP4 video (có âm thanh của dự án), GIF (lặp, không âm thanh), chuỗi PNG (một ảnh mỗi khung, luôn trong suốt), GIF trong suốt (không nền); bốn định dạng theo thiết kế (câu hỏi 1).
- Chọn chất lượng 720p hay 1080p.
- Watermark Frameloop bật sẵn. Bỏ watermark, chọn chuỗi PNG hay nền trong suốt cần xem một quảng cáo có thưởng; một lần xem mở cả ba cho lần xuất đó của dự án.
- Màn chờ xuất có thanh tiến độ, số khung đã làm, phần trăm, thời gian còn lại ước tính và nút Hủy.
- Xuất xong: file tự lưu vào thư viện máy; màn xong có ảnh xem trước, đường dẫn và dung lượng file, nút chia sẻ, mở trong thư viện, quay lại màn vẽ.
- Xuất lỗi: báo lý do bằng lời dễ hiểu, khẳng định dự án vẫn an toàn, có nút thử lại.
- Quyền lưu vào thư viện trên Android 9 trở xuống.
- Chế độ sáng và tối, màn hình thấp 360 × 640, điện thoại màn hình gập.

**Ngoài phạm vi lần này:**

- Xuất chạy nền: tiếp tục xuất khi người dùng đã rời app, có thông báo tiến độ (bản đề xuất mục 5, nhóm không làm). Rời app khi đang xuất thì xem KB-7 và câu hỏi 3.
- Chỉnh sửa video sau khi xuất (cắt, thêm nhạc, đổi tốc độ), xuất một đoạn khung, độ phân giải trên 1080p, 60 khung/giây.
- Đăng lên mạng xã hội hay cộng đồng trong app; chỉ chia sẻ qua bảng chia sẻ của máy.
- Chế độ ngoại tuyến: app luôn cần mạng (ADR-0024, bản đề xuất Q8). Mất mạng do app chặn bằng popup chung; feature không tự kiểm tra mạng.

## 3. Kịch bản sử dụng

### KB-1. Mở màn Xuất

Người dùng đang ở màn vẽ của một dự án, hay ở màn Hoàn thành bài học, và muốn xuất hoạt hình.

Tiêu chí chấp nhận:

- [ ] Khi bấm "Export" trên màn vẽ thì màn Xuất mở ở trạng thái chọn tùy chọn, tiêu đề "Export", có nút đóng ở góc trên trái.
- [ ] Khi bấm "Export my animation" trên màn Hoàn thành bài học thì màn Xuất mở cho dự án của bài học đó, như trên.
- [ ] Khi màn Xuất mở thì thẻ tóm tắt hiện ảnh khung đầu, tên dự án, dòng "<số khung> frames · <tốc độ> fps · <thời lượng> s" và dòng "<tỷ lệ khung> · <rộng> × <cao>" của dự án; ví dụ dự án 48 khung, 12 khung/giây, 9:16, 1080 × 1920 hiện "48 frames · 12 fps · 4.0 s" và "9:16 · 1080 × 1920".
- [ ] Khi mở màn Xuất thì những gì vừa vẽ đã được lưu, không mất nét nào.
- [ ] Khi bấm nút đóng hay nút quay lại của hệ thống ở trạng thái chọn tùy chọn thì app về màn đã mở màn Xuất, không có quảng cáo toàn màn hình, không xuất gì.

Trường hợp đặc biệt:

- [ ] Khi không có mạng thì app hiện popup mất mạng chung của app; feature không tự báo lỗi mạng.
- [ ] Khi xoay máy hay mở, gập máy gập ở màn Xuất thì lựa chọn đang có và tùy chọn đã mở khóa giữ nguyên.

### KB-2. Chọn định dạng, chất lượng, watermark

Tiêu chí chấp nhận:

- [ ] Khi màn Xuất mở thì MP4 video được chọn sẵn, chất lượng bằng độ phân giải của dự án được chọn sẵn (dự án 1080p thì "1080p"; dự án 720p thì không có chip "1080p"; câu hỏi 13), watermark bật; nút ghim đáy ghi "Export MP4".
- [ ] Khi chạm một thẻ định dạng miễn phí (MP4 video, GIF) thì thẻ đó được chọn, có viền nhấn, các thẻ khác bỏ chọn; nút ghim đáy đổi chữ theo định dạng: "Export MP4", "Export GIF", "Export PNGs", "Export transparent GIF" (câu hỏi 6).
- [ ] Khi chạm 720p hay 1080p thì chỉ một mức được chọn.
- [ ] Khi tùy chọn thêm chưa mở thì thẻ chuỗi PNG và thẻ GIF trong suốt có nhãn "Ad", và dòng watermark ghi "Turn off by watching one ad".
- [ ] Khi danh sách tùy chọn dài hơn màn hình thì nội dung cuộn được, nút xuất ghim đáy luôn thấy được và không che thẻ cuối khi cuộn hết.

### KB-3. Mở tùy chọn thêm bằng một quảng cáo có thưởng

Người dùng muốn bỏ watermark, xuất chuỗi PNG hay GIF trong suốt.

Tiêu chí chấp nhận:

- [ ] Khi chạm thẻ chuỗi PNG, thẻ GIF trong suốt, hay gạt tắt watermark lúc tùy chọn thêm chưa mở thì app hỏi người dùng có muốn xem một quảng cáo không; chưa đổi lựa chọn nào.
- [ ] Khi người dùng xem hết quảng cáo và được thưởng thì cả ba tùy chọn mở cho lần xuất này của dự án: nhãn "Ad" biến mất, lựa chọn vừa chạm được áp dụng (thẻ được chọn, hay watermark tắt), các tùy chọn còn lại chọn được không cần xem thêm.
- [ ] Khi đã mở thì đổi qua lại giữa các định dạng, chất lượng, bật tắt watermark nhiều lần không cần xem thêm quảng cáo nào trong lần xuất đó.
- [ ] Khi người dùng từ chối xem hay đóng quảng cáo trước khi được thưởng thì tùy chọn không mở, lựa chọn trước đó giữ nguyên, watermark vẫn bật.
- [ ] Khi chưa có quảng cáo để xem thì app làm theo chính sách chung của quảng cáo có thưởng (câu hỏi 14, spec editor câu 4): tùy chọn chưa mở ở lần thử đầu và mở luôn sau 2 lần thử liên tiếp không có quảng cáo.
- [ ] Khi quảng cáo có thưởng của màn Xuất đang tắt từ xa thì ba tùy chọn chọn được ngay, không hỏi xem quảng cáo, không có nhãn "Ad".
- [ ] Khi tùy chọn đã mở thì phạm vi "lần xuất đó" giữ trong suốt một lần mở màn Xuất của dự án: thử lại sau lỗi, hủy rồi xuất lại, dừng vì rời app (KB-7) đều không cần xem lại; rời màn Xuất thì hết, lần mở sau cần xem lại (câu hỏi 2).

### KB-4. Xuất và theo dõi tiến độ

Tiêu chí chấp nhận:

- [ ] Khi bấm nút xuất thì màn chuyển sang trạng thái đang xuất: tiêu đề "Exporting", ảnh khung đang làm, dòng "Making your video…" (GIF: "Making your GIF…"; chuỗi PNG: "Making your images…"; câu hỏi 6), thanh tiến độ, "Frame <đã làm> of <tổng số>" bên trái, "<phần trăm>% · about <số> s left" bên phải, nút "Cancel".
- [ ] Khi xuất thì phần trăm bằng số khung đã làm chia tổng số khung, làm tròn xuống; ví dụ khung 29 của 48 hiện "60%". Thanh tiến độ và phần trăm không bao giờ lùi.
- [ ] Khi bấm "Cancel" thì việc xuất dừng, không file nào được lưu vào thư viện, không để lại file dở, và màn quay về trạng thái chọn tùy chọn với lựa chọn cũ.
- [ ] Khi bấm nút quay lại của hệ thống lúc đang xuất thì như bấm "Cancel": dừng ngay, không hỏi xác nhận (câu hỏi 4).
- [ ] Khi xuất MP4 thì video có âm thanh của dự án (nếu dự án có âm thanh), đúng tốc độ khung và tỷ lệ khung của dự án, ở độ phân giải đã chọn.
- [ ] Khi xuất GIF thì file lặp vô hạn, không âm thanh. Khi xuất chuỗi PNG thì có đúng một ảnh cho mỗi khung của dự án.
- [ ] Khi watermark bật thì mọi khung của file xuất ra có watermark Frameloop (chữ "Frameloop" kèm biểu tượng app, màu trắng trên viên thuốc nền đen mờ, ở góc dưới phải, cao khoảng 5% cạnh ngắn của khung, cách mép 3%, theo mẫu watermark ở design/README.md; câu hỏi 7); khi watermark tắt thì không khung nào có.
- [ ] Khi xuất, dù thành công, lỗi hay hủy, thì dự án không thay đổi.

### KB-5. Xuất xong: lưu, mở, chia sẻ

Tiêu chí chấp nhận:

- [ ] Khi xuất xong thì file tự lưu vào thư viện máy, không cần bấm thêm; màn chuyển sang trạng thái xong: ảnh xem trước là ảnh tĩnh khung đầu (câu hỏi 10), dấu đã xong, "Saved to Gallery", dòng "<đường dẫn> · <dung lượng>" (ví dụ "Movies/Frameloop/Bouncing ball.mp4 · 2.4 MB"), nút "Share", "Open in Gallery", "Back to editor".
- [ ] Khi xuất GIF thì file lưu ở "Pictures/Frameloop/"; khi xuất chuỗi PNG thì các ảnh "<tên dự án>_001.png"... lưu trong "Pictures/Frameloop/<tên dự án>/", dòng kết quả ghi thư mục và tổng dung lượng, "Share" chia sẻ mọi ảnh cùng lúc, "Open in Gallery" mở ảnh đầu tiên (câu hỏi 5).
- [ ] Khi mở ứng dụng thư viện ảnh của máy sau khi xuất xong thì thấy file vừa xuất.
- [ ] Khi bấm "Share" thì bảng chia sẻ của máy mở với file vừa xuất; đóng bảng chia sẻ thì vẫn ở màn xong, không có quảng cáo toàn màn hình.
- [ ] Khi bấm "Open in Gallery" thì file vừa xuất mở trong ứng dụng xem ảnh, video của máy.
- [ ] Khi bấm "Back to editor", nút đóng, hay nút quay lại của hệ thống ở màn xong thì app rời màn Xuất về màn đã mở nó; mở từ màn Hoàn thành bài học thì về màn đó và nút ghi "Back" (câu hỏi 9), có thể kèm một quảng cáo toàn màn hình theo mục 5.
- [ ] Khi trùng tên với file đã có trong thư viện thì file cũ không bị ghi đè; file mới thêm " (2)", " (3)"... vào tên (câu hỏi 5).

### KB-6. Xuất lỗi và thử lại

Tiêu chí chấp nhận:

- [ ] Khi xuất không xong thì màn chuyển sang trạng thái lỗi: biểu tượng báo lỗi, "Export didn't finish", lời giải thích có câu "Your project is safe.", nút "Try again" và "Back to editor".
- [ ] Khi lỗi vì máy thiếu bộ nhớ thì lời giải thích nói máy sắp hết bộ nhớ và nêu số MB ước tính cần giải phóng, như "Your project is safe. The phone is low on storage: free up about 50 MB and try again."
- [ ] Khi lỗi vì lý do khác thì lời giải thích dùng câu chung "Your project is safe. Something went wrong while making the file. Please try again." (câu hỏi 6).
- [ ] Khi bấm "Try again" thì app xuất lại với đúng lựa chọn cũ, quay về trạng thái đang xuất từ 0%.
- [ ] Khi bấm "Back to editor", nút đóng, hay nút quay lại của hệ thống ở trạng thái lỗi thì app về màn đã mở màn Xuất, không có quảng cáo toàn màn hình.
- [ ] Khi xuất lỗi thì không có file dở trong thư viện máy.

### KB-7. Rời app khi đang xuất

Bản đề xuất không làm xuất chạy nền. Tiêu chí dưới đây theo câu hỏi 3.

Tiêu chí chấp nhận:

- [ ] Khi người dùng về màn hình chính của máy, chuyển app khác, hay tắt màn hình lúc đang xuất thì việc xuất dừng, không file nào được lưu, không để lại file dở.
- [ ] Khi người dùng quay lại app sau đó thì màn Xuất ở trạng thái chọn tùy chọn, lựa chọn và tùy chọn đã mở giữ nguyên, có thông báo ngắn rằng việc xuất đã dừng vì rời app, "Export stopped because you left the app. Tap Export to start again." (câu hỏi 3, 6); bấm xuất là làm lại từ đầu.
- [ ] Khi máy dừng hẳn app lúc đang ở nền thì lần mở sau dự án vẫn nguyên vẹn, không có file dở trong thư viện.

### KB-8. Quyền lưu vào thư viện trên Android 9 trở xuống

Tiêu chí chấp nhận:

- [ ] Khi máy chạy Android 10 trở lên thì app không hỏi quyền nào để xuất và lưu.
- [ ] Khi máy chạy Android 8 hay 9 và chưa cho quyền lưu thì app hỏi quyền khi người dùng bấm nút xuất, trước khi bắt đầu xuất (câu hỏi 8).
- [ ] Khi người dùng cho quyền thì việc xuất bắt đầu ngay, không cần bấm lại.
- [ ] Khi người dùng từ chối thì việc xuất không bắt đầu, màn giữ trạng thái chọn tùy chọn và có thông báo "Allow storage access to save your animation to the Gallery." với nút "Allow"; từ chối hẳn thì nút đổi thành "Open settings", mở trang cài đặt của app (câu hỏi 6, 8).

### KB-9. Màn hình thấp, máy gập, chế độ tối

Tiêu chí chấp nhận:

- [ ] Khi màn hình 360 × 640 thì ở mọi trạng thái chữ không bị cắt hay chồng nhau, nội dung cuộn được, nút chính ("Export MP4", "Try again", "Share") không bị che hay cắt.
- [ ] Khi máy gập mở ra (673 × 841) thì mọi trạng thái vẫn dùng được: nội dung không bị cắt, nút chính thấy được, quảng cáo giữ đúng chỗ như mục 5 (câu hỏi 12).
- [ ] Khi máy để chế độ tối thì màn Xuất dùng màu tối như thiết kế; chế độ sáng dùng màu sáng.
- [ ] Khi đặt cỡ chữ lớn của hệ thống thì nút và nhãn không bị cắt chữ.
- [ ] Khi đo vùng chạm của mọi nút (thẻ định dạng, chip chất lượng, công tắc watermark, nút đóng) thì vùng chạm cao ít nhất 48 dp.

### KB-10. Tắt feature từ xa

Tiêu chí chấp nhận:

- [ ] Khi cờ của feature tắt thì nút "Export" trên màn vẽ và nút "Export my animation" trên màn Hoàn thành bài học không hiện, và không có cách nào mở màn Xuất.
- [ ] Khi cờ đổi trong lúc người dùng đang ở màn Xuất thì việc đang làm (chọn, xuất, xem kết quả) không bị ngắt; cờ áp từ lần mở sau.

## 4. Giao diện

- **Thiết kế:** [design/README.md](design/README.md), đã duyệt ngày 2026-11-20. Chữ theo bảng chữ của thiết kế (tiếng Anh, bản đề xuất Q9). Thiết kế không có frame cho máy gập mở ra (`_unfolded`); bản đề xuất mục 7 chỉ yêu cầu frame gập cho Màn vẽ và Danh sách dự án.
- **Các màn hình:**

| Màn hình | Mục đích | Kịch bản liên quan |
| --- | --- | --- |
| `ExportScreen`, trạng thái options (sáng, tối) | Tóm tắt dự án, chọn định dạng, chất lượng, watermark, bấm xuất | KB-1, KB-2, KB-3, KB-8, KB-9 |
| `ExportScreen`, trạng thái exporting (sáng, tối) | Màn chờ xuất có tiến độ và Hủy | KB-4, KB-7, KB-9 |
| `ExportScreen`, trạng thái done (sáng, tối) | Báo đã lưu vào thư viện, chia sẻ, mở, quay lại | KB-5, KB-9 |
| `ExportScreen`, trạng thái error (sáng, tối) | Báo lỗi, dự án an toàn, thử lại | KB-6, KB-9 |
| Hộp thoại hỏi xem quảng cáo có thưởng (thành phần chung của quảng cáo) | Hỏi trước khi mở tùy chọn thêm | KB-3 |
| Hộp thoại hỏi quyền của hệ thống (Android 8, 9) | Xin quyền lưu vào thư viện | KB-8 |

## 5. Quảng cáo

Chép từ bảng 9.1 của bản đề xuất. Mọi vị trí mặc định tắt, chỉ hiện khi khóa của nó bật trên Remote Config. Cả năm vị trí thuộc nhóm A (bật khi phát hành).

**Màn hình không có quảng cáo (nếu spec muốn giữ):** trạng thái lỗi của màn Xuất không có vị trí quảng cáo nào (bản đề xuất không đặt vị trí ở đây). Không có vị trí quảng cáo nằm giữa các thẻ định dạng, và không dùng thanh đáy ở màn chọn vì sẽ sát nút xuất ghim đáy (bản đề xuất 9.1).

| Vị trí | Định dạng | Màn hình | Khi nào hiển thị | Giới hạn tần suất | Khóa bật tắt |
| --- | --- | --- | --- | --- | --- |
| `export_options_mrec` | Banner MREC | Xuất, trạng thái options | Ngay dưới thẻ tóm tắt dự án, trên danh sách định dạng, cách thẻ định dạng ít nhất 24 dp (Q18). Khi vị trí bật thì giữ khung cố định cao 250 dp để danh sách không nhảy khi quảng cáo nạp lỗi; vị trí tắt, chưa có đồng ý quảng cáo, hay chỗ hẹp hơn 300 dp thì khung về 0. Nhóm A | Nạp lại theo luật chung của MREC | `is_show_export_options_mrec` |
| `export_wait_mrec` | Banner MREC | Xuất, trạng thái exporting | Chỗ trống cố định 300 × 250 dưới nút "Cancel", cách nút ít nhất 24 dp. Nhóm A | Nạp lại theo luật chung của MREC | `is_show_export_wait_mrec` |
| `export_done_native` | Native medium | Xuất, trạng thái done | Dưới ảnh xem trước và dòng kết quả; các nút ghim đáy, cách quảng cáo ít nhất 24 dp. Nhóm A | Không làm mới | `is_show_export_done_native` |
| `export_interstitial` | Interstitial | Rời màn Xuất ở trạng thái done ("Back to editor", nút đóng, nút quay lại) | Chỉ sau khi file đã lưu vào thư viện; ở hành động rời màn của người dùng. Không hiện khi rời trạng thái options hay error, hay khi bấm "Share", "Open in Gallery". Nhóm A | Theo `ads_interval` (30 giây, Q16) | `is_show_export_interstitial` |
| `export_rw_extra` | Rewarded | Xuất, trạng thái options | Khi người dùng chạm chuỗi PNG, GIF trong suốt hay gạt tắt watermark lúc chưa mở: một lượt xem mở bỏ watermark, chuỗi PNG, nền trong cho lần xuất đó của dự án (KB-3). Đóng trước khi được thưởng thì không mở; chưa có quảng cáo thì `allow_after_2` (câu hỏi 14). Khóa tắt thì tùy chọn mở thẳng. Nhóm A | Không qua `ads_interval` | `is_show_export_rw_extra` |

Ghi chú:

- **Không có luật trước nét vẽ đầu tiên** (bỏ ngày 2026-11-22, câu hỏi 15): mỗi vị trí chỉ theo khóa `is_show_<mã>` và các tham số Remote Config ở bảng, kể cả khi người dùng chưa từng vẽ nét nào trên máy.
- Sau `export_interstitial` có thể có native toàn màn hình `native_combo` theo vị trí toàn app (bản đề xuất 9.1); spec này không thêm vị trí đó.
- Quảng cáo khi quay lại app (`resume`, `resume_inter`) hiện ở mọi màn, kể cả màn chờ xuất (Q4); theo luật của vị trí toàn app, không thuộc spec này.
- Màn Xuất có quảng cáo trong nội dung (MREC, native medium), nên lớp nổi của quảng cáo không hiện cạnh chúng (luật chung của quảng cáo).

## 6. Dữ liệu và phân tích

**Dữ liệu lưu trên máy:**

| Dữ liệu | Mục đích | Giữ trong bao lâu |
| --- | --- | --- |
| File đã xuất (MP4, GIF, các ảnh PNG) trong thư viện ảnh của máy | Thành quả của người dùng | Đến khi người dùng tự xóa; gỡ app không xóa |
| File tạm trong lúc xuất | Dựng file trước khi lưu vào thư viện | Xóa ngay khi lưu xong, khi hủy, lỗi hay dừng |
| Trạng thái đã mở tùy chọn thêm của lần xuất | Không bắt xem lại quảng cáo trong cùng lần xuất | Chỉ trong lần xuất đó (câu hỏi 2), không ghi lâu dài |

Feature không đọc, không ghi mốc "nét vẽ đầu tiên" (câu hỏi 15). Không tài khoản, không gửi tranh hay file đi đâu ngoài khi người dùng tự chia sẻ (bản đề xuất 9.4).

**Sự kiện phân tích** (từ danh sách đề xuất ở bản đề xuất 9.2; danh sách và tham số đã được anh Nhật duyệt ở câu hỏi 11):

| Tên sự kiện | Khi nào ghi | Tham số | Dùng để trả lời câu hỏi gì |
| --- | --- | --- | --- |
| `export_option_unlock_rewarded` | Khi người dùng được thưởng sau khi xem `export_rw_extra` | `option` (tùy chọn đã chạm để mở: `no_watermark`, `png_sequence`, `transparent_gif`) | Tùy chọn nào đáng để người dùng xem quảng cáo |
| `export_start` | Khi việc xuất bắt đầu (cả lần thử lại) | `format`, `quality`, `watermark` | Định dạng, chất lượng nào được dùng nhiều |
| `export_success` | Khi file đã lưu vào thư viện | `format`, số khung (`frame_count`), thời lượng (`duration_ms`) (bản đề xuất 9.2) | Bao nhiêu lần xuất thành công, hoạt hình dài bao nhiêu |
| `export_fail` | Khi việc xuất chuyển sang trạng thái lỗi | `format`, `reason` (`low_storage`, `other`) | Xuất lỗi bao nhiêu, vì đâu |

Không ghi tên dự án, nội dung tranh, âm thanh hay đường dẫn file.

**Quyền của app và dịch vụ bên ngoài:**

- `WRITE_EXTERNAL_STORAGE` chỉ trên Android 9 trở xuống (khai báo `maxSdkVersion=28`, bản đề xuất Q5), để lưu file vào thư viện máy. Hỏi khi người dùng bấm nút xuất, trước khi bắt đầu; bị từ chối thì không xuất, báo vì sao cần quyền, từ chối hẳn thì có nút mở cài đặt của app (KB-8, câu hỏi 8).
- Android 10 trở lên: không cần quyền nào để lưu vào thư viện.
- Chia sẻ qua bảng chia sẻ của hệ thống, mở file bằng ứng dụng xem của máy: không cần quyền.
- Không thêm dịch vụ hay SDK bên ngoài ngoài quảng cáo và phân tích đã có.

## 7. File tĩnh trên R2

Không có.

## 8. Cờ tính năng

| Tên cờ | Mặc định | Ý nghĩa |
| --- | --- | --- |
| `feature_export_enabled` | tắt | Bật để hiện nút xuất trên màn vẽ và màn Hoàn thành bài học, mở màn Xuất cho người dùng. Bản staging bật sẵn |

## 9. Câu hỏi còn mở

1. **"Transparent GIF" có trên thiết kế, bản đề xuất chỉ nói nền trong suốt cho chuỗi PNG.** Bản đề xuất mục 5 ghi "Xuất chuỗi PNG, nền trong suốt bằng rewarded" và bảng 9.1 ghi "Bỏ watermark, chuỗi PNG, nền trong". Thiết kế có bốn định dạng: MP4, GIF, PNG sequence (Ad), Transparent GIF (Ad), không có công tắc nền trong cho chuỗi PNG. Vậy nền trong suốt áp cho định dạng nào: chỉ GIF trong suốt như thiết kế, chỉ chuỗi PNG như bản đề xuất, hay cả hai? Chuỗi PNG có nền giấy của dự án hay trong suốt?
   - Đề xuất của AI: giữ bốn định dạng như thiết kế; chuỗi PNG luôn trong suốt (bỏ nền giấy, dòng phụ "for editing apps" gợi dùng ở app khác), GIF trong suốt bỏ nền. Không thêm công tắc nền trong riêng.
   - Trả lời: Bốn định dạng như thiết kế, theo đề xuất của AI.

2. **Phạm vi "lần xuất đó" của tùy chọn đã mở bằng quảng cáo.** Bản đề xuất ghi "mở cho lần xuất đó của dự án" mà không nói rõ khi thử lại sau lỗi, hủy rồi xuất lại, xuất xong rồi muốn xuất thêm định dạng khác, hay rời màn Xuất rồi mở lại.
   - Đề xuất của AI: tùy chọn giữ mở trong suốt một lần mở màn Xuất của dự án đó: thử lại sau lỗi, hủy rồi xuất lại, quay về trạng thái chọn sau khi app dừng việc xuất (KB-7) đều không cần xem lại. Rời màn Xuất (về màn vẽ, màn Hoàn thành bài học) thì hết; lần mở sau cần xem lại. Một lần xem mở cả ba tùy chọn, và mọi định dạng xuất trong lần đó đều không có watermark nếu người dùng tắt nó.
   - Trả lời: Đồng ý đề xuất của AI.

3. **Người dùng rời app khi đang xuất thì sao?** Bản đề xuất mục 5 ghi "xuất chạy nền" là không làm, nhưng không nói điều gì xảy ra khi người dùng bấm về màn hình chính, chuyển app hay tắt màn hình giữa lúc xuất. Quảng cáo khi quay lại app cũng có thể hiện ở màn chờ (Q4).
   - Đề xuất của AI: dừng việc xuất ngay khi app ra nền, xóa file dở; quay lại thì màn ở trạng thái chọn với lựa chọn cũ và tùy chọn đã mở, có thông báo ngắn "Export stopped because you left the app. Tap Export to start again." (KB-7). Lý do: không chạy nền thì máy có thể dừng app bất cứ lúc nào, nên tiếp tục ngầm dễ cho kết quả dở dang mà người dùng không biết. Phương án khác: tiếp tục xuất khi app ở nền tới khi máy dừng app, không có thông báo; quay lại thấy kết quả hay thấy lỗi.
   - Trả lời: Đồng ý đề xuất của AI.

4. **Nút quay lại của hệ thống lúc đang xuất, và Hủy có cần hỏi lại không?** Thiết kế màn chờ chỉ có nút "Cancel", không có nút đóng, không có hộp thoại xác nhận.
   - Đề xuất của AI: nút quay lại của hệ thống làm như "Cancel"; cả hai dừng ngay, không hỏi xác nhận, về trạng thái chọn (KB-4). Hoạt hình ngắn nên mất ít công.
   - Trả lời: Đồng ý đề xuất của AI.

5. **Nơi lưu, tên file và cách lưu chuỗi PNG.** Thiết kế chỉ có ví dụ MP4 "Movies/Frameloop/Bouncing ball.mp4". Chưa rõ GIF lưu ở đâu, chuỗi PNG lưu thế nào (nhiều ảnh rời trong một thư mục, hay một file nén), trùng tên thì sao, và "Share", "Open in Gallery" làm gì với chuỗi PNG.
   - Đề xuất của AI: MP4 lưu vào "Movies/Frameloop/", GIF vào "Pictures/Frameloop/", chuỗi PNG thành các ảnh rời "<tên dự án>_001.png"... trong "Pictures/Frameloop/<tên dự án>/". Tên file theo tên dự án; trùng tên thì thêm " (2)", " (3)"... không ghi đè. Với chuỗi PNG: dòng kết quả ghi thư mục và tổng dung lượng, "Share" chia sẻ mọi ảnh cùng lúc, "Open in Gallery" mở ảnh đầu tiên.
   - Trả lời: Đồng ý đề xuất của AI.

6. **Chữ chưa có trên thiết kế.** Thiết kế chỉ có chữ cho MP4 và cho lỗi thiếu bộ nhớ. Cần chữ cho: nút xuất với GIF, chuỗi PNG, GIF trong suốt; dòng "Making your video…" khi không phải video; lỗi vì lý do khác; thông báo dừng vì rời app (câu hỏi 3); thông báo khi bị từ chối quyền (câu hỏi 8).
   - Đề xuất của AI: "Export GIF", "Export PNGs", "Export transparent GIF"; "Making your GIF…", "Making your images…"; lỗi khác: "Your project is safe. Something went wrong while making the file. Please try again."; quyền: "Allow storage access to save your animation to the Gallery." với nút "Allow" (và "Open settings" khi từ chối hẳn). Bản tiếng Việt theo cùng nghĩa.
   - Trả lời: Đồng ý đề xuất của AI.

7. **Watermark trông thế nào, đặt ở đâu?** Bản đề xuất và thiết kế chỉ ghi "Frameloop watermark", không có hình mẫu.
   - Đề xuất của AI: chữ "Frameloop" kèm biểu tượng app, trắng mờ có viền tối mảnh để thấy được trên mọi nền, ở góc dưới phải, cao khoảng 5% cạnh ngắn của khung, cách mép khoảng 3%. Cần một frame thiết kế cho watermark trước khi làm plan.
   - Trả lời: Đồng ý đề xuất của AI. Sau đó (2026-11-21) đổi sang chữ trắng trên viên thuốc nền đen 35%, theo design/README.md.

8. **Quyền lưu trên Android 8, 9: hỏi lúc nào, từ chối thì sao?** Q5 chốt có quyền này (`maxSdkVersion=28`) nhưng không nói lúc hỏi và cách xử lý khi bị từ chối.
   - Đề xuất của AI: hỏi khi bấm nút xuất, trước khi bắt đầu (không hỏi lúc mở màn); cho quyền thì xuất ngay; từ chối thì không xuất, hiện thông báo ở câu hỏi 6 với nút hỏi lại; từ chối hẳn thì nút đổi thành mở trang cài đặt của app (KB-8). Không có phương án "xuất rồi chỉ chia sẻ", vì thiết kế màn xong luôn ghi "Saved to Gallery".
   - Trả lời: Đồng ý đề xuất của AI.

9. **Mở từ màn Hoàn thành bài học thì "Back to editor" và nút đóng về đâu?** Thiết kế lessons có nút "Export my animation" mở màn Xuất, nhưng nút trên màn Xuất ghi "Back to editor".
   - Đề xuất của AI: mọi lối rời màn Xuất về đúng màn đã mở nó; khi mở từ màn Hoàn thành bài học thì về màn đó, và nút ghi "Back" thay cho "Back to editor" (cần thêm chữ "Back").
   - Trả lời: Đồng ý đề xuất của AI.

10. **Ảnh xem trước ở màn xong là ảnh tĩnh hay phát lại hoạt hình?** Phạm vi yêu cầu có "xem trước"; thiết kế ghi ảnh xem trước là khung đầu của dự án.
    - Đề xuất của AI: ảnh tĩnh khung đầu như thiết kế; người dùng xem đầy đủ bằng "Open in Gallery".
    - Trả lời: Đồng ý đề xuất của AI.

11. **Xác nhận danh sách sự kiện phân tích và tham số.** Bản đề xuất 9.2 là danh sách đề xuất, chưa có câu trả lời riêng; chỉ `export_success` có tham số (định dạng, số khung, thời lượng). Không có sự kiện cho hủy, chia sẻ hay mở trong thư viện.
    - Đề xuất của AI: dùng bốn sự kiện ở mục 6 với tham số đề xuất ở đó. Không thêm sự kiện hủy, chia sẻ; nếu muốn biết người dùng bỏ giữa chừng thì so `export_start` với `export_success` cộng `export_fail`.
    - Trả lời: Duyệt như đề xuất.

12. **Máy gập mở ra: có cần frame thiết kế riêng không?** Thiết kế màn Xuất không có frame `_unfolded`; bản đề xuất mục 7 chỉ yêu cầu cho Màn vẽ và Danh sách dự án.
    - Đề xuất của AI: không cần frame riêng; giữ bố cục một cột như điện thoại, nội dung và nút căn giữa với bề rộng như ở khung 411 dp, MREC 300 × 250 căn giữa; có screenshot test ở khung 673 × 841 cho trạng thái options và done.
    - Trả lời: Đồng ý đề xuất của AI.

13. **Chất lượng khi dự án nhỏ hơn 1080p.** Bản đề xuất cho dự án tối đa 1080p; thiết kế luôn có hai chip 720p và 1080p, chọn sẵn 1080p.
    - Đề xuất của AI: mức chọn sẵn là độ phân giải của dự án; dự án 720p thì chip 1080p không có (không phóng to). "720p", "1080p" tính theo cạnh ngắn của khung với mọi tỷ lệ.
    - Trả lời: Đồng ý đề xuất của AI.

14. **Chưa có quảng cáo có thưởng để hiện thì `export_rw_extra` làm gì?** Mặc định của phần quảng cáo dùng chung là chặn, người dùng thử lại được (MC-05). Spec editor câu 4 hỏi cùng điều này cho ba vị trí rewarded của màn vẽ và đề xuất `allow_after_2`. Câu trả lời ở spec editor câu 4, áp cho cả bốn vị trí rewarded; câu này chỉ trỏ về đó.
    - Đề xuất của AI: trả lời chung với câu 4 của spec editor. AI đề xuất `allow_after_2`: sau 2 lần thử liên tiếp không có quảng cáo thì mở luôn tùy chọn cho lần xuất đó. Phương án khác: `block`, tùy chọn không mở tới khi có quảng cáo.
    - Trả lời: Theo spec editor câu 4: `allow_after_2`.

15. **Bỏ luật "trước nét vẽ đầu tiên"** (yêu cầu thay đổi của anh Nhật ngày 2026-11-22: "Việc thiết lập quảng cáo sẽ cần linh hoạt tuỳ theo kịch bản kinh doanh"). Luật này giữ `export_options_mrec`, `export_wait_mrec`, `export_done_native` và `export_interstitial` tới khi người dùng vẽ nét đầu tiên. Bỏ luật thì mỗi vị trí chỉ theo khóa `is_show_<mã>` và các tham số Remote Config, luật kinh doanh sẵn có.
    - Trả lời: Bỏ hẳn luật, giữ mốc nét vẽ đầu tiên làm dữ liệu dự phòng (anh Nhật, 2026-11-22, trong phiên).

## 10. Lịch sử

| Ngày | Người | Nội dung |
| --- | --- | --- |
| 2026-11-21 | AI | Tạo bản đầu từ bản đề xuất đã duyệt (docs/research/proposal.md) và thiết kế đã duyệt |
| 2026-11-21 | AI | Ghi câu trả lời của anh Nhật cho mọi câu hỏi ở mục 9 (anh chọn trong phiên ngày 2026-11-21) và sửa phần thân spec theo các câu trả lời; sửa các điểm nên sửa còn lại của lần rà soát cuối. Spec vẫn `draft` |
| 2026-11-21 | AI | Ghi các frame thiết kế bổ sung, sửa theo câu trả lời (chờ anh Nhật duyệt) ở mục 4 và các tiêu chí liên quan |
| 2026-11-21 | AI | Watermark đổi sang chữ trắng trên viên thuốc nền đen mờ, theo lời anh Nhật trong phiên (dễ thấy trên nền trắng) |
| 2026-11-21 | AI | Thiết kế bổ sung đã được anh Nhật duyệt trong phiên (chọn "Duyệt") |
| 2026-11-21 | AI | Chuyển `confirmed` theo lời anh Nhật (người phụ trách sản phẩm) trong phiên: "xác nhận 6 spec" |
| 2026-11-22 | AI | Bỏ luật quảng cáo trước nét vẽ đầu tiên: sửa ghi chú ở mục 5, bỏ việc đọc mốc ở mục 6, thêm câu hỏi 15; chuyển `changed`. Theo lời anh Nhật trong phiên: bỏ luật trước nét vẽ đầu tiên, giữ mốc làm dữ liệu dự phòng; chờ xác nhận lại |
