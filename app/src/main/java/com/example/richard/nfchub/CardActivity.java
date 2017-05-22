package com.example.richard.nfchub;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.Math.toIntExact;

public class CardActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "SP_Cards";

    private List<Tag> tags = new ArrayList<>();
    private List<String> cards = new ArrayList<>();

    private ListView list;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        list = (ListView) findViewById(R.id.listView);

        getCards();
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
            adapter=new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1,
                    cards);
            setListAdapter(adapter);
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
