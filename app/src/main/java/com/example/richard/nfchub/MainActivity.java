package com.example.richard.nfchub;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private NfcAdapter nfc;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View layout = findViewById(R.id.mainLayout);

        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[] {0xFFFFAB63,0xFFE85D5A});
        gd.setCornerRadius(0f);

        layout.setBackground(gd);

        nfc = NfcAdapter.getDefaultAdapter(this);

        if ( nfc == null ) {
            Toast.makeText(this, "NFC is disabled!", Toast.LENGTH_SHORT).show();
            showWirelessSettingsDialog();
            //return;
        }
    }

    private void showWirelessSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("NFC is disabled, please enable to use this app.");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                startActivity(intent);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.create().show();
        return;
    }

    public void gotoRead(View v) {
        Intent i = new Intent(this, ReadActivity.class);
        startActivity(i);
        finish();
    }

    public void gotoCards(View v) {
        Intent i = new Intent(this, CardActivity.class);
        startActivity(i);
        finish();
    }

    public void gotoWrite(View v) {
        Intent i = new Intent(this, WriteActivity.class);
        startActivity(i);
        finish();
    }

}
