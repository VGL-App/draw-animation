---
feature: onboarding
status: confirmed
owner: Nhật
product_owner: Nhật
requests:
  - requests/2026-11-21-ban-dau.md
design: design/README.md
created: 2026-11-21
confirmed: 2026-11-21
---

# SPEC: ONBOARDING

## 1. Mục tiêu

- **Người dùng được gì:** người dùng mới biết app làm được gì qua 4 trang ngắn (vẽ từng khung, xem khung trước và sau, thêm âm thanh và xuất video, không mất bài), rồi vào thẳng danh sách dự án để bắt đầu vẽ.
- **Vì sao làm bây giờ:** anh Nhật quyết định thêm 4 trang giới thiệu bắt buộc ở lần mở đầu, có một thẻ quảng cáo dưới nội dung. Đây là màn đầu tiên người dùng mới thấy sau Loading, nên phải xong trước các màn khác của luồng mở đầu.
- **Đo kết quả bằng gì:** từ lần mở đầu tới nét vẽ đầu tiên không quá 7 thao tác: 4 lần bấm nút qua 4 trang, "New animation", "Create", nét vẽ. Không tính chạm ở hộp thoại đồng ý quảng cáo và chạm bỏ qua hướng dẫn trên màn vẽ. Phần của feature này là đúng 4 thao tác.

## 2. Phạm vi

**Trong phạm vi:**

- 4 trang giới thiệu, hiện sau Loading và sau hộp thoại đồng ý quảng cáo (khi hộp thoại có hiện), cho tới khi người dùng bấm nút ở trang cuối.
- Mỗi trang có: số trang ("1 / 4" tới "4 / 4") ở trên, hình minh họa, tiêu đề, một câu mô tả, dãy chấm cho biết đang ở trang nào, thẻ quảng cáo dưới nội dung, nút ở đáy. Trang 1 tới 3 có nút "Next", trang 4 có nút "Start drawing". Chữ theo bảng chữ của thiết kế (tiếng Anh).
- Bắt buộc qua đủ 4 trang. Không có nút bỏ qua.
- Nút Back của hệ thống về trang trước; ở trang 1 thì thoát app.
- Vuốt ngang để đổi trang: vuốt sang trái tới trang sau, vuốt sang phải về trang trước (câu hỏi 1).
- Chỉ đánh dấu đã xong khi bấm "Start drawing". Xong rồi thì không bao giờ hiện lại, app mở thẳng vào danh sách dự án.
- Chưa xong mà app bị đóng thì lần mở sau hiện lại từ trang 1.
- Thẻ quảng cáo chỉ ở lần mở đầu sau khi cài, đổi quảng cáo mỗi trang. Nút ở đáy đứng cùng chỗ ở mọi trang, cách thẻ ít nhất 24 dp ở mọi cỡ chữ. Sang trang không chờ quảng cáo.
- Chế độ sáng và tối.
- Tắt được cả feature từ xa: khi tắt, app mở thẳng vào danh sách dự án.

**Ngoài phạm vi lần này:**

- Nút bỏ qua, hỏi tuổi, màn trả phí.
- Xem lại onboarding sau khi đã xong (ví dụ từ Cài đặt).
- Hướng dẫn trên màn vẽ: thuộc feature editor.
- Danh sách dự án trống và nút "New animation": thuộc feature projects.
- Hộp thoại đồng ý quảng cáo và quảng cáo khi quay lại app: do vỏ app và feature ads lo.
- Chế độ không có mạng: app luôn cần mạng; mất mạng do app chặn bằng popup chung, feature không tự xử lý.

## 3. Kịch bản sử dụng

### KB-1. Lần mở đầu: đi qua 4 trang và vào danh sách dự án

Người dùng vừa cài app, mở lần đầu khi có mạng.

Tiêu chí chấp nhận:

- [ ] Khi Loading xong và hộp thoại đồng ý quảng cáo đã đóng (hoặc không hiện) thì trang 1 hiện với "1 / 4", tiêu đề "Draw frame by frame", câu mô tả, chấm thứ nhất nổi bật, nút "Next", đúng thiết kế.
- [ ] Khi bấm "Next" ở trang 1, 2, 3 thì app sang đúng trang kế tiếp: số trang, hình minh họa, tiêu đề, câu mô tả và chấm nổi bật đổi theo bảng chữ của thiết kế.
- [ ] Khi đang ở trang 4 thì nút đổi thành "Start drawing", có biểu tượng cây cọ; trang 1 tới 3 nút "Next" có biểu tượng mũi tên.
- [ ] Khi bấm "Start drawing" thì app nhớ đã xong onboarding và mở danh sách dự án trống.
- [ ] Khi đã vào danh sách dự án và bấm Back thì app thoát, không quay lại onboarding.
- [ ] Khi đi từ lần mở đầu tới nét vẽ đầu tiên theo đường ngắn nhất thì đếm được đúng 4 thao tác trong onboarding và tổng không quá 7 thao tác (không tính hộp thoại đồng ý quảng cáo và chạm bỏ qua hướng dẫn trên màn vẽ).
- [ ] Khi đang ở bất kỳ trang nào thì không có nút hay cử chỉ nào bỏ qua các trang còn lại.

Trường hợp đặc biệt:

- [ ] Khi bấm "Next" liên tiếp thật nhanh thì mỗi lần bấm sang đúng một trang, không trang nào bị bỏ qua; bấm "Start drawing" hai lần thì danh sách dự án chỉ mở một lần.
- [ ] Khi xoay máy hay gập, mở máy gập ở một trang thì vẫn ở đúng trang đó.
- [ ] Khi đưa app ra nền rồi quay lại mà app chưa bị đóng thì vẫn ở đúng trang đó. Quảng cáo khi quay lại app có thể hiện như mọi màn (feature ads lo).
- [ ] Khi không có mạng thì app hiện popup mất mạng chung; feature không có màn hay thông báo riêng cho việc mất mạng.
- [ ] Khi app không nhớ được việc đã xong (ghi lên máy bị lỗi) thì app vẫn mở danh sách dự án, không dừng đột ngột, không báo lỗi cho người dùng, ghi log lỗi; lần mở sau có thể hiện lại onboarding (câu hỏi 9).

### KB-2. Quay lại trang trước và thoát app

Người dùng đang ở giữa onboarding, muốn xem lại trang trước hoặc thoát.

Tiêu chí chấp nhận:

- [ ] Khi bấm Back ở trang 2, 3 hay 4 thì app về trang ngay trước đó, số trang và chấm nổi bật đổi theo.
- [ ] Khi bấm Back ở trang 1 thì app thoát, không đánh dấu đã xong.
- [ ] Khi đã về trang trước rồi bấm "Next" thì app sang lại trang sau như bình thường.
- [ ] Khi vuốt sang trái ở trang 1, 2, 3 thì app sang trang kế tiếp như bấm "Next"; khi vuốt sang phải ở trang 2, 3, 4 thì app về trang trước như bấm Back.
- [ ] Khi vuốt sang trái ở trang 4 thì không có gì xảy ra; chỉ bấm "Start drawing" mới xong onboarding.
- [ ] Khi đang vuốt dở giữa hai trang thì thẻ quảng cáo chưa đổi; quảng cáo của trang mới hiện khi trang đã dừng hẳn.

### KB-3. Chưa xong mà app bị đóng, mở lại

Người dùng đóng app khi chưa bấm "Start drawing" (vuốt bỏ app, bấm Back ở trang 1, hay hệ thống đóng app khi ở nền).

Tiêu chí chấp nhận:

- [ ] Khi mở lại app sau khi app đã bị đóng hẳn thì sau Loading, onboarding hiện lại từ trang 1, dù lần trước đã tới trang mấy.
- [ ] Khi onboarding hiện ở lần mở thứ hai trở đi thì không có thẻ quảng cáo nào trên cả 4 trang; nút ở đáy vẫn ở đúng chỗ như lần đầu.
- [ ] Khi bấm "Start drawing" ở lần mở sau thì app nhớ đã xong và mở danh sách dự án, như KB-1.

