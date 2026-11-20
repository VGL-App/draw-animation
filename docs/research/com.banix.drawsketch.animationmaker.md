---
package: com.banix.drawsketch.animationmaker
name: Draw Animation – Anim Creator
version: 10.8 (99)
surveyed_at: 2026-11-19
surveyed_on: máy thật Xiaomi (23021RAAEG), Android 15, 1080x2400, 440 dpi (393x873 dp)
network: có mạng (Wi-Fi, vùng Việt Nam)
status: draft
---

# APP MẪU: DRAW ANIMATION – ANIM CREATOR (BANIX)

<!--
Báo cáo khảo sát một app mẫu (ADR-0021, docs/ops/clone-study.md). AI viết, dev xem lại.
Mỗi nhận xét dẫn về ảnh (`NN-ten`, trong captures/com.banix.drawsketch.animationmaker/ của thư mục làm việc, không vào repo) hay dòng cây giao diện.
PHIÊN DỪNG Ở MÀN CHÍNH: trên màn chính `uiautomator dump` luôn lỗi ("could not get idle state"), không lấy được cây giao diện nên không chạm được (mục 9, 10). Chưa tạo dự án, chưa vẽ.
Phiên chia ba đợt: đợt 1 (16:21 đến 16:46) kẹt ở interstitial Pangle; người sửa bộ chặn; đợt 2 (20:03 đến 20:11) không `pm clear`, đi lại hướng dẫn rồi tới màn chính, dừng vì công cụ dùng cây cũ; người sửa `dump`; đợt 3 (20:23 đến 20:36) thử lấy cây ở màn chính trong 15 phút, không được.
-->

## 1. Thông tin

Draw Animation – Anim Creator của Banix (Thái Bình). Ra mắt 02/2024, 16 triệu lượt tải, 690 nghìn lượt tải mỗi 30 ngày (nhỉnh hơn FlipaClip), điểm 4,42 (64 nghìn đánh giá). Kiếm tiền bằng quảng cáo và IAP. Thị trường mạnh: Philippines, Ấn Độ, Brazil ([web-play.md](web-play.md), mục 2). Bản trên máy: 10.8 (99).

Mã nguồn riêng ([apk-analysis.md](apk-analysis.md), mục 2): View, Fragment, DataBinding; lõi vẽ là thư viện Doodle mã nguồn mở. Quảng cáo AdMob và AppLovin MAX. Trên máy thấy AdMob (`AdActivity`, native toàn màn hình tự dựng `admob_fullscreen__nativeAdView`) và Pangle (`TTFullScreenExpressVideoActivity`).

## 2. Lần mở đầu

Đợt 1 bắt đầu bằng `pm clear`. App chỉ ghi "đã xong hướng dẫn" sau khi interstitial cuối đóng, nên mỗi lần mở lại (4 lần ở đợt 1, 1 lần ở đợt 2) đều đi lại từ màn chờ như người dùng mới. Bảng dưới ghép hai đợt: bước 1 đến 10 đo được ở cả năm lần, bước 11 đến 16 chỉ ở đợt 2.

