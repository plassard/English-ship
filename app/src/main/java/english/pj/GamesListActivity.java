package english.pj;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import static android.text.Html.fromHtml;


public class GamesListActivity extends Activity {
    private dbHelper myDB;
    SQLiteDatabase db;
    LinearLayout layoutYourTurn, layoutWaitingPlayers, layoutCompleted;
    ArrayList<String[]> gamesListYourTurnNew = new ArrayList<String[]>();;
    ArrayList<String[]> gamesListWaitingNew = new ArrayList<String[]>();;
    ArrayList<String[]> gamesListCompletedNew = new ArrayList<String[]>();;
    int idsForYourTurn[] = new int[20];
    int idsForWaitingTurn[] = new int[20];
    int idsForCompletedGames[] = new int[20];
    int countAssignedIDs;
    String[] interimStr;
    View viewResult;
    LayoutInflater inflater;
    Button btnYourTurn, btnWaiting, btnZaversh;
    JSONArray json_all_games_array;
    CountDownTimer countDownTimer;
    FontChangeCrawler fontChanger;
    LinearLayout linlaHeaderProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_list);
        fontChanger = new FontChangeCrawler(getAssets(), "fonts/Raleway-Bold.ttf");
        fontChanger.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));
        TextView tvHeader = (TextView)findViewById(R.id.textViewGLHeader);
        tvHeader.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Raleway-Black.ttf"));
        layoutYourTurn = (LinearLayout)findViewById(R.id.LinLayoutGamesYourTurn);
        layoutWaitingPlayers = (LinearLayout)findViewById(R.id.LinLayoutWaitingPlayers);
        layoutCompleted = (LinearLayout)findViewById(R.id.LinLayoutCompletedGames);
        btnYourTurn = (Button) findViewById(R.id.btnSix1);
        btnWaiting = (Button) findViewById(R.id.btnSix3);
        btnZaversh = (Button) findViewById(R.id.btnZaversh);

        myDB = new dbHelper(this);
        db = myDB.getWritableDatabase();
        linlaHeaderProgress = (LinearLayout)findViewById(R.id.linlaHeaderProgress);
    }

    @Override
    protected void onResume() {
        super.onResume();
        linlaHeaderProgress.setVisibility(View.GONE);
        countDownTimer = new CountDownTimer(600000, 2000) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateLists();
                startBWAllGames(); }

            @Override
            public void onFinish() {
                updateLists();           }
        }.start();

        updateLists();
        BWGetChatMessages bwGetChatMessages = new BWGetChatMessages(this);
        bwGetChatMessages.execute();
    }

    void startBWAllGames(){
        if(!isInvitationShown){
        BWAllGamesWithMe bwAllGamesWithMe = new BWAllGamesWithMe(this);
        bwAllGamesWithMe.execute();}
    }

    @Override
    protected void onPause() {
        countDownTimer.cancel();
        super.onPause();
    }

    Dialog dialog;


    static boolean isInvitationShown = false;
    void showInvitation(JSONObject jsonObject){

        //отображаем приглашение
        if(!isInvitationShown){
            isInvitationShown = true;
            dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.invitationdialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.show();
            Log.i("KLK", "on click000");
            TextView tvWhoInvite = (TextView)dialog.findViewById(R.id.tvWhoInvites);
            try {
                invitationPlayer = jsonObject.getString("player2");
            } catch (JSONException e) {  e.printStackTrace();  }

            if (android.os.Build.VERSION.SDK_INT >= 24) {
                tvWhoInvite.setText(Html.fromHtml("<strong>" + invitationPlayer + "</strong>" + " приглашает Вас сразиться. Вы принимаете вызов?",Html.FROM_HTML_MODE_LEGACY));
            } else {
                tvWhoInvite.setText(fromHtml("<strong>" + invitationPlayer + "</strong>" + " приглашает Вас сразиться. Вы принимаете вызов?"));
            }

            Button btnAccept = (Button)dialog.findViewById(R.id.buttonAcceptInvitation);
            Button btnDecline = (Button)dialog.findViewById(R.id.buttonDeclineInvitation);
            btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BWAcceptInvitationToGame bwAcceptInvitationToGame = new BWAcceptInvitationToGame(getApplicationContext());
                    bwAcceptInvitationToGame.execute(mech.getLogin(getApplicationContext()), invitationPlayer);
                    invitationPlayer="";
                    dialog.dismiss();
                    isInvitationShown = false;
                }
            });

            btnDecline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BWDeleteGameRow bwDeleteGameRow2 = new BWDeleteGameRow(getApplicationContext());
                    bwDeleteGameRow2.execute(mech.getLogin(getApplicationContext()), invitationPlayer);
                    invitationPlayer="";
                    dialog.dismiss();
                    isInvitationShown = false;
                }
            });

        }
    }
    String invitationPlayer;


    void updateLists(){

        gamesListYourTurnNew.clear();
        gamesListWaitingNew.clear();
        gamesListCompletedNew.clear();

        layoutYourTurn.removeAllViews();
        layoutWaitingPlayers.removeAllViews();
        layoutCompleted.removeAllViews();

        //присваиваем постоянные IDs
        for (int it=0; it<20; it++) {
            idsForYourTurn[it] = it + 1000;
            idsForWaitingTurn[it] = it + 1100;
            idsForCompletedGames[it] = it + 1200;
        }
        countAssignedIDs = 0;

        //берем список игр из базы с ботами
        Cursor cursor = db.rawQuery("SELECT * FROM games", null);
        cursor.moveToFirst();

        //заносим список из базы с ботами в ARRAYs твоя очередь, ожидаем ...
        int iter = 0;
        while(cursor.getCount() > iter){
            JSONObject jsonObject = mech.cursorToJson(cursor);
            if(mech.whosTurnNubmer(jsonObject) == 1){
                putGameYourTurn(jsonObject, 1);
            } else if(mech.whosTurnNubmer(jsonObject) == 2){
                putGameWaiting(jsonObject, 1);
            } else {
                putGameCompleted(jsonObject, 1);
            }
            iter++;
            cursor.moveToNext();
        }

        try {
            json_all_games_array = new JSONArray(BWAllGamesWithMe.resultAllGames);
            int i = 0;
            JSONObject jsonObject;
            while(i < json_all_games_array.length()) {
                jsonObject = null;
                jsonObject = new JSONObject(json_all_games_array.getString(i));

                if(mech.whosTurn(jsonObject, this).equals("you")){
                    putGameYourTurn(jsonObject, 0);
                } else if(mech.whosTurn(jsonObject, this).equals("notyou")){
                    putGameWaiting(jsonObject, 0);
                } else if(mech.whosTurn(jsonObject, this).equals("invitation")){
                    showInvitation(jsonObject);
                } else if (mech.whosTurn(jsonObject, this).equals("finish")){
                    putGameCompleted(jsonObject, 0);
                }
                i++;
            }
        } catch (Exception e) {  Log.i("MMM", "NOT ok");  }

        //ARRAYS into layouts
        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Button btn;
        String[] abc;
        int i = 0;
        try {
            for (i = 0; i < 20; i++) {
                abc = gamesListYourTurnNew.get(i);
                viewResult = inflater.inflate(R.layout.row_in_games_list, layoutYourTurn, false);
                //fontChanger.replaceFonts((ViewGroup)viewResult);
                btn = (Button) viewResult.findViewById(R.id.buttonName);
                btn.setText(abc[1]);
                btn.setId(Integer.valueOf(abc[2]));
                ImageView ivA = (ImageView) viewResult.findViewById(R.id.imgAvaPreGame);
                if (abc[3].equals("") || abc[3].equals(null)) {
                    if (abc[1].equals("")) {
                        Picasso.with(this).load(R.drawable.ava).resize(480, 0).onlyScaleDown().transform(new CircleTransform()).into(ivA);
                    } else {
                        Picasso.with(this).load(R.drawable.ava).resize(480, 0).onlyScaleDown().transform(new CircleTransform()).into(ivA);
                    }
                    if(abc[1].equals("Ричард")){
                        Picasso.with(this).load(R.drawable.richard).resize(480, 0).onlyScaleDown().transform(new CircleTransform()).into(ivA);
                    }else if(abc[1].equals("Клавдия Степановна")){
                        Picasso.with(this).load(R.drawable.clavd_step).resize(480, 0).onlyScaleDown().transform(new CircleTransform()).into(ivA);
                    }else if(abc[1].equals("Анжела")){
                        Picasso.with(this).load(R.drawable.angela).resize(480, 0).onlyScaleDown().transform(new CircleTransform()).into(ivA);
                    }
                } else {
                    Picasso.with(this).load(abc[3]).resize(480, 0).onlyScaleDown().transform(new CircleTransform()).into(ivA);
                }

                layoutYourTurn.addView(viewResult);
            }
        }catch (Exception e) { }
        try{
            for(i = 0; i < 20; i++){
                abc = gamesListWaitingNew.get(i);
                viewResult = inflater.inflate(R.layout.row_in_games_list, layoutWaitingPlayers, false);
                //fontChanger.replaceFonts((ViewGroup)viewResult);
                btn = (Button)viewResult.findViewById(R.id.buttonName);
                btn.setText(abc[1]);
                btn.setId(Integer.valueOf(abc[2]));
                ImageView ivA = (ImageView)viewResult.findViewById(R.id.imgAvaPreGame);
                if(abc[3].equals("") || abc[3].equals(null)) {
                    if(abc[1].equals("")) {
                        Picasso.with(this).load(R.drawable.ava).resize(480, 0).onlyScaleDown().transform(new CircleTransform()).into(ivA);
                    } else {
                        Picasso.with(this).load(R.drawable.ava).resize(480, 0).onlyScaleDown().transform(new CircleTransform()).into(ivA); }
                    if(abc[1].equals("Ричард")){
                        Picasso.with(this).load(R.drawable.richard).resize(480, 0).onlyScaleDown().transform(new CircleTransform()).into(ivA);
                    }else if(abc[1].equals("Клавдия Степановна")){
                        Picasso.with(this).load(R.drawable.clavd_step).resize(480, 0).onlyScaleDown().transform(new CircleTransform()).into(ivA);
                    }else if(abc[1].equals("Анжела")){
                        Picasso.with(this).load(R.drawable.angela).resize(480, 0).onlyScaleDown().transform(new CircleTransform()).into(ivA);
                    }}
                else { Picasso.with(this).load(abc[3]).resize(480, 0).onlyScaleDown().transform(new CircleTransform()).into(ivA); }

                layoutWaitingPlayers.addView(viewResult);
            }
        }catch (Exception e) {Log.e("LLL", "exc 4 perebr your turn " + e.toString()); }

        try{
            for(i = 0; i < 20; i++){
                abc = gamesListCompletedNew.get(i);
                viewResult = inflater.inflate(R.layout.row_in_games_list, layoutCompleted, false);
               // fontChanger.replaceFonts((ViewGroup)viewResult);
                btn = (Button)viewResult.findViewById(R.id.buttonName);
                btn.setText(abc[1]);
                btn.setId(Integer.valueOf(abc[2]));
                ImageView ivA = (ImageView)viewResult.findViewById(R.id.imgAvaPreGame);
                if(abc[3].equals("") || abc[3].equals(null)) {
                    Picasso.with(this).load(R.drawable.ava).resize(480, 0).onlyScaleDown().transform(new CircleTransform()).into(ivA);
                    if(abc[1].equals("Ричард")){
                        Picasso.with(this).load(R.drawable.richard).resize(480, 0).onlyScaleDown().transform(new CircleTransform()).into(ivA);
                    }else if(abc[1].equals("Клавдия Степановна")){
                        Picasso.with(this).load(R.drawable.clavd_step).resize(480, 0).onlyScaleDown().transform(new CircleTransform()).into(ivA);
                    }else if(abc[1].equals("Анжела")){
                        Picasso.with(this).load(R.drawable.angela).resize(480, 0).onlyScaleDown().transform(new CircleTransform()).into(ivA);
                    }
                } else { Picasso.with(this).load(abc[3]).resize(480, 0).onlyScaleDown().transform(new CircleTransform()).into(ivA); }

                layoutCompleted.addView(viewResult);
            }
        }catch (Exception e) { Log.e("LLL", "exc 5" + e.toString()); }

        if (gamesListYourTurnNew.isEmpty()) {
            btnYourTurn.setVisibility(View.GONE);
            btnYourTurn.setHeight(0);
        } else {
            btnYourTurn.setVisibility(View.VISIBLE);
            btnYourTurn.setHeight(30);
        }

        if (gamesListWaitingNew.isEmpty()) {
            btnWaiting.setVisibility(View.GONE);
            btnWaiting.setHeight(0);
        } else {
            btnWaiting.setVisibility(View.VISIBLE);
            btnWaiting.setHeight(30);
        }

        if (gamesListCompletedNew.isEmpty()) {
            btnZaversh.setVisibility(View.GONE);
            btnZaversh.setHeight(0);
        } else {
            btnZaversh.setVisibility(View.VISIBLE);
            btnZaversh.setHeight(30);
        }
    }

    protected void putGameYourTurn(JSONObject jsonObject, int bot){
        interimStr = new String[4];
        if(bot == 1) {
            try {
                interimStr[0] = jsonObject.getString("botname"); // gamenumber
                interimStr[1] = jsonObject.getString("botname"); // enemy name
                interimStr[2] = String.valueOf(idsForYourTurn[countAssignedIDs]); // ids
                countAssignedIDs++;
                interimStr[3] = ""; //ava
                gamesListYourTurnNew.add(interimStr);
            } catch (JSONException e) {
            }
        } else {
            try {
                interimStr[0] = jsonObject.getString("_id");
                if(jsonObject.getString("player1").equals(mech.getLogin(this))){
                    interimStr[1] = jsonObject.getString("player2");
                    interimStr[3] = jsonObject.getString("ava2");
                } else {
                    interimStr[1] = jsonObject.getString("player1");
                    interimStr[3] = jsonObject.getString("ava1");
                }
            } catch (Exception e) {
            }
            Log.i("MMM", "interim" + interimStr.toString());
            interimStr[2] = String.valueOf(idsForYourTurn[countAssignedIDs]);
            countAssignedIDs++;
            gamesListYourTurnNew.add(interimStr);

        }
        interimStr = null;
    }

    protected void putGameWaiting(JSONObject jsonObject, int bot){
        interimStr = new String[4];
        if(bot == 1) {
            try {
                interimStr[0] = jsonObject.getString("botname"); // gamenumber
                interimStr[1] = jsonObject.getString("botname");
                interimStr[2] = String.valueOf(idsForWaitingTurn[countAssignedIDs]);
                countAssignedIDs++;
                interimStr[3] = "";//ava
                gamesListWaitingNew.add(interimStr);
            } catch (JSONException e) {    e.printStackTrace();    }
        } else{
            try {
                interimStr[0] = jsonObject.getString("_id");
                if(jsonObject.getString("player1").equals(mech.getLogin(this))){
                    interimStr[1] = jsonObject.getString("player2");
                    interimStr[3] = jsonObject.getString("ava2");
                } else {
                    interimStr[1] = jsonObject.getString("player1");
                    interimStr[3] = jsonObject.getString("ava1");
                }
            } catch (Exception e) {
            }
            Log.i("MMM", "interim" + interimStr.toString());
            interimStr[2] = String.valueOf(idsForWaitingTurn[countAssignedIDs]);
            countAssignedIDs++;
            gamesListWaitingNew.add(interimStr);
        }
        interimStr = null;
    }

    protected void putGameCompleted(JSONObject jsonObject, int bot){
        interimStr = new String[4];
        if(bot == 1) {
            try {
                interimStr[0] = jsonObject.getString("botname"); // gamenumber
                interimStr[1] = jsonObject.getString("botname");
                interimStr[2] = String.valueOf(idsForCompletedGames[countAssignedIDs]);
                countAssignedIDs++;
                interimStr[3] = "";//ava
                gamesListCompletedNew.add(interimStr);
            } catch (JSONException e) {   e.printStackTrace();    }
        } else{
            try {
                interimStr[0] = jsonObject.getString("_id");
                if(jsonObject.getString("player1").equals(mech.getLogin(this))){
                    interimStr[1] = jsonObject.getString("player2");
                    interimStr[3] = jsonObject.getString("ava2");
                } else {
                    interimStr[1] = jsonObject.getString("player1");
                    interimStr[3] = jsonObject.getString("ava1");
                }
            } catch (Exception e) {
            }
            Log.i("MMM", "interim" + interimStr.toString());
            interimStr[2] = String.valueOf(idsForCompletedGames[countAssignedIDs]);
            countAssignedIDs++;
            gamesListCompletedNew.add(interimStr);
        }
        interimStr = null;
    }

    public void onBot(View v){
        Random random = new Random();
        int randBot = random.nextInt(3);
        String botName;
        linlaHeaderProgress.setVisibility(View.VISIBLE);
        if(randBot == 0) {
            botName = "Клавдия Степановна";
            if(dbHelper.ifBotAlreadyPlays(db, botName)){
                botName = "Ричард";
            }
            if(dbHelper.ifBotAlreadyPlays(db, botName)){
                botName = "Анжела";
            }

        } else if(randBot == 1) {
            botName = "Ричард";
            if(dbHelper.ifBotAlreadyPlays(db, botName)){
                botName = "Анжела";
            }
            if(dbHelper.ifBotAlreadyPlays(db, botName)){
                botName = "Клавдия Степановна";
            }
        } else {
            botName = "Анжела";
            if(dbHelper.ifBotAlreadyPlays(db, botName)){
                botName = "Клавдия Степановна";
            }
            if(dbHelper.ifBotAlreadyPlays(db, botName)){
                botName = "Ричард";
            }
        }


        if(dbHelper.ifBotAlreadyPlays(db, botName)){
            //openPreGame
            Intent intent = new Intent(this, PreGameActivity.class);
            intent.putExtra("bot", 1);
            intent.putExtra("enemy", botName);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, ChooseCategoryOfQuestionsActivity.class);
            intent.putExtra("bot", 1);
            intent.putExtra("enemy", botName);
            intent.putExtra("round", 1);
            startActivity(intent);
        }
    }


    public void onGameClick(View view){
        Button btn = (Button)view;
        int id = view.getId();
        String gameNumber = "";
        String[] interimString;
        linlaHeaderProgress.setVisibility(View.VISIBLE);
        int i;
        try{
            for (i=0; i<30; i++){
                interimString = gamesListYourTurnNew.get(i);
                if(id == Integer.valueOf(interimString[2])){
                    gameNumber = interimString[0];
                }
            } } catch (Exception e) { }

        try{
            for (i=0; i<30; i++){
                interimString = gamesListWaitingNew.get(i);
                if(id == Integer.valueOf(interimString[2])){
                    gameNumber = interimString[0];
                }
            } } catch (Exception e) { }

        try{
            for (i=0; i<30; i++){
                interimString = gamesListCompletedNew.get(i);
                if(id == Integer.valueOf(interimString[2])){
                    gameNumber = interimString[0];
                }
            } } catch (Exception e) { }
        i = 0;

        if(gameNumber.equals("Ричард") || gameNumber.equals("Клавдия Степановна") || gameNumber.equals("Анжела")) {
            Intent intent = new Intent(this, PreGameActivity.class);
            intent.putExtra("bot", 1);
            intent.putExtra("enemy", gameNumber);
            startActivity(intent);
        } else {

            try {
                Intent intent = new Intent(this, PreGameActivity.class);
                intent.putExtra("bot", 0);
                intent.putExtra("gameid", mech.getJSONObjFromAllGamesByID(Integer.valueOf(gameNumber)).getInt("_id"));
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
    } }

    public void onFind(View view) {
            Intent intent = new Intent(this, FindActivity.class);
            this.startActivity(intent);
    }

    public void onFindNewPlayer(View view){
        linlaHeaderProgress.setVisibility(View.VISIBLE);
        BWPutPlayerIntoGamesTable putPlayerIntoWaitingTable = new BWPutPlayerIntoGamesTable(this);
        putPlayerIntoWaitingTable.execute();
    }

    public void onStatistics(View view){
        Intent intent = new Intent(this, StatisticsActivity.class);
        startActivity(intent);
    }

    public void onSettingsBtn (View v) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
    public void onButtonBack(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
