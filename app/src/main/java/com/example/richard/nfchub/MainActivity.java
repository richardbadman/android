package com.example.richard.nfchub;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private NfcAdapter nfc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nfc = NfcAdapter.getDefaultAdapter(this);

        if ( nfc == null ) {
            Toast.makeText(this, "NFC is disabled!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
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
