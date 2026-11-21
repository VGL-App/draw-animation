---
feature: lessons
status: draft
owner: Nhật
product_owner: Nhật
requests:
  - requests/2026-11-21-ban-dau.md
design: design/README.md
created: 2026-11-21
confirmed:
---

# SPEC: BÀI HỌC

## 1. Mục tiêu

- **Người dùng được gì:** một thư viện 18 bài học chia 4 nhóm. Chạm một bài là vào ngay màn vẽ với một hoạt hình dựng sẵn và chỉ dẫn từng bước, làm xong được hoạt hình đầu tiên trong vài phút.
- **Vì sao làm bây giờ:** người mới dễ bỏ app khi chưa biết vẽ gì. App mẫu ZenByte giữ người mới bằng bài học mở thẳng vào màn vẽ, nên bài học thuộc nhóm tính năng khác biệt của bản đầu (bản đề xuất mục 4, mục 5, Q12).
- **Đo kết quả bằng gì:** tỉ lệ người mới hoàn thành bài đầu tiên trong ngày đầu (bản đề xuất mục 4), cùng số lần bắt đầu và hoàn thành mỗi bài.

## 2. Phạm vi

**Trong phạm vi:**

- Màn Bài học, mở từ thanh điều hướng (mục "Lessons") và từ nút "Try a free lesson" ở màn dự án trống.
- Danh sách 18 bài chia 4 nhóm "Basics", "Motion", "Characters", "Effects" (4 đến 5 bài mỗi nhóm; câu hỏi 1), mỗi nhóm có tiêu đề và số bài; hàng nút lọc theo nhóm ("All" và một nút mỗi nhóm).
- Mỗi thẻ bài có ảnh nhỏ, nhóm và số thứ tự trong nhóm, tên bài, thời lượng ước tính và số khung; bài đã xong có nhãn "Done"; bài đang làm dở có thanh tiến độ.
- Danh sách bài và nội dung bài tải từ máy chủ file của app; có trạng thái đang tải, trạng thái lỗi có nút thử lại.
- Chạm một bài: mở bài thành một dự án dựng sẵn trong màn vẽ. Bài học không hiện trong danh sách dự án và không tính vào số dự án.
- Giao nội dung bài (các bước, khung có sẵn, nét dẫn) cho màn vẽ, nhận lại tiến độ từng bước và lúc bài hoàn thành.
- Màn Hoàn thành bài học: ảnh xem trước, lời chúc mừng, tóm tắt hoạt hình, nút "Next lesson" và "Export my animation".
- Ghi nhớ bài đã xong và tiến độ của bài đang làm trên máy.
- Quảng cáo của màn Bài học, lúc vào bài, màn Hoàn thành bài học và khi bấm "Next lesson", theo mục 5.

**Ngoài phạm vi lần này:**

- Phần chỉ dẫn từng bước hiện trên màn vẽ (khung bước, nét dẫn màu đỏ, nút "Show me", "Next step"): thuộc feature editor. Spec này chỉ nói bài học giao gì cho màn vẽ và nhận lại gì.
- Màn Xuất: thuộc feature export. Spec này chỉ nói nút "Export my animation" mở màn Xuất cho hoạt hình của bài.
- Nút "Try a free lesson" trên màn dự án trống: thuộc feature projects. Spec này chỉ nói nút đó mở đâu.
- Bộ truyện nhiều tập, bản đồ bài học kiểu Duolingo (bản đề xuất Q12, để bản sau).
- Bài học trả phí hay mở bằng quảng cáo thưởng.
- Làm nội dung bài (kịch bản, hình vẽ): việc của nhóm, không phải code (câu hỏi 4).

## 3. Kịch bản sử dụng

App luôn cần mạng. Mất mạng thì app chặn bằng popup mất mạng chung (spec network); feature này không làm chế độ ngoại tuyến và không tự kiểm mạng. Lỗi tải ở các kịch bản dưới đây là lỗi khi máy chủ file không trả lời trong lúc vẫn có mạng.

### KB-1. Xem thư viện bài học

Người dùng mở mục Bài học từ thanh điều hướng.

Tiêu chí chấp nhận:

