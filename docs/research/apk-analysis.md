# Phân tích tĩnh APK: 6 app vẽ hoạt hình

Ngày: 2026-11-19. Bước 3 ("Đọc APK") của `clone-study.md`. Chỉ giải nén và đọc. Không chạy code trong APK, không tải gì từ máy chủ của app mẫu, không dùng điện thoại.

- APK gốc: `apks/<package>/` (base.apk và các split)
- Kết quả jadx: `decoded/<package>/jadx/`. Với ZenByte (Flutter), đọc thêm chuỗi trong `libapp.so` bằng `strings` (chỉ đọc).
- Mã đơn vị quảng cáo (`ca-app-pub-...`), khóa API, token: chỉ ghi có hay không, không chép.
- Các app viết tắt như trong `proposal.md`: FlipaClip, Banix, ZenByte, CEM, FlipArtify, Stickman.

## Bảng so sánh

### Thông tin chung và công nghệ

| | FlipaClip | Banix | ZenByte | CEM | FlipArtify | Stickman |
| --- | --- | --- | --- | --- | --- | --- |
| Package | `com.vblast.flipaclip` | `com.banix.drawsketch.animationmaker` | `com.zenbyte.flipanimation` | `com.cem.draw.animation` | `com.cem.flipartify` | `com.appolo13.stickmandrawanimation` |
| Phiên bản | 5.1.1 (982) | 10.8 (99) | 2.3.0 (53) | 2.1.1 | 1.6.5 | 6.2.7s (181) |
| minSdk / target | 32 / 36 | 23 / 36 | 24 / 36 | 32 / 37 | 24 / 36 | 32 / 36 |
| Cỡ APK (base + arm64) | 120 + 45 MB | 101 + 4 MB | 57 + 39 MB | 46 + 4 MB | 71 + 3,5 MB | 95 + 0,9 MB |
| Giao diện | View + ViewBinding, nhiều module, một ít Compose | View + Fragment + DataBinding | Flutter (Dart), bloc, go_router | Compose | View + Fragment | View + Fragment, Compose cho màn bài học |
| Lõi vẽ | Engine C++ riêng `libfc.so` (26 MB), Skia trên OpenGL ES (`SkiaConfigChooser`, `GLSurfaceView`) | Thư viện Doodle (`cn.hzw.doodle`) trên Canvas, tô màu bằng `libQueueLinearFloodFiller.so` | Flutter Canvas: nét vector, thêm lớp raster với shader `raster_dab.frag` | Canvas, engine chung với FlipArtify (lớp `Pvs*`, `HiPen*`) | Canvas, engine chung với CEM | Canvas + Bitmap (`DrawView`), tô màu `ForestFireFloodFillAlgorithm` |
| Low-latency ink (androidx.ink, graphics-core) | Không | Không | Không | Không | Không | Không |
| Mã hóa MP4 | Engine native (`native_startExport`) + `libffmpeg.so` + MediaCodec | MediaCodec `video/avc` tự viết, đổi Bitmap sang YUV | `flutter_quick_video_encoder` (MediaCodec H.264) | JCodec (Java thuần) | JCodec (Java thuần) | MediaCodec tự viết (`Mp4FrameMuxer`) |
| Mã hóa GIF | Chưa xác định (có thể trong engine native) | Bộ mã hóa GIF Java | Bộ mã hóa riêng, có `gif_alpha` (nền trong); thêm APNG | `libandroidndkgif.so` (waynejo) | `libandroidndkgif.so` | `AnimatedGifEncoder` (Java) |
| Định dạng xuất | MP4, GIF, PNG sequence, PNG khung | MP4, GIF, hình nền động | MP4 (tối đa 1080p với Premium), GIF, APNG | GIF, MP4, PNG từng khung | GIF, MP4 | MP4, GIF |
| Lưu dự án | Room (danh sách) + thư mục dự án, khung là file `.fcd` đặt tên theo SHA-256; sao lưu ra file `.fc` (ZIP) | JSON (Gson) các nét vector theo khung, PNG từng khung, trong `filesDir/data_draw/<thư mục>/` | sqflite, nét vector nén (`compact_strokes_codec`), gói `.fapk` (ZIP) để chia sẻ | Room, dự án là chuỗi JSON (`DraftEntity.jsonData`) + ảnh layer | Như CEM | SQLDelight (bảng `draw_project`), khung là ảnh trong thư mục dự án |
| Sao lưu | File `.fc` (bị khóa: xem quảng cáo hoặc Plus) | Không | Gói `.fapk`; cộng đồng trên mạng | Không | Không | Không |

### Tính năng

