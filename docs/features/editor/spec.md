---
feature: editor
status: draft
owner: Nhật
product_owner: Nhật
requests:
  - requests/2026-11-21-ban-dau.md
design: design/README.md
created: 2026-11-21
confirmed:
---

# SPEC: MÀN VẼ

## 1. Mục tiêu

- **Người dùng được gì:** một xưởng vẽ hoạt hình lật trang đủ đồ nghề ngay trên điện thoại: cọ, layer, onion skin, dòng thời gian, âm thanh, camera ảo. Vẽ thoải mái, không quảng cáo nào bật lên giữa nét vẽ hay lúc phát, không bao giờ mất bài.
- **Vì sao làm bây giờ:** màn vẽ là lõi của app. Các app mẫu bị chê vì quảng cáo chen vào lúc vẽ, khóa tính năng cốt lõi, mất bài, tô màu tràn, thao tác khung rườm rà; app thắng bằng cách sửa đúng các điểm đó.
- **Đo kết quả bằng gì:** số lần phát thử và số khung thêm mỗi phiên; tỷ lệ người dùng xem quảng cáo có thưởng khi chạm giới hạn; các thước đo chất lượng ở KB-1, KB-4, KB-9, KB-10, KB-16 đạt trên bản staging.

## 2. Phạm vi

**Trong phạm vi:**

- Canvas theo tỷ lệ khung của dự án, thanh tiêu đề (quay lại, tên dự án, trạng thái lưu, hoàn tác, làm lại, Xuất), thanh công cụ dọc bên trái, cột tùy chọn bên phải, dòng thời gian ở đáy.
- Nút "thêm" (ba chấm) của thanh công cụ mở menu: hút màu, thước, vẽ đối xứng, nhập ảnh hay video, âm thanh, xem lại hướng dẫn (câu hỏi 1, 15).
- Cọ: 8 cọ cơ bản luôn miễn phí, chỉnh cỡ, độ mờ, chống rung, lực bút; gói 4 cọ nâng cao mở 24 giờ bằng một lượt xem quảng cáo có thưởng.
- Tẩy, chọn màu, hút màu, tô màu tự đóng khe hở và tô lấn dưới nét.
- Chọn vùng, di chuyển, thu phóng, xoay, lật, chép, xóa vùng chọn; thu phóng, xoay canvas bằng hai ngón; thước, hình, chữ, vẽ đối xứng.
- Layer: 5 miễn phí; layer 6 đến 10 mở cho cả dự án bằng một lượt xem quảng cáo có thưởng.
- Onion skin: số khung trước, sau, độ mờ, màu, lặp ở hai đầu.
- Dòng thời gian: thêm, xóa, nhân bản, chọn nhiều, chép và dán theo dải, giữ khung, đổi tốc độ khung, phát, lặp.
- Hoàn tác, làm lại nhiều bước, kể cả xóa khung, xóa layer.
- Nhập ảnh, video làm nền vẽ theo qua bộ chọn ảnh của hệ thống.
- Âm thanh: nhập nhạc miễn phí, bộ hiệu ứng cơ bản miễn phí, ghi âm giọng; gói hiệu ứng thêm mở 24 giờ bằng một lượt xem quảng cáo có thưởng.
- Camera ảo: lia, phóng, xoay cả cảnh.
- Tự lưu liên tục; mở lại sau khi app tắt đột ngột mất tối đa 30 giây công.
- Hướng dẫn trên màn vẽ 9 bước, bỏ qua được, xem lại được.
- Chỉ dẫn bài học trên màn vẽ: hiện từng bước của bài; nội dung bài do feature Bài học cung cấp.
- Dải quảng cáo gọn dưới thanh tiêu đề, bố cục không có dải khi vị trí tắt; lời mời xem quảng cáo có thưởng khi chạm giới hạn; quảng cáo xen giữa khi rời màn vẽ.
- Bố cục cho điện thoại gập mở ra.

**Ngoài phạm vi lần này:**

- Danh sách dự án, tạo dự án, sao lưu và khôi phục từ file (feature projects).
- Màn Xuất và lưu file vào thư viện máy (feature export). Màn vẽ chỉ mở màn Xuất.
- Dòng thời gian thu gọn: không làm ở bản đầu (câu hỏi 2).
- Thư viện bài học, tải bài, màn Hoàn thành bài học (feature lessons).
- Khẩu hình, khung xương người que, nội suy chuyển động, làm mờ, 60 khung/giây (bản đề xuất mục 5, nhóm "sau").
- Máy tính bảng (Q6).
- Chế độ ngoại tuyến: app luôn cần mạng; mất mạng do app chặn bằng popup chung.

## 3. Kịch bản sử dụng

Chữ trong ngoặc kép là chữ trên thiết kế (tiếng Anh, Q9).

### KB-1. Mở màn vẽ và bố cục

Người dùng mở một dự án từ danh sách hay ngay sau khi tạo dự án.

Tiêu chí chấp nhận:

- [ ] Khi màn vẽ mở thì có thanh tiêu đề (quay lại, tên dự án, dòng "Frame 12 / 48 · saved" cho biết khung hiện tại, tổng số khung và trạng thái lưu, hoàn tác, làm lại, nút "Export"), thanh công cụ 8 nút bên trái, cột tùy chọn bên phải (màu, cỡ cọ, độ mờ, layer), dòng thời gian ở đáy (chip onion skin, số khung, nút phát, chip tốc độ khung, nút nhân bản khung, dải 8 khung nhỏ và nút thêm khung), đúng thiết kế.
- [ ] Khi dự án có tỷ lệ 9:16 thì ở khung 411 × 891, canvas chiếm ít nhất 50% chiều cao màn (thiết kế: 288 × 512 dp, khoảng 57%), cả khi có dải quảng cáo lẫn khi không có.
- [ ] Khi dự án có tỷ lệ 16:9 hay 1:1 thì canvas giữ đúng tỷ lệ, vừa trong vùng làm việc, không bị cắt.
- [ ] Khi đo vùng chạm của mỗi mục ở cột tùy chọn bên phải (màu, cỡ cọ, độ mờ, layer) thì vùng chạm ít nhất 48 × 48 dp, dù biểu tượng nhìn thấy nhỏ hơn.
- [ ] Khi máy có màn 360 × 640 thì mọi nút vẫn dùng được, không bị che hay cắt; thanh công cụ bên trái và các bảng công cụ cuộn được khi nội dung dài hơn màn; canvas thu nhỏ, giữ đúng tỷ lệ (câu hỏi 18).
- [ ] Khi đo vùng chạm của mỗi nút ở thanh công cụ bên trái thì vùng chạm ít nhất 48 × 48 dp, dù hình nút là 44 dp như thiết kế (câu hỏi 21).
- [ ] Khi máy đang ở chế độ sáng hay tối thì màn vẽ theo đúng bản sáng hay tối của thiết kế.
- [ ] Khi cờ của feature Xuất tắt thì thanh tiêu đề không có nút "Export" (xem spec export KB-10).

Trường hợp đặc biệt:

- [ ] Khi mất mạng thì popup mất mạng chung của app hiện; màn vẽ không có chế độ ngoại tuyến riêng. Phần đã vẽ vẫn được tự lưu như KB-16.
- [ ] Khi xoay máy hay ứng dụng bị hệ thống dừng tạm rồi quay lại thì khung, layer, công cụ đang chọn giữ nguyên.

### KB-2. Vẽ bằng cọ, tẩy, chọn màu, hút màu

