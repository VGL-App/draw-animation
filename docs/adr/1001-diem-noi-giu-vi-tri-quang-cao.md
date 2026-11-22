---
id: ADR-1001
title: App thêm điểm nối AdPlacementHold vào :core:ads để giữ vị trí quảng cáo theo luật riêng của app, là ngoại lệ có giới hạn của ADR-0030 và ADR-0045 mục 4.9
status: proposed             # proposed | accepted | superseded
origin: app                  # template: ADR của dự án mẫu (mã dưới 1000) | app: ADR riêng của app (mã từ 1001)
date: 2026-11-22
deciders: [Nhật]
supersedes:                  # không thay ADR nào: ngoại lệ có giới hạn, ADR-0030 và ADR-0045 vẫn hiệu lực cho mọi phần khác
superseded_by:
---

# ADR-1001: APP THÊM ĐIỂM NỐI ADPLACEMENTHOLD VÀO :CORE:ADS ĐỂ GIỮ VỊ TRÍ QUẢNG CÁO THEO LUẬT RIÊNG CỦA APP, LÀ NGOẠI LỆ CÓ GIỚI HẠN CỦA ADR-0030 VÀ ADR-0045 MỤC 4.9

## Bối cảnh

Bản đề xuất của Frameloop (dưới bảng 9.1) và mục 5 của sáu spec có luật "trước nét vẽ đầu tiên": tới khi người dùng vẽ nét đầu tiên trên máy, app chỉ hiện `native_onboarding`, `projects_empty_native`, `editor_top_native` và rewarded người dùng tự chọn; mọi vị trí khác không hiện. `:core:ads` không có luật này.

ADR-0030 và `docs/ops/to-app.md` mục 2 coi `:core:ads` (trừ `AdPlacement.kt`) là của dự án mẫu: app không sửa, mỗi đợt đồng bộ lấy bản của mẫu. ADR-0045 mục 4.9: cần cơ chế mới thì đề xuất về dự án mẫu, không thêm vào `:core:ads` của app.

Không sửa `:core:ads` thì luật phải nằm ở từng feature (bỏ slot, bỏ lời gọi interstitial khi chưa có mốc). Cách đó không chặn được lượt nạp sẵn của `:core:ads` (interstitial, rewarded, pool native của vị trí có khóa bật), nên quảng cáo vẫn được tải rồi hết hạn mà không hiện, và luật rải ở năm feature. Bọc `RemoteSettings` ở `:core:config` cho khóa `is_show_<mã>` đọc ra `false` thì che giá trị từ xa với mọi nơi đọc và buộc `:core:config` biết khóa quảng cáo. Đợi dự án mẫu nhận cơ chế thì chặn mọi ticket quảng cáo của app.

ADR này không ghi `supersedes` cho ADR-0030 hay ADR-0045: app không chuyển ADR của dự án mẫu sang `superseded` được, và `scripts/template-sync.sh` xóa ADR của dự án mẫu đã `superseded`; vì vậy đây là ngoại lệ có giới hạn, hai ADR đó vẫn hiệu lực cho mọi phần khác.

## Quyết định

