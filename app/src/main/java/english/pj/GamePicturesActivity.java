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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;

public class GamePicturesActivity extends Activity {
    JSONObject jsonGameRow;
    JSONObject jsonObjectQuestion;
    private dbHelper myDB;
    SQLiteDatabase db;
    Button b1, b2, b3, b4;
    String qNumber;
    int points, aCorrect;
    String enemy;
    int bot, _id;
    int nextQuestionID;
    TextView tvHeader;
    int[] questionsIdsForThisRound;
    int playerOrder, qnumb;
    CountDownTimer countDownTimer;
    ProgressBar pbTime;
    ImageView iv;
    MediaPlayer player;
    private Animation anim1, anim2, anim3, anim4, animOut, animOutAlone, animIn;
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
        setContentView(R.layout.activity_game_pictures);

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

        tvHeader = (TextView)findViewById(R.id.textViewAboveGame);

        b1 = (Button)findViewById(R.id.button1WordsRu);
        b2 = (Button)findViewById(R.id.button2WordsRu);
        b3 = (Button)findViewById(R.id.button3WordsRu);
        b4 = (Button)findViewById(R.id.button4WordsRu);
        pbTime = (ProgressBar)findViewById(R.id.progressBarTime);
        iv = (ImageView)findViewById(R.id.imageViewPicture);