Tiêu chí chấp nhận:

- [ ] Khi chạm công cụ cọ lần nữa (lúc cọ đang được chọn) thì bảng "Brush" mở với 8 cọ cơ bản: "Pencil", "Ink", "Marker", "Airbrush", "Soft", "Pixel", "Calligraphy", "Chalk", cùng thanh "Size", "Opacity", "Stabilizer" và công tắc "Pressure from stylus".
- [ ] Khi vẽ bằng một ngón trên canvas thì nét theo cọ, cỡ, độ mờ, màu đang chọn hiện ngay trên khung và layer đang chọn.
- [ ] Khi kéo hai ngón trên canvas thì canvas di chuyển, thu phóng, xoay; không có nét nào được vẽ.
- [ ] Khi bật "Pressure from stylus" và vẽ bằng bút cảm ứng lực thì nét dày mỏng theo lực nhấn; tắt thì nét đều; công tắc này chỉnh riêng cọ đang chọn và thắng giá trị mặc định "Stylus pressure" của Cài đặt (câu hỏi 22).
- [ ] Khi cọ đang chọn chưa được chỉnh riêng lực bút thì công tắc "Pressure from stylus" hiện theo giá trị "Stylus pressure" của Cài đặt (câu hỏi 22).
- [ ] Khi tăng "Stabilizer" thì nét mượt hơn, bớt rung tay.
- [ ] Khi chọn tẩy và vẽ thì phần nét dưới tẩy bị xóa trên layer đang chọn, các layer khác không đổi.
- [ ] Khi chạm công cụ tẩy lần nữa thì bảng "Eraser" mở với "Size" (thiết kế: 24 px), "Softness" (0%) và công tắc "Erase on all layers" (tắt); bật công tắc thì tẩy xóa nét ở mọi layer tại chỗ tẩy (câu hỏi 23).
- [ ] Khi chạm ô màu ở cột phải thì bảng "Color" mở với 12 ô màu và thanh "Opacity"; chạm một ô thì màu đó thành màu đang chọn, ô có viền chọn.
- [ ] Khi chạm ô màu ở cột phải thì chọn được màu mới; khi dùng hút màu và chạm canvas thì màu tại điểm chạm thành màu đang chọn.
- [ ] Khi người dùng vẽ nét đầu tiên trên máy (ở bất kỳ dự án hay bài học nào) thì app nhớ mốc "nét vẽ đầu tiên" trên máy.

### KB-3. Mở gói cọ nâng cao

Tiêu chí chấp nhận:

- [ ] Khi bảng "Brush" mở mà gói chưa mở thì 4 cọ "Watercolor", "Glow", "Grain", "Spray" có dấu khóa.
- [ ] Khi chạm một cọ có khóa thì trong bảng hiện thẻ mời "Pro brushes", "Watch one ad to unlock the 4 pro brushes for 24 hours.", nút "Watch ad"; không có màn toàn màn hình nào tự mở.
- [ ] Khi bấm "Watch ad" và xem hết quảng cáo thì cả 4 cọ mở trong 24 giờ kể từ lúc đó, cọ vừa chạm được chọn.
- [ ] Khi đóng quảng cáo trước khi được thưởng thì cọ vẫn khóa, cọ đang dùng không đổi.
- [ ] Khi hết 24 giờ thì 4 cọ khóa lại; nét đã vẽ bằng cọ nâng cao giữ nguyên.
- [ ] Khi hết 24 giờ lúc đang chọn một cọ nâng cao thì người dùng vẽ tiếp được bằng cọ đó tới khi rời màn vẽ; lần mở sau cọ đang chọn về "Pencil" (câu hỏi 6).
- [ ] Khi mở gói cọ nâng cao thì cần đúng 1 lượt xem; khi dùng 8 cọ cơ bản thì không cọ nào bị khóa, kể cả lúc gói chưa mở hay đã hết hạn.

### KB-4. Tô màu không tràn

Tiêu chí chấp nhận:

- [ ] Khi chạm công cụ tô lần nữa thì bảng "Fill" mở với "Close gaps" (các mức "Off", "Small · 2 px", "Medium · 4 px" là mặc định, "Large · 8 px"; câu hỏi 8), dòng giải thích "Small gaps in your lines close automatically, so the color stays inside.", "Tolerance" (mặc định 20%), công tắc "Fill under lines".
- [ ] Khi tô vào một vùng có khe hở 1, 2 hay 4 px trên bộ ảnh thử, với "Close gaps" ở mức mặc định, thì màu không tràn ra ngoài vùng.
- [ ] Khi bật "Fill under lines" thì màu lấn dưới nét, không còn viền trắng giữa nét và màu.
- [ ] Khi tô vào vùng hở thật (khe lớn hơn mức đang chọn) thì màu tô theo vùng thật, có thể hoàn tác bằng một lần bấm hoàn tác.

### KB-5. Chọn vùng, hình, chữ, thước, vẽ đối xứng

Tiêu chí chấp nhận:

- [ ] Khi chọn công cụ chọn vùng và khoanh một vùng thì thanh thao tác nổi hiện cạnh vùng chọn với di chuyển, đổi cỡ, xoay, lật, chép, xóa; mỗi thao tác chỉ đổi phần trong vùng chọn.
- [ ] Khi chọn công cụ hình thì vẽ được hình cơ bản bằng một lần kéo.
- [ ] Khi chạm công cụ hình lần nữa thì bảng "Shape" mở với các loại "Line", "Rectangle", "Ellipse", "Arrow", thanh "Stroke" và công tắc "Fill shape"; bật "Fill shape" thì hình được tô màu đang chọn bên trong (câu hỏi 23).
- [ ] Khi chọn công cụ chữ thì bảng "Text" mở: ô nhập chữ, ba phông "Space Grotesk", "Inter", "Marker", thanh "Size" (thiết kế: 44 pt), nút "Done"; chữ hiện trên canvas và kéo đặt được chỗ.
- [ ] Khi bật thước thì nét vẽ bám theo đường thẳng của thước; dưới canvas hiện chip "Ruler on · tap to turn off", chạm chip thì thước tắt.
- [ ] Khi bật vẽ đối xứng thì nét vẽ ở một bên hiện đối xứng ở bên kia trục dọc giữa canvas (trục hiện nét đứt); dưới canvas hiện chip "Symmetry on · tap to turn off", chạm chip thì tắt.

### KB-6. Layer

Tiêu chí chấp nhận:

- [ ] Khi chạm biểu tượng layer ở cột phải thì bảng "Layers" mở, hiện số layer đang dùng trên giới hạn ("3 / 5"), danh sách layer có tay kéo, tên, nút ẩn hiện, và dòng "5 layers are free. Layers 6 to 10 unlock for this project with one ad.".
- [ ] Khi bấm thêm layer và dự án có dưới 5 layer (hay dưới 10 khi đã mở) thì layer mới được thêm và chọn.
- [ ] Khi kéo tay kéo thì thứ tự layer đổi và canvas vẽ theo thứ tự mới; khi bấm ẩn thì layer không hiện trên canvas và lúc phát.
- [ ] Khi dự án có 200 khung và 5 layer thì người dùng không gặp màn khóa hay lời mời nào.

### KB-7. Mở layer 6 đến 10

Tiêu chí chấp nhận:

- [ ] Khi bấm thêm layer thứ 6 thì hiện lời mời "Unlock layers 6 to 10", "Watch one short ad to use up to 10 layers in this project.", nút "Not now" và "Watch ad" trong hộp thoại như thiết kế (câu hỏi 3).
- [ ] Khi bấm "Not now" thì lời mời đóng, dự án giữ 5 layer.
- [ ] Khi bấm "Watch ad" và xem hết quảng cáo thì layer thứ 6 được thêm và dự án dùng được tới 10 layer mãi mãi; bảng "Layers" hiện "6 / 10".
- [ ] Khi mở layer 6 đến 10 trong một dự án thì cần đúng 1 lượt xem; khi thêm layer thứ 6 ở một dự án khác thì lời mời hiện lại, cần mở riêng.
- [ ] Khi đóng quảng cáo trước khi được thưởng thì không có layer nào được thêm.

### KB-8. Onion skin

Tiêu chí chấp nhận:

- [ ] Khi chạm chip onion skin thì bảng "Onion skin" mở: công tắc "Show onion skin", "Frames before" (mặc định 2), "Frames after" (mặc định 1), "Opacity" (mặc định 45%), màu "Before" (đỏ) và "After" (xanh), công tắc "Loop at the ends".
- [ ] Khi bật onion skin thì các khung trước hiện mờ màu đỏ, các khung sau hiện mờ màu xanh, đúng số khung và độ mờ đã chọn; chip hiện "Onion 2 · 1" theo số đã chọn.
- [ ] Khi bật "Loop at the ends" và đứng ở khung đầu thì khung cuối hiện như khung trước (và ngược lại).
- [ ] Khi đang phát thì onion skin không hiện.

### KB-9. Dòng thời gian

Tiêu chí chấp nhận:

- [ ] Khi bấm thêm khung thì khung trống mới được thêm sau khung hiện tại và thành khung hiện tại; không có giới hạn số khung.
- [ ] Khi chạm một khung nhỏ thì khung đó thành khung hiện tại, viền nổi bật, canvas đổi theo.
- [ ] Khi nhấn giữ một khung thì vào chế độ chọn nhiều: thanh "4 frames selected" (theo số khung chọn) với đóng, chép, nhân bản, giữ khung, xóa; khung được chọn có dấu chọn.
- [ ] Khi muốn nhân bản 10 khung liền nhau thì người dùng làm được trong tối đa 3 thao tác: nhấn giữ khung đầu rồi kéo dọc dải để chọn cả dải, rồi bấm nhân bản (câu hỏi 9).
- [ ] Khi chép một dải khung rồi dán thì cả dải được chèn sau khung hiện tại, đúng thứ tự, đủ mọi layer.
- [ ] Khi đặt giữ khung cho một khung thì khung đó hiện lâu hơn đúng số nhịp đã chọn lúc phát.
- [ ] Khi chạm chip tốc độ khung thì bảng "Frame rate" mở với các mức 6, 8, 12, 15, 24, 30 khung/giây và dòng thời lượng (ví dụ "12 frames per second · 48 frames play in 4.0 s"); chọn một mức thì tốc độ của dự án đổi ngay.
- [ ] Khi bấm giữ khung trên thanh chọn nhiều thì bảng "Hold frame" mở với các mức 1×, 2×, 3×, 4× cho các khung đang chọn.
- [ ] Khi xóa khung thì có thể lấy lại bằng hoàn tác.

### KB-10. Phát thử

Tiêu chí chấp nhận:

- [ ] Khi bấm phát thì hoạt hình chạy theo tốc độ khung của dự án, dòng phụ của tiêu đề thành "Playing · 12 fps" (theo tốc độ), nút phát thành nút dừng, chip "Loop" hiện, số khung chạy theo; thanh công cụ và cột phải ẩn.
- [ ] Khi bật "Loop" thì phát xong khung cuối quay về khung đầu; tắt thì dừng ở khung cuối.
- [ ] Khi dự án có 200 khung × 5 layer thì phát ở 12 khung/giây mượt trên máy tầm trung: ít nhất 95% khung hiện đúng nhịp trong 3 vòng phát, đo trên một máy tầm trung chốt ở prototype (câu hỏi 10).
- [ ] Khi dự án có âm thanh thì âm thanh phát khớp với khung.
- [ ] Khi đang phát thì không quảng cáo nào hiện lên (dải quảng cáo cố định ở trên không tính).

### KB-11. Hoàn tác, làm lại

Tiêu chí chấp nhận:

- [ ] Khi bấm hoàn tác thì thao tác gần nhất bị bỏ, kể cả nét vẽ, tô màu, biến đổi vùng chọn, thêm hay xóa khung, thêm hay xóa layer; bấm liên tiếp thì lùi được ít nhất 50 bước trong một lần mở dự án (câu hỏi 11).
- [ ] Khi bấm làm lại thì thao tác vừa hoàn tác được làm lại; khi đã có thao tác mới sau hoàn tác thì làm lại không còn.
- [ ] Khi không còn gì để hoàn tác hay làm lại thì nút tương ứng mờ đi và không làm gì.

### KB-12. Nhập ảnh, video để vẽ theo

Tiêu chí chấp nhận:

- [ ] Khi chọn nhập ảnh hay video thì bộ chọn ảnh của hệ thống mở, không hỏi quyền nào.
- [ ] Khi chọn một ảnh thì ảnh hiện làm nền để vẽ theo, dưới các layer vẽ.
- [ ] Khi chọn một video thì video được chia theo tốc độ khung của dự án và hiện làm nền theo từng khung; video dài hơn 10 giây thì chỉ lấy 10 giây đầu (câu hỏi 12).
- [ ] Khi có ảnh hay video để vẽ theo thì lớp này hiện mờ, có công tắc ẩn hiện, và không có trong file xuất (câu hỏi 12).
- [ ] Khi vừa chọn xong ảnh hay video, hoặc chọn lại mục nhập trong menu "thêm" khi đã có lớp vẽ theo, thì bảng "Reference" mở với "Opacity" (mặc định 40%), công tắc "Show reference" và nút "Remove reference"; bấm "Remove reference" thì lớp vẽ theo bị bỏ, nét vẽ không đổi.
- [ ] Khi đóng bộ chọn mà không chọn gì thì màn vẽ không đổi.

Trường hợp đặc biệt:

- [ ] Khi file không đọc được thì báo lỗi ngắn trên màn vẽ, dự án không đổi.

### KB-13. Âm thanh

Tiêu chí chấp nhận:

- [ ] Khi mở bảng "Sound" thì thấy danh sách âm thanh của dự án (nhạc, giọng ghi âm, hiệu ứng; mỗi dòng có tên và chỗ đặt, ví dụ "Music · from 0:00 · 4.0 s", "Recorded · frames 8 to 20", "Sound effect · frame 12") và ba nút "Music", "Record", "Effects".
- [ ] Khi bấm "Music" thì bộ chọn file của hệ thống mở; chọn một bài thì bài được thêm vào dự án, miễn phí.
- [ ] Khi bấm "Record" lần đầu thì app hỏi quyền ghi âm; cho phép thì ghi được giọng, giọng được đặt từ khung hiện tại.
- [ ] Khi bấm "Effects" thì chọn được hiệu ứng trong bộ cơ bản miễn phí; hiệu ứng được đặt tại khung hiện tại.
- [ ] Khi từ chối quyền ghi âm thì app báo cần quyền để ghi âm; nhạc và hiệu ứng vẫn dùng được; từ chối hẳn thì bấm "Record" hiện lời giải thích ngắn và nút mở cài đặt quyền của app (câu hỏi 13).

