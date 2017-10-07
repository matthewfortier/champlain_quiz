package com.matthewfortier.champlainquiz;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.Map;
import java.util.StringTokenizer;

public class LeaderBoardActivity extends AppCompatActivity {

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    private final String TAG = getClass().getSimpleName();

    private LinearLayout mSubmitLayout;

    private TextView mLeaderboardScores;
    private TextView mLeaderboardNames;
    private TextView mScoreView;

    private Button mSubmitScoreButton;
    private Button mShareButton;

    private EditText mScoreEdit;

    public LeaderBoardEntry mEntry;

    private int mScore;
    private String mQuizCategory;

    private final String QUIZ_SCORE = "quiz_score";
    private final String QUIZ_CATEGORY = "quiz_category";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader);

        // Initialize all layouts, buttons, text views, and text edits
        mLeaderboardNames = (TextView) findViewById(R.id.leaders_names);
        mLeaderboardScores = (TextView) findViewById(R.id.leaders_scores);

        mSubmitLayout = (LinearLayout) findViewById(R.id.submit_layout);

        mScoreView = (TextView) findViewById(R.id.score_view);
        mScoreEdit = (EditText) findViewById(R.id.name_edit);

        mSubmitScoreButton = (Button) findViewById(R.id.score_submit);
        mShareButton = (Button) findViewById(R.id.score_share);

        mQuizCategory = getIntent().getStringExtra(QUIZ_CATEGORY);

        // If the user is coming from a quiz with a score
        if (getIntent().hasExtra(QUIZ_SCORE))
        {
            // Grab the score and category from the previous quiz activity
            mScore = getIntent().getIntExtra(QUIZ_SCORE, 0);

            // Set the score view to the value of the quiz score
            mScoreView.setText(""+mScore);

            // Add a listener for the submit button
            mSubmitScoreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Disable the submit button to prevent the user from adding many entries
                    mSubmitScoreButton.setEnabled(false);

                    // Create a new LeaderBoardEntry object to be sent to the Firebase Database
                    mEntry = new LeaderBoardEntry(mScoreEdit.getText().toString(), mScore);

                    // Send object to database
                    myRef.child(mQuizCategory.toLowerCase()).push().setValue(mEntry);

                    // When the data is sent to the database, pull the entire table to display new data
                    myRef.child(mQuizCategory.toLowerCase()).orderByChild("score").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            // Clear out text views for new data
                            mLeaderboardNames.setText("");
                            mLeaderboardScores.setText("");

                            // Iterates through query results and appends them to the views
                            for (DataSnapshot entryDataSnapshot : dataSnapshot.getChildren()) {
                                LeaderBoardEntry value = entryDataSnapshot.getValue(LeaderBoardEntry.class);
                                mLeaderboardNames.setText(value.getInitials() + "\n" + mLeaderboardNames.getText());
                                mLeaderboardScores.setText(value.getScore() + "\n" + mLeaderboardScores.getText());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            System.out.println("The read failed: " + databaseError.getCode());
                        }
                    });
                }
            });

            // Open the share intent if the share button is clicked
            mShareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);

                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, ""+mScore);
                    startActivity(Intent.createChooser(intent, "Share"));
                }
            });
        }
        else
        {
            mSubmitLayout.setVisibility(View.GONE);
        }

        // Initially load the leaderboard before the user submits

        myRef.child(mQuizCategory.toLowerCase()).orderByChild("score").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mLeaderboardNames.setText("");
                mLeaderboardScores.setText("");

                for (DataSnapshot entryDataSnapshot : dataSnapshot.getChildren()) {
                    LeaderBoardEntry value = entryDataSnapshot.getValue(LeaderBoardEntry.class);
                    mLeaderboardNames.setText(value.getInitials() + "\n" + mLeaderboardNames.getText());
                    mLeaderboardScores.setText(value.getScore() + "\n" + mLeaderboardScores.getText());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }
}
