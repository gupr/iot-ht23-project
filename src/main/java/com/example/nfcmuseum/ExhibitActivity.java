package com.example.nfcmuseum;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExhibitActivity extends Activity {

    Button backButton;
    Button artistButton;
    Button historyButton;
    Button descButton;
    Button similarButton;
    Button exitArtist;
    CardView infoCard;
    ImageView exhibitImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exhibit);

        // BUTTONS & CARDS
        infoCard = findViewById(R.id.infoCard);
        backButton = findViewById(R.id.exhibit_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchActivities();
            }
        });

        artistButton = findViewById(R.id.artistButton);
        artistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infoCard.setVisibility(View.VISIBLE);
                Intent intent = getIntent();
                MuseumExhibit exhibit = (MuseumExhibit) intent.getSerializableExtra("exhibit");
                ((TextView) findViewById(R.id.title)).setText("Artist Information");
                String artistID = exhibit.getArtistID();
                ((TextView) findViewById(R.id.body)).setText(artistReader(artistID, 2));
            }
        });

        exitArtist = findViewById(R.id.exitArtist);
        exitArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infoCard.setVisibility(View.INVISIBLE);
            }
        });

        historyButton = findViewById(R.id.historyButton);
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infoCard.setVisibility(View.VISIBLE);
                Intent intent = getIntent();
                ExhibitInfo info = (ExhibitInfo) intent.getSerializableExtra("exhibitInfo");
                ((TextView) findViewById(R.id.title)).setText("History");
                ((TextView) findViewById(R.id.body)).setText(info.getHistoryInfoCard());
            }
        });

        descButton = findViewById(R.id.descButton);
        descButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infoCard.setVisibility(View.VISIBLE);
                Intent intent = getIntent();
                ExhibitInfo info = (ExhibitInfo) intent.getSerializableExtra("exhibitInfo");
                ((TextView) findViewById(R.id.title)).setText("Description");
                ((TextView) findViewById(R.id.body)).setText(info.getDescriptionInfoCard());
            }
        });

        similarButton = findViewById(R.id.similarButton);
        similarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            infoCard.setVisibility(View.VISIBLE);
                Intent intent = getIntent();
                ExhibitInfo info = (ExhibitInfo) intent.getSerializableExtra("exhibitInfo");
                ((TextView) findViewById(R.id.title)).setText("Similar Exhibitions");
                ((TextView) findViewById(R.id.body)).setText(similarExhibitsReader(info.getSimilar()));
            }
        });

        // Set an exit transition
        getWindow().setExitTransition(new AutoTransition());
        exhibitImg = findViewById(R.id.exhibitImage);
        Intent intent = getIntent();
        // receive the value by getSerializableExtra() method and
        // key must be same which is sent by first activity
        MuseumExhibit exhibit = (MuseumExhibit) intent.getSerializableExtra("exhibit");
        // display the title string on textView
        ((TextView) findViewById(R.id.exhibitTitle)).setText(exhibit.getTitle());
        ((TextView) findViewById(R.id.exhibitYear)).setText(exhibit.getYear());

        String name = exhibit.getImg();
        int id = getResources().getIdentifier(name, "drawable", getPackageName());
        exhibitImg.setImageResource(id);

        String artistID = exhibit.getArtistID();
        ((TextView) findViewById(R.id.exhibitArtist)).setText(artistReader(artistID, 1));
    }

    // This methods takes the artist ID and puts the correct artist under the title via the database.
    // as well as the description of the artist if the integer value is correct. (set to 2)
    // probably kinda redundant but this will do for now as long as it works :)
    private String artistReader(String artistID, int i) {
        String fileName = "artist_database.txt";
        Map<String, List<String>> artistMap = new HashMap<>();

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new InputStreamReader(getAssets().open(fileName)));
            String line;
            String currentArtistId = null;
            List<String> currentArtistParameters = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                if (line.equals("-")) {
                    if (currentArtistId != null) {
                        artistMap.put(currentArtistId, new ArrayList<>(currentArtistParameters));
                        currentArtistParameters.clear();
                    }
                } else {
                    String[] parts = line.split("=");
                    if (parts.length == 2) {
                        String key = parts[0].trim();
                        String value = parts[1].trim();

                        if ((key.equals("artist_id"))) {
                            currentArtistId = value;
                        }

                        currentArtistParameters.add(value);
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
        if (artistMap.containsKey(artistID)) {
            List<String> artistParameters = artistMap.get(artistID);
            if (artistParameters.size() >= 3) {
                        //artistParameters.get(0);  // artist id
                        //artistParameters.get(1);  // artist name
                        //artistParameters.get(2); // artist description
                if(i == 1) {
                    return artistParameters.get(1);
                }else if (i == 2){
                    return artistParameters.get(2);
                }
            }
        }
        return null;
    }

    private String similarExhibitsReader(List<String> targetExhibitIds) {
        String fileName = "similar_exhibits_database.txt";
        StringBuilder result = new StringBuilder();

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new InputStreamReader(getAssets().open(fileName)));
            String line;
            String currentExhibitId = null;
            StringBuilder currentSimilarExhibits = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("exhibit_id")) {
                    // Extract the exhibit ID
                    currentExhibitId = line.split("=")[1].trim();

                    // Check if the current exhibit ID is one of the target exhibit IDs
                    if (targetExhibitIds.contains(currentExhibitId)) {
                        // Check if we have similar exhibits for the previous exhibit
                        if (currentSimilarExhibits.length() > 0) {
                            result.append(currentSimilarExhibits);
                            currentSimilarExhibits = new StringBuilder();
                        }
                    }
                } else if (line.startsWith("similar_exhibit")) {
                    // Append the similar exhibits information only if the current exhibit ID matches a target ID
                    if (currentExhibitId != null && targetExhibitIds.contains(currentExhibitId)) {
                        currentSimilarExhibits.append(line.split("=")[1].trim()).append("\n" + "\n");
                    }
                } else if (line.equals("-")) {
                    // End of an exhibit, add parameters to the result
                    if (currentExhibitId != null && targetExhibitIds.contains(currentExhibitId)) {
                        result.append(currentSimilarExhibits);
                        currentSimilarExhibits = new StringBuilder();
                    }
                }
            }

            // Check for the last exhibit
            if (currentExhibitId != null && targetExhibitIds.contains(currentExhibitId)) {
                result.append(currentSimilarExhibits);
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
        return result.toString();
    }

    private void switchActivities() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        finish();
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }
}

