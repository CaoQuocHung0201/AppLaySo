package com.hduy.app_lay_so.Views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.print.PrinterCapabilitiesInfo;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.hduy.app_lay_so.Controller.Bluetooth.PrintBluetooth;
import com.hduy.app_lay_so.Models.Database_setting;
import com.hduy.app_lay_so.Models.SQLite;
import com.hduy.app_lay_so.Models.Var;
import com.hduy.app_lay_so.R;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Activity_Bo_Lay_so extends AppCompatActivity {

    SQLite sqLite= new SQLite(this, Var.Name_databasae_sqlite,null,1);
    DatabaseReference mydata;

    TextView txt_bls_num,txt_bls_time;
    TextView btn_bls_lay_so;
    int so_chinh,so_da_xu_ly,hang_cho_so;
    String id,print_id_bt;

    PrintBluetooth printBluetooth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bo_lay_so);
//        int SDK_INT = android.os.Build.VERSION.SDK_INT;
//        if (SDK_INT > 8)
//        {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//            //your codes here
//            //luôn sáng màn hình
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//            //ẩn thanh thông báo
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//
//            anhxa();
//            get_time();
//            get_data_sqlite();
//            get_DatabaseReference();
//
//        }
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
                if (SDK_INT > 8)
                {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    //your codes here
                    get_DatabaseReference();
                    get_data_sqlite();

                    so_chinh=so_chinh+1;
                    hang_cho_so=hang_cho_so+1;
                    txt_bls_num.setText(String.valueOf(so_chinh));
                    up_DatabaseReference();
                    //printNumberSocket();

                }
            }
        });
    }

    private void printNumberSocket(){
        // Get the printer's IP address and port number
        String ipAddress = "192.168.10.100"; // replace with your printer's IP address
        int portNumber = 9100; // default port number for most printers

        try {
            // Create a socket connection to the printer
            Socket printerSocket = new Socket(ipAddress, portNumber);

            // Get the output stream from the socket
            OutputStream outputStream = printerSocket.getOutputStream();

            // Print 10 ascending order numbers

            String txt="-------------------\n";
            txt += "Thời gian: "+1+"\n";
            txt += "Số thứ tự: "+3+"\n";
            txt +="-------------------\n";

            byte[] numberBytes = txt.getBytes();
            outputStream.write(numberBytes);
            outputStream.write("\n".getBytes());

            // Close the output stream and socket
            outputStream.close();
            printerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void printShareAPI(){

    }



    //----------------------------------------------------------------------------------------------

    //in bluetooth
//    private void print_bt(){
//        PrintBluetooth.printer_id=print_id_bt;
//        try {
//            printBluetooth.findBT();
//            printBluetooth.openBT();
//            printBluetooth.printText(String.valueOf(so_chinh),time);
//            printBluetooth.closeBT();
//        }catch (IOException ex){
//            ex.printStackTrace();
//        }
//    }

    //Lấy time hiện hành
    private void get_time(){
        Handler  handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                String currentDate = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault()).format(new Date());
                txt_bls_time.setText("Thời gian: "+currentDate);
                //time=String.valueOf(currentDate);
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(r, 0);
    }

    // Lấy thông tin từ sql
    private void get_data_sqlite(){
        Cursor getdata = sqLite.GetData("select * from "+Var.Name_table_sqlite);
        while (getdata.moveToNext()){
            id=getdata.getString(0);
            print_id_bt=getdata.getString(1);
        }
    }

    //lấy DatabaseReference
    private void get_DatabaseReference(){
        mydata.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Database_setting database_setting  = snapshot.getValue(Database_setting.class);
                so_chinh=database_setting.getSo_chinh_bls();
                hang_cho_so=database_setting.getHang_cho_so();
                so_da_xu_ly=database_setting.getSo_da_xu_ly();

                txt_bls_num.setText(String.valueOf(so_chinh));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    //---------------------------------------------------------------------------------------------


    //up DatabaseReference
    private void up_DatabaseReference(){
        mydata.child(id).child(Var.hang_cho_so).setValue(hang_cho_so);
        mydata.child(id).child(Var.so_chinh_bls).setValue(so_chinh);
    }

    //---------------------------------------------------------------------------------------------

    private void anhxa() {
        mydata= Var.mydata;
        txt_bls_num=findViewById(R.id.bls_number);
        txt_bls_time=findViewById(R.id.bls_time);
        btn_bls_lay_so=findViewById(R.id.bls_lay_so);
        printBluetooth=new PrintBluetooth();
    }

}