Trường hợp đặc biệt:

- [ ] Khi bộ hiệu ứng chưa tải được thì bảng báo lỗi và có nút thử lại; nhạc và ghi âm vẫn dùng được.

### KB-14. Mở gói hiệu ứng âm thanh

Tiêu chí chấp nhận:

- [ ] Khi mở bảng "Sound" mà gói chưa mở thì mục "Effect packs" hiện các gói có dấu khóa ("Cartoon", "24 sounds · boing, pop, swoosh"; "Ambience", "16 sounds · rain, city, forest").
- [ ] Khi chạm một gói có khóa thì trong bảng hiện thẻ mời "All effect packs", "Watch one ad to use every effect pack for 24 hours. Your music and voice stay free.", nút "Watch ad"; không có màn toàn màn hình nào tự mở.
- [ ] Khi xem hết quảng cáo thì mọi gói hiệu ứng mở trong 24 giờ.
- [ ] Khi đóng quảng cáo trước khi được thưởng thì các gói vẫn khóa.
- [ ] Khi hết 24 giờ thì các hiệu ứng từ gói đã đặt vào dự án vẫn phát và có trong file xuất; chỉ không đặt thêm được hiệu ứng mới từ gói (câu hỏi 7).
- [ ] Khi gói hiệu ứng chưa mở hay đã hết hạn thì nhập nhạc, ghi âm và bộ hiệu ứng cơ bản vẫn dùng được, không có dấu khóa.

### KB-15. Camera ảo

Tiêu chí chấp nhận:

- [ ] Khi chọn công cụ camera ảo thì lia, phóng, xoay được cả cảnh ở từng khung, không đổi nét vẽ trên các layer.
- [ ] Khi phát thì cảnh chuyển động theo camera ảo đã đặt.
- [ ] Khi tắt camera ảo hay xóa điểm đặt thì cảnh về vị trí gốc.
- [ ] Khi chạm công cụ camera ảo lần nữa thì bảng "Camera" mở với khung đang đặt, "Zoom" (thiết kế: 120%), "Rotate" (0°), công tắc "Camera on" và nút "Reset camera"; bấm "Reset camera" thì cảnh của khung đó về vị trí gốc.

### KB-16. Tự lưu và khôi phục

Tiêu chí chấp nhận:

- [ ] Khi người dùng vẽ thì app tự lưu, không cần bấm lưu; dòng phụ của tiêu đề hiện "saved" khi đã lưu xong.
- [ ] Khi app tắt đột ngột giữa lúc vẽ thì mở lại dự án mất tối đa 30 giây công.
- [ ] Khi rời màn vẽ, bấm Xuất hay chuyển app sang nền thì app lưu xong trước.

Trường hợp đặc biệt:

- [ ] Khi bộ nhớ máy đầy và không lưu được thì báo cho người dùng, app không dừng đột ngột, phần đang vẽ vẫn còn trên màn.

### KB-17. Hướng dẫn trên màn vẽ

Tiêu chí chấp nhận:

- [ ] Khi lần đầu vào một dự án thường (không phải bài học; câu hỏi 15) thì hướng dẫn 9 bước hiện: thẻ chỉ dẫn có số bước "1 / 9", tiêu đề, lời chỉ dẫn (ví dụ "Draw on the canvas", "Pick a brush on the left, then draw with one finger. Two fingers move and zoom."), nút "Skip tour" và "Next"; lớp tối phủ phần màn vẽ dưới dải quảng cáo, không phủ thanh tiêu đề và dải quảng cáo.
- [ ] Khi bấm "Skip tour" ở bất kỳ bước nào thì hướng dẫn đóng ngay và không tự hiện lại.
- [ ] Khi qua hết 9 bước thì hướng dẫn đóng và không tự hiện lại.
- [ ] Khi chọn mục xem lại hướng dẫn trong menu của nút "thêm" thì hướng dẫn hiện lại từ bước 1 (câu hỏi 15).

### KB-18. Chỉ dẫn bài học trên màn vẽ

Người dùng chọn một bài ở feature Bài học; bài mở trên màn vẽ như một dự án dựng sẵn.

Tiêu chí chấp nhận:

- [ ] Khi bài mở thì tiêu đề hiện tên bài và bước ("Lesson 1 · Bouncing ball", "Step 3 of 6"), nút đóng thay cho nút quay lại, không có nút "Export".
- [ ] Khi đang ở một bước thì thẻ chỉ dẫn hiện tên bước, số bước ("3 / 6"), lời chỉ dẫn, nút "Show me" và "Next step"; nét dẫn màu đỏ hiện trên canvas.
- [ ] Khi bấm "Next step" thì sang bước sau; ở bước cuối thì sang màn Hoàn thành bài học của feature Bài học.
- [ ] Khi bấm "Show me" thì app cho xem cách làm bước đó bằng một đoạn mẫu của bước đó phát trên canvas, xong thì trả về khung đang vẽ (câu hỏi 16).
- [ ] Khi đang làm bài thì mọi công cụ vẽ dùng được như ở dự án thường; bài không xuất hiện trong danh sách dự án và không tính vào số dự án.
- [ ] Khi bấm nút đóng (hay nút back của máy) lúc bài chưa xong thì app tự lưu xong rồi về màn Bài học, không về danh sách dự án; có thể hiện quảng cáo xen giữa như rời màn vẽ thường, và bước đang làm được giữ để lần sau mở tiếp (câu hỏi 17).

### KB-19. Dải quảng cáo trên màn vẽ

Tiêu chí chấp nhận:

- [ ] Khi vị trí dải quảng cáo bật thì dải cao khoảng 56 dp nằm ngay dưới thanh tiêu đề, cách hàng nút của thanh tiêu đề và cách canvas mỗi bên ít nhất 16 dp, ở mọi trạng thái của màn vẽ (kể cả lúc phát, lúc có hướng dẫn, lúc học bài).
- [ ] Khi quảng cáo đang nạp hay nạp lỗi thì dải vẫn giữ chỗ; canvas không đổi chỗ khi quảng cáo nạp xong hay lỗi.
- [ ] Khi vị trí tắt hay người dùng chưa đồng ý quảng cáo thì màn vẽ dùng bố cục không có dải, canvas dời lên.
- [ ] Khi người dùng vẽ, phát, hay mở canvas, dòng thời gian, các bảng công cụ thì không quảng cáo nào ngoài dải này nằm trong các vùng đó hay hiện lên, và không có màn toàn màn hình nào do app tự mở trên màn vẽ (quảng cáo khi quay lại app không tính).
- [ ] Khi chữ hệ thống ở cỡ lớn thì dải vẫn cao như cũ, nhãn "Ad" luôn thấy, chữ nút của quảng cáo không bị cắt.

### KB-20. Rời màn vẽ, mở màn Xuất

Tiêu chí chấp nhận:

- [ ] Khi bấm quay lại (nút trên thanh tiêu đề hay nút back của máy) ở một dự án thường thì app tự lưu xong, có thể hiện một quảng cáo xen giữa theo khoảng cách tối thiểu giữa hai lần, rồi về danh sách dự án. Rời bài học giữa chừng thì về màn Bài học (KB-18, câu hỏi 17).
- [ ] Khi người dùng chưa vẽ nét đầu tiên nào trên máy thì rời màn vẽ không có quảng cáo xen giữa.
- [ ] Khi bấm "Export" thì app tự lưu xong rồi mở màn Xuất của dự án.

