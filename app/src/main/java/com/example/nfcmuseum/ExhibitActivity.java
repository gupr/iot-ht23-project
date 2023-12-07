package com.example.nfcmuseum;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import org.w3c.dom.Text;

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

        backButton = findViewById(R.id.exhibit_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchActivities();
            }
        });

        artistButton = findViewById(R.id.artistButton);
        infoCard = findViewById(R.id.infoCard);
        artistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infoCard.setVisibility(View.VISIBLE);
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
            }
        });

        descButton = findViewById(R.id.descButton);
        descButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infoCard.setVisibility(View.VISIBLE);
            }
        });

        similarButton = findViewById(R.id.similarButton);
        similarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            infoCard.setVisibility(View.VISIBLE);
            }
        });

        // Set an exit transition
        getWindow().setExitTransition(new AutoTransition());
        exhibitImg = findViewById(R.id.exhibitImage);
        exhibitImg.setImageResource(R.drawable.monalisa);
        Intent intent = getIntent();
        // receive the value by getStringExtra() method and
        // key must be same which is send by first activity
        MuseumExhibit exhibit = (MuseumExhibit) intent.getSerializableExtra("exhibit");
        // display the title string on textView
        ((TextView) findViewById(R.id.exhibitTitle)).setText(exhibit.getTitle());
        ((TextView) findViewById(R.id.exhibitYear)).setText("" + exhibit.getYear());
    }

    private void switchActivities() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        finish();
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }
}

