package com.hduy.app_lay_so.Views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.ValueEventRegistration;
import com.hduy.app_lay_so.Controller.Check_internet;
import com.hduy.app_lay_so.Models.Database_setting;
import com.hduy.app_lay_so.Models.SQLite;
import com.hduy.app_lay_so.Models.Var;
import com.hduy.app_lay_so.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Activity_Bo_dem_so extends AppCompatActivity {

    DatabaseReference mydata;
    SQLite sqLite= new SQLite(this, Var.Name_databasae_sqlite,null,1);

    TextView txt_bds_id,txt_bds_da_xu_ly,txt_bds_hang_cho,txt_bds_num, txtDate;
    TextView btn_bds_nhac_lai,btn_bds_next;
    ImageView imgv_bds_ic_setting,imgv_bds_ic_out;

    int so_da_xu_ly,hang_cho_so,so_chinh;
    String id;

    MediaPlayer mp0,mp1,mp2,mp3,mp4,mp5,mp6,mp7,mp8,mp9;
    int dv=0,c=0,tr=0;
    String sc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bo_dem_so);
        
        anhxa();
        get_data_sqlite();
        get_DatabaseReference();
        get_time();

        // kiểm tra check internet
        if (Check_internet.isNetworkAvaliable(this)==false){
            AlertDialog.Builder dialog=new AlertDialog.Builder(Activity_Bo_dem_so.this);
            dialog.setTitle("Thông báo");
            dialog.setMessage("Không có kết nối wifi!");
            dialog.setCancelable(true);
            dialog.setPositiveButton("Thoát", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            dialog.show();
        }

        btn_bds_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(so_chinh>=1 && hang_cho_so!=0){
                    tinh_so();
                    set_so();
                    doc_so();
                    btn_bds_next.setEnabled(false);
                    btn_bds_next.setBackgroundResource(R.drawable.button_disable);
                }else Toast.makeText(Activity_Bo_dem_so.this, "Đã hết hàng đợi", Toast.LENGTH_SHORT).show();

            }
        });
        btn_bds_nhac_lai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (so_chinh>=1 && hang_cho_so!=0)
                    doc_so();
                else Toast.makeText(Activity_Bo_dem_so.this, "Đã hết hàng đợi", Toast.LENGTH_SHORT).show();
            }
        });
        imgv_bds_ic_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(Activity_Bo_dem_so.this,Activity_Setting.class));
            }
        });

        //kết thúc pha làm việc
        imgv_bds_ic_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // xác nhận kêt thúc
