<!-- Báo cáo thị trường do anh Nhật cung cấp ngày 2026-11-19 (bản gốc PhanTich_DrawAnimation.md, 19/10/2026). Dùng thay cho bước 4 "Tìm trên web và Play" của docs/ops/clone-study.md. -->

# PHÂN TÍCH ĐỐI THỦ VÀ ĐỀ XUẤT TÍNH NĂNG APP VẼ HOẠT HÌNH

**Ngày:** 19/10/2026 · **Phạm vi:** 21 app hoạt hình 2D trên Android (lật trang, người que, stop motion), số liệu AppBrain và Similarweb, tính năng trên trang store và trang hướng dẫn, review ở Mỹ, Anh, Úc, Canada, cùng nhận xét từ Common Sense Media và JustUseApp.

---

## 1. Bốn điều cần biết trước khi đọc danh sách

1. **Hai app chiếm phần lớn thị trường.** FlipaClip (670 nghìn tải/tháng) và Draw Animation của Banix (690 nghìn) chiếm khoảng 1,36 triệu trong 2,1 triệu lượt tải/tháng của các app đo được. Tầng thứ hai gồm 5 app, mỗi app 76 đến 260 nghìn. Phần còn lại đều dưới 30 nghìn.
2. **Lượt tải dồn về thị trường mới nổi, tiền Tier 1 đến từ app trả phí.** FlipaClip là app duy nhất mạnh thật ở Tier 1 (hạng 6 về mức sử dụng trong nhóm Nghệ thuật ở Mỹ). Banix chủ yếu có người dùng ở Philippines, Ấn Độ, Brazil, Malaysia và chỉ đứng hạng 81 ở Mỹ. Ở Tier 1, người dùng sẵn sàng trả tiền: Stick Nodes Pro ($3,99) đứng hạng 2 app trả phí nhóm Giải trí ở Mỹ; Stop Motion Studio và RoughAnimator cũng bán trả một lần.
3. **Người dùng nghiêng về lứa tuổi 8 đến 13.** Common Sense Media xếp FlipaClip 9+. Trong review có người viết "tôi 11 tuổi", "tôi 12 tuổi". Một app đối thủ còn tự quảng cáo "thân thiện với trẻ em". Đây là rủi ro lớn nhất với mô hình chỉ chạy quảng cáo (mục 6).
4. **Clone không có gì mới thì chết.** Trong các app ra mắt từ 2024 đến nay, chỉ Banix bứt lên được. Hai app của CEM đạt khoảng 4 triệu tải rồi đang tụt, điểm 2,9 đến 3,5. App của Fusion Flow ra 11/2025 đạt gần 1 triệu tải rồi rơi còn 4,6 nghìn/tháng. Vào ngách này phải thắng bằng chất lượng.

---

## 2. Danh sách app hiện có

Số liệu AppBrain cập nhật 08 đến 19/10/2026, là ước lượng mô hình. Chỉ số giữ nhịp = lượt tải 30 ngày ÷ (tổng tải ÷ số tháng từ khi ra mắt).

### Nhóm dẫn đầu

| # | App (nhà phát triển) | Ra mắt | Tổng tải | Tải/30 ngày | Giữ nhịp | Điểm (số đánh giá) | Tiền | Thị trường mạnh |
|---|---|---|---:|---:|---:|---|---|---|
| 1 | FlipaClip (Visual Blasters, Mỹ) | 2012 | 82 tr | 670K | 1,42 | 4,29 (646K) | QC + thuê bao Plus | **Tier 1**: hạng 18 đến 24 ở Anh, Mỹ, Canada; Editors' Choice |
| 2 | Draw Animation – Anim Creator (Banix, Thái Bình) | 02/2024 | 16 tr | 690K | 1,38 | 4,42 (64K) | QC + IAP | Philippines, Ấn Độ, Brazil; hạng cao ở Ý, Ba Lan, Pháp |

### Tầng thứ hai

