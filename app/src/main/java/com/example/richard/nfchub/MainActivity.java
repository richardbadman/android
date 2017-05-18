package com.example.richard.nfchub;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String MIME_TEXT_PLAIN = "text/plain";

    private NfcAdapter nfc;
    private PendingIntent pendingIntent;
    private NdefMessage message;

    ToggleButton readButton;
    TextView result;

    private List<Tag> tags = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nfc = NfcAdapter.getDefaultAdapter(this);

        readButton = (ToggleButton)findViewById(R.id.tglReadWrite);
        result = (TextView)findViewById(R.id.readResult);


        if ( nfc != null && nfc.isEnabled() ) {
            Toast.makeText(this, "NFC is enabled!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "NFC is disabled!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        resolveIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //add null checks
        if ( nfc != null ) {
            nfc.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //add null checks
        if ( nfc != null) {
            nfc.disableForegroundDispatch(this);
        }
    }

    private void resolveIntent(Intent intent) {
        Log.i("Action: ", intent.getAction());
        String action = intent.getAction();
        NdefMessage[] msgs = null;

        if ( nfc.ACTION_TAG_DISCOVERED.equals(action)) {
            Parcelable[] raw = intent.getParcelableArrayExtra(nfc.EXTRA_NDEF_MESSAGES);
            if (raw != null) {
                msgs = new NdefMessage[raw.length];
                for (int i = 0; i < raw.length; i++) {
                    msgs[i] = (NdefMessage) raw[i];
                }
            } else {
                //unidentified tag type
                byte[] empty = new byte[0];
                byte[] id = intent.getByteArrayExtra(nfc.EXTRA_ID);
                Tag tag = (Tag)intent.getParcelableExtra(nfc.EXTRA_TAG);

                byte[] payload = String.valueOf(toDecimal(id)).getBytes();

                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload);
                NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
                msgs = new NdefMessage[] { msg };

                tags.add(tag);
            }

            if (msgs != null) {
                String res = "";
                byte[] payload = msgs[0].getRecords()[0].getPayload();
                for (int b = 1; b < payload.length; b++) {
                    res += (char) payload[b];
                }

                Log.i("Result: ", res);
            }
        }
    }

    public void tglReadOnClick(View view) {
        result.setText("");
    }

    private long toDecimal(byte[] b) {
        long res = 0;
        long base = 1;
        for ( int i = 0 ; i < b.length ; i++ ) {
            long v = b[i] * 0xffl;
            res += v * base;
            base *= 256l;
        }
        return res;
    }

}
