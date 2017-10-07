package com.matthewfortier.champlainquiz;

import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.media.Image;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class QuizActivity extends AppCompatActivity {

    public ArrayList<Question> mQuestions;

    private LinearLayout mFillLayout;
    private TableLayout mMultipleLayout;

    private EditText mFillText;
    private TextView mQuestionText;

    private Button mHintButton;
    private Button mFillButton;

    private ArrayList<Button> mAnswerButtons;
    private ArrayList<ImageView> mImageButtons;

    private int mDefaultTextColor;
    private int mQuestionIndex;
    private int mCorrectIndex;
    private int mCorrectQuestions = 0;

    private String mCorrectAnswer;
    private String mQuizCategory;

    private boolean mHintGiven;

    private final String TAG = getClass().getSimpleName();
    private final String QUIZ_SCORE = "quiz_score";
    private final String QUIZ_CATEGORY = "quiz_category";

    @Override
    protected void onResume() {
        // Resets the quiz activity if the user goes back and tries to increase their score
        String action = getIntent().getAction();
        // Prevent endless loop by adding a unique action, don't restart if action is present
        if(action == null || !action.equals("Already created")) {
            Intent intent = new Intent(this, QuizActivity.class);
            intent.putExtra(QUIZ_CATEGORY, mQuizCategory);
            startActivity(intent);
            finish();
        }
        // Remove the unique action so the next time onResume is called it will restart
        else
            getIntent().setAction(null);

        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Set action "Already Created"
        getIntent().setAction("Already created");

        // Grab the quiz category from the main activity
        mQuizCategory = getIntent().getStringExtra(QUIZ_CATEGORY);

        // Initialize all UI elements and class members
        mMultipleLayout = (TableLayout) findViewById(R.id.multiple_layout);
        mFillLayout = (LinearLayout) findViewById(R.id.fill_layout);

        mFillButton = (Button) findViewById(R.id.fill_button);
        mFillText = (EditText) findViewById(R.id.fill_text);

        mQuestions = new ArrayList<Question>();
        mQuestionText = (TextView) findViewById(R.id.quiz_question);

        mAnswerButtons = new ArrayList<Button>();

        // Add the buttons and image buttons to separate arrays for easy access
        mAnswerButtons.add((Button) findViewById(R.id.option_1));
        mAnswerButtons.add((Button) findViewById(R.id.option_2));
        mAnswerButtons.add((Button) findViewById(R.id.option_3));
        mAnswerButtons.add((Button) findViewById(R.id.option_4));

        mImageButtons = new ArrayList<>();

        mImageButtons.add((ImageView) findViewById(R.id.image_1));
        mImageButtons.add((ImageView) findViewById(R.id.image_2));
        mImageButtons.add((ImageView) findViewById(R.id.image_3));
        mImageButtons.add((ImageView) findViewById(R.id.image_4));

        mHintButton = (Button) findViewById(R.id.hint_button);

        // Save the default text color for replacing the color later on
        mDefaultTextColor = mAnswerButtons.get(1).getTextColors().getDefaultColor();

        // Set initial values
        mQuestionIndex = 0;
        mCorrectAnswer = null;
        mHintGiven = false;

        // Parsel corresponding category xml file
        XmlResourceParser stream;
        int id = getResources().getIdentifier(mQuizCategory.toLowerCase(), "xml", getPackageName());
        stream = getResources().getXml(id);
        try {
            parseXml(stream);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        // Apply first question to screen
        setQuestion();

        // Add click listeners to each button so the answers can be checked
        mAnswerButtons.get(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(0);
            }
        });

        mAnswerButtons.get(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(1);
            }
        });

        mAnswerButtons.get(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(2);
            }
        });

        mAnswerButtons.get(3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(3);
            }
        });

        // Initialize the submit button for fill in the blank asnwers
        mFillButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(-1);
            }
        });

        // Initiale the hint button
        mHintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mHintGiven)
                    giveHint();
            }
        });

    }

    private void giveHint() {
        Random rand = new Random();
        int i = -1;

        // Select answer that is not the answer
        do{
            i = rand.nextInt(4);
        } while(i == mCorrectIndex);

        // Disable button and grey it out
        mAnswerButtons.get(i).setEnabled(false);
        mAnswerButtons.get(i).setAlpha(.5f);
        mImageButtons.get(i).setAlpha(.5f);

        // Get rid of the hint button and note that a hint was given
        mHintButton.setVisibility(View.GONE);
        mHintGiven = true;
    }

    private void setQuestion() {

        // Get the correct answer and set the question text
        mCorrectAnswer = mQuestions.get(mQuestionIndex).getAnswer();
        mQuestionText.setText(mQuestions.get(mQuestionIndex).getQuestion());

        // Get the question options and randomize their placement
        Question currentQuestion = mQuestions.get(mQuestionIndex);
        ArrayList<String> questionOptions = currentQuestion.getOptions();
        Collections.shuffle(questionOptions);

        // Determine the type of question
        if (currentQuestion.getType().equals("Fill"))
        {
            // If it is a fill question, wee need to replace the multiple layout with the fill one
            mMultipleLayout.setVisibility(View.GONE);
            mFillLayout.setVisibility(View.VISIBLE);
            mFillText.setText("");
        }
        else
        {
            // If it is a multiple one (image or text) hide the fill layout and show the mulitple
            mMultipleLayout.setVisibility(View.VISIBLE);
            mFillLayout.setVisibility(View.GONE);

            // For each button, show it and set the index of the correct answer
            for(int i = 0; i < 4; i++)
            {
                mAnswerButtons.get(i).setEnabled(true);
                mAnswerButtons.get(i).setAlpha(1f);
                mAnswerButtons.get(i).getBackground().setAlpha(255);
                mAnswerButtons.get(i).setTextColor(mDefaultTextColor);
                mAnswerButtons.get(i).setText(questionOptions.get(i));

                if (questionOptions.get(i).equals(mCorrectAnswer))
                    mCorrectIndex = i;
            }

            // If it is an image question, hide the button to show the image
            if (currentQuestion.getType().equals("Image"))
            {
                for(int i = 0; i < 4; i++)
                {
                    mImageButtons.get(i).setAlpha(1f);
                    mAnswerButtons.get(i).getBackground().setAlpha(0);
                    mAnswerButtons.get(i).setTextColor(Color.TRANSPARENT);
                    int imgId = getResources().getIdentifier(questionOptions.get(i), "drawable", getPackageName());
                    mImageButtons.get(i).setBackground(getResources().getDrawable(imgId));
                }
            }
            else
            {
                // Otherwise hide the image
                for(int i = 0; i < 4; i++)
                {
                    mImageButtons.get(i).setBackground(null);
                }
            }
        }
    }

    private void checkAnswer(int i) {

        Question currentQuestion = mQuestions.get(mQuestionIndex);
        Context context;
        CharSequence text;

        int sound = 0;

        // if the question type is fill, check the correct to the given
        if (currentQuestion.getType().equals("Fill") && mFillText.getText().toString().equalsIgnoreCase(mCorrectAnswer))
        {
            context = getApplicationContext();
            text = "Correct!";
            sound = R.raw.correct;
            mCorrectQuestions++;
        }
        // Check if the answer pressed is the correct answer
        else if (i >= 0 && mAnswerButtons.get(i).getText().equals(mCorrectAnswer))
        {
            context = getApplicationContext();
            text = "Correct!";
            sound = R.raw.correct;
            mCorrectQuestions++;
        }
        // Wrong answer
        else
        {
            context = getApplicationContext();
            sound = R.raw.wrong;
            text = "Incorrect!";
        }

        //Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        //toast.show();

        // Play success or wrong sound
        MediaPlayer mp = MediaPlayer.create(context, sound);
        mp.start();

        // Make sure we are not at the end of the questions
        // If so, load the leaderboard and send the category and score
        if (mQuestionIndex == mQuestions.size() - 1)
        {
            Log.d(TAG, "Start New Intent");
            Intent intent = new Intent(getBaseContext(), LeaderBoardActivity.class);
            intent.putExtra(QUIZ_SCORE, mCorrectQuestions);
            intent.putExtra(QUIZ_CATEGORY, mQuizCategory);
            startActivity(intent);
        }
        // Otherwise load the next quiestion
        else
        {
            mQuestionIndex++;
            setQuestion();
        }

    }

    private void parseXml(XmlResourceParser parser) throws XmlPullParserException, IOException {

        // Get initial event type
        int eventType = parser.getEventType();

        String q = null;
        String a = null;
        String t = null;

        ArrayList<String> o = new ArrayList<String>();

        // While it is not the end of the document
        while (eventType != XmlPullParser.END_DOCUMENT)
        {

            if (eventType == XmlPullParser.START_DOCUMENT)
            {
                Log.d(TAG, "Start document");
            }
            // If the event is a start tag, grab the text
            else if (eventType == XmlPullParser.START_TAG)
            {
                Log.d(TAG, "Start tag " + parser.getName());
                if (parser.getName().equals("text"))
                    q = parser.nextText();
                else if (parser.getName().equals("answer"))
                    a = parser.nextText();
                else if (parser.getName().equals("type"))
                    t = parser.nextText();
                else if (parser.getName().equals("option"))
                    o.add(parser.nextText());
            }
            // If the event is an end tag and that tag is the question tag
            // Add it to the questions array
            else if (eventType == XmlPullParser.END_TAG)
            {

                if (parser.getName().equals("question"))
                {
                    o.add(a);
                    mQuestions.add(new Question(q, a, o, t));
                    o = new ArrayList<String>();
                }

                Log.d(TAG, "End tag " + parser.getName());
            }
            else if (eventType == XmlPullParser.TEXT)
            {
                Log.d(TAG, "Text " + parser.getText());
            }

            // Advance to next event
            eventType = parser.next();
        }
    }
}
