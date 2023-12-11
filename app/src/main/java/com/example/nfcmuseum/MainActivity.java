package com.example.nfcmuseum;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
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
import android.transition.AutoTransition;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {

    NfcAdapter nfcAdapter;
    Button simulateButton;
    String payload;

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
        EditText simPayload = new EditText(this);
        simulateButton = findViewById(R.id.simulate_button);
        // Dialog for inputting simulated payload
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Set payload")
                .setMessage("Numerical ID (e.g. '1', '3')")
                .setView(simPayload)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    payload = simPayload.getText().toString();
                    switchActivities();
                })
                .setNegativeButton("Cancel", null)
                .create();
        simulateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show the dialog
                dialog.show();
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
            ((TextView) findViewById(R.id.payload_text)).setText("No NFC adapter detected");
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
                ((TextView) findViewById(R.id.payload_text)).setText("Content:\n" + payload);
                (new Handler()).postDelayed(this::switchActivities, 1000);
            }
            else System.out.println("Failed to discover tag");
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
        msg = msg.substring(msg.lastIndexOf("en") + 2);
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

    private MuseumExhibit exhibitReader(String targetExhibitId) {
        String fileName = "exhibit_database.txt";
        Map<String, List<String>> exhibitsMap = new HashMap<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(getAssets().open(fileName)));
            String line;
            String currentExhibitId = null;
            List<String> currentExhibitParameters = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                if (line.equals("-")) {
                    // End of an exhibit, add parameters to the map
                    if (currentExhibitId != null) {
                        exhibitsMap.put(currentExhibitId, new ArrayList<>(currentExhibitParameters));
                        currentExhibitParameters.clear();
                    }
                } else {
                    // Split the line into key and value
                    String[] parts = line.split("=");
                    if (parts.length == 2) {
                        String key = parts[0].trim();
                        String value = parts[1].trim();
                        if (key.equals("exhibit_id")) {
                            currentExhibitId = value;
                        }
                        currentExhibitParameters.add(value);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // Ensure the map contains the target exhibit
        if (exhibitsMap.containsKey(targetExhibitId)) {
            List<String> exhibitParameters = exhibitsMap.get(targetExhibitId);
            // Check if there are enough parameters
            if (exhibitParameters.size() >= 5) {
                MuseumExhibit exhibit = new MuseumExhibit(
                        exhibitParameters.get(0),  // exhibit id
                        exhibitParameters.get(1),  // exhibit name
                        exhibitParameters.get(2),  // artist year
                        exhibitParameters.get(3),  // artist id
                        exhibitParameters.get(4),
                        null,
                        null,
                        null
                         );
                return exhibit;
            }
        }
        return null;
    }

    private ExhibitInfo exhibitInfoReader(String targetExhibitId) {
        String fileName = "exhibit_database.txt";
        Map<String, List<String>> exhibitsMap = new HashMap<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(getAssets().open(fileName)));
            String line;
            String currentExhibitId = null;
            List<String> currentExhibitParameters = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                if (line.equals("-")) {
                    // End of an exhibit, add parameters to the map
                    if (currentExhibitId != null) {
                        exhibitsMap.put(currentExhibitId, new ArrayList<>(currentExhibitParameters));
                        currentExhibitParameters.clear();
                    }
                } else {
                    // Split the line into key and value
                    String[] parts = line.split("=");
                    if (parts.length == 2) {
                        String key = parts[0].trim();
                        String value = parts[1].trim();
                        if (key.equals("exhibit_id")) {
                            currentExhibitId = value;
                        }
                        currentExhibitParameters.add(value);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // Ensure the map contains the target exhibit
        if (exhibitsMap.containsKey(targetExhibitId)) {
            List<String> exhibitParameters = exhibitsMap.get(targetExhibitId);
            // Check if there are enough parameters
            if (exhibitParameters.size() >= 3) {
                List<String> objectList = new ArrayList<>(Arrays.asList(exhibitParameters.get(7).split(",")));
                ExhibitInfo info = new ExhibitInfo(
                        exhibitParameters.get(5), // exhibit description
                        exhibitParameters.get(6), // exhibit history
                        objectList // similar exhibits
                );
                return info;
            }
        }
        return null;
    }

    private void switchActivities() {
        Intent intent = new Intent(this, ExhibitActivity.class);
        if (exhibitReader(payload) != null) {
            finish();
            intent.putExtra("exhibit", exhibitReader(payload)); //This is where payload should be used to deliver the ID
            intent.putExtra("exhibitInfo", exhibitInfoReader(payload)); //This is where payload should be used to deliver the ID
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        }
        else {
            System.out.println("REACHED");
            AlertDialog error = new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Could not find exhibit with the ID of: " + payload)
                    .setPositiveButton("OK", null)
                    .create();
            error.show();
        }
    }
}