Trường hợp đặc biệt:

- [ ] Khi bấm Back ở trang 1 rồi mở lại app ngay, lúc hệ thống chưa đóng hẳn app, thì onboarding hiện lại từ trang 1 và thẻ quảng cáo vẫn hiện, vì lần mở đầu kéo dài tới khi hệ thống đóng hẳn app (câu hỏi 2).

### KB-4. Thẻ quảng cáo trên các trang

Người dùng mới, lần mở đầu, vị trí quảng cáo của onboarding đang bật.

Tiêu chí chấp nhận:

- [ ] Khi trang đang hiện và đã có quảng cáo thì thẻ quảng cáo nằm dưới nội dung, trên nút ở đáy, cách nút ít nhất 24 dp, đúng vị trí của thiết kế; thẻ có nhãn "Ad" và nút của quảng cáo viền xanh dương, khác nút xanh chanh của app.
- [ ] Khi đổi sang trang khác (tới hay lui) thì thẻ hiện một quảng cáo khác với quảng cáo của trang vừa rời.
- [ ] Khi bấm "Next" mà quảng cáo của trang sau chưa có thì app sang trang ngay, không chờ; quảng cáo hiện khi nạp xong.
- [ ] Khi đổi trang thì nút ở đáy đứng yên cùng một chỗ trên màn hình ở cả 4 trang, có hay không có quảng cáo, đang chờ hay đã có quảng cáo.
- [ ] Khi cỡ chữ của máy lớn nhất thì thẻ có thể cao thêm, nhưng nút vẫn cách thẻ ít nhất 24 dp, không bị thẻ che hay đẩy ra khỏi màn hình.
- [ ] Khi người dùng không đồng ý quảng cáo, vị trí bị tắt từ xa, hay không phải lần mở đầu thì không có thẻ quảng cáo; không có khung giữ chỗ, nội dung trang dùng phần chỗ đó, nút vẫn ghim đáy cùng chỗ. Khi vị trí được phép mà quảng cáo đang nạp hay nạp lỗi thì khung cao bằng thẻ vẫn giữ chỗ và hiện khung chờ (câu hỏi 3).
- [ ] Khi ở onboarding thì không có quảng cáo nào khác (không thanh quảng cáo ở đáy, không quảng cáo toàn màn hình khi bấm nút).

### KB-5. Đã xong onboarding, mở lại app

Người dùng đã bấm "Start drawing" ở một lần trước.

Tiêu chí chấp nhận:

- [ ] Khi mở lại app thì sau Loading, app mở thẳng danh sách dự án, không hiện onboarding, ở mọi lần mở sau, kể cả khi người dùng chưa vẽ nét nào.
- [ ] Khi người dùng xóa dữ liệu app hay cài lại thì onboarding hiện lại như lần mở đầu.

### KB-6. Hiển thị ở mọi cỡ màn hình và cỡ chữ

Tiêu chí chấp nhận:

- [ ] Khi máy ở chế độ tối thì 4 trang theo bản tối của thiết kế; chế độ sáng theo bản sáng.
- [ ] Khi màn hình thấp 360 × 640 dp thì tiêu đề, câu mô tả và nút đáy không bị cắt hay chồng lên nhau, nút đáy không bị che; nội dung không vừa thì đọc được hết bằng cách cuộn (hình minh họa co lại giữ tỷ lệ, thấp nhất 96 dp; câu hỏi 7).
- [ ] Khi cỡ chữ của máy lớn nhất thì tiêu đề và câu mô tả xuống dòng, không bị cắt bằng dấu ba chấm; chữ "Start drawing" trên nút không bị cắt.
- [ ] Khi đo vùng chạm thì nút ở đáy cao ít nhất 48 dp.
- [ ] Khi máy gập mở ra thì bố cục một cột, căn giữa, nội dung, thẻ và nút rộng tối đa 411 dp, nút vẫn ghim đáy (câu hỏi 8).

### KB-7. Tắt feature từ xa