| | FlipaClip | Banix | ZenByte | CEM | FlipArtify | Stickman |
| --- | --- | --- | --- | --- | --- | --- |
| Cọ | 42 cọ (`preinstalled_brushes.json`); 4 chế độ vẽ, tẩy, làm mờ, nhòe; 4 kiểu chống rung | 10 cọ ảnh WebP, hình, chữ, sticker, mẫu tô | Khoảng 10 loại (pencil, inkPen, marker, crayon, calligraphy, watercolor, charcoal, airbrush, highlighter, eraser), xưởng tự tạo cọ | Khoảng 20 bút (`HiPen*`, `Pen*`) + 9 hình | Như CEM, ít hơn 9 lớp (không có neon, dashed, star) | Bút, tẩy, tô màu, sticker |
| Layer | 3 miễn phí, tối đa 10 (Remote Config); 29 chế độ hòa trộn, clipping mask, alpha lock, glow, pixel | Không có layer | Không giới hạn với Pro, đang mở miễn phí ("Unlimited layers are free for now") | Có (`Layer`, `maxLayerCount`) | Có | Không thấy |
| Giới hạn khung | Không giới hạn | Phải mở bằng rewarded hoặc VIP ("Unlock unlimited frames") | Miễn phí 150 khung/dự án, 4 dự án | Mở bằng Premium ("Unlimited frames") | Mở bằng Premium | Miễn phí 7 khung ("does not support more than 7 frames"), mở bằng rewarded hoặc trả tiền |
| Onion skin | Trước/sau riêng: số khung, bỏ qua khung, độ mờ đầu/cuối, có màu | Có thể ("Above frame / Below frame") | 0/1/2 khung, bật khi mở dự án | Có | Có | Có |
| Kích thước khung | Preset + tùy chỉnh W×H (báo lỗi lẻ, quá lớn, quá nhỏ); demo 1280×720, 12 fps | Tỉ lệ khung (9:16 cho hình nền) | Preset theo nhóm (`CanvasPresetGroup`) | Tùy chọn kích thước | Tùy chọn kích thước | Chọn kích thước, chọn định dạng |
| Công cụ khác | Lasso, tô màu (độ mạnh), thước (thẳng, oval, vuông, đối xứng), chữ, ảnh, hút màu, nhập video và âm thanh, Magic Cut (tách nền, cần tài khoản) | Camera, mẫu vẽ theo, hình nền động | In-between tự động, đối xứng, chọn vùng, biến dạng, cửa sổ tham chiếu video, thước | Vẽ theo video ("Turn videos into drawings"), hình | Mẫu vẽ theo (template) | AI tạo khung từ khung đầu ("Magic", máy chủ) |
| Âm thanh | Thư viện âm thanh (có gói premium), nhập âm thanh (khóa), AI audio, ghi âm | Chọn âm thanh | 573 file WAV hiệu ứng trong 21 nhóm, đóng gói sẵn; nhạc nền | Không thấy | Không thấy | Không thấy |
| Nội dung đóng gói | 42 cọ, 31 nền (giấy, cảnh, hoa văn), 36 phông, 2 dự án mẫu | 10 cọ, 3 nền, 1 gói 20 sticker, 8 phông | 23 bài học, 3 bộ truyện (8 + 10 + 20 tập) dựng bằng code, 6 phông | Mẫu nền, hướng dẫn Lottie | 16 mẫu vẽ theo (Easy 5, Medium 3, Hard 2, World Cup 6) | Khoảng 42 bài học (khung PNG + ảnh xem trước theo mức light, middle, hard), 13 ảnh sticker |
| Nội dung tải về | Bài viết, thử thách, cộng đồng, âm thanh | Mẫu, sticker, phông, hoa văn, nền: JSON trên GitHub raw và jsDelivr | Cộng đồng (bài đăng, remix) | Mẫu (`TemplateDatabase`, `ApiHelper`) | Không thấy | Bài học mới (thông báo), AI |
| Watermark | Có | Có | Có, bỏ bằng Premium | Có, bỏ bằng Premium | Có | Có, bỏ bằng trả tiền |

### Kiếm tiền và dịch vụ

| | FlipaClip | Banix | ZenByte | CEM | FlipArtify | Stickman |
| --- | --- | --- | --- | --- | --- | --- |
| Quảng cáo | App open, interstitial, rewarded, banner, native | App open, inter, rewarded, banner, native thu gọn (cả màn vẽ), native toàn màn hình | Không | App open, inter, rewarded, native thu gọn (cả màn vẽ), native toàn màn hình, "ads break" | Như CEM, 24 vị trí | App open, inter, rewarded, rewarded interstitial, banner |
| Mediation | XMediator (x3mads) + AdMob + AppLovin MAX; mạng: Meta, ironSource, Unity, Mintegral, Pangle, Vungle, InMobi, DT Fyber, Moloco, Verve, BidMachine, Ogury, Amazon, Yandex | AdMob + MAX; Meta, ironSource, Unity, Mintegral, Pangle, Vungle, InMobi, DT Fyber, Moloco, Bigo, myTarget | Không | AdMob (cả GMA next-gen) + MAX; Meta, ironSource, Unity, Mintegral, Pangle, Vungle, DT Fyber, Bigo | Như CEM | AdMob; AppLovin, ironSource, Vungle; Yandex là đường thay thế |
| Mã đơn vị trong APK | Có | Có | Không | Có (`ads_config.json`) | Có (`ads_config.json`) | Có (38 chỗ trong một lớp) |
| Firebase | Analytics, Remote Config, Crashlytics, Messaging, Auth, Firestore, Storage | Analytics, Remote Config, Crashlytics | Analytics, Crashlytics, Messaging, Auth | Analytics, Remote Config, Crashlytics, Messaging | Như CEM | Analytics, Remote Config, Crashlytics, Messaging |
| Phân tích khác | Amplitude, AppsFlyer, Adjust, MoEngage, OneTrust | Adjust, TikTok | Không | AppsFlyer, TikTok | AppsFlyer, TikTok | Không |
| Thanh toán | Play Billing + Purchasely (thuê bao FlipaClip Plus) | Play Billing (VIP tuần, tháng, năm, dùng thử 3 ngày) | RevenueCat (Premium) | Play Billing + Qonversion | Play Billing + Qonversion | Play Billing (mở khung, bỏ watermark, ưu đãi) |
| Màu chính | Hồng đỏ `#ff5079` trên nền trắng, chữ `#353a3e` | Xanh tím than `#373877` | Phong cách giấy và mực (ảnh giấy, bàn học, linh vật) | Hồng `#fb6a8d` | Vàng `#fecd2c` | Tím `#7f43ff`, nền `#eff3f6` |
| Phông giao diện | Inter, Poppins, Caveat | Inter | Pangolin, Balsamiq Sans, Bangers, Amatic SC, Pacifico | Inter, Poppins, Fredoka, Balsamiq Sans | Inter, Poppins, Nunito, Montserrat | Montserrat |

