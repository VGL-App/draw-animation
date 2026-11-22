---
feature: projects
status: changed
owner: Nhật
product_owner: Nhật
requests:
  - requests/2026-11-21-ban-dau.md
design: design/README.md
created: 2026-11-21
confirmed: 2026-11-21
---

# SPEC: DANH SÁCH DỰ ÁN VÀ TẠO DỰ ÁN

## 1. Mục tiêu

- **Người dùng được gì:** một chỗ để thấy mọi hoạt hình của mình, mở lại để vẽ tiếp, tạo hoạt hình mới chỉ với hai lần chạm, và giữ bài an toàn bằng cách sao lưu từng dự án ra một file rồi khôi phục khi cần.
- **Vì sao làm bây giờ:** đây là màn chính của app, nằm trên đường từ lần mở đầu tới nét vẽ đầu tiên. App mẫu bắt người mới qua 10 thao tác và màn trả phí; người dùng của app mẫu phàn nàn nhiều nhất về mất bài (bản đề xuất, mục 4).
- **Đo kết quả bằng gì:**
  - Từ lần mở đầu tới nét vẽ đầu tiên không quá 7 thao tác; phần của feature này là 2 thao tác ("New animation", "Create").
  - Số dự án được tạo và được mở lại mỗi ngày (sự kiện ở mục 6; lần mở lại do màn vẽ ghi, spec editor câu 5).
  - Số lần sao lưu và khôi phục thành công (sự kiện ở mục 6); không đo tỷ lệ khôi phục thành công, vì sự kiện chỉ ghi lần thành công (câu hỏi 17).

## 2. Phạm vi

**Trong phạm vi:**

- Màn danh sách dự án: trạng thái trống (lời mời, nút "New animation", nút "Try a free lesson" dẫn sang danh sách bài học, chỉ khi feature Bài học bật) và trạng thái có dự án (lưới thẻ dự án, mới sửa gần nhất ở đầu).
- Thanh trên của danh sách: tên app, nút nhập dự án từ file, nút cài đặt mở màn Cài đặt (chỉ khi feature Cài đặt bật).
- Thanh điều hướng hai mục "Projects" và "Lessons" ở đáy màn (cột bên trái khi điện thoại gập mở ra); mục "Lessons" chỉ có khi feature Bài học bật.
- Mở một dự án vào màn vẽ.
- Menu dự án khi nhấn giữ thẻ: mở, đổi tên, nhân bản, sao lưu ra file, xóa.
- Sao lưu một dự án ra một file qua bộ chọn file của hệ thống; người dùng chọn được thư mục trên máy hay Google Drive.
- Khôi phục một dự án từ file: hộp thoại đang khôi phục có tiến độ và nút hủy, hộp thoại lỗi khi file hỏng hay không phải file sao lưu của app.
- Màn Tạo dự án: tên, khung hình (9:16, 16:9, 1:1), độ phân giải (720p, 1080p), tốc độ khung (6, 8, 12, 15, 24, 30 khung/giây), nền (trắng, xám đậm, xanh, hay một ảnh của người dùng); giá trị mặc định lấy từ Cài đặt.
- Bố cục riêng cho điện thoại gập khi mở ra.
- Các vị trí quảng cáo của mục 5.

**Ngoài phạm vi lần này:**

- Màn vẽ, tự lưu trong lúc vẽ, khôi phục sau khi app tắt đột ngột (feature `editor` và phần dùng chung).
- Màn Cài đặt, gồm sao lưu và khôi phục tất cả dự án (feature `settings`).
- Danh sách bài học, mở bài học, màn hoàn thành bài học (feature `lessons`). Bài học không phải dự án: không hiện trong danh sách này và không tính vào số dự án.
- Xuất video, GIF (feature `export`).
- Tài khoản, lưu trên đám mây của app, đồng bộ giữa các máy. Google Drive chỉ là một nơi người dùng tự chọn trong bộ chọn file.
- Tìm kiếm, thư mục, đổi cách sắp xếp danh sách (xem câu hỏi 6).
- Chế độ không có mạng: app luôn cần mạng. Mất mạng thì app chặn bằng popup chung; feature này không tự xử lý.

## 3. Kịch bản sử dụng

### KB-1. Lần đầu vào danh sách, chưa có dự án

Người mới vừa qua onboarding, hoặc người dùng đã xóa hết dự án.

Tiêu chí chấp nhận:

- [ ] Khi chưa có dự án nào thì màn hiện hình minh họa, tiêu đề "Make your first animation", lời giới thiệu "Draw frame by frame, add sound, then export a video or GIF. Everything saves on your phone as you go.", nút chính "New animation" và nút phụ "Try a free lesson" (giữ chữ "free", câu hỏi 22), đúng thiết kế màn trống.
- [ ] Khi bấm "New animation" thì màn Tạo dự án mở ra (KB-3).
- [ ] Khi bấm "Try a free lesson" thì danh sách bài học mở ra (feature `lessons`).
- [ ] Khi cờ của feature Bài học tắt thì màn trống không có nút "Try a free lesson" và thanh điều hướng không có mục "Lessons"; thanh còn một mục thì ẩn cả thanh (xem spec lessons KB-10, câu 14).
- [ ] Khi người dùng xóa dự án cuối cùng thì màn chuyển về trạng thái trống ngay, không cần mở lại.
- [ ] Khi người dùng đã làm bài học nhưng chưa tạo dự án nào thì màn vẫn ở trạng thái trống.
- [ ] Khi bấm nút quay lại của máy ở danh sách dự án thì app đóng.

Trường hợp đặc biệt:

- [ ] Khi màn hình thấp 360 × 640 thì nội dung cuộn được; hai nút không bị che hay cắt.
- [ ] Khi mất mạng thì app hiện popup mất mạng chung; feature không có màn riêng cho trường hợp này.

### KB-2. Xem danh sách và mở một dự án

Người dùng đã có dự án.

Tiêu chí chấp nhận:

- [ ] Khi có ít nhất một dự án thì màn hiện nút "New animation" ở đầu, tiêu đề "Your projects" với nhãn "Recent", và lưới thẻ dự án hai cột, đúng thiết kế màn danh sách.
- [ ] Khi xem một thẻ thì thẻ có ảnh thu nhỏ của dự án (khung đầu tiên, gộp mọi layer đang hiện, trên nền của dự án; câu hỏi 20), tên dự án và dòng "<số khung> frames · <tốc độ> fps" (ví dụ "48 frames · 12 fps").
- [ ] Khi danh sách có nhiều dự án thì dự án sửa gần nhất đứng đầu; dự án vừa tạo, vừa nhân bản hay vừa khôi phục đứng đầu.
- [ ] Khi chạm một thẻ thì dự án đó mở trong màn vẽ.
- [ ] Khi cờ của màn vẽ tắt mà người dùng dùng bất kỳ lối nào mở màn vẽ (chạm một thẻ, bấm "Open" trong menu dự án, bấm "Create") thì app hiện thông báo ngắn "Coming soon" và ở lại màn đang đứng, không mở màn vẽ; bấm "Create" lúc đó không tạo dự án (spec editor câu 20).
- [ ] Khi từ màn vẽ quay về danh sách thì thẻ của dự án đó đứng đầu, ảnh thu nhỏ và số khung đúng với nội dung vừa vẽ.
- [ ] Khi danh sách dài hơn màn thì cuộn được; thanh trên và thanh điều hướng giữ nguyên chỗ.
- [ ] Khi tên dự án dài hơn bề rộng thẻ thì tên cắt bằng dấu "…" trên một dòng, không đè lên dòng số khung.

Trường hợp đặc biệt:

- [ ] Khi có 100 dự án thì danh sách mở ra và cuộn không giật trên máy tầm trung.
- [ ] Khi một dự án không đọc được (dữ liệu hỏng) thì các dự án khác vẫn hiện và mở được bình thường .
- [ ] Khi một dự án không đọc được thì thẻ của nó vẫn hiện với tên (nếu đọc được) và ảnh thu nhỏ trống; chạm thẻ thì app báo không mở được; menu của thẻ chỉ còn "Delete"; app không tự xóa dự án (câu hỏi 9).

### KB-3. Tạo dự án mới

Người dùng bấm "New animation" ở danh sách.

Tiêu chí chấp nhận:

- [ ] Khi màn Tạo dự án mở thì có tiêu đề "New animation", nút đóng, ô "Name", nhóm "Canvas" (9:16, 16:9, 1:1), nhóm "Size" (720p, 1080p), nhóm "Frame rate" (6, 8, 12, 15, 24, 30 fps), nhóm "Background" (trắng, xám đậm, xanh, "Photo") và nút "Create" ghim ở đáy, đúng thiết kế màn Tạo dự án.
- [ ] Khi màn mở thì mỗi nhóm đã có sẵn một lựa chọn: khung hình và tốc độ khung lấy theo "Default canvas" và "Default frame rate" của màn Cài đặt (chưa đổi thì 9:16 và 12 khung/giây); độ phân giải 1080p; nền trắng; tên "My animation <số>"; số là số dự án đã từng tạo cộng một, không giảm khi xóa (câu hỏi 4).
- [ ] Khi bấm "Create" mà không đổi gì thì dự án được tạo với các giá trị mặc định trên và mở ngay trong màn vẽ. Từ danh sách trống tới màn vẽ là đúng 2 lần chạm.
- [ ] Khi chọn một lựa chọn khác trong một nhóm thì lựa chọn đó sáng lên, lựa chọn cũ của nhóm tắt; mỗi nhóm luôn có đúng một lựa chọn.
- [ ] Khi tạo dự án 16:9 ở 1080p thì khung vẽ là 1920 × 1080; 9:16 ở 1080p là 1080 × 1920; 1:1 ở 1080p là 1080 × 1080; 720p cho 1280 × 720, 720 × 1280, 720 × 720.
- [ ] Khi bấm "Photo" thì bộ chọn ảnh của hệ thống mở ra, không hỏi quyền; chọn một ảnh thì ảnh đó là nền của dự án; đóng bộ chọn mà không chọn thì nền giữ lựa chọn trước; ảnh là nền cố định của mọi khung, có trong file xuất, được cắt vừa khung hình (câu hỏi 3).
- [ ] Khi ảnh gốc bị xóa khỏi máy sau khi tạo dự án thì nền của dự án không đổi, vì app đã chép ảnh vào dự án (câu hỏi 3).
- [ ] Khi bấm nút đóng hay nút quay lại của máy thì về danh sách, không tạo dự án nào.
- [ ] Khi tạo dự án thì dự án nằm trên máy, xuất hiện ở đầu danh sách khi quay về.

Trường hợp đặc biệt:

- [ ] Khi xóa hết chữ trong ô tên rồi bấm "Create" thì dự án dùng tên mặc định (câu hỏi 5).
- [ ] Khi nhập tên thì ô tên nhận tối đa 40 ký tự (câu hỏi 5).
- [ ] Khi đây là dự án đầu tiên người dùng tạo thì bấm "Create" không hiện quảng cáo toàn màn hình nào.
- [ ] Khi máy không đủ chỗ trống để tạo dự án thì app báo lỗi bằng lời dễ hiểu, giữ người dùng ở màn Tạo dự án với các lựa chọn đã chọn, không tạo dự án dở.
- [ ] Khi màn hình thấp 360 × 640 thì phần tùy chọn cuộn được; nút "Create" luôn thấy ở đáy, không che lựa chọn cuối khi cuộn hết.
- [ ] Khi bấm "Create" hai lần liên tiếp thật nhanh thì chỉ một dự án được tạo.

### KB-4. Mở menu của một dự án

Tiêu chí chấp nhận:

- [ ] Khi nhấn giữ một thẻ dự án thì menu trượt lên từ đáy, nền phía sau tối đi; đầu menu có ảnh thu nhỏ, tên dự án và dòng "<số khung> frames · <tốc độ> fps · edited <thời điểm>"; các mục "Open", "Rename", "Duplicate", "Back up to a file" (kèm chú thích ".frameloop"), "Delete" (màu báo lỗi), đúng thiết kế menu dự án.
- [ ] Khi bấm "Open" thì dự án mở trong màn vẽ.
- [ ] Khi chạm ra ngoài menu, kéo menu xuống hay bấm nút quay lại của máy thì menu đóng, danh sách giữ nguyên.

Trường hợp đặc biệt:

- [ ] Khi menu đang mở thì không có quảng cáo nào trong menu.

### KB-5. Đổi tên dự án

Tiêu chí chấp nhận:

- [ ] Khi bấm "Rename" thì hộp thoại đổi tên mở ra với tiêu đề "Rename", nút "Save" và "Cancel", ô nhập có sẵn tên hiện tại và bàn phím hiện lên (câu hỏi 2).
- [ ] Khi nhập tên mới và xác nhận thì thẻ hiện tên mới ngay; mở lại app vẫn giữ tên mới.
- [ ] Khi hủy thì tên giữ nguyên.

Trường hợp đặc biệt:

- [ ] Khi ô nhập trống hay chỉ có khoảng trắng thì nút xác nhận bị tắt.
- [ ] Khi tên mới trùng tên một dự án khác thì vẫn cho lưu (câu hỏi 5).

### KB-6. Nhân bản dự án

Tiêu chí chấp nhận:

- [ ] Khi bấm "Duplicate" thì một dự án mới có cùng khung, layer, âm thanh, khung hình, độ phân giải, tốc độ và nền xuất hiện ở đầu danh sách, tên "<tên> copy" (lần sau "<tên> copy 2"), cắt bớt tên gốc khi vượt 40 ký tự (câu hỏi 5); dự án gốc không đổi.
- [ ] Khi vẽ trên bản sao thì dự án gốc không đổi, và ngược lại.

Trường hợp đặc biệt:

- [ ] Khi máy không đủ chỗ trống thì app báo lỗi bằng lời dễ hiểu và không để lại bản sao dở trong danh sách.
- [ ] Khi dự án lớn (200 khung × 5 layer) thì có dấu hiệu đang làm trong lúc nhân bản; người dùng không bấm nhân bản lần nữa được tới khi xong.

### KB-7. Xóa dự án

Tiêu chí chấp nhận:

- [ ] Khi bấm "Delete" thì app hỏi xác nhận bằng hộp thoại "Delete “<tên>”?", lời "This can't be undone.", nút "Delete" và "Cancel" trước khi xóa; đã xóa thì không hoàn tác được (câu hỏi 7).
- [ ] Khi xác nhận thì thẻ biến mất khỏi danh sách và dữ liệu của dự án bị xóa khỏi máy; mở lại app không thấy lại dự án.
- [ ] Khi hủy thì dự án giữ nguyên.
- [ ] Khi xóa dự án cuối cùng thì màn về trạng thái trống (KB-1).

### KB-8. Sao lưu một dự án ra file

Người dùng muốn giữ bản sao của một dự án ngoài app, ví dụ trên Google Drive, để không mất bài khi đổi máy hay cài lại app.

Tiêu chí chấp nhận:

- [ ] Khi bấm "Back up to a file" thì bộ chọn file của hệ thống mở ra để lưu, tên file gợi ý là tên dự án với đuôi ".frameloop"; app không hỏi quyền nào.
- [ ] Khi người dùng chọn một thư mục trên máy hay trên Google Drive và lưu thì file được ghi xong, app báo đã sao lưu bằng thông báo ngắn "Backup saved" ở đáy, tự ẩn, không có nút (câu hỏi 8).
- [ ] Khi khôi phục file vừa sao lưu (KB-9) thì được một dự án giống hệt: cùng số khung, layer, âm thanh, khung hình, độ phân giải, tốc độ, nền và tên.
- [ ] Khi người dùng đóng bộ chọn file mà không lưu thì không có gì xảy ra, không báo lỗi.

Trường hợp đặc biệt:

- [ ] Khi ghi file thất bại (hết chỗ, mất quyền ghi vào nơi đã chọn) thì app báo bằng thông báo ngắn "Couldn't save the backup. Try again." có nút "Retry" (câu hỏi 2); dự án trên máy không bị ảnh hưởng.
- [ ] Khi dự án lớn thì có dấu hiệu đang ghi tới khi xong; người dùng vẫn ở danh sách.

### KB-9. Khôi phục một dự án từ file

Người dùng có file ".frameloop" đã sao lưu trước đó.

Tiêu chí chấp nhận:

- [ ] Khi bấm nút nhập file trên thanh trên thì bộ chọn file của hệ thống mở ra để chọn file; người dùng chọn được file trên máy hay trên Google Drive; app không hỏi quyền nào.
- [ ] Khi chọn một file sao lưu hợp lệ thì hộp thoại đang khôi phục hiện ra với tiêu đề "Restoring “<tên dự án>”", lời "Keep the app open. This takes a few seconds.", thanh tiến độ, dòng "<số khung đã xong> of <tổng số khung> frames" và phần trăm, nút "Cancel", đúng thiết kế hộp thoại đang khôi phục.
- [ ] Khi file chứa nhiều dự án (sao lưu tất cả từ Cài đặt) thì hộp thoại ghi số dự án thay cho tên một dự án, tiến độ tính trên tổng số khung của mọi dự án trong file; xong thì mọi dự án khôi phục đứng đầu danh sách (câu hỏi 19).
- [ ] Khi khôi phục xong thì hộp thoại đóng, dự án mới đứng đầu danh sách và mở được trong màn vẽ, giống hệt dự án lúc sao lưu.
- [ ] Khi bấm "Cancel" thì việc khôi phục dừng, hộp thoại đóng, về danh sách như trước khi khôi phục; không có dự án dở nào trong danh sách hay trên máy.
- [ ] Khi đóng bộ chọn file mà không chọn thì không có gì xảy ra.
- [ ] Khi khôi phục từ trạng thái trống thì sau khi xong, màn chuyển sang trạng thái có dự án.

