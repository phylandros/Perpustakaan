package com.example.perpustakaan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

public class MainActivity extends AppCompatActivity {
    Button btnWelkom;
    BottomSheetDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        btnWelkom = findViewById(R.id.btnWelkom);
        dialog = new BottomSheetDialog(this);

        btnWelkom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoginDialog();
            }
        });


        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.getWindow().setDimAmount(0.0f);
    }

    public void showLoginDialog() {
        dialog.setContentView(R.layout.bsdlogin);
        Button btnDaftar = dialog.findViewById(R.id.btndaftar);
        Button btnMasuk = dialog.findViewById(R.id.btnmasuk);
        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                showRegisterDialog();
            }
        });
        btnMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        dialog.show();
    }

    public void showRegisterDialog() {
        // Tampilkan dialog register (bsdregister)
        dialog.setContentView(R.layout.bsdregister);
        Button btnLanjut = dialog.findViewById(R.id.btnlanjut);
        btnLanjut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                showRegister2Dialog();
            }
        });
        dialog.show();
    }

    public void showRegister2Dialog() {
        // Tampilkan dialog register2 (bsdregister2)
        dialog.setContentView(R.layout.bsdregister2);
        Button btnLanjut2 = dialog.findViewById(R.id.btnlanjut2);
        btnLanjut2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                showRegisterDoneDialog();
            }
        });
        dialog.show();
    }

    public void showRegisterDoneDialog() {
        // Tampilkan dialog register done (bsdregisdone)
        dialog.setContentView(R.layout.bsdregisdone);
        Button btnBacktoLogin = dialog.findViewById(R.id.btnbacklogin);
        btnBacktoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                showLoginDialog();
            }
        });
        dialog.show();
    }

}