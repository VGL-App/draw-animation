---
package: com.vblast.flipaclip
name: FlipaClip
version: 5.1.1 (982)
surveyed_at: 2026-11-19
surveyed_on: máy thật Xiaomi (23021RAAEG), Android 15, 1080x2400, 440 dpi (393x873 dp)
network: có mạng (Wi-Fi, vùng Việt Nam)
status: draft
---

# APP MẪU: FLIPACLIP

<!--
Báo cáo khảo sát một app mẫu (ADR-0021, docs/ops/clone-study.md). AI viết, dev xem lại.
Mỗi nhận xét dẫn về ảnh (`NN-ten`, trong captures/com.vblast.flipaclip/ của thư mục làm việc, không vào repo) hay dòng cây giao diện.
Phiên chạy ba lần do sự cố công cụ (mục 10); lần mở đầu tính ở lần thứ ba, sau `pm clear`.
-->

## 1. Thông tin

FlipaClip của Visual Blasters (Mỹ), 82 triệu lượt tải, 670 nghìn lượt tải mỗi 30 ngày, điểm 4,29, kiếm tiền bằng quảng cáo và thuê bao Plus, Editors' Choice ([web-play.md](web-play.md), mục 2). Bản trên máy: 5.1.1 (982).

Từ danh sách màn hình của APK (`screens.md`): giao diện View và Navigation, một phần Compose (thanh công cụ của màn vẽ là `ComposeView`). Có hai trình vẽ: "Studio" (mới, mặc định) và "Classic" (Cài đặt > Animation Editor > New FlipaClip). Trả phí qua Purchasely. Hộp thoại đồng ý OneTrust. Quảng cáo qua XMediator; thấy trên máy Mintegral (`MBRewardVideoActivity`) và Pangle (`TTFullScreenExpressVideoActivity`).

## 2. Lần mở đầu

Sau `pm clear`, có mạng, chọn tuổi 25:

| # | Màn hình | Thao tác của người dùng | Ảnh |
| --- | --- | --- | --- |
| 1 | Splash: màn trắng rồi logo, khoảng 3 đến 6 giây | (chờ) | `01-launch` |
| 2 | Màn chào: video nền mờ, logo, "The #1 Animation App", nút "Get Started", "Have an account? Sign In" | Get Started | `02-welcome` |
| 3 | **Hỏi tuổi** "What's your age?" | Kéo thanh trượt tới 25, Continue | `03-age`, `03-age-25` |
| 4 | Quyền thông báo của hệ thống | Từ chối | `04-notif-permission` |
| 5 | **Màn trả phí FlipaClip Plus** toàn màn hình | Đóng bằng X | `05-paywall-onboarding` |
| 6 | Khảo sát "What brings you to FlipaClip? Pick one." (4 lựa chọn, nút Skip; chọn xong nút đổi thành Continue) | Chọn "I want to learn how to animate", Continue | `07-survey` |
| 7 | "Make your first animation! 20+ Million people made this, too!" có video mẫu, nút Play, "Discover more" và X | Discover more (vào tab Discover); X thì vào tab Home | `08-first-animation` |
| 8 | Màn chính, tab Discover | Chạm HOME | `09-discover`, `10-home` |
| 9 | Home: dự án mẫu "Demo" (2 cảnh) | Nút + giữa thanh dưới | `10-home` |
| 10 | Tạo dự án | Nhập tên, CREATE PROJECT | `11-new-project` |
| 11 | Màn vẽ (Studio), cọ Pen chọn sẵn | Vẽ nét đầu tiên | `15-studio`, `16-first-strokes` |

**Tới nét vẽ đầu tiên: 10 thao tác (đường ngắn nhất: Get Started, chọn tuổi, Continue, quyền thông báo, X paywall, Skip, X, +, đặt tên, Create), 1 màn trả phí toàn màn hình, 0 quảng cáo, không có hộp thoại đồng ý.** Log cho thấy OneTrust tắt banner đồng ý theo vùng ("Show Banner is disabled for current geolocation rule"), nên ở vùng khác có thể thêm một bước.