Trường hợp đặc biệt:

- [ ] Khi khôi phục file của một dự án vẫn còn trong danh sách thì app tạo một dự án mới bên cạnh, không ghi đè dự án đang có (câu hỏi 10).
- [ ] Khi hộp thoại đang khôi phục mở thì chạm ra ngoài hộp thoại không đóng nó; nút quay lại của máy làm như "Cancel" (câu hỏi 11).
- [ ] Khi màn hình thấp 360 × 640 thì nội dung hộp thoại cuộn được, nút "Cancel" luôn tới được (câu hỏi 13).
- [ ] Khi người dùng rời app trong lúc khôi phục rồi quay lại thì khôi phục vẫn tiếp tục, hoặc nếu app đã bị hệ thống đóng thì không còn dự án dở (câu hỏi 11).
- [ ] Khi máy không đủ chỗ trống thì việc khôi phục dừng, không để lại dự án dở, và hộp thoại lỗi ghi "Not enough space on your phone. Free up some space and try again." (câu hỏi 12).

### KB-10. File không khôi phục được

Tiêu chí chấp nhận:

- [ ] Khi file chọn không phải file sao lưu của app, hay file bị hỏng, thì hộp thoại lỗi hiện tiêu đề "Can't open this file", lời "This file is not a Frameloop backup, or it is damaged. Your other projects are safe.", hai nút "Close" và "Choose another file", đúng thiết kế hộp thoại lỗi khôi phục.
- [ ] Khi bấm "Close" thì hộp thoại đóng, danh sách giữ nguyên.
- [ ] Khi bấm "Choose another file" thì bộ chọn file mở lại.
- [ ] Khi khôi phục thất bại giữa chừng thì không có dự án dở trong danh sách và các dự án khác không đổi.

Trường hợp đặc biệt:

- [ ] Khi file được tạo bởi một bản app mới hơn mà bản đang chạy không đọc được thì app báo lỗi, không tạo dự án dở; hộp thoại lỗi ghi "This backup was made with a newer version of Frameloop. Update the app and try again." (câu hỏi 12).

### KB-11. Mở Cài đặt

Tiêu chí chấp nhận:

- [ ] Khi bấm nút cài đặt trên thanh trên thì màn Cài đặt mở ra (feature `settings`).
- [ ] Khi từ Cài đặt quay lại thì về danh sách, đúng chỗ đang cuộn.
- [ ] Khi đổi "Default canvas" hay "Default frame rate" trong Cài đặt rồi mở màn Tạo dự án thì lựa chọn sẵn theo giá trị mới.
- [ ] Khi cờ của feature Cài đặt tắt thì thanh trên không có nút cài đặt, và màn Tạo dự án chọn sẵn 12 khung/giây, 9:16 (xem spec settings KB-10, câu 15).
- [ ] Khi cờ của feature Cài đặt tắt mà người dùng ở vùng bắt buộc hộp thoại đồng ý quảng cáo thì thanh trên hiện nút lựa chọn quyền riêng tư quảng cáo ở chỗ nút cài đặt; bấm thì hộp thoại lựa chọn quyền riêng tư quảng cáo dùng chung của app mở lại, như dòng "Ad privacy choices" của màn Cài đặt (spec settings KB-7). Ở vùng không bắt buộc thì không hiện nút nào (câu hỏi 23).

### KB-12. Dùng trên điện thoại gập khi mở ra

Tiêu chí chấp nhận:

- [ ] Khi màn hình rộng như điện thoại gập mở ra (673 × 841) thì thanh điều hướng thành cột bên trái, nút "New animation" nằm trên thanh trên cạnh nút nhập file và nút cài đặt, lưới thẻ có ba cột, đúng thiết kế danh sách khi điện thoại gập mở ra.
- [ ] Khi gập hay mở máy trong lúc dùng thì màn đổi bố cục, giữ dự án đang xem, menu hay hộp thoại đang mở, và việc khôi phục đang chạy.
- [ ] Khi xoay máy hay gập mở máy trong lúc ở màn Tạo dự án thì các lựa chọn đã chọn giữ nguyên.

### KB-13. Tắt feature từ xa

Tiêu chí chấp nhận:

- [ ] Khi cờ của feature tắt thì app mở vào màn mẫu có sẵn từ dự án mẫu (màn mà app mới tạo từ dự án mẫu mở ra, trước khi có các feature của Frameloop), người dùng không vào được danh sách dự án và màn Tạo dự án (câu hỏi 18).

## 4. Giao diện

- **Thiết kế:** [design/README.md](design/README.md), dựng trên Figma theo hướng Studio Night, có chế độ sáng và tối. Chữ theo bảng chữ của thiết kế (tiếng Anh, bản đề xuất Q9).
- **Thiết kế đã sửa ngày 2026-11-21, anh Nhật duyệt ngày 2026-11-21 (chọn "Duyệt" trong phiên):** bỏ ô tỷ lệ 4:5 trên frame màn Tạo dự án (sáng, tối), ảnh và số liệu đã xuất lại (câu hỏi 1).
- **Các màn hình:**

