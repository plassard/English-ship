package english.pj;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Random;

public class GameMatchActivity extends Activity implements View.OnClickListener {
    JSONObject jsonGameRow;
    JSONObject jsonObjectQuestion;
    private dbHelper myDB;
    SQLiteDatabase db;
    Button b1, b2, b3, b4, b5, b6, b7, b8;
    String qNumber;
    int points, aCorrect;
    String enemy;
    int bot;
    int nextQuestionID, _id;
    int clickedBtnID;
    String clickedBtnText;
    String[] ruWords;
    String[] enWords;
    int howMuchAnswered;
    int[] questionsIdsForThisRound;
    int playerOrder, qnumb;
    CountDownTimer countDownTimer;
    ProgressBar pbTime;
    int progress = 100;
    TextView tvPoints;
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
        setContentView(R.layout.activity_game_match);
        b1 = (Button)findViewById(R.id.buttonMatch1);
        b2 = (Button)findViewById(R.id.buttonMatch2);
        b3 = (Button)findViewById(R.id.buttonMatch3);
        b4 = (Button)findViewById(R.id.buttonMatch4);
        b5 = (Button)findViewById(R.id.buttonMatch5);
        b6 = (Button)findViewById(R.id.buttonMatch6);
        b7 = (Button)findViewById(R.id.buttonMatch7);
        b8 = (Button)findViewById(R.id.buttonMatch8);
        tvPoints = (TextView)findViewById(R.id.textViewMatchPoints);

        b1.setOnClickListener(this);
        b2.setOnClickListener(this);
        b3.setOnClickListener(this);
        b4.setOnClickListener(this);
        b5.setOnClickListener(this);
        b6.setOnClickListener(this);
        b7.setOnClickListener(this);
        b8.setOnClickListener(this);
        pbTime = (ProgressBar)findViewById(R.id.progressBarTimeMatch);

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

        myDB = new dbHelper(this);
        db = myDB.getWritableDatabase();

