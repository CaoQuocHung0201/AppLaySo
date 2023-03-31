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
import android.util.Printer;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.binary.Base64;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.hduy.app_lay_so.Models.Database_setting;
import com.hduy.app_lay_so.Models.SQLite;
import com.hduy.app_lay_so.Models.Var;
import com.hduy.app_lay_so.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
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
//                    testPrint();

                }
            }
        });
    }

    private byte[] bitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
    public byte[] concatenateByteArrays(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
    public byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, stream);
        byte[] byteArray = stream.toByteArray();
        return Base64.encodeBase64(byteArray);
    }


    private void printNumberSocket() {
        try {
            Socket printerSocket = new Socket(print_id_bt, 9100); // Thiết lập kết nối wifi đến máy in
            OutputStream printerOutputStream = printerSocket.getOutputStream();
            String currentDate = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault()).format(new Date());

            String txt0 ="UBND Huyen Binh Đai\n\n";

            String txt = so_chinh + "\n\n";
            String txt2 = currentDate + "\n";
            txt2 += "www.phamthanh.vn\n";
            txt2 += "Hotline: 0987720307";

            byte[] centerAlign = new byte[]{0x1B, 0x61, 0x01};

            byte[] cutCommand = new byte[] { 0x1B, 0x64, 0x01, 0x1D, 0x56, 0x42, 0x01 };
            byte[] textInBytes0 = txt0.getBytes(StandardCharsets.UTF_8);
            byte[] textInBytes = txt.getBytes(Charset.forName("UTF-8"));
            byte[] textInBytes2 = txt2.getBytes(Charset.forName("UTF-8"));


            byte[] doubleHeight = new byte[]{0x1D, 0x21, (byte)110};
            byte[] nomalHeight = new byte[]{0x1D, 0x21, (byte)25};
            byte[] normalTextCommand = new byte[]{0x1B, 0x21, 0x00};

            printerOutputStream.write(centerAlign);

//            int width = 20; // chiều rộng bitmap
//            int height = 20; // chiều cao bitmap
//            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(bitmap);
//            Paint paint = new Paint();
//            paint.setTextSize(30);
//            paint.setColor(Color.BLACK);
//            paint.setTypeface(Typeface.DEFAULT_BOLD);
//            String text = "Há há";
//            canvas.drawText(text, 0, height / 2, paint);
//            ImageView imageView = findViewById(R.id.imgQR);
//            imageView.setImageBitmap(bitmap);
//            printerOutputStream.write(bitmapToBytes(bitmap));
            // Tạo đối tượng Printer

//            byte[] cmd = new byte[]{27, 77, 0}; // Mã lệnh ESC/POS để thiết lập font chữ Times New Roman không in đậm
//            printerOutputStream.write(cmd);
//
//            // In chuỗi ký tự tiếng Việt có dấu
//            String vietnameseStr = "Hóa đơn mua hàng\nSố lượng: 10\nTổng tiền: 1.000.000 đồng\n\n";
//            byte[] utf8Bytes = vietnameseStr.getBytes("UTF-8");
//            byte[] cmd2 = new byte[utf8Bytes.length + 2];
//            cmd2[0] = 27;
//            cmd2[1] = 116; // Mã lệnh ESC/POS để thiết lập mã ký tự Unicode
//            System.arraycopy(utf8Bytes, 0, cmd2, 2, utf8Bytes.length);
//            printerOutputStream.write(cmd2);

            printerOutputStream.write(nomalHeight);
            printerOutputStream.write(textInBytes0);

            printerOutputStream.write(doubleHeight);
            printerOutputStream.write(textInBytes);

            printerOutputStream.write(normalTextCommand);
            printerOutputStream.write(textInBytes2);
            

//            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.qr3);
//            String data = "SIZE 80mm,80mm\nGAP 0mm,0mm\nDIRECTION 0,0\nCLS\nBITMAP 0,0," + bitmap.getWidth() + "," + bitmap.getHeight() + "," + 1 + ",";
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream);
//            byte[] byteArray = stream.toByteArray();
//            String imageData = Base64.encodeToString(byteArray, Base64.DEFAULT);
//            data += imageData + "\n";
//            data += "PRINT 1,1\n";
//            printerOutputStream.write(data.getBytes());
//
//            SVG svg = SVG.getFromResource(getResources(), R.raw.qrcode);
//            Bitmap bitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(bitmap);
//            svg.renderToCanvas(canvas);
//            String data = "SIZE 80mm,80mm\nGAP 0mm,0mm\nDIRECTION 0,0\nCLS\nBITMAP 0,0," + bitmap.getWidth() + "," + bitmap.getHeight() + "," + 1 + ",";
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//            byte[] byteArray = stream.toByteArray();
//            String imageData = Base64.encodeToString(byteArray, Base64.DEFAULT);
//            data += imageData + "\n";
//            data += "PRINT 1,1\n";
//            printerOutputStream.write(data.getBytes());