Ghi chú chung:
- **Không app nào dùng androidx.ink hay androidx.graphics:graphics-core (low-latency).** `androidx.graphics` có trong 4 app chỉ là `graphics-path` (thư viện đi kèm Compose).
- Chỉ FlipaClip có engine vẽ native (C++, Skia, OpenGL). Bốn app Android còn lại vẽ bằng `android.graphics.Canvas`. ZenByte vẽ bằng Canvas của Flutter, thêm shader cho cọ raster.
- CEM và FlipArtify cùng một mã nguồn (mục "App cùng mã nguồn").
- Mọi app có quảng cáo đều dùng nhiều mạng mediation và có id đơn vị trong APK. ZenByte là app duy nhất không có quảng cáo, chỉ thuê bao.

---

## 1. FlipaClip (`com.vblast.flipaclip`, 5.1.1)

**Quyền.** `RECORD_AUDIO` (ghi âm), `READ_EXTERNAL_STORAGE` (maxSdk 32), `WRITE_EXTERNAL_STORAGE` (maxSdk 29), `GET_ACCOUNTS`, `GOOGLE_AUTH.youtube` (đăng YouTube), `FOREGROUND_SERVICE_DATA_SYNC`, `FOREGROUND_SERVICE_MEDIA_PROCESSING` (xuất chạy nền), `POST_NOTIFICATIONS`, `AD_ID`, các quyền AdServices, Billing.

**Công nghệ.** Rất nhiều module (`feature_stage`, `feature_projects`, `feature_brushes`, `audiolib`, `adbox`, `billing_*`, `service_*`...). Giao diện chủ yếu View + ViewBinding, một phần Compose. Lõi vẽ là thư viện `com.vblast.fclib` gọi xuống `libfc.so` (26 MB): `StageCanvasView`, `Fc2Studio`, `Fc2SurfaceView`, `SkiaConfigChooser` (EGL cho Skia, OpenGL ES 2 hoặc 3, MSAA). Công cụ: `DrawTool`, `EyeDropperTool`, `FloodFillTool`, `LassoTool`, `TextTool`, `ImageTool`, thước (`Fc2RulerType`). Chống rung (`StabilizerType`): NONE, MOVING_AVERAGE, DOUGLAS_PEUCKER, ROPE_PULL. Chế độ cọ: DRAW, ERASE, BLUR, SMUDGE. 29 chế độ hòa trộn layer. Hỗ trợ S Pen và SonarPen. Thêm `libffmpeg.so` (11 MB), `libSDL2.so`, Pairip.

**Định dạng dự án (fc2).** File `.fc` là ZIP:
- `manifest.json` (`format_type: fc2`, `format_version`, checksum)
- `data/project.json` (tên, `canvas_width`, `canvas_height`, `fps`, màu nền)
- `data/tracks.json`, `track_items.json`, `track_item_contents.json`, `multi_tracks.json`: dòng thời gian nhiều track (layer vẽ, âm thanh), mỗi track có `position_key` dạng số thực để chèn giữa không phải đánh số lại
- `data/effects.json`, `key_frames.json`, `interpolators.json`: hiệu ứng (alpha, blend, glow, pixel, biến đổi vị trí, tỉ lệ, xoay) và keyframe
- `data/assets.json` + `assets/<2 ký tự>/<sha256>.fcd`: mỗi ảnh khung là một file nhị phân riêng (header `\x89FCD`, giống PNG), đặt tên theo mã băm, có `refcount`. Khung giống nhau dùng chung một file.

Danh sách dự án lưu trong Room (`ProjectEntity`). Có thư mục `ProjectsRecovery` và cảnh báo hết bộ nhớ khi tự lưu ("FlipaClip needs at least %d MB"). Sao lưu dự án ra file bị khóa: xem quảng cáo hoặc mua Plus.