        if(getIntent().getIntExtra("bot", 0) == 1){
            enemy = getIntent().getStringExtra("enemy");
            bot = getIntent().getIntExtra("bot", 0);
            jsonGameRow = dbHelper.selectGameRowJSON(db, enemy);
            qNumber = getIntent().getStringExtra("qnumber"); //1, 4, 5... номер следующего вопроса
            try {
                questionsIdsForThisRound = new int[4];
                questionsIdsForThisRound[1] = jsonGameRow.getInt("q" + qNumber);
                questionsIdsForThisRound[2] = jsonGameRow.getInt("q" + Integer.valueOf(qNumber) + 1);
                questionsIdsForThisRound[3] = jsonGameRow.getInt("q" + Integer.valueOf(qNumber) + 2);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
        Random random = new Random();
        questionsIdsForThisRound[0] = random.nextInt(mech.wordsAvailable(this));
        int qIntNumber = Integer.valueOf(qNumber);

        howMuchAnswered = 0;
        points = 0;
        tvPoints.setText("очков: " + points);
        ruWords = new String[4];
        enWords = new String[4];
        //jsonGameRow = dbHelper.selectGameRowJSON(db, enemy);
        int questIDs = 0;
        String questTemporary;

        //заполняем массивы русских и английских слов
        ArrayList<Integer> questionIDs = new ArrayList<>();
        for(int i = 0; i < 4; i++){
            try {
              //  nextQuestionID = jsonGameRow.getInt("q" + qIntNumber);
                questionIDs.add(questionsIdsForThisRound[i]);

                jsonObjectQuestion = dbHelper.selectQuestionJSON(db, String.valueOf(questionsIdsForThisRound[i]));
                enWords[i] = jsonObjectQuestion.getString("question");
                questTemporary = "";
                questTemporary = jsonObjectQuestion.getString("acor");
                questTemporary = jsonObjectQuestion.getString("a" + questTemporary);
                ruWords[i] = questTemporary;
                qIntNumber++;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // берем случайный 4-й вопрос, сначала его _id
        random = new Random();
        int rand1 = random.nextInt(mech.wordsAvailable(this));

        while(questionIDs.contains(rand1)) {
                rand1 = random.nextInt(mech.wordsAvailable(this));
        }
        //потом берем сам вопрос и ответ
        jsonObjectQuestion = dbHelper.selectQuestionJSON(db, String.valueOf(rand1));
        try {
            enWords[3] = jsonObjectQuestion.getString("question");
            questTemporary = "";
            questTemporary = jsonObjectQuestion.getString("acor");
            questTemporary = jsonObjectQuestion.getString("a" + questTemporary);
            ruWords[3] = questTemporary;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //we add two arrays into one here
        String[] mixed = new String[ruWords.length + enWords.length];
        System.arraycopy(ruWords, 0, mixed, 0, ruWords.length);
        System.arraycopy(enWords, 0, mixed, ruWords.length, enWords.length);

        //and mix it here
        Random rnd = new Random();
        for (int i = mixed.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            String a = mixed[index];
            mixed[index] = mixed[i];
            mixed[i] = a;
        }

        b1.setText(mixed[0]);
        b2.setText(mixed[1]);
        b3.setText(mixed[2]);
        b4.setText(mixed[3]);
        b5.setText(mixed[4]);
        b6.setText(mixed[5]);
        b7.setText(mixed[6]);
        b8.setText(mixed[7]);
        clickedBtnText = "";

        countDownTimer = new CountDownTimer(16500, 150) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(progress > 0){
                    progress--;
                    pbTime.setProgress(progress);}
                else {
                    //fail
                }
            }
            @Override
            public void onFinish() {
                putAnswer(); }
        }.start();
    }

    @Override
    public void onClick(View view) {
        Button btn = (Button)findViewById(view.getId());

        if(clickedBtnText.equals("")){
            clickedBtnText = btn.getText().toString();
            clickedBtnID = view.getId();
            btn.setBackgroundResource(R.drawable.btn_transp_pressed);
        } else {
            int indexOfRUWord = 10; // 10 means NULL
            int indexOfENWord = 10; // 10 means NULL
            for (int i = 0; i < ruWords.length; i++) {
                if (clickedBtnText.equals(ruWords[i])) {
                    indexOfRUWord = i;
                }
            }
            for (int i = 0; i < enWords.length; i++) {
                if (clickedBtnText.equals(enWords[i])) {
                    indexOfENWord = i;
                }
            }
            String secondPressedButtonText = btn.getText().toString();
            for (int i = 0; i < ruWords.length; i++) {
                if (secondPressedButtonText.equals(ruWords[i])) {
                    indexOfRUWord = i;
                }
            }
            for (int i = 0; i < enWords.length; i++) {
                if (secondPressedButtonText.equals(enWords[i])) {
                    indexOfENWord = i;
                }
            }
            if(indexOfENWord == indexOfRUWord){
                Log.i("OOO", "correct");
                howMuchAnswered++;
                points = points + 2;
                tvPoints.setText("очков: " + points);
                btn.setVisibility(View.INVISIBLE);
                Button secondButton = (Button)findViewById(clickedBtnID);
                secondButton.setVisibility(View.INVISIBLE);
                clickedBtnText = "";
            } else {
                points--;
                tvPoints.setText("очков: " + points);
                Log.i("OOO", "INcorrect");
            }

            if(howMuchAnswered >= 4){
putAnswer();
            }
        }
    }

    public void putAnswer(){
        countDownTimer.cancel();
        if (bot == 1){
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
            //if not bot
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                BWPutAnswerForLettersInDB bwPutAnswerForLettersInDB = new BWPutAnswerForLettersInDB(this);
                bwPutAnswerForLettersInDB.execute(String.valueOf(_id), String.valueOf(points),
                        "q" + qNumber + "p" + playerOrder,
                        "q" + (Integer.valueOf(qNumber) + 1) + "p" + playerOrder,
                        "q" + (Integer.valueOf(qNumber) + 2) + "p" + playerOrder);
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
}