- [ ] Khi chạm mục "Lessons" trên thanh điều hướng thì màn Bài học hiện, mục "Lessons" trên thanh điều hướng ở trạng thái đang chọn, tiêu đề "Lessons", đúng thiết kế.
- [ ] Khi danh sách đã tải xong thì màn hiện hàng nút lọc ("All" đang chọn, rồi một nút mỗi nhóm theo thứ tự nhóm), rồi từng nhóm: tiêu đề nhóm, số đếm "<số bài đã xong> of <tổng số bài của nhóm>" ở bên phải tiêu đề (câu hỏi 2), và các thẻ bài xếp hai cột.
- [ ] Khi danh sách có từ 15 đến 20 bài thì mọi bài đều có trong danh sách, mỗi bài đúng một lần, theo thứ tự nhóm rồi thứ tự trong nhóm.
- [ ] Khi một thẻ hiện thì thẻ có ảnh nhỏ của bài, dòng nhóm và số thứ tự trong nhóm (ví dụ "Basics · 1"), tên bài (ví dụ "Bouncing ball"), dòng thời lượng ước tính và số khung (ví dụ "5 min · 12 frames").
- [ ] Khi một bài đã hoàn thành thì thẻ của bài có nhãn "Done" kèm dấu tích ở góc trên trái ảnh nhỏ.
- [ ] Khi một bài đang làm dở (đã mở, chưa hoàn thành) thì thẻ của bài có thanh tiến độ dưới dòng thời lượng, phần đã đầy tỉ lệ với số bước đã qua trên tổng số bước (câu hỏi 7).
- [ ] Khi chạm một nút lọc nhóm thì nút đó ở trạng thái đang chọn và danh sách chỉ còn các bài của nhóm đó; chạm "All" thì hiện lại mọi nhóm.
- [ ] Khi danh sách dài hơn màn hình thì danh sách cuộn được, hàng nút lọc cuộn ngang được khi không đủ chỗ, thanh điều hướng luôn ở đáy.
- [ ] Khi bấm nút quay lại của hệ thống ở màn Bài học thì app về màn Danh sách dự án (mục đầu của thanh điều hướng).

Trường hợp đặc biệt:

- [ ] Khi máy để màn hình thấp 360 × 640 thì tiêu đề, hàng nút lọc và ít nhất một hàng thẻ thấy được, danh sách cuộn được, chữ trên thẻ không bị cắt ngang hay chồng nhau.
- [ ] Khi người dùng xoay máy hoặc rời app rồi quay lại thì nút lọc đang chọn và vị trí cuộn giữ nguyên, danh sách không tải lại.

### KB-2. Tải danh sách bài, đang tải và lỗi

Danh sách bài nằm trên máy chủ file của app. Người dùng mở màn Bài học khi danh sách chưa có trên máy, hoặc máy chủ không trả lời.

Tiêu chí chấp nhận:

- [ ] Khi danh sách đang tải thì màn hiện tiêu đề "Lessons", hàng nút lọc và khung xương của hai nhóm, mỗi nhóm hai thẻ, đúng thiết kế trạng thái đang tải; không có thẻ nào chạm được.
- [ ] Khi tải danh sách lỗi, hoặc không có phản hồi sau 15 giây (mức sẵn có của việc tải file), thì màn hiện biểu tượng mây bị gạch, tiêu đề "Lessons didn't load", lời "The lesson server is not responding. Your projects still work." và nút "Try again"; thanh điều hướng vẫn dùng được.
- [ ] Khi bấm "Try again" thì màn về trạng thái đang tải và tải lại danh sách; tải được thì hiện danh sách như KB-1.
- [ ] Khi danh sách đã tải thành công một lần thì các lần mở màn Bài học sau (kể cả sau khi tắt app) dùng bản đã có trên máy, không hiện trạng thái đang tải; app vẫn kiểm bản mới hơn ở nền và cập nhật danh sách khi có (câu hỏi 5).

Trường hợp đặc biệt:

- [ ] Khi máy để màn hình thấp 360 × 640 thì ở trạng thái lỗi, tiêu đề lỗi, lời giải thích và nút "Try again" thấy đủ, không bị thanh điều hướng che.
- [ ] Khi tải lỗi thì danh sách dự án, màn vẽ và các dự án của người dùng vẫn dùng bình thường.

### KB-3. Mở một bài học

Người dùng chạm một thẻ bài.

Tiêu chí chấp nhận:

- [ ] Khi chạm thẻ một bài chưa mở lần nào thì app tải nội dung của bài (nếu chưa có trên máy), rồi mở màn vẽ với dự án dựng sẵn của bài: khung có sẵn, nét dẫn, tốc độ khung và các bước chỉ dẫn của bài, bắt đầu ở bước 1.
- [ ] Khi đang tải nội dung của bài thì người dùng thấy bài đang được mở và không chạm được thẻ khác (hộp thoại nhỏ "Opening lesson…" có vòng chờ, chạm ngoài không đóng, nút quay lại hủy việc mở; câu hỏi 6); tải lỗi hay không có phản hồi sau 15 giây thì app báo lỗi, có nút thử lại, người dùng vẫn ở màn Bài học.
- [ ] Khi bài đã mở trong màn vẽ thì danh sách dự án không có thêm dự án nào và số dự án của người dùng không đổi; điều kiện "từ dự án thứ 2" của quảng cáo khi tạo dự án không tính bài học.
- [ ] Khi bài đã mở trong màn vẽ thì app ghi nhận một lần bắt đầu bài, kèm mã bài.
- [ ] Khi người dùng chưa từng vẽ nét nào ở bất kỳ dự án hay bài học nào thì lúc vào bài không có quảng cáo toàn màn hình (mục 5).

Trường hợp đặc biệt:

- [ ] Khi chạm hai thẻ liên tiếp rất nhanh thì chỉ một bài được mở.
- [ ] Khi người dùng xoay máy trong lúc đang tải nội dung bài thì việc tải không bắt đầu lại và bài vẫn mở khi tải xong.