**Nội dung đóng gói.** 42 cọ (`assets/brushes/*.fcb`, mỗi file là ZIP gồm `meta` và `data`); `accessType` 0 có 14 cọ, 1 có 12 cọ, 2 có 16 cọ; nhóm 0 là cọ dùng ngay, các nhóm còn lại có thể là cọ khóa (chuỗi "Watch an Ad to unlock all brushes": mở hết cọ trong vài giờ bằng một lần xem quảng cáo, hoặc Plus). Chưa xác định nghĩa từng giá trị. 31 ảnh nền (17 giấy, 4 cảnh, hoa văn, khác), 36 phông chữ cho công cụ chữ, 2 dự án mẫu (`demo1_v2.fc` "Flour Sack and Ball", 1280×720, 12 fps).

**Remote Config.** Bảng mặc định có `feature_layers_free_count` = 3, `feature_layers_max_count` = 10, `show_app_review_popup`, `welcome_flow_config`, `studio_config`, `feature_purchasely_paywalls_v2`... Quảng cáo đọc một JSON (`adbox`) với các khóa viết tắt: `st` (mặc định 1 giờ), `new_usr_imp_delay` (5 phút: người mới không thấy quảng cáo toàn màn hình trong 5 phút đầu), `new_usr_banner_delay` (5 phút), `ifc` (khoảng cách interstitial, 180 giây), `ifcm`, `dic` (6), `rw_*` (rewarded), danh sách đơn vị `au`, `rau`, `bau`. Onion skin có bộ tham số riêng cho chế độ thi đấu (`onion_*_contest`).

**SDK.** Như bảng. Đặc biệt: mediation XMediator (x3mads của Etermax) đặt trên AdMob và AppLovin MAX; tài khoản (Firebase Auth, Google), thi đấu và cộng đồng (Firestore, Storage), MoEngage (đẩy tin), OneTrust (đồng ý), Purchasely (paywall).

**Giới hạn và khóa.** 3 layer miễn phí, nhập video tối đa 6 giây miễn phí, nhập âm thanh và gói âm thanh premium khóa, sao lưu khóa, một số cọ khóa. Khung không giới hạn.

**Giao diện.** Material 3 nền sáng. Màu nhấn `#ff5079`, phụ `#8349ff`, `#4675f4`, `#5ab577`, `#f9c800`. Chữ `#353a3e`. Inter, Poppins, Caveat.

## 2. Banix (`com.banix.drawsketch.animationmaker`, 10.8)

**Quyền.** `CAMERA`, `SET_WALLPAPER`, `SCHEDULE_EXACT_ALARM` (nhắc hằng ngày), `READ/WRITE_EXTERNAL_STORAGE` (maxSdk 32), `MOUNT_UNMOUNT_FILESYSTEMS`, `POST_NOTIFICATIONS`, `AD_ID`, Billing, quyền AppHub của AppLovin.

**Công nghệ.** View + Fragment + DataBinding, Navigation (`nav_home`), MMKV, Room (chỉ bảng `PackUnlockTable`), Retrofit, Lottie, CameraX. Lõi vẽ là thư viện mã nguồn mở Doodle (`cn.hzw.doodle`, có cả `DoodleActivity`), vẽ trên Canvas. Tô màu bằng `libQueueLinearFloodFiller.so`. Video: lớp tự viết dùng MediaCodec `video/avc` + MediaMuxer, bitrate = rộng × cao. GIF: bộ mã hóa Java.

**Lưu dự án.** `filesDir/data_draw/<thư mục>/`: `DAM_json_draw.txt` (Gson, `List<List<DoodleItemBaseData>>`, tức nét vector theo từng khung), `DAM_json_setup.txt` (tỉ lệ, fps, mẫu), PNG từng khung, `DAM_thumb.webp`, `DAM_back.jpg`. Có thông báo "Your draft file is corrupted!". Không sao lưu.

**Nội dung.** Đóng gói: 10 ảnh cọ, 3 nền, 1 gói 20 sticker, 8 phông (Inter, Lobster, Modak, Baloo...). Tải về: mẫu, sticker, phông, hoa văn, nền từ các file JSON trên GitHub raw và jsDelivr (`Json/draw-animation-*.txt`). Có mẫu vẽ theo ("Draw According Template", mức Easy, Hard).

**Remote Config.** `AD_FLOW`, `AD_FLOW_SCRIPT`, `AD_MEDIATION_SWAP`, `AD_NETWORK`, `TIMER_OPEN_AD`, `TIMER_SHOW_FULL`, `TIME_RELOAD_COLLAPSE`, `AB_DRAW_USE_NATIVE_COLLAPSE` (native thu gọn trên màn vẽ), `AB_NATIVE_FULL_AFTER_OPEN`, `AB_SHOW_REWARD_ADD_FRAME` (rewarded khi thêm khung), `AB_FREE_TRIAL`, `AB_USE_NEW_VIP`, `ANIMATION_IS_USE_NATIVE_FULL`, `CTR_CLICK_COLLAPSE_HOME_V2`, `CTR_CLICK_COLLAPSE_DRAW_V2`, `CTR_CLICK_COLLAPSE_SETUP_V2`, `CTR_CLICK_COLLAPSE_SHARE_V2`, `CTR_CLICK_NATIVE_FULL_MAIN`, `CTR_CLICK_NATIVE_FULL_TUT`, `ANIMATION_CHANGE_DATA_*`. Các khóa `CTR_CLICK_*` cho thấy app chỉnh tỉ lệ bấm vào quảng cáo native thu gọn, kể cả trên màn vẽ. Vị trí native: `native_language_1/2`, `native_info_1/2`, `native_full_main`, `native_full_tut`. Có id đơn vị trong APK.

