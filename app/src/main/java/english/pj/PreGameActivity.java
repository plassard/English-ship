package english.pj;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class PreGameActivity extends Activity {

    TextView tvPl1Name, tvPl2Name, tvPl1Round1, tvPl2Round1, tvPl1Round2, tvPl2Round2, tvPl1Round3, tvPl2Round3, tvPl1Round4, tvPl2Round4, tvRound1, tvRound2, tvRound3, tvRound4, tvTotalScore;
    String enemy;
    Button btnPlay;
    int playerOrder;
    JSONObject jsonGameRow;
    private dbHelper myDB;
    SQLiteDatabase db;
    int currentGameID;
    CountDownTimer countDownTimer;
    boolean resultWasShown;
    int pl1points, pl2points;
    ImageView ivPl1, ivPl2;
    LinearLayout linlayProgress;
    FontChangeCrawler fontChanger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_game);

        fontChanger = new FontChangeCrawler(getAssets(), "fonts/Raleway-Bold.ttf");
        fontChanger.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));

        tvPl1Name = (TextView)findViewById(R.id.textViewPlayer1);
        tvPl2Name  = (TextView)findViewById(R.id.textViewPlayer2);
        tvPl1Round1 = (TextView)findViewById(R.id.textViewRound1Pl1);
        tvPl2Round1 = (TextView)findViewById(R.id.textViewRound1Pl2);
        tvPl1Round2 = (TextView)findViewById(R.id.textViewRound2Pl1);
        tvPl2Round2 = (TextView)findViewById(R.id.textViewRound2Pl2);
        tvPl1Round3 = (TextView)findViewById(R.id.textViewRound3Pl1);
        tvPl2Round3 = (TextView)findViewById(R.id.textViewRound3Pl2);
        tvPl1Round4 = (TextView)findViewById(R.id.textViewRound4Pl1);
        tvPl2Round4 = (TextView)findViewById(R.id.textViewRound4Pl2);
        tvRound1 = (TextView)findViewById(R.id.textViewRound1Categ);
        tvRound2 = (TextView)findViewById(R.id.textViewRound2Categ);
        tvRound3  = (TextView)findViewById(R.id.textViewRound3Categ);
        tvRound4 = (TextView)findViewById(R.id.textViewRound4Categ);
        tvTotalScore = (TextView)findViewById(R.id.textViewTotalScore);

        tvPl1Round1.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/OpenSans-ExtraBold.ttf"));
        tvPl2Round1.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/OpenSans-ExtraBold.ttf"));
        tvPl1Round2.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/OpenSans-ExtraBold.ttf"));
        tvPl2Round2.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/OpenSans-ExtraBold.ttf"));
        tvPl1Round3.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/OpenSans-ExtraBold.ttf"));
        tvPl2Round3.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/OpenSans-ExtraBold.ttf"));
        tvPl1Round4.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/OpenSans-ExtraBold.ttf"));
        tvPl2Round4.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/OpenSans-ExtraBold.ttf"));
        tvTotalScore.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/OpenSans-ExtraBold.ttf"));

        btnPlay = (Button)findViewById(R.id.btnPlay);
        ivPl1 = (ImageView)findViewById(R.id.imgAvaPreGame1);
        ivPl2 = (ImageView)findViewById(R.id.imgAvaPreGame2);
        linlayProgress = (LinearLayout)findViewById(R.id.linlayPreGameProgress);

        myDB = new dbHelper(this);
        db = myDB.getWritableDatabase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        linlayProgress.setVisibility(View.GONE);

        if(getIntent().getIntExtra("bot", 0) == 1){
            enemy = getIntent().getStringExtra("enemy");
            playerOrder = 1;
        } else{
            currentGameID = getIntent().getIntExtra("gameid", 0);
        }

        countDownTimer = new CountDownTimer(60000, 2000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (!resultWasShown) {
                    setAllDataOnScreen();
                    startBWAllGames();
                }
            }
            @Override
            public void onFinish() {  }
        }.start();

        setAllDataOnScreen();
        BWGetChatMessages bwGetChatMessages = new BWGetChatMessages(this);
        bwGetChatMessages.execute();
    }

    void startBWAllGames(){
        BWAllGamesWithMe bwAllGamesWithMe = new BWAllGamesWithMe(this);
        bwAllGamesWithMe.execute();
    }

    @Override
    protected void onPause() {
        countDownTimer.cancel();
        super.onPause();
    }

    public void onPlay(View v){
        linlayProgress.setVisibility(View.VISIBLE);
        if(getIntent().getIntExtra("bot", 0) == 1) {
            if (mech.getNextQuestionToBeAnswered(jsonGameRow, this).equals("7")) {
                Intent intent = new Intent(this, ChooseCategoryOfQuestionsActivity.class);
                intent.putExtra("bot", 1);
                intent.putExtra("enemy", enemy);
                intent.putExtra("round", 3);
                startActivity(intent);
            } else if (mech.getRoundName(jsonGameRow, this).equals("pictures")){
                Intent intent = new Intent(this, GamePicturesActivity.class);
                intent.putExtra("enemy", enemy);
                intent.putExtra("bot", 1);
                intent.putExtra("qnumber", mech.getNextQuestionToBeAnswered(jsonGameRow, this));
                startActivity(intent);
            } else if (mech.getRoundName(jsonGameRow, this).equals("yesno")){
                Intent intent = new Intent(this, GameYesNoActivity.class);
                intent.putExtra("enemy", enemy);
                intent.putExtra("bot", 1);
                intent.putExtra("qnumber", mech.getNextQuestionToBeAnswered(jsonGameRow, this));
                startActivity(intent);
            } else if (mech.getRoundName(jsonGameRow, this).equals("letters")){
                Intent intent = new Intent(this, GameLettersActivity.class);
                intent.putExtra("enemy", enemy);
                intent.putExtra("bot", 1);
                intent.putExtra("qnumber", mech.getNextQuestionToBeAnswered(jsonGameRow, this));
                startActivity(intent);
            } else if (mech.getRoundName(jsonGameRow, this).equals("wordsEN")){
                Intent intent = new Intent(this, GameWordsEnActivity.class);
                intent.putExtra("enemy", enemy);
                intent.putExtra("bot", 1);
                intent.putExtra("qnumber", mech.getNextQuestionToBeAnswered(jsonGameRow, this));
                startActivity(intent);
            } else if (mech.getRoundName(jsonGameRow, this).equals("wordsRU")){
                Intent intent = new Intent(this, GameWordsRuActivity.class);
                intent.putExtra("enemy", enemy);
                intent.putExtra("bot", 1);
                intent.putExtra("qnumber", mech.getNextQuestionToBeAnswered(jsonGameRow, this));
                startActivity(intent);
            } else if (mech.getRoundName(jsonGameRow, this).equals("match")){
                Intent intent = new Intent(this, GameMatchActivity.class);
                intent.putExtra("enemy", enemy);
                intent.putExtra("bot", 1);
                intent.putExtra("qnumber", mech.getNextQuestionToBeAnswered(jsonGameRow, this));
                startActivity(intent);
            } else if (mech.getRoundName(jsonGameRow, this).equals("picturesRU")){
                Intent intent = new Intent(this, GamePicturesRUActivity.class);
                intent.putExtra("enemy", enemy);
                intent.putExtra("bot", 1);
                intent.putExtra("qnumber", mech.getNextQuestionToBeAnswered(jsonGameRow, this));
                startActivity(intent);
            }
        } else {
            //if NOT bot
            Intent intent = new Intent(this, ChooseCategoryOfQuestionsActivity.class);
            Log.i("LOL2", "row is " + jsonGameRow);
            Log.i("LOL2", "next q is " + mech.getNextQuestionToBeAnswered(jsonGameRow, this));

            if(mech.whosTurn(jsonGameRow, this).equals("you") && mech.getNextQuestionToBeAnswered(jsonGameRow, this).equals("1") && jsonGameRow.isNull("r1categ") && mech.areYouPlayer1(jsonGameRow, this)){
                intent = new Intent(this, ChooseCategoryOfQuestionsActivity.class);
            } else if (mech.whosTurn(jsonGameRow, this).equals("you") && mech.getNextQuestionToBeAnswered(jsonGameRow, this).equals("4") && jsonGameRow.isNull("r2categ") && !mech.areYouPlayer1(jsonGameRow, this)){
                intent = new Intent(this, ChooseCategoryOfQuestionsActivity.class);
            } else if(mech.whosTurn(jsonGameRow, this).equals("you") && mech.getNextQuestionToBeAnswered(jsonGameRow, this).equals("7")&& jsonGameRow.isNull("r3categ") && mech.areYouPlayer1(jsonGameRow, this)){
                intent = new Intent(this, ChooseCategoryOfQuestionsActivity.class);
            } else if (mech.whosTurn(jsonGameRow, this).equals("you") && mech.getNextQuestionToBeAnswered(jsonGameRow, this).equals("10")&& jsonGameRow.isNull("r4categ") && !mech.areYouPlayer1(jsonGameRow, this)){
                intent = new Intent(this, ChooseCategoryOfQuestionsActivity.class);
            }

            else if (mech.getRoundNumber(jsonGameRow, this) <= 2){
                intent = new Intent(this, ShowVsEnemyActivity.class);
                intent.putExtra("category", mech.getRoundName(jsonGameRow, this));
                try { intent.putExtra("p1points", jsonGameRow.getString("p1points"));} catch (JSONException e) {e.printStackTrace();}
                try { intent.putExtra("p2points", jsonGameRow.getString("p2points"));} catch (JSONException e) {e.printStackTrace();}
                try { intent.putExtra("p1vocab", jsonGameRow.getString("p1vocab"));} catch (JSONException e) {e.printStackTrace();}
                try { intent.putExtra("p2vocab", jsonGameRow.getString("p2vocab"));} catch (JSONException e) {e.printStackTrace();}
                if(mech.areYouPlayer1(jsonGameRow, this)){
                    intent.putExtra("order", 1);
                    try { intent.putExtra("ava2", jsonGameRow.getString("ava2"));} catch (JSONException e) {e.printStackTrace();}
                } else {
                    intent.putExtra("order", 2);
                    try { intent.putExtra("ava1", jsonGameRow.getString("ava1"));} catch (JSONException e) {e.printStackTrace();}
                }

            }

            else if (mech.getRoundName(jsonGameRow, this).equals("pictures")){
                intent = new Intent(this, GamePicturesActivity.class);
            } else if (mech.getRoundName(jsonGameRow, this).equals("yesno")){
                intent = new Intent(this, GameYesNoActivity.class);
            } else if (mech.getRoundName(jsonGameRow, this).equals("letters")){
                intent = new Intent(this, GameLettersActivity.class);
            } else if (mech.getRoundName(jsonGameRow, this).equals("wordsEN")){
                intent = new Intent(this, GameWordsEnActivity.class);
            } else if (mech.getRoundName(jsonGameRow, this).equals("wordsRU")){
                intent = new Intent(this, GameWordsRuActivity.class);
            } else if (mech.getRoundName(jsonGameRow, this).equals("match")){
                intent = new Intent(this, GameMatchActivity.class);
            } else if (mech.getRoundName(jsonGameRow, this).equals("picturesRU")){
                intent = new Intent(this, GamePicturesRUActivity.class);
            }
            intent.putExtra("bot", 0);
            intent.putExtra("enemy", enemy);
            intent.putExtra("_id", currentGameID);
            intent.putExtra("round", mech.getRoundNumber(jsonGameRow, this));
            String nextQ = mech.getNextQuestionToBeAnswered(jsonGameRow, this);
            try {
                intent.putExtra("q1", jsonGameRow.getInt("q" + nextQ));
                intent.putExtra("q2", jsonGameRow.getInt("q" + (Integer.valueOf(nextQ) + 1)));
                intent.putExtra("q3", jsonGameRow.getInt("q" + (Integer.valueOf(nextQ) + 2)));
            } catch (JSONException e) {                e.printStackTrace();            }

            intent.putExtra("qnumber", mech.getNextQuestionToBeAnswered(jsonGameRow, this));  //1, 2, 3
            startActivity(intent);

        }
    }

    void setAllDataOnScreen(){

        if(getIntent().getIntExtra("bot", 0) == 1){
            jsonGameRow = dbHelper.selectGameRowJSON(db, enemy);
            Log.i("KLK", "here1");
            try {jsonGameRow.getString("_id");
            } catch (JSONException e) { Intent intent = new Intent(this, GamesListActivity.class); startActivity(intent);  }

            tvPl1Name.setText(mech.getLogin(this));
            tvPl2Name.setText(enemy);
            Log.i("KLK", "here2");
            try {
                if(mech.getAvatar(this).equals("") || mech.getAvatar(this).equals(null)) {
                    Picasso.with(this).load(R.drawable.ava).transform(new CircleTransform()).into(ivPl1); } else {
                Picasso.with(this).load(mech.getAvatar(this)).transform(new CircleTransform()).into(ivPl1); }
            } catch (Exception e) { Log.i("KLK", "here4"); Picasso.with(this).load(R.drawable.ava).transform(new CircleTransform()).into(ivPl1); }
            try {
                if (enemy.equals("Ричард")){
                    Picasso.with(this).load(R.drawable.richard).transform(new CircleTransform()).into(ivPl2);
                } else if(enemy.equals("Клавдия Степановна")){
                    Picasso.with(this).load(R.drawable.clavd_step).transform(new CircleTransform()).into(ivPl2);
                } else if(enemy.equals("Анжела")){
                    Picasso.with(this).load(R.drawable.angela).transform(new CircleTransform()).into(ivPl2);
                }
            } catch (Exception e) {
                Picasso.with(this).load(R.drawable.ava).transform(new CircleTransform()).into(ivPl2); }

        } else {
            jsonGameRow = mech.getJSONObjFromAllGamesByID(currentGameID);

            try {
                tvPl1Name.setText(jsonGameRow.getString("player1"));
                tvPl2Name.setText(jsonGameRow.getString("player2"));
                if(jsonGameRow.getString("player1").equals(mech.getLogin(this))){
                    playerOrder = 1; enemy = jsonGameRow.getString("player2");
                } else {
                    playerOrder = 2; enemy = jsonGameRow.getString("player1");
                }
            } catch (Exception e) {
                //это если игра типа закончилась, строку удалили и она еще раз возвращается
                Intent intent = new Intent(this, GamesListActivity.class); startActivity(intent);
            }

            try {
                if(jsonGameRow.getString("ava1").equals("") || jsonGameRow.getString("ava1").equals(null)) {
                    Picasso.with(this).load(R.drawable.ava).transform(new CircleTransform()).into(ivPl1); } else {
                    Picasso.with(this).load(jsonGameRow.getString("ava1")).transform(new CircleTransform()).into(ivPl1);}
            } catch (Exception e) { Picasso.with(this).load(R.drawable.ava).transform(new CircleTransform()).into(ivPl1); }
            try {
                if(jsonGameRow.getString("ava2").equals("") || jsonGameRow.getString("ava2").equals(null)) {Picasso.with(this).load(R.drawable.ava).transform(new CircleTransform()).into(ivPl2); }
                Picasso.with(this).load(jsonGameRow.getString("ava2")).transform(new CircleTransform()).into(ivPl2);
            } catch (Exception e) { Picasso.with(this).load(R.drawable.ava).transform(new CircleTransform()).into(ivPl2); }
        }


        pl1points = pl2points = 0;
        try {

            tvRound1.setText(mech.translateCateg(jsonGameRow.getString("r1categ")));
            tvRound2.setText(mech.translateCateg(jsonGameRow.getString("r2categ")));
            tvRound3.setText(mech.translateCateg(jsonGameRow.getString("r3categ")));
            tvRound4.setText(mech.translateCateg(jsonGameRow.getString("r4categ")));
            /*
            tvRound2.setText(jsonGameRow.getString("r2categ"));
            tvRound3.setText(jsonGameRow.getString("r3categ"));
            tvRound4.setText(jsonGameRow.getString("r4categ"));*/
        } catch (Exception e) { }


        int points = 0;
        String pointsStr = "-";
        try{
            points = jsonGameRow.getInt("q1p1");
            pointsStr = String.valueOf(points);
            points = points + jsonGameRow.getInt("q2p1");
            pointsStr = String.valueOf(points);
            points = points + jsonGameRow.getInt("q3p1");
            pointsStr = String.valueOf(points);
        } catch (Exception e) {  }
       tvPl1Round1.setText(pointsStr);
        pl1points = points;

        try{
            points = 0;
            pointsStr = "-";
            points = jsonGameRow.getInt("q1p2");
            pointsStr = String.valueOf(points);
            points = points + jsonGameRow.getInt("q2p2");
            pointsStr = String.valueOf(points);
            points = points + jsonGameRow.getInt("q3p2");
            pointsStr = String.valueOf(points);
        } catch (Exception e) {  }
        tvPl2Round1.setText(pointsStr);
        pl2points = points;

        try{
            points = 0;
            pointsStr = "-";
            points = jsonGameRow.getInt("q4p1");
            pointsStr = String.valueOf(points);
            points = points + jsonGameRow.getInt("q5p1");
            pointsStr = String.valueOf(points);
            points = points + jsonGameRow.getInt("q6p1");
            pointsStr = String.valueOf(points);
        } catch (Exception e) {  }
        tvPl1Round2.setText(pointsStr);
        pl1points = pl1points + points;

        try{
            points = 0;
            pointsStr = "-";
            points = jsonGameRow.getInt("q4p2");
            pointsStr = String.valueOf(points);
            points = points + jsonGameRow.getInt("q5p2");
            pointsStr = String.valueOf(points);
            points = points + jsonGameRow.getInt("q6p2");
            pointsStr = String.valueOf(points);
        } catch (Exception e) {  }
        tvPl2Round2.setText(pointsStr);
        pl2points = pl2points + points;

        try{
            points = 0;
            pointsStr = "-";
            points = jsonGameRow.getInt("q7p1");
            pointsStr = String.valueOf(points);
            points = points + jsonGameRow.getInt("q8p1");
            pointsStr = String.valueOf(points);
            points = points + jsonGameRow.getInt("q9p1");
            pointsStr = String.valueOf(points);
        } catch (Exception e) {  }
        tvPl1Round3.setText(pointsStr);
        pl1points = pl1points + points;

        try{
            points = 0;
            pointsStr = "-";
            points = jsonGameRow.getInt("q7p2");
            pointsStr = String.valueOf(points);
            points = points + jsonGameRow.getInt("q8p2");
            pointsStr = String.valueOf(points);
            points = points + jsonGameRow.getInt("q9p2");
            pointsStr = String.valueOf(points);
        } catch (Exception e) {  }
        tvPl2Round3.setText(pointsStr);
        pl2points = pl2points + points;

        try{
            points = 0;
            pointsStr = "-";
            points = jsonGameRow.getInt("q10p1");
            pointsStr = String.valueOf(points);
            points = points + jsonGameRow.getInt("q11p1");
            pointsStr = String.valueOf(points);
            points = points + jsonGameRow.getInt("q12p1");
            pointsStr = String.valueOf(points);
        } catch (Exception e) {  }
        tvPl1Round4.setText(pointsStr);
        pl1points = pl1points + points;

        try{
            points = 0;
            pointsStr = "-";
            points = jsonGameRow.getInt("q10p2");
            pointsStr = String.valueOf(points);
            points = points + jsonGameRow.getInt("q11p2");
            pointsStr = String.valueOf(points);
            points = points + jsonGameRow.getInt("q12p2");
            pointsStr = String.valueOf(points);
        } catch (Exception e) {  }
        tvPl2Round4.setText(pointsStr);
        pl2points = pl2points + points;

        tvTotalScore.setText(pl1points + " - " + pl2points);
        if(getIntent().getIntExtra("bot", 0) == 1){
            if (mech.whosTurnNubmer(jsonGameRow) == 1) {
                btnPlay.setText("Играть");
                btnPlay.setClickable(true);
            } else if (mech.whosTurnNubmer(jsonGameRow) == 2) {
                btnPlay.setText("Ожидаем");
                btnPlay.setClickable(false);
            } else {
                btnPlay.setText("Игра закончена");
                btnPlay.setClickable(false);
                showResult();
            }
        }else {
            if (mech.whosTurn(jsonGameRow, this).equals("you")) {
                btnPlay.setText("Играть");
                btnPlay.setClickable(true);
            } else if (mech.whosTurn(jsonGameRow, this).equals("notyou")) {
                btnPlay.setText("Ожидаем");
                btnPlay.setClickable(false);
            } else {
                    btnPlay.setText("Игра закончена");
                    btnPlay.setClickable(false);
                    showResult();

            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, GamesListActivity.class);
        startActivity(intent);
    }

    AlertDialog.Builder adb;
    Dialog dialog;
    void showResult(){

        if(resultWasShown == false) {
            countDownTimer.cancel();
            resultWasShown = true;

            dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.result_window);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.show();

            TextView tvViewResultHeader = (TextView) dialog.findViewById(R.id.textViewResultHeader);
            TextView tvViewResult = (TextView) dialog.findViewById(R.id.textViewResult);
                TextView tvViewResult2 = (TextView) dialog.findViewById(R.id.textViewResult2);
                TextView tvViewResult3 = (TextView) dialog.findViewById(R.id.textViewResult3);
                String header = "";
            ImageView ivFireworks = (ImageView)dialog.findViewById(R.id.imageViewFireworks);
            final Button btnRevansh = (Button)dialog.findViewById(R.id.buttonRevansh);

                if (playerOrder == 1 && (pl1points > pl2points)) {
                    tvViewResult.setText(Html.fromHtml("Поздравляем, вы победили " + "<font color=\"#FD686D\">" + enemy + "</font>"));
                    tvViewResult2.setText("со счетом " + String.valueOf(pl1points) + "-" + String.valueOf(pl2points));
                    tvViewResult3.setText("очки за победу: +3");
                    header = "ПОБЕДА!";
                    ivFireworks.setVisibility(View.VISIBLE);
                    btnRevansh.setVisibility(View.GONE);
                    makeGameSeenOrDeleted("win");
                }
                if (playerOrder == 1 && pl1points == pl2points) {
                    tvViewResult.setText(Html.fromHtml("Ничья с " + "<font color=\"#FD686D\">" + enemy + "</font>"));
                    tvViewResult2.setText("со счетом " + String.valueOf(pl1points) + "-" + String.valueOf(pl2points));
                    tvViewResult3.setText("очки за ничью: +1");
                    header = "Ничья";
                    ivFireworks.setVisibility(View.GONE);
                    makeGameSeenOrDeleted("draw");
                }
                if (playerOrder == 1 && pl1points < pl2points) {
                    tvViewResult.setText(Html.fromHtml("Поражение от " + "<font color=\"#FD686D\">" + enemy + "</font>"));
                    tvViewResult2.setText("со счетом " + String.valueOf(pl1points) + "-" + String.valueOf(pl2points) + " очки: 0");
                    tvViewResult3.setText("не расстраивайтесь, у Вас все получится, попробуйте еще!");
                    header = "Поражение";
                    ivFireworks.setVisibility(View.GONE);
                    makeGameSeenOrDeleted("loss");
                }
                if (playerOrder == 2 && pl1points < pl2points) {
                    tvViewResult.setText(Html.fromHtml("Поздравляем, вы победили " + "<font color=\"#FD686D\">" + enemy + "</font>"));
                    tvViewResult2.setText("со счетом " + String.valueOf(pl1points) + "-" + String.valueOf(pl2points));
                    tvViewResult3.setText("очки за победу: +3");
                    header = "ПОБЕДА!";
                    ivFireworks.setVisibility(View.VISIBLE);
                    btnRevansh.setVisibility(View.GONE);
                    makeGameSeenOrDeleted("win");
                }
                if (playerOrder == 2 && pl1points == pl2points) {
                    tvViewResult.setText(Html.fromHtml("Ничья с " + "<font color=\"#FD686D\">" + enemy + "</font>"));
                    tvViewResult2.setText("со счетом " + String.valueOf(pl1points) + "-" + String.valueOf(pl2points));
                    tvViewResult3.setText("очки за ничью: +1");
                    header = "Ничья";
                    ivFireworks.setVisibility(View.GONE);
                    makeGameSeenOrDeleted("draw");
                }
                if (playerOrder == 2 && pl1points > pl2points) {
                    tvViewResult.setText(Html.fromHtml("Поражение от " + "<font color=\"#FD686D\">" + enemy + "</font>"));
                    tvViewResult2.setText("со счетом " + String.valueOf(pl1points) + "-" + String.valueOf(pl2points) + " очки: 0");
                    tvViewResult3.setText("не расстраивайтесь, у Вас все получится, попробуйте еще!");
                    ivFireworks.setVisibility(View.GONE);
                    header = "Поражение";
                    makeGameSeenOrDeleted("loss");
                }
                tvViewResultHeader.setText(header);
            final Intent intentS = new Intent(this, StatisticsActivity.class);
            Button btnStat = (Button)dialog.findViewById(R.id.buttonStatRes);
            final Dialog dialog2;
            dialog2 = new Dialog(this);
            btnStat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();

                    try {
                        if (8 == Integer.valueOf(BWUserStatistics.json_row.getString("wins")) || 30 == Integer.valueOf(BWUserStatistics.json_row.getString("wins"))){
                            dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog2.setContentView(R.layout.invitation_to_efc);
                            dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog2.setCanceledOnTouchOutside(false);
                            dialog2.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                            dialog2.show();
                            ImageView imgLink = (ImageView)dialog2.findViewById(R.id.imageViewLink);
                            imgLink.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=pp.com.competeinenglish");
                                    Intent intentLink = new Intent(Intent.ACTION_VIEW, uri);
                                    startActivity(intentLink);
                                    dialog2.dismiss();
                                }
                            });
                            Button btnLink = (Button)dialog2.findViewById(R.id.buttonGoToPlaymarket);
                            btnLink.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=pp.com.competeinenglish");
                                    Intent intentLink = new Intent(Intent.ACTION_VIEW, uri);
                                    startActivity(intentLink);
                                    dialog2.dismiss();
                                }
                            });
                            Button btnNo = (Button)dialog2.findViewById(R.id.buttonNoThanks);
                            btnNo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog2.dismiss();
                                }
                            });

                        }
                    }catch (Exception e){}
                }
            });
          //  final Intent intentS = new Intent(this, StatisticsActivity.class);

            if(getIntent().getIntExtra("bot", 0) == 1){
                btnRevansh.setVisibility(View.GONE);
            }
            final String login = mech.getLogin(this);
            final BWInviteToPlay bwInviteToPlay = new BWInviteToPlay(this);
            btnRevansh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bwInviteToPlay.execute(login, enemy);
                    btnRevansh.setText("Вызван");
                    btnRevansh.setClickable(false);
                }
            });


            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                public void onDismiss(DialogInterface dialog) {

                }
            });

             /*
                final Button btnRevansh = (Button) viewResult.findViewById(R.id.buttonReansh);
                if (header.equals("Поражение")) {
                    try {
                        if (jsonGameRow.getString("player2").equals("Анжела") || jsonGameRow.getString("player2").equals("Клавдия Степановна") || jsonGameRow.getString("player2").equals("Ричард")) {
                            btnRevansh.setVisibility(View.GONE);
                        } else {
                            btnRevansh.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    inviteFriend();
                                    try {
                                        if (!(BWGetAnPlayerInfo.jsonObjectAnPl.getString("login").equals(login))) {
                                            btnRevansh.setText("Вызван, ожидаем подтвеждения");
                                            btnRevansh.setClickable(false);
                                        }
                                    } _catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    } _catch (Exception e) {
                    }
                } else {
                    btnRevansh.setVisibility(View.GONE);
                }

                tvViewResult.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();

                    }
                });
                BWGetUserStatistics bwGetUserStatistics = new BWGetUserStatistics(this);
                bwGetUserStatistics.execute(mech.getLogin(this));*/
            }
    }

    void makeGameSeenOrDeleted(String result){
        Log.i("III", "makeG" + result);
        Cursor cursor = db.rawQuery("SELECT * FROM vocab2", null);
        if(getIntent().getIntExtra("bot", 0) == 1){
            db.delete("games", "botname = '" + enemy + "'", null);


            BWPutGameResultWithBot bwPutGameResultWithBot = new BWPutGameResultWithBot(this);
            bwPutGameResultWithBot.execute(result, String.valueOf(cursor.getCount()));
        } else {
            Log.i("III", "else");
            try {
                if ((playerOrder == 1) && jsonGameRow.getString("seenbypl1").equals("0")) {
                    Log.i("III", "here1");
                        BWPutSeenMarkAndResult bwPutSeenMarkAndResult = new BWPutSeenMarkAndResult(this);
                        bwPutSeenMarkAndResult.execute(jsonGameRow.getString("_id"), result, String.valueOf(playerOrder), String.valueOf(cursor.getCount()));

                } else if ((playerOrder == 2) && jsonGameRow.getString("seenbypl2").equals("0")) {
                    Log.i("III", "here2");
                    BWPutSeenMarkAndResult bwPutSeenMarkAndResult = new BWPutSeenMarkAndResult(this);
                    bwPutSeenMarkAndResult.execute(jsonGameRow.getString("_id"), result, String.valueOf(playerOrder), String.valueOf(cursor.getCount()));
                }
            } catch (JSONException e) { }
        }
    }


    public void onShowPlayer1(View v){
        linlayProgress.setVisibility(View.VISIBLE);
        String player = "";
        try { player = jsonGameRow.getString("player1");
        } catch (JSONException e) {  e.printStackTrace();  }
        if(player.equals("Ожидается") || player.equals("Ричард") || player.equals("Клавдия Степановна") || player.equals("Анжела")) {
            } else {
                BWGetAnPlayerInfo bwGetAnPlayerInfo = new BWGetAnPlayerInfo(this);
                bwGetAnPlayerInfo.execute(player, "showUserActivityStart");}
    }

    public void onShowPlayer2(View v){
        String player = "";
        player = tvPl2Name.getText().toString();
        //try { player = jsonGameRow.getString("player2");
        //} _catch (JSONException e) {  e.printStackTrace();  }
        if(player.equals("Ожидается") || player.equals("Ричард") || player.equals("Клавдия Степановна") || player.equals("Анжела")) {
        } else {
            linlayProgress.setVisibility(View.VISIBLE);
            BWGetAnPlayerInfo bwGetAnPlayerInfo = new BWGetAnPlayerInfo(this);
            bwGetAnPlayerInfo.execute(player, "showUserActivityStart");}
    }

}