Ghi chú:
- **Hỏi tuổi trung lập:** thanh trượt ngang (khoảng 313x56 dp), núm tròn trắng có mũi tên `< >`, bóng số hồng phía trên. **Mặc định 0**, Continue mờ và không bấm được khi còn 0, không có nút bỏ qua. Dải khoảng 0 đến 99, mỗi tuổi khoảng 2,6 dp: chọn đúng một tuổi bằng ngón tay khá khó.
- **Màn trả phí là bước bắt buộc của onboarding:** phím back không đóng nó. Dừng app rồi mở lại thì nó hiện ngay khi mở, chưa qua màn chào (`06-paywall-relaunch`). Chỉ qua được bằng nút X (56x56 dp, góc trên trái, hiện ngay, không phải chờ).
- **Nút CREATE PROJECT không phản hồi khi tên dự án còn trống** và không báo lỗi gì; nhập tên xong thì tạo được. Chưa chắc tên có bắt buộc không, hay lần chạm đầu rơi vào lúc màn chưa sẵn sàng.
- Ở luồng tuổi 10 (mục 4), sau khi đóng paywall còn một hộp thoại "Please go to settings to enable notifications" (DENY, SETTINGS). Ở luồng tuổi 25 không thấy hộp thoại này, có thể vì lần đó app bị dừng rồi mở lại giữa chừng.
- Mở lại app khi đã qua onboarding: vào thẳng Home, không có màn trả phí, không có quảng cáo.

## 3. Bản đồ màn hình

| Màn hình | Mục đích | Vào từ đâu | Ảnh |
| --- | --- | --- | --- |
| Màn chào, hỏi tuổi, khảo sát, "Make your first animation" (StartupActivity) | Onboarding | Lần mở đầu | `02` đến `08` |
| Trả phí Plus (PurchaselyPaywallActivity) | Bán thuê bao | Onboarding, nút kim cương, bật tắt watermark | `05`, `06`, `46` |
| Home: Projects, Movies | Danh sách dự án và video đã xuất | Tab HOME | `10-home` |
| Discover: New!, Getting Started!, Challenges, Contest, Tutorials | Bài viết, thử thách, cuộc thi | Tab DISCOVER | `09-discover` |
| Watch (FlipsActivity) | Phim hoạt hình của FlipaClip, "Submit now" | Tab WATCH | `45-watch` |
| Search | Tìm kiếm | Tab SEARCH | (không xem) |
| Drawer cài đặt | Accounts, App Settings, About; Support, Community, Bugs | Nút menu góc trên trái | (cây giao diện) |
| Tạo dự án (EditProjectActivity) | Tên, nền, khổ hình, tốc độ khung | Nút + | `11` đến `14` |
| Màn vẽ (StudioActivity) | Vẽ, layer, khung, dòng thời gian | Tạo dự án, chạm dự án | `15` đến `35` |
| Onion (OnionSettingsActivity), Grid (GridSettingsActivity) | Cài đặt onion skin, lưới | Project options > Edit | `32`, `33` |
| Audio library, Audio Recorder, Voice maker (AI) | Âm thanh | Add audio | `36` đến `41` |
| Make movie (BuildMovieActivity) > Share (ShareMediaActivity) | Xuất video | Project options > Make movie | `42`, `43` |

Luồng chính: `Home > + > Tạo dự án > Màn vẽ > (vẽ, thêm khung, layer) > ⋮ > Make movie > Share > Đóng > Màn vẽ > X > Home (Movies)`.

## 4. Tính năng

**Tạo dự án** (`11` đến `14`):
- Tên dự án (placeholder "Name your animation").
- Nền: Presets (nhóm Scenes và Paper), Color, Gallery, Camera.
- Format, 10 khổ có sẵn: YouTube 1080p, YouTube 720p (mặc định), Instagram 16x9, Instagram 1x1, TikTok 1080p, TikTok 720p, Vimeo 1080p, Facebook 720p, Tumblr 16x9, Tumblr 4x3. Ngoài ra nhập tự do WIDTH, HEIGHT theo px, có khóa tỷ lệ.
- FPS: bộ chọn số cuộn (mặc định 12), có dòng giải thích "Currently you would need to draw 12 frames to make 1 second".

**Danh sách dự án:**
- Thẻ lưới 2 cột (188x220 dp), ảnh bìa, số cảnh, thời lượng, fps.
- Nhấn giữ một thẻ để vào chế độ chọn, thanh dưới có Edit, Share, Duplicate, Time-Lapse, Delete (`44-project-menu`).
- View Options: Project Details, Sort (Custom...).
- Tab Movies liệt kê video đã xuất.