**Tính năng.** Một màn vẽ không layer; cọ, hình, chữ, sticker, tô màu (màu thường, gradient, hoa văn); camera; tỉ lệ khung; fps; xuất MP4, GIF, đặt làm hình nền động. VIP: dùng thử 3 ngày, tuần, tháng, năm; hứa "Full HD", "unlimited motion paths".

**Giao diện.** Màu chính `#373877`, nền nhạt `#f5f4ff`. Phông Inter. Nhiều bố cục khung chờ quảng cáo (`item_shimmer_ads_*` từ 100 tới 300 dp).

## 3. ZenByte (`com.zenbyte.flipanimation`, 2.3.0)

**Quyền.** Ít nhất trong 6 app: `INTERNET`, `POST_NOTIFICATIONS`, `RECEIVE_BOOT_COMPLETED`, `USE_BIOMETRIC`, Billing. Không có `AD_ID`.

**Công nghệ.** Flutter. Mã Dart nằm trong `libapp.so`, tên gói `flipanim`. Plugin: firebase_core, analytics, auth, crashlytics, messaging; google_sign_in; purchases_flutter (RevenueCat); sqflite; audioplayers; video_player; image_picker; file_picker; share_plus; in_app_review; flutter_local_notifications; flutter_quick_video_encoder. Không có SDK quảng cáo (gói `gms/ads` chỉ là phần lấy mã định danh).

Cấu trúc mã đọc được từ tên file Dart:
- `features/editor/bloc/`: `canvas_bloc`, `layers_cubit`, `layer_history`, `onion_skin_cubit`, `playback_cubit`, `timeline_cubit`, `tools_cubit`, `symmetry_transforms`, `selection_mask`, `quick_shape`, `deformation`
- `features/editor/raster/`: `raster_engine`, `raster_tiles`, `raster_dab`, `raster_live_painter` (cọ raster theo ô, có shader GLSL `raster_dab.frag`)
- `features/editor/inbetween/`: `inbetween_engine`, `inbetween_generator` (tự sinh khung giữa từ nét vector)
- `core/utils/`: `stroke_stabilizer`, `flood_fill`, `fill_worker` (tô màu chạy ở isolate), `lasso_clip`, `memory_guard`
- `data/database/`: `app_database` (sqflite), `compact_strokes_codec`, `stroke_sanitizer`
- `data/services/`: `export_service`, `apng_encoder`, `gif_alpha`, `export_branding` (watermark), `video_reference_service` (vẽ theo video)
- `data/community/package/`: `fapk_codec`, `fapk_zip` (gói dự án để chia sẻ, remix)

**Lưu dự án.** sqflite: bảng `projects` có cột `layers_json`; nét lưu dạng vector nén (`domain/models/stroke.dart`, `stroke_point.dart`). Khung là danh sách nét, không phải ảnh. Gói `.fapk` (ZIP) để xuất nhập.

**Nội dung đóng gói.** 573 file WAV hiệu ứng âm thanh trong 21 nhóm (foley 66, hits 65, cartoon 54, weapons 54, footsteps 35, nature 29...) + 21 file JSON danh mục. 23 bài học (ảnh xem trước: bouncing ball, pendulum, walking stick, blinking face...). 3 bộ truyện dựng bằng code (nhân vật mèo Mochi 8 tập, người que 10 tập, "King" 20 tập). Ảnh giấy, bàn học, linh vật 6 trạng thái. 6 phông OFL.

**Kiếm tiền.** Chỉ thuê bao (RevenueCat). Miễn phí: 4 dự án, 150 khung/dự án, có watermark; Premium: không giới hạn, video 60 giây 1080p, nhiều lớp âm thanh. Layer không giới hạn đang mở miễn phí (`FreeLayersSwitch`). Không quảng cáo.

**Khác.** Cộng đồng (đăng, theo dõi, thích, báo cáo, remix) qua Firebase Auth và máy chủ riêng. Bản địa hóa nhiều thứ tiếng (thấy chuỗi Indonesia, Đức, Ý, Bồ Đào Nha, Hà Lan, Thụy Điển, Tây Ban Nha).

**Giao diện.** Phong cách giấy và mực vẽ tay: nền giấy ngà, kẹp, đèn bàn, cốc bút, sổ; phông Pangolin, Balsamiq Sans. Có chế độ tối (logo `_dark`). Mã màu nằm trong mã Dart đã biên dịch, không đọc được.

## 4. CEM (`com.cem.draw.animation`, 2.1.1)

**Quyền.** `WRITE/READ_EXTERNAL_STORAGE` (maxSdk 28), `READ_BASIC_PHONE_STATE`, `POST_NOTIFICATIONS`, `AD_ID`, AdServices, Billing.