//                AlertDialog.Builder dialog=new AlertDialog.Builder(Activity_Bo_dem_so.this, android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
                AlertDialog.Builder dialog=new AlertDialog.Builder(Activity_Bo_dem_so.this);
                dialog.setTitle("Thông báo");
                dialog.setMessage("Bạn muốn kết thúc pha làm việc!");
                dialog.setCancelable(true);
                dialog.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ket_thuc_lam_viec();
                    }
                });
                dialog.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                dialog.show();
            }
        });
    }

    // Lấy thông tin từ sql
    private void get_data_sqlite(){
        Cursor getdata = sqLite.GetData("select * from "+Var.Name_table_sqlite);
        while (getdata.moveToNext()){
            id=getdata.getString(0);
            txt_bds_id.setText("Tên: "+id);
        }
    }

    //lấy DatabaseReference
    private void get_DatabaseReference(){
        mydata.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue()==null){
                    finish();
                    startActivity(new Intent(Activity_Bo_dem_so.this,Activity_Setting.class));
                }
                else{
                    Database_setting database_setting=snapshot.getValue(Database_setting.class);
                    so_chinh=database_setting.getSo_chinh_bds();
                    hang_cho_so=database_setting.getHang_cho_so();
                    so_da_xu_ly=database_setting.getSo_da_xu_ly();
                    set_so();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                finish();
                startActivity(new Intent(Activity_Bo_dem_so.this,Activity_Setting.class));
            }
        });
    }

    //----------------------------------------------------------------------------------------------

    // đọc số
    private void doc_so(){
        anh_xa_mp();
        MediaPlayer mp_xc=MediaPlayer.create(this,R.raw.source_xmps);
        sc=String.valueOf(so_chinh);
        mp_xc.start();
        mp_xc.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (so_chinh<10){
                    dv=so_chinh;
                    mp_don_vi();
                }
                else if (so_chinh >=10 && so_chinh <=99){
                    c=Integer.valueOf((String) sc.substring(0,1));
                    dv=Integer.valueOf((String) sc.substring(1,2));
                    mp_chuc();
                }else if(so_chinh>=100 && so_chinh<=999){
                    c=Integer.valueOf((String) sc.substring(1,2));
                    dv=Integer.valueOf((String) sc.substring(2,3));
                    tr = Integer.valueOf((String) sc.substring(0,1));
                    mp_tram();
                }

            }
        });

    }

    private void mp_tram(){
        anh_xa_mp();
        switch (tr){
            case 1:
                mp1.start();
                mp1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp_chuc();
                    }
                });
                break;
            case 2:
                mp2.start();
                mp2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp_chuc();
                    }
                });
                break;
            case 3:
                mp3.start();
                mp3.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp3.stop();
                        mp_chuc();
                    }
                });
                break;
            case 4:
                mp4.start();
                mp4.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp4.stop();
                        mp_chuc();
                    }
                });
                break;
            case 5:
                mp5.start();
                mp5.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp5.stop();
                        mp_chuc();
                    }
                });
                break;
            case 6:
                mp6.start();
                mp6.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp6.stop();
                        mp_chuc();
                    }
                });
                break;
            case 7:
                mp7.start();
                mp7.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp7.stop();
                        mp_chuc();
                    }
                });
                break;
            case 8:
                mp8.start();
                mp8.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp8.stop();
                        mp_chuc();
                    }
                });
                break;
            case 9:
                mp9.start();
                mp9.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp9.stop();
                        mp_chuc();
                    }
                });
                break;
        }
    }

    private void mp_chuc(){
        anh_xa_mp();
        switch (c){
            case 0:
                mp0.start();
                mp0.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp_don_vi();
                    }
                });
                break;
            case 1:
                mp1.start();
                mp1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp_don_vi();
                    }
                });
                break;
            case 2:
                mp2.start();
                mp2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp_don_vi();
                    }
                });
                break;
            case 3:
                mp3.start();
                mp3.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp3.stop();
                        mp_don_vi();
                    }
                });
                break;
            case 4:
                mp4.start();
                mp4.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp4.stop();
                        mp_don_vi();
                    }
                });
                break;
            case 5:
                mp5.start();
                mp5.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp5.stop();
                        mp_don_vi();
                    }
                });
                break;
            case 6:
                mp6.start();
                mp6.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp6.stop();
                        mp_don_vi();
                    }
                });
                break;
            case 7:
                mp7.start();
                mp7.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp7.stop();
                        mp_don_vi();
                    }
                });
                break;
            case 8:
                mp8.start();
                mp8.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp8.stop();
                        mp_don_vi();
                    }
                });
                break;
            case 9:
                mp9.start();
                mp9.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp9.stop();
                        mp_don_vi();
                    }
                });
                break;
        }
    }

    private void mp_don_vi(){
        anh_xa_mp();
        switch (dv){
            case 0:
                mp0.start();
                mp0.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp_cuoi();
                    }
                });
                break;
            case 1:
                mp1.start();
                mp1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp_cuoi();
                    }
                });
                break;
            case 2:
                mp2.start();
                mp2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp_cuoi();
                    }
                });
                break;
            case 3:
                mp3.start();
                mp3.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp_cuoi();
                    }
                });
                break;
            case 4:
                mp4.start();
                mp4.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp_cuoi();
                    }
                });
                break;
            case 5:
                mp5.start();
                mp5.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp_cuoi();
                    }
                });
                break;
            case 6:
                mp6.start();
                mp6.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp_cuoi();
                    }
                });
                break;
            case 7:
                mp7.start();
                mp7.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp_cuoi();
                    }
                });
                break;
            case 8:
                mp8.start();
                mp8.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp_cuoi();
                    }
                });
                break;
            case 9:
                mp9.start();
                mp9.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp_cuoi();
                    }
                });
                break;
        }
    }

    private void mp_cuoi(){
        MediaPlayer mediaPlayer_cuoi=MediaPlayer.create(this,R.raw.source_dcs);
        mediaPlayer_cuoi.start();
        mediaPlayer_cuoi.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                btn_bds_next.setEnabled(true);
                btn_bds_next.setBackgroundResource(R.drawable.button);
            }
        });
    }

    private void anh_xa_mp(){
        //media
        mp0=MediaPlayer.create(this,R.raw.soruce_0);
        mp1=MediaPlayer.create(this,R.raw.soruce_1);
        mp2=MediaPlayer.create(this,R.raw.soruce_2);
        mp3=MediaPlayer.create(this,R.raw.soruce_3);
        mp4=MediaPlayer.create(this,R.raw.soruce_4);
        mp5=MediaPlayer.create(this,R.raw.soruce_5);
        mp6=MediaPlayer.create(this,R.raw.soruce_6);
        mp7=MediaPlayer.create(this,R.raw.soruce_7);
        mp8=MediaPlayer.create(this,R.raw.soruce_8);
        mp9=MediaPlayer.create(this,R.raw.soruce_9);
    }
    //----------------------------------------------------------------------------------------------

    //sử lý số
    private void set_so(){
        txt_bds_da_xu_ly.setText("Đã xử lý: "+String.valueOf(so_da_xu_ly));
        txt_bds_hang_cho.setText("Hàng đợi: "+String.valueOf(hang_cho_so));
        txt_bds_num.setText(String.valueOf(so_chinh));
    }

    private void tinh_so(){// khi nhấn số kế tiếp sẽ tính toán thay đổi giá trị cho phù hợp
        if(hang_cho_so == 0){
            Toast.makeText(this, "Đã hết hàng đợi", Toast.LENGTH_SHORT).show();
        }else {
            so_da_xu_ly=so_da_xu_ly+1;
            hang_cho_so=hang_cho_so-1;
            so_chinh=so_chinh+1;
        }
        // up database
        up_DatabaseReference();
    }