Người phụ trách tắt onboarding khi có sự cố.

Tiêu chí chấp nhận:

- [ ] Khi cờ của feature tắt thì sau Loading app mở thẳng danh sách dự án, không hiện onboarding và không ghi là đã xong.
- [ ] Khi cờ bật lại thì người dùng chưa xong sẽ thấy onboarding ở lần mở sau, từ trang 1 (kể cả người dùng đã có dự án), không có quảng cáo vì không phải lần mở đầu (câu hỏi 6).
- [ ] Khi cờ của danh sách dự án tắt thì sau "Start drawing" (hay khi cờ onboarding tắt) app mở vào màn thay cho danh sách dự án theo spec projects câu 18: màn mẫu có sẵn từ dự án mẫu (màn mà app mới tạo từ dự án mẫu mở ra, trước khi có các feature của Frameloop).

## 4. Giao diện

- **Thiết kế:** [design/README.md](design/README.md), Figma, đã duyệt 2026-11-20. 4 trang, mỗi trang bản sáng và tối. Trạng thái không có quảng cáo có frame `page-1-no-ad` (bổ sung ngày 2026-11-21, anh Nhật duyệt ngày 2026-11-21 (chọn "Duyệt" trong phiên); các trang khác theo cùng cách). Thiết kế không có khung thấp 360 × 640 hay khung máy gập mở (câu hỏi 7, 8).
- **Các màn hình:**

| Màn hình | Trạng thái theo thiết kế | Mục đích | Kịch bản liên quan |
| --- | --- | --- | --- |
| `OnboardingScreen` | page-1, sáng và tối | Trang 1 "Draw frame by frame", nút "Next" | KB-1, KB-2, KB-3, KB-4, KB-6 |
| `OnboardingScreen` | page-2, sáng và tối | Trang 2 "See the frame before and after", nút "Next" | KB-1, KB-2, KB-4, KB-6 |
| `OnboardingScreen` | page-3, sáng và tối | Trang 3 "Add sound, share a video", nút "Next" | KB-1, KB-2, KB-4, KB-6 |
| `OnboardingScreen` | page-4, sáng và tối | Trang 4 "Never lose your work", nút "Start drawing" | KB-1, KB-2, KB-3, KB-4, KB-6 |
| `OnboardingScreen` | page-1-no-ad, sáng và tối | Trang 1 khi không có thẻ quảng cáo; nút vẫn ghim đáy | KB-4 |
| Danh sách dự án (feature projects) | Trống | Đích sau khi bấm "Start drawing", và khi đã xong hay cờ tắt | KB-1, KB-5, KB-7 |

Số liệu chính của thiết kế (khung 411 × 891 dp): thanh trên cao 48 dp có số trang; hình minh họa 300 × 190 dp; thẻ quảng cáo cao 250 dp, lề hai bên 16 dp; nút cao 48 dp, rộng 379 dp, lề hai bên 16 dp; mép dưới thẻ cách mép trên nút 24 dp. Hình minh họa vẽ trong app, không tải về.

## 5. Quảng cáo

**Màn hình không có quảng cáo (nếu spec muốn giữ):** không có vị trí nào khác trên onboarding ngoài `native_onboarding`: không thanh đáy, không lớp nổi, không interstitial khi bấm "Next" hay "Start drawing". `open_app` tắt ở lần mở đầu; `resume`, `resume_inter` vẫn hiện khi người dùng rời app rồi quay lại (Q4, do feature ads lo).

| Vị trí | Định dạng | Màn hình | Khi nào hiển thị | Giới hạn tần suất | Khóa bật tắt |
| --- | --- | --- | --- | --- | --- |
| `native_onboarding` | Native ở bước của luồng (`native_step`, thẻ 250 dp đặt ngoài vùng trang, `NativeStepAd`) | Onboarding, cả 4 trang | Chỉ lần mở đầu sau khi cài (`firstOpenOnly`); đổi quảng cáo mỗi trang; khung cao theo `NativeStepAdDefaults.height()` (khoảng 250 dp, cao thêm khi chữ lớn); nút Next ghim cố định, đứng cùng chỗ ở mọi trang, cách thẻ ít nhất 24 dp ở mọi cỡ chữ; sang trang không chờ quảng cáo (quảng cáo của trang sau hiện khi nạp xong). Nhóm A (bật khi phát hành) | Một quảng cáo mỗi lần đổi trang, hai trang kề nhau không cùng quảng cáo; từ lần mở thứ hai không nạp, không hiện | `is_show_native_onboarding` |

