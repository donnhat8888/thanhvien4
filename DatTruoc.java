import java.util.Arrays;
import java.util.Date;

public class DatTruoc {
    private String maDat;
    private Date ngayDat;
    private int thuTuUuTien;

    private DocGia docGia;
    private Sach sach;
    private boolean daHuy;

    private static DatTruoc[] hangDoiDatTruoc = new DatTruoc[0];
    private static int tongSoDonDat = 0;

    public DatTruoc(String maDat, DocGia docGia, Sach sach, Date ngayDat) {
        this.maDat = maDat;
        this.docGia = docGia;
        this.sach = sach;
        this.ngayDat = ngayDat != null ? ngayDat : new Date();
        this.daHuy = false;

        this.thuTuUuTien = tinhThuTuUuTienMoi(sach);
        themVaoHangDoi(this);

        Logger.log("Đặt trước thành công [Mã=" + maDat + ", Sách=" 
                + (sach != null ? sach.getMaVach() : "N/A") 
                + ", Thứ tự ưu tiên=" + thuTuUuTien + "]");
    }

    private static int tinhThuTuUuTienMoi(Sach sachCanDat) {
        if (sachCanDat == null) return 1;
        int dem = 0;
        for (int i = 0; i < tongSoDonDat; i++) {
            DatTruoc dt = hangDoiDatTruoc[i];
            if (!dt.daHuy && dt.sach != null 
                    && dt.sach.getMaVach().equalsIgnoreCase(sachCanDat.getMaVach())) {
                dem++;
            }
        }
        return dem + 1;
    }

    private static void themVaoHangDoi(DatTruoc phieuMoi) {
        hangDoiDatTruoc = Arrays.copyOf(hangDoiDatTruoc, tongSoDonDat + 1);
        hangDoiDatTruoc[tongSoDonDat] = phieuMoi;
        tongSoDonDat++;
    }

    public void huyDat() {
        if (this.daHuy) {
            Logger.error("Phiếu đặt trước " + maDat + " đã được hủy trước đó!");
            return;
        }
        this.daHuy = true;
        int viTriCanXoa = -1;

        for (int i = 0; i < tongSoDonDat; i++) {
            if (hangDoiDatTruoc[i].maDat.equalsIgnoreCase(this.maDat)) {
                viTriCanXoa = i;
                break;
            }
        }

        if (viTriCanXoa != -1) {
            for (int i = 0; i < tongSoDonDat; i++) {
                DatTruoc dt = hangDoiDatTruoc[i];
                if (!dt.daHuy && dt.sach != null && this.sach != null
                        && dt.sach.getMaVach().equalsIgnoreCase(this.sach.getMaVach())
                        && dt.thuTuUuTien > this.thuTuUuTien) {
                    dt.thuTuUuTien--;
                }
            }

            for (int i = viTriCanXoa; i < tongSoDonDat - 1; i++) {
                hangDoiDatTruoc[i] = hangDoiDatTruoc[i + 1];
            }
            hangDoiDatTruoc = Arrays.copyOf(hangDoiDatTruoc, tongSoDonDat - 1);
            tongSoDonDat--;
        }

        this.thuTuUuTien = 0;
        Logger.log("Đã hủy đặt trước mã " + maDat + " và cập nhật lại hàng đợi ưu tiên.");
    }

    public static DatTruoc layUuTienSoMot(String maVachSach) {
        for (int i = 0; i < tongSoDonDat; i++) {
            DatTruoc dt = hangDoiDatTruoc[i];
            if (!dt.daHuy && dt.sach != null 
                    && dt.sach.getMaVach().equalsIgnoreCase(maVachSach) 
                    && dt.thuTuUuTien == 1) {
                return dt;
            }
        }
        return null;
    }

    public String getMaDat() { return maDat; }
    public Date getNgayDat() { return ngayDat; }
    public int getThuTuUuTien() { return thuTuUuTien; }
    public DocGia getDocGia() { return docGia; }
    public Sach getSach() { return sach; }
    public boolean isDaHuy() { return daHuy; }
    public static DatTruoc[] getHangDoiDatTruoc() { return hangDoiDatTruoc; }
}