### KB-21. Điện thoại gập mở ra

Tiêu chí chấp nhận:

- [ ] Khi máy gập mở ra (khung 673 × 841) thì bố cục theo thiết kế màn gập: thanh công cụ bên trái, canvas ở giữa, bảng bên phải có "Layers" và tùy chọn "Brush" ("Size", "Opacity") luôn hiện, dòng thời gian ở đáy, dải quảng cáo trên cùng rộng hết màn.
- [ ] Khi gập lại hay mở ra giữa lúc vẽ thì khung, layer, công cụ đang chọn giữ nguyên, không mất nét đang vẽ dở đã nhấc tay.

### KB-22. Tắt màn vẽ mới từ xa

Tiêu chí chấp nhận:

- [ ] Khi cờ của feature tắt mà người dùng dùng bất kỳ lối nào mở màn vẽ (chạm một dự án, bấm "Open" trong menu dự án, bấm "Create", chạm một thẻ bài học hay bấm "Next lesson") thì app hiện thông báo ngắn "Coming soon" và ở lại màn đang đứng, không mở màn vẽ, không tạo dự án (câu hỏi 20).

## 4. Giao diện

- **Thiết kế:** [design/README.md](design/README.md) (Figma, duyệt 2026-11-20). Mỗi trạng thái có bản sáng và tối.
- **Các màn hình:**

| Màn hình | Trạng thái | Mục đích | Kịch bản liên quan |
| --- | --- | --- | --- |
| `EditorScreen` | drawing | Vẽ, có dải quảng cáo | KB-1, KB-2, KB-6, KB-9, KB-11, KB-12, KB-15, KB-16, KB-19, KB-20 |
| `EditorScreen` | drawing-no-ad | Vẽ, vị trí quảng cáo tắt | KB-1, KB-19 |
| `EditorScreen` | drawing, gập mở ra | Bố cục màn gập | KB-21 |
| `EditorScreen` | tour | Hướng dẫn trên màn vẽ | KB-17 |
| `EditorScreen` | tool-brush | Bảng cọ | KB-2 |
| `EditorScreen` | brush-locked | Mời mở gói cọ nâng cao | KB-3 |
| `EditorScreen` | tool-fill | Bảng tô màu | KB-4 |
| `EditorScreen` | tool-select | Thanh thao tác vùng chọn | KB-5 |
| `EditorScreen` | tool-text | Bảng chữ | KB-5 |
| `EditorScreen` | layers | Bảng layer | KB-6 |
| `EditorScreen` | layer-locked | Mời mở layer 6 đến 10 | KB-7 |
| `EditorScreen` | onion-settings | Bảng onion skin | KB-8 |
| `EditorScreen` | timeline-multiselect | Chọn nhiều khung | KB-9 |
| `EditorScreen` | playing | Phát thử | KB-10 |
| `EditorScreen` | sound | Bảng âm thanh | KB-13 |
| `EditorScreen` | sound-locked | Mời mở gói hiệu ứng | KB-14 |
| `EditorScreen` | lesson-step | Chỉ dẫn bài học | KB-18 |
| `EditorScreen` | more-menu | Menu của nút "thêm" | KB-2, KB-5, KB-12, KB-13, KB-17 |
| `EditorScreen` | tool-eraser | Bảng tẩy | KB-2 |
| `EditorScreen` | tool-shape | Bảng hình | KB-5 |
| `EditorScreen` | color-picker | Bảng màu | KB-2 |
| `EditorScreen` | camera | Bảng camera ảo | KB-15 |
| `EditorScreen` | frame-rate | Chọn tốc độ khung | KB-9 |
| `EditorScreen` | hold-frame | Giữ khung | KB-9 |
| `EditorScreen` | reference | Lớp vẽ theo sau khi nhập ảnh, video | KB-12 |
| `EditorScreen` | ruler | Thước đang bật | KB-5 |
| `EditorScreen` | symmetry | Vẽ đối xứng đang bật | KB-5 |

- Thiết kế bổ sung ngày 2026-11-21, anh Nhật duyệt ngày 2026-11-21 (chọn "Duyệt" trong phiên): các trạng thái từ `more-menu` tới `symmetry` ở bảng trên (câu hỏi 1). Hút màu chọn từ menu "thêm" rồi chạm canvas, không có bảng riêng. Khung 360 × 640 không có frame, kiểm bằng screenshot test (câu hỏi 18).

## 5. Quảng cáo

Chép từ bản đề xuất mục 9.1. Mọi vị trí mặc định tắt, bật bằng Remote Config. Nhóm A bật khi phát hành.

**Màn hình không có quảng cáo (nếu spec muốn giữ):** canvas, dòng thời gian, các bảng công cụ của màn vẽ (cọ, tô, chữ, layer, âm thanh, onion skin); ngoài `editor_top_native`, không quảng cáo nào hiện lên trong lúc vẽ hay phát.

| Vị trí | Định dạng | Màn hình | Khi nào hiển thị | Giới hạn tần suất | Khóa bật tắt |
| --- | --- | --- | --- | --- | --- |
| `editor_top_native` | native banner gọn một tầng, cao khoảng 56 dp (định dạng mới, xem dưới bảng) | Màn vẽ, ngay dưới thanh tiêu đề, cả bố cục màn gập | Mọi trạng thái của màn vẽ, kể cả lúc phát; cách hàng nút của thanh tiêu đề và canvas mỗi bên ít nhất 16 dp; khi bật thì giữ chỗ cả lúc nạp hay lỗi; lớp mờ của tour không phủ lên. Nhóm A | Nạp lại theo `time_reload_editor_top_native`, mặc định 120 giây (khai theo `docs/ops/remote-config.md`) | `is_show_editor_top_native` |
| `editor_exit_inter` | interstitial | Rời màn vẽ | Khi người dùng bấm quay lại, sau khi đã tự lưu. Nhóm A | Theo `ads_interval` (30 giây, Q16) | `is_show_editor_exit_inter` |
| `editor_rw_layer` | rewarded | Màn vẽ, thêm layer thứ 6 | Người dùng bấm "Watch ad"; mở layer 6 đến 10 cho cả dự án. Nhóm A | Không bị `ads_interval` chặn | `is_show_editor_rw_layer` |
| `editor_rw_brushes` | rewarded | Màn vẽ, chọn cọ nâng cao | Người dùng bấm "Watch ad"; mở gói cọ nâng cao 24 giờ; 8 cọ cơ bản luôn miễn phí. Nhóm A | Không bị `ads_interval` chặn | `is_show_editor_rw_brushes` |
| `editor_rw_sounds` | rewarded | Màn vẽ, bảng âm thanh | Người dùng bấm "Watch ad"; mở gói hiệu ứng âm thanh 24 giờ; nhập nhạc, ghi âm, bộ hiệu ứng cơ bản luôn miễn phí. Nhóm A | Không bị `ads_interval` chặn | `is_show_editor_rw_sounds` |