| # | App (nhà phát triển) | Ra mắt | Tổng tải | Tải/30 ngày | Giữ nhịp | Điểm | Tiền | Ghi chú |
|---|---|---|---:|---:|---:|---:|---|---|
| 3 | Stickman: draw animation maker (Appache) | 2016 | 39 tr | 260K | 0,79 | 4,45 | QC, xem quảng cáo để mở thêm khung | Đông Âu, Brazil |
| 4 | Stop Motion Studio (Cateater) | 2015 | 22 tr | 160K | 1,01 | 4,11 | **Không QC**, Pro $5,99 | Tier 1, Teacher Approved |
| 5 | Stick Nodes (ForTheLoss) | 2014 | 17 tr | 100K | 0,86 | 4,67 | QC một lần lúc mở + bản Pro $3,99 | Bản Pro hạng 2 app trả phí Giải trí Mỹ |
| 6 | Animation Creator: Flipbook 2D (CEM) | 07/2024 | 4,1 tr | 100K | 0,66 | 3,39 | QC + Pro | Bị chê khoá sau 4 khung |
| 7 | FlipArtify (CEM) | 06/2024 | 4,5 tr | 76K | 0,47 | 3,12 | QC | Tự quảng cáo "thân thiện với trẻ em" |
| 8 | Draw Cartoons 2 (Zaliv) | 2015 | 20 tr | 53K | 0,35 | 4,59 | QC + IAP, PRO $9,99 | Không cập nhật từ 05/2025 |

### Nhóm nhỏ và mới