### KB-4. Làm dở rồi quay lại bài

Người dùng rời màn vẽ khi bài chưa xong, sau đó quay lại.

Tiêu chí chấp nhận:

- [ ] Khi người dùng rời màn vẽ ở bước 3 của một bài 6 bước thì thẻ của bài trên màn Bài học có thanh tiến độ ứng với các bước đã qua.
- [ ] Khi chạm lại thẻ của bài đang làm dở thì màn vẽ mở bài ở bước đang làm, với các nét người dùng đã vẽ, mất tối đa 30 giây công như dự án thường (câu hỏi 8).
- [ ] Khi app bị tắt đột ngột giữa lúc làm bài thì lần mở bài sau có lại các nét đã vẽ, mất tối đa 30 giây công, và ở đúng bước đang làm.

Trường hợp đặc biệt:

- [ ] Khi nội dung bài trên máy chủ đã đổi sang bản mới trong lúc người dùng làm dở thì bài đang làm dở vẫn mở bằng bản cũ đã có trên máy cho tới khi người dùng làm xong hay làm lại (câu hỏi 5).

### KB-5. Hoàn thành bài học

Người dùng làm xong bước cuối của bài trong màn vẽ.

Tiêu chí chấp nhận:

- [ ] Khi người dùng qua bước cuối của bài thì màn Hoàn thành bài học hiện: nút đóng và tiêu đề "Lesson <số> · <tên bài>" (ví dụ "Lesson 1 · Bouncing ball") ở thanh trên, ảnh xem trước hoạt hình người dùng vừa làm, biểu tượng chúc mừng, dòng "Lesson complete!", dòng tóm tắt số khung và thời lượng của hoạt hình kèm tên bài tiếp theo (ví dụ "12 frames · 1.0 s · Next: Squash and stretch"), rồi hai nút ghim ở đáy "Next lesson" và "Export my animation", đúng thiết kế (dòng tóm tắt khác thiết kế vì không khóa bài, câu hỏi 3).
- [ ] Khi màn Hoàn thành bài học hiện thì bài được đánh dấu đã xong; quay về màn Bài học thì thẻ của bài có nhãn "Done", không còn thanh tiến độ, và số đếm ở tiêu đề nhóm cập nhật (câu hỏi 2).
- [ ] Khi bài hoàn thành thì app ghi nhận một lần hoàn thành bài, kèm mã bài, chỉ một lần cho mỗi lần làm xong.
- [ ] Khi bấm nút đóng hay nút quay lại của hệ thống ở màn Hoàn thành bài học thì app về màn Bài học, không quay lại màn vẽ của bài.
- [ ] Khi cờ của feature Xuất tắt thì màn Hoàn thành bài học không có nút "Export my animation" (xem spec export KB-10).

Trường hợp đặc biệt:

- [ ] Khi máy để màn hình thấp 360 × 640 thì phần nội dung (ảnh xem trước, lời chúc mừng, tóm tắt, quảng cáo nếu có) cuộn được, hai nút "Next lesson" và "Export my animation" luôn thấy đủ ở đáy, không bị che hay cắt.
- [ ] Khi người dùng xoay máy ở màn Hoàn thành bài học thì màn giữ nguyên, sự kiện hoàn thành không bị ghi thêm.
- [ ] Khi làm lại một bài đã xong và hoàn thành lần nữa thì bài vẫn mang nhãn "Done" (câu hỏi 8).

### KB-6. Sang bài tiếp

Người dùng bấm "Next lesson" ở màn Hoàn thành bài học.

Tiêu chí chấp nhận:

- [ ] Khi bấm "Next lesson" thì app mở bài tiếp theo trong màn vẽ như KB-3, không qua màn Bài học; bài tiếp theo là bài kế trong cùng nhóm, hết nhóm thì là bài đầu của nhóm sau, kể cả khi bài đó đã xong (câu hỏi 9).
- [ ] Khi bấm "Next lesson" thì có tối đa một quảng cáo toàn màn hình trước khi bài tiếp mở, theo nhịp chung của app (mục 5, câu hỏi 12).
- [ ] Khi bài vừa xong là bài cuối cùng của thư viện thì màn Hoàn thành bài học không có nút "Next lesson" và dòng tóm tắt không nhắc bài tiếp theo (câu hỏi 9).

### KB-7. Xuất hoạt hình của bài

Người dùng bấm "Export my animation" ở màn Hoàn thành bài học.

Tiêu chí chấp nhận:

- [ ] Khi bấm "Export my animation" thì màn Xuất mở với hoạt hình người dùng vừa làm trong bài, các lựa chọn xuất như với dự án thường (spec export).
- [ ] Khi xuất xong và rời màn Xuất thì người dùng về màn Hoàn thành bài học, không về màn vẽ của bài (câu hỏi 10).
- [ ] Khi xuất hoạt hình của bài thì danh sách dự án vẫn không có bài học đó.

