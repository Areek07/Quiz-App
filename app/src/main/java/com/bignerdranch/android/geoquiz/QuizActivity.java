package com.bignerdranch.android.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActiviy";
    private static final String KEY_INDEX = "index";
    private static final String KEY_SCORE = "score";
    private static final String KEY_CHEATER = "cheater";
    private static final String KEY_TOKENS = "tokens";
    private static final String KEY_NO_CHEAT = "no_cheat";
    private static final String KEY_COUNTER = "counter";
    private static final int REQUEST_CODE_CHEAT = 0;

    public static String result = "result";
    public static String cheat_token = "tokens";
    //public static String currentIndex = "index";

    private Button mTrueButton;
    private Button mFalseButton;
    private Button mCheatButton;
    private Button mSkip;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;
    private Button mResetButton;
    private TextView mQuestionTextView;
    private TextView mRemainingTokensTextView;
    private TextView mQuestionCounterView;

    public ProgressBar mProgressBar;
    public int i = 0;

    private Question[] mQuestionBank = new Question[] {
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };

    private boolean[] mIsCheater = new boolean[mQuestionBank.length];
    private int mCurrentIndex = 0;
    private int mCurrentScore = 0;
    private int mRemainingCheatTokens = 3;
    private int mAnswerIndex = mCurrentIndex - 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mCurrentScore = savedInstanceState.getInt(KEY_SCORE, 0);
            mRemainingCheatTokens = savedInstanceState.getInt(KEY_TOKENS, 3);
            mIsCheater = savedInstanceState.getBooleanArray(KEY_CHEATER);
            mAnswerIndex = savedInstanceState.getInt(KEY_NO_CHEAT, mCurrentIndex);
            i = savedInstanceState.getInt(KEY_COUNTER, i);
        }

        mProgressBar = findViewById(R.id.progressBar);

        mQuestionCounterView = findViewById(R.id.question_counter);
        mQuestionCounterView.setText("" + i);

        mSkip = findViewById(R.id.skip_button);
        mSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                transferScore();
            }
        });

        mQuestionTextView = findViewById(R.id.question_text_view);
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextQuestion();
                disableButtons();
            }
        });

        mRemainingTokensTextView = findViewById(R.id.cheat_tokens_text_view);
        mRemainingTokensTextView.setText("Remaining Cheat Tokens: " + mRemainingCheatTokens);

        mTrueButton = findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                checkAnswer(true);
                mTrueButton.setEnabled(false);
                mFalseButton.setEnabled(false);
                mAnswerIndex += 1;
                i += 1;
                if (i < 6) {
                    nextQuestion();
                }
                mProgressBar.incrementProgressBy(16);
                mQuestionCounterView.setText("" + i);
            }
        });

        mFalseButton = findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(false);
                mTrueButton.setEnabled(false);
                mFalseButton.setEnabled(false);
                mAnswerIndex += 1;
                i += 1;
                if (i < 6){
                    nextQuestion();
                }
                mProgressBar.incrementProgressBy(16);
                mQuestionCounterView.setText("" + i);
            }
        });

        mNextButton = findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextQuestion();
                disableButtons();
            }
        });

        mPrevButton = findViewById(R.id.previous_button);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prevQuestion();
                disableButtons();
            }
        });

        mCheatButton = findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
                //int index = mCurrentIndex;
                //intent.putExtra(currentIndex, index);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
                updateQuestion();
            }
        });

        mResetButton = findViewById(R.id.reset_button);
        mResetButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent reset= getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                startActivity(reset);
            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) {
                return;
            }
            mIsCheater[mCurrentIndex] = CheatActivity.wasAnswerShown(data);
            mRemainingCheatTokens--;
            mRemainingTokensTextView.setText("Remaining Cheat Tokens: " + mRemainingCheatTokens);
            if (mRemainingCheatTokens == 0) {
                mCheatButton.setEnabled(false);
            }
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    //place where no String data is stored between orientations
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState() called");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putInt(KEY_SCORE, mCurrentScore);
        savedInstanceState.putInt(KEY_TOKENS, mRemainingCheatTokens);
        savedInstanceState.putBooleanArray(KEY_CHEATER, mIsCheater);
        savedInstanceState.putInt(KEY_COUNTER, i);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    private void prevQuestion() {
        if (mCurrentIndex != 0) {
            mCurrentIndex = (mCurrentIndex - 1) % mQuestionBank.length;
        } else {
            mCurrentIndex = mQuestionBank.length - 1;
        }
        updateQuestion();
    }

    private void nextQuestion() {
        mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;

        updateQuestion();

        //Restart the score when they start over
        if (mCurrentIndex == 0) {
            mCurrentScore = 0;
        }
    }

    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
        toggleAnswerButtonsTo(true);
    }

    private void disableButtons() {
        if (mAnswerIndex >= mCurrentIndex) {
            mTrueButton.setEnabled(false);
            mFalseButton.setEnabled(false);
        }
    }

    //method to check if user has cheated or not
    private void checkAnswer(boolean userPressedTrue) {
        toggleAnswerButtonsTo(false);
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageResId;

        if (mIsCheater[mCurrentIndex]) {
            messageResId = R.string.judgment_toast;
        } else {
            if (userPressedTrue == answerIsTrue) {
                messageResId = R.string.correct_toast;
                mCurrentScore += 1;
                Log.d(TAG, mCurrentScore + "");
            } else {
                messageResId = R.string.incorrect_toast;
            }
        }
        Toast toast = Toast.makeText(this, messageResId, Toast.LENGTH_SHORT);
        toast.show();
        if (mCurrentIndex == mQuestionBank.length - 1) {
            showScore();
        }
    }

    private void showScore() {
        int percentage = (int) (((double)mCurrentScore/mQuestionBank.length)*100);
        String stringScore = "You got " + percentage + "% correct answers";
        Toast.makeText(this, stringScore, Toast.LENGTH_SHORT).show();
    }

    private void transferScore(){
        int score = mCurrentScore;
        int cheats = mRemainingCheatTokens;
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra(result, score);
        intent.putExtra(cheat_token, cheats);
        startActivity(intent);
    }

    private void toggleAnswerButtonsTo(boolean b) {
        if (b == false) {
            mTrueButton.setEnabled(false);
            mFalseButton.setEnabled(false);
        } else if (b == true) {
            mTrueButton.setEnabled(true);
            mFalseButton.setEnabled(true);
        }
    }
}