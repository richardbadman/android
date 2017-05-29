package com.example.richard.nfchub;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.nfc.Tag;
import android.os.Build;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;

import static android.R.attr.handle;
import static java.lang.Math.toIntExact;

public class CardActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "SP_Cards";

    private List<Tag> tags = new ArrayList<>();
    private ArrayList<String> cards = new ArrayList<>();

    private ListView list;
    private ArrayAdapter<String> adapter;
    private customAdapter custom;

    private ProgressDialog progress;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My cards");

        View layout = findViewById(R.id.activity_card);

        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[] {0xFFFFAB63,0xFFE85D5A});
        gd.setCornerRadius(0f);

        layout.setBackground(gd);

        list = (ListView) findViewById(R.id.listView);

        getCards();

        list.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String test = String.valueOf(parent.getItemAtPosition(position));
                        emulateCard(test);
                    }
                }
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void emulateCard(final String input) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage("Place device onto reader until read.");
////        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
////            public void onClick(DialogInterface dialogInterface, int i) {
////                return;
////            }
////        });
//        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialogInterface, int i) {
//                return;
//            }
//        });
//        builder.create().show();
//        return;
        progress = new ProgressDialog(this);
        progress.setMax(10);
        progress.setMessage("Place device on reader...");
        progress.setTitle("Using card: " + input);

        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show();

        Host h = new Host(input);
//
//
//        new Thread(new Runnable() {
//            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//            @Override
//            public void run() {
//                try {
//                    //Thread.sleep(10000);
//                    //Host h = new Host(input);
//                    //Emulation to go here
//                } catch ( Exception e ) {
//                    e.printStackTrace();
//                }
//                progress.dismiss();
//            }
//        }).start();

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getCards() {


        String preJSON;
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Map<String, ?> allEntries = prefs.getAll();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            //Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
            preJSON = prefs.getString(entry.getKey(), "");

            cards.add(entry.getKey());

            if ( preJSON != null ) {

                try {
                    JSONObject json = new JSONObject(preJSON);

                    long preID = json.getLong("ID");
                    byte[] id = longToBytes(preID);

                    String techList[] = json.getString("techList").split("@@@");

                    int techIDs[] = new int[techList.length];

                    for ( int i = 0 ; i < techList.length ; i++ ) {
                        techIDs[i] = getTechID(techList[i]);
                    }

//                    Tag t = new Tag(id, techIDs, , , );

                } catch (JSONException e) {
                        e.printStackTrace();
                }
            }
        }

        if ( !cards.isEmpty() ) {
            //adapter = new ArrayAdapter<String>(this, R.layout.list_text, R.id.list_content, cards);
            custom = new customAdapter(cards, this);
            setListAdapter(custom);
        }

    }

    protected void setListAdapter(ListAdapter adapter) {
        getListView().setAdapter(adapter);
    }

    protected ListView getListView() {
        if (list == null) {
            list = (ListView) findViewById(R.id.listView);
        }
        return list;
    }

    protected ListAdapter getListAdapter() {
        ListAdapter adapter = getListView().getAdapter();
        if (adapter instanceof HeaderViewListAdapter) {
            return ((HeaderViewListAdapter)adapter).getWrappedAdapter();
        } else {
            return adapter;
        }
    }

    private static byte[] longToBytes(long l) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte)(l & 0xFF);
            l >>= 8;
        }
        return result;
    }

    private int getTechID(String s) {
        switch ( s ) {
            case "android.nfc.tech.NfcA":
                return 1;

            case "android.nfc.tech.NfcB":
                return 2;

            case "android.nfc.tech.IsoDep":
                return 3;

            case "android.nfc.tech.NfcF":
                return 4;

            case "android.nfc.tech.NfcV":
                return 5;

            case "android.nfc.tech.Ndef":
                return 6;

            case "android.nfc.tech.NdefFormatable":
                return 7;

            case "android.nfc.tech.MifareClassic":
                return 8;

            case "android.nfc.tech.MifareUltralight":
                return 9;

            case "android.nfc.tech.NfcBarcode":
                return 10;

            default:
                return 0;
        }
    }


}
