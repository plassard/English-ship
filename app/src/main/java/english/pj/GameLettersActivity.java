package english.pj;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import java.util.Arrays;
import java.util.Random;

public class GameLettersActivity extends Activity implements View.OnClickListener {
    Button btnLetter1, btnLetter2, btnLetter3, btnLetter4, btnLetter5, btnLetter6, btnLetter7, btnLetter8, btnLetter9, btnLetter10, btnLetter11, btnLetter12, btnLetter13, btnLetter14, btnLetter15, btnLetter16, btnLetter17, btnLetter18, btnLetter19, btnLetter20, btnLetter21;
    Button btnQuestion;
    String corrAnswer;
    String currentAnswer;
    TextView tvAnswer, tvPoints;
    int bot;
    String enemy;
    private dbHelper myDB;
    SQLiteDatabase db;
    JSONObject jsonGameRow;
    JSONObject jsonObjectQuestion;
    String qNumber;
    int points;
    String question;
    int playerOrder;
    int nextQuestionID, _id;
    String prefix;
    ProgressIndicator mProgressIndicator1;
    boolean threadRunning = false;
    boolean wasThreadStopped = false;
    float max = 1;
    float update = 0;
    MediaPlayer player;
    private Animation animCircleUp, animCircleDown, animCircleDownAlone;
    String questTemporary;
    InterstitialAd mInterstitialAd;
    Context context;
    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mInterstitialAd.loadAd(adRequest);
    }

    protected void findAllViewElements(){
        btnQuestion = (Button)findViewById(R.id.buttonQuestion);
        tvAnswer = (TextView)findViewById(R.id.textViewAnswer);
        tvPoints = (TextView)findViewById(R.id.textViewWordNumber);

        btnLetter1 = (Button)findViewById(R.id.buttonLetter1);
        btnLetter2 = (Button)findViewById(R.id.buttonLetter2);
        btnLetter3 = (Button)findViewById(R.id.buttonLetter3);
        btnLetter4 = (Button)findViewById(R.id.buttonLetter4);
        btnLetter5 = (Button)findViewById(R.id.buttonLetter5);
        btnLetter6 = (Button)findViewById(R.id.buttonLetter6);
        btnLetter7 = (Button)findViewById(R.id.buttonLetter7);
        btnLetter8 = (Button)findViewById(R.id.buttonLetter8);
        btnLetter9 = (Button)findViewById(R.id.buttonLetter9);
        btnLetter10 = (Button)findViewById(R.id.buttonLetter10);
        btnLetter11 = (Button)findViewById(R.id.buttonLetter11);
        btnLetter12 = (Button)findViewById(R.id.buttonLetter12);
        btnLetter13 = (Button)findViewById(R.id.buttonLetter13);
        btnLetter14 = (Button)findViewById(R.id.buttonLetter14);
        btnLetter15 = (Button)findViewById(R.id.buttonLetter15);
        btnLetter16 = (Button)findViewById(R.id.buttonLetter16);
        btnLetter17 = (Button)findViewById(R.id.buttonLetter17);
        btnLetter18 = (Button)findViewById(R.id.buttonLetter18);
        btnLetter19 = (Button)findViewById(R.id.buttonLetter19);
        btnLetter20 = (Button)findViewById(R.id.buttonLetter20);
        btnLetter21 = (Button)findViewById(R.id.buttonLetter21);

        mProgressIndicator1 = (ProgressIndicator) findViewById(R.id.determinate_progress_indicator1);
        mProgressIndicator1.setForegroundColor(Color.parseColor("#9863bb"));
        mProgressIndicator1.setBackgroundColor(Color.parseColor("#FFFFFF"));

        context = this;
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-1551179296384155/1524672920");

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                if (bot == 1){
                    dbHelper.insertAnswer(db, "q" + qNumber + "p" + playerOrder, points, enemy, context, 5);
                    dbHelper.insertAnswerNo23zeroWithBot(db, "q" + (Integer.valueOf(qNumber) + 1) + "p" + playerOrder, enemy, context);
                    dbHelper.insertAnswerNo23zeroWithBot(db, "q" + (Integer.valueOf(qNumber) + 2) + "p" + playerOrder, enemy, context);
                    Intent intent = new Intent(context, PreGameActivity.class);
                    intent.putExtra("bot", bot);
                    intent.putExtra("enemy", enemy);
                    startActivity(intent);
                } else {
                    //if not bot
                    BWPutAnswerForLettersInDB bwPutAnswerForLettersInDB = new BWPutAnswerForLettersInDB(context);
                    Log.i("LOL2", String.valueOf(_id) + " " + String.valueOf(points) + " " + question + " " +
                            "q" + (Integer.valueOf(qNumber) + 1) + "p" + playerOrder + " " +
                            "q" + (Integer.valueOf(qNumber) + 2) + "p" + playerOrder);

                    bwPutAnswerForLettersInDB.execute(String.valueOf(_id), String.valueOf(points),
                            "q" + qNumber + "p" + playerOrder,
                            "q" + (Integer.valueOf(qNumber) + 1) + "p" + playerOrder,
                            "q" + (Integer.valueOf(qNumber) + 2) + "p" + playerOrder);
                }
            }
        });
        if(!mech.isPremium(this)) {
            requestNewInterstitial();
        }

        //ставим нужные размеры кнопок в зависимости от размера экрана
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int radius = (width / 7) - (width / 50);
        ViewGroup.LayoutParams param;

        param = btnQuestion.getLayoutParams();
        param.width = param.height = ((width / 5) * 4) - 10;
        btnQuestion.setLayoutParams(param);

        param = mProgressIndicator1.getLayoutParams();
        param.width = param.height = (width / 5) * 4;
        mProgressIndicator1.setLayoutParams(param);

        param = btnLetter1.getLayoutParams();
        param.width =  param.height = radius;
        btnLetter1.setLayoutParams(param);

        param = btnLetter2.getLayoutParams();
        param.width =  param.height = radius;
        btnLetter2.setLayoutParams(param);

        param = btnLetter3.getLayoutParams();
        param.width =  param.height = radius;
        btnLetter3.setLayoutParams(param);

        param = btnLetter4.getLayoutParams();
        param.width =  param.height = radius;
        btnLetter4.setLayoutParams(param);

        param = btnLetter5.getLayoutParams();
        param.width =  param.height = radius;
        btnLetter5.setLayoutParams(param);

        param = btnLetter6.getLayoutParams();
        param.width =  param.height = radius;
        btnLetter6.setLayoutParams(param);

        param = btnLetter7.getLayoutParams();
        param.width = param.height = radius;
        btnLetter7.setLayoutParams(param);

        param = btnLetter8.getLayoutParams();
        param.width = param.height = radius;
        btnLetter8.setLayoutParams(param);

        param = btnLetter9.getLayoutParams();
        param.width = param.height = radius;
        btnLetter9.setLayoutParams(param);

        param = btnLetter10.getLayoutParams();
        param.width = param.height = radius;
        btnLetter10.setLayoutParams(param);

        param = btnLetter11.getLayoutParams();
        param.width = param.height = radius;
        btnLetter11.setLayoutParams(param);

        param = btnLetter12.getLayoutParams();
        param.width = param.height = radius;
        btnLetter12.setLayoutParams(param);

        param = btnLetter13.getLayoutParams();
        param.width = param.height = radius;
        btnLetter13.setLayoutParams(param);

        param = btnLetter14.getLayoutParams();
        param.width = param.height = radius;
        btnLetter14.setLayoutParams(param);

        param = btnLetter15.getLayoutParams();
        param.width = param.height = radius;
        btnLetter15.setLayoutParams(param);

        param = btnLetter16.getLayoutParams();
        param.width = param.height = radius;
        btnLetter16.setLayoutParams(param);

        param = btnLetter17.getLayoutParams();
        param.width = param.height = radius;
        btnLetter17.setLayoutParams(param);

        param = btnLetter18.getLayoutParams();
        param.width = param.height = radius;
        btnLetter18.setLayoutParams(param);

        param = btnLetter19.getLayoutParams();
        param.width = param.height = radius;
        btnLetter19.setLayoutParams(param);

        param = btnLetter20.getLayoutParams();
        param.width = param.height = radius;
        btnLetter20.setLayoutParams(param);

        param = btnLetter21.getLayoutParams();
        param.width = param.height = radius;
        btnLetter21.setLayoutParams(param);

        btnLetter1.setOnClickListener(this);
        btnLetter2.setOnClickListener(this);
        btnLetter3.setOnClickListener(this);
        btnLetter4.setOnClickListener(this);
        btnLetter5.setOnClickListener(this);
        btnLetter6.setOnClickListener(this);
        btnLetter7.setOnClickListener(this);
        btnLetter8.setOnClickListener(this);
        btnLetter9.setOnClickListener(this);
        btnLetter10.setOnClickListener(this);
        btnLetter11.setOnClickListener(this);
        btnLetter12.setOnClickListener(this);
        btnLetter13.setOnClickListener(this);
        btnLetter14.setOnClickListener(this);
        btnLetter15.setOnClickListener(this);
        btnLetter16.setOnClickListener(this);
        btnLetter17.setOnClickListener(this);
        btnLetter18.setOnClickListener(this);
        btnLetter19.setOnClickListener(this);
        btnLetter20.setOnClickListener(this);
        btnLetter21.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_letters);

        findAllViewElements();
        animCircleUp = AnimationUtils.loadAnimation(this, R.anim.scale);
        animCircleDown = AnimationUtils.loadAnimation(this, R.anim.scaledown);
        animCircleDownAlone = AnimationUtils.loadAnimation(this, R.anim.scaledown);
        animCircleDown.setFillAfter(true);
        animCircleDownAlone.setFillAfter(true);


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
            nextQuestionID = getIntent().getIntExtra("q1", 0);
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

        points = 5;

        //we get json question here
        if(bot == 1){
            try {
                nextQuestionID = jsonGameRow.getInt("q" + qNumber);
            } catch (JSONException e) { e.printStackTrace(); }
            jsonObjectQuestion = dbHelper.selectQuestionJSON(db, String.valueOf(nextQuestionID));
        } else {
            jsonObjectQuestion = dbHelper.selectQuestionJSON(db, String.valueOf(nextQuestionID));
        }

        try {
                questTemporary = "";
                questTemporary = jsonObjectQuestion.getString("acor"); //1, 2, 3, 4
                questTemporary = jsonObjectQuestion.getString("a" + questTemporary); //жилье
                btnQuestion.setText(questTemporary);
                corrAnswer = jsonObjectQuestion.getString("question");

            if(corrAnswer.startsWith("an ")) {
                    prefix = "an ";
                    question = corrAnswer.substring(3);
                } else if(corrAnswer.startsWith("a ")) {
                    prefix = "a ";
                    question = corrAnswer.substring(2);
                } else if(corrAnswer.startsWith("to ")) {
                    prefix = "to ";
                    question = corrAnswer.substring(3);
                } else {
                    prefix = "";
                    question = corrAnswer;
                }
            } catch (JSONException e) {   e.printStackTrace();     }

        currentAnswer = prefix + "";


        setLettersOnButtons(question);
        btnQuestion.setAnimation(animCircleUp);

        animCircleUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                btnQuestion.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                mProgressIndicator1.setVisibility(View.VISIBLE);

                startThread();
               // playSound();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {     }
        });
        animCircleDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                putAnswerInDB();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {     }
        });
    }
