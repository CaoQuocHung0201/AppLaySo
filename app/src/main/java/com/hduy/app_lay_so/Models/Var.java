package com.hduy.app_lay_so.Models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Var {
    public static String Name_databasae_sqlite="Setting_thong_tin.sqlite";
    public static String Name_table_sqlite="Thong_tin";
    // tạo bảng
    public static String Table_sqlite="CREATE TABLE IF NOT EXISTS "+Name_table_sqlite+"(id nvarchar(100), num_print nvarchar(100), boolean_loai nvarchar(10))";

    public static String rad_bds="bds";
    public static String rad_bls="bls";
    public static String rad_tv="tivi";

    public static DatabaseReference mydata=FirebaseDatabase.getInstance().getReference();
    // child
    public static String child="child";
    public static String so_chinh_bds="so_chinh_bds";
    public static String so_chinh_bls="so_chinh_bls";
    public static String so_da_xu_ly="so_da_xu_ly";
    public static String hang_cho_so="hang_cho_so";
}