| # | Màn hình | Quảng cáo | Thao tác của người dùng | Ảnh |
| --- | --- | --- | --- | --- |
| 1 | Màn chờ "DrawAnimation." (trong `TutorialActivity`): ảnh động, "Loading data ...", **"This action may contain ads."**, khung native 393x200 dp ở đáy | QC 1: native 200 dp (lúc chụp là khung shimmer) | (chờ 6 đến 13 giây) | `01-splash` |
| 2 | **Interstitial AdMob** | QC 2 | Đóng; nút đóng hiện sau 2, 6, 11, 6 giây. Một mẫu dạng web có nút X không có trong cây giao diện | `02-ad-splash-full`, `09-ad-relaunch` |
| 3 | **Native toàn màn hình** (393x823 dp, CTA 340x50 dp ở đáy) | QC 3 | X 29x29 dp ở (332,66) dp, hiện ngay. **Lần chạm X đầu không đóng ở 3/5 lần**, trong đó 1 lần mở Chrome. Back không đóng | `03-native-full-splash` |
| 4 | Hướng dẫn trang 1 "Tạo hoạt hình", nút "Tiếp theo" 345x50 dp ở đáy (y 783 dp) | | Tiếp theo | `04-tut1` |
| 5 | Trang 2 "Hình nền động" | | Tiếp theo | |
| 6 | **Native toàn màn hình**, hiện khi sang trang 3 (5/5 lần) | QC 4 | Chạm X 1 đến 2 lần | `05-native-full-tut` |
| 7 | Trang 3 "Hoạt hình phép thuật" | | Tiếp theo | |
| 8 | Trang 4 "Hoạt hình đường": **nút "Tiếp theo" dời lên giữa màn (y 379 dp, tím); chỗ nút cũ ở đáy là CTA của native, cùng tọa độ, cỡ (24,783 dp, 345x50 dp) và màu xanh lá với nút "Tiếp theo" của các trang trước** | QC 5 (native 361x250 dp) | Tiếp theo (nút đã dời) | `06-tut4-native-trap` |
| 9 | Trang 5 "Hiệu ứng hoạt hình", nút "Bắt đầu" ở đáy | | Bắt đầu | |
| 10 | **Interstitial Pangle** dạng playable, 2 quảng cáo nối nhau ("Quảng cáo tiếp theo >") | QC 6 và 7 | Nút đóng (`desc='Quảng cáo đã đóng'`, 28 dp góc trên phải) hiện sau **42, 64, 66 giây**; một lần quá 3 phút vẫn chưa có. Back không đóng | `07`, `08`, `10` |
| 11 | **Chọn ngôn ngữ**: 6 thẻ 361x70 dp (English, 한국인, Tiếng Việt, Portuguese, 日本, Français), không chọn sẵn; native 361x200 dp ở đáy (`lnAds1`) | QC 8 | Chọn English | `11-language` |
| 12 | Sau khi chọn: nút "Apply" 361x50 dp hiện ở y 575 đến 625 dp; **native được thay bằng quảng cáo mới (`lnAds2`)**, khung quảng cáo bắt đầu ở y 641 dp, cách đáy nút Apply 16 dp (nội dung quảng cáo từ y 657 dp, cách 32 dp) | QC 9 | Apply | `12-language-apply` |
| 13 | **Màn VIP** (bước bắt buộc, mục 6) | | X góc trên phải (39 dp, hiện ngay) | `13-vip-start` |
| 14 | Chọn chủ đề "Select at least 1 topic": 6 thẻ ảnh 2 cột (Animals, Line art style, People, Christmas, Cute, Cartoon); "Continue" mờ tới khi chọn | | Chọn Animals, Continue | `14-topics` |
| 15 | **Native toàn màn hình** sau Continue | QC 10 | X | |
| 16 | **Màn chính**: lưới mẫu theo chủ đề; **quảng cáo thu gọn (collapsible) có video** ở đáy, đang mở: từ y khoảng 573 dp tới đáy (khoảng 280 dp, một phần ba màn hình), che hàng mẫu thứ hai; nút thu gọn ˅ ở góc trên phải; không tự thu gọn trong 12 phút, chỉ đổi quảng cáo (TikTok, Paper.io 2, Romano) | QC 11 | (dừng ở đây, mục 9) | `17-home`, `18-resume` |

**Cộng dồn tới màn chính: 10 lần chạm nút của app (5 nút hướng dẫn, chọn ngôn ngữ, Apply, X của VIP, chọn chủ đề, Continue), chưa kể khoảng 6 đến 8 lần đóng quảng cáo; 11 quảng cáo, trong đó 5 toàn màn hình (1 interstitial AdMob, 3 native toàn màn hình, 1 interstitial Pangle gồm 2 quảng cáo) và 5 native trong trang (màn chờ, trang 4, 2 ở màn ngôn ngữ, màn chính); cộng 1 màn VIP.**

Cách cộng: bước 1 đến 10 lấy theo đợt 2, khớp với cả bốn lần đi của đợt 1 (cùng quảng cáo, cùng chỗ). Bước 11 đến 16 chỉ có ở đợt 2. Quảng cáo Pangle tính 2 vì hiện 2 quảng cáo liên tiếp. Không tính lại các quảng cáo của những lần mở lại ở đợt 1. Đợt 2 mở app lúc 20:03:05, tới màn chính khoảng 20:08, tức khoảng 5 phút nếu chờ hết quảng cáo.

**Chưa tới nét vẽ đầu tiên**: còn bước chọn mẫu hay tạo dự án, và theo APK có thể thêm interstitial hay native trước màn vẽ. Lời chê "8 quảng cáo trước khi vẽ được khung đầu tiên" thấp hơn số đo được.

