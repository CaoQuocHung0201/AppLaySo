package com.hduy.app_lay_so.Views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.hduy.app_lay_so.Models.Database_setting;
import com.hduy.app_lay_so.Models.SQLite;
import com.hduy.app_lay_so.Models.Var;
import com.hduy.app_lay_so.R;

import java.time.LocalDate;

public class Activity_Setting extends AppCompatActivity {

    SQLite sqLite= new SQLite(this, Var.Name_databasae_sqlite,null,1);

    TextView btn_xn,btn_huy;
    RadioGroup radioGroup;
    RadioButton rad_dem_so,rad_lay_so;// radio bộ đếm số, bộ lấy số
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
        btn_xn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ktr_data_input()){
                    select_sqlite();
                    if (Trang_thai.equals(Var.rad_bds)){
                        finish();
                        startActivity(new Intent(Activity_Setting.this,Activity_Bo_dem_so.class));
                    }
                    else{
                        finish();
                        startActivity(new Intent(Activity_Setting.this,Activity_Bo_Lay_so.class));
                    }
                }
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
                txt_printid.setVisibility(View.GONE);
                label.setVisibility(View.GONE);
            }
        });



    }

    //Ktr dữ liêu nhập
    private boolean ktr_data_input(){
        boolean kt=false;
        // check radio btn
        if (!(rad_dem_so.isChecked() || rad_lay_so.isChecked())){
            Toast.makeText(this, "Yêu cầu chọn trạng thái", Toast.LENGTH_SHORT).show();
            kt=false;
        }
        else{
            if (rad_dem_so.isChecked()==true)
                radio_btn=Var.rad_bds;
            else if (rad_lay_so.isChecked()==true)
                radio_btn=Var.rad_bls;
            kt=true;
        }
        if (txt_id.getText().toString().trim().equals("")){
            Toast.makeText(this, "Yêu cầu nhập tên", Toast.LENGTH_SHORT).show();
            kt=false;
        }
        else {
            id = String.valueOf(txt_id.getText()).trim();
            kt=true;
        }

        if (txt_printid.getText().toString().trim().equals("") && rad_dem_so.isChecked()){
            Toast.makeText(this, "Yêu cầu nhập tên thiết bị in", Toast.LENGTH_SHORT).show();
            kt=false;
        }
        else {
            Print_id_bt = String.valueOf(txt_printid.getText()).trim();
            kt=true;
        }

//        // ktr giá trị limit_num hoặc print id theo radio butto
//        if (rad_lay_so.isChecked()==true){
//
//        }
        //ghi vào sqlite
        if (kt==true) {
            insert_sqlite();
            insert_DatabaseReference();
            return true;
        }else return false;

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
        }


    }
    //firebase
    private void insert_DatabaseReference(){
        // khi là admin thì tạo phòng, còn lại thì tham gia
        if (rad_dem_so.isChecked()==true) {
            //tạo phòng
            Database_setting database_setting=new Database_setting("",0,0,0,0,txt_printid.getText().toString());
            mydata.child(id).setValue(database_setting).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(Activity_Setting.this, "Lưu thành công!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        if (rad_lay_so.isChecked()==true){
            //vào phòng
            mydata.child(id).child(Var.child).setValue("1").addOnCompleteListener(new OnCompleteListener<Void>() {
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
            if(!getdata.getString(0).isEmpty()){
                txt_id.setEnabled(false);
            }
            id=getdata.getString(0);
            txt_id.setText(id);

            if (getdata.getString(2).equals(Var.rad_bds)) {
                rad_dem_so.setChecked(true);
                Trang_thai=Var.rad_bds;
                //txt_printid.setEnabled(false);
            }
            else {
                rad_lay_so.setChecked(true);
                Trang_thai=Var.rad_bls;
                //txt_printid.setText(getdata.getString(1));
            }
        }

    }
    private void get_DatabaseReference(){
        mydata.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Database_setting database_setting=snapshot.getValue(Database_setting.class);
                txt_printid.setText(database_setting.getIpPrint());
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
        radioGroup=findViewById(R.id.setting_radio_group);
        txt_id=findViewById(R.id.setting_id);
        txt_printid=findViewById(R.id.setting_id_print_bt);
        label = findViewById(R.id.labelPrintId);
        mydata=Var.mydata;

    }
}