### KB-8. Vào bài học từ màn dự án trống

Người mới chưa có dự án nào thấy nút "Try a free lesson" ở màn Danh sách dự án.

Tiêu chí chấp nhận:

- [ ] Khi bấm "Try a free lesson" ở màn dự án trống thì màn Bài học mở như KB-1, mục "Lessons" trên thanh điều hướng ở trạng thái đang chọn (câu hỏi 15).

### KB-9. Quảng cáo ở bài học

Quảng cáo theo mục 5; mọi vị trí mặc định tắt và bật từ xa.

Tiêu chí chấp nhận:

- [ ] Khi vị trí quảng cáo thanh đáy của màn Bài học bật và người dùng đã vẽ nét đầu tiên thì thanh quảng cáo nằm ngay trên thanh điều hướng ở trạng thái danh sách, không che thẻ bài nào (danh sách cuộn được tới thẻ cuối).
- [ ] Khi vị trí ô quảng cáo trong lưới bật và người dùng đã vẽ nét đầu tiên thì danh sách có một ô quảng cáo sau mỗi 4 thẻ bài, ô quảng cáo tách bạch với thẻ bài (câu hỏi 11).
- [ ] Khi vị trí quảng cáo ở màn Hoàn thành bài học bật và người dùng đã vẽ nét đầu tiên thì quảng cáo nằm dưới dòng tóm tắt, cách nút "Next lesson" ít nhất 24 dp ở mọi cỡ chữ.
- [ ] Khi người dùng chưa vẽ nét nào ở bất kỳ dự án hay bài học nào thì màn Bài học, lúc vào bài, màn Hoàn thành bài học và lúc bấm "Next lesson" không có quảng cáo nào.
- [ ] Khi một vị trí tắt từ xa thì chỗ của nó không còn, không để khoảng trống; thẻ bài và nút không đổi chỗ khi quảng cáo nạp xong hay nạp lỗi ở màn Hoàn thành bài học (nút ghim đáy).
- [ ] Khi đang ở trạng thái đang tải hay lỗi của màn Bài học thì không có thanh quảng cáo đáy và ô quảng cáo trong lưới (thiết kế hai trạng thái này không có, câu hỏi 11).

### KB-10. Tắt bài học từ xa

Người phụ trách tắt bài học khi có sự cố (ví dụ nội dung trên máy chủ lỗi).

Tiêu chí chấp nhận:

- [ ] Khi cờ bài học tắt thì thanh điều hướng không có mục "Lessons", màn dự án trống không có nút "Try a free lesson", và không đường nào mở được màn Bài học (câu hỏi 14).
- [ ] Khi cờ tắt lúc người dùng đang ở màn Bài học hay đang làm bài thì người dùng làm tiếp được tới khi rời màn; lần mở sau không còn lối vào (câu hỏi 14).
- [ ] Khi cờ bật lại thì bài đã xong và tiến độ bài dở trước đó vẫn còn.
- [ ] Khi cờ của màn vẽ tắt thì chạm thẻ bài hay bấm "Next lesson" không mở được bài; app hiện thông báo ngắn "coming soon" và ở lại màn đang đứng (spec editor câu 20).

## 4. Giao diện

- **Thiết kế:** [design/README.md](design/README.md), dựng trên Figma theo hướng Studio Night, chế độ tối và sáng. Chữ tiếng Anh theo bảng chữ của thiết kế (bản đề xuất Q9). Tên bài, nhóm, các bước chỉ dẫn là nội dung tải từ máy chủ, không phải chữ cố định của app.
- **Các màn hình:**

| Màn hình | Trạng thái theo thiết kế | Mục đích | Kịch bản liên quan |
| --- | --- | --- | --- |
| `LessonsScreen` | `list` (tối, sáng) | Thư viện bài học chia nhóm, lọc theo nhóm, nhãn đã xong, thanh tiến độ, thanh quảng cáo đáy | KB-1, KB-3, KB-4, KB-8, KB-9 |
| `LessonsScreen` | `loading` (tối, sáng) | Khung xương trong lúc tải danh sách | KB-2 |
| `LessonsScreen` | `error` (tối, sáng) | Báo tải danh sách lỗi, nút "Try again" | KB-2 |
| `LessonDoneScreen` | `default` (tối, sáng) | Chúc mừng, tóm tắt, quảng cáo native medium, "Next lesson", "Export my animation" | KB-5, KB-6, KB-7, KB-9 |
| `EditorScreen` (feature editor) | `lesson-step` | Màn vẽ hiện bài với chỉ dẫn từng bước; bài học giao nội dung, nhận tiến độ | KB-3, KB-4, KB-5 |
| `ProjectsScreen` (feature projects) | `empty` | Nút "Try a free lesson" mở màn Bài học | KB-8 |
| `ExportScreen` (feature export) | `options` | Xuất hoạt hình của bài | KB-7 |

- Thiết kế chưa có: trạng thái đang tải nội dung một bài sau khi chạm thẻ (câu hỏi 6), màn Hoàn thành của bài cuối (câu hỏi 9), bố cục màn gập mở ra (câu hỏi 16).

