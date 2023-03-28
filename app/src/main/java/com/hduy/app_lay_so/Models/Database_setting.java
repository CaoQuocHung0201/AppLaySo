package com.hduy.app_lay_so.Models;

public class Database_setting {
    private String child;
    private int so_chinh_bds;
    private int so_chinh_bls;
    private int so_da_xu_ly;
    private int hang_cho_so;

    private String ipPrint;

    public Database_setting() {
    }

    public Database_setting(String child, int so_chinh_bds, int so_chinh_bls, int so_da_xu_ly, int hang_cho_so, String ipPrint) {
        this.child = child;
        this.so_chinh_bds = so_chinh_bds;
        this.so_chinh_bls = so_chinh_bls;
        this.so_da_xu_ly = so_da_xu_ly;
        this.hang_cho_so = hang_cho_so;
        this.ipPrint = ipPrint;
    }

    public String getChild() {
        return child;
    }

    public void setChild(String child) {
        this.child = child;
    }

    public int getSo_chinh_bds() {
        return so_chinh_bds;
    }

    public void setSo_chinh_bds(int so_chinh_bds) {
        this.so_chinh_bds = so_chinh_bds;
    }

    public int getSo_chinh_bls() {
        return so_chinh_bls;
    }

    public void setSo_chinh_bls(int so_chinh_bls) {
        this.so_chinh_bls = so_chinh_bls;
    }

    public int getSo_da_xu_ly() {
        return so_da_xu_ly;
    }

    public void setSo_da_xu_ly(int so_da_xu_ly) {
        this.so_da_xu_ly = so_da_xu_ly;
    }

    public int getHang_cho_so() {
        return hang_cho_so;
    }

    public void setHang_cho_so(int hang_cho_so) {
        this.hang_cho_so = hang_cho_so;
    }

    public String getIpPrint() {
        return ipPrint;
    }

    public void setIpPrint(String ipPrint) {
        this.ipPrint = ipPrint;
    }

}
