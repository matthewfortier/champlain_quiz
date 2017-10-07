package com.matthewfortier.champlainquiz;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    final private String QUIZ_CATEGORY = "quiz_category";
    private TextView mTextMessage;
    private Button mBuildings;
    private Button mHistory;
    private Button mStats;

    private ImageButton mBuildingsLeaderboard;
    private ImageButton mHistoryLeaderboard;
    private ImageButton mStatsLeaderboard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBuildings = (Button) findViewById(R.id.quiz_buildings_button);
        mBuildings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), QuizActivity.class);
                intent.putExtra(QUIZ_CATEGORY, getString(R.string.buildings_text));
                startActivity(intent);
            }
        });

        mBuildingsLeaderboard = (ImageButton) findViewById(R.id.buildings_leaderboard);
        mBuildingsLeaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), LeaderBoardActivity.class);
                intent.putExtra(QUIZ_CATEGORY, getString(R.string.buildings_text));
                startActivity(intent);
            }
        });

        mHistory = (Button) findViewById(R.id.quiz_history_button);
        mHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), QuizActivity.class);
                intent.putExtra(QUIZ_CATEGORY, getString(R.string.history_text));
                startActivity(intent);
            }
        });

        mHistoryLeaderboard = (ImageButton) findViewById(R.id.history_leaderboard);
        mHistoryLeaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), LeaderBoardActivity.class);
                intent.putExtra(QUIZ_CATEGORY, getString(R.string.history_text));
                startActivity(intent);
            }
        });

        mStats = (Button) findViewById(R.id.quiz_stats_button);
        mStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), QuizActivity.class);
                intent.putExtra(QUIZ_CATEGORY, getString(R.string.statistics_text));
                startActivity(intent);
            }
        });

        mStatsLeaderboard = (ImageButton) findViewById(R.id.statistics_leaderboard);
        mStatsLeaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), LeaderBoardActivity.class);
                intent.putExtra(QUIZ_CATEGORY, getString(R.string.statistics_text));
                startActivity(intent);
            }
        });
    }

}