Ghi chú:
- **Không hỏi tuổi.** Không có hộp thoại đồng ý quảng cáo (vùng Việt Nam). Không xin quyền nào tới màn chính.
- Hướng dẫn hiện theo ngôn ngữ máy (tiếng Việt) trước khi chọn ngôn ngữ; chọn English thì phần sau tiếng Anh.
- Nội dung quảng cáo: TikTok, game giải đố playable có hình ảnh người lớn (`07`), quảng cáo thời trang, video ca nhạc. Không lọc cho trẻ em.

## 3. Bản đồ màn hình

| Màn hình | Mục đích | Vào từ đâu | Ảnh |
| --- | --- | --- | --- |
| Màn chờ (TutorialActivity) | Tải dữ liệu, chờ quảng cáo | Mở app | `01-splash` |
| Hướng dẫn 5 trang (TutorialActivity, ViewPager) | Giới thiệu, chèn quảng cáo | Lần mở đầu | `04`, `06` |
| Ngôn ngữ (MainActivity, `fragment_language`) | Chọn ngôn ngữ | Sau hướng dẫn; Setting > Language | `11`, `12` |
| VIP (`fragment_vip_start`) | Bán thuê bao | Sau ngôn ngữ; nút PRO ở màn chính | `13-vip-start` |
| Chọn chủ đề (`fragment_select_topics`) | Cá nhân hóa mẫu | Sau VIP | `14-topics` |
| Màn chính (`fragment_home`) | Mẫu theo chủ đề, nút PRO, cài đặt | Sau chọn chủ đề | `17-home` |
| Hộp thoại thoát (`fragment_exit`) | "Thank you for enjoying our app !", nút "Stay here", "Exit Now" (329x53 dp), banner quảng cáo 392x61 dp ở đáy | Back ở màn chính | `16-exit-dialog` |
| Setting (`fragment_setting`) | Language, Rate app, Share with friends, "More apps (Ad)", banner 361x88 dp | Bánh răng ở màn chính | `15-setting` |

Luồng đã đi: `Màn chờ > Interstitial > Native toàn màn hình > Hướng dẫn 1 > 2 > Native toàn màn hình > 3 > 4 > 5 > Interstitial Pangle > Ngôn ngữ > VIP > Chủ đề > Native toàn màn hình > Màn chính`.

## 4. Tính năng

Thấy trên máy:
- **Mẫu vẽ theo** là nội dung chính của màn chính: thẻ mẫu 2 cột ("Bunny, Level: Medium, 12 frames", "Chubby Cat, Level: Medium, 8 frames"), icon tải về trên ảnh, **vương miện cam ở góc (mẫu VIP)**. Chip chủ đề cuộn ngang (Animals, Line art style, Basic...), chủ đề chọn ở onboarding đứng đầu.
- Setting chỉ có 4 mục: Language, Rate app, Share with friends, More apps (Ad). Không có mục quyền riêng tư, khôi phục giao dịch hay xóa dữ liệu.

Chưa thấy trên máy, theo APK ([apk-analysis.md](apk-analysis.md), mục 2): một màn vẽ không layer; 10 cọ ảnh, hình, chữ (8 phông), sticker; tô màu (màu thường, gradient, hoa văn); camera; tỉ lệ khung; fps; xuất MP4, GIF, hình nền động.

## 5. Chức năng chính

Chưa vào được màn vẽ (mục 9). Theo APK: số khung bị giới hạn, mở bằng rewarded (`AB_SHOW_REWARD_ADD_FRAME`, `dialog_unlock_add_frame`) hoặc VIP; native thu gọn trên màn vẽ (`AB_DRAW_USE_NATIVE_COLLAPSE`, `CTR_CLICK_COLLAPSE_DRAW_V2`). Bố cục màn vẽ, khoảng cách quảng cáo tới nút, onion skin, dòng thời gian, xuất video: chưa đo.

## 6. Kiếm tiền