Ghi chú:

- **Trước nét vẽ đầu tiên** (bản đề xuất, dưới bảng 9.1; app tự làm, `:core:ads` không có luật này): tới khi người dùng vẽ nét đầu tiên ở bất kỳ dự án hay bài học nào, app chỉ hiện `native_onboarding`, `projects_empty_native` trên màn trống và `editor_top_native` khi đã vào màn vẽ, không hiện vị trí quảng cáo nào khác. Onboarding nằm trong khoảng này nên chỉ có `native_onboarding`.
- Màn onboarding khai `native_onboarding` là quảng cáo trong nội dung (MC-13), nên không có lớp nổi.
- Thẻ do `:core:ads` vẽ theo bố cục `native_step`; bố cục thẻ trên thiết kế khác và chỉ là khung giữ chỗ (câu hỏi 4).

## 6. Dữ liệu và phân tích

**Dữ liệu lưu trên máy:**

| Dữ liệu | Mục đích | Giữ trong bao lâu |
| --- | --- | --- |
| Đã xong onboarding (có hay không) | Biết lần mở sau có hiện onboarding không; chỉ ghi khi bấm "Start drawing" | Đến khi người dùng xóa dữ liệu app hay gỡ app |

Trang đang xem không lưu trên máy: app bị đóng thì lần sau luôn bắt đầu từ trang 1. Dấu "lần mở đầu" dùng cho quảng cáo là của `:core:ads`, feature không lưu thêm. Không có tài khoản, không gửi dữ liệu này ra ngoài (bản đề xuất 9.4).

**Sự kiện phân tích:**

| Tên sự kiện | Khi nào ghi | Tham số | Dùng để trả lời câu hỏi gì |
| --- | --- | --- | --- |
| `onboarding_page_view` | Mỗi lần một trang hiện (kể cả khi quay lại trang trước) | `page`: 1 tới 4 | Người dùng bỏ app ở trang nào |
| `onboarding_complete` | Khi bấm "Start drawing" | Không có | Tỷ lệ người dùng đi hết onboarding |

Hai sự kiện này anh Nhật thêm ngày 2026-11-21 (câu hỏi 5); bản đề xuất mục 9.2 đã cập nhật theo.

**Quyền của app và dịch vụ bên ngoài:** Không có. Feature không cần quyền nào và không thêm SDK; quảng cáo đi qua `:core:ads`.

## 7. File tĩnh trên R2

Không có. Hình minh họa của 4 trang vẽ trong app theo số liệu thiết kế.

## 8. Cờ tính năng

| Tên cờ | Mặc định | Ý nghĩa |
| --- | --- | --- |
| `feature_onboarding_enabled` | tắt | Bật để hiện onboarding cho người dùng chưa xong. Tắt thì app mở thẳng danh sách dự án, không ghi đã xong. Bản staging bật sẵn |

## 9. Câu hỏi còn mở

1. **Có vuốt ngang để đổi trang không, hay chỉ bấm "Next"?** Bản đề xuất chỉ nói "4 lần Next" và nút Back. Thiết kế đặt tên vùng nội dung là "Page (pager)" và có dãy chấm trang, thường đi với vuốt, nhưng không ghi rõ.
   - Đề xuất của AI: cho vuốt. Vuốt sang trái tới trang sau, vuốt sang phải về trang trước, như bấm "Next" và Back. Ở trang 4 vuốt sang trái không làm gì; chỉ nút "Start drawing" mới xong onboarding. Thẻ quảng cáo đổi khi trang đã dừng hẳn, không đổi giữa lúc vuốt. Thước đo 7 thao tác không đổi. Nếu chọn chỉ bấm nút thì bỏ dòng "Vuốt ngang" ở mục Ngoài phạm vi, KB-1 giữ nguyên.
   - Trả lời: Cho vuốt, theo đề xuất của AI.
