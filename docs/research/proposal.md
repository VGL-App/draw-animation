---
title: Frameloop – Draw Animation
package: com.vgl.draw.animation
date: 2026-11-19
author: AI, theo yêu cầu của anh Nhật
reference_apps: [com.vblast.flipaclip, com.banix.drawsketch.animationmaker, com.zenbyte.flipanimation, com.cem.draw.animation, com.cem.flipartify, com.appolo13.stickmandrawanimation, org.fortheloss.sticknodes, com.cateater.stopmotionstudio, com.weirdhat.roughanimator]
status: approved               # draft | approved. Chỉ dev ghi approved
approved_by: anh Nhật
approved_at: 2026-11-20
design_direction: Studio Night # tên hướng thiết kế dev đã chọn (mục 10), điền sau bước chọn hướng
---

# ĐỀ XUẤT APP: FRAMELOOP – DRAW ANIMATION

<!--
Bản đề xuất app clone (ADR-0021, docs/ops/clone-study.md). AI viết từ báo cáo khảo sát; dev duyệt trước khi sang Figma.
Mỗi cải tiến dẫn về bài học có bằng chứng. Quảng cáo và dữ liệu thu thập chỉ ghi thành câu hỏi.
-->

Nguồn: báo cáo thị trường [web-play.md](web-play.md), do anh Nhật cung cấp (bản ngày 19/10/2026, 21 app, AppBrain, Similarweb, review ở Mỹ, Anh, Úc, Canada). Trong bài, "(Play §n)" là mục n của báo cáo đó.

Khảo sát ngày 2026-11-19 (Q1), trên máy Xiaomi Android 15, vùng Việt Nam, người dùng mới ngày đầu:

| Báo cáo | Phạm vi | Ghi chú |
| --- | --- | --- |
| [com.vblast.flipaclip.md](com.vblast.flipaclip.md) | Đủ, 48 ảnh | Chưa thấy interstitial, app open ở phiên sau |
| [com.banix.drawsketch.animationmaker.md](com.banix.drawsketch.animationmaker.md) | Tới màn chính, 18 ảnh | Màn chính có video quảng cáo chạy liên tục, công cụ không lấy được cây giao diện nên không vào được màn vẽ; phần màn vẽ lấy từ APK |
| [com.zenbyte.flipanimation.md](com.zenbyte.flipanimation.md) | Đủ, 43 ảnh | Một luồng thanh toán của Play tự mở khi máy mất kết nối, do chạm không phải của công cụ; không có giao dịch nào hoàn tất trong log |
| [apk-analysis.md](apk-analysis.md) | 6 app | Công nghệ, lưu dự án, SDK, khóa Remote Config |

Trong bài, "(FC)", "(Banix)", "(ZB)", "(APK)" là các báo cáo trên.

| Viết tắt | App | Package |
| --- | --- | --- |
| FlipaClip | FlipaClip (Visual Blasters) | `com.vblast.flipaclip` |
| Banix | Draw Animation – Anim Creator (Banix) | `com.banix.drawsketch.animationmaker` |
| ZenByte | Flip Animation (ZenByte) | `com.zenbyte.flipanimation` |
| CEM | Animation Creator: Flipbook 2D (CEM) | `com.cem.draw.animation` |
| FlipArtify | FlipArtify (CEM) | `com.cem.flipartify` |
| Stickman | Stickman: draw animation maker (Appache) | `com.appolo13.stickmandrawanimation` |
| Stick Nodes | Stick Nodes (ForTheLoss) | `org.fortheloss.sticknodes` |
| SMS | Stop Motion Studio (Cateater) | `com.cateater.stopmotionstudio` |
| RoughAnimator | RoughAnimator (WeirdHat) | `com.weirdhat.roughanimator` |

## 1. Thông tin dev cung cấp