//    private void ktr_het_so(){//kiểm tra khi hết tổng số hoặc hết số hàng đợi
//        if (tong_so==so_da_xu_ly) {
//            Toast.makeText(this, "Hết số !!", Toast.LENGTH_SHORT).show();
//            txt_bds_num.setText("Hết số");
//            btn_bds_next.setEnabled(false);
//        }
//        else if (hang_cho_so==0) {
//            Toast.makeText(this, "Hàng chờ hết số", Toast.LENGTH_SHORT).show();
//        }
//        else {
//            tinh_so();
//            set_so();
//        }
//    }

    //---------------------------------------------------------------------------------------------


    // đợi time hồi khi nhấn số tiếp
//    private void cd_btn_bds_next(){
//        final int[] i = {0};
//        btn_bds_next.setEnabled(false);
//        Handler handler = new Handler();
//
//        //thời gian cd
//        int time=5;
//        final Runnable r = new Runnable() {
//            public void run() {
//                i[0] = i[0] +1;
//                if (time==i[0])
//                    btn_bds_next.setEnabled(true);
//                handler.postDelayed(this, 1000);
//            }
//        };
//        handler.postDelayed(r, 0);
//    }
    //---------------------------------------------------------------------------------------------

    //up DatabaseReference
    private void up_DatabaseReference(){
        mydata.child(id).child(Var.hang_cho_so).setValue(hang_cho_so);
        mydata.child(id).child(Var.so_da_xu_ly).setValue(so_da_xu_ly);
        mydata.child(id).child(Var.so_chinh_bds).setValue(so_chinh);
    }

    //---------------------------------------------------------------------------------------------

    //kết thúc pha làm việc
    private void ket_thuc_lam_viec(){
        so_chinh=0;
        so_da_xu_ly=0;
        hang_cho_so=0;
        set_so();
        mydata.child(id).child(Var.hang_cho_so).setValue(hang_cho_so);
        mydata.child(id).child(Var.so_da_xu_ly).setValue(so_da_xu_ly);
        mydata.child(id).child(Var.so_chinh_bds).setValue(so_chinh);
        mydata.child(id).child(Var.so_chinh_bls).setValue(so_chinh);
    }

    //----------------------------------------------------------------------------------------------


    private void anhxa() {
        mydata= Var.mydata;
        txt_bds_id=findViewById(R.id.bds_id);
        txt_bds_da_xu_ly=findViewById(R.id.bds_da_xu_ly);
        txt_bds_hang_cho=findViewById(R.id.bds_hang_cho);
        txt_bds_num=findViewById(R.id.bds_number);
        imgv_bds_ic_setting=findViewById(R.id.bds_ic_setting);
        imgv_bds_ic_out=findViewById(R.id.bds_ic_out);
        btn_bds_next=findViewById(R.id.bds_next_num);
        btn_bds_nhac_lai=findViewById(R.id.bds_nhac_lai);
        txtDate = findViewById(R.id.txtDate);
    }
    private void get_time(){
        Handler  handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
                txtDate.setText("Ngày: "+currentDate);
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(r, 0);
    }
}