- **Định dạng mới cho `editor_top_native` (ngoại lệ có chủ đích, bản đề xuất đoạn dưới bảng 9.1, Q17):** `:core:ads` chưa có native cao dưới 106 dp, nên app tự làm native banner gọn một tầng trong `:core:ads` của app. Việc này lệch MC-02, `ads-guide.md` và `docs/ops/to-app.md` mục 2, nên: plan làm định dạng là plan rủi ro cao kèm một ADR của app (dải 1001 trở lên); mọi lần chạy `template-sync.sh` cho app đặt `hold=core/ads` tới khi dự án mẫu nhận định dạng; chạy ổn trên bản phát hành thì mở issue `from-app`. Hình thức: chỉ icon, không ảnh chính; nhãn "Ad" luôn thấy; chừa góc trên phải cho AdChoices; CTA viền, khác nút xanh chanh của app; cao cố định khoảng 56 dp ở mọi cỡ chữ, chữ dài cắt bằng "…"; plan có test nhãn "Ad" và chữ CTA không bị cắt ở cỡ chữ lớn.
- **Trước nét vẽ đầu tiên (app tự làm):** tới khi người dùng vẽ nét đầu tiên ở bất kỳ dự án hay bài học nào, màn vẽ chỉ hiện `editor_top_native`; không hiện `editor_exit_inter`. Rewarded chỉ khi người dùng tự chọn.
- **Rewarded:** lời mời là thẻ trong bảng công cụ (cọ, âm thanh) hay lời mời khi thêm layer thứ 6; chỉ khi người dùng bấm "Watch ad" mới mở quảng cáo, không tự mở. Khóa `is_show_<mã>` tắt thì hành động làm thẳng, không mời (MC-05). Điều kiện "mở 24 giờ" và "cả dự án" do app tự làm. Khi không có quảng cáo để hiện: `allow_after_2`, sau 2 lần thử liên tiếp không có quảng cáo thì mở luôn (câu hỏi 4).
- **Vị trí toàn app (không thuộc bảng này):** `resume`, `resume_inter` hiện cả ở màn vẽ khi quay lại app (Q4); `native_combo` có thể hiện sau `editor_exit_inter` theo spec ads.

## 6. Dữ liệu và phân tích

**Dữ liệu lưu trên máy:** chỉ trên máy, không tài khoản (bản đề xuất mục 9.4).

| Dữ liệu | Mục đích | Giữ trong bao lâu |
| --- | --- | --- |
| Dự án: khung, layer, nền đã nhập, âm thanh (nhạc đã chọn, giọng ghi âm, hiệu ứng đã đặt), camera ảo, tốc độ khung, cài đặt onion skin | Tự lưu, mở lại, khôi phục sau khi app tắt đột ngột | Đến khi người dùng xóa dự án hay xóa dữ liệu app |
| Dự án đã mở layer 6 đến 10 | Giữ quyền mở cho cả dự án | Cùng dự án |
| Thời điểm mở gói cọ nâng cao, gói hiệu ứng âm thanh | Tính 24 giờ | Đến khi hết hạn |
| Lực bút chỉnh riêng của từng cọ (bật, tắt, hay chưa chỉnh) | Công tắc ở bảng "Brush" thắng giá trị mặc định ở Cài đặt (câu hỏi 22) | Đến khi xóa dữ liệu app |
| Mốc "nét vẽ đầu tiên" | Luật quảng cáo trước nét vẽ đầu tiên | Đến khi xóa dữ liệu app |
| Đã xem hay bỏ qua hướng dẫn trên màn vẽ | Không tự hiện lại | Đến khi xóa dữ liệu app |
| Bộ hiệu ứng âm thanh đã tải | Không tải lại mỗi lần | Đến khi xóa dữ liệu app |

**Sự kiện phân tích:** lấy từ danh sách đề xuất ở bản đề xuất mục 9.2; danh sách và tham số đã được anh Nhật duyệt (câu hỏi 5). Không ghi nội dung tranh, âm thanh hay chữ người dùng nhập.

| Tên sự kiện | Khi nào ghi | Tham số | Dùng để trả lời câu hỏi gì |
| --- | --- | --- | --- |
| `project_open` | Khi màn vẽ mở một dự án (không tính bài học) | `frame_count`, `layer_count` | Người dùng quay lại dự án bao nhiêu lần |
| `frame_add` | Gom theo phiên, ghi một lần khi rời màn vẽ, không ghi mỗi lần thêm | `count` (số khung thêm trong phiên) | Người dùng làm bao nhiêu khung mỗi phiên |
| `playback_start` | Khi bấm phát | `frame_count`, `fps` | Người dùng có phát thử không, dự án dài bao nhiêu |
| `layer_unlock_rewarded` | Khi xem hết quảng cáo và mở layer 6 đến 10 | Không có | Bao nhiêu người chạm giới hạn layer và chịu xem quảng cáo |
| `brush_pack_unlock_rewarded` | Khi xem hết quảng cáo và mở gói cọ | Không có | Gói cọ nâng cao có hấp dẫn không |
| `sound_pack_unlock_rewarded` | Khi xem hết quảng cáo và mở gói hiệu ứng | Không có | Gói hiệu ứng có hấp dẫn không |

**Quyền của app và dịch vụ bên ngoài:**

- `RECORD_AUDIO`: hỏi khi người dùng bấm "Record" lần đầu (Q5), mọi phiên bản Android. Từ chối thì app báo cần quyền để ghi âm, mọi phần khác vẫn dùng được; từ chối hẳn thì bấm "Record" hiện lời giải thích và nút mở cài đặt quyền của app (câu hỏi 13).
- Nhập ảnh, video qua bộ chọn ảnh của hệ thống (Photo Picker), nhập nhạc qua bộ chọn file của hệ thống: không cần quyền.
- `WRITE_EXTERNAL_STORAGE` (`maxSdkVersion=28`) dùng cho lưu file vào thư viện máy ở feature export, màn vẽ không cần.
- Không dịch vụ hay SDK bên ngoài mới; quảng cáo qua `:core:ads` (chỉ AdMob, Q7).

## 7. File tĩnh trên R2

| Nhóm file | Dung lượng ước tính | Khi nào tải | Khi không tải được |
| --- | --- | --- | --- |
| Bộ hiệu ứng âm thanh cơ bản (CC0) | Khoảng 10 hiệu ứng, dưới 2 MB, đường dẫn có phiên bản như `sounds/basic/v1/` (câu hỏi 14) | Lần đầu mở "Effects"; lần sau dùng bản trên máy | Bảng âm thanh báo lỗi, có nút thử lại (KB-13) |
| Gói hiệu ứng thêm (CC0): "Cartoon" 24 âm, "Ambience" 16 âm | Mỗi gói dưới 5 MB, đường dẫn có phiên bản như `sounds/cartoon/v1/` (câu hỏi 14) | Khi người dùng dùng gói đã mở | Báo lỗi, có nút thử lại; quyền mở 24 giờ vẫn giữ |
| Nội dung bài học | Theo spec lessons | Feature Bài học tải và đưa cho màn vẽ | Theo spec lessons |

## 8. Cờ tính năng

| Tên cờ | Mặc định | Ý nghĩa |
| --- | --- | --- |
| `feature_editor_enabled` | tắt | Bật để người dùng vào màn vẽ mới. Bản staging bật sẵn |

Tham số Remote Config của màn vẽ (không phải cờ): `time_reload_editor_top_native`, mặc định 120 giây (mục 5). Camera ảo không có cờ riêng, đi chung cờ `feature_editor_enabled` (câu hỏi 19).

## 9. Câu hỏi còn mở