2. **"Lần mở sau" ở KB-3 tính thế nào khi app chưa bị hệ thống đóng hẳn?** Bản đề xuất: tắt app giữa chừng thì lần mở sau không còn quảng cáo vì không phải lần mở đầu. Luật của `:core:ads` (OB-03): lần mở đầu kéo dài tới khi tiến trình của app kết thúc. Người dùng bấm Back ở trang 1 rồi mở lại ngay thì tiến trình thường còn sống, nên theo luật của `:core:ads` thẻ quảng cáo vẫn hiện.
   - Đề xuất của AI: theo luật của `:core:ads`, không làm luật riêng. Onboarding luôn hiện lại từ trang 1 khi mở lại sau Back ở trang 1; thẻ quảng cáo hiện khi tiến trình còn sống, không hiện khi app đã bị đóng hẳn.
   - Trả lời: Đồng ý đề xuất của AI.
3. **Chỗ của thẻ quảng cáo khi không có quảng cáo.** Thiết kế chỉ có trạng thái có thẻ. Có hai trường hợp: (a) vị trí được phép nhưng quảng cáo đang nạp hay nạp lỗi; (b) vị trí không được phép (không phải lần mở đầu, khóa tắt, chưa đồng ý quảng cáo).
   - Đề xuất của AI: (a) giữ khung cố định cao bằng thẻ, hiện khung chờ, để nội dung và nút không nhảy giữa các trang; (b) không có khung, nội dung trang dùng phần chỗ đó, nút vẫn ghim đáy cùng chỗ. Thêm ảnh trạng thái (b) vào thiết kế, hoặc chấp nhận screenshot test không có ảnh thiết kế tương ứng.
   - Trả lời: Đồng ý đề xuất của AI.
4. **Bố cục thẻ quảng cáo trên thiết kế khác thẻ của `:core:ads`.** Thiết kế vẽ thẻ theo kiểu native medium: hàng đầu (icon 36 dp, nhãn "Ad", tên), ảnh 136 dp, nút "Install". Thẻ `native_step` của `:core:ads` có ảnh ở trên, dưới ảnh là icon 32 dp, tiêu đề một dòng, nhãn "Ad", nút cao 36 dp.
   - Đề xuất của AI: theo thẻ của `:core:ads` (cùng chiều cao 250 dp, cùng chỗ trên màn), coi thẻ trên thiết kế là khung giữ chỗ. Screenshot test dùng thẻ giả, như các màn khác của dự án mẫu.
   - Trả lời: Theo thẻ của `:core:ads`, theo đề xuất của AI.
5. **Sự kiện phân tích cho onboarding.** Bản đề xuất 9.2 không có sự kiện nào cho onboarding, nên spec không ghi sự kiện. Nhưng onboarding bắt buộc là bước thêm trước nét vẽ đầu tiên, và không có sự kiện thì không biết bao nhiêu người bỏ app giữa 4 trang.
   - Đề xuất của AI: hai phương án. (a) Không thêm sự kiện ở bản đầu, như 9.2. (b) Thêm `onboarding_page_view` (tham số `page`: 1 tới 4, ghi mỗi lần một trang hiện) và `onboarding_complete` (không tham số, ghi khi bấm "Start drawing"), đủ để đo tỷ lệ đi hết và trang bị bỏ nhiều nhất. AI nghiêng về (b) vì Q19 đã nêu rủi ro mất người dùng ở bước này; chọn (b) thì cập nhật 9.2 của bản đề xuất và mục 6 của spec.
   - Trả lời: Phương án (b): thêm `onboarding_page_view` và `onboarding_complete`.
