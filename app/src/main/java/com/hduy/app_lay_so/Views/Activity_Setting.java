package com.hduy.app_lay_so.Views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.hduy.app_lay_so.Controller.Check_internet;
import com.hduy.app_lay_so.Models.Database_setting;
import com.hduy.app_lay_so.Models.SQLite;
import com.hduy.app_lay_so.Models.Var;
import com.hduy.app_lay_so.R;

import java.time.LocalDate;

public class Activity_Setting extends AppCompatActivity {

    SQLite sqLite= new SQLite(this, Var.Name_databasae_sqlite,null,1);

    TextView btn_xn,btn_huy, tv_chonMaunen;
    RadioGroup radioGroup;
    RadioButton rad_dem_so,rad_lay_so,rad_tivi, rad_xanhbien,rad_do,rad_cam,rad_xanhngoc;// radio bộ đếm số, bộ lấy số
    EditText txt_id,txt_printid; //nhập id và giới hạn số

    TextView label;
    DatabaseReference mydata;
    String id="", limit_num="",radio_btn="",Trang_thai="",Print_id_bt="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        anhxa();
        select_sqlite();
        get_DatabaseReference();


        // kiểm tra check internet
        if (Check_internet.isNetworkAvaliable(this)==false){
            AlertDialog.Builder dialog=new AlertDialog.Builder(Activity_Setting.this);
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

        btn_xn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ktr_data_input()){
                    select_sqlite();
                    if (Trang_thai.equals(Var.rad_bds)){
                        finish();
                        startActivity(new Intent(Activity_Setting.this,Activity_Bo_dem_so.class));
                    }
                    else if(Trang_thai.equals(Var.rad_bls)){
                        finish();
                        startActivity(new Intent(Activity_Setting.this,Activity_Bo_Lay_so.class));
                    }else {
                        finish();
                        startActivity(new Intent(Activity_Setting.this,Activity_Tivi.class));
                    }
                }
                else Toast.makeText(Activity_Setting.this, "Chưa nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            }
        });

        btn_huy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(Activity_Setting.this,Activity_Bo_dem_so.class));
            }
        });

        // khi nhấn radio
        rad_dem_so.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_printid.setVisibility(View.VISIBLE);
                label.setVisibility(View.VISIBLE);

            }
        });
        rad_lay_so.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_printid.setVisibility(View.INVISIBLE);
                label.setVisibility(View.INVISIBLE);
            }
        });
        rad_tivi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_printid.setVisibility(View.INVISIBLE);
                label.setVisibility(View.INVISIBLE);
            }
        });



    }

    //Ktr dữ liêu nhập
    private boolean ktr_data_input(){
        boolean kt=false;
        if(rad_dem_so.isChecked() && !txt_id.getText().toString().isEmpty() && !txt_printid.getText().toString().isEmpty()){
            radio_btn=Var.rad_bds;
            id = String.valueOf(txt_id.getText()).trim();
            Print_id_bt = String.valueOf(txt_printid.getText()).trim();
            insert_sqlite();
            insert_DatabaseReference();
            kt = true;
        }
        if(rad_lay_so.isChecked() && !txt_id.getText().toString().isEmpty()){
            radio_btn=Var.rad_bls;
            id = String.valueOf(txt_id.getText()).trim();
            Print_id_bt = String.valueOf(txt_printid.getText()).trim();
            insert_sqlite();
            insert_DatabaseReference();
            kt = true;
        }
        if(rad_tivi.isChecked() && !txt_id.getText().toString().isEmpty()){
            radio_btn=Var.rad_tv;
            id = String.valueOf(txt_id.getText()).trim();
            insert_sqlite();
            kt = true;
        }

        return kt;
    }
    //----------------------------------------------------------------------------------------------

    // Lưu lại setting vaof sqlite
    public void insert_sqlite(){
        sqLite.QueryData("delete from "+Var.Name_table_sqlite);

        // ktr giá trị limit_num hoặc print id theo radio button
        if (rad_dem_so.isChecked()==true){
            sqLite.QueryData("Insert into "+Var.Name_table_sqlite+" values('"+id+"','"+limit_num+"','"+radio_btn+"')");
        }
        else if (rad_lay_so.isChecked()==true){
            sqLite.QueryData("Insert into "+Var.Name_table_sqlite+" values('"+id+"','"+Print_id_bt+"','"+radio_btn+"')");
        }else {
            sqLite.QueryData("Insert into "+Var.Name_table_sqlite+" values('"+id+"','"+limit_num+"','"+radio_btn+"')");
        }


    }
    //firebase
    private void insert_DatabaseReference(){
        // khi là admin thì tạo phòng, còn lại thì tham gia
        if (rad_dem_so.isChecked()) {
            //tạo phòng
            Database_setting database_setting=new Database_setting(txt_id.getText().toString(),0,0,0,0,txt_printid.getText().toString());
            mydata.child(id).setValue(database_setting).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(Activity_Setting.this, "Lưu thành công!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        if (rad_lay_so.isChecked()){
            //vào phòng
            mydata.child(id).child(Var.child).setValue(txt_id.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(Activity_Setting.this, "Lưu thành công!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
//        mydata.child(id).child("so_chinh_bds").setValue("");
    }


    //----------------------------------------------------------------------------------------------

    // hiện setting
    public void select_sqlite(){
        Cursor getdata = sqLite.GetData("select * from "+Var.Name_table_sqlite);
        while (getdata.moveToNext()){
            id=getdata.getString(0);
            txt_id.setText(id);

            if (getdata.getString(2).equals(Var.rad_bds)) {
                rad_dem_so.setChecked(true);
                Trang_thai=Var.rad_bds;
                //txt_printid.setEnabled(false);
            }
            else if (getdata.getString(2).equals(Var.rad_bls)){
                rad_lay_so.setChecked(true);
                Trang_thai=Var.rad_bls;
                //txt_printid.setText(getdata.getString(1));
            }else {
                rad_tivi.setChecked(true);
                Trang_thai=Var.rad_tv;
            }
        }

    }
    private void get_DatabaseReference(){
        mydata.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue()!=null){
                    Database_setting database_setting=snapshot.getValue(Database_setting.class);
                    txt_id.setText(database_setting.getChild());
                    txt_printid.setText(database_setting.getIpPrint());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    //----------------------------------------------------------------------------------------------

    private void anhxa(){
        btn_xn=findViewById(R.id.setting_btn_xn);
        btn_huy=findViewById(R.id.setting_btn_huy);
        rad_dem_so=findViewById(R.id.setting_rad_bd);
        rad_lay_so=findViewById(R.id.setting_rad_bl);
        rad_tivi = findViewById(R.id.setting_rad_tivi);
        radioGroup=findViewById(R.id.setting_radio_group);
        txt_id=findViewById(R.id.setting_id);
        txt_printid=findViewById(R.id.setting_id_print_bt);
        label = findViewById(R.id.labelPrintId);
        mydata=Var.mydata;

    }
}