| Màn hình | Trạng thái theo thiết kế | Mục đích | Kịch bản liên quan |
| --- | --- | --- | --- |
| `ProjectsScreen` | empty | Danh sách trống, mời tạo dự án đầu tiên hay học thử | KB-1, KB-11 |
| `ProjectsScreen` | list | Lưới dự án, mở dự án | KB-2, KB-11 |
| `ProjectsScreen` | list, điện thoại gập mở ra (`_unfolded`) | Bố cục rộng: cột điều hướng, ba cột thẻ | KB-12 |
| `ProjectsScreen` | menu | Menu dự án: mở, đổi tên, nhân bản, sao lưu, xóa | KB-4, KB-5, KB-6, KB-7, KB-8 |
| `ProjectsScreen` | restoring | Hộp thoại đang khôi phục, có tiến độ và nút hủy | KB-9 |
| `ProjectsScreen` | restore-error | Hộp thoại lỗi khôi phục | KB-10 |
| `NewProjectScreen` | default | Chọn tên, khung hình, độ phân giải, tốc độ, nền rồi tạo | KB-3 |
| Hộp thoại đổi tên | hộp thoại chuẩn của hệ thống thiết kế, không có frame riêng | Nhập tên mới | KB-5 (câu hỏi 2) |
| Hộp thoại xác nhận xóa | hộp thoại chuẩn của hệ thống thiết kế, không có frame riêng | Xác nhận trước khi xóa | KB-7 (câu hỏi 2, 7) |
| Thông báo sao lưu xong, sao lưu lỗi, đang nhân bản | thông báo ngắn chuẩn của hệ thống thiết kế, không có frame riêng | Báo kết quả | KB-6, KB-8 (câu hỏi 2, 8) |

## 5. Quảng cáo

Chép từ bản đề xuất, mục 9.1. Mọi vị trí mặc định tắt, bật bằng khóa của nó trên Remote Config. Nhóm A bật khi phát hành, nhóm B dựng sẵn và bật dần.

**Màn hình không có quảng cáo:** menu dự án, hộp thoại đổi tên, hộp thoại xác nhận xóa, hộp thoại lỗi khôi phục. `restore_mrec` chỉ hiện lúc đang khôi phục.

| Vị trí | Định dạng | Màn hình | Khi nào hiển thị | Giới hạn tần suất | Khóa bật tắt |
| --- | --- | --- | --- | --- | --- |
| `projects_empty_native` | native medium | Danh sách dự án, trạng thái trống | Dưới hai nút "New animation" và "Try a free lesson", cách ít nhất 24 dp; mọi lúc danh sách trống, kể cả sau khi xóa hết dự án và trước nét vẽ đầu tiên. Nhóm A | Không | `is_show_projects_empty_native` |
| `projects_bottom` | thanh đáy native nhỏ | Danh sách dự án | Trên thanh điều hướng, chỉ ở trạng thái có dự án; trạng thái trống không có thanh đáy (câu hỏi 14). Nhóm A | Không | `is_show_projects_bottom` |
| `projects_grid` | native ô lưới | Danh sách dự án, trạng thái có dự án | Một ô sau mỗi 6 dự án (sau dự án thứ 6, 12, ...); có cả ở bố cục điện thoại gập (câu hỏi 15). Nhóm A | Không | `is_show_projects_grid` |
| `restore_mrec` | banner MREC | Hộp thoại đang khôi phục | Dưới thanh tiến độ, cách nút "Cancel" ít nhất 24 dp; hộp thoại rộng 348 dp, lề trong 24 dp; chỉ lúc đang khôi phục, không ở hộp thoại lỗi. Nhóm A | Không | `is_show_restore_mrec` |
| `create_inter` | interstitial | Tạo dự án, khi bấm "Create" | Từ dự án thứ 2 (câu hỏi 16); bài học không tính. Nhóm A | Theo `ads_interval` (30 giây) | `is_show_create_inter` |
| `new_project_native` | native medium | Tạo dự án | Trong phần cuộn, giữa nhóm "Frame rate" và nhóm "Background", cách mỗi nhóm 24 dp; giữ chỗ cả khi chưa có quảng cáo; nút "Create" ghim đáy. Nhóm B | Không | `is_show_new_project_native` |

- Màn danh sách khai báo ba vị trí trong nội dung `projects_empty_native`, `projects_grid`, `restore_mrec`: khi một trong ba đang bật thì thanh đáy không có lớp native thu gọn (MC-13).
- Sau `create_inter` có thể hiện `native_combo` (native toàn màn hình sau interstitial của feature), theo spec `ads`.
- **Trước nét vẽ đầu tiên** (bản đề xuất, đoạn dưới bảng 9.1): tới khi người dùng vẽ nét đầu tiên ở bất kỳ dự án hay bài học nào, feature này chỉ hiện `projects_empty_native` trên màn trống. `projects_bottom`, `projects_grid`, `restore_mrec`, `create_inter`, `new_project_native` không hiện, kể cả khi người dùng đã tạo dự án mà chưa vẽ nét nào. Mốc nét vẽ đầu tiên được nhớ trên máy, dùng chung với các feature khác.
- "Từ dự án thứ 2" do app tự đếm, không phải luật của phần quảng cáo dùng chung.

## 6. Dữ liệu và phân tích

**Dữ liệu lưu trên máy:**

Mọi dữ liệu chỉ lưu trên máy, không tài khoản, không gửi lên máy chủ (bản đề xuất, mục 9.4). Dữ liệu dự án thuộc phần dùng chung cho `projects`, `editor`, `export`.

| Dữ liệu | Mục đích | Giữ trong bao lâu |
| --- | --- | --- |
| Dự án: tên, khung hình, độ phân giải, tốc độ khung, nền (kể cả bản sao ảnh nền người dùng chọn), các khung và layer, âm thanh, thời điểm sửa gần nhất, ảnh thu nhỏ | Hiện danh sách, mở, nhân bản, sao lưu | Đến khi người dùng xóa dự án hay xóa dữ liệu app |
| Số dự án người dùng đã tạo | Điều kiện "từ dự án thứ 2" của `create_inter` và số trong tên mặc định (câu hỏi 4, 16) | Đến khi xóa dữ liệu app |
| Mốc "đã vẽ nét đầu tiên" (dùng chung, đọc ở đây) | Luật "Trước nét vẽ đầu tiên" | Đến khi xóa dữ liệu app |
| Khung hình và tốc độ mặc định (của feature `settings`, đọc ở đây) | Lựa chọn sẵn ở màn Tạo dự án | Đến khi xóa dữ liệu app |