void playSound(){
    if(mech.isSoundOn(this)) {
        player = MediaPlayer.create(this, getResources().getIdentifier(question, "raw", getPackageName()));
        player.start();
    }
}
    @Override
    protected void onPause() {
        super.onPause();
        wasThreadStopped = true;
    }

    private void setLettersOnButtons(String str) {
        tvAnswer.setText(prefix);
        btnLetter1.setVisibility(View.VISIBLE);
        btnLetter2.setVisibility(View.VISIBLE);
        btnLetter3.setVisibility(View.VISIBLE);
        btnLetter4.setVisibility(View.VISIBLE);
        btnLetter5.setVisibility(View.VISIBLE);
        btnLetter6.setVisibility(View.VISIBLE);
        btnLetter7.setVisibility(View.VISIBLE);
        String allChars;
        char letter;
        String dict = "abcdefghijklmnopqrstuvwxyz";
        char charArray[] = dict.toCharArray();
        allChars = str;
        if(str.length() <= 7) {

            while (allChars.length() < 7) {
                Random r = new Random();
                letter = charArray[r.nextInt(26)];
                allChars = allChars + letter;
            }

        } else if(str.length() <= 14) {
            while (allChars.length() < 14) {
                Random r = new Random();
                letter = charArray[r.nextInt(26)];
                allChars = allChars + letter;
            }
        } else {
            while (allChars.length() < 21) {
                Random r = new Random();
                letter = charArray[r.nextInt(26)];
                allChars = allChars + letter;
            }

        }

        char letters[] = allChars.toCharArray();
        Arrays.sort(letters);
        String newWord = new String(letters);

        btnLetter1.setText(String.valueOf(letters[0]));
        btnLetter2.setText(String.valueOf(letters[1]));
        btnLetter3.setText(String.valueOf(letters[2]));
        btnLetter4.setText(String.valueOf(letters[3]));
        btnLetter5.setText(String.valueOf(letters[4]));
        btnLetter6.setText(String.valueOf(letters[5]));
        btnLetter7.setText(String.valueOf(letters[6]));

        if(newWord.length() <= 7) {
            btnLetter8.setVisibility(View.GONE);
            btnLetter9.setVisibility(View.GONE);
            btnLetter10.setVisibility(View.GONE);
            btnLetter11.setVisibility(View.GONE);
            btnLetter12.setVisibility(View.GONE);
            btnLetter13.setVisibility(View.GONE);
            btnLetter14.setVisibility(View.GONE);
            btnLetter15.setVisibility(View.GONE);
            btnLetter16.setVisibility(View.GONE);
            btnLetter17.setVisibility(View.GONE);
            btnLetter18.setVisibility(View.GONE);
            btnLetter19.setVisibility(View.GONE);
            btnLetter20.setVisibility(View.GONE);
            btnLetter21.setVisibility(View.GONE);
        } else if (str.length() <= 14) {
            btnLetter8.setVisibility(View.VISIBLE);
            btnLetter9.setVisibility(View.VISIBLE);
            btnLetter10.setVisibility(View.VISIBLE);
            btnLetter11.setVisibility(View.VISIBLE);
            btnLetter12.setVisibility(View.VISIBLE);
            btnLetter13.setVisibility(View.VISIBLE);
            btnLetter14.setVisibility(View.VISIBLE);
            btnLetter8.setText(String.valueOf(letters[7]));
            btnLetter9.setText(String.valueOf(letters[8]));
            btnLetter10.setText(String.valueOf(letters[9]));
            btnLetter11.setText(String.valueOf(letters[10]));
            btnLetter12.setText(String.valueOf(letters[11]));
            btnLetter13.setText(String.valueOf(letters[12]));
            btnLetter14.setText(String.valueOf(letters[13]));

            btnLetter15.setVisibility(View.GONE);
            btnLetter16.setVisibility(View.GONE);
            btnLetter17.setVisibility(View.GONE);
            btnLetter18.setVisibility(View.GONE);
            btnLetter19.setVisibility(View.GONE);
            btnLetter20.setVisibility(View.GONE);
            btnLetter21.setVisibility(View.GONE);
        } else {
            btnLetter8.setVisibility(View.VISIBLE);
            btnLetter9.setVisibility(View.VISIBLE);
            btnLetter10.setVisibility(View.VISIBLE);
            btnLetter11.setVisibility(View.VISIBLE);
            btnLetter12.setVisibility(View.VISIBLE);
            btnLetter13.setVisibility(View.VISIBLE);
            btnLetter14.setVisibility(View.VISIBLE);
            btnLetter15.setVisibility(View.VISIBLE);
            btnLetter16.setVisibility(View.VISIBLE);
            btnLetter17.setVisibility(View.VISIBLE);
            btnLetter18.setVisibility(View.VISIBLE);
            btnLetter19.setVisibility(View.VISIBLE);
            btnLetter20.setVisibility(View.VISIBLE);
            btnLetter21.setVisibility(View.VISIBLE);
            btnLetter8.setText(String.valueOf(letters[7]));
            btnLetter9.setText(String.valueOf(letters[8]));
            btnLetter10.setText(String.valueOf(letters[9]));
            btnLetter11.setText(String.valueOf(letters[10]));
            btnLetter12.setText(String.valueOf(letters[11]));
            btnLetter13.setText(String.valueOf(letters[12]));
            btnLetter14.setText(String.valueOf(letters[13]));
            btnLetter15.setText(String.valueOf(letters[14]));
            btnLetter16.setText(String.valueOf(letters[15]));
            btnLetter17.setText(String.valueOf(letters[16]));
            btnLetter18.setText(String.valueOf(letters[17]));
            btnLetter19.setText(String.valueOf(letters[18]));
            btnLetter20.setText(String.valueOf(letters[19]));
            btnLetter21.setText(String.valueOf(letters[20]));


        }
    }

    @Override
    public void onClick(View view) {
        Button btn = (Button)findViewById(view.getId());
        String answerLetter = btn.getText().toString();
        int answLength = (currentAnswer + answerLetter).length();

        if((corrAnswer).substring(0, answLength).equals(currentAnswer + answerLetter)){
            currentAnswer = currentAnswer + answerLetter;
            btn.setVisibility(View.INVISIBLE);
            tvAnswer.setText(currentAnswer);
            onAnswerChanged();
        } else {
            points--;
            tvPoints.setText("Баллов: " + points);
            TwinkleButtonWrong(btn);
        }
    }

    private void putAnswerInDB(){
        wasThreadStopped = true;
        if(points == 5) {
            //заносим в слов.запас только, если ответил правильно с первого раза
            dbHelper.vocabAdd(db, nextQuestionID);
        } else {
            dbHelper.vocabDelete(db, nextQuestionID);
        }
        if (bot == 1){
            //if interstitial is loaded, show interst. if no, ...
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                dbHelper.insertAnswer(db, "q" + qNumber + "p" + playerOrder, points, enemy, this, 5);
                dbHelper.insertAnswerNo23zeroWithBot(db, "q" + (Integer.valueOf(qNumber) + 1) + "p" + playerOrder, enemy, this);
                dbHelper.insertAnswerNo23zeroWithBot(db, "q" + (Integer.valueOf(qNumber) + 2) + "p" + playerOrder, enemy, this);
                Intent intent = new Intent(this, PreGameActivity.class);
                intent.putExtra("bot", bot);
                intent.putExtra("enemy", enemy);
                startActivity(intent);
            }
        } else {
            //if interstitial is loaded, show interst. if no, ...
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                //if not bot
                BWPutAnswerForLettersInDB bwPutAnswerForLettersInDB = new BWPutAnswerForLettersInDB(this);
                Log.i("LOL2", String.valueOf(_id) + " " + String.valueOf(points) + " " + question + " " +
                        "q" + (Integer.valueOf(qNumber) + 1) + "p" + playerOrder + " " +
                        "q" + (Integer.valueOf(qNumber) + 2) + "p" + playerOrder);

                bwPutAnswerForLettersInDB.execute(String.valueOf(_id), String.valueOf(points),
                        "q" + qNumber + "p" + playerOrder,
                        "q" + (Integer.valueOf(qNumber) + 1) + "p" + playerOrder,
                        "q" + (Integer.valueOf(qNumber) + 2) + "p" + playerOrder);
            }
        }
        BlockAllButtons();
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
            tvViewHintWord.setText(questTemporary + " - " + corrAnswer);
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
                wasThreadStopped = false;
                putAnswerInDB();
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

    private void onAnswerChanged(){

        if(tvAnswer.getText().length() == corrAnswer.length()) {
            if(points == 5) {
                //заносим в слов.запас только, если ответил правильно с первого раза
                tvAnswer.setBackgroundResource(R.drawable.train_line_correct);
                waitAndPutInDB();
            } else {
                showIncorrect();
            }
        } else {
            tvAnswer.setBackgroundResource(R.drawable.train_line);
        }
    }

    private void BlockAllButtons(){
        btnLetter1.setClickable(false);
        btnLetter2.setClickable(false);
        btnLetter3.setClickable(false);
        btnLetter4.setClickable(false);
        btnLetter5.setClickable(false);
        btnLetter6.setClickable(false);
        btnLetter7.setClickable(false);
        btnLetter8.setClickable(false);
        btnLetter9.setClickable(false);
        btnLetter10.setClickable(false);
        btnLetter11.setClickable(false);
        btnLetter12.setClickable(false);
        btnLetter13.setClickable(false);
        btnLetter14.setClickable(false);
        btnLetter15.setClickable(false);
        btnLetter16.setClickable(false);
        btnLetter17.setClickable(false);
        btnLetter18.setClickable(false);
        btnLetter19.setClickable(false);
        btnLetter20.setClickable(false);
        btnLetter21.setClickable(false);
    }

    protected void TwinkleButtonWrong(final Button btn) {
        btn.setBackgroundResource(R.drawable.btn_red_normal);
        //sound
        tvPoints.setTextColor(getResources().getColor(R.color.red));
        new CountDownTimer(500, 100) {

            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                btn.setBackgroundResource(R.drawable.button_letter);
                tvPoints.setTextColor(getResources().getColor(R.color.white));
            }

        }.start();
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
                            Thread.sleep(100);
                        } catch (Exception e) {    }
                    }
                }
                threadRunning = false;

                if(!wasThreadStopped){
                    points = 0;
                    showIncorrectFromMainThread();}
            }
        }).start();

    }

    private void showIncorrectFromMainThread() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showIncorrect();
            }
        });
    }

    CountDownTimer countDownTimer;
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
                btnQuestion.startAnimation(animCircleDown);
                mProgressIndicator1.startAnimation(animCircleDownAlone);
            }
        }.start();
    }

    private void updateProgressIndicatorValue() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressIndicator1.setValue(update);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, PreGameActivity.class);
        intent.putExtra("bot", bot);
        intent.putExtra("gameid", _id);
        intent.putExtra("enemy", enemy);
        startActivity(intent);
    }

}