1. **Thiết kế chưa có bảng cho nhiều công cụ của bản đề xuất:** tẩy, hình, thước, vẽ đối xứng, hút màu, chọn màu (bảng màu), camera ảo, nhập ảnh và video, chọn tốc độ khung, giữ khung. Thanh công cụ có 8 nút (cọ, tẩy, tô, chọn vùng, hình, chữ, camera ảo, thêm), không có nút riêng cho hút màu, thước, đối xứng, nhập nền; bảng âm thanh cũng không có nút mở rõ trên trạng thái drawing.
   - Đề xuất của AI: nút "thêm" (ba chấm) mở một menu gồm hút màu, thước, vẽ đối xứng, nhập ảnh hay video, âm thanh, xem lại hướng dẫn. Dựng thêm các bảng còn thiếu trên Figma (cùng thành phần đã duyệt) trước khi viết plan cho các phần đó; plan chưa có thiết kế thì không chạy.
   - Trả lời: Menu "thêm", và dựng các bảng còn thiếu trên Figma trước khi viết plan, theo đề xuất của AI.
2. **Dòng thời gian thu gọn:** bản đề xuất mục 4 ghi "dòng thời gian thu gọn được (dòng thời gian 56 dp)"; thiết kế chỉ có dòng thời gian 140 dp, không có trạng thái thu gọn, và canvas 9:16 vẫn đạt khoảng 57% chiều cao.
   - Đề xuất của AI: không làm thu gọn ở bản đầu vì thước đo canvas đã đạt; ghi lại để xét ở bản sau.
   - Trả lời: Ban đầu anh Nhật chọn có làm (khác đề xuất của AI). Sau khi dựng thử, canvas 9:16 ở khung 411 × 891 chỉ lớn từ 288 × 512 lên 296 × 526 dp, nên ngày 2026-11-21 anh đổi lại: không làm ở bản đầu, theo đề xuất của AI.
3. **Lời mời mở layer 6 đến 10: thẻ nhỏ hay hộp thoại?** Bản đề xuất mục 4 ghi "mời rewarded bằng thẻ nhỏ trên màn vẽ" (học từ ZenByte: không chặn, không làm tối nền). Thiết kế `layer-locked` là hộp thoại giữa màn có nút "Not now" và "Watch ad". Hai trạng thái cọ và âm thanh dùng thẻ trong bảng công cụ.
   - Đề xuất của AI: giữ hộp thoại như thiết kế đã duyệt vì nó chỉ hiện sau khi người dùng bấm thêm layer, không phải màn toàn màn hình; hoặc đổi thành thẻ mời trong bảng "Layers" cho đồng bộ với cọ và âm thanh. Anh Nhật chọn.
   - Trả lời: Giữ hộp thoại như thiết kế.
4. **Chính sách khi không có quảng cáo có thưởng để hiện** (`editor_rw_layer`, `editor_rw_brushes`, `editor_rw_sounds`): mặc định của `:core:ads` là chặn, người dùng thử lại được (MC-05).
   - Đề xuất của AI: `allow_after_2`: sau 2 lần thử liên tiếp không có quảng cáo thì cho mở luôn, để người dùng không kẹt vì lỗi mạng quảng cáo. Áp chung một chính sách cho cả bốn vị trí rewarded của app, gồm cả `export_rw_extra` của màn Xuất (spec export câu 14 trỏ về đây), để anh chọn một lần.
   - Trả lời: `allow_after_2`, áp cho cả bốn vị trí rewarded.
5. **Xác nhận danh sách sự kiện và tham số ở mục 6** (bản đề xuất mục 9.2 chỉ là đề xuất). Hai điểm cần chốt thêm: `project_open` do màn vẽ hay feature projects ghi; `lesson_complete` (bước cuối của bài nằm trên màn vẽ) do màn vẽ hay feature lessons ghi.
   - Đề xuất của AI: giữ sáu sự kiện ở mục 6 với tham số đã ghi; `project_open` do màn vẽ ghi khi mở được dự án (spec projects câu 17 đã theo hướng này, bảng sự kiện của projects không còn dòng `project_open`); `lesson_start`, `lesson_complete` do feature lessons ghi, màn vẽ báo cho lessons khi người dùng bấm "Next step" ở bước cuối.
   - Trả lời: Duyệt như đề xuất.
6. **Cọ nâng cao hết hạn khi đang được chọn:** nét đã vẽ giữ nguyên, còn cọ đang chọn thì sao?
   - Đề xuất của AI: người dùng vẽ tiếp được tới khi rời màn vẽ; lần mở sau cọ đang chọn về "Pencil".
   - Trả lời: Đồng ý đề xuất của AI.
7. **Hiệu ứng từ gói thêm đã đặt vào dự án khi gói hết 24 giờ:** giữ trong dự án và khi xuất không?
   - Đề xuất của AI: giữ, phát và xuất bình thường; chỉ không đặt thêm được hiệu ứng mới từ gói.
   - Trả lời: Đồng ý đề xuất của AI.
8. **Mức "Close gaps":** thiết kế ghi "Medium · 4 px"; bản đề xuất chỉ đo khe 1, 2, 4 px.
   - Đề xuất của AI: các mức "Off", "Small · 2 px", "Medium · 4 px" (mặc định), "Large · 8 px"; thước đo ảnh thử 1, 2, 4 px dùng mức mặc định.
   - Trả lời: Đồng ý đề xuất của AI.
9. **Cách chọn dải khung để nhân bản 10 khung trong tối đa 3 thao tác:** thiết kế chỉ có nhấn giữ để vào chọn nhiều và chạm từng khung.
   - Đề xuất của AI: nhấn giữ khung đầu rồi kéo ngón dọc dải để chọn liền cả dải (thao tác 1), bấm nhân bản (thao tác 2). Chạm từng khung vẫn chọn thêm hay bỏ từng khung.
   - Trả lời: Đồng ý đề xuất của AI.
10. **Cách đo "phát mượt 12 khung/giây trên máy tầm trung":** máy nào, mượt là bao nhiêu.
    - Đề xuất của AI: đo trên một máy Android tầm trung cụ thể của nhóm (anh Nhật chọn máy); mượt là ít nhất 95% khung hiện đúng nhịp trong 3 vòng phát dự án 200 khung × 5 layer. Đo ở prototype tuần đầu (bản đề xuất mục 5).
    - Trả lời: Giữ ngưỡng 95%; chốt máy đo ở prototype tuần đầu.
11. **Số bước hoàn tác tối đa:** bản đề xuất không ghi.
    - Đề xuất của AI: ít nhất 50 bước mỗi lần mở dự án; lịch sử hoàn tác không giữ sau khi rời màn vẽ.
    - Trả lời: Đồng ý đề xuất của AI.
12. **Nhập ảnh, video để vẽ theo (KB-12):** video dài hơn dự án thì sao, lớp vẽ theo có xuất ra file không? Câu này chỉ nói ảnh hay video nhập trên màn vẽ để vẽ theo, khác nền "Photo" chọn ở màn Tạo dự án (spec projects câu 3 đề xuất nền đó có trong file xuất).
    - Đề xuất của AI: video được chia theo tốc độ khung của dự án, tối đa 10 giây; lớp vẽ theo chỉ để vẽ theo, hiện mờ, không xuất ra file (có công tắc ẩn hiện). Ảnh nhập để vẽ theo cũng không xuất ra file. Nền "Photo" của dự án theo câu trả lời ở spec projects câu 3.
    - Trả lời: Đồng ý đề xuất của AI.
13. **Người dùng từ chối hẳn quyền ghi âm ("Don't ask again"):** bản đề xuất không nói.
    - Đề xuất của AI: bấm "Record" thì hiện lời giải thích ngắn và nút mở cài đặt quyền của app trong hệ thống.
    - Trả lời: Đồng ý đề xuất của AI.
