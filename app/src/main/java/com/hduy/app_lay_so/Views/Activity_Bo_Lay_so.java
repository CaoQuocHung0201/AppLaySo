package com.hduy.app_lay_so.Views;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.hduy.app_lay_so.Controller.PrintPic;
import com.hduy.app_lay_so.Models.Database_setting;
import com.hduy.app_lay_so.Models.SQLite;
import com.hduy.app_lay_so.Models.Var;
import com.hduy.app_lay_so.R;
import com.journeyapps.barcodescanner.BarcodeEncoder;

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
        try {
            Socket printerSocket = new Socket(print_id_bt, 9100); // Thiết lập kết nối wifi đến máy in
            OutputStream printerOutputStream = printerSocket.getOutputStream();
            String currentDate = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault()).format(new Date());

            String txt = "UBND Huyện Bình Đại";
            String txtsochinh = so_chinh + "\n\n";
            byte[] centerAlign = new byte[]{0x1B, 0x61, 0x01};
            byte[] cutCommand = new byte[] { 0x1B, 0x00, 0x01, 0x1D, 0x56, 0x42, 0x01 };
            byte[] doubleHeight = new byte[]{0x1D, 0x21, (byte)110};
            byte[] textInBytes = txtsochinh.getBytes(Charset.forName("UTF-8"));
            printerOutputStream.write(centerAlign);

            // tiêu đề
            int width = 470; // chiều rộng bitmap
            int height = 60; // chiều cao bitmap
            Bitmap bitmap_td = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap_td);
            Paint paint = new Paint();
            paint.setTextSize(40);
            paint.setColor(Color.BLACK);
            canvas.drawText(txt, 30, height / 2, paint);
            PrintPic printPic_td = PrintPic.getInstance();
            printPic_td.init(bitmap_td);
            byte[] bitmapdata_td = printPic_td.printDraw();
            printerOutputStream.write(bitmapdata_td);
//
//            /// số chính
//            int width_num = 450; // chiều rộng bitmap
//            int height_num = 190; // chiều cao bitmap
//            Bitmap bitmap_td_num = Bitmap.createBitmap(width_num, height_num, Bitmap.Config.ARGB_8888);
//            Canvas canvas_num = new Canvas(bitmap_td_num);
//            Paint paint_num = new Paint();
//            paint_num.setTextSize(180);
//            paint_num.setColor(Color.BLACK);
//            String text_num = so_chinh+"";
//            if(so_chinh>99){
//                canvas_num.drawText(text_num, 70, height_num-60, paint_num);
//            }else {
//                canvas_num.drawText(text_num, 130, height_num-60, paint_num);
//            }
//            PrintPic printPic_td_num = PrintPic.getInstance();
//            printPic_td_num.init(bitmap_td_num);
//            byte[] bitmapdata_td_num = printPic_td_num.printDraw();
//            //printerOutputStream.write(bitmapdata_td_num);
//
            //số chính
            printerOutputStream.write(doubleHeight);
            printerOutputStream.write(textInBytes);
//
            Bitmap bitmap_footer = Bitmap.createBitmap(200, 130, Bitmap.Config.ARGB_8888);
            Canvas canvas2 = new Canvas(bitmap_footer);
            Paint paint2 = new Paint();
            paint2.setColor(Color.BLACK);
            paint2.setTextSize(20);

            String text1 = currentDate+"";
            String text2 = "www.phamthanh.vn";
            String text3 = "Hotline: 0987720307";

            canvas2.drawText(text1, 0, 20, paint2);
            canvas2.drawText(text2, 0, 50, paint2);
            canvas2.drawText(text3, 0, 80, paint2);
//
            // qr code
            String qrData = "https://seokit.biz/danh-thiep/14953";
            int qrCodeDimention = 130; // kích thước mã QR code
            MultiFormatWriter multiFormatWriter=new MultiFormatWriter();
            BitMatrix bitMatrix=multiFormatWriter.encode(qrData,BarcodeFormat.QR_CODE,qrCodeDimention,qrCodeDimention);
            BarcodeEncoder barcodeEncoder=new BarcodeEncoder();
            Bitmap bitmap_qr=barcodeEncoder.createBitmap(bitMatrix);


            int width_combie = bitmap_qr.getWidth() + bitmap_footer.getWidth();
            int height_combie = bitmap_footer.getHeight()-30;
            Bitmap combinedBitmap = Bitmap.createBitmap(width_combie, height_combie, Bitmap.Config.ARGB_8888);
            Canvas canvas_combie = new Canvas(combinedBitmap);
            canvas_combie.drawBitmap(bitmap_footer, 0f, 0f, null);
            canvas_combie.drawBitmap(bitmap_qr, bitmap_footer.getWidth(), 0f-20, null);


            PrintPic printPic1 = PrintPic.getInstance();
            printPic1.init(combinedBitmap);
            byte[] bitmapdata2 = printPic1.printDraw();
            printerOutputStream.write(bitmapdata2);


            printerOutputStream.write(cutCommand);
            printerOutputStream.flush();

            printerOutputStream.close();
            printerSocket.close();

        } catch (IOException e) {
        } catch (WriterException e) {
            throw new RuntimeException(e);
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