## 5. Quảng cáo

Chép từ bảng 9.1 của bản đề xuất. Mọi vị trí mặc định tắt, bật bằng khóa Remote Config `is_show_<mã vị trí>`. Nhóm A bật khi phát hành, nhóm B dựng sẵn và bật dần (Q15). Định dạng và hành vi theo `:core:ads` (bộ PARTNER, ADR-0045) và bảng "Luôn giữ" của `docs/ops/ads-guide.md`. Màn Bài học và màn Hoàn thành bài học có vị trí trong nội dung (ô trong lưới, native medium), nên mỗi màn khai các vị trí đó cho lớp quảng cáo nổi không hiện cạnh chúng.

**Màn hình không có quảng cáo:** trạng thái đang tải và lỗi của màn Bài học (thiết kế không có thanh đáy hay ô lưới); chỉ dẫn bài học trong màn vẽ chỉ có dải trên của màn vẽ (`editor_top_native`, thuộc spec editor), không có vị trí riêng của bài học. Đóng bài giữa chừng thì có thể hiện `editor_exit_inter` của màn vẽ trước khi về màn Bài học (spec editor câu 17); vị trí đó thuộc spec editor.

| Vị trí | Định dạng | Màn hình | Khi nào hiển thị | Giới hạn tần suất | Khóa bật tắt |
| --- | --- | --- | --- | --- | --- |
| `lessons_grid` | Native trong lưới | Bài học (`list`) | Một ô sau mỗi 4 thẻ; nhóm A | Theo hành vi ô lưới của `:core:ads` | `is_show_lessons_grid` |
| `lessons_bottom` | Thanh đáy native nhỏ | Bài học (`list`) | Trên thanh điều hướng; nhóm A | Theo hành vi thanh đáy của `:core:ads` | `is_show_lessons_bottom` |
| `lesson_start_inter` | Interstitial | Vào bài học (chạm thẻ bài) | Lúc người dùng chạm thẻ, trước khi màn vẽ mở bài; nhóm A | Theo `ads_interval` (30 giây, Q16) | `is_show_lesson_start_inter` |
| `lesson_done_native` | Native medium | Hoàn thành bài học | Dưới kết quả (dòng tóm tắt), cách nút "Next lesson" ít nhất 24 dp; hai nút ghim đáy; nhóm B | Theo hành vi native medium của `:core:ads` | `is_show_lesson_done_native` |
| `lesson_next_inter` | Interstitial | Bấm "Next lesson" | Lúc bấm, trước khi bài tiếp mở; nhóm B | Theo `ads_interval` (30 giây, Q16) | `is_show_lesson_next_inter` |

- Sau mỗi interstitial của feature, `native_combo` (vị trí toàn app) có thể hiện theo `is_show_native_after_inter`; không phải vị trí của spec này.
- Bấm "Next lesson" mở thẳng bài tiếp, nên chỉ `lesson_next_inter` được gọi, không gọi thêm `lesson_start_inter` cho bài đó (câu hỏi 12).

**Trước nét vẽ đầu tiên (bản đề xuất, đoạn dưới bảng 9.1):** tới khi người dùng vẽ nét đầu tiên ở bất kỳ dự án hay bài học nào, app chỉ hiện `native_onboarding`, `projects_empty_native` trên màn trống và `editor_top_native` khi đã vào màn vẽ. Vì vậy cả năm vị trí trên không hiện trước nét vẽ đầu tiên: không có `lesson_start_inter` lúc vào bài, không có thanh đáy, ô lưới ở màn Bài học, không có native medium hay `lesson_next_inter` ở màn Hoàn thành. Mốc "nét vẽ đầu tiên" app nhớ trên máy, dùng chung với các feature khác.

**Không đặt quảng cáo ở** (bản đề xuất 9.1): canvas, dòng thời gian, các bảng công cụ của màn vẽ, kể cả khi làm bài; không làm quảng cáo giống nút của app hay sát nút.

## 6. Dữ liệu và phân tích

**Dữ liệu lưu trên máy:** (chỉ trên máy, không tài khoản, bản đề xuất 9.4)

| Dữ liệu | Mục đích | Giữ trong bao lâu |
| --- | --- | --- |
| Danh sách bài và nội dung các bài đã tải | Mở lại màn Bài học và bài không phải tải lại | Đến khi có bản mới trên máy chủ, hoặc người dùng xóa dữ liệu app |
| Bài đã hoàn thành (mã bài) | Hiện nhãn "Done", số đếm của nhóm | Đến khi người dùng xóa dữ liệu app |
| Tiến độ bài đang làm (mã bài, bước đang làm) | Thanh tiến độ trên thẻ, mở lại đúng bước | Đến khi bài hoàn thành hay người dùng làm lại, hoặc xóa dữ liệu app |
| Hoạt hình người dùng vẽ trong bài | Mở lại bài dở, xem trước ở màn Hoàn thành, xuất | Đến khi người dùng làm lại bài, hoặc xóa dữ liệu app (câu hỏi 8) |
| Mốc "đã vẽ nét đầu tiên" (dùng chung, không riêng feature này) | Luật quảng cáo trước nét vẽ đầu tiên | Đến khi người dùng xóa dữ liệu app |

