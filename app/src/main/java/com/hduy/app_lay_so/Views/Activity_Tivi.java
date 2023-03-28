package com.hduy.app_lay_so.Views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.hduy.app_lay_so.Models.Database_setting;
import com.hduy.app_lay_so.Models.SQLite;
import com.hduy.app_lay_so.Models.Var;
import com.hduy.app_lay_so.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Activity_Tivi extends AppCompatActivity {

    TextView txt_bds_num, txtDate, txtHour_bls,txt_bds_num_next;
    int sotieptheo, so_chinh;
    String id="";
    DatabaseReference mydata;
    SQLite sqLite = new SQLite(this, Var.Name_databasae_sqlite, null, 1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tivi);
        anhxa();
        get_data_sqlite();
        get_DatabaseReference();
        get_time();
        //Toast.makeText(this, "sdcsdvvv:"+id, Toast.LENGTH_SHORT).show();

    }


    private void anhxa(){
        mydata = Var.mydata;
        txtHour_bls = findViewById(R.id.txtHour_bls);
        txt_bds_num = findViewById(R.id.bds_number_tv);
        txt_bds_num_next = findViewById(R.id.bds_numbernext);
        txtDate = findViewById(R.id.txtDate_tv);
    }
    private void get_time() {
        Handler handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
                String currentHour = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                txtDate.setText("Ngày: " + currentDate);
                txtHour_bls.setText("Giờ: "+currentHour);
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(r, 0);
    }
    private void set_so() {
        txt_bds_num.setText(String.valueOf(so_chinh));
        txt_bds_num_next.setText(String.valueOf(sotieptheo));
    }

    private void get_DatabaseReference() {
        mydata.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() == null) {
                    finish();
                    startActivity(new Intent(Activity_Tivi.this, Activity_Setting.class));
                } else {
                    Database_setting database_setting = snapshot.getValue(Database_setting.class);
                    so_chinh = database_setting.getSo_chinh_bds();
                    sotieptheo = so_chinh+1;
                    set_so();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                finish();
                startActivity(new Intent(Activity_Tivi.this, Activity_Setting.class));
            }
        });
    }
    private void get_data_sqlite() {
        Cursor getdata = sqLite.GetData("select * from " + Var.Name_table_sqlite);
        while (getdata.moveToNext()) {
            id = getdata.getString(0);
        }

    }
}