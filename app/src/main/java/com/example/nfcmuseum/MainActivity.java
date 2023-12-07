package com.example.nfcmuseum;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.SystemClock;
import android.transition.AutoTransition;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.HashMap;

public class MainActivity extends Activity {
/*
TODO:
Forma en databas i form av file_format.txt

 ^^^ efter att detta är gjort så börjar vi arbeta med filereader osv... helt enkelt få in datan i själva kanpparna osv....

 uppdelade uppgifter: (till på torsdag)

  Theodor : ---> fixa data till databasen. (DONE)

  Gustav :---> fixa objekttransfer mellan activities (Parcelable) (DONE)

 */
    NfcAdapter nfcAdapter;
    Button simulateButton;
    String payload;
    // The database.
    HashMap<Integer, MuseumExhibit> exhibitList = new HashMap<>();

    // list of NFC technologies detected:
    private final String[][] techList = new String[][] {
            new String[] {
                    NfcA.class.getName(),
                    NfcB.class.getName(),
                    NfcF.class.getName(),
                    NfcV.class.getName(),
                    IsoDep.class.getName(),
                    MifareClassic.class.getName(),
                    MifareUltralight.class.getName(), Ndef.class.getName()
            }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        setContentView(R.layout.activity_main);
        simulateButton = findViewById(R.id.simulate_button);
        simulateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payload = "enMona Lisa";
                switchActivities();
            }
        });
        // Set an exit transition
        getWindow().setExitTransition(new AutoTransition());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // creating pending intent:
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_MUTABLE);
        // creating intent receiver for NFC events:
        IntentFilter filter = new IntentFilter();
        filter.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
        // enabling foreground dispatch for getting intent from NFC event:
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            ((TextView) findViewById(R.id.payload_text)).setText("ERROR: No NFC adapter detected");
        } else {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, new IntentFilter[]{filter}, this.techList);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter != null) {
            // disabling foreground dispatch:
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        try {
            // Code for tag has been discovered.
            if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
                System.out.println("TAG DISCOVERED");
                Toast toast = Toast.makeText(this /* MyActivity */, "NFC Tag touched!", Toast.LENGTH_SHORT);
                toast.show();
                String serial = GetNfcSerial(intent);
                String info = GetNfcFullInfo(intent);
                payload = GetNfcPayload(intent);
                ((TextView) findViewById(R.id.serial_text)).setText("NFC Tag Serial Number:\n" + serial);
                ((TextView) findViewById(R.id.info_text)).setText("Info:\n" + info);
                ((TextView) findViewById(R.id.payload_text)).setText("Content:\n" + payload.substring(payload.lastIndexOf("en") + 2));
                (new Handler()).postDelayed(this::switchActivities, 1000);
            }
            else System.out.println("Tag failed discover");
        } catch (Exception e) {
            ((TextView)findViewById(R.id.info_text)).setText("Failed due to " + e);
        }
    }
    // Get the payload of the NFC tag (the text content)
    private String GetNfcPayload (Intent intent) {
        Parcelable[] ndefMessageArray = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        NdefMessage ndefMessage = (NdefMessage) ndefMessageArray[0];
        String msg = new String(ndefMessage.getRecords()[0].getPayload());
        return msg;
    }

    // Get the serial number of NFC tag
    private String GetNfcSerial (Intent intent) {
        String msg = ByteArrayToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID));
        return msg;
    }

    // Get the full Ndefmessage from the NFC tag
    private String GetNfcFullInfo (Intent intent) {
        Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        String tagMsg = "";
        if (rawMessages != null) {
            System.out.println("Raw messages not null");
            NdefMessage[] messages = new NdefMessage[rawMessages.length];
            StringBuilder record = new StringBuilder();
            for (int i = 0; i < rawMessages.length; i++) {
                messages[i] = (NdefMessage) rawMessages[i];
                record.append("Message #" + i + ": " + messages[i] + "\n");
            }
            tagMsg = record.toString();
        }
        return tagMsg;
    }

    private String ByteArrayToHexString(byte [] inarray) {
        int i, j, in;
        String [] hex = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
        String out= "";

        for(j = 0 ; j < inarray.length ; ++j)
        {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }

    private void switchActivities() {
        finish();
        // Create a new MuseumExhibit object and send it to the next activity
        MuseumExhibit exhibit = new MuseumExhibit("Mona Lisa", 1234, null, 1);
        Intent intent = new Intent(this, ExhibitActivity.class);
        intent.putExtra("exhibit", exhibit);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }
}