| Loại quảng cáo | Vị trí | Thời điểm | Tần suất | Thời gian chờ đóng | Ảnh |
| --- | --- | --- | --- | --- | --- |
| Native 200 dp | Đáy màn chờ, 393x200 dp | Mở app | Mỗi lần mở | (không đóng) | `01` |
| Interstitial AdMob | Toàn màn hình | Hết màn chờ | Mỗi lần mở (5/5) | 2 đến 11 giây | `02`, `09` |
| Native toàn màn hình (`native_full_main` theo APK) | Toàn màn hình | Sau interstitial mở app; sau màn chọn chủ đề | Mỗi lần | X hiện ngay; **lần chạm X đầu thường không đóng, có lần mở Chrome** | `03` |
| Native toàn màn hình (`native_full_tut`) | Toàn màn hình | Giữa trang 2 và 3 hướng dẫn | Mỗi lần qua hướng dẫn | Như trên | `05` |
| Native trong trang | Trang 4 hướng dẫn, 361x250 dp; **CTA trùng chỗ, cỡ, màu nút "Tiếp theo" cũ** | Trang 4 | Mỗi lần | (không đóng) | `06` |
| Interstitial Pangle, 2 quảng cáo nối nhau | Toàn màn hình | Sau "Bắt đầu" | Mỗi lần qua hướng dẫn (5/5) | **42 đến 66 giây**, một lần quá 3 phút; back không đóng; nền end card bấm được | `07`, `08`, `10` |
| Native ngôn ngữ (`native_language_1`, `_2`) | Đáy, 361x200 dp; **đổi sang quảng cáo mới ngay khi chọn ngôn ngữ**; khung cách nút Apply 16 dp | Màn ngôn ngữ | 2 quảng cáo mỗi lần | (không đóng) | `11`, `12` |
| Quảng cáo thu gọn có video (collapsible) | Đáy màn chính, đang mở cao khoảng 280 dp (y 573 dp tới đáy), che hàng mẫu thứ hai; nút thu gọn ˅ | Màn chính, đổi quảng cáo khi quay lại app | Liên tục, không tự thu gọn trong 12 phút | (nút ˅, chưa chạm được) | `17`, `18` |
| Banner (Pangle, 392x61 dp) | Đáy hộp thoại thoát; nút "Exit Now" cách mép trên banner 16 dp | Back ở màn chính | Mỗi lần | | `16` |
| Quảng cáo resume | Không thấy: về màn hình máy 35 giây rồi mở lại, app về thẳng màn chính, chỉ đổi quảng cáo thu gọn | | | | `18` |
| Banner trong Setting | 361x88 dp dưới thanh trên (`imgBanner`; chưa rõ là quảng cáo hay ảnh của app) | Setting | | | `15` |

Nhận định (cho Frameloop tránh):
- **Bẫy bấm nhầm có chủ đích:** đổi chỗ nút "Tiếp theo" ở trang 4 để CTA quảng cáo nằm đúng chỗ ngón tay đã quen chạm ba lần.
- **Nút X của native toàn màn hình không đóng ở lần chạm đầu**, có lần mở trang quảng cáo. Khớp với khóa `CTR_CLICK_NATIVE_FULL_MAIN`, `CTR_CLICK_NATIVE_FULL_TUT`: app có vẻ chỉnh tỉ lệ biến lần chạm X thành lần bấm. Vi phạm chính sách AdMob.
- Native ở màn ngôn ngữ đổi quảng cáo đúng lúc nút Apply hiện ra ngay trên nó.
- Nút "Exit Now" của hộp thoại thoát nằm sát banner (16 dp).
- Quảng cáo thu gọn ở màn chính để ở trạng thái mở, chiếm một phần ba màn hình.
- Interstitial Pangle gần một phút mới đóng được, back không đóng.
- Màn chờ ghi "This action may contain ads.".

**Gói VIP** (`13-vip-start`, giá vùng Việt Nam; hiện bắt buộc sau màn ngôn ngữ, đóng bằng X góc trên phải, không phải chờ):
- Monthly **198.000 ₫/tháng**.
- **Weekly chọn sẵn, nhãn "Best value", chỉ ghi "Free, For today, Try it free for 3 days"; giá tuần không ghi ở đâu trên màn.** Dòng dưới: "Enjoy 3 days free, then pay weekly. Automatically renews until you cancel."
- Yearly **594.000 ₫/năm**.
- Nút "Continue" có icon VIP; Term of Service, Privacy Policy. Không bấm.
- Màn này không liệt kê quyền lợi (chỉ tiêu đề "VIP BENEFITS" và ảnh). Theo APK, VIP hứa "Full HD", "unlimited motion paths", không giới hạn khung; mẫu có vương miện là mẫu VIP.
- Màn chính có nút "PRO" (giỏ hàng, nền vàng) ở thanh trên.

## 7. Giao diện

