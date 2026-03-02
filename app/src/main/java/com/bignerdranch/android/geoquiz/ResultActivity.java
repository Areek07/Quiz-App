package com.bignerdranch.android.geoquiz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();

        int score = intent.getIntExtra(QuizActivity.result,0);
        int cheats_used = intent.getIntExtra(QuizActivity.cheat_token,3);

        TextView resultText = findViewById(R.id.result_text);
        resultText.setText("" + score);

        TextView cheatText = findViewById(R.id.cheats_remaining);
        cheatText.setText(""+ cheats_used);


    }


}