**Cọ** (`18-brush-picker`), 37 loại:
- 13 miễn phí: Pen, Sketch, Airbrush, Crayon, Pencil, Brush, Highlighter, Gradient, Mosaic, Sparkles, Sprinkles, Static, Saw.
- 24 khóa: Pen (Soft), Pen (Choppy), Airbrush (Darker), Pencil H, Pixel, Ink, Spray, Stipple, Grain, Rough Pen, Caligraphy, Dip Pen, Halftone Left Hatch, Leaves, Hair, Fur, 3D Brush, 3D Candy Cane, Grunge, Dots, Squares, Dash, Gear, Heartbeat.
- Mở khóa: **một rewarded mở mọi cọ trong 4 giờ** (có đồng hồ đếm ngược trong bảng cọ, `21-brushes-unlocked`), hoặc Plus.
- Cài đặt cọ (Pen, `22-brush-settings`): Stabilizer (Beta) 20%, Thickness 6px, Opacity 100%, Color.

**Thanh bên của cọ** (`17-brush`):
- Brush, Size, Color, Ruler.
- Ruler (`27-ruler`): Line, Circle, Box, Mirror (vẽ đối xứng), Lock ruler. Đây chính là phần "thước và hình".

**Tẩy:** Size, Fade, Alpha, Ruler.

**Tô màu** (`24-fill-tool`, `25-fill-result`):
- Threshold, Color.
- Tô một vùng hở thì cả nền đổi màu; tô tam giác nhỏ khép kín thì được.
- Cài đặt có mục "Color Fill 2.0 (Beta)".

**Màu** (`23-color-picker`):
- Eye dropper, Add preset, Previous/Current color.
- Bộ màu "Basic" 5 ô và nút thêm.
- 5 chế độ: WHEEL, CLASSIC, HARMONY, VALUE, SWATCH.

**Các công cụ còn lại:**
- Lasso (chọn vùng): không có bảng tùy chọn riêng; chưa thử chọn rồi di chuyển.
- Text (chữ): chưa mở.
- More options: Smudge, Blur.
- Thanh dưới: Undo, Redo, Copy, Paste, Image/Video (nhập ảnh, video), Add audio, Clear.

**Layer** (`28-layers`, `29-layer-locked`):
- Bảng layer nhỏ nổi trên dòng thời gian: thumbnail, tên, nút ẩn, nút "+".
- **3 layer miễn phí.** Layer thứ 4 trở đi hiện "This feature is locked! You can unlock it by watching an ad or by getting FlipaClip Plus." với UPGRADE TO FLIPACLIP PLUS, WATCH AD, NO, THANKS.
- **Mỗi layer thêm là một rewarded:** xem xong được layer 4, thêm layer 5 lại bị khóa.

**Onion skin** (`32-onion-settings`):
- Bật tắt chung (mặc định bật), Color (tô màu khung trước sau), Loop.
- Frames before (mặc định 1), Frames after (mặc định 0).
- Mỗi bên có opacity đầu và cuối (50% đến 20%) và số khung bỏ qua (Skip, mặc định 0).
- Bật tắt nhanh trong menu ⋮ của màn vẽ.

**Lưới** (`33-grid-settings`): bật tắt (mặc định tắt), Line opacity 25%, Vertical line spacing 80px, Horizontal line spacing 80px.

**Dòng thời gian:**
- Thu gọn (`15-studio`): ô Layer (74x48 dp), dải khung thumbnail 86x52 dp có số khung, ô "+" thêm khung, nút Play 40 dp.
- Kéo tay nắm lên để mở dòng thời gian đầy đủ (`35-timeline`): đầu phát (lùi, Play, tới), mã thời gian `00:00.03`, thước số khung, mỗi layer một hàng; tiêu đề đổi "Studio" thành "Classic".
- Chạm khung đang chọn mở menu (`34-frame-menu`): Add (trước), Copy, PNG (xuất khung), Delete, Add (sau).
- Không thấy chọn nhiều khung ở chế độ này; chưa thử nhấn giữ khung.
- Phát và dừng: không có quảng cáo.

**Âm thanh** (`36` đến `41`):
- Add audio có 4 mục: Voice maker, Audio Library, Audio Recorder, Add Audio.
- Audio Library: 8 gói hiệu ứng (Fight, Fun, Machine, Human, Nature, Superhero, Toons, City), mỗi gói vài chục mẫu 2 đến 8 giây. Nghe thử được, nhưng **gói nào cũng gắn nhãn Plus**, nút thêm mờ.
- **Add Audio (nhập file của mình) khóa bằng rewarded** hoặc Plus ("Import your audio by watching an ad...").
- **Audio Recorder miễn phí** (nhấn giữ để ghi, `40-recorder`).
- Voice maker: chữ thành giọng nói bằng AI, tối đa 150 ký tự, 6 giọng (Ruberto, Bushelbeard, Astra-9, Johnny, Tex, Belladrix), tính bằng credit. Không thử vì sẽ gửi chữ lên máy chủ.