14. **Dung lượng và đường dẫn của bộ hiệu ứng cơ bản và các gói trên R2:** chưa có số. R2 staging tạm dùng `https://draw-animation-staging.vgl.io.vn/`.
    - Đề xuất của AI: bộ cơ bản khoảng 10 hiệu ứng, dưới 2 MB; mỗi gói tải riêng, dưới 5 MB; đường dẫn có phiên bản, ví dụ `sounds/basic/v1/`, `sounds/cartoon/v1/`. Nhóm chọn nguồn CC0 và ghi giấy phép.
    - Trả lời: Đồng ý đề xuất của AI.
15. **Chỗ xem lại hướng dẫn trên màn vẽ, và hướng dẫn có tự hiện khi vào qua bài học không** ("xem lại được" ở bản đề xuất mục 5; thiết kế không có).
    - Đề xuất của AI: một mục trong menu của nút "thêm" trên thanh công cụ (cùng câu hỏi 1). Hướng dẫn không tự hiện khi vào màn vẽ qua bài học; hiện ở lần đầu vào một dự án thường. Spec settings câu 14 đề xuất thêm một dòng trong Cài đặt; trả lời chung hai câu, có thể chọn một hay cả hai lối vào.
    - Trả lời: Chỉ lối vào trong menu "thêm" của màn vẽ, không thêm dòng ở Cài đặt. Hướng dẫn không tự hiện khi vào qua bài học, theo đề xuất của AI.
16. **Nút "Show me" của chỉ dẫn bài học làm gì, và nội dung bài đưa sang màn vẽ gồm gì?**
    - Đề xuất của AI: "Show me" phát một đoạn mẫu của bước đó trên canvas rồi trả về khung đang vẽ. Mỗi bài từ feature Bài học gồm: dự án dựng sẵn, danh sách bước (tên, lời chỉ dẫn, khung áp dụng, lớp nét dẫn, đoạn mẫu). Hai spec editor và lessons chốt cùng một dạng.
    - Trả lời: Đồng ý đề xuất của AI.
17. **Đóng bài học giữa chừng:** về đâu, có hiện `editor_exit_inter` không, có giữ tiến độ bài không? Bản đề xuất và thiết kế không nói.
    - Đề xuất của AI: về màn Bài học (KB-18); có hiện `editor_exit_inter` như rời màn vẽ thường (theo `ads_interval`, sau nét vẽ đầu tiên); giữ bước đang làm để lần sau mở tiếp (spec lessons KB-4, mục 5).
    - Trả lời: Đồng ý đề xuất của AI.
18. **Màn 360 × 640:** thiết kế chỉ có khung 411 × 891 và màn gập. Ở 360 × 640, thanh công cụ 8 nút (cao 392 dp) không vừa vùng làm việc khi có dải quảng cáo.
    - Đề xuất của AI: thanh công cụ cuộn được; canvas thu nhỏ giữ tỷ lệ; các bảng công cụ cuộn được. Screenshot test ở trạng thái drawing có dải quảng cáo và tool-brush.
    - Trả lời: Đồng ý đề xuất của AI.
19. **Camera ảo có cần cờ riêng không?** Camera ảo có trong bản đầu (Q3) nhưng làm lịch dài thêm khoảng một tuần.
    - Đề xuất của AI: thêm cờ phụ `feature_editor_camera_enabled` (mặc định tắt) để phát hành màn vẽ trước nếu camera ảo chưa xong.
    - Trả lời: Không thêm cờ phụ; camera ảo đi chung cờ `feature_editor_enabled` (khác đề xuất của AI).
20. **Cờ `feature_editor_enabled` tắt thì sao?** Màn vẽ là lõi của app, không có màn cũ để thay.
    - Đề xuất của AI: cờ chỉ để giữ màn vẽ khỏi bản phát hành khi chưa xong; khi tắt, chạm thẻ dự án, bấm "Create" (không tạo dự án), chạm thẻ bài học hay bấm "Next lesson" thì hiện thông báo ngắn "Coming soon" và ở lại màn đang đứng. Bản staging bật sẵn.
    - Trả lời: Đồng ý đề xuất của AI.
21. **Vùng chạm của thanh công cụ trái:** thiết kế ghi nút 44 dp, cách nhau 4 dp; bảng tiêu chí duyệt của thiết kế đòi vùng chạm ít nhất 48 dp.
    - Đề xuất của AI: giữ hình nút 44 dp như thiết kế, vùng chạm mở rộng tới 48 dp (dùng khoảng cách giữa các nút), như tiêu chí 48 dp của cột phải ở KB-1.
    - Trả lời: Đồng ý đề xuất của AI.
22. **Lực bút: công tắc chung hay theo từng cọ?** Thiết kế Cài đặt có công tắc chung "Stylus pressure" (spec settings câu 7); thiết kế bảng "Brush" của màn vẽ có công tắc "Pressure from stylus" (KB-2). Bản đề xuất chỉ nói "lực bút" trong cọ. Có giữ cả hai không, và khi hai công tắc khác nhau thì bên nào thắng?
    - Đề xuất của AI: giữ cả hai. Công tắc ở Cài đặt là giá trị mặc định cho cọ chưa chỉnh; công tắc ở bảng "Brush" chỉnh riêng cọ đang chọn và thắng giá trị mặc định cho cọ đó. Phương án khác: bỏ công tắc ở bảng "Brush" (chỉ còn công tắc chung), hoặc bỏ dòng ở Cài đặt (chỉ còn theo cọ); cả hai cần sửa thiết kế. Trả lời chung với spec settings câu 7.
    - Trả lời: Giữ cả hai công tắc, theo đề xuất của AI.

23. **Điều khiển có trên thiết kế bổ sung mà bản đề xuất không nói** (thêm ngày 2026-11-21 khi dựng thiết kế): "Softness" và "Erase on all layers" ở bảng tẩy, loại "Arrow" và "Fill shape" ở bảng hình, "Reset camera" ở bảng camera, các mức 1× đến 4× của giữ khung.
    - Đề xuất của AI: giữ cả, vì làm thêm ít và hợp với app vẽ; nếu muốn gọn thì bỏ "Softness" và "Erase on all layers" (tẩy chỉ có "Size"), sửa frame `tool-eraser`.
    - Trả lời: Giữ cả, theo đề xuất của AI.

## 10. Lịch sử

| Ngày | Người | Nội dung |
| --- | --- | --- |
| 2026-11-21 | AI | Tạo bản đầu từ bản đề xuất đã duyệt (docs/research/proposal.md) và thiết kế đã duyệt |
| 2026-11-21 | AI | Ghi câu trả lời của anh Nhật cho mọi câu hỏi ở mục 9 (anh chọn trong phiên ngày 2026-11-21) và sửa phần thân spec theo các câu trả lời; sửa các điểm nên sửa còn lại của lần rà soát cuối. Spec vẫn `draft` |
| 2026-11-21 | AI | Ghi các frame thiết kế bổ sung, sửa theo câu trả lời (chờ anh Nhật duyệt) ở mục 4 và các tiêu chí liên quan |
| 2026-11-21 | AI | Ghi câu trả lời của anh Nhật trong phiên: bỏ dòng thời gian thu gọn (câu hỏi 2, đổi lại câu trả lời trước), giữ các điều khiển mới (câu hỏi 23) |
| 2026-11-21 | AI | Thiết kế bổ sung đã được anh Nhật duyệt trong phiên (chọn "Duyệt") |