File sao lưu ".frameloop" nằm ở nơi người dùng chọn (trên máy hay Google Drive); app không giữ bản sao hay đường dẫn tới nó.

**Sự kiện phân tích:**

Chỉ các sự kiện của bản đề xuất mục 9.2 thuộc feature này; danh sách và tham số đã được anh Nhật duyệt (câu hỏi 17). Không ghi tên dự án, nội dung tranh, âm thanh hay chữ người dùng nhập.

| Tên sự kiện | Khi nào ghi | Tham số | Dùng để trả lời câu hỏi gì |
| --- | --- | --- | --- |
| `project_create` | Dự án được tạo xong ở màn Tạo dự án | `ratio` (9:16, 16:9, 1:1), `size` (720p, 1080p), `fps`, `background` (white, dark, blue, photo), `is_default` (người dùng không đổi lựa chọn nào) | Người dùng tạo bao nhiêu dự án, chọn khung và tốc độ nào, có giữ mặc định không |
| `backup_export` | Sao lưu ghi file xong (chỉ khi thành công) | `source` (`settings` khi bấm "Back up all projects" ở Cài đặt, `projects` khi bấm "Back up to a file" ở menu dự án), `project_count` | Bao nhiêu người sao lưu, bấm từ Cài đặt hay từ danh sách dự án |
| `backup_restore` | Khôi phục xong (chỉ khi thành công) | `source` (`settings` khi bấm "Restore from a file" ở Cài đặt, `projects` khi bấm nút nhập file ở danh sách dự án), `project_count` (số dự án đã khôi phục) | Bao nhiêu người khôi phục, từ đâu, bao nhiêu dự án |

Hai sự kiện sao lưu và khôi phục dùng chung với spec `settings`, cùng tên và tham số. Lần mở một dự án (`project_open`) do màn vẽ ghi (spec editor, mục 6).

**Quyền của app và dịch vụ bên ngoài:** Không có quyền mới. Sao lưu và khôi phục dùng bộ chọn file của hệ thống, không cần quyền đọc ghi bộ nhớ ở mọi phiên bản Android từ 8.0; Google Drive chỉ hiện trong bộ chọn đó nếu máy có ứng dụng Drive, app không đăng nhập Google và không dùng thư viện của Drive. Chọn ảnh nền dùng bộ chọn ảnh của hệ thống, không cần quyền. Quyền `WRITE_EXTERNAL_STORAGE` (Android 9 trở xuống, bản đề xuất Q5) chỉ dùng cho việc lưu file xuất vào thư viện máy của feature `export`, feature này không cần.

## 7. File tĩnh trên R2

Không có.

## 8. Cờ tính năng

| Tên cờ | Mặc định | Ý nghĩa |
| --- | --- | --- |
| `feature_projects_enabled` | tắt | Bật để mở danh sách dự án và màn Tạo dự án cho người dùng. Bản staging bật sẵn; bản production bật từ lần phát hành đầu và không tắt sau đó (câu hỏi 18) |

## 9. Câu hỏi còn mở

1. **Tỷ lệ khung 4:5 có trên thiết kế màn Tạo dự án nhưng bản đề xuất chỉ có 16:9, 9:16, 1:1 (mục 5).** Spec đang theo bản đề xuất.
   - Đề xuất của AI: bỏ 4:5 khỏi thiết kế cho đúng bản đề xuất đã duyệt, sửa frame Figma và xuất lại. Nếu muốn giữ 4:5 thì ghi kích thước (1080 × 1350 ở 1080p, 720 × 900 ở 720p) và feature `export`, `settings` ("Default canvas") cũng phải có 4:5.
   - Trả lời: Bỏ 4:5 khỏi thiết kế, theo đề xuất của AI.
2. **Thiết kế chưa có hộp thoại đổi tên, hộp thoại xác nhận xóa, thông báo sao lưu xong hay lỗi, dấu hiệu đang nhân bản.**
   - Đề xuất của AI: dùng hộp thoại và thông báo ngắn (snackbar) chuẩn của hệ thống thiết kế, không cần frame mới; chữ đề xuất: "Rename" / "Save" / "Cancel"; "Delete “<tên>”?" / "This can't be undone." / "Delete" / "Cancel"; "Backup saved"; "Couldn't save the backup. Try again." Nếu muốn so ảnh thiết kế thì thêm frame trên Figma trước khi viết plan.
   - Trả lời: Đồng ý đề xuất của AI.
3. **Nền "Photo" ở màn Tạo dự án:** bản đề xuất có nhập ảnh, video làm nền vẽ theo trong màn vẽ (feature `editor`), không nói tới chọn ảnh lúc tạo dự án. Ảnh nền là nền cố định của mọi khung (xuất ra cả trong video) hay chỉ là lớp vẽ theo (không xuất)?
   - Đề xuất của AI: giữ "Photo" như thiết kế; ảnh là nền cố định của mọi khung, có trong file xuất, ảnh được cắt vừa khung hình; chép ảnh vào dự án để xóa ảnh gốc không ảnh hưởng. Lớp vẽ theo vẫn ở màn vẽ.
   - Trả lời: Ảnh là nền cố định của mọi khung, có trong file xuất, theo đề xuất của AI.
4. **Tên mặc định "My animation 5":** số lấy theo gì?
   - Đề xuất của AI: số = số dự án đã từng tạo cộng một (không giảm khi xóa), để tên không trùng với dự án đã xóa rồi tạo lại.
   - Trả lời: Đồng ý đề xuất của AI.
5. **Quy tắc tên dự án:** độ dài tối đa, tên trùng, tên của bản nhân bản.
   - Đề xuất của AI: tối đa 40 ký tự; cho trùng tên; ô tên trống khi bấm "Create" thì dùng tên mặc định; bản nhân bản tên "<tên> copy" (lần sau "<tên> copy 2"), cắt bớt tên gốc nếu vượt 40 ký tự.
   - Trả lời: Đồng ý đề xuất của AI.