**Sự kiện phân tích:** (từ danh sách đề xuất ở bản đề xuất 9.2; danh sách và tham số đã được anh Nhật duyệt ở câu hỏi 13)

| Tên sự kiện | Khi nào ghi | Tham số | Dùng để trả lời câu hỏi gì |
| --- | --- | --- | --- |
| `lesson_start` | Khi bài đã mở trong màn vẽ (KB-3, KB-6) | Đề xuất: `lesson_id`, `group`, `resumed` (mở lại bài dở hay bắt đầu mới) | Bài nào được mở nhiều, người dùng vào bài từ đâu |
| `lesson_complete` | Khi màn Hoàn thành bài học hiện (KB-5), một lần mỗi lần làm xong | Đề xuất: `lesson_id`, `group`, `duration_ms` (thời gian từ lần mở bài đầu tiên tới khi xong) | Tỉ lệ người mới hoàn thành bài đầu tiên trong ngày đầu; bài nào hay bị bỏ dở |

Không ghi nội dung tranh hay chữ người dùng nhập (bản đề xuất 9.2).

**Quyền của app và dịch vụ bên ngoài:** Không có quyền mới. Feature chỉ tải file từ máy chủ file của app (R2) qua lối tải file chung. Xuất hoạt hình của bài dùng quyền của feature export.

## 7. File tĩnh trên R2

Nội dung bài do nhóm tự làm (bản đề xuất mục 11: AI soạn kịch bản, người vẽ hoặc duyệt). Đường dẫn có phiên bản; nội dung đổi thì dùng đường dẫn mới (ADR-0015). Cách chia file và con số dưới đây theo câu hỏi 4, 5.

| Nhóm file | Dung lượng ước tính | Khi nào tải | Khi không tải được |
| --- | --- | --- | --- |
| Danh sách bài (nhóm, thứ tự, tên, thời lượng, số khung, số bước, đường dẫn nội dung từng bài), ví dụ `lessons/v1/index.json` | Vài KB | Lần đầu mở màn Bài học; lần sau dùng bản trên máy, kiểm bản mới ở nền | Trạng thái lỗi, "Try again" (KB-2) |
| Ảnh nhỏ của từng bài, ví dụ `lessons/v1/<mã bài>/thumb.webp` | Khoảng 10 đến 30 KB mỗi ảnh, 15 đến 20 ảnh | Cùng lúc hiện danh sách | Thẻ hiện nền trống, vẫn chạm được |
| Gói nội dung từng bài (khung dựng sẵn, nét dẫn, chữ của các bước, tốc độ khung, đoạn mẫu cho nút "Show me" theo spec editor câu 16), ví dụ `lessons/v1/<mã bài>/lesson.zip` | Đề xuất tối đa 2 MB mỗi bài, cả thư viện dưới 40 MB | Khi chạm thẻ bài lần đầu; lần sau dùng bản trên máy | Báo lỗi, cho thử lại, ở lại màn Bài học (KB-3) |

R2 staging của app chưa có, tạm dùng tên miền staging ghi ở bản đề xuất mục 1. Thiếu file bắt buộc thì ghi vào `docs/ops/required-assets.txt` khi có nội dung (câu hỏi 4).

## 8. Cờ tính năng

| Tên cờ | Mặc định | Ý nghĩa |
| --- | --- | --- |
| `feature_lessons_enabled` | tắt | Bật để hiện mục "Lessons" trên thanh điều hướng, nút "Try a free lesson" ở màn dự án trống và màn Bài học (KB-10). Bản staging bật sẵn khi có nội dung bài mẫu |

Khóa Remote Config khác (không phải cờ): `lessons_content_version`, thư mục nội dung bài hiện hành trên R2 (ví dụ `v1` cho `lessons/v1/`), khai báo theo `docs/ops/remote-config.md` (câu hỏi 5, anh Nhật đồng ý thêm ngày 2026-11-21).

## 9. Câu hỏi còn mở

1. **Danh sách bài, số bài mỗi nhóm và tên nhóm:** bản đề xuất Q12 chốt 15 đến 20 bài, nhóm "nền tảng, nguyên lý, nhân vật, hiệu ứng...". Thiết kế có bốn nhóm "Basics, Motion, Characters, Effects" ("Motion" thay cho "nguyên lý") và tiêu đề nhóm ghi "2 of 6", tức 6 bài mỗi nhóm, 4 nhóm là 24 bài, vượt mức 20.
   - Đề xuất của AI: 4 nhóm theo thiết kế (Basics, Motion, Characters, Effects), 4 đến 5 bài mỗi nhóm, tổng 18 bài; danh sách tên bài do nhóm chốt cùng kịch bản (câu hỏi 4). Ví dụ trên thiết kế (Bouncing ball, Squash and stretch, Stick figure walk, Blink and look) làm 4 bài đầu.
   - Trả lời: 4 nhóm, 18 bài, theo đề xuất của AI.