        anim1 = AnimationUtils.loadAnimation(this, R.anim.transparency);
        anim2 = AnimationUtils.loadAnimation(this, R.anim.transparency);
        anim3 = AnimationUtils.loadAnimation(this, R.anim.transparency);
        anim4 = AnimationUtils.loadAnimation(this, R.anim.transparency);
        animIn = AnimationUtils.loadAnimation(this, R.anim.transparency);
        animOut = AnimationUtils.loadAnimation(this, R.anim.transparency_out);
        animOutAlone = AnimationUtils.loadAnimation(this, R.anim.transparency_out);
        animPopUp = AnimationUtils.loadAnimation(this, R.anim.popup);
        animPopUp2 = AnimationUtils.loadAnimation(this, R.anim.popup2);
        animPopUp.setFillAfter(true);

        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                b1.startAnimation(animOutAlone);
                b2.startAnimation(animOutAlone);
                b3.startAnimation(animOutAlone);
                b4.startAnimation(animOutAlone);
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                iv.startAnimation(animIn);
                b1.startAnimation(anim1);
                b2.setVisibility(View.INVISIBLE);
                b3.setVisibility(View.INVISIBLE);
                b4.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {     }
        });

        anim1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                b1.setVisibility(View.VISIBLE);
                iv.setVisibility(View.VISIBLE);
                updateScreen();

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                b2.startAnimation(anim2);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {            }
        });;

        anim2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                b2.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                b3.startAnimation(anim3);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {            }
        });;
        anim3.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                b3.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                b4.startAnimation(anim4);
                b4.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {            }
        });;

        anim4.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                b3.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationEnd(Animation animation) {

                //startThread();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {            }
        });;



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
        //updateScreen();
        iv.startAnimation(animIn);
        b1.startAnimation(anim1);
    }

    int progress = 300;

    void updateScreen(){
        iv.setClickable(false);
        try{countDownTimer.cancel();} catch (Exception e){}
        progress = 300;
        countDownTimer = new CountDownTimer(17500, 50) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(progress > 0){
                progress--;
                pbTime.setProgress(progress);}
                else {
                    //fail
                    cancel();
                    timeEnded();
                }
            }
            @Override
            public void onFinish() {
                points = 0;
                showIncorrect();
            }
        }.start();

        b1.setBackgroundResource(R.drawable.btn_transp_selector);
        b2.setBackgroundResource(R.drawable.btn_transp_selector);
        b3.setBackgroundResource(R.drawable.btn_transp_selector);
        b4.setBackgroundResource(R.drawable.btn_transp_selector);
        b1.setClickable(true);
        b2.setClickable(true);
        b3.setClickable(true);
        b4.setClickable(true);

        points = 3;

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
            String question = "";
            aCorrect = jsonObjectQuestion.getInt("acor");
            question = jsonObjectQuestion.getString("question");
            tvHeader.setText(question);

            //put respective picture on button
            if(question.startsWith("an ")) {
                question = question.substring(3);
            } else if(question.startsWith("a ")) {
                question = question.substring(2);
            } else if(question.startsWith("to ")) {
                question = question.substring(3);  }
            String pictName = "_" + question;
            //btnPictQuestion.setBackgroundResource(getResources().getIdentifier(pictName, "drawable", getPackageName()));
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int displWidth = displaymetrics.widthPixels;

            playSound();
            Log.i("PICTT", "picture is " + pictName);
            Picasso.with(getApplicationContext())
                    .load(getResources().getIdentifier(pictName, "drawable", getPackageName()))
                    .resize(displWidth, 0)
                    .transform(new RoundedTransformation(50, 0))
                    .into(iv);
            b1.setText(jsonObjectQuestion.getString("a1"));
            b2.setText(jsonObjectQuestion.getString("a2"));
            b3.setText(jsonObjectQuestion.getString("a3"));
            b4.setText(jsonObjectQuestion.getString("a4"));
        } catch (JSONException e) {            e.printStackTrace();        }
    }

    void playSound(){
        if(mech.isSoundOn(this)) {
            String question = "";
            try {
                question = jsonObjectQuestion.getString("question");
                if (question.startsWith("an ")) {
                    question = question.substring(3);
                } else if (question.startsWith("a ")) {
                    question = question.substring(2);
                } else if (question.startsWith("to ")) {
                    question = question.substring(3);
                }
                if(question.equals("catch")){ question = "_catch";}
                if(question.equals("throw")){ question = "_throw";}
                player = MediaPlayer.create(this, getResources().getIdentifier(question, "raw", getPackageName()));
                player.start();
            } catch (JSONException e) {
                e.printStackTrace();    }
        }
    }

    private Animation animPopUp, animPopUp2;

    void showPointsAdded(View v){
        if(mech.isSoundOn(this)) {
            player = MediaPlayer.create(this, R.raw.click_correct);
            player.start();
        }
        v.getX();
        final TextView tvPopUp = new TextView(this);
        if(points == 1) {
            tvPopUp.setText("+ " + points + " балл");
        } else {
            tvPopUp.setText("+ " + points + " балла");
        };

        tvPopUp.setWidth(v.getWidth());
        tvPopUp.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tvPopUp.setX(v.getX());
        tvPopUp.setY(v.getY());
        RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.RelativePicturesGame);
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

    void playWrongMedia(){
       /* if(mech.isSoundOn(this)) {
            player = MediaPlayer.create(this, R.raw.wrong);
            player.start();
        }*/
    }
    public void onButton1(View v) {

        if(aCorrect == 1){
            showPointsAdded(v);
            if(points == 3) {
                //заносим в слов.запас только, если ответил правильно с первого раза
                b1.setBackgroundResource(R.drawable.button_option_pressed_correct);
                waitAndPutInDB();
                //putAnswerInDB("q" + qNumber + "p" + playerOrder, points);
            } else {
                b1.setBackgroundResource(R.drawable.button_option_pressed_correct);
                showIncorrect();
            }
        } else {
            playWrongMedia();
            points--;
            b1.setVisibility(View.INVISIBLE);
        }
    }
    public void onButton2(View v) {

        if(aCorrect == 2){
            showPointsAdded(v);
            if(points == 3) {
                //заносим в слов.запас только, если ответил правильно с первого раза
                b2.setBackgroundResource(R.drawable.button_option_pressed_correct);
                waitAndPutInDB();
                //putAnswerInDB("q" + qNumber + "p" + playerOrder, points);
            } else {
                b2.setBackgroundResource(R.drawable.button_option_pressed_correct);
                showIncorrect();
            }
        } else {
            playWrongMedia();
            points--;
            b2.setVisibility(View.INVISIBLE);
        }
    }

    public void onButton3(View v) {

        if(aCorrect == 3){
            showPointsAdded(v);
            if(points == 3) {
                //заносим в слов.запас только, если ответил правильно с первого раза
                b3.setBackgroundResource(R.drawable.button_option_pressed_correct);
                waitAndPutInDB();
                //putAnswerInDB("q" + qNumber + "p" + playerOrder, points);
            } else {
                b3.setBackgroundResource(R.drawable.button_option_pressed_correct);
                showIncorrect();
            }
        } else {
            playWrongMedia();
            points--;
            b3.setVisibility(View.INVISIBLE);
        }
    }
    public void onButton4(View v) {
        if(aCorrect == 4){
            showPointsAdded(v);
            if(points == 3) {
                //заносим в слов.запас только, если ответил правильно с первого раза
                b4.setBackgroundResource(R.drawable.button_option_pressed_correct);
                waitAndPutInDB();
                //putAnswerInDB("q" + qNumber + "p" + playerOrder, points);
            } else {
                b4.setBackgroundResource(R.drawable.button_option_pressed_correct);
                showIncorrect();
            }
        } else {
            playWrongMedia();
            points--;
            b4.setVisibility(View.INVISIBLE);
        }
    }

    Dialog dialog;
    void showIncorrect(){
        countDownTimer.cancel();
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

    private void putAnswerInDB(String question, int pointsPut) {
        if(points == 3) {
            //заносим в слов.запас только, если ответил правильно с первого раза
            dbHelper.vocabAdd(db, nextQuestionID);
        } else {
            dbHelper.vocabDelete(db, nextQuestionID);
        }

        if (bot == 1){
            dbHelper.insertAnswer(db, question, pointsPut, enemy, this, 3);
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
            qNumber = String.valueOf(Integer.valueOf(qNumber) + 1);
            qnumb++;
                iv.startAnimation(animOut);
            //updateScreen();
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
                iv.startAnimation(animOut);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, PreGameActivity.class);
        intent.putExtra("bot", bot);
        intent.putExtra("gameid", _id);
        intent.putExtra("enemy", enemy);
        startActivity(intent);
    }

    void waitAndPutInDB(){
        try{countDownTimer.cancel();} catch (Exception e){}

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

    void timeEnded() {
            points = 0;
            if (aCorrect == 1) {
                b1.setBackgroundResource(R.drawable.button_option_pressed_correct);
            }
            if (aCorrect == 2) {
                b2.setBackgroundResource(R.drawable.button_option_pressed_correct);
            }
            if (aCorrect == 3) {
                b3.setBackgroundResource(R.drawable.button_option_pressed_correct);
            }
            if (aCorrect == 4) {
                b4.setBackgroundResource(R.drawable.button_option_pressed_correct);
            }
            iv.setClickable(true);
        b1.setClickable(false);
        b2.setClickable(false);
        b3.setClickable(false);
        b4.setClickable(false);
    }
    public void onNextPict(View view){
        putAnswerInDB("q" + qNumber + "p" + playerOrder, points);
    }

}