| # | App | Ra mắt | Tải/30 ngày | Giữ nhịp | Điểm | Ghi chú |
|---|---|---|---:|---:|---:|---|
| 9 | Draw Animation: Draw Cartoons (YakinGlobal) | 06/2024 | 26K | 1,45 | 3,84 | |
| 10 | Flip Animation (ZenByte) | **05/2026** | 23K | 1,6 | 4,14 | App mới đang lên |
| 11 | Draw animation (Haleema) | 04/2025 | 16K | 1,6 | 3,96 | |
| 12 | Draw Animation – Anime Maker (AppZone) | 05/2024 | 11K | 2,3 | 3,92 | |
| 13 | Animation Draw: Flip Maker (Fusion Flow) | 11/2025 | 4,6K | **0,05** | 4,48 | Bùng rồi sụp |
| 14 | ArtAnimator (codeBage) | 03/2026 | 4,2K | 2,4 | 3,57 | Mới, còn nhỏ |
| 15 | Animation Studio (Let's Draw) | 2021 | 3,7K | 0,36 | 3,29 | Đang tàn |
| 16 | RoughAnimator (WeirdHat) | 2014 | 1,0K | 2,1 | 4,44 | Trả phí $7,99, dành cho người làm hoạt hình nghiêm túc |

Thêm vài app đã chết hoặc gần chết: Apptacus, BetterTool, White Goose, Cartoon Video & Gif Maker. Animation Desk (Kdan) đã gỡ khỏi Play.

### App liền kề cần để ý

**Anijam: AI Video Maker** (ra 05/2026): người dùng gõ ý tưởng, AI dựng thành video hoạt hình. Không chạy quảng cáo, bán gói tín dụng và thuê bao năm, đã đứng **hạng 15 doanh thu nhóm Nghệ thuật ở Mỹ**. Đây là nhóm cạnh tranh mới về doanh thu Tier 1, nhưng dựa trên AI đám mây nên nằm ngoài bộ lọc của studio.

---

## 3. Đối thủ đang có gì

**Ký hiệu:** C = có, miễn phí · P = khoá sau gói trả phí · QC = mở bằng quảng cáo · không = không có · ? = không xác minh được.

| Tính năng | FlipaClip | Banix | CEM | Stick Nodes | Stop Motion Studio | RoughAnimator |
|---|---|---|---|---|---|---|
| Số khung | Không giới hạn, C | ? | **4 khung rồi P hoặc QC** (theo review) | n/a | Hàng trăm | Không giới hạn |
| Layer | 3 miễn phí; mỗi layer thêm đến 10 phải xem QC; gộp layer P | **Không có layer** | ? | n/a (khung xương) | Có | Không giới hạn |
| Onion skin | C | ? (bị chê không thấy khung trước) | C | C | Lớp phủ | C |
| Cọ | Nhiều loại, cảm ứng lực bút | **Một cọ, một tẩy** | Tuỳ chỉnh được | không | Cơ bản | Cọ tuỳ chỉnh, bút S-Pen |
| Tô màu | C, nhưng bị chê tô tràn qua khe hở | ? | ? | n/a | ? | Tô theo vùng chọn |
| Thước, hình, chữ | C | ? | ? | Hình, chữ | Chữ, bong bóng thoại | **Không có** (người dùng xin) |
| Nhập ảnh, video làm nền vẽ theo | C (video chiếm một layer) | ? | ? | Ảnh | C, có phông xanh | C (bị chê crash) |
| Âm thanh | 6 track; **nhập nhạc riêng phải P hoặc QC**; ghi âm giọng | ? | ? | Hiệu ứng âm thanh P | Nhạc, hiệu ứng, lồng tiếng | Nhập âm thanh |
| Khẩu hình theo lời thoại | Làm tay | không | không | không | không | Làm tay |
| Dòng thời gian | Nhân bản khung, giữ khung, lặp (bổ sung năm 2026) | ? | Xem trước | Tự nội suy chuyển động, camera ảo | Cắt, chép, dán | Chỉnh độ dài khung |
| Xuất | MP4, GIF C; chuỗi PNG P | GIF, MP4 | GIF, video | GIF C; MP4 P | MP4, GIF, ảnh | MP4, GIF, PNG nền trong |
| Watermark | Có sẵn; bỏ phải P | ? | ? | Có; bỏ phải P | ? | Không có |
| Lưu, sao lưu | Chỉ trong máy; **mất bài khi cài lại** | ? | ? | Lưu ra file | Chuyển qua Drive, Dropbox | Tương thích phần mềm máy tính |
| Cộng đồng, thi đấu | Thi hằng tuần, có giải | không | không | Chia sẻ nhân vật | không | không |
| AI | Lồng tiếng AI, tách vật thể (trong gói Plus) | không | không | không | không | không |

**Cách họ đặt quảng cáo:**
- **FlipaClip:** quảng cáo xen kẽ khi mở app, khi dừng phát, sau các thao tác ("3 quảng cáo trong chưa đến 5 phút"). Mỗi layer thêm phải xem quảng cáo, có khi phải xem 3 lần cho một tính năng, có lúc xem xong không được mở. Gói mua một lần đã bị thay bằng thuê bao, người dùng cũ rất bực.
- **Banix:** "8 quảng cáo trước khi vẽ được khung đầu tiên"; quảng cáo đặt sát các nút quan trọng gây bấm nhầm; quảng cáo che vùng khung hình.
- **CEM:** quảng cáo bật ra **ngay khi đang kéo nét vẽ**.
- **Stick Nodes:** chỉ một quảng cáo lúc mở app "rồi không bao giờ nữa", được khen nhiều nhất.

**Bài học:** người dùng chấp nhận quảng cáo ở chỗ ngắt tự nhiên; quảng cáo chen vào lúc đang vẽ là thứ bị phạt nặng nhất.

---

## 4. Người dùng than gì

Mười điểm đau chung:

1. Quảng cáo dày, chen vào lúc đang vẽ hoặc đang phát.
2. Quảng cáo đặt đè lên nút, gây bấm nhầm.
3. Tính năng cốt lõi bị khoá: layer, số khung, nhập nhạc, xuất MP4.
4. Mở khoá bằng quảng cáo theo từng món, phải xem nhiều lần, có khi không được mở.
5. **Mất bài:** dự án không lưu, mất khi cài lại app, không có sao lưu. Review mất bài của FlipArtify được 213 người đánh dấu hữu ích.
6. Crash hoặc chậm khi nhiều layer, nhiều khung, hoặc nhập video.
7. Tô màu tràn qua khe hở nhỏ, hoặc để lại viền trắng quanh nét.
8. Thao tác khung rườm rà: phải chạm từng khung, không chọn nhiều, không chép theo dải.
9. Thuê bao thay cho mua một lần.
10. Thiếu công cụ: thước, hình, chữ, làm mờ, camera ảo, khẩu hình, thêm cọ.

**Được khen:** miễn phí cho người mới, không giới hạn khung (FlipaClip), dễ vui khi chưa biết vẽ (Stick Nodes), ổn định (Stop Motion Studio), "không tính năng trả phí, không quảng cáo" (RoughAnimator).

---

## 5. Bộ tính năng đề xuất

### Định vị

**Xưởng hoạt hình miễn phí, đủ đồ nghề, không quảng cáo khi đang vẽ, không bao giờ mất bài.**

Nói gọn: những gì FlipaClip khoá sau thuê bao thì ta mở miễn phí hoặc mở bằng một lượt quảng cáo; và ta làm tốt hơn Banix ở đúng những điểm Banix bị chê (layer, cọ, quảng cáo).

### Tầng 1: bắt buộc cho bản đầu (ngang bằng thị trường)

| # | Tính năng | Ghi chú |
|---|---|---|
| 1 | Khung hình 16:9, 9:16, 1:1, tối đa 1080p; tốc độ 6 đến 30 khung/giây | Giới hạn 1080p để chạy được trên điện thoại |
| 2 | **Không giới hạn số khung** | Đánh vào giới hạn 4 khung của CEM |
| 3 | **5 layer miễn phí**, mở đến 10 layer bằng quảng cáo | FlipaClip cho 3, Banix không có |
| 4 | Onion skin chỉnh được số khung trước và sau, màu, độ mờ | |
| 5 | 8 đến 10 loại cọ (chì, mực, bút dạ, phun, cọ mềm...), nhận lực bút, chống rung nét; tẩy | Banix chỉ có một cọ |
| 6 | Bảng màu, hút màu, tô màu | |
| 7 | Chọn vùng, di chuyển, phóng to thu nhỏ, xoay; thước, hình cơ bản, chữ, vẽ đối xứng | |
| 8 | Dòng thời gian: thêm, xoá, nhân bản khung; **chọn nhiều khung, chép theo dải**; giữ khung; lặp đoạn; phát thử | Điểm đau số 8 |
| 9 | Hoàn tác nhiều bước, kể cả xoá khung và xoá layer | FlipaClip đến 2026 mới có |
| 10 | Nhập ảnh hoặc video làm nền để vẽ theo | Qua Photo Picker, không cần quyền |
| 11 | Âm thanh: **nhập nhạc riêng miễn phí**, ghi âm giọng, bộ hiệu ứng âm thanh CC0 | FlipaClip khoá nhập nhạc riêng |
| 12 | Xuất MP4 và GIF; watermark bỏ bằng một lượt quảng cáo | |
| 13 | **Tự lưu mỗi 30 giây**, khôi phục khi crash, danh sách dự án | Điểm đau số 5 |
| 14 | Nét vẽ độ trễ thấp; chạy mượt với 200 khung × 5 layer trên máy tầm trung | Lưu khung trên bộ nhớ máy, chỉ giữ trong RAM các khung gần đầu phát |
| 15 | Không tài khoản, chạy offline | |

### Tầng 2: khác biệt (lý do người dùng chọn ta)

| # | Tính năng | Vì sao |
|---|---|---|
| A | **Quảng cáo công bằng:** không quảng cáo khi đang vẽ, đang phát, hay đặt cạnh nút; mỗi lần xem quảng cáo mở khoá cho cả dự án hoặc 24 giờ, không mở từng món | Điểm đau số 1, 2, 4. Học theo cách Stick Nodes được khen |
| B | **Sao lưu dự án ra một file** (lưu vào máy hoặc Google Drive qua bộ chọn file hệ thống) và khôi phục từ file | Chống mất bài khi cài lại máy; không cần backend |
| C | **Tô màu tự đóng khe hở**, chỉnh độ mờ khi tô | Điểm đau số 7; app miễn phí trên điện thoại chưa ai làm tốt |
| D | **Camera ảo:** lia, phóng, xoay cả cảnh | Người dùng FlipaClip xin nhiều lần |
| E | Xuất chuỗi PNG và nền trong suốt, mở bằng quảng cáo | FlipaClip khoá sau thuê bao |
| F | **Bài học có sẵn trong app:** quả bóng nảy, nhân vật que đi bộ, nháy mắt | Thay cho cộng đồng; giúp người mới không bỏ app ngày đầu. Nội dung nhỏ, khoảng 6 đến 10 bài |

### Tầng 3: để bản sau

Gợi ý khẩu hình theo lời thoại chạy trên máy · lớp khung xương người que để tạo chuyển động nhanh · tự nội suy chuyển động · làm mờ, hiệu ứng phát sáng · 60 khung/giây · xuất định dạng cho phần mềm máy tính.

### Không làm

| Tính năng | Lý do |
|---|---|
| Cộng đồng, thi đấu, đăng tác phẩm công khai | Nội dung người dùng tạo ra kéo theo nghĩa vụ kiểm duyệt và an toàn trẻ em |
| AI dựng video từ câu lệnh (như Anijam) | Chi phí đám mây theo lượt |
| Tài khoản, lưu đám mây | Cần backend |
| Nhân vật hoặc skin của bên khác (Minecraft...) | Rủi ro bản quyền |
| Gắn từ "kids", "trẻ em" vào tên hay mô tả | Kéo app vào chính sách gia đình |
| Xuất video chạy nền | Phải khai báo foreground service |

---

## 6. Rủi ro trẻ em và cách xử lý

Đây là rủi ro lớn nhất của hướng này, cần anh nắm rõ trước khi quyết.

**Thực tế:** dù app khai đối tượng 13 tuổi trở lên, người dùng thật vẫn sẽ có nhiều em dưới 13. Mọi đối thủ đều đang khai xếp hạng "Everyone" và chạy quảng cáo bình thường; độ tuổi mục tiêu họ khai với Google không công khai.

**Chính sách Google Play:** nếu đối tượng mục tiêu có trẻ em, app phải có màn hỏi tuổi trung lập, chỉ được dùng SDK quảng cáo đã tự chứng nhận cho chương trình gia đình, và chỉ hiện quảng cáo không cá nhân hoá cho trẻ em. Google cũng xét hình ảnh trên store: hình vẽ kiểu trẻ con có thể bị coi là nhắm trẻ em.

**Cách xử lý đề xuất:**
1. Khai đối tượng mục tiêu 13 tuổi trở lên trong Play Console.
2. Hình ảnh store và ví dụ hoạt hình theo phong cách tuổi teen và người lớn, không dùng nhân vật hoạt hình dễ thương.
3. Không có từ "kids" trong tên, mô tả, từ khoá.
4. Dùng các SDK quảng cáo đã có chứng nhận cho chương trình gia đình ngay từ đầu, để nếu Google yêu cầu chuyển sang đối tượng hỗn hợp thì không phải làm lại.
5. Lập kế hoạch tài chính theo kịch bản eCPM thấp, vì một phần đáng kể người dùng sẽ là trẻ em và người ở thị trường mới nổi.

---

## 7. Bản đồ quảng cáo

| Vị trí | Định dạng | Quy tắc |
|---|---|---|
| Mở app | Quảng cáo mở app, từ lần mở thứ ba, tối đa một lần mỗi phiên | Học theo Stick Nodes |
| Danh sách dự án, thư viện bài học | Native | Không xuất hiện trong màn hình vẽ |
| Mở layer 6 đến 10, gói cọ, gói âm thanh | Rewarded, mở cho cả dự án hoặc 24 giờ | Một lần xem, một lần mở |
| Bỏ watermark, xuất chuỗi PNG, nền trong suốt | Rewarded khi xuất | |
| Màn hình chờ xuất | Native hoặc banner | Người dùng đang chờ |
| Sau khi xuất xong | Interstitial, giới hạn tần suất | Đặt sau khi file đã lưu an toàn |
| **Không bao giờ** | Trong lúc vẽ, lúc phát, đè lên khung hình hoặc dòng thời gian | Điểm đau số 1 và 2 |

---

## 8. Lịch làm việc gợi ý (8 đến 9 tuần)

| Tuần | Việc | Điểm kiểm tra |
|---|---|---|
| 1 | **Prototype lõi vẽ:** độ trễ nét vẽ, bộ nhớ với 200 khung × 5 layer, phát lại mượt trên máy cấu hình thấp | Không đạt thì dừng để cân nhắc lại |
| 2 đến 3 | Cọ, tẩy, layer, onion skin, hoàn tác | |
| 4 | Dòng thời gian: chọn nhiều, chép dải, giữ khung, lặp, phát | |
| 5 | Nhập nền vẽ theo, âm thanh, chữ, hình, thước, chọn vùng | |
| 6 | Xuất MP4 và GIF, tự lưu, sao lưu ra file | |
| 7 | Tô màu đóng khe, camera ảo, bài học | |
| 8 | Quảng cáo, khai báo độ tuổi, ASO, kiểm thử nhiều dòng máy | |
| 9 | Dự phòng, phát hành Úc và Canada trước | |

---

## 9. So với app ghép ảnh thành video

| | Vẽ hoạt hình | Ghép ảnh thành video |
|---|---|---|
| Nhu cầu | Bền, hai app dẫn đầu vẫn tăng | Bền, hơn 8 năm |
| Phiên dùng | Rất dài, giữ chân tốt | Ngắn, theo dịp |
| Người dùng | Nhiều trẻ em, nhiều thị trường mới nổi | Người lớn |
| Giá trị mỗi lượt quảng cáo | Thấp hơn | Cao hơn |
| Độ khó kỹ thuật | Cao (lõi vẽ, bộ nhớ) | Trung bình |
| Thời gian | 8 đến 9 tuần | 6 đến 7 tuần |
| Liên kết với app hiện có | Không | Cao (Wallpaper Maker) |
| Đối thủ | Hai app mạnh, phía dưới yếu | Đông, nhiều app khá |

---

## 10. Điều chưa xác minh được

- **Giá và nội dung gói Pro** của Banix và CEM.
- **Giới hạn 4 khung của CEM:** chỉ từ review.
- **Số cọ, kích thước khung miễn phí** của FlipaClip; FlipaClip trên Android có tính năng khung xương hay không.
- **Độ tuổi mục tiêu** các đối thủ khai với Google: không công khai.
- **Tỉ lệ người dùng dưới 13 tuổi:** suy ra từ review và Common Sense Media, không có số liệu chính thức.
- **Review:** mỗi trang Play chỉ hiện khoảng 3 review cho mỗi thị trường; phần còn lại lấy qua trang tổng hợp, mẫu nhỏ.
- Số liệu AppBrain là ước lượng bậc độ lớn; Similarweb không công bố tỉ lệ theo nước.

---

## Nguồn tham khảo

**Đối thủ**
- [FlipaClip](https://www.appbrain.com/app/flipaclip-draw-2d-animation/com.vblast.flipaclip) · [trên Google Play](https://play.google.com/store/apps/details?id=com.vblast.flipaclip&hl=en&gl=us) · [Similarweb](https://www.similarweb.com/app/google/com.vblast.flipaclip/)
- [Draw Animation – Anim Creator (Banix)](https://www.appbrain.com/app/draw-animation-anim-creator/com.banix.drawsketch.animationmaker) · [trên Google Play](https://play.google.com/store/apps/details?id=com.banix.drawsketch.animationmaker&hl=en&gl=us) · [Similarweb](https://www.similarweb.com/app/google/com.banix.drawsketch.animationmaker/)
- [Stickman: draw animation maker](https://www.appbrain.com/app/com.appolo13.stickmandrawanimation) · [Stop Motion Studio](https://www.appbrain.com/app/com.cateater.stopmotionstudio) · [Stick Nodes](https://www.appbrain.com/app/org.fortheloss.sticknodes) · [Stick Nodes Pro](https://www.appbrain.com/app/org.fortheloss.sticknodespro)
- [Animation Creator (CEM)](https://www.appbrain.com/app/animation-creator-flipbook-2d/com.cem.draw.animation) · [FlipArtify](https://www.appbrain.com/app/flipartify-2d-cartoon-maker/com.cem.flipartify) · [Draw Cartoons 2](https://www.appbrain.com/app/com.zalivka.animation2) · [Flip Animation (ZenByte)](https://www.appbrain.com/app/com.zenbyte.flipanimation) · [Animation Draw (Fusion Flow)](https://www.appbrain.com/app/animation-draw-flip-maker/com.animationlab.draw.animte.art) · [RoughAnimator](https://www.appbrain.com/app/com.weirdhat.roughanimator) · [Anijam](https://www.appbrain.com/app/com.seekoohk.anijamApp)
- [Bảng top free Nghệ thuật ở Mỹ](https://www.appbrain.com/stats/google-play-rankings/top_free/art_design/us)

**Review và độ tuổi**
- Common Sense Media (bài đánh giá FlipaClip) · JustUseApp (tổng hợp review FlipaClip): đọc qua công cụ tìm kiếm, không lưu được đường dẫn chính xác
- [Trung tâm hỗ trợ FlipaClip](https://support.flipaclip.com) · [Stop Motion Studio: bản miễn phí và Pro](https://help.stopmotionstudio.com)

**Kỹ thuật và chính sách**
- Tài liệu Android về nhập liệu bằng bút và LowLatencyCanvasView (developer.android.com) · [Photo Picker](https://developer.android.com/training/data-storage/shared/photo-picker)
- [Chính sách gia đình của Google Play](https://support.google.com/googleplay/android-developer/answer/9893335) · [Khai báo foreground service](https://support.google.com/googleplay/android-developer/answer/13392821)
