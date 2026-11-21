---
feature: settings
status: draft
owner: Nhật
product_owner: Nhật
requests:
  - requests/2026-11-21-ban-dau.md
design: design/README.md
created: 2026-11-21
confirmed:
---

# SPEC: CÀI ĐẶT

## 1. Mục tiêu

- **Người dùng được gì:** một chỗ để chọn giá trị mặc định cho dự án mới, sao lưu cả kho dự án ra một file và khôi phục lại, xem lại lựa chọn quyền riêng tư quảng cáo, đọc chính sách quyền riêng tư và biết phiên bản app.
- **Vì sao làm bây giờ:** mất bài là nỗi sợ lớn nhất của người dùng app vẽ hoạt hình (bản đề xuất mục 4). Sao lưu tất cả dự án ra một file giúp người dùng chuyển máy hay cài lại app mà không mất gì, khi app không có tài khoản hay đám mây (mục 9.4). Màn này cũng là lối vào lựa chọn quyền riêng tư quảng cáo mà người dùng cần để rút hay cho lại đồng ý.
- **Đo kết quả bằng gì:** số lần sao lưu tất cả và số lần khôi phục thành công mỗi tuần, so với số người dùng hoạt động (mục 6); không đo tỷ lệ khôi phục thành công trên số lần thử, vì sự kiện chỉ ghi lần thành công (câu hỏi 1).

## 2. Phạm vi

**Trong phạm vi:**

- Màn Cài đặt mở từ nút cài đặt trên thanh trên của danh sách dự án; nút quay lại về danh sách dự án.
- Nhóm "Drawing": tốc độ khung mặc định ("Default frame rate", thiết kế ghi 12 fps), khung hình mặc định ("Default canvas", thiết kế ghi 9:16), lực bút cảm ứng ("Stylus pressure", thiết kế ghi On). Giá trị mặc định được chọn sẵn ở màn Tạo dự án; người dùng vẫn đổi được ở đó.
- Nhóm "Your projects": sao lưu tất cả dự án ra một file ("Back up all projects", "To a file"), khôi phục từ một file ("Restore from a file"), cả hai qua bộ chọn file của hệ thống (chọn được Google Drive); dòng tự lưu ("Autosave", "Every 30 s").
- Nhóm "About": lựa chọn quyền riêng tư quảng cáo ("Ad privacy choices", mở lại hộp thoại đồng ý quảng cáo dùng chung của app), chính sách quyền riêng tư ("Privacy policy"), đánh giá app ("Rate Frameloop"), phiên bản ("Version").
- Thanh quảng cáo native nhỏ ở đáy màn (mục 5).
- Màn hiện theo chế độ sáng hay tối của máy, cả hai có trên thiết kế.

**Ngoài phạm vi lần này:**

- Sao lưu hay khôi phục một dự án lẻ từ menu dự án và nút "Nhập từ file" trên danh sách dự án: thuộc feature projects.
- Hướng dẫn trên màn vẽ: thuộc feature editor. Lối vào "xem lại hướng dẫn" nằm trong menu "thêm" của màn vẽ, không ở Cài đặt (câu hỏi 14).
- Tài khoản, đồng bộ đám mây, sao lưu tự động theo lịch, gói gỡ quảng cáo (bản đề xuất mục 5, Q10).
- Chọn ngôn ngữ trong app: app chỉ có tiếng Anh (Q9).
- Điều khoản sử dụng, liên hệ hỗ trợ: không có trên thiết kế và bản đề xuất, không làm ở bản đầu (câu hỏi 11, 13).

## 3. Kịch bản sử dụng

App luôn cần mạng: khi mất mạng, app chặn bằng popup mất mạng chung; màn Cài đặt không làm chế độ ngoại tuyến riêng.

### KB-1. Mở màn Cài đặt và quay lại

Người dùng đang ở danh sách dự án, muốn xem cài đặt.

Tiêu chí chấp nhận:

- [ ] Khi bấm nút cài đặt trên thanh trên của danh sách dự án thì màn Cài đặt mở, tiêu đề "Settings", có ba nhóm "Drawing", "Your projects", "About" với đủ các dòng và thứ tự như thiết kế.
- [ ] Khi bấm nút quay lại trên thanh trên hay nút quay lại của máy thì app về danh sách dự án.
- [ ] Khi mỗi dòng bấm được thì vùng chạm của dòng cao ít nhất 48 dp.
- [ ] Khi màn hiện ở khung thấp 360 × 640 dp thì nội dung cuộn được tới dòng "Version", không dòng nào bị thanh quảng cáo đáy che, chữ không chồng nhau hay bị cắt.