**Menu ⋮ của dự án:** Project settings, Onion skin (Edit, bật tắt), Grid (Edit, bật tắt), Magic cut (AI, Plus), Make movie.

**Xuất video** (`42-build-movie`, `43-share-after-build`):
- Tên phim, Output Size (theo khổ dự án), Format: MP4, GIF, PNG SEQUENCE, SPRITE SHEET.
- Transparent background (với MP4 không bật được).
- **FlipaClip watermark bật sẵn; tắt nó mở thẳng màn trả phí Plus**, không có lựa chọn rewarded.
- MAKE MOVIE (3 khung xuất gần như tức thì) mở màn Share: xem trước; Upload lên YouTube, TikTok, Facebook; More (bảng chia sẻ của hệ thống).
- Video nằm trong tab Movies của app. `ax.sh newfiles` không thấy file mới trong Pictures, DCIM, nên app không tự lưu vào thư viện máy.
- Không có quảng cáo trước, trong hay sau khi xuất.

**Khác:**
- Discover: Getting Started, Challenges, Contest (cuộc thi theo nhân vật), Tutorials, "QUICK TOUR".
- Watch: phim của FlipaClip, mời gửi phim.
- App Settings: Light and Dark Mode, Splash Animation, Animation Editor (New FlipaClip), Color Fill 2.0 (Beta), Make Movie File Override, Canvas Rotation, Stylus and Touch, Time-Lapse, Graphics Engine, Stylus Pressure, Stylus Hover Preview, Stylus Predicted Points (Beta), SonarPen.
- Có Accounts, Support, Community, Bugs, About; không mở.

**Luồng dưới 13 tuổi** (tuổi 10, `46-age10-paywall`, `47-age10-main`):
- Màn trả phí giống hệt, cùng giá.
- Thanh dưới chỉ còn HOME, +, DISCOVER (mất WATCH và SEARCH, tìm kiếm chuyển lên thanh trên).
- Discover vẫn có Contest.
- Cọ khóa vẫn mời **WATCH AD** như người lớn.
- Chưa so loại quảng cáo (có thể app gửi yêu cầu quảng cáo hợp với trẻ em; không thấy được từ giao diện).

## 5. Chức năng chính

Màn vẽ Studio (`15-studio`, số đo dp từ cây giao diện, khung 393x873 dp):

| Vùng | Vị trí, cỡ | Nội dung |
| --- | --- | --- |
| Thanh trên | y 34 đến 82, icon 24 dp trong ô 48 dp | X (đóng), kim cương Plus, nút chế độ "Studio"/"Classic" ở giữa, Share, ⋮ |
| Thanh công cụ nổi | y 103 đến 151, x 35 đến 359, bo tròn lớn, nền trắng | Tay nắm kéo (32x48), Brush, Eraser, Lasso, Color Fill, Text (icon 24, cách nhau 48), More options; công cụ đang chọn có nền hồng |
| Canvas | 16:9 rộng gần hết màn (khoảng 377x212 dp), giữa màn, nền giấy | |
| Thanh bên của công cụ | x 333 đến 381, từ y khoảng 291, kéo được | Brush/Size/Color/Ruler (cọ), Size/Fade/Alpha/Ruler (tẩy), Threshold/Color (tô) |
| Dòng thời gian thu gọn | y 737 đến 793, lề 8 dp, bo tròn | Ô layer 74x48, thumbnail khung 86x52, "+", Play 40 |
| Thanh dưới | y 801 đến 857, mục 40 dp có chữ 12 dp | Undo, Redo, Copy, Paste, Image/Video, Add audio, Clear (cuộn ngang) |

- Vẽ bằng `drag` cho nét liền, mượt. Cọ mặc định Pen 6px đen, chống rung 20%.
- Thoát màn vẽ (X): về Home, không hỏi lưu (tự lưu), không có quảng cáo. Mở lại dự án giữ nguyên nét vẽ.

## 6. Kiếm tiền

