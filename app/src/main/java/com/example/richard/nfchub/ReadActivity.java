package com.example.richard.nfchub;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReadActivity extends AppCompatActivity {

    public static final String MIME_TEXT_PLAIN = "text/plain";
    public static final String PREFS_NAME = "SP_Cards";

    private NfcAdapter nfc;
    private PendingIntent pendingIntent;
    private NdefMessage message;

    private Tag t = null;

    private TextView result;
    private EditText input;
    private Button saveTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        result = (TextView) findViewById(R.id.cardID);
            result.setVisibility(View.INVISIBLE);

        input = (EditText) findViewById(R.id.inputField);
            input.setVisibility(View.INVISIBLE);

        saveTag = (Button) findViewById(R.id.saveCard);
            saveTag.setVisibility(View.INVISIBLE);

        saveTag.setOnClickListener(saveListener);


        nfc = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
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
        Tag tag = null;

        if (nfc.ACTION_TAG_DISCOVERED.equals(action)) {
            Parcelable[] raw = intent.getParcelableArrayExtra(nfc.EXTRA_NDEF_MESSAGES);
            if (raw != null) {
                msgs = new NdefMessage[raw.length];
                for (int i = 0; i < raw.length; i++) {
                    msgs[i] = (NdefMessage) raw[i];
                }
            } else {
                //unidentified tag type
//                byte[] empty = new byte[0];
//                byte[] id = intent.getByteArrayExtra(nfc.EXTRA_ID);
//                Tag tag = intent.getParcelableExtra(nfc.EXTRA_TAG);
//
//                byte[] payload = String.valueOf(toDecimal(id)).getBytes();
//
//                Log.i("ID: ", String.valueOf(toDecimal(tag.getId())));
//
//                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload);
//                NdefMessage msg = new NdefMessage(new NdefRecord[]{record});
//                msgs = new NdefMessage[]{msg};
//
//                //Push tag to shared storage
//                tags.add(tag);

                tag = intent.getParcelableExtra(nfc.EXTRA_TAG);

                t = tag;
            }

            if (msgs != null) {
                String res = "";
                byte[] payload = msgs[0].getRecords()[0].getPayload();
                for (int b = 1; b < payload.length; b++) {
                    res += (char) payload[b];
                }

                Log.i("Result: ", res);
            }

            if ( tag != null ) {
                //Log.i("ID: ", String.valueOf(toDecimal(tag.getId())));
                displayTag(tag);
            }

        }
    }

    private void displayTag(Tag t) {
        result.setVisibility(View.VISIBLE);
        input.setVisibility(View.VISIBLE);
        saveTag.setVisibility(View.VISIBLE);

        result.setText("ID: " + String.valueOf(toDecimal(t.getId())));
    }

    private View.OnClickListener saveListener = new View.OnClickListener() {

        @Override
        public void onClick(final View v) {

            if ( isEmpty(input) ) {
                Toast.makeText(ReadActivity.this, "Please enter a card name!", Toast.LENGTH_SHORT).show();
            } else {

//                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
//                SharedPreferences.Editor pEdit = prefs.edit();
//                Gson gson = new Gson();
//                String json = gson.toJson(t);
//                pEdit.putString(input.getText().toString(), json);
//                pEdit.commit();


//            final byte[] mId; v
//            final int[] mTechList;
//            final String[] mTechStringList; v
//            final Bundle[] mTechExtras;
//            final int mServiceHandle;  // for use by NFC service, 0 indicates a mock
//            final INfcTag mTagService; // interface to NFC service, will be null if mock tag
//
//            int mConnectedTechnology;

                try {
                    JSONObject json = new JSONObject();

                    json.put("ID", toDecimal(t.getId()));

                    StringBuilder sb = new StringBuilder();

                    for ( String s : t.getTechList() ) {
                        sb.append(s);
                        sb.append("@@@");
                    }

                    json.put("techList", sb);

                    SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor pEdit = prefs.edit();

                    pEdit.putString(input.getText().toString(), json.toString());
                    pEdit.commit();

                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }

                t = null;

                result.setVisibility(View.INVISIBLE);
                input.setText("");
                input.setVisibility(View.INVISIBLE);
                saveTag.setVisibility(View.INVISIBLE);

                Toast.makeText(ReadActivity.this, "Card has been saved!", Toast.LENGTH_SHORT).show();

            }
        }
    };

    private long toDecimal(byte[] b) {
        long res = 0;
        long base = 1;
        for ( int i = 0 ; i < b.length ; i++ ) {
            long v = b[i] & 0xffl;
            res += v * base;
            base *= 256l;
        }
        return res;
    }

    private boolean isEmpty(EditText etText)
    {
        return etText.getText().toString().trim().length() == 0;
    }
}