6. **Nhãn "Recent" ở danh sách:** chỉ là nhãn cho biết thứ tự, hay bấm được để đổi cách sắp xếp (tên, ngày tạo)?
   - Đề xuất của AI: chỉ là nhãn, không bấm được ở bản đầu; đổi cách sắp xếp để bản sau. Bố cục điện thoại gập không có nhãn này trên thiết kế, giữ như thiết kế.
   - Trả lời: Đồng ý đề xuất của AI.
7. **Xóa dự án có hỏi xác nhận không, có hoàn tác được không?** Thiết kế không có hộp thoại xác nhận; bản đề xuất hứa "không bao giờ mất bài".
   - Đề xuất của AI: hỏi xác nhận bằng hộp thoại; không có thùng rác hay hoàn tác ở bản đầu.
   - Trả lời: Đồng ý đề xuất của AI.
8. **Cách báo sao lưu xong:** thông báo ngắn ở đáy hay hộp thoại? Có nút mở hay chia sẻ file không?
   - Đề xuất của AI: thông báo ngắn "Backup saved" ở đáy, tự ẩn, không có nút.
   - Trả lời: Đồng ý đề xuất của AI.
9. **Dự án không đọc được (dữ liệu hỏng)** hiện thế nào trong danh sách?
   - Đề xuất của AI: vẫn hiện thẻ với tên (nếu đọc được) và ảnh thu nhỏ trống; chạm thì báo không mở được, menu chỉ còn "Delete". Không tự xóa.
   - Trả lời: Đồng ý đề xuất của AI.
10. **Khôi phục file của dự án đang có trong danh sách:** tạo dự án mới hay hỏi ghi đè?
    - Đề xuất của AI: luôn tạo dự án mới, giữ tên trong file; người dùng tự xóa bản cũ nếu muốn. Không ghi đè để không mất bài.
    - Trả lời: Đồng ý đề xuất của AI.
11. **Nút quay lại của máy và rời app trong lúc khôi phục.** Thiết kế chỉ nói "Cancel dừng khôi phục, về danh sách, không tạo dự án dở".
    - Đề xuất của AI: nút quay lại làm như "Cancel"; chạm ra ngoài không đóng hộp thoại; rời app thì khôi phục vẫn chạy khi app còn sống, app bị hệ thống đóng thì phần dở bị dọn ở lần mở sau, không hiện trong danh sách.
    - Trả lời: Đồng ý đề xuất của AI.
12. **Lời báo lỗi khôi phục khi máy hết chỗ hay file của bản app mới hơn:** thiết kế chỉ có một lời "not a Frameloop backup, or it is damaged", không đúng với hai trường hợp này.
    - Đề xuất của AI: dùng cùng hộp thoại, đổi lời: hết chỗ "Not enough space on your phone. Free up some space and try again."; bản mới hơn "This backup was made with a newer version of Frameloop. Update the app and try again."
    - Trả lời: Đồng ý đề xuất của AI.
13. **Hộp thoại khôi phục ở màn 360 × 640:** hộp thoại cao hơn màn (MREC 250 cộng tiêu đề, tiến độ, nút). Thiết kế ghi nội dung cuộn được.
    - Đề xuất của AI: phần nội dung (tiêu đề, lời, tiến độ, quảng cáo) cuộn được, nút "Cancel" luôn ở đáy hộp thoại; có screenshot test ở khung 360 × 640 (trạng thái `restoring_small`) kiểm nút thấy được và quảng cáo cách nút ít nhất 24 dp.
    - Trả lời: Đồng ý đề xuất của AI.
14. **`projects_bottom` ở trạng thái trống:** bản đề xuất ghi thanh đáy ở "Danh sách dự án" không phân biệt trạng thái; thiết kế `ProjectsScreen_empty` không có thanh đáy.
    - Đề xuất của AI: theo thiết kế, trạng thái trống không có thanh đáy (đã có `projects_empty_native`); thanh đáy chỉ ở trạng thái có dự án.
    - Trả lời: Đồng ý đề xuất của AI.
15. **Ô quảng cáo trong lưới "sau mỗi 6 dự án" là số cố định hay chỉnh được từ xa?** Quy tắc quảng cáo cho phép một khóa `native_every_<mã vị trí>`, nhưng bản đề xuất không có khóa này.
    - Đề xuất của AI: số cố định 6 ở bản đầu, không thêm khóa. Ở bố cục ba cột của điện thoại gập vẫn sau mỗi 6 dự án.
    - Trả lời: Đồng ý đề xuất của AI.
16. **"Từ dự án thứ 2" của `create_inter` tính thế nào?** Theo số dự án đã từng tạo hay số dự án đang có? Dự án khôi phục, nhân bản có tính không?
    - Đề xuất của AI: tính theo số dự án đã từng tạo bằng màn Tạo dự án (không giảm khi xóa, không tính bài học, khôi phục hay nhân bản): lần bấm "Create" đầu tiên trong đời app không có interstitial, các lần sau theo `ads_interval` và luật trước nét vẽ đầu tiên.
    - Trả lời: Đồng ý đề xuất của AI.
17. **Xác nhận danh sách sự kiện và tham số ở mục 6.** Bản đề xuất mục 9.2 là "đề xuất", chưa có câu trả lời riêng. Đổi tên, nhân bản, xóa dự án không có sự kiện nào trong 9.2. `backup_export`, `backup_restore` dùng chung cho sao lưu, khôi phục một dự án (feature này) và tất cả dự án (spec `settings`); câu này là chỗ trả lời chung cho hai spec (spec settings câu 1 trỏ về đây).
    - Đề xuất của AI: dùng ba sự kiện `project_create`, `backup_export`, `backup_restore` với tham số ở mục 6; không thêm sự kiện cho đổi tên, nhân bản, xóa ở bản đầu. Theo đề xuất, `project_open` do màn vẽ ghi (spec editor câu 5), feature này không ghi. Hai sự kiện sao lưu và khôi phục có `scope` (`all`, `project`) và `project_count`, chỉ ghi khi thành công, vì 9.2 không có sự kiện lỗi.
    - Phương án khác cho anh chọn: ghi cả lần lỗi và lần hủy bằng thêm tham số `result` (`success`, `fail`, `cancel`; `cancel` chỉ có ở khôi phục), có thể thêm `frame_count`. Phương án này đo được tỷ lệ khôi phục thành công (mục 1 của spec này và của spec settings) nhưng ghi cả lúc thất bại, ngoài những gì 9.2 nêu.
    - Trả lời: Chỉ ghi khi thành công, theo đề xuất của AI. Duyệt danh sách sự kiện và tham số ở mục 6. Sau đó (2026-11-21) tham số `scope` đổi thành `source` (`settings`, `projects`).