- **Ý tưởng:** app vẽ hoạt hình 2D kiểu lật trang (flipbook) trên điện thoại: vẽ từng khung, nhiều layer, onion skin, dòng thời gian, âm thanh, xuất MP4 và GIF.
- **Cách làm:** app clone, theo hai app dẫn đầu là FlipaClip và Banix; phải thắng bằng chất lượng (Play §1, điều 4).
- **Cách kiếm tiền:** quảng cáo, kèm rewarded để mở các phần thêm (Play §7).
- **Định vị:** xưởng hoạt hình miễn phí, đủ đồ nghề, không quảng cáo chen vào giữa lúc vẽ hay phát, không bao giờ mất bài (Play §5; sửa theo Q4).
- **Trả lời ngày 2026-11-19 (mục 12):**
  - Tên: **Frameloop – Draw Animation** trên store, **Frameloop** dưới icon (Q2). Tên gói **`com.vgl.draw.animation`** (chưa có trên Play: trang Play của gói này trả 404 ngày 2026-11-19). Thư mục repo `/Users/khacnhat/Projects/draw-animation`. R2 staging chưa có, tạm dùng `https://draw-animation-staging.vgl.io.vn/`. 
  - Khảo sát trên máy: FlipaClip, Banix, ZenByte; đọc APK 6 app (Q1).
  - **Camera ảo có trong bản đầu** (Q3).
  - **Quảng cáo `resume`, `resume_inter` hiện như mọi màn, kể cả màn vẽ**; định vị sửa thành "không chen vào giữa lúc vẽ hay phát" (Q4).
  - Quyền `RECORD_AUDIO` (hỏi khi bấm ghi âm) và `WRITE_EXTERNAL_STORAGE` với `maxSdkVersion=28` (Q5).
  - Không hỗ trợ máy tính bảng ở bản đầu; có điện thoại màn hình gập (Q6).
  - **Mediation: chỉ AdMob** (Q7).
  - Giữ ADR-0024: app luôn cần mạng (Q8).
  - Tiếng Anh, phát hành thử Úc và Canada trước (Q9).
  - Chưa có gói gỡ quảng cáo hay bản Pro (Q10).

## 2. Tóm tắt thị trường

| App | Tổng tải | Tải/30 ngày | Điểm | Kiếm tiền | Điểm mạnh nổi bật |
| --- | ---: | ---: | --- | --- | --- |
| FlipaClip | 82 tr | 670K | 4,29 | QC + thuê bao Plus | Đủ đồ nghề, không giới hạn khung, mạnh ở Tier 1, có cộng đồng thi đấu |
| Banix | 16 tr | 690K | 4,42 | QC + IAP | Lên nhanh nhất từ 2024; thị trường mới nổi |
| Stickman (Appache) | 39 tr | 260K | 4,45 | QC, rewarded mở khung | Người que, dễ chơi |
| SMS | 22 tr | 160K | 4,11 | Không QC, Pro $5,99 | Ổn định, Teacher Approved |
| Stick Nodes | 17 tr | 100K | 4,67 | Một QC lúc mở + Pro $3,99 | Quảng cáo nhẹ, được khen nhiều nhất |
| CEM | 4,1 tr | 100K | 3,39 | QC + Pro | Đang tụt; khoá sau 4 khung |

- **Thị trường:** hai app dẫn đầu chiếm khoảng 65% lượt tải đo được. Lượt tải dồn về Philippines, Ấn Độ, Brazil; Tier 1 chủ yếu trả tiền cho app trả phí (Play §1).
- **Người dùng:** nghiêng về 8 đến 13 tuổi (Play §1, §6). Đây là rủi ro chính sách lớn nhất (mục 9.3).
- **Khoảng trống:** quảng cáo chen vào lúc vẽ, khoá tính năng cốt lõi, mất bài, tô màu tràn, thao tác khung rườm rà (Play §4).

### Đo trên máy

| | FlipaClip | Banix | ZenByte |
| --- | --- | --- | --- |
| Thao tác tới nét vẽ đầu tiên | 10 | > 10 (chưa tới được màn vẽ) | 4 |
| Quảng cáo trước nét vẽ đầu tiên | 0 | ≥ 11, trong đó 5 toàn màn hình; khoảng 5 phút | 0 (app không có quảng cáo) |
| Màn trả phí bắt buộc ở lần mở đầu | Có, back không đóng | Có (VIP) | Không |
| Hỏi tuổi | Có, thanh trượt trung lập, mặc định 0 | Không | Không |
| Layer miễn phí | 3, từ layer 4 mỗi layer một rewarded | Không có layer (APK) | Không giới hạn |
| Giới hạn miễn phí khác | 24 cọ, 8 gói âm thanh, nhập nhạc, bỏ watermark | Khung, mẫu VIP (APK) | 4 dự án, 150 khung (đã thấy trên máy), bài học, nội suy, bỏ watermark |
| Gói trả phí (giá VN) | Plus 712.000 ₫/năm, 142.000 ₫/tháng, thử 7 ngày | VIP 594.000 ₫/năm, 198.000 ₫/tháng, gói tuần chọn sẵn không ghi giá | Pro **trọn đời 140.000 ₫** hoặc 28.000 ₫/tuần |
| Quảng cáo khi quay lại app | Không thấy | Không | Không |

