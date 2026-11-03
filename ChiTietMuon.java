import java.util.ArrayList;
import java.util.List;

public class ChiTietMuon {
    private int soLuong;
    private double donGia;
    private Sach sach;

    private List<Sach> danhSachBanSao;

    public ChiTietMuon(Sach sach, int soLuong, double donGia) {
        if (sach == null || soLuong <= 0) {
            throw new IllegalArgumentException("Thông tin sách hoặc số lượng không hợp lệ!");
        }
        this.sach = sach;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.danhSachBanSao = new ArrayList<>();
        this.danhSachBanSao.add(sach);
    }

    public double thanhTien() {
        return this.soLuong * this.donGia;
    }

    public double tinhPhiPhatTheoLoaiSach(int soNgayTre) {
        if (soNgayTre <= 0 || sach == null) {
            return 0.0;
        }
        return sach.tinhPhiMuon(soNgayTre) * this.soLuong;
    }

    public void themBanSao(Sach banSaoMoi) {
        if (banSaoMoi != null) {
            danhSachBanSao.add(banSaoMoi);
            this.soLuong = danhSachBanSao.size();
        }
    }

    public boolean xoaBanSao(String maVach) {
        boolean removed = danhSachBanSao.removeIf(s -> s.getMaVach().equalsIgnoreCase(maVach));
        if (removed) {
            this.soLuong = danhSachBanSao.size();
        }
        return removed;
    }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }

    public double getDonGia() { return donGia; }
    public void setDonGia(double donGia) { this.donGia = donGia; }

    public Sach getSach() { return sach; }
    public void setSach(Sach sach) { this.sach = sach; }

    public List<Sach> getDanhSachBanSao() { return danhSachBanSao; }

    @Override
    public String toString() {
        return String.format("ChiTietMuon[Mã vạch sách=%s, Số lượng=%d, Đơn giá=%.2f, Thành tiền=%.2f]",
                (sach != null ? sach.getMaVach() : "N/A"), soLuong, donGia, thanhTien());
    }
}