18. **Cờ `feature_projects_enabled` tắt thì app vào đâu?** Danh sách dự án là màn chính, mọi luồng (màn vẽ, bài học, cài đặt) đi qua nó.
    - Đề xuất của AI: cờ chỉ để giấu feature trong lúc làm; tắt thì app vào màn mẫu có sẵn từ dự án mẫu như hôm nay. Bản staging bật sẵn, bản production bật từ lần phát hành đầu và không tắt sau đó.
    - Trả lời: Đồng ý đề xuất của AI.
19. **"Restore from a file" ở màn Cài đặt** (thiết kế của `settings`) và nút nhập file ở danh sách: cùng loại file, cùng hộp thoại khôi phục không? Sao lưu tất cả dự án có dùng đuôi ".frameloop" không?
    - Đề xuất của AI: hai nút cùng nhận một loại file ".frameloop", chứa một hay nhiều dự án; khôi phục từ Cài đặt quay về danh sách dự án và chạy với cùng hộp thoại (có `restore_mrec`), hộp thoại ghi số dự án khi file có nhiều dự án. Spec `settings` ghi phần của nó.
    - Trả lời: Đồng ý đề xuất của AI.
20. **Ảnh thu nhỏ trên thẻ dự án** lấy từ đâu?
    - Đề xuất của AI: khung đầu tiên của dự án, gộp mọi layer đang hiện, trên nền của dự án; cập nhật khi rời màn vẽ.
    - Trả lời: Đồng ý đề xuất của AI.
21. **Bố cục điện thoại gập cho trạng thái trống, menu, hộp thoại khôi phục và màn Tạo dự án** chưa có trên thiết kế (chỉ có `list_unfolded`).
    - Đề xuất của AI: trạng thái trống và màn Tạo dự án giữ một cột, căn giữa, rộng tối đa bằng bố cục điện thoại; menu và hộp thoại như trên điện thoại. Chỉ `ProjectsScreen_list_unfolded` có screenshot so với ảnh thiết kế.
    - Trả lời: Đồng ý đề xuất của AI.
22. **Chữ "free" trong nút "Try a free lesson" ở màn trống.** Mọi bài học đều miễn phí, nên chữ "free" có thể làm người dùng nghĩ có bài trả phí. Bản đề xuất mục 6 gọi lối vào này là thẻ "Học bài đầu tiên"; thiết kế đã duyệt ghi "Try a free lesson". Spec lessons câu 15 giao quyết định về chữ cho câu này.
    - Đề xuất của AI: giữ chữ theo thiết kế đã duyệt. Nếu muốn tránh hiểu nhầm thì đổi thành "Try a lesson" hay "Start your first lesson", cần sửa thiết kế và bảng chữ. Nút mở màn Bài học theo spec lessons câu 15.
    - Trả lời: Giữ chữ "Try a free lesson".
23. **Lối vào lựa chọn quyền riêng tư quảng cáo khi cờ Cài đặt tắt.** Mọi cờ mặc định tắt khi chưa tải được cấu hình từ xa (ADR-0003); lần mở đầu trên production mà chưa tải được thì người dùng mất dòng "Ad privacy choices", lối vào duy nhất để rút đồng ý quảng cáo (spec settings câu 15).
    - Đề xuất của AI: khi cờ Cài đặt tắt, chỗ nút cài đặt trên thanh trên hiện nút lựa chọn quyền riêng tư quảng cáo có sẵn của phần quảng cáo dùng chung, chỉ ở vùng bắt buộc hộp thoại đồng ý.
    - Trả lời: Nút trên thanh trên, theo đề xuất của AI (anh Nhật chọn trong phiên 2026-11-22).

## 10. Lịch sử

| Ngày | Người | Nội dung |
| --- | --- | --- |
| 2026-11-21 | AI | Tạo bản đầu từ bản đề xuất đã duyệt (docs/research/proposal.md) và thiết kế đã duyệt |
| 2026-11-21 | AI | Ghi câu trả lời của anh Nhật cho mọi câu hỏi ở mục 9 (anh chọn trong phiên ngày 2026-11-21) và sửa phần thân spec theo các câu trả lời; sửa các điểm nên sửa còn lại của lần rà soát cuối. Spec vẫn `draft` |
| 2026-11-21 | AI | Ghi các frame thiết kế bổ sung, sửa theo câu trả lời (chờ anh Nhật duyệt) ở mục 4 và các tiêu chí liên quan |
| 2026-11-21 | AI | Đổi tham số `scope` (`all`, `project`) của `backup_export`, `backup_restore` thành `source` (`settings`, `projects`), theo lời anh Nhật trong phiên; thiết kế bổ sung đã được anh duyệt |
| 2026-11-21 | AI | Chuyển `confirmed` theo lời anh Nhật (người phụ trách sản phẩm) trong phiên: "xác nhận 6 spec" |
| 2026-11-22 | AI | Thêm tiêu chí vào KB-11 và câu hỏi 23: nút lựa chọn quyền riêng tư quảng cáo trên thanh trên khi cờ Cài đặt tắt, theo câu trả lời của anh Nhật trong phiên. Spec `changed`, chờ xác nhận lại |