## 3. Tên và tên gói đề nghị

| Tên | Tên gói | Lý do | Kiểm tra trùng trên Play |
| --- | --- | --- | --- |
| Draw Animation (dev chọn lúc đầu, bỏ) | `com.vgl.draw.animation` | Đúng từ khoá chính của ngách | Tên gói trống. **Tên trùng đầu tên app mẫu Banix** ("Draw Animation – Anim Creator") và ít nhất 3 app khác (Play §2, #9, #11, #12) |
| **Frameloop – Draw Animation** (dev chọn 2026-11-19) | `com.vgl.draw.animation` | frame (khung) + loop (lặp): đúng cách làm hoạt hình lật trang, gợi GIF lặp vòng. Giữ từ khoá "Draw Animation" cho ASO, có tên riêng để không giống app mẫu (ADR-0021, mục 7). 26 ký tự, vừa giới hạn 30 của Play | Tìm "Frameloop" trên Play (Mỹ, 2026-11-19): không có app trùng. **Chưa tra nhãn hiệu**: tra trước khi phát hành |
| Inkreel, Sketchreel, Penframe (phương án khác) | | | Không trùng trên Play cùng ngày |

Tên gói giữ nguyên. Tên trên store và tên dưới icon đổi được sau này, tên gói thì không.

## 4. Bài học từ app mẫu và cải tiến

| Bài học | Bằng chứng | Cải tiến | Cách đo |
| --- | --- | --- | --- |
| Quảng cáo chen vào lúc vẽ bị phạt nặng nhất | CEM bật QC khi đang kéo nét; FlipaClip "3 QC trong chưa tới 5 phút"; Banix "8 QC trước khi vẽ được khung đầu" (Play §3). Đo trên máy: Banix 11 QC trước màn chính (Banix); Banix, CEM đặt native thu gọn trên màn vẽ (APK) | Không có vị trí quảng cáo nào trên màn vẽ, không quảng cáo chen vào giữa nét vẽ hay lúc phát. Quảng cáo `resume` khi quay lại app vẫn hiện ở mọi màn (Q4) | 0 vị trí QC đặt trên màn vẽ; số QC từ lúc mở app tới nét vẽ đầu tiên ≤ 1 |
| Quảng cáo sát nút gây bấm nhầm | Banix đặt QC sát nút, che khung hình (Play §3). Đo trên máy: trang 4 hướng dẫn Banix dời nút "Tiếp theo" lên giữa và đặt nút CTA quảng cáo cùng cỡ, cùng màu đúng chỗ cũ; native ở màn ngôn ngữ đổi quảng cáo đúng lúc nút Apply hiện, cách nút 16 dp; khóa `CTR_CLICK_*` trong APK (Banix, APK) | Native chỉ ở danh sách dự án, bài học, màn chờ xuất; luôn chừa khoảng cách với nút | Rà ảnh thiết kế; không có vị trí QC trên màn vẽ và dòng thời gian |
| Mở khoá từng món bằng QC làm người dùng bực | FlipaClip mỗi layer một QC, có khi xem 3 lần (Play §3, §4 điểm 4) | Một lượt rewarded mở cho cả dự án | Số lượt xem QC để mở layer 6 đến 10 trong một dự án = 1 |
| Khoá tính năng cốt lõi | CEM khoá sau 4 khung; FlipaClip khoá nhập nhạc (Play §3) | Không giới hạn khung; 5 layer miễn phí; nhập nhạc miễn phí | Dự án 200 khung × 5 layer không gặp màn khoá nào |
| Mất bài | Review mất bài của FlipArtify được 213 người đánh dấu hữu ích; FlipaClip mất bài khi cài lại (Play §3, §4 điểm 5) | Tự lưu, khôi phục sau crash, sao lưu dự án ra một file | Tắt app đột ngột giữa lúc vẽ: mở lại mất tối đa 30 giây công |
| Chậm, crash khi nhiều khung | Play §4 điểm 6 | Khung lưu trên bộ nhớ máy, RAM chỉ giữ các khung quanh đầu phát | 200 khung × 5 layer phát mượt ở 12 khung/giây trên máy tầm trung; độ trễ nét vẽ đo được ở tuần 1 |
| Tô màu tràn qua khe hở, để lại viền trắng | Play §4 điểm 7. Đo trên máy: FlipaClip tô vào vùng hở thì đổ màu cả nền (FC) | Tô màu tự đóng khe hở, tô lấn dưới nét | Bộ ảnh thử có khe 1, 2, 4 px: không tràn; không còn viền trắng |
| Thao tác khung rườm rà | Play §4 điểm 8. Đo trên máy: FlipaClip và ZenByte đều không có chọn nhiều khung (FC, ZB) | Chọn nhiều khung, chép và dán theo dải, giữ khung | Nhân bản 10 khung liền nhau trong ≤ 3 thao tác |
| Rào cản trước nét vẽ đầu tiên | FlipaClip 10 thao tác và một màn trả phí không đóng bằng back; Banix 11 QC và màn VIP; ZenByte chỉ 4 thao tác (FC, Banix, ZB) | Không có màn trả phí hay onboarding nhiều trang trước màn vẽ; hướng dẫn trên màn vẽ bỏ qua được | ≤ 4 thao tác từ lần mở đầu tới nét vẽ đầu tiên |
| Lời mời nâng cấp nhẹ nhàng giữ được việc đang làm | ZenByte: ở khung 150, một thẻ nhỏ trên canvas báo giới hạn và có nút "Nâng cấp", không chặn, không làm tối nền, tự ẩn sau vài giây (ZB). FlipaClip đẩy màn trả phí toàn màn hình (FC) | Khi chạm giới hạn layer, mời rewarded bằng thẻ nhỏ trên màn vẽ, không mở màn toàn màn hình | Không có màn toàn màn hình nào tự mở trên màn vẽ |
| Bài học mở thẳng vào màn vẽ giữ người mới | ZenByte: 115 bài trong 6 nhóm, bài mở thẳng vào màn vẽ với khung dựng sẵn, nét dẫn màu đỏ, biểu đồ nhịp và âm thanh đặt sẵn; bộ truyện nhiều tập; nút "Thử bài học miễn phí" ở màn dự án trống (ZB). Stickman có bản đồ bài học kiểu Duolingo (APK) | Bài học là một dự án dựng sẵn mở trong màn vẽ, có lớp dẫn; không tính vào số dự án | Tỉ lệ người mới hoàn thành bài đầu tiên trong ngày đầu (sự kiện `lesson_complete`) |
| Canvas nhỏ vì nhiều hàng điều khiển | ZenByte có 4 hàng điều khiển dưới canvas, canvas 16:9 chỉ còn khoảng 25% chiều cao màn (ZB) | Thanh công cụ một bên, dòng thời gian thu gọn được (khuôn của FlipaClip, dòng thời gian 56 dp) | Canvas của dự án 9:16 chiếm ≥ 40% chiều cao màn ở khung 411 × 891; dự án 16:9 cho xoay ngang màn vẽ, vì canvas 16:9 rộng hết màn dọc chỉ cao khoảng 26% |
| Rewarded không đóng được làm người dùng kẹt | Rewarded Pangle của FlipaClip không có nút đóng sau 60 giây rồi tự mở Play; interstitial Pangle của Banix chờ 42 đến 66 giây (FC, Banix) | Chỉ AdMob (Q7) | Không quảng cáo nào chờ quá 30 giây mới đóng được (đo trên staging) |

## 5. Tính năng

| Tính năng | Nhóm | Có ở app mẫu nào | Ghi chú |
| --- | --- | --- | --- |
| Danh sách dự án, tạo dự án (16:9, 9:16, 1:1, tối đa 1080p; 6 đến 30 khung/giây) | bắt buộc | Mọi app | `:feature:projects` |
| Canvas: cọ (8 đến 10 loại, lực bút, chống rung), tẩy, màu, hút màu, tô màu | bắt buộc | FlipaClip, RoughAnimator | `:feature:editor`. Banix chỉ có một cọ |
| Không giới hạn khung | bắt buộc | FlipaClip, RoughAnimator | CEM khoá sau 4 khung |
| Layer: 5 miễn phí, 6 đến 10 mở bằng rewarded | bắt buộc | FlipaClip 3 layer; Banix không có | |
| Onion skin chỉnh số khung trước sau, màu, độ mờ | bắt buộc | FlipaClip, CEM, RoughAnimator | |
| Chọn vùng, di chuyển, thu phóng, xoay; thước, hình, chữ, vẽ đối xứng | bắt buộc | FlipaClip | |
| Dòng thời gian: thêm, xoá, nhân bản, chọn nhiều, chép dải, giữ khung, lặp, phát | bắt buộc | FlipaClip (một phần) | |
| Hoàn tác nhiều bước, kể cả xoá khung, xoá layer | bắt buộc | FlipaClip (từ 2026) | |
| Nhập ảnh, video làm nền vẽ theo, qua Photo Picker | bắt buộc | FlipaClip, SMS | Không cần quyền |
| Âm thanh: nhập nhạc miễn phí, hiệu ứng CC0, ghi âm giọng | bắt buộc | FlipaClip (nhập nhạc khoá), SMS | Ghi âm cần quyền `RECORD_AUDIO`: Q5 |
| Xuất MP4, GIF; watermark bỏ bằng rewarded | bắt buộc | Mọi app | `:feature:export` |
| Tự lưu, khôi phục sau crash | bắt buộc | | Chỉ lưu trên máy |
| Quảng cáo công bằng (mục 9) | khác biệt | Stick Nodes | |
| Sao lưu dự án ra file và khôi phục (bộ chọn file hệ thống, chọn được Google Drive) | khác biệt | SMS (qua Drive) | Không cần backend |
| Tô màu đóng khe hở | khác biệt | Chưa app miễn phí nào làm tốt | |
| Camera ảo: lia, phóng, xoay cả cảnh | khác biệt | Stick Nodes | Có trong bản đầu (Q3) |
| Xuất chuỗi PNG, nền trong suốt bằng rewarded | khác biệt | FlipaClip khoá sau thuê bao | |
| 15 đến 20 bài học có sẵn chia nhóm (nền tảng, nguyên lý, nhân vật, hiệu ứng...), mở thẳng vào màn vẽ với lớp dẫn (Q12) | khác biệt | ZenByte (115 bài), Stickman (khoảng 42 bài) | `:feature:lessons`, nội dung trên R2. Q12 |
| Hướng dẫn trên màn vẽ, bỏ qua được, xem lại được | bắt buộc | ZenByte (9 bước) | |
| Khẩu hình, khung xương người que, nội suy chuyển động, làm mờ, 60 khung/giây | sau | Stick Nodes (khung xương, nội suy) | Play §5 tầng 3 |
| Cộng đồng, thi đấu; AI dựng video; tài khoản, đám mây; nhân vật của bên khác; từ "kids"; xuất chạy nền | không làm | | Play §5 |

**Phạm vi bản đầu:** toàn bộ nhóm `bắt buộc` và nhóm `khác biệt`. Lịch có thể dài hơn 8 đến 9 tuần khoảng một tuần vì camera ảo, và thêm thời gian làm nội dung cho 15 đến 20 bài học (Q12).

**Module dự kiến:**

| Module | Vai trò |
| --- | --- |
| `:core:animation` (core mới) | Mô hình dự án, khung, layer; lưu khung trên bộ nhớ máy; tự lưu; đọc ghi file sao lưu. Dùng chung cho `projects`, `editor`, `export`, nên phải là core (ADR-0001) |
| `:feature:projects` | Danh sách dự án, tạo, đổi tên, xoá, nhân bản, khôi phục từ file |
| `:feature:editor` | Canvas, công cụ, layer, onion skin, dòng thời gian, âm thanh, phát thử |
| `:feature:export` | Xuất MP4, GIF, PNG; màn chờ xuất; lưu vào thư viện máy, chia sẻ |
| `:feature:lessons` | Thư viện bài học, mở bài học thành dự án mẫu |

`:feature:editor` là phần lớn và rủi ro nhất. Tuần đầu làm prototype lõi vẽ (Play §8): độ trễ nét vẽ, bộ nhớ với 200 khung × 5 layer, phát lại mượt trên máy yếu. Không đạt thì dừng để cân nhắc lại.

## 6. Luồng sử dụng

Đối chiếu với khảo sát: FlipaClip mất 10 thao tác và một màn trả phí, ZenByte 4 thao tác, Banix hơn 10 thao tác và 11 quảng cáo (mục 2). Frameloop theo cách của ZenByte.

- **Mở đầu, lần đầu:** Loading > hộp thoại đồng ý quảng cáo > Danh sách dự án (trống, có nút "Tạo dự án" và thẻ "Học bài đầu tiên") > Tạo dự án (chọn khung, tốc độ; có giá trị mặc định) > Màn vẽ (hướng dẫn trên màn vẽ, bỏ qua được). **3 đến 4 bước tới nét vẽ đầu tiên**, không có quảng cáo toàn màn hình (open app chỉ từ lần mở thứ 3), không có màn trả phí.
- **Mở lại:** Loading > (open app từ lần mở thứ 3, tối đa một lần mỗi phiên) > Danh sách dự án > chạm dự án > Màn vẽ.
- **Luồng chính:** Màn vẽ: vẽ > thêm khung > onion skin > phát thử > lặp lại. Mọi công cụ nằm trong màn vẽ (thanh công cụ, bảng layer, dòng thời gian, bảng âm thanh).
- **Xuất:** Màn vẽ > Xuất > chọn định dạng, chất lượng (rewarded nếu bỏ watermark, PNG, nền trong) > Màn chờ xuất (native) > Xong: lưu vào thư viện, chia sẻ > (interstitial sau khi file đã lưu) > quay lại.
- **Bài học:** Danh sách dự án > Bài học > chọn bài > dự án mẫu mở trong màn vẽ, có chỉ dẫn từng bước.
- **Sao lưu:** Danh sách dự án > menu dự án > Sao lưu ra file > bộ chọn file hệ thống. Khôi phục: Danh sách dự án > Nhập từ file.

## 7. Danh sách màn hình

| Màn hình | Tên frame | Trạng thái cần dựng | Feature |
| --- | --- | --- | --- |
| Danh sách dự án | `ProjectsScreen_<trạng thái>_<light\|dark>` | empty, list, menu, restoring, restore_error | projects |
| Tạo dự án | `NewProjectScreen_...` | default | projects |
| Màn vẽ | `EditorScreen_...` | drawing, tour, tool_brush, tool_fill, tool_select, tool_text, layers, onion_settings, timeline_multiselect, playing, sound, layer_locked (mời rewarded) | editor |
| Xuất | `ExportScreen_...` | options, exporting, done, error | export |
| Bài học | `LessonsScreen_...` | list, loading, error | lessons |
| Chỉ dẫn bài học trên màn vẽ | `EditorScreen_lesson_...` | step | editor |

Hỗ trợ điện thoại màn hình gập theo mặc định của mẫu: thêm frame `_unfolded` (673 × 841) cho Màn vẽ và Danh sách dự án, vì bố cục đổi khi rộng (bảng layer và dòng thời gian nằm cạnh canvas). Máy tính bảng: Q6.

## 8. Điểm nổi bật

- Vẽ thoải mái: không quảng cáo nào chen vào lúc bạn đang vẽ hay xem lại.
- Không giới hạn khung, 5 layer miễn phí, nhập nhạc của bạn miễn phí.
- Không bao giờ mất bài: tự lưu liên tục, sao lưu cả dự án ra một file.
- Tô màu không tràn, kể cả khi nét vẽ còn hở.
- Chọn nhiều khung, chép cả đoạn chỉ với vài chạm.
- 15 đến 20 bài học có sẵn giúp bạn làm hoạt hình đầu tiên trong vài phút.

## 9. Quảng cáo và dữ liệu (câu hỏi cho dev)

### 9.1 Vị trí quảng cáo

App mẫu: FlipaClip dày (open, sau thao tác, rewarded từng layer); Banix rất dày, sát nút; Stick Nodes một lần lúc mở (Play §3). AI đề xuất theo Play §7, ánh xạ vào `AdPlacement` của `:core:ads` (bộ PARTNER của AdsKit, ADR-0045):

| Vị trí | Định dạng | Màn | Quy tắc đề xuất |
| --- | --- | --- | --- |
| `open_app` | App open | Loading | Từ lần mở thứ 3, tối đa một lần mỗi phiên |
| `resume`, `resume_inter` | App open, interstitial | Quay lại app | Hiện như mọi màn, kể cả màn vẽ (Q4); chỉ khi quay lại app, không chen vào giữa nét vẽ |
| `projects_native` | Native medium | Danh sách dự án | Cuối danh sách hoặc dưới cùng, cách xa nút |
| `lessons_native` | Native small | Bài học | |
| `editor_rw_layer` | Rewarded | Màn vẽ, khi thêm layer thứ 6 | Mở layer 6 đến 10 cho cả dự án |
| `export_rw_extra` | Rewarded | Xuất | Bỏ watermark, chuỗi PNG, nền trong: mở cho lần xuất đó của dự án |
| `export_native` | Native medium | Màn chờ xuất | |
| `export_interstitial` | Interstitial | Sau khi xuất xong | Sau khi file đã lưu; theo `ads_interval` |
| (không bao giờ) | | Vị trí đặt trên màn vẽ, chen vào lúc đang phát, đè canvas hay dòng thời gian | |

Gói âm thanh, gói cọ mở bằng rewarded (Play §7) để bản sau, khi có nội dung thêm.

### 9.2 Sự kiện phân tích đề xuất

`project_create`, `project_open`, `frame_add` (gom theo phiên, không ghi mỗi lần), `playback_start`, `layer_unlock_rewarded`, `export_start`, `export_success` (định dạng, số khung, thời lượng), `export_fail`, `backup_export`, `backup_restore`, `lesson_start`, `lesson_complete`. Không ghi nội dung tranh, âm thanh hay chữ người dùng nhập.

### 9.3 Trẻ em và chính sách gia đình

Theo Play §6, AI đề xuất làm cả 5 điều: khai đối tượng 13+; hình ảnh store theo phong cách tuổi teen và người lớn; không có "kids"; **chỉ AdMob, không mediation** ở bản đầu (Q7; AdMob có chứng nhận cho chương trình gia đình); lập kế hoạch doanh thu theo eCPM thấp.

### 9.4 Dữ liệu

Tranh, dự án, âm thanh chỉ lưu trên máy. Không tài khoản. Không có quyền vị trí, danh bạ.

## 10. Hướng thiết kế

**Giữ:** canvas ở giữa, thanh công cụ dọc một bên, dòng thời gian dạng dải ảnh nhỏ ở đáy, onion skin đỏ (khung trước) và xanh (khung sau), nút phát ở dòng thời gian. Đây là khuôn mà người dùng FlipaClip đã quen.

**Đổi:** màu, bộ icon (giấy phép rõ ràng, ví dụ Material Symbols), phông chữ, hình dạng. Giọng văn hướng tuổi teen và người lớn, không dùng nhân vật dễ thương (mục 9.3). Giao diện màn vẽ dùng màu trung tính để không lấn màu tranh.

| Hướng | Ý chính | Màu | Chữ | Icon | Link Figma |
| --- | --- | --- | --- | --- | --- |
| Studio Night | Tối, tối giản, kiểu phần mềm dựng phim; giao diện lùi vào nền tối để tranh nổi bật | Nền #0E1014, bề mặt #171A20, nhấn xanh chanh #B6F35C, xanh dương #6EA8FF; onion skin đỏ #FF5A5F và xanh #3D8BFF | Space Grotesk (tiêu đề), Inter (nội dung) | Material Symbols Rounded (Apache 2.0) | [Direction/Studio Night](https://www.figma.com/design/uDdqi9ojRvIy4689FvCh5O?node-id=2-2) |
| Paper & Ink | Sáng, ấm, như sổ phác thảo; tiêu đề chữ có chân | Nền giấy #F6F1E8, mực xanh #2340C8, cam đất #E0673A | Fraunces, DM Sans | Material Symbols Rounded | [Direction/Paper & Ink](https://www.figma.com/design/uDdqi9ojRvIy4689FvCh5O?node-id=3-2) |
| Loop Pop | Nhiều màu, vui nhưng không trẻ con; bo tròn lớn, chữ đậm | Tím #6B4EFF, vàng #FFB800, nền trắng | Bricolage Grotesque, Plus Jakarta Sans | Material Symbols Rounded | [Direction/Loop Pop](https://www.figma.com/design/uDdqi9ojRvIy4689FvCh5O?node-id=3-309) |

Mỗi hướng có bảng phong cách (biến màu Light và Dark, chữ, bo góc, khoảng cách, icon), 7 component và hai màn `ProjectsScreen_list` và `EditorScreen_drawing` ở khung 411 × 891, cùng một bố cục để so phong cách. So với màn tương ứng của FlipaClip (ảnh khảo sát `10-home`, `16-first-strokes`): nút tạo hoạt hình lớn ở đầu màn dự án thay cho nút + giữa thanh đáy; thanh công cụ dọc bên trái và tùy chọn cọ bên phải để canvas 9:16 chiếm khoảng 57% chiều cao màn; dải khung hiện 8 khung kèm onion skin, số khung, tốc độ khung; không có quảng cáo trên màn vẽ.

Dev chọn: **Studio Night**, ngày 2026-11-20 (anh Nhật).

## 11. Tài sản mượn tạm dự kiến

Không có. Hiệu ứng âm thanh lấy từ nguồn CC0 ngay từ đầu. Bài học do nhóm tự làm (AI soạn kịch bản, người vẽ hoặc duyệt). Cọ là code, không phải ảnh.

## 12. Câu hỏi còn mở

Mọi câu đã có trả lời của anh Nhật ngày 2026-11-19, ghi ở cột cuối, kể cả Q11 đến Q14 sau khảo sát.

| # | Câu hỏi | Phương án AI đề xuất | Dev trả lời |
| --- | --- | --- | --- |
| Q1 | Có khảo sát trên máy và đọc APK không? | FlipaClip và Banix | FlipaClip, Banix, ZenByte; đọc APK 6 app trên máy |
| Q2 | Tên app trùng đầu tên app mẫu Banix | Thêm thương hiệu riêng | **Frameloop – Draw Animation** |
| Q3 | Bản đầu có camera ảo không? | Dời sang 1.1 | Có trong bản đầu |
| Q4 | Quảng cáo `resume`, `resume_inter` khi quay lại app đang ở màn vẽ | Không hiện ở màn vẽ, màn chờ xuất | Hiện như mọi màn; sửa định vị thành "không chen vào giữa lúc vẽ hay phát" |
| Q5 | Quyền `RECORD_AUDIO`, `WRITE_EXTERNAL_STORAGE` (`maxSdkVersion=28`) | Có cả hai; ghi âm hỏi khi bấm | Như đề xuất |
| Q6 | Máy tính bảng | Không trong bản đầu; có máy gập | Như đề xuất |
| Q7 | Mediation | Chỉ mạng có chứng nhận gia đình | Chỉ AdMob |
| Q8 | App luôn cần mạng (ADR-0024) hay chạy offline (Play §5 #15) | Giữ ADR-0024; Play §5 #15 hiểu là "không cần tài khoản" | Như đề xuất |
| Q9 | Ngôn ngữ, thị trường | Tiếng Anh (`values/`, `localeFilters`); thử Úc, Canada trước | Như đề xuất |
| Q10 | Gói gỡ quảng cáo, bản Pro | Chưa trong bản đầu | Như đề xuất |

Câu hỏi mới sau khảo sát trên máy:

| # | Câu hỏi | Phương án AI đề xuất | Dev trả lời |
| --- | --- | --- | --- |
| Q11 | Có hỏi tuổi ở lần mở đầu không? FlipaClip hỏi bằng thanh trượt trung lập (mặc định 0, Continue khóa tới khi chọn); Banix, ZenByte không hỏi | Không hỏi ở bản đầu, vì khai đối tượng 13+ (mục 9.3). Nếu Google yêu cầu đối tượng hỗn hợp thì làm cổng tuổi trung lập theo cách FlipaClip || Không hỏi |
| Q12 | Số bài học bản đầu | 6 đến 10 bài, mỗi bài là dự án dựng sẵn mở trong màn vẽ (khung có sẵn, lớp nét dẫn, biểu đồ nhịp), theo cách ZenByte. Bộ truyện nhiều tập để bản sau || **15 đến 20 bài, chia nhóm** |
| Q13 | Layer miễn phí: ZenByte cho không giới hạn, FlipaClip 3 | Giữ 5 miễn phí, 6 đến 10 bằng một rewarded cho cả dự án; nâng lên nếu số liệu cho thấy người dùng chạm giới hạn nhiều mà ít xem quảng cáo || Như đề xuất |
| Q14 | Q10 (bản Pro) có xem lại không? ZenByte bán Pro trọn đời 140.000 ₫, FlipaClip và Banix bán thuê bao | Giữ "chưa trong bản đầu"; ghi lại mức giá trọn đời làm mốc khi bàn bản Pro || Giữ: chưa làm |