| Loại quảng cáo | Vị trí | Thời điểm | Tần suất | Thời gian chờ đóng | Ảnh |
| --- | --- | --- | --- | --- | --- |
| Rewarded (Mintegral) | Mở mọi cọ khóa trong 4 giờ | Khi người dùng bấm WATCH AD | Mỗi 4 giờ | Nút đóng khi đang phát hiện sau khoảng 3 giây; đóng sớm vẫn được thưởng; sau đó còn trang cuối (end card) có nút X | `20-rewarded`, `21` |
| Rewarded (Pangle) | Mở thêm một layer (từ layer 4) | Khi bấm WATCH AD | Mỗi layer một lần | Không thấy nút đóng trong 60 giây; quảng cáo **tự mở Play** (trang TikTok) không cần chạm; sau khi back mới có X trên end card | `30-pangle-rewarded` |
| Rewarded (chưa xem) | Nhập file âm thanh | Khi bấm Add Audio | | | `39-import-audio` |

- **Không thấy interstitial, banner, native, app open hay quảng cáo resume nào:**
  - Không có ở onboarding, Home, Discover, màn vẽ, lúc phát hay dừng, lúc thoát màn vẽ (3 lần mở và đóng dự án).
  - Không có trước hay sau khi xuất video.
  - Không có khi về màn hình chính 35 giây rồi quay lại.
  - Không có khi dừng app rồi mở lại.
- Lần đo này là người dùng mới trong ngày đầu, vùng Việt Nam. Review trên Play có nhắc quảng cáo xen kẽ ([web-play.md](web-play.md), mục 3), nên có thể app chỉ bật interstitial sau vài phiên hay theo vùng; cần đo lại ở phiên sau (mục 9).

Gói trả phí FlipaClip Plus (`05-paywall-onboarding`; giá theo vùng Việt Nam):
- Yearly 712.000 ₫/năm, chọn sẵn, nhãn "58% OFF", hiện thành 59.333,33 ₫/tháng.
- Monthly 142.000 ₫/tháng.
- Dùng thử 7 ngày ("Try for $0.00", "✓ No payment now", "Cancel anytime. Renews automatically.").
- Quyền lợi: No Ads, Unlock All Special Features, AI Tools (Voice AI + Magic Cut), No Watermark on Videos.
- Paywall hiện ở onboarding (bắt buộc đi qua), khi chạm nút kim cương, khi tắt watermark, và qua nút "UPGRADE TO FLIPACLIP PLUS" ở mọi hộp thoại khóa. Không bấm dùng thử.

Bảng khóa:
- Khóa bằng rewarded hoặc Plus: 24 cọ (một rewarded cho 4 giờ), layer thứ 4 trở đi (mỗi layer một rewarded), nhập âm thanh.
- **Chỉ Plus**: thư viện hiệu ứng âm thanh, bỏ watermark, công cụ AI.

## 7. Giao diện

- Theme sáng; màn vẽ nền xám nhạt, canvas giấy trắng ngà.
- Màu nhấn hồng đỏ: nút Get Started, công cụ đang chọn, nút +; nút chính onboarding khoảng `#ff5079`, nút tắt `#4c2e59`.
- Paywall nền tối, banner và nút "Try" màu hồng tím đậm (chưa lấy mã màu).
- Phông tròn hình học kiểu Poppins cho tiêu đề và nút (ước lượng từ ảnh); logo vẽ tay viền đen.
- Icon nét mảnh, viền đen 24 dp.
- Home:
  - Thanh trên 64 dp: menu, logo giữa, nút kim cương tròn gradient tím hồng.
  - Tab Projects, Movies có gạch chân hồng.
  - Lưới thẻ 2 cột.
  - Thanh điều hướng dưới 64 dp: HOME, DISCOVER, nút + tròn hồng 48 dp ở giữa, WATCH, SEARCH; chữ 10 đến 12 dp in hoa.
- Tạo dự án: danh sách dạng cài đặt, nhãn nhóm 48 dp, khung xem nền 361x330 dp với 4 nút tròn 48 dp, nút chính CREATE PROJECT 361x56 dp dưới đáy.
- Hộp thoại khóa: thẻ trắng 232 dp rộng, tiêu đề, mô tả, ba nút dọc 48 dp (Plus, Watch ad, No thanks).

## 8. Điểm mạnh, điểm yếu

Mạnh:
- Đồ nghề sâu: 37 cọ, ruler có Line, Circle, Box, Mirror, 5 kiểu chọn màu, onion skin chỉnh từng bên, lưới.
- Thanh công cụ nổi gọn, thanh bên theo công cụ.
- Không có quảng cáo chen ngang nào trong lúc dùng ở phiên này.
- Rewarded mở cọ cho 4 giờ là hào phóng.
- Hỏi tuổi trung lập ngay đầu.