**Công nghệ.** Compose, một Activity. Engine vẽ `com.cem.draw.animation.draw` dùng chung với FlipArtify (69/78 lớp trùng tên): `pen/high/HiPen*` (Brush, Carbon, CarbonPencil, Circle, CircleSoft, Cloth, Crayon, CrayonDark, Hand, Mark, Oil, Pencil, Pixel, Soft), `pen/Pen*` (Chalk, Crayon, Erase, Leaf, Maobi, Mark, Steel, Distort...), thêm `pen/brush/` (Blur, Dashed, Dotted, Neon) và hình (Line, Rect, Oval, Triangle, Star, Pentagon, Arrow, Bezier). Lớp `Pvs*Layer` (ảnh, chữ, hình, nền). Lớp database vẫn tên `FlipArtifyDatabase`. MP4 bằng JCodec (bộ mã hóa H.264 viết bằng Java, chậm), GIF bằng `libandroidndkgif.so`.

**Lưu dự án.** Room: `DraftEntity` (`name`, `coverPath`, `jsonData`, `draftType`, thời gian); `jsonData` chứa danh sách layer (`Layer`: `fileName`, `alpha`, `isShow`), ảnh layer lưu file. Không sao lưu.

**Quảng cáo.** `assets/ads_config.json` (có id đơn vị): `fullscreen_interval` 30 giây, `open_interval` 5, `banner_interval` 10, `native_interval` 5, `countdown` 3 (đếm ngược trước "ads break"). 19 vị trí, đều AdMob: `native_splash`, `inter_inapp`, `inter_backup`, `inter_break`, `open_ads`, `aoa_backup`, `native_cls_home`, `native_home`, `native_fs_inter`, `native_fs_backup`, `native_preview`, `native_cls_draw` (native thu gọn trên màn vẽ), `reward_inapp`, `reward_backup`, `native_movie`, `native_backup`, `native_cls_backup`, `native_success`, `native_result`. Khóa Remote Config đọc trong mã: `banner_config`, `fullscreen_config`, `native_config`, `ctr_config`, `story_config`, `independent_native_ads`, `show_native`, `reload_interval`, `retry_interval`. Có Activity "native show open fake" (native giả quảng cáo mở app).

**Tính năng.** Frame, onion, fps, xuất GIF/MP4/PNG, vẽ theo video, hình kéo thả. Premium (Qonversion): bỏ quảng cáo và watermark, "Unlimited frames, templates & backgrounds". Play cho biết khóa sau 4 khung (proposal, mục 4).

**Giao diện.** Hồng `#fb6a8d`. Inter, Poppins, Fredoka, Balsamiq Sans, Irish Grover.

## 5. FlipArtify (`com.cem.flipartify`, 1.6.5)

**Quyền.** Như CEM, storage maxSdk 32.

**Công nghệ.** View + Fragment, Navigation (`main_graph`), Room, Lottie. Engine vẽ như CEM (bản cũ hơn, thiếu 9 lớp cọ và hình). MP4 bằng JCodec, GIF bằng `libandroidndkgif.so`. Màn hình: Splash, Language, Main, Project, ArtSpace (vẽ), Frame, Background, Template, Preview, Result, Purchase, Policy.

**Nội dung.** 16 mẫu vẽ theo trong `assets/Data_Art` (Easy 5: Animal, Halloween, Person, Shape, Xmas; Medium 3; Hard 2; World Cup 6), 603 file ảnh khung. Mẫu có `fpsDefault`, `level`, `category`.

**Quảng cáo.** `ads_config.json` cùng lược đồ với CEM, 24 vị trí (thêm `inter_splash`, `native_language`, `native_language2`, `native_onboard`, `native_onboard2`, `native_fs_onboard`), `reward_interval` 30. Cùng khóa Remote Config với CEM.

**Giao diện.** Vàng `#fecd2c`. Inter, Poppins, Nunito, Montserrat, SF Pro.

## 6. Stickman (`com.appolo13.stickmandrawanimation`, 6.2.7s)

**Quyền.** `CHANGE_WIFI_STATE`, `POST_NOTIFICATIONS`, `RECEIVE_BOOT_COMPLETED`, `AD_ID`, AdServices, Billing, license check. Khai báo `uses-feature camera`, màn hình dọc.

**Công nghệ.** View + Fragment (Navigation `nav_graph`) cho phần lớn màn hình, Compose cho bản đồ bài học kiểu Duolingo (`ui/duo_screen`). Kiến trúc chia `data`, `domain`, `shared/viewmodel` (cấu trúc chuẩn bị cho đa nền tảng, có `IsIosUseCase`). SQLDelight (`StickmanDatabase`: `draw_project`, `lessons`, `backgrounds`, `lock_gifs`, `lock_stickers_pack`, `human_animations`, `last_colors`). Vẽ bằng `DrawView` (Canvas, Bitmap, Path), tô màu `ForestFireFloodFillAlgorithm`. MP4 bằng `Mp4FrameMuxer` (MediaCodec), GIF bằng `AnimatedGifEncoder`.

**Lưu dự án.** Bảng `draw_project`: `name`, `folder`, `width`, `height`, `canvas_format`, `fps`, `count_frame`, `is_mp4_format`, `is_watermark`, `unlimited_frames`, cờ bài học và AI. Khung là ảnh trong `folder`. Không sao lưu.

