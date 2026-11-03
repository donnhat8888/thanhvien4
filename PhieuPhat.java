import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PhieuPhat implements IThanhToan {
    private String maPhat;
    private double soTien;
    private Date ngayLap;

    private PhieuMuon phieuMuon;
    private boolean daThanhToan;

    private List<String> lichSuGiaoDich;

    private static List<PhieuPhat> danhSachToanBoPhieuPhat = new ArrayList<>();

    public PhieuPhat(String maPhat, PhieuMuon phieuMuon, double soTien, Date ngayLap) {
        this.maPhat = maPhat;
        this.phieuMuon = phieuMuon;
        this.soTien = soTien > 0 ? soTien : (phieuMuon != null ? phieuMuon.tinhTienPhat() : 0.0);
        this.ngayLap = ngayLap != null ? ngayLap : new Date();
        this.daThanhToan = false;
        this.lichSuGiaoDich = new ArrayList<>();

        if (this.phieuMuon != null) {
            this.phieuMuon.themPhieuPhat(this);
        }

        danhSachToanBoPhieuPhat.add(this);
        lichSuGiaoDich.add("Khởi tạo phiếu phạt với số tiền: " + this.soTien);
        Logger.log("Lập Phiếu Phạt mới: " + maPhat + " | Số tiền: " + this.soTien);
    }

    public boolean thanhToan() {
        return thanhToan(this.soTien);
    }

    @Override
    public boolean thanhToan(double soTienDong) {
        if (this.daThanhToan) {
            Logger.error("Phiếu phạt " + maPhat + " đã được thanh toán trước đó!");
            return false;
        }
        if (soTienDong < this.soTien) {
            lichSuGiaoDich.add("Thanh toán thất bại (Đóng thiếu: " + soTienDong + "/" + this.soTien + ")");
            Logger.error("Thanh toán phiếu phạt " + maPhat + " thất bại: Số tiền đóng (" 
                    + soTienDong + ") nhỏ hơn số tiền phạt (" + this.soTien + ")!");
            return false;
        }

        this.daThanhToan = true;
        double tienThua = soTienDong - this.soTien;
        lichSuGiaoDich.add("Đã thanh toán đủ " + this.soTien + " (Tiền thừa: " + tienThua + ")");
        Logger.log("Thanh toán thành công Phiếu Phạt: " + maPhat);
        return true;
    }

    public String getMaPhat() { return maPhat; }
    public double getSoTien() { return soTien; }
    public void setSoTien(double soTien) { this.soTien = soTien; }
    public Date getNgayLap() { return ngayLap; }
    public PhieuMuon getPhieuMuon() { return phieuMuon; }
    public boolean isDaThanhToan() { return daThanhToan; }
    public List<String> getLichSuGiaoDich() { return lichSuGiaoDich; }
    public static List<PhieuPhat> getDanhSachToanBoPhieuPhat() { return danhSachToanBoPhieuPhat; }
}