import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class PhieuMuon implements IThanhToan {
    private String maPhieu;
    private Date ngayMuon;
    private Date ngayHenTra;

    private DocGia docGia;
    private TrangThai trangThai;

    private ChiTietMuon[] danhSachChiTiet;
    private int soLuongChiTiet;

    private PhieuPhat[] danhSachPhieuPhat;
    private int soLuongPhieuPhat;

    private static PhieuMuon[] khoPhieuMuon = new PhieuMuon[0];

    public PhieuMuon(String maPhieu, DocGia docGia, Date ngayMuon, Date ngayHenTra) {
        this.maPhieu = maPhieu;
        this.docGia = docGia;
        this.ngayMuon = ngayMuon != null ? ngayMuon : new Date();
        this.ngayHenTra = ngayHenTra;
        this.trangThai = TrangThai.DANG_MUON; // Enum từ Thành viên 1

        this.danhSachChiTiet = new ChiTietMuon[0];
        this.soLuongChiTiet = 0;
        this.danhSachPhieuPhat = new PhieuPhat[0];
        this.soLuongPhieuPhat = 0;

        luuVaoKhoHeThong(this);
        Logger.log("Tạo mới Phiếu Mượn: " + maPhieu + " cho độc giả " + (docGia != null ? docGia.getHoTen() : "N/A"));
    }

    public void themChiTietMuon(Sach sach, int soLuong, double donGia) {
        if (sach == null || !sach.isConHang()) {
            Logger.error("Không thể thêm vào phiếu mượn: Sách không tồn tại hoặc đã hết hàng!");
            return;
        }
        ChiTietMuon chiTietMoi = new ChiTietMuon(sach, soLuong, donGia);
        danhSachChiTiet = Arrays.copyOf(danhSachChiTiet, soLuongChiTiet + 1);
        danhSachChiTiet[soLuongChiTiet] = chiTietMoi;
        soLuongChiTiet++;

        if (this.ngayHenTra == null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(this.ngayMuon);
            cal.add(Calendar.DAY_OF_MONTH, sach.getThoiHanMuon());
            this.ngayHenTra = cal.getTime();
        }
    }

    public boolean xoaChiTietMuon(int index) {
        if (index < 0 || index >= soLuongChiTiet) {
            return false;
        }
        for (int i = index; i < soLuongChiTiet - 1; i++) {
            danhSachChiTiet[i] = danhSachChiTiet[i + 1];
        }
        danhSachChiTiet = Arrays.copyOf(danhSachChiTiet, soLuongChiTiet - 1);
        soLuongChiTiet--;
        return true;
    }

    public double tinhTong() {
        double tong = 0;
        for (int i = 0; i < soLuongChiTiet; i++) {
            tong += danhSachChiTiet[i].thanhTien();
        }
        return tong;
    }

    public double tinhTienPhat() {
        Date hienTai = new Date();
        if (ngayHenTra == null || !hienTai.after(ngayHenTra)) {
            return 0.0;
        }
        long diffInMillies = Math.abs(hienTai.getTime() - ngayHenTra.getTime());
        int soNgayTre = (int) TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        if (soNgayTre <= 0) return 0.0;

        this.trangThai = TrangThai.QUA_HAN;
        double tongPhat = 0;
        for (int i = 0; i < soLuongChiTiet; i++) {
            tongPhat += danhSachChiTiet[i].tinhPhiPhatTheoLoaiSach(soNgayTre);
        }

        Logger.log("Phiếu mượn " + maPhieu + " quá hạn " + soNgayTre + " ngày. Tiền phạt: " + tongPhat);
        return tongPhat;
    }

    public void giaHan(int soNgayGiaHan) {
        if (soNgayGiaHan <= 0) {
            Logger.error("Số ngày gia hạn phải lớn hơn 0!");
            return;
        }
        if (this.trangThai == TrangThai.QUA_HAN || this.trangThai == TrangThai.DA_TRA) {
            Logger.error("Không thể gia hạn phiếu mượn đã trả hoặc đang quá hạn: " + maPhieu);
            return;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.ngayHenTra);
        cal.add(Calendar.DAY_OF_MONTH, soNgayGiaHan);
        this.ngayHenTra = cal.getTime();

        Logger.log("Đã gia hạn phiếu mượn " + maPhieu + " thêm " + soNgayGiaHan + " ngày.");
    }

    @Override
    public boolean thanhToan(double soTienKhachDua) {
        double canThanhToan = tinhTong() + tinhTienPhat();
        if (soTienKhachDua < canThanhToan) {
            Logger.error("Thanh toán phiếu mượn " + maPhieu + " thất bại: Số tiền không đủ!");
            return false;
        }
        this.trangThai = TrangThai.DA_TRA;
        for (int i = 0; i < soLuongChiTiet; i++) {
            if (danhSachChiTiet[i].getSach() != null) {
                danhSachChiTiet[i].getSach().tra();
            }
        }
        Logger.log("Thanh toán & hoàn tất trả sách cho phiếu mượn " + maPhieu + " thành công!");
        return true;
    }

    public void themPhieuPhat(PhieuPhat phieuPhat) {
        if (phieuPhat != null) {
            danhSachPhieuPhat = Arrays.copyOf(danhSachPhieuPhat, soLuongPhieuPhat + 1);
            danhSachPhieuPhat[soLuongPhieuPhat] = phieuPhat;
            soLuongPhieuPhat++;
        }
    }

    private static void luuVaoKhoHeThong(PhieuMuon pm) {
        khoPhieuMuon = Arrays.copyOf(khoPhieuMuon, khoPhieuMuon.length + 1);
        khoPhieuMuon[khoPhieuMuon.length - 1] = pm;
    }

    public void huyPhieuMuon() {
        this.danhSachChiTiet = new ChiTietMuon[0];
        this.soLuongChiTiet = 0;
        Logger.log("Đã hủy phiếu mượn và giải phóng toàn bộ ChiTietMuon của mã: " + maPhieu);
    }

    public String getMaPhieu() { return maPhieu; }
    public Date getNgayMuon() { return ngayMuon; }
    public Date getNgayHenTra() { return ngayHenTra; }
    public DocGia getDocGia() { return docGia; }
    public TrangThai getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThai trangThai) { this.trangThai = trangThai; }
    public ChiTietMuon[] getDanhSachChiTiet() { return danhSachChiTiet; }
    public PhieuPhat[] getDanhSachPhieuPhat() { return danhSachPhieuPhat; }
    public static PhieuMuon[] getKhoPhieuMuon() { return khoPhieuMuon; }
}