**Nội dung.** Khoảng 42 bài học đóng gói dưới dạng chuỗi PNG trong `res/drawable` (light, middle, hard; ví dụ `middle_walk` 27 khung, `hard_dance` 18 khung), cộng các bài người que theo chủ đề (Santa, bóng đá, quyền anh...). Nhóm cấp độ: Stickman, Animals, Dances, Emotions, Stickman fights... Có điểm cấp độ người học. AI ("Magic") sinh khung từ khung đầu trên máy chủ, đang thử nghiệm.

**Quảng cáo.** AdMob là chính, adapter AppLovin, ironSource, Vungle; Yandex là đường thay thế (`AdManagerAndroidYandexImpl`). Id đơn vị viết trong mã, chia theo biến thể A/B (banner, inter, reward × control, A..G, mediation A/B). Khóa Remote Config: `AB_stickman_v` (biến thể), `AB_user_properties`, `is_yandex_stickman_v`, `is_server_on_stickman`, `is_mock_ad_stickman_v`, `privacy_policy`.

**Giới hạn.** Miễn phí 7 khung; mở một lần bằng rewarded ("Remove limitation once") hoặc mua "Unlimited frames forever"; bài học khóa theo thứ tự, mở sớm bằng xem quảng cáo; bỏ watermark trả tiền.

**Giao diện.** Tím `#7f43ff`, nền `#eff3f6`. Montserrat Bold.

---

## Nhận định

### App cùng mã nguồn

- **CEM và FlipArtify cùng một mã nguồn** của CEM Software (Hà Nội). Bằng chứng: 69 lớp engine vẽ trùng tên và cấu trúc gói (`draw/pen/high/HiPen*`, `draw/core/entity/Pvs*`), cùng module quảng cáo `com.cem.admodule`, cùng lược đồ `ads_config.json`, cùng khóa Remote Config, cùng chuỗi chính sách. CEM còn giữ tên lớp `FlipArtifyDatabase`. CEM là bản viết lại giao diện bằng Compose trên engine cũ, thêm vài cọ và hình.
- Bốn app còn lại có mã riêng, không chung engine hay thư viện quảng cáo nội bộ: FlipaClip (Visual Blasters, engine C++), Banix (Doodle mã nguồn mở), ZenByte (Flutter), Stickman (Appolo13/Appache).

### Cách làm lõi vẽ và lưu khung

Có hai trường phái:
1. **Lưu ảnh từng khung** (FlipaClip, Stickman, CEM, FlipArtify; Banix lưu cả hai). Dễ làm cọ texture và tô màu; tốn bộ nhớ, cần bộ nhớ đệm khung.
2. **Lưu nét vector** (ZenByte, phần chính của Banix). Nhẹ, hoàn tác dễ, cho phép in-between tự động; khó làm cọ texture và tô màu, phải dựng lại ảnh khi phát.

FlipaClip là app duy nhất đầu tư engine native, và là app duy nhất khử trùng khung bằng mã băm (khung giống nhau dùng chung một file). Đây cũng là lý do FlipaClip không cần giới hạn khung.

### Chiến lược quảng cáo thấy được

- Nhóm app nhỏ (Banix, CEM, FlipArtify) đặt native thu gọn ngay trên màn vẽ (`native_cls_draw`, `CTR_CLICK_COLLAPSE_DRAW_V2`) và có khóa chỉnh tỉ lệ bấm. Khớp với phàn nàn "quảng cáo chen vào lúc vẽ" trên Play.
- FlipaClip dùng quảng cáo toàn màn hình có nhịp: người mới 5 phút không thấy, sau đó cách nhau 3 phút. Rewarded dùng để mở từng món (layer, cọ trong vài giờ, nhập âm thanh, sao lưu).
- Stickman và Banix khóa số khung (7 khung, hoặc theo A/B), mở bằng rewarded.
- ZenByte chứng minh có thể làm app vẽ hoạt hình chất lượng chỉ với thuê bao, không quảng cáo.

---

## Gợi ý cho Frameloop

### Lõi vẽ

- **Không cần engine C++ như FlipaClip.** Bốn trên năm app Android vẽ bằng Canvas thường mà vẫn có hàng triệu lượt cài. Frameloop dùng Canvas trên một `Bitmap` riêng cho mỗi layer của khung đang vẽ, nét đang kéo vẽ thẳng lên một lớp tạm.
- **Độ trễ nét là điểm có thể thắng**, vì không app nào dùng low-latency. Thử `androidx.graphics:graphics-core` (`CanvasFrontBufferedRenderer`) trong prototype tuần đầu (proposal mục 5): vẽ nét đang kéo lên front buffer, xong nét mới gộp vào bitmap của layer. Nếu không đạt trên máy yếu thì lùi về Canvas thường, vẫn ngang các app mẫu. `androidx.ink` (bản 1.0) đáng xem cho phần dựng nét và cọ, nhưng thêm phụ thuộc lớn; quyết định sau khi đo.
- **Chống rung**: FlipaClip có 4 kiểu (trung bình trượt, Douglas-Peucker, "rope pull"). Bản đầu làm trung bình trượt và kéo dây, có thanh chỉnh độ mạnh.
- **Cọ**: 8 đến 10 cọ (proposal) là đủ để hơn Banix (1 cọ thực chất), gần CEM. Cọ texture dạng "dab" (đóng dấu ảnh dọc nét), như ZenByte và FlipaClip. Không cần 42 cọ như FlipaClip.
- **Tô màu đóng khe hở**: các app dùng flood fill thường (QueueLinear, ForestFire, scanline). Frameloop làm flood fill scanline chạy trên luồng nền, thêm bước nới vùng (dilate) để đóng khe 1 đến 4 px và tô lấn dưới nét. Đây là khác biệt đã chọn trong proposal.
- **Undo**: lưu thao tác dưới dạng ảnh vùng bị đổi (dirty rect) trước và sau, không lưu cả bitmap, để 50 bước hoàn tác không tốn bộ nhớ.