//            String qrData = "Dữ liệu cần mã hóa thành QR code";
//            int qrCodeDimention = 10; // kích thước mã QR code
//            QRCodeWriter writer = new QRCodeWriter();
//            try {
//                BitMatrix bitMatrix = writer.encode(qrData, BarcodeFormat.QR_CODE, qrCodeDimention, qrCodeDimention);
//                int width = bitMatrix.getWidth();
//                int height = bitMatrix.getHeight();
//                Bitmap qrBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//                for (int x = 0; x < width; x++) {
//                    for (int y = 0; y < height; y++) {
//                        qrBitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
//                    }
//                }
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                qrBitmap.compress(Bitmap.CompressFormat.PNG, 10, stream);
//                byte[] byteArray = stream.toByteArray();
//                byte[] escPosCommand = new byte[]{0x1B, 0x40, // reset printer
//                        0x1B, 0x33, 0x00, // set line spacing to default
//                        0x1B, 0x74, 0x10, // set character code table to UTF-8
//                        0x1B, 0x3A, 0x00, // set PNG print mode to auto
//                        0x1B, 0x70, 0x01, 0x00, 0x01}; // print PNG image
//                printerOutputStream.write(escPosCommand);
//                printerOutputStream.write(byteArray);
//
//            } catch (WriterException e) {
//                e.printStackTrace();
//            }

//            String text = "Chào bạn, đây là một ví dụ về việc in các ký tự tiếng Việt có dấu trên máy in XPrinter XP-Q80B.";
//            byte[] data = text.getBytes("UTF-8");
//
//            printerOutputStream.write(new byte[]{0x1B, 0x74, 0x08}); // Chọn bảng mã Unicode
//            printerOutputStream.write(data); // In chuỗi đã chuyển đổi

//            String content = "Đây là nội dung mã QR";
//            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
//            Bitmap bitmap = barcodeEncoder.encodeBitmap(content, BarcodeFormat.QR_CODE, 10, 10);
//            byte[] data = new byte[]{0x1B, 0x40}; // Ký tự ESC/POS đầu tiên
//            data = concatenateByteArrays(data, bitmapToByteArray(bitmap)); // Thêm dữ liệu mã QR
//            data = concatenateByteArrays(data, new byte[]{0x0A, 0x0A}); // Ký tự nối và kết thúc dữ liệu in
//            printerOutputStream.write(data);





//            // Thiết lập font chữ Arial
//            byte[] cmd = new byte[]{27, 77, 49}; // Mã lệnh ESC/POS để thiết lập font chữ Arial
//            byte[] cmd = new byte[]{27, 82, 1}; // Mã lệnh ESC/POS để thiết lập font chữ Tahoma
//            printerOutputStream.write(cmd);
//
//            // In chuỗi ký tự tiếng Việt có dấu
//            String vietnameseStr = "Hóa đơn mua hàng\nSố lượng: 10\nTổng tiền: 1.000.000 đồng\n\n";
//            byte[] utf8Bytes = vietnameseStr.getBytes("UTF-8");
//            byte[] cmd2 = new byte[utf8Bytes.length + 2];
//            cmd2[0] = 27;
//            cmd2[1] = 116; // Mã lệnh ESC/POS để thiết lập mã ký tự Unicode
//            System.arraycopy(utf8Bytes, 0, cmd2, 2, utf8Bytes.length);
//            printerOutputStream.write(cmd2);


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