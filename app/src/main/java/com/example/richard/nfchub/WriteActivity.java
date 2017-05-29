package com.example.richard.nfchub;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URI;

public class WriteActivity extends AppCompatActivity {

    private NfcAdapter nfc;
    private PendingIntent pIntent;
    private AlertDialog.Builder adb;
    private AlertDialog alert;

    private String[] options = new String[] {"URL", "Phone Number"};
    private Spinner optionSpinner;
    private EditText value;
    private Button write;
    private TextView prefix;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Write to Tag");

        View layout = findViewById(R.id.activity_write);

        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[] {0xFFFFAB63,0xFFE85D5A});
        gd.setCornerRadius(0f);

        layout.setBackground(gd);

        nfc = NfcAdapter.getDefaultAdapter(WriteActivity.this);

        value = (EditText)findViewById(R.id.values);

        write = (Button)findViewById(R.id.writeTag);
        write.setOnClickListener(writeTag);

        prefix = (TextView)findViewById(R.id.prefix);

        optionSpinner = (Spinner)findViewById(R.id.options);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_custom, options);
        optionSpinner.setAdapter(adapter);

        optionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                switch (position) {
                    case 0:
                        prefix.setText("http://");
                        ViewGroup.LayoutParams params = prefix.getLayoutParams();
                        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                        prefix.setLayoutParams(params);
                    break;

//                    case 1:
//                        prefix.setText("");
//                        params = prefix.getLayoutParams();
//                        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
//                        prefix.setLayoutParams(params);
//                    break;

                    case 1:
                        prefix.setText("");
                        params = prefix.getLayoutParams();
                        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                        prefix.setLayoutParams(params);
                    break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

    }

    private View.OnClickListener writeTag = new View.OnClickListener() {

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onClick(View v) {

            adb = new AlertDialog.Builder(WriteActivity.this);
            adb.setTitle("Writing to Tag");
            adb.setMessage("Place tag on device...");
            alert = adb.show();

            pIntent = PendingIntent.getActivity(WriteActivity.this, 0,
                    new Intent(WriteActivity.this, WriteActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

            setUpNFC();

        }
    };

    private void setUpNFC() {
        IntentFilter filter = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter[] filters = new IntentFilter[] { filter };

        nfc.enableForegroundDispatch(this, pIntent, filters, null);
    }

    @Override
    protected void onNewIntent(Intent intent) {

        NdefRecord record;
        NdefMessage msg = null;
        Uri uri = null;

        if ( NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Tag detected = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            switch (optionSpinner.getSelectedItem().toString()) {

                case "URL":
                    uri = Uri.parse("http://" + value.getText().toString());

                    record = NdefRecord.createUri(uri);
                    msg = new NdefMessage(new NdefRecord[] { record });

                    break;

//                case "Volume":
//                    //TODO
//                    break;

                case "Phone Number":
                    //TODO
                    //"+" + number
                    uri = Uri.parse("tel:" + value.getText().toString());

                    record = NdefRecord.createUri(uri);
                    msg = new NdefMessage(new NdefRecord[] { record });
                    break;

            }


            //Dialog
            if ( writeToTag(msg, detected) ) {
                alert.setMessage("Tag successfully written!");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1500);
                            alert.dismiss();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }
    }

    public boolean writeToTag(NdefMessage m, Tag t) {
        try {
            Ndef ndef = Ndef.get(t);
            if ( ndef != null ) {
                ndef.connect();
                ndef.writeNdefMessage(m);
                ndef.close();
                return true;
            }
        } catch (Exception e) {
            return false;
        }

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