1. App thêm vào `:core:ads` một điểm nối chung `AdPlacementHold` (interface có `NONE`, cùng kiểu `AdFreeStatus`), và `DefaultAdsFlags` trong `AdsFlags.kt` nhận nó: công tắc của vị trí không phải toàn app thành `is_show_ads && !adFree && is_show_<mã> && !held`. Đây là hai chỗ duy nhất app sửa trong `:core:ads` ngoài `AdPlacement.kt`.
2. Điểm nối không mang tên hay luật của Frameloop. Quy tắc vị trí nào bị giữ và mốc nào nhả nằm ở `:app` (binding Hilt của `AdPlacementHold`).
3. Từ khi phần sửa này vào nhánh chính, mọi lần chạy `template-sync.sh` cho app đặt `hold=core/ads` tới khi dự án mẫu nhận điểm nối (hay một cơ chế tương đương) và app chuyển sang bản của mẫu.
4. Sau mỗi đợt đồng bộ, người chạy xem lại các thay đổi của mẫu ở `:core:ads` mà app đang bỏ qua vì `hold`, và ghi vào mô tả PR đồng bộ những bản sửa cần lấy tay.
5. Khi điểm nối chạy ổn trên staging, mở issue `from-app` đề xuất nó về dự án mẫu (`docs/ops/from-app.md`), kèm binding mặc định `NONE` cho app của mẫu.
6. Ngoại lệ khác ở `:core:ads` (ví dụ định dạng dải native 56 dp) chỉ đi bằng một ADR hợp nhất có `supersedes: ADR-1001`, chép lại mọi quy tắc còn đúng của ADR này rồi thêm phần mới; không sửa ADR này khi đã `accepted`, không viết ADR riêng song song, vì một quy tắc không được nằm rải ở hai ADR còn hiệu lực (ADR-0044).

## Các phương án đã cân nhắc

| Phương án | Ưu điểm | Nhược điểm |
| --- | --- | --- |
| Điểm nối `AdPlacementHold` trong `:core:ads`, quy tắc ở `:app` (đã chọn) | Mọi đường nạp và hiện đọc cùng một công tắc, chặn cả lượt nạp sẵn; một chỗ test được hết; cơ chế chung, đề xuất được về mẫu | Lệch dự án mẫu ở hai file; phải giữ `hold=core/ads` khi đồng bộ, app không nhận bản sửa của mẫu cho `:core:ads` tới khi gỡ |
| Từng feature tự kiểm mốc | Không lệch mẫu | Không chặn lượt nạp sẵn; luật rải ở năm feature, mỗi ticket màn hình tự làm, tự test |
| Bọc `RemoteSettings` ở `:core:config` | Không sửa `:core:ads` | Che giá trị từ xa với mọi nơi đọc, khó tìm vì sao vị trí không hiện; `:core:config` phải biết khóa quảng cáo, trái ADR-0046 |
| Đọc thẳng mốc trong `:core:ads` | Ít file | Đưa luật riêng của app vào module của mẫu, không đề xuất về mẫu được |
| Chờ dự án mẫu nhận cơ chế | Không lệch | Chặn mọi ticket quảng cáo của app trong thời gian chờ |

## Hệ quả

- **Tích cực:** luật trước nét vẽ đầu tiên áp ở một chỗ cho mọi feature, kể cả lượt nạp sẵn; feature không tự đọc mốc; dự án mẫu có sẵn một đề xuất cơ chế chung.
- **Phải chấp nhận:** trong lúc giữ `hold=core/ads`, app không nhận bản sửa nào của mẫu cho `:core:ads`; người chạy đồng bộ phải xem lại sau mỗi đợt; khi mẫu nhận cơ chế với tên hay chữ ký khác, app sửa binding của mình theo.
- **Việc AI phải tuân thủ:** trong `:core:ads` của app, ngoài `AdPlacement.kt` chỉ được có điểm nối `AdPlacementHold` và phần đọc nó trong `DefaultAdsFlags`; luật giữ vị trí riêng của app đặt ở binding của `AdPlacementHold` trong `:app`, không đặt trong `:core:ads` hay trong feature; điểm nối không mang tên hay luật của app; `held` phát giá trị hiện tại ngay, cả khi đọc lỗi; ngoại lệ khác ở `:core:ads` cần ADR riêng của app hay ADR hợp nhất thay ADR này; đồng bộ dự án mẫu thì đặt `hold=core/ads` tới khi mẫu nhận điểm nối; sau mỗi đợt đồng bộ xem lại thay đổi của mẫu ở `:core:ads` bị bỏ qua; điểm nối chạy ổn trên staging thì mở issue `from-app`.