6. **Người dùng đã có dự án mà chưa xong onboarding.** Chỉ xảy ra khi cờ `feature_onboarding_enabled` tắt lúc người dùng cài app rồi được bật sau (KB-7).
   - Đề xuất của AI: cờ bật từ bản phát hành đầu nên trường hợp này gần như không có. Giữ đơn giản: người chưa xong thì thấy onboarding một lần ở lần mở sau, không có quảng cáo vì không phải lần mở đầu. Không dò dự án đã có để bỏ qua.
   - Trả lời: Đồng ý đề xuất của AI.
7. **Màn hình thấp 360 × 640 dp và chữ lớn.** Ở khung này, sau thanh trạng thái, thanh trên, thẻ quảng cáo 250 dp và phần nút 96 dp, chỉ còn khoảng 220 dp cho nội dung trang, trong khi thiết kế cần khoảng 318 dp. Thiết kế không có khung thấp.
   - Đề xuất của AI: hình minh họa co lại theo chỗ còn lại, giữ tỷ lệ, thấp nhất 96 dp; vẫn không đủ thì phần nội dung trang cuộn được, thẻ quảng cáo và nút đứng yên. Có screenshot test `OnboardingScreen_page-2_small` (trang có câu mô tả dài nhất) ở khung 360 × 640.
   - Trả lời: Đồng ý đề xuất của AI.
8. **Bố cục khi máy gập mở ra.** App hỗ trợ máy gập (Q6) nhưng thiết kế không có frame máy gập mở.
   - Đề xuất của AI: một cột, căn giữa, nội dung, thẻ và nút rộng tối đa bằng khung thiết kế (411 dp), nút vẫn ghim đáy. Không có ảnh `_unfolded` nên không cần screenshot test cùng tên; plan thêm một ảnh ở khung `w673dp-h841dp` để xem bố cục.
   - Trả lời: Đồng ý đề xuất của AI.
9. **Ghi "đã xong" lên máy bị lỗi khi bấm "Start drawing".**
   - Đề xuất của AI: vẫn mở danh sách dự án, không báo lỗi cho người dùng, ghi log lỗi. Lần mở sau có thể hiện lại onboarding; chấp nhận vì lỗi này rất hiếm và chặn người dùng ở trang cuối tệ hơn.
   - Trả lời: Đồng ý đề xuất của AI.
10. **Chữ "Start drawing" mà đích là danh sách dự án trống.** Nút ở trang 4 gợi là vào vẽ ngay, nhưng bản đề xuất mục 6 đưa người dùng tới danh sách dự án (trống), rồi còn "New animation" và "Create" mới tới màn vẽ.
   - Đề xuất của AI: giữ đích theo bản đề xuất (thước đo 7 thao tác đã tính hai bước này) và giữ chữ theo thiết kế đã duyệt. Nếu muốn khớp chữ hơn thì có hai cách: đổi chữ nút (ví dụ "Get started"), hoặc mở thẳng màn Tạo dự án (bớt một thao tác); cả hai cần sửa thiết kế hay bản đề xuất.
   - Trả lời: Giữ nguyên đích và chữ, theo đề xuất của AI.

## 10. Lịch sử

| Ngày | Người | Nội dung |
| --- | --- | --- |
| 2026-11-21 | AI | Tạo bản đầu từ bản đề xuất đã duyệt (docs/research/proposal.md) và thiết kế đã duyệt |
| 2026-11-21 | AI | Ghi câu trả lời của anh Nhật cho mọi câu hỏi ở mục 9 (anh chọn trong phiên ngày 2026-11-21) và sửa phần thân spec theo các câu trả lời; sửa các điểm nên sửa còn lại của lần rà soát cuối. Spec vẫn `draft` |
| 2026-11-21 | AI | Ghi các frame thiết kế bổ sung, sửa theo câu trả lời (chờ anh Nhật duyệt) ở mục 4 và các tiêu chí liên quan |
| 2026-11-21 | AI | Thiết kế bổ sung đã được anh Nhật duyệt trong phiên (chọn "Duyệt") |
| 2026-11-21 | AI | Chuyển `confirmed` theo lời anh Nhật (người phụ trách sản phẩm) trong phiên: "xác nhận 6 spec" |
