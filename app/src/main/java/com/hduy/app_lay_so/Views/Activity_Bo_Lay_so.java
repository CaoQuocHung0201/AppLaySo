package com.hduy.app_lay_so.Views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.hduy.app_lay_so.Controller.PrinterConnection;
import com.hduy.app_lay_so.Models.Database_setting;
import com.hduy.app_lay_so.Models.SQLite;
import com.hduy.app_lay_so.Models.Var;
import com.hduy.app_lay_so.R;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Activity_Bo_Lay_so extends AppCompatActivity {

    SQLite sqLite = new SQLite(this, Var.Name_databasae_sqlite, null, 1);
    DatabaseReference mydata;

    TextView txt_bls_num, txt_bls_time;
    TextView btn_bls_lay_so;
    int so_chinh, so_da_xu_ly, hang_cho_so;
    String id, print_id_bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bo_lay_so);
        //luôn sáng màn hình
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //ẩn thanh thông báo
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        anhxa();
        get_time();
        get_data_sqlite();
        get_DatabaseReference();

        btn_bls_lay_so.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    get_DatabaseReference();
                    get_data_sqlite();

                    so_chinh = so_chinh + 1;
                    hang_cho_so = hang_cho_so + 1;
                    txt_bls_num.setText(String.valueOf(so_chinh));
                    up_DatabaseReference();
                    printNumberSocket();

                }
            }
        });
    }

    private void printNumberSocket() {

        PrinterConnection connection = new PrinterConnection();
        boolean isConnected = connection.connect(print_id_bt, 9100);

        if (isConnected) {
            // Connection successful
            try {
                Socket printerSocket = new Socket(print_id_bt, 9100); // Thiết lập kết nối wifi đến máy in
                OutputStream printerOutputStream = printerSocket.getOutputStream();
                String currentDate = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault()).format(new Date());
                String txt = so_chinh + "\n\n\n\n";
                String txt2 = currentDate + "\n";
                txt2 += "www.mibo.mobi";

                byte[] centerAlign = new byte[]{0x1B, 0x61, 0x01};
                byte[] cutCommand = new byte[] { 0x1B, 0x64, 0x02, 0x1D, 0x56, 0x42, 0x05 };
                byte[] textInBytes = txt.getBytes(Charset.forName("UTF-8"));
                byte[] textInBytes2 = txt2.getBytes(Charset.forName("UTF-8"));
                
                printerOutputStream.write(centerAlign);
                byte[] doubleHeight = new byte[]{0x1D, 0x21, (byte)110};

                printerOutputStream.write(doubleHeight);
                printerOutputStream.write(textInBytes);

                byte[] normalTextCommand = new byte[]{0x1B, 0x21, 0x00};
                printerOutputStream.write(normalTextCommand);
                printerOutputStream.write(textInBytes2);

                printerOutputStream.write(cutCommand);
                printerOutputStream.flush();

                printerOutputStream.close();
                printerSocket.close();
            } catch (IOException e) {
            }
        } else {
            Toast.makeText(this, "Chưa kết nối được máy in", Toast.LENGTH_LONG).show();
        }


    }
    //Lấy time hiện hành
    private void get_time() {
        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                String currentDate = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault()).format(new Date());
                txt_bls_time.setText(currentDate);
                //time=String.valueOf(currentDate);
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(r, 0);
    }

    // Lấy thông tin từ sql
    private void get_data_sqlite() {
        Cursor getdata = sqLite.GetData("select * from " + Var.Name_table_sqlite);
        while (getdata.moveToNext()) {
            id = getdata.getString(0);
        }
    }

    //lấy DatabaseReference
    private void get_DatabaseReference() {
        mydata.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue()!=null) {
                    Database_setting database_setting = snapshot.getValue(Database_setting.class);
                    so_chinh = database_setting.getSo_chinh_bls();
                    hang_cho_so = database_setting.getHang_cho_so();
                    so_da_xu_ly = database_setting.getSo_da_xu_ly();
                    print_id_bt = database_setting.getIpPrint();
                    txt_bls_num.setText(String.valueOf(so_chinh));
                }
                else {
                    finish();
                    startActivity(new Intent(Activity_Bo_Lay_so.this,Activity_Setting.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    //---------------------------------------------------------------------------------------------


    //up DatabaseReference
    private void up_DatabaseReference() {
        mydata.child(id).child(Var.hang_cho_so).setValue(hang_cho_so);
        mydata.child(id).child(Var.so_chinh_bls).setValue(so_chinh);
    }

    //---------------------------------------------------------------------------------------------

    private void anhxa() {
        mydata = Var.mydata;
        txt_bls_num = findViewById(R.id.bls_number);
        txt_bls_time = findViewById(R.id.bls_time);
        btn_bls_lay_so = findViewById(R.id.bls_lay_so);
    }

}