### Lưu khung

- **Lấy cách của FlipaClip, làm đơn giản hơn**: mỗi ảnh layer của mỗi khung lưu thành một file PNG hoặc WebP lossless, đặt tên theo mã băm nội dung (SHA-256), có đếm tham chiếu. Nhân bản khung, giữ khung, khung trống không tốn chỗ. Metadata (dự án, layer, thứ tự khung, fps, kích thước, âm thanh) lưu Room hoặc JSON.
- Dùng khóa thứ tự dạng số thực (`position_key` của FlipaClip) để chèn khung giữa mà không đánh số lại.
- RAM chỉ giữ khung đang vẽ, các khung onion skin và một cửa sổ quanh đầu phát; bộ nhớ đệm LRU theo cỡ byte. Đúng chỉ tiêu "200 khung × 5 layer" trong proposal.
- **Tự lưu và chống mất bài**: ghi file tạm rồi đổi tên (atomic), ghi thao tác theo nhật ký để khôi phục sau crash; kiểm dung lượng trống trước khi ghi và cảnh báo như FlipaClip.
- **Sao lưu ra một file**: dự án là một ZIP (`manifest.json` có phiên bản định dạng, `project.json`, thư mục ảnh theo mã băm, âm thanh), mở bằng bộ chọn file hệ thống. FlipaClip khóa tính năng này sau quảng cáo; Frameloop để miễn phí là khác biệt rõ.

### Xuất video và GIF

- **MP4**: MediaCodec H.264 với input Surface (vẽ từng khung bằng `lockCanvas` hoặc OpenGL lên Surface của encoder) + MediaMuxer. Không cần FFmpeg (FlipaClip mang 11 MB) hay JCodec (CEM, chậm). Media3 Transformer cũng được nếu cần ghép âm thanh; đo cỡ APK trước khi chọn.
- **Âm thanh**: ghép track AAC vào MediaMuxer; nhạc nhập từ người dùng giải mã bằng MediaExtractor.
- **GIF**: bộ mã hóa GIF chạy trên luồng nền, bảng màu tối ưu cho mỗi khung (NeuQuant hoặc median cut), hỗ trợ nền trong suốt (ZenByte có `gif_alpha`). Có thể dùng thư viện native nhỏ như `androidndkgif` của CEM nếu bản Kotlin quá chậm; đo ở tuần đầu.
- **PNG sequence**: ZIP các khung, như FlipaClip.
- Xuất chạy trong `ForegroundService` loại `mediaProcessing` (FlipaClip khai báo `FOREGROUND_SERVICE_MEDIA_PROCESSING`) nếu xuất lâu; proposal hiện để "xuất chạy nền" ở nhóm "không làm", nên bản đầu xuất ngay trên màn chờ xuất.
- Watermark vẽ lên từng khung lúc xuất, không lưu vào dự án.

### Nội dung và kiếm tiền

- **Âm thanh**: ZenByte đóng gói 573 WAV, khoảng 30 MB trong 57 MB base. Frameloop nên để thư viện âm thanh CC0 trên R2, tải khi cần, giữ APK nhỏ.
- **Bài học**: ZenByte (23 bài + truyện dựng bằng code) và Stickman (khoảng 42 bài là chuỗi PNG) cho thấy bài học là nội dung giữ chân. Frameloop đặt 6 đến 10 bài học dạng dự án mẫu (ZIP như định dạng sao lưu) trên R2, dùng lại trình mở dự án.
- **Quảng cáo**: không đặt native thu gọn trên màn vẽ như Banix và CEM. Học nhịp của FlipaClip: không quảng cáo toàn màn hình trong vài phút đầu của người mới, có khoảng cách tối thiểu giữa hai lần. Rewarded mở theo dự án, không theo từng món (proposal, mục 4).
- **Không khóa số khung** (Stickman 7 khung, CEM khoảng 4 khung là nguồn đánh giá xấu); ZenByte cho 150 khung miễn phí, FlipaClip không giới hạn.

### Quyền

- Đủ: `INTERNET`, `ACCESS_NETWORK_STATE`, `RECORD_AUDIO` (hỏi khi bấm ghi âm), `WRITE_EXTERNAL_STORAGE` với `maxSdkVersion=28`, `AD_ID`, `POST_NOTIFICATIONS` nếu có nhắc nhở. Ảnh và video nhập qua Photo Picker, xuất qua MediaStore: không cần quyền đọc bộ nhớ (FlipaClip, Banix còn khai báo tới SDK 32).
- Không cần `CAMERA` (Banix), `GET_ACCOUNTS` (FlipaClip), `SET_WALLPAPER`.