2. **Số đếm ở tiêu đề nhóm ("2 of 6") nghĩa là gì?** Trên thiết kế nhóm Basics có 1 bài "Done" và 1 bài đang dở mà ghi "2 of 6"; nhóm Motion chưa có bài nào xong cũng ghi "2 of 6".
   - Đề xuất của AI: "<số bài đã xong> of <tổng số bài của nhóm>"; ảnh thiết kế chỉ là số mẫu. Screenshot test cùng tên ảnh thiết kế dùng dữ liệu cho ra đúng chữ trên ảnh.
   - Trả lời: Đồng ý đề xuất của AI.
3. **Có khóa bài theo thứ tự không?** Màn Hoàn thành ghi "Squash and stretch unlocked next" (mở khóa bài tiếp), nhưng thẻ bài không có biểu tượng khóa và bản đề xuất không nói khóa bài.
   - Đề xuất của AI: không khóa, mọi bài mở được ngay; đổi dòng tóm tắt thành "12 frames · 1.0 s · Next: Squash and stretch" (cần sửa thiết kế và bảng chữ).
   - Trả lời: Không khóa bài, theo đề xuất của AI.
4. **Ai làm nội dung bài và khi nào có?** Bản đề xuất mục 11: AI soạn kịch bản, người vẽ hoặc duyệt. Chưa rõ người vẽ, lịch, và bài nào có trước để thử trên staging.
   - Đề xuất của AI: AI soạn kịch bản cả thư viện thành một tài liệu trong `docs/features/lessons/` để anh Nhật duyệt; một người trong nhóm vẽ khung và nét dẫn bằng chính app (khi màn vẽ đủ dùng) hoặc công cụ vẽ khác; có 2 bài mẫu ("Bouncing ball", "Squash and stretch") trên R2 staging trước khi làm các ticket mở bài, phần còn lại trước khi phát hành.
   - Trả lời: Đồng ý đề xuất của AI.
5. **Dạng nội dung, dung lượng và cách đánh phiên bản trên R2:**
   - Đề xuất của AI: một file danh sách và mỗi bài một gói như bảng mục 7, cả thư viện trong một thư mục có phiên bản (`lessons/v1/`); sửa một bài thì đưa cả thư viện lên thư mục mới (`lessons/v2/`) và app đọc thư mục hiện hành từ một khóa Remote Config (ví dụ `lessons_content_version`, khai theo `docs/ops/remote-config.md`). Mỗi gói dưới 2 MB, cả thư viện dưới 40 MB. Bài đang làm dở giữ bản cũ tới khi xong hay làm lại (KB-4). Chữ của bài (tên, các bước) chỉ tiếng Anh theo Q9, nằm trong nội dung trên R2, không vào `strings.xml`. Khóa Remote Config này là khóa mới, cần anh Nhật đồng ý.
   - Trả lời: Đồng ý, thêm khóa `lessons_content_version`.
6. **Sau khi chạm thẻ, lúc đang tải nội dung bài thì hiện gì?** Thiết kế không có trạng thái này.
   - Đề xuất của AI: hộp thoại nhỏ "Opening lesson…" có vòng chờ, không đóng được bằng chạm ngoài, nút quay lại hủy việc mở; tải lỗi thì hộp thoại đổi sang lời báo lỗi có "Try again" và "Cancel". Cần thêm vào thiết kế trước khi làm ticket.
   - Trả lời: Đồng ý đề xuất của AI.
7. **Thanh tiến độ trên thẻ tính theo gì?** Thiết kế vẽ thanh đầy hết chiều rộng trên thẻ "Squash and stretch" dù bài chưa xong.
   - Đề xuất của AI: tỉ lệ bước đã qua trên tổng số bước (ví dụ bước 3 của 6 thì đầy một nửa); ảnh thiết kế là số mẫu.
   - Trả lời: Đồng ý đề xuất của AI.
8. **Chạm lại bài đang dở hay bài đã xong thì sao?**
   - Đề xuất của AI: bài dở mở lại đúng bước và các nét đã vẽ (KB-4). Bài đã xong mở lại từ bước 1 với khung dựng sẵn ban đầu (làm lại), nhãn "Done" giữ nguyên; hoạt hình lần trước không giữ. Không có nút "làm lại" riêng cho bài dở ở bản đầu.
   - Trả lời: Đồng ý đề xuất của AI.
9. **"Next lesson" là bài nào, bài cuối thì sao, và số trong "Lesson 1" đếm thế nào?** Thẻ ghi "Basics · 1" (số trong nhóm), màn Hoàn thành ghi "Lesson 1" (có thể là số trong cả thư viện).
   - Đề xuất của AI: bài tiếp là bài kế theo thứ tự thư viện (hết nhóm thì sang bài đầu nhóm sau), kể cả khi bài đó đã xong; bài cuối không có nút "Next lesson", dòng tóm tắt bỏ phần bài tiếp. "Lesson <số>" là số thứ tự trong cả thư viện. Thiết kế cần thêm ảnh màn Hoàn thành của bài cuối.
   - Trả lời: Đồng ý đề xuất của AI.