Yếu:
- Onboarding dài (10 thao tác tới nét đầu), có paywall bắt buộc mà back không đóng được.
- Nút Create im lặng khi tên trống.
- **Mỗi layer thêm một rewarded** (đúng lời phàn nàn trên Play).
- Toàn bộ thư viện âm thanh khóa Plus.
- Nhập nhạc của mình phải xem quảng cáo.
- Watermark chỉ bỏ được bằng thuê bao.
- Rewarded của Pangle không có nút đóng trong 60 giây và tự mở Play: trải nghiệm tệ, dễ khiến trẻ em rời app.
- Tô màu vùng hở tràn cả nền.
- Chế độ Studio và Classic đổi tên ở giữa thanh trên khi kéo dòng thời gian, dễ lúng túng.
- Bàn phím tiếng Việt Telex đổi "Test1" thành "Tét1" (lỗi của máy, không phải app).

## 9. Chưa xem được

- **Interstitial, banner, app open, resume:** không xuất hiện trong phiên. Cần đo lại sau vài phiên hay vài ngày, hoặc với vùng khác (Mỹ, Úc), để biết app có bật chúng không.
- **Hộp thoại đồng ý OneTrust:** tắt theo vùng Việt Nam (log), không thấy.
- Công cụ Text, Lasso (chọn rồi di chuyển), Image/Video, Magic cut, chọn nhiều khung, cài đặt dự án, Time-Lapse.
- Xem rewarded nhập âm thanh; ghi âm thật (cần quyền micro); Voice maker (gửi chữ lên máy chủ).
- Xuất GIF, PNG; xuất video dài để thấy màn chờ.
- Search, Accounts, Challenges, Contest chi tiết.
- Luồng tuổi 10 chỉ xem tới màn chính và một hộp thoại khóa cọ.
- `screens.md`: App Settings mở trong StartupActivity, không phải SettingsActivity như đánh dấu.

**Góp ý cho công cụ:**
1. `guard.py`: id `exo_*` của ExoPlayer và `BILLING_ACTIVITY` quá rộng (đã sửa ở worktree `template-guard-fix` trong phiên này).
2. `adwait` không thấy nút đóng của Pangle (nút có `desc='Quảng cáo đã đóng'`, không id) và để quảng cáo tự mở Play; nên nhận thêm nút góc trên phải không nhãn ở activity quảng cáo, và tự back khi activity chuyển sang Play.
3. `seq` vỡ khi đối số có dấu ngoặc (`taptext Pen (Soft)`: "syntax error near unexpected token `('"); nên nhận chữ trong ngoặc kép hoặc thoát ký tự.
4. `type` bị bàn phím tiếng Việt Telex sửa chữ ("Test1" thành "Tét1"); nên đề nghị chuyển bàn phím sang tiếng Anh trong `check`.
5. `ax.sh` nên so cây giao diện trước và sau mỗi lần chạm và báo bằng mã riêng khi nhiều lần không đổi; `check` nên thử một lần chạm thật và đọc `dumpsys input` (sự cố con trỏ kẹt ở lần 1).
6. `shot` nên chờ màn hình đứng yên: ảnh ngay sau `launch` là màn trắng hay splash.
7. Không có lệnh nhấn giữ; `drag x y x+2 y+2 1200` dùng thay được.

## 10. Sự cố trong lúc khảo sát

`incidents.log` có 4 dòng mới ngày 2026-11-19:

1. **15:25:** máy không nhận chạm giả lập, vì một con trỏ kẹt trong `miui-gesture` từ khoảng 24 ngày trước. Người khởi động lại máy.
2. **15:33:** bộ chặn từ chối thanh trượt tuổi vì coi `exo_ad_overlay` là quảng cáo. Người sửa `guard.py`.
3. **15:41:** bộ chặn từ chối nút X của paywall vì tên activity có chữ "billing". Người sửa `guard.py`.
4. **15:59:** rewarded Pangle tự mở Play (trang TikTok) mà không có lần chạm nào; đã back. Không có lần chạm nhầm quảng cáo nào.

Ngoài ra:
- App không văng.
- USB không bị ngắt trong lần chạy cuối.
- Dữ liệu app trên máy hiện là luồng tuổi 10 (dự án "Kid"); dự án "Tét1" và video đã xuất bị xóa khi `pm clear`.
- File để lại trên máy (`ax.sh newfiles`): không có file mới.