- Theme sáng, nền xám rất nhạt; thẻ trắng bo góc khoảng 16 dp, có bóng.
- Màn chờ: chữ "DrawAnimation." 247x45 dp, ảnh động 190x200 dp, vòng tải 150 dp.
- Hướng dẫn: ảnh minh họa nửa trên (393x329 dp), chấm trang (chấm hiện tại là thanh tím 25 dp), tiêu đề, mô tả xám, thẻ dưới 361x250 dp chứa ảnh 329x184 dp và nút chính 345x50 dp bo tròn hẳn màu xanh lá. **Thẻ hướng dẫn và thẻ native dùng cùng khung, cùng nền, cùng nút xanh.**
- Ngôn ngữ: thanh trên 80 dp, tiêu đề giữa; thẻ 361x70 dp cách nhau 16 dp, cờ tròn 33 dp, radio 23 dp bên phải; nút Apply tím 361x50 dp.
- VIP: ảnh minh họa nửa trên 393x380 dp, kim cương cam 111 dp, tiêu đề "VIP BENEFITS" giữa hai nhành nguyệt quế, 3 thẻ giá rộng 117 dp (thẻ giữa cao 180 dp, viền tím, nhãn hồng "Best value"), nút Continue tím 361x60 dp.
- Chọn chủ đề: lưới ảnh 2 cột, nhãn dưới ảnh, "Continue" dạng chữ ở góc trên phải.
- Màn chính (ước từ ảnh kiểm tra, cây giao diện không lấy được): logo chữ "DrawAnimation." xanh dương bên trái, nút PRO vàng có giỏ hàng, bánh răng hồng; chip chủ đề tròn (chip chọn nền tím, chữ trắng); lưới thẻ mẫu 2 cột, thẻ khoảng 183x230 dp, tên đậm, "Level:" với mức màu cam, số khung chữ xám.
- Setting: thanh trên có nút back 45 dp, banner 361x88 dp, mục cao 60 dp có icon 23 dp.
- Màu theo APK: chính `#373877`, nền `#f5f4ff`; phông Inter. Trên ảnh: nút tím, nút hướng dẫn xanh lá, logo xanh dương (chưa lấy mã màu).

## 8. Điểm mạnh, điểm yếu

Mạnh:
- Mẫu vẽ theo có mức độ và số khung; chọn chủ đề lúc đầu để cá nhân hóa mẫu: người mới có ngay việc để làm.
- Minh họa đẹp, đồng bộ; có ngôn ngữ máy ngay từ đầu.

Yếu:
- **11 quảng cáo và 1 màn VIP trước khi tới màn chính**, khoảng 5 phút; 5 quảng cáo toàn màn hình, 1 interstitial chờ tới 1 phút.
- Bẫy bấm nhầm: CTA đặt đúng chỗ nút "Tiếp theo" cũ; X của native toàn màn hình không đóng lần đầu; native đổi mới khi nút Apply hiện.
- Back không đóng quảng cáo nào; hướng dẫn không bỏ qua được.
- Kẹt quảng cáo rồi mở lại app thì đi lại hướng dẫn từ đầu, kèm toàn bộ quảng cáo.
- Màn VIP giấu giá gói tuần, gắn "Best value" cho gói tuần.
- Quảng cáo thu gọn ở màn chính để mở, chiếm khoảng 280 dp, che hàng mẫu thứ hai.
- Không hỏi tuổi, không có hộp thoại đồng ý, nội dung quảng cáo không lọc cho trẻ em.

## 9. Chưa xem được