10. **Nút "Export my animation" ở màn Hoàn thành:** thiết kế có, luồng Bài học của bản đề xuất (mục 6) không nhắc. Xuất xong thì về đâu?
    - Đề xuất của AI: giữ nút theo thiết kế; mở màn Xuất cho hoạt hình của bài; rời màn Xuất thì về màn Hoàn thành bài học. Quảng cáo của màn Xuất theo spec export.
    - Trả lời: Đồng ý đề xuất của AI.
11. **Ô quảng cáo "sau mỗi 4 thẻ" đếm thế nào, và thanh đáy có ở trạng thái đang tải, lỗi không?** Danh sách chia nhóm có tiêu đề, và lọc theo nhóm làm số thẻ đổi. Thiết kế không vẽ ô lưới, và hai trạng thái đang tải, lỗi không có thanh đáy.
    - Đề xuất của AI: đếm theo thứ tự thẻ trong danh sách đang hiện, bỏ qua tiêu đề nhóm (ô chiếm một ô của lưới hai cột sau thẻ thứ 4, 8, 12...); khi lọc thì đếm lại trên danh sách đã lọc; nhóm ít hơn 4 thẻ không có ô. Thanh đáy và ô lưới chỉ ở trạng thái danh sách.
    - Trả lời: Đồng ý đề xuất của AI.
12. **Một lần bấm "Next lesson" có thể gặp hai interstitial không?** `lesson_next_inter` và `lesson_start_inter` cùng có thể chạy khi bài tiếp mở. Và mở lại một bài dở có tính là "vào bài" không?
    - Đề xuất của AI: "Next lesson" chỉ gọi `lesson_next_inter`, không gọi `lesson_start_inter`; chạm thẻ bài từ màn Bài học (bài mới, bài dở hay bài đã xong) đều gọi `lesson_start_inter`. `ads_interval` 30 giây vẫn áp cho cả hai.
    - Trả lời: Đồng ý đề xuất của AI.
13. **Xác nhận sự kiện `lesson_start`, `lesson_complete` và tham số:** bản đề xuất 9.2 chỉ là danh sách đề xuất.
    - Đề xuất của AI: hai sự kiện, tham số như bảng mục 6 (`lesson_id`, `group`, `resumed`; `lesson_id`, `group`, `duration_ms`). Không thêm sự kiện mở màn Bài học hay lỗi tải; nếu cần đo lỗi tải thì anh Nhật cho thêm.
    - Trả lời: Duyệt như đề xuất.
14. **Khi cờ `feature_lessons_enabled` tắt thì lối vào hiện thế nào?**
    - Đề xuất của AI: ẩn mục "Lessons" trên thanh điều hướng (thanh còn một mục thì ẩn cả thanh) và ẩn nút "Try a free lesson" ở màn dự án trống (cần ghi ở spec projects); người đang ở trong bài làm tiếp được tới khi rời màn.
    - Trả lời: Đồng ý đề xuất của AI.
15. **Lối vào từ màn dự án trống mở danh sách hay mở thẳng bài đầu?** Bản đề xuất mục 6 gọi là thẻ "Học bài đầu tiên"; thiết kế (projects) là nút "Try a free lesson" mở màn Bài học. Chữ "free" có thể làm người dùng nghĩ có bài trả phí, trong khi mọi bài đều miễn phí.
    - Đề xuất của AI: theo thiết kế, mở màn Bài học. Quyết định về chữ "free" trả lời ở câu 22 của spec projects.
    - Trả lời: Theo thiết kế, mở màn Bài học; giữ chữ "Try a free lesson" (spec projects câu 22).
16. **Màn gập mở ra:** bản đề xuất mục 7 chỉ dựng frame `_unfolded` cho Màn vẽ và Danh sách dự án; thiết kế bài học không có. Thiết kế ghi thanh điều hướng thành thanh bên khi màn rộng.
    - Đề xuất của AI: ở màn rộng, lưới bài học giãn thành 3 cột, màn Hoàn thành giữ nội dung ở giữa với bề rộng tối đa như khung 411 dp; không thêm ảnh thiết kế, có một screenshot test ở khung `w673dp-h841dp` cho mỗi màn.
    - Trả lời: Đồng ý đề xuất của AI.

## 10. Lịch sử

| Ngày | Người | Nội dung |
| --- | --- | --- |
| 2026-11-21 | AI | Tạo bản đầu từ bản đề xuất đã duyệt (docs/research/proposal.md) và thiết kế đã duyệt |
| 2026-11-21 | AI | Ghi câu trả lời của anh Nhật cho mọi câu hỏi ở mục 9 (anh chọn trong phiên ngày 2026-11-21) và sửa phần thân spec theo các câu trả lời; sửa các điểm nên sửa còn lại của lần rà soát cuối. Spec vẫn `draft` |
