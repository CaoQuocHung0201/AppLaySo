package com.hduy.app_lay_so;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.hduy.app_lay_so.Models.SQLite;
import com.hduy.app_lay_so.Models.Var;
import com.hduy.app_lay_so.Views.Activity_Bo_Lay_so;
import com.hduy.app_lay_so.Views.Activity_Bo_dem_so;
import com.hduy.app_lay_so.Views.Activity_Setting;

public class MainActivity extends AppCompatActivity {

    SQLite sqLite= new SQLite(this, Var.Name_databasae_sqlite,null,1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        select_sql();


    }

    // Ktr thông tin sqliite
    //----------------------------------------------------------------------------------------------
    public void select_sql(){
        sqLite.QueryData(Var.Table_sqlite);
        Cursor getdata = sqLite.GetData("select * from "+Var.Name_table_sqlite);

        if(!getdata.moveToLast())// nếu rỗng hoặc chưa có setting sẻ chuyển đến trang setting
        {
            finish();
            startActivity(new Intent(MainActivity.this, Activity_Setting.class));
        }
        else if (getdata.getString(2).equals(Var.rad_bds)){
            finish();
            startActivity(new Intent(MainActivity.this, Activity_Bo_dem_so.class));
        }
        else{
            finish();
            startActivity(new Intent(MainActivity.this, Activity_Bo_Lay_so.class));
        }
//        startActivity(new Intent(MainActivity.this, Activity_Setting.class));
    }
    //----------------------------------------------------------------------------------------------
}