Trường hợp đặc biệt:

- [ ] Khi người dùng xoay máy hay app bị hệ thống dừng rồi mở lại ở màn Cài đặt thì các giá trị hiển thị giữ nguyên.
- [ ] Khi người dùng mở app lần đầu, chưa có dự án nào, thì màn Cài đặt vẫn mở được và hiện giá trị mặc định 12 fps, 9:16, lực bút bật.

### KB-2. Đổi tốc độ khung mặc định

Người dùng hay làm hoạt hình 24 khung/giây, không muốn chọn lại mỗi lần tạo dự án.

Tiêu chí chấp nhận:

- [ ] Khi bấm dòng "Default frame rate" thì app hiện danh sách tốc độ khung giống các lựa chọn ở màn Tạo dự án (6, 8, 12, 15, 24, 30 khung/giây), lựa chọn hiện tại được đánh dấu.
- [ ] Khi chọn 24 thì dòng hiện "24 fps", và lần tạo dự án sau màn Tạo dự án chọn sẵn 24.
- [ ] Khi đóng danh sách mà không chọn thì giá trị không đổi.
- [ ] Khi tắt app rồi mở lại thì giá trị đã chọn vẫn còn.

Trường hợp đặc biệt:

- [ ] Khi đổi tốc độ khung mặc định thì các dự án đã có và các bài học không đổi tốc độ khung.

### KB-3. Đổi khung hình mặc định

Người dùng hay làm video ngang, muốn mặc định 16:9.

Tiêu chí chấp nhận:

- [ ] Khi bấm dòng "Default canvas" thì app hiện danh sách tỷ lệ khung giống các lựa chọn ở màn Tạo dự án, lựa chọn hiện tại được đánh dấu: 16:9, 9:16, 1:1 (câu hỏi 9).
- [ ] Khi chọn 16:9 thì dòng hiện "16:9", và lần tạo dự án sau màn Tạo dự án chọn sẵn 16:9.
- [ ] Khi tắt app rồi mở lại thì giá trị đã chọn vẫn còn.

Trường hợp đặc biệt:

- [ ] Khi đổi khung hình mặc định thì các dự án đã có và các bài học không đổi khung hình.

### KB-4. Bật tắt lực bút cảm ứng

Người dùng vẽ bằng bút cảm ứng, muốn nét đậm nhạt theo lực nhấn, hoặc muốn nét đều.

Tiêu chí chấp nhận:

- [ ] Khi bấm dòng "Stylus pressure" lúc đang "On" thì dòng chuyển sang "Off", và trên màn vẽ, với cọ chưa chỉnh riêng lực bút trong bảng "Brush", nét vẽ bằng bút cảm ứng có độ dày đều, không theo lực nhấn (công tắc "Pressure from stylus" trong bảng "Brush" chỉnh riêng cọ đang chọn và thắng giá trị này: câu hỏi 7, spec editor câu 22).
- [ ] Khi bấm lại thì dòng về "On" và nét vẽ bằng bút cảm ứng lại theo lực nhấn ở các cọ có lực bút mà người dùng chưa chỉnh riêng lực bút.
- [ ] Khi tắt app rồi mở lại thì lựa chọn vẫn còn.

Trường hợp đặc biệt:

- [ ] Khi vẽ bằng ngón tay thì lựa chọn này không đổi gì.

Chạm dòng để bật tắt, mặc định "On"; chỉ áp khi vẽ bằng bút cảm ứng (câu hỏi 7).

### KB-5. Sao lưu tất cả dự án ra một file

Người dùng sắp đổi máy, muốn giữ toàn bộ dự án.

Tiêu chí chấp nhận:

- [ ] Khi bấm "Back up all projects" thì bộ chọn file của hệ thống mở để chọn chỗ lưu, tên file gợi ý có tên app và ngày (ví dụ "Frameloop-backup-2026-11-21"), chọn được Google Drive.
- [ ] Khi chọn chỗ lưu thì app ghi mọi dự án của người dùng (khung, layer, âm thanh, tên, tốc độ khung, khung hình) vào đúng một file, và báo sao lưu xong kèm số dự án.
- [ ] Khi file sao lưu được khôi phục trên một máy khác (KB-6) thì mọi dự án hiện lại đủ khung, layer, âm thanh như lúc sao lưu.
- [ ] Khi đang sao lưu thì người dùng thấy app đang làm việc và không bấm sao lưu lần nữa được: dòng "Back up all projects" hiện vòng chờ thay cho "To a file" và không bấm được (câu hỏi 5).
- [ ] Khi sao lưu xong thì thông báo ngắn ở đáy ghi số dự án (ví dụ "Backed up 4 projects"); lỗi thì thông báo "Couldn't back up. Try again." có nút "Retry" (câu hỏi 5).

Trường hợp đặc biệt:

- [ ] Khi người dùng đóng bộ chọn file mà không chọn chỗ lưu thì không có file nào được ghi, không báo lỗi.
- [ ] Khi chưa có dự án nào thì dòng "Back up all projects" mờ đi, chữ phụ "No projects yet", bấm không mở bộ chọn file (câu hỏi 5).
- [ ] Khi ghi file thất bại (hết chỗ, mất quyền ghi, Google Drive lỗi) thì app báo sao lưu không thành công, có thể thử lại, các dự án trên máy không bị đổi, và file dở dang không được để lại như một bản sao lưu dùng được.
- [ ] Khi sao lưu thì app đọc các dự án từ bản đã lưu trên máy; bản sao lưu chứa đúng nội dung mà mở dự án trên màn vẽ sẽ thấy (màn vẽ tự lưu xong trước khi người dùng rời màn).

### KB-6. Khôi phục dự án từ một file

Người dùng vừa cài lại app, có file sao lưu.

Tiêu chí chấp nhận:

- [ ] Khi bấm "Restore from a file" thì bộ chọn file của hệ thống mở, chọn được file trên máy và trên Google Drive.
- [ ] Khi chọn một file sao lưu hợp lệ thì app về danh sách dự án, khôi phục các dự án trong file với hộp thoại khôi phục của danh sách dự án (có tiến độ), xong thì các dự án có trong danh sách (câu hỏi 3).
- [ ] Khi khôi phục xong thì không dự án nào đang có trên máy bị xóa hay ghi đè; dự án khôi phục luôn là dự án mới, giữ tên trong file, cho phép trùng tên (câu hỏi 4).

Trường hợp đặc biệt:

- [ ] Khi người dùng đóng bộ chọn file mà không chọn thì không có gì thay đổi.
- [ ] Khi file không phải file sao lưu của Frameloop, bị hỏng hay đọc dở chừng thì app báo lỗi khôi phục như hộp thoại lỗi của danh sách dự án, không thêm dự án dở dang nào.
- [ ] Khi máy không đủ chỗ trống để khôi phục thì app báo lỗi, không thêm dự án dở dang nào.
- [ ] Khi app bị tắt giữa lúc khôi phục thì lần mở sau không có dự án hỏng hay dở dang trong danh sách.

### KB-7. Lựa chọn quyền riêng tư quảng cáo

Người dùng muốn rút hay cho lại đồng ý quảng cáo cá nhân hóa.

Tiêu chí chấp nhận:

- [ ] Khi bấm "Ad privacy choices" thì hộp thoại lựa chọn quyền riêng tư quảng cáo dùng chung của app mở lại.
- [ ] Khi người dùng rút đồng ý trong hộp thoại thì không còn quảng cáo mới nào được tải và quảng cáo đã tải không hiện nữa, kể cả thanh quảng cáo đáy của màn Cài đặt (theo spec ads, KB-12).
- [ ] Khi người dùng đồng ý lại thì quảng cáo tải lại mà không cần mở lại app (spec ads, KB-12).
- [ ] Khi hộp thoại đang mở thì không quảng cáo nào tự hiện lên khi người dùng rời app rồi quay lại.

Trường hợp đặc biệt:

- [ ] Khi người dùng ở vùng không bắt buộc hộp thoại này thì dòng "Ad privacy choices" không hiện (câu hỏi 10).

### KB-8. Thông tin app

Người dùng muốn đọc chính sách quyền riêng tư, đánh giá app hay biết phiên bản.

Tiêu chí chấp nhận:

- [ ] Khi bấm "Privacy policy" thì trang chính sách quyền riêng tư của Frameloop mở trong trình duyệt của máy (địa chỉ https://studio.vgl.io.vn/privacy-policy, câu hỏi 11).
- [ ] Khi bấm "Rate Frameloop" thì trang của Frameloop trên Google Play mở (câu hỏi 12).
- [ ] Khi xem dòng "Version" thì dòng hiện đúng số phiên bản của bản app đang chạy (thiết kế ghi "1.0.0" là ví dụ); dòng không bấm được.
- [ ] Khi xem dòng "Autosave" thì dòng hiện "Every 30 s", đúng với mức mất tối đa 30 giây công của tự lưu; dòng chỉ để xem, không bấm được (câu hỏi 8).

Trường hợp đặc biệt:

- [ ] Khi máy không có trình duyệt hay ứng dụng Google Play để mở trang thì app báo không mở được, không dừng đột ngột.

### KB-9. Quảng cáo ở đáy màn Cài đặt

Người vận hành bật quảng cáo ở màn Cài đặt (nhóm B, bật dần).

Tiêu chí chấp nhận:

- [ ] Khi vị trí quảng cáo của màn Cài đặt được bật, người dùng đã đồng ý quảng cáo và đã vẽ nét vẽ đầu tiên thì thanh quảng cáo native nhỏ hiện ở đáy màn, nội dung cuộn phía trên, không dòng nào bị che.
- [ ] Khi vị trí bị tắt thì không có thanh quảng cáo và nội dung dùng cả chiều cao màn.
- [ ] Khi người dùng chưa vẽ nét vẽ đầu tiên ở bất kỳ dự án hay bài học nào thì màn Cài đặt không có quảng cáo.

Trường hợp đặc biệt:

- [ ] Khi quảng cáo chưa tải xong hay tải lỗi thì các dòng của màn Cài đặt không dịch chỗ khiến người dùng bấm nhầm.

### KB-10. Tắt màn Cài đặt từ xa

Người vận hành tắt màn Cài đặt khi có sự cố.

Tiêu chí chấp nhận:

- [ ] Khi feature Cài đặt bị tắt thì danh sách dự án không có nút cài đặt, và màn Tạo dự án dùng giá trị mặc định 12 fps, 9:16; giá trị đã chọn trước đó vẫn giữ trên máy để dùng lại khi bật cờ (câu hỏi 15).

## 4. Giao diện

- **Thiết kế:** [design/README.md](design/README.md), đã duyệt ngày 2026-11-20, chữ tiếng Anh theo bảng chữ ở mục 2 của README.
- **Các màn hình:**

| Màn hình | Mục đích | Kịch bản liên quan |
| --- | --- | --- |
| `SettingsScreen`, default (sáng, tối) | Cài đặt mặc định khi tạo dự án, sao lưu và khôi phục, quyền riêng tư quảng cáo, thông tin app | KB-1, KB-2, KB-3, KB-4, KB-5, KB-6, KB-7, KB-8, KB-9 |
| `ProjectsScreen` (feature projects) | Nút cài đặt trên thanh trên mở màn Cài đặt | KB-1, KB-10 |
| `NewProjectScreen` (feature projects) | Chọn sẵn tốc độ khung và khung hình mặc định | KB-2, KB-3, KB-10 |

Thiết kế chưa có: danh sách chọn tốc độ khung và khung hình, trạng thái đang sao lưu, thông báo sao lưu xong hay lỗi, thông báo chưa có dự án (câu hỏi 5, 9). Screenshot test ở khung thấp 360 × 640 dp cho trạng thái default.

## 5. Quảng cáo

**Màn hình không có quảng cáo (nếu spec muốn giữ):** không có vị trí quảng cáo trong danh sách các dòng, trong danh sách chọn giá trị và trong thông báo sao lưu, khôi phục. Bộ chọn file và hộp thoại quyền riêng tư là của hệ thống và của phần quảng cáo dùng chung, không đặt quảng cáo.

| Vị trí | Định dạng | Màn hình | Khi nào hiển thị | Giới hạn tần suất | Khóa bật tắt |
| --- | --- | --- | --- | --- | --- |
| `settings_bottom` | Thanh đáy native nhỏ | Cài đặt | Mọi lúc ở màn Cài đặt, sau nét vẽ đầu tiên; nhóm B (dựng sẵn, bật dần) | Thay nội dung theo `time_reload_native_small` của thanh đáy | `is_show_settings_bottom` |

**Trước nét vẽ đầu tiên** (bản đề xuất, đoạn dưới bảng 9.1): tới khi người dùng vẽ nét đầu tiên ở bất kỳ dự án hay bài học nào, app chỉ hiện `native_onboarding`, `projects_empty_native` và `editor_top_native`; `settings_bottom` không hiện. Quảng cáo `resume`, `resume_inter` khi quay lại app vẫn theo spec ads (Q4).

## 6. Dữ liệu và phân tích

**Dữ liệu lưu trên máy:**

Chỉ lưu trên máy, không tài khoản (bản đề xuất mục 9.4).

| Dữ liệu | Mục đích | Giữ trong bao lâu |
| --- | --- | --- |
| Tốc độ khung mặc định | Chọn sẵn ở màn Tạo dự án | Đến khi người dùng đổi hay xóa dữ liệu app |
| Khung hình mặc định | Chọn sẵn ở màn Tạo dự án | Đến khi người dùng đổi hay xóa dữ liệu app |
| Bật tắt lực bút cảm ứng | Màn vẽ đọc khi vẽ bằng bút | Đến khi người dùng đổi hay xóa dữ liệu app |
| File sao lưu | Người dùng tự chọn chỗ lưu (máy hay Google Drive) | Do người dùng giữ, app không tự xóa |

**Sự kiện phân tích:**

Theo bản đề xuất mục 9.2; danh sách và tham số đã được anh Nhật duyệt (câu hỏi 1, spec projects câu 17).

| Tên sự kiện | Khi nào ghi | Tham số | Dùng để trả lời câu hỏi gì |
| --- | --- | --- | --- |
| `backup_export` | Khi sao lưu ghi file xong (chỉ khi thành công) | Đề xuất: `scope` (`all` từ Cài đặt, `project` từ menu dự án), `project_count` | Bao nhiêu người sao lưu, sao lưu tất cả hay một dự án |
| `backup_restore` | Khi khôi phục xong (chỉ khi thành công) | Đề xuất: `scope` (`all` khi bấm "Restore from a file" ở Cài đặt, `project` khi bấm nút nhập file ở danh sách dự án), `project_count` (số dự án đã khôi phục) | Bao nhiêu người khôi phục, từ đâu, bao nhiêu dự án |

Hai sự kiện này dùng chung với spec projects, cùng tên và tham số (spec projects, câu 17).

Không ghi tên dự án, tên file hay nội dung tranh.

**Quyền của app và dịch vụ bên ngoài:** không có quyền mới. Sao lưu và khôi phục qua bộ chọn file của hệ thống, không cần quyền bộ nhớ trên mọi phiên bản Android từ 8.0; quyền `WRITE_EXTERNAL_STORAGE` (`maxSdkVersion=28`) của bản đề xuất Q5 chỉ cho việc lưu file vào thư viện máy của feature export, màn Cài đặt không dùng. Chính sách quyền riêng tư mở bằng trình duyệt; "Rate Frameloop" mở trang Google Play, không thêm thư viện đánh giá trong app (câu hỏi 12).

## 7. File tĩnh trên R2

Không có.

## 8. Cờ tính năng

| Tên cờ | Mặc định | Ý nghĩa |
| --- | --- | --- |
| `feature_settings_enabled` | tắt | Bật để hiện nút cài đặt trên danh sách dự án và mở màn Cài đặt. Bản staging bật sẵn |

## 9. Câu hỏi còn mở

1. **Danh sách sự kiện và tham số:** bản đề xuất 9.2 là đề xuất, chưa có câu trả lời riêng. `backup_export`, `backup_restore` có ghi cho cả sao lưu tất cả (Cài đặt) và sao lưu một dự án (menu dự án) không? Có ghi lần thất bại không?
   - Đề xuất của AI: trả lời chung với câu 17 của spec projects, nơi ghi đề xuất duy nhất cho cả hai spec: dùng chung hai sự kiện cho cả hai nơi, cả hai có `scope` (`all`, `project`) và `project_count`, chỉ ghi khi thành công (9.2 không có sự kiện lỗi); phương án ghi cả lần lỗi, hủy cũng nêu ở câu đó.
   - Trả lời: Theo spec projects câu 17: chỉ ghi khi thành công.
2. **File sao lưu tất cả và file sao lưu một dự án có cùng loại không? "Restore from a file" ở Cài đặt nhận loại nào?**
   - Đề xuất của AI: một loại file chung, chứa một hay nhiều dự án; cả "Restore from a file" ở Cài đặt và "Nhập từ file" ở danh sách dự án nhận cả hai, đuôi ".frameloop" như spec projects. Trả lời chung với câu 19 của spec projects.
   - Trả lời: Đồng ý đề xuất của AI.
3. **Khôi phục từ Cài đặt hiện tiến độ ở đâu?** Thiết kế chỉ có hộp thoại đang khôi phục và hộp thoại lỗi trên danh sách dự án (có MREC `restore_mrec`); màn Cài đặt không có trạng thái này.
   - Đề xuất của AI: chọn file xong thì app về danh sách dự án và chạy khôi phục ở đó, dùng chung hộp thoại đang khôi phục và hộp thoại lỗi của projects (kèm `restore_mrec` như 9.1). Không thêm quảng cáo vào màn Cài đặt. Trả lời chung với câu 19 của spec projects.
   - Trả lời: Đồng ý đề xuất của AI.
4. **Khôi phục khi trên máy đã có dự án trùng (cùng dự án hay cùng tên)?**
   - Đề xuất của AI: luôn thêm thành dự án mới, giữ tên trong file (cho phép trùng tên), không ghi đè dự án đang có. Trả lời chung với câu 10 của spec projects.
   - Trả lời: Đồng ý đề xuất của AI.
5. **Trạng thái sao lưu không có trên thiết kế:** đang sao lưu, sao lưu xong, sao lưu lỗi, chưa có dự án để sao lưu.
   - Đề xuất của AI: đang sao lưu thì dòng "Back up all projects" hiện vòng chờ thay cho "To a file" và không bấm được; xong hay lỗi thì hiện thông báo ngắn ở đáy màn ("Backed up 4 projects", "Couldn't back up. Try again." có nút "Retry"); chưa có dự án thì dòng mờ đi, chữ phụ "No projects yet". Chữ tiếng Anh này cần anh duyệt, có thể bổ sung thiết kế.
   - Trả lời: Đồng ý đề xuất của AI.
6. **Bài học đang làm dở có nằm trong bản sao lưu không?** Bản đề xuất ghi bài học không tính vào số dự án.
   - Đề xuất của AI: không; bản sao lưu chỉ gồm dự án của người dùng.
   - Trả lời: Đồng ý đề xuất của AI.
7. **"Stylus pressure" có trên thiết kế, bản đề xuất không có mục cài đặt này** (chỉ có "lực bút" trong cọ của màn vẽ). Có giữ không, chọn thế nào, áp cho đâu?
   - Đề xuất của AI: giữ; chạm dòng để bật tắt (có thể thay chữ "On" bằng công tắc nếu anh muốn sửa thiết kế); mặc định bật; là giá trị mặc định cho mọi cọ có lực bút mà người dùng chưa chỉnh riêng trong bảng "Brush", chỉ khi vẽ bằng bút cảm ứng (khớp đề xuất của spec editor câu 22). Quan hệ với công tắc "Pressure from stylus" trong bảng "Brush" của màn vẽ (bên nào thắng, có bỏ một bên không): trả lời chung với câu 22 của spec editor.
   - Trả lời: Giữ cả hai công tắc, theo đề xuất của AI (trả lời chung với spec editor câu 22).
8. **"Autosave, Every 30 s" chỉ để xem hay người dùng chọn được chu kỳ?** Bản đề xuất chỉ cam kết mất tối đa 30 giây công.
   - Đề xuất của AI: chỉ để xem, không bấm được, để giữ cam kết 30 giây.
   - Trả lời: Đồng ý đề xuất của AI.
9. **Danh sách giá trị và cách chọn mặc định:** thiết kế không có danh sách chọn. Tỷ lệ khung: bản đề xuất có 16:9, 9:16, 1:1; thiết kế màn Tạo dự án (projects) có thêm 4:5. Mặc định có gồm kích thước (720p, 1080p) và nền không?
   - Đề xuất của AI: chạm dòng mở hộp thoại chọn một giá trị, chọn là lưu ngay. Tốc độ khung: 6, 8, 12, 15, 24, 30 như chip của màn Tạo dự án. Tỷ lệ khung: theo câu trả lời của spec projects về 4:5, hai màn dùng cùng danh sách. Không thêm mặc định cho kích thước và nền (thiết kế không có).
   - Trả lời: Đồng ý đề xuất của AI. Tỷ lệ khung là 16:9, 9:16, 1:1 (spec projects câu 1 bỏ 4:5).
10. **"Ad privacy choices" luôn hiện trên thiết kế, nhưng phần quảng cáo dùng chung chỉ mở được hộp thoại này khi hộp thoại là bắt buộc với người dùng** (ví dụ ở châu Âu; gọi lúc không bắt buộc thì không mở được).
    - Đề xuất của AI: chỉ hiện dòng khi hộp thoại là bắt buộc; người dùng ở vùng khác không thấy dòng này. Screenshot test cùng tên ảnh thiết kế vẽ trạng thái có dòng.
    - Trả lời: Chỉ hiện dòng khi hộp thoại là bắt buộc, theo đề xuất của AI.
11. **"Privacy policy" cần địa chỉ thật; có thêm điều khoản sử dụng không?** Thiết kế không có điều khoản.
    - Đề xuất của AI: anh cho địa chỉ trang chính sách quyền riêng tư của Frameloop (cùng địa chỉ khai trên Google Play), cố định trong app. Không thêm điều khoản sử dụng ở bản đầu.
    - Trả lời: Địa chỉ: https://studio.vgl.io.vn/privacy-policy. Không thêm điều khoản sử dụng.
12. **"Rate Frameloop" có trên thiết kế, bản đề xuất không có.** Có giữ không, mở thế nào?
    - Đề xuất của AI: giữ; mở trang Frameloop trên Google Play. Không dùng hộp thoại đánh giá trong app, vì cần thêm thư viện mới.
    - Trả lời: Giữ, mở trang Frameloop trên Google Play, theo đề xuất của AI.
13. **Liên hệ hỗ trợ (gửi thư cho nhóm) không có trên thiết kế và bản đề xuất.** Có cần không?
    - Đề xuất của AI: không làm ở bản đầu.
    - Trả lời: Đồng ý đề xuất của AI.
14. **Xem lại hướng dẫn trên màn vẽ:** bản đề xuất mục 5 ghi hướng dẫn "xem lại được", nhưng thiết kế Cài đặt và thiết kế màn vẽ đều không có lối vào xem lại.
    - Đề xuất của AI: thêm dòng "Replay drawing tour" vào nhóm "Drawing" của Cài đặt (cần bổ sung thiết kế); bấm thì lần mở màn vẽ tới hướng dẫn hiện lại từ bước 1. Phần hướng dẫn thuộc spec editor, Cài đặt chỉ đặt lại mốc "đã xem". Spec editor câu 15 đề xuất lối vào trong menu "thêm" của màn vẽ; trả lời chung hai câu, có thể chọn một hay cả hai lối vào.
    - Trả lời: Chỉ một lối vào: mục trong menu "thêm" của màn vẽ (spec editor câu 15). Cài đặt không thêm dòng.
15. **Khi cờ `feature_settings_enabled` tắt:** người dùng mất lối vào lựa chọn quyền riêng tư quảng cáo, và giá trị mặc định đã chọn trước đó có còn dùng ở màn Tạo dự án không?
    - Đề xuất của AI: bật cờ trên production từ ngày phát hành, vì đây là lối vào duy nhất để rút đồng ý quảng cáo. Khi cờ tắt, màn Tạo dự án dùng 12 fps, 9:16, giá trị đã lưu vẫn giữ để dùng lại khi bật cờ.
    - Trả lời: Đồng ý đề xuất của AI.

## 10. Lịch sử

| Ngày | Người | Nội dung |
| --- | --- | --- |
| 2026-11-21 | AI | Tạo bản đầu từ bản đề xuất đã duyệt (docs/research/proposal.md) và thiết kế đã duyệt |
| 2026-11-21 | AI | Ghi câu trả lời của anh Nhật cho mọi câu hỏi ở mục 9 (anh chọn trong phiên ngày 2026-11-21) và sửa phần thân spec theo các câu trả lời; sửa các điểm nên sửa còn lại của lần rà soát cuối. Spec vẫn `draft` |
