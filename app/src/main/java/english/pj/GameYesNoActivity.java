package english.pj;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import org.json.JSONException;
import org.json.JSONObject;

public class GameYesNoActivity extends Activity {
    JSONObject jsonGameRow;
    JSONObject jsonObjectQuestion;
    private dbHelper myDB;
    SQLiteDatabase db;
    Button btnQuestionYN, bCor, bIncor;
    String qNumber;
    int points, aCorrect;
    String enemy;
    int bot;
    int nextQuestionID, _id;
    int[] questionsIdsForThisRound;
    int playerOrder, qnumb;
    ProgressIndicator mProgressIndicator1;
    boolean threadRunning = false;
    boolean wasThreadStopped = false;
    float max = 1;
    float update = 0;
    boolean answerWasMade = false;
    CountDownTimer countDownTimer;
    MediaPlayer player;
    private Animation animMoveLeft, animMoveRight, animMoveLeftBack, animMoveRightBack,animCircleUp, animCircleDown, animCircleDownAlone;
    Animation animPopUp;
    Animation animPopUp2;
    InterstitialAd mInterstitialAd;
    Context context;
    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mInterstitialAd.loadAd(adRequest);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_yes_no);

        btnQuestionYN = (Button)findViewById(R.id.btnQuestionYesNo);
        bCor = (Button)findViewById(R.id.buttonCorrect);
        bIncor = (Button)findViewById(R.id.buttonIncorrect);
        mProgressIndicator1 = (ProgressIndicator) findViewById(R.id.determinate_progress_indicator1);
        mProgressIndicator1.setForegroundColor(Color.parseColor("#9863bb"));
        mProgressIndicator1.setBackgroundColor(Color.parseColor("#FFFFFF"));
        mProgressIndicator1.setVisibility(View.VISIBLE);

        context = this;
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-1551179296384155/1524672920");

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                if (bot == 1){
                    Intent intent = new Intent(context, PreGameActivity.class);
                    intent.putExtra("bot", bot);
                    intent.putExtra("enemy", enemy);
                    startActivity(intent);
                } else {
                    BWPutAnswerInDB bwPutAnswerInDB = new BWPutAnswerInDB(context);
                    bwPutAnswerInDB.execute(String.valueOf(_id), String.valueOf(pointsPutForInterstEnd), questionForInterstitial, "last");
                }
            }
        });
        if(!mech.isPremium(this)) {
            requestNewInterstitial();
        }

        animMoveRight = AnimationUtils.loadAnimation(this, R.anim.move_right);
        animMoveLeft = AnimationUtils.loadAnimation(this, R.anim.move_left);
        animMoveRightBack = AnimationUtils.loadAnimation(this, R.anim.move_right_back);
        animMoveLeftBack = AnimationUtils.loadAnimation(this, R.anim.move_left_back);
        animCircleUp = AnimationUtils.loadAnimation(this, R.anim.scale);
        animCircleDown = AnimationUtils.loadAnimation(this, R.anim.scaledown);
        animCircleDownAlone = AnimationUtils.loadAnimation(this, R.anim.scaledown);
        animPopUp = AnimationUtils.loadAnimation(this, R.anim.popup);
        animPopUp2 = AnimationUtils.loadAnimation(this, R.anim.popup2);
        animMoveRight.setFillAfter(true);
        animMoveLeft.setFillAfter(true);

        animCircleDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {    }
            @Override
            public void onAnimationEnd(Animation animation) {
                btnQuestionYN.startAnimation(animCircleUp);
                mProgressIndicator1.startAnimation(animCircleUp);
                bCor.startAnimation(animMoveRightBack);
                bIncor.startAnimation(animMoveLeftBack);
                mProgressIndicator1.setValue(0);
                updateScreen();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {     }
        });

        animCircleUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mProgressIndicator1.setVisibility(View.VISIBLE);
                btnQuestionYN.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationEnd(Animation animation) {       }
            @Override
            public void onAnimationRepeat(Animation animation) {     }
        });



        myDB = new dbHelper(this);
        db = myDB.getWritableDatabase();

        if(getIntent().getIntExtra("bot", 0) == 1){
            enemy = getIntent().getStringExtra("enemy");
            bot = getIntent().getIntExtra("bot", 0);
            jsonGameRow = dbHelper.selectGameRowJSON(db, enemy);
            qNumber = getIntent().getStringExtra("qnumber"); //1, 4, 5... номер следующего вопроса
            playerOrder = 1;
        } else {
            //если не бот
            _id = getIntent().getIntExtra("_id", 0);
            questionsIdsForThisRound = new int[4];
            nextQuestionID = questionsIdsForThisRound[1] = getIntent().getIntExtra("q1", 0);
            questionsIdsForThisRound[2] = getIntent().getIntExtra("q2", 0);
            questionsIdsForThisRound[3] = getIntent().getIntExtra("q3", 0);
            qNumber = getIntent().getStringExtra("qnumber"); //1, 4, 5... номер следующего вопроса
            jsonGameRow = mech.getJSONObjFromAllGamesByID(_id);

            if(getIntent().getIntExtra("order", 0) == 1){
                playerOrder = 1;
            } else {
                if(mech.areYouPlayer1(jsonGameRow, this)){
                    playerOrder = 1;
                } else {
                    playerOrder = 2;
                }
            };
        }
        qnumb = Integer.valueOf(qNumber);
        if(qnumb > 3) {
            qnumb = qnumb % 3;
        }

        updateScreen();
        btnQuestionYN.startAnimation(animCircleUp);
        mProgressIndicator1.startAnimation(animCircleUp);
        bCor.startAnimation(animMoveRightBack);
        bIncor.startAnimation(animMoveLeftBack);
    }


    void showPointsAdded(View v){
        if(mech.isSoundOn(this)) {
            player = MediaPlayer.create(this, R.raw.click_correct);
            player.start();
        }

        LinearLayout linLay = (LinearLayout)findViewById(R.id.linLayYNBelow);

        final TextView tvPopUp = new TextView(this);
        tvPopUp.setText("+ 1 балл");

        tvPopUp.setWidth(v.getWidth());
        tvPopUp.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tvPopUp.setX(v.getX());
        tvPopUp.setY(v.getY() + linLay.getY());
        RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.relativeYesNo);
        tvPopUp.setBackgroundColor(Color.TRANSPARENT);
        relativeLayout.addView(tvPopUp);
        tvPopUp.setTextColor(Color.YELLOW);
        tvPopUp.setTextSize(22);
        tvPopUp.startAnimation(animPopUp);
        tvPopUp.setTypeface(null, Typeface.BOLD);


        animPopUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                tvPopUp.startAnimation(animPopUp2);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {            }
        });;

        animPopUp2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                tvPopUp.setVisibility(View.GONE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {            }
        });;

    }

    void hideAnim(){
        btnQuestionYN.startAnimation(animCircleDown);
        mProgressIndicator1.startAnimation(animCircleDownAlone);
        bCor.startAnimation(animMoveLeft);
        bIncor.startAnimation(animMoveRight);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, PreGameActivity.class);
        intent.putExtra("bot", bot);
        intent.putExtra("enemy", enemy);
        intent.putExtra("gameid", _id);
        startActivity(intent);
    }

    void updateScreen(){
        btnQuestionYN.setClickable(false);
        bCor.setClickable(true);
        bIncor.setClickable(true);
        bIncor.setBackgroundResource(R.drawable.btn_transp_selector);
        bCor.setBackgroundResource(R.drawable.btn_transp_selector);
        answerWasMade = false;
        startThread();
        points = 1;

        //we get json question here
        if(bot == 1){
            try {
                nextQuestionID = jsonGameRow.getInt("q" + qNumber);
            } catch (JSONException e) { e.printStackTrace(); }
            jsonObjectQuestion = dbHelper.selectQuestionJSON(db, String.valueOf(nextQuestionID));
        } else {
            nextQuestionID = questionsIdsForThisRound[qnumb];
            jsonObjectQuestion = dbHelper.selectQuestionJSON(db, String.valueOf(nextQuestionID));
        }

        try {
            aCorrect = jsonObjectQuestion.getInt("acor");
            Log.i("MLog", "NNN"+ jsonObjectQuestion.getString("question") + " - " + jsonObjectQuestion.getString("a1"));
            if(aCorrect == 1){
                btnQuestionYN.setText(jsonObjectQuestion.getString("question") + " - " + jsonObjectQuestion.getString("a1"));
            } else if (aCorrect == 3){
                btnQuestionYN.setText(jsonObjectQuestion.getString("question") + " - " + jsonObjectQuestion.getString("a3"));
            } else if (aCorrect == 2){
                btnQuestionYN.setText(jsonObjectQuestion.getString("question") + " - " + jsonObjectQuestion.getString("a4"));
            }else if (aCorrect == 4){
                btnQuestionYN.setText(jsonObjectQuestion.getString("question") + " - " + jsonObjectQuestion.getString("a2"));
            }

            if(mech.isSoundOn(this)) {
                String question = "";
                question = jsonObjectQuestion.getString("question");
                if (question.startsWith("an ")) {
                    question = question.substring(3);
                } else if (question.startsWith("a ")) {
                    question = question.substring(2);
                } else if (question.startsWith("to ")) {
                    question = question.substring(3);
                }
                if(question.equals("catch")){ question = "_catch";}
                if(question.equals("_throw")){ question = "_throw";}
                player = MediaPlayer.create(this, getResources().getIdentifier(question, "raw", getPackageName()));
                player.start();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }



    }

    public void onButtonCorrect(View v) {
        answerWasMade = true;
        if(aCorrect == 1 || aCorrect == 3){
            showPointsAdded(bCor);
            dbHelper.vocabAdd(db, nextQuestionID);
            bCor.setBackgroundResource(R.drawable.button_option_pressed_correct);
            waitAndPutInDB();
        } else {
            points--;
            bCor.setBackgroundResource(R.drawable.button_option_pressed_wrong);
            showIncorrect();
            dbHelper.vocabDelete(db, nextQuestionID);
        }
    }

    public void onButtonIncorrect(View v) {
        answerWasMade = true;
        if(aCorrect == 1 || aCorrect == 3){
            points--;
            bIncor.setBackgroundResource(R.drawable.button_option_pressed_wrong);
            showIncorrect();
        } else {
            showPointsAdded(bIncor);
            bIncor.setBackgroundResource(R.drawable.button_option_pressed_correct);
            waitAndPutInDB();
        }
    }
    Dialog dialog;
    void showIncorrect(){
        wasThreadStopped = true;
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.hint_window_en);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();
        TextView tvViewHintWord = (TextView) dialog.findViewById(R.id.textViewHintWord);
        TextView tvViewHint = (TextView) dialog.findViewById(R.id.textViewHint);

        Button btnHintBuy = (Button)dialog.findViewById(R.id.buttonHintBuyPr);
        try {
            tvViewHintWord.setText(jsonObjectQuestion.getString("question") + " - " + jsonObjectQuestion.getString("a" + aCorrect));
            if(mech.isPremium(this)){
                tvViewHint.setText(jsonObjectQuestion.getString("comment"));
                btnHintBuy.setVisibility(View.GONE);
            } else {
                String hint = jsonObjectQuestion.getString("comment");
                int lenght = hint.length() / 2;
                hint = hint.substring(0, lenght) + ".....";
                tvViewHint.setText(hint);
                btnHintBuy.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {            e.printStackTrace();        }

        Button btnClear = (Button)dialog.findViewById(R.id.buttonClear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
                putAnswerInDB("q" + qNumber + "p" + playerOrder, points);
            }
        });
        final Intent intentBuy = new Intent(this, BuyActivity.class);
        btnHintBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intentBuy);
            }
        });
    }
    int pointsPutForInterstEnd = 0;
    String questionForInterstitial = "";
    private void putAnswerInDB(String question, int pointsPut){
        wasThreadStopped = true;
        if (bot == 1){
            dbHelper.insertAnswer(db, question, pointsPut, enemy, this, 1);
            if (mech.ifLastQinRound(qNumber)) {
                //if interstitial is loaded, show interst. if no, ...
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Intent intent = new Intent(this, PreGameActivity.class);
                    intent.putExtra("bot", bot);
                    intent.putExtra("enemy", enemy);
                    startActivity(intent);
                }
            } else {
                hideAnim();
                qNumber = String.valueOf(Integer.valueOf(qNumber) + 1);
                qnumb++;
               // updateScreen();
            }
        } else {
            //if not bot
            if (mech.ifLastQinRound(qNumber)) {
                //if interstitial is loaded, show interst. if no, ...
                if (mInterstitialAd.isLoaded()) {
                    pointsPutForInterstEnd = pointsPut;
                    questionForInterstitial = question;
                    mInterstitialAd.show();
                } else {
                    BWPutAnswerInDB bwPutAnswerInDB = new BWPutAnswerInDB(this);
                    bwPutAnswerInDB.execute(String.valueOf(_id), String.valueOf(pointsPut), question, "last");
                }

            } else {
                BWPutAnswerInDB bwPutAnswerInDB = new BWPutAnswerInDB(this);
                bwPutAnswerInDB.execute(String.valueOf(_id), String.valueOf(pointsPut), question, "notLast");
                qNumber = String.valueOf(Integer.valueOf(qNumber) + 1);
                qnumb++;
                hideAnim();
                //updateScreen();
            }
        }
    }

    private void startThread() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                threadRunning = true;
                update = 0;
                wasThreadStopped = false;
                while (update <= max) {
                    if (wasThreadStopped == true) {
                        update = 0;
                        break;
                    } else {
                        update += 0.005;
                        updateProgressIndicatorValue();
                        try {
                            Thread.sleep(80);
                        } catch (Exception e) {    }
                    }
                }
                threadRunning = false;

                if(!(answerWasMade)) {
                    timeEndedFromMainThread();
                }
            }
        }).start();
    }

    private void updateProgressIndicatorValue() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressIndicator1.setValue(update);
            }
        });
    }


    void waitAndPutInDB(){
        wasThreadStopped = true;
        try{countDownTimer.cancel();} catch (Exception e){}
        wasThreadStopped = true;
        countDownTimer = new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }
            @Override
            public void onFinish() {
                putAnswerInDB("q" + qNumber + "p" + playerOrder, points);
            }
        }.start();
    }

    private void timeEndedFromMainThread() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                timeEnded();
            }
        });
    }

    void timeEnded() {
        points = 0;
        if (aCorrect == 1 || aCorrect == 3) {
            bCor.setBackgroundResource(R.drawable.button_option_pressed_correct);
        } else {
            bIncor.setBackgroundResource(R.drawable.button_option_pressed_correct);
        }
        btnQuestionYN.setClickable(true);
        bCor.setClickable(false);
        bIncor.setClickable(false);
        points = 0;
        showIncorrect();
    }

    public void onButtonNextYN(View view){
        putAnswerInDB("q" + qNumber + "p" + playerOrder, points);
    }

}