- **Từ màn chính trở đi:** mở mẫu, tạo dự án, màn vẽ (bố cục, cọ, tẩy, màu, tô màu, onion skin, native thu gọn và khoảng cách tới nút), dòng thời gian, giới hạn khung và rewarded mở khung, âm thanh, xuất video, watermark, quảng cáo khi mở và đóng dự án, khi phát, khi xuất, quảng cáo resume.
- **Lý do (đợt 2):** trên màn chính, `adb shell uiautomator dump` lỗi "could not get idle state" ở mọi lần thử (quảng cáo video ở đáy và ảnh động làm màn hình không bao giờ rảnh). Hàm `dump` của `ax.sh` bỏ qua lỗi và đọc lại `/sdcard/ax.xml` cũ, nên `tree` và bộ chặn dùng cây của màn trước (Setting, 20:09). Ba lần `adclose` liên tiếp chạm (952,222) theo cây cũ; một lần rơi vào nút bánh răng của màn chính (mở Setting). Không lần nào trúng quảng cáo, nhưng bộ chặn đang quyết định trên cây sai, nên tôi dừng chạm.
- **Đợt 3 (sau khi người sửa `dump`, 20:23 đến 20:36):** 16 lần lấy cây (`tree` hay `shot`), mỗi lần công cụ thử 3 lần cách 1 giây. **14 lần lỗi** (đều ở màn chính, kể cả sau khi chờ 7 đến 10 giây giữa các lần, sau khi mở hộp thoại thoát rồi quay lại, sau khi về màn hình máy 35 giây rồi mở lại), **2 lần thành công** (đều ở hộp thoại thoát). Quảng cáo thu gọn có video ở đáy không tự thu gọn, chỉ đổi quảng cáo, nên màn chính không bao giờ rảnh. Không chạm lần nào trên màn chính. Dừng theo giới hạn 15 phút.
- **Cần người làm:** một cách lấy cây không cần màn hình rảnh. Ví dụ: dịch vụ trợ năng, hay `uiautomator` qua thư viện chờ ngắn hơn. Hoặc người tự chạm nút ˅ thu gọn quảng cáo hay mở một mẫu. Ngoài ra, màn vẽ nhiều khả năng cũng có native thu gọn (`AB_DRAW_USE_NATIVE_COLLAPSE`), nên có thể gặp đúng lỗi này.
- Native 200 dp ở màn chờ: lúc chụp mới là khung shimmer.

**Góp ý cho công cụ:**
1. **Cây cũ khi `uiautomator dump` lỗi** (đợt 2). Đã sửa: đợt 3 công cụ báo "LỖI: không lấy được cây giao diện" và không chạm. Còn lại: **không lấy được cây khi màn có video chạy liên tục** (đợt 3); cần cách dump không chờ màn rảnh.
2. `adclose` in "đã đóng" mà không kiểm lại; lặp `adclose` theo cây cũ thì chạm vào nút của app. Nên so cây trước và sau.
3. Đợt 1: bộ chặn để lọt lần chạm `tvNext` dưới native toàn màn hình. Đã sửa: ở đợt 2, lần thử lại bị từ chối mã 4.
4. Đợt 1: `adclose` không nhận nút "Quảng cáo đã đóng" của Pangle. Đã sửa: ở đợt 2, `adwait 120` đóng được, báo 66 giây.
5. Đợt 1: `adcorner tr` chạm thấp hơn nút X của Pangle 60 px, rơi vào quảng cáo.
6. Đợt 1: GetApps của Xiaomi tự tải app khi mở (đã thêm vào danh sách tự back).
7. Một lần `shot` treo quá 120 giây khi native video đang phát.

## 10. Sự cố trong lúc khảo sát

`incidents.log` có 9 dòng mới ngày 2026-11-19:

1. **16:22:00:** chạm X (`imb_admob_closeAd`) của native toàn màn hình đầu tiên mở Chrome; công cụ đã back. X là nút đóng thật, nhiều khả năng là cơ chế `CTR_CLICK_NATIVE_FULL_*`.
2. **16:24:19:** native toàn màn hình hiện giữa trang 2 và 3; `tapid tvNext` chạm trúng CTA "Cài đặt ngay" (lỗi bộ chặn, đã sửa). GetApps của Xiaomi **tự bắt đầu tải TikTok (2%)**; đã tạm dừng và đóng thẻ. TikTok chưa cài. **Cần người hủy bản tải dở trong GetApps.**
3. **16:31:27, 16:36:54, 16:41:24:** interstitial Pangle không đóng được bằng công cụ; dừng app, mở lại (3 lần).
4. **16:45:34:** `adcorner tr` rơi vào nền end card của Pangle, mở Play; công cụ đã back.
5. **16:45:46:** dừng đợt 1.
6. **20:11:** `uiautomator dump` lỗi trên màn chính, công cụ dùng cây cũ; một lần `adclose` chạm vào nút cài đặt của màn chính. Dừng đợt 2.
7. **20:35:** 14 lần lấy cây ở màn chính đều lỗi; dừng đợt 3.

Đợt 2 và 3 không có lần mở trang ngoài nào, không có lần tải hay cài gì. Tổng cả phiên: 3 lần quảng cáo mở trang ngoài (Chrome, GetApps, Play), đều ở đợt 1.

File để lại trên máy: `ax.sh newfiles` báo "không có file mới trên máy" (chạy cuối đợt 1 và cuối đợt 3). Bản tải dở TikTok trong GetApps cần người hủy. App đang mở ở màn chính.
