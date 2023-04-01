package com.hduy.app_lay_so.Views;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.util.Printer;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.binary.Base64;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.util.EncodingUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.hduy.app_lay_so.Controller.PrintPic;
import com.hduy.app_lay_so.Controller.Util;
import com.hduy.app_lay_so.Models.Database_setting;
import com.hduy.app_lay_so.Models.SQLite;
import com.hduy.app_lay_so.Models.Var;
import com.hduy.app_lay_so.R;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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

    Bitmap bitmap;

    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;
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

        String data = "https://seokit.biz/danh-thiep/14953";
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 150, 150);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            ImageView imageView = findViewById(R.id.imgQR);
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        btn_bls_lay_so.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                String txt0 ="UBND Huyện Bình Đại\n\n";
//                ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(txt0);
//
//                String utf8String = new String(byteBuffer.array(), Charset.forName("Cp1258"));
//                Log.d("AAA",utf8String);
//
//                try {
//                    String out=new String(txt0.getBytes(StandardCharsets.UTF_8),"Cp1258");
//                    Log.d("AAA",out);
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }


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

            String txt = so_chinh + "\n\n";
            String txt2 = currentDate + "\n";
            txt2 += "www.phamthanh.vn\n";
            txt2 += "Hotline: 0987720307\n";

            byte[] centerAlign = new byte[]{0x1B, 0x61, 0x01};

            byte[] cutCommand = new byte[] { 0x1B, 0x64, 0x00, 0x1D, 0x56, 0x42, 0x01 };

            byte[] textInBytes = txt.getBytes(Charset.forName("UTF-8"));
            byte[] textInBytes2 = txt2.getBytes(Charset.forName("UTF-8"));


            byte[] doubleHeight = new byte[]{0x1D, 0x21, (byte)110};
            byte[] nomalHeight = new byte[]{0x1D, 0x21, (byte)25};
            byte[] normalTextCommand = new byte[]{0x1B, 0x21, 0x00};

            printerOutputStream.write(centerAlign);

            /// tiêu đề
            int width = 450; // chiều rộng bitmap
            int height = 100; // chiều cao bitmap
            Bitmap bitmap_td = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap_td);
            Paint paint = new Paint();
            paint.setTextSize(45);
            paint.setColor(Color.BLACK);
            String text = "UBND Huyện Bình Đại";
            canvas.drawText(text, 0, height / 2, paint);
//            canvas.drawBitmap(bitmap_td,0,0,null);
            PrintPic printPic_td = PrintPic.getInstance();
            printPic_td.init(bitmap_td);
            byte[] bitmapdata_td = printPic_td.printDraw();
            printerOutputStream.write(bitmapdata_td);

            printerOutputStream.write(doubleHeight);
            printerOutputStream.write(textInBytes);

            printerOutputStream.write(normalTextCommand);
            printerOutputStream.write(textInBytes2);

            // qr code
            String qrData = "https://seokit.biz/danh-thiep/14953";
            int qrCodeDimention = 150; // kích thước mã QR code
            MultiFormatWriter multiFormatWriter=new MultiFormatWriter();
            try {
                BitMatrix bitMatrix=multiFormatWriter.encode(qrData,BarcodeFormat.QR_CODE,qrCodeDimention,qrCodeDimention);
                BarcodeEncoder barcodeEncoder=new BarcodeEncoder();
                Bitmap bitmap=barcodeEncoder.createBitmap(bitMatrix);
                PrintPic printPic1 = PrintPic.getInstance();
                printPic1.init(bitmap);
                byte[] bitmapdata2 = printPic1.printDraw();
                printerOutputStream.write(bitmapdata2);
            }
            catch(Exception e){
                e.printStackTrace();
            }

            printerOutputStream.write(cutCommand);
            printerOutputStream.flush();

            printerOutputStream.close();
            printerSocket.close();

        } catch (IOException e) {
//        } catch (SVGParseException e) {
//            throw new RuntimeException(e);
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