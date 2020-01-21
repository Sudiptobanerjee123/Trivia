package com.example.trivia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trivia.data.AnswerListAsyncResponse;
import com.example.trivia.data.Questionbank;
import com.example.trivia.model.Question;
import com.example.trivia.model.Score;
import com.example.trivia.util.Prefs;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView questiontextview;
    private TextView questioncountertextview;
    private Button trueButton;
    private Button falseButton;
    private ImageButton nextButton;
    private  ImageButton prevButton;
    private TextView my_score;
    private int currentQuestionIndex = 0;
    private List<Question> questionList;
    private int scorecounter = 0;
    private Score score;
    private Prefs prefs;
    private TextView highscore;
    private Button reset;
    //private int reset_button = 0;



        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        score = new Score();

        prefs = new Prefs(MainActivity.this);


        nextButton = findViewById(R.id.next_button);
        prevButton = findViewById(R.id.prev_button);
        trueButton = findViewById(R.id.true_button);
        falseButton = findViewById(R.id.false_button);
        questioncountertextview = findViewById(R.id.counter_text);
        questiontextview = findViewById(R.id.question_text_view);
        my_score = findViewById(R.id.my_new_score);
        highscore = findViewById(R.id.highest_score);
        nextButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);
        falseButton.setOnClickListener(this);
        trueButton.setOnClickListener(this);
        reset= findViewById(R.id.reset);
        reset.setOnClickListener(this);
        questiontextview.setTextColor(getResources().getColor(R.color.Mycolor));

        //get prev state
            currentQuestionIndex = prefs.getState();

        highscore.setText(MessageFormat.format("Highest Score: {0}", String.valueOf(prefs.getHighScore())));
             questionList = new Questionbank().getQuestion(new AnswerListAsyncResponse()
        {

            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {
               questiontextview.setText(questionArrayList.get(currentQuestionIndex).getAnswer());
               questioncountertextview.setText(currentQuestionIndex + "/" + questionArrayList.size()); //0/234

               Log.d("Inside","ProcessFinished: " + questionArrayList);
            }
        });
    }



    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.prev_button:
                if (currentQuestionIndex>0)
                {
                    currentQuestionIndex = (currentQuestionIndex -1) % questionList.size();
                    updateQuestion();
                }
                break;
            case R.id.next_button:
                 currentQuestionIndex = (currentQuestionIndex+1) % questionList.size();
                updateQuestion();
                break;
            case R.id.true_button:
                checkAnswer(true);
                updateQuestion();
                break;
            case R.id.false_button:
                checkAnswer(false);
                updateQuestion();
                break;
           /* case R.id.reset:
                Reset();

                //highscore.setText(MessageFormat.format("Highest Score: {0}", String.valueOf(prefs.getHighScore())));
                highscore.setText(MessageFormat.format("Highest Score:{0}", reset_button));
                break;*/
        }


    }
    private void AddPoints(){
        scorecounter +=100;
        score.setScore(scorecounter);
        Log.d("Score","ADDPoints:" + score.getScore());
    }
    private void deductPoints(){
        if(scorecounter> 0) {

            scorecounter -= 100;
            score.setScore(scorecounter);
            //Log.d("Score", "ADDPoints:" + score.getScore());
        }
        else
        {
            scorecounter = 0;
            score.setScore(scorecounter);
            //og.d("Score Neg","DeductPoints:" + score.getScore());
        }
    }
    private void updateQuestion(){
     String question = questionList.get(currentQuestionIndex).getAnswer();
     questiontextview.setText(question);
     questioncountertextview.setText(currentQuestionIndex + "/" + questionList.size());

    }
    private void checkAnswer(boolean userChooseCorrect)
    {
        boolean answertrue = questionList.get(currentQuestionIndex).isAnswertrue();
        int toastmessageid=0;
        if (userChooseCorrect == answertrue){
            fadeView();
            AddPoints();
            my_score.setText("Your Score: " + scorecounter );
            toastmessageid = R.string.correctanswer;

        }
        else
        {
            deductPoints();
            my_score.setText("Your Score: " + scorecounter );
            shakeAnimation();
            toastmessageid = R.string.incorrectanswer;
        }
        Toast.makeText(MainActivity.this,toastmessageid,Toast.LENGTH_SHORT).show();


    }
    private void fadeView(){
        final CardView cardView= findViewById(R.id.cardView);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f,0.0f);

        alphaAnimation.setDuration(350);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        cardView.setAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
                currentQuestionIndex = (currentQuestionIndex+1) % questionList.size();
                updateQuestion();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
    private  void shakeAnimation()
    {
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this,
                R.anim.shake_animation);

        final CardView cardView = findViewById(R.id.cardView);
        cardView.setAnimation(shake);

        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
                currentQuestionIndex = (currentQuestionIndex+1) % questionList.size();
                updateQuestion();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    @Override
    protected void onPause() {
        prefs.saveHighScore(score.getScore());
        prefs.setState(currentQuestionIndex);
         super.onPause();
    }
   /* public void Reset()
    {
        reset_button = prefs.getHighScore();
        reset_button = 0;
        highscore.setText(MessageFormat.format("Highest Score:{0}", reset_button));
    }*/

}





