package english.pj;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class ChooseCategoryOfQuestionsActivity extends Activity implements View.OnClickListener  {
    Button btn1, btn2, btn3, btn4, btn5, btn6, btn7;
    boolean isBot;
    String enemy;
    int round;
    int _id;
    FontChangeCrawler fontChanger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_category_of_questions);
        fontChanger = new FontChangeCrawler(getAssets(), "fonts/Raleway-Bold.ttf");
        fontChanger.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));

        btn1 = (Button)findViewById(R.id.btnCateg1);
        btn2 = (Button)findViewById(R.id.btnCateg2);
        btn3 = (Button)findViewById(R.id.btnCateg3);
        btn4 = (Button)findViewById(R.id.btnCateg4);
        btn5 = (Button)findViewById(R.id.btnCateg5);
        btn6 = (Button)findViewById(R.id.btnCateg6);
        btn7 = (Button)findViewById(R.id.btnCateg1RU);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);

        if(getIntent().getIntExtra("bot", 0) == 1) {
            isBot = true;
        } else {
            isBot = false;
        };
        enemy = "";
        round = 1;
        enemy = getIntent().getStringExtra("enemy");
        round = getIntent().getIntExtra("round", 0);
        _id = getIntent().getIntExtra("_id", 0);
    }

    private dbHelper myDB;

    @Override
    public void onClick(View view) {
        myDB = new dbHelper(this);
        SQLiteDatabase db = myDB.getWritableDatabase();

        String categ = "";

        switch (view.getId()) {
            case R.id.btnCateg1:
                categ = "pictures";      break;

            case R.id.btnCateg2:
                categ = "yesno";        break;

            case R.id.btnCateg3:
                categ = "wordsEN";     break;

            case R.id.btnCateg4:
                categ = "wordsRU";    break;

            case R.id.btnCateg5:
                categ = "letters";     break;

            case R.id.btnCateg6:
                categ = "match";     break;

            case R.id.btnCateg1RU:
                categ = "picturesRU";     break;
        }


        if(isBot) {
            dbHelper.insertNewCategoryWithBot(db, categ, round, enemy, this, true);
        } else {

            //здесь мы подбираем номера вопросов так, чтоб они были уже по разу отвечены, но не из словарного запаса
            int wordId1, wordId2, wordId3;
            wordId1 = wordId2 = wordId3 = 10000;
            /*
            //берем по разу отвеченные из vocab1
            Cursor cursor = db.rawQuery("SELECT * FROM vocab1", null);
            cursor.moveToFirst();
            JSONObject jsonObject = mech.cursorToJson(cursor);
            try {
                wordId1 = jsonObject.getInt("wordID");
            } catch (JSONException e) {  wordId1 = 10000;   }
            try {
                cursor.moveToNext();
                jsonObject = mech.cursorToJson(cursor);
                wordId2 = jsonObject.getInt("wordID");
            } catch (JSONException e) { wordId2 = 10000;  }
            try {
                cursor.moveToNext();
                jsonObject = mech.cursorToJson(cursor);
                wordId3 = jsonObject.getInt("wordID");
            } catch (JSONException e) {   wordId3 = 10000;   }
            Log.i("PPP", "words" + wordId1 + wordId2 + wordId3);
            */
            //если в vocab1 не набралось нужных трех слов, то выбираем их рандомно и так, чтоб не из словарного запаса
            Random random = new Random();
            if(wordId1 == 10000) {
                for (int i = 0; i < 30; i++) {
                    wordId1 = random.nextInt(mech.wordsAvailable(this));
                    if (dbHelper.isWordInVocab2(db, wordId1)) {
                        Log.i("PPP", "no good, try another" + wordId1);
                    } else {
                        Log.i("PPP", "good" + wordId1);
                        break;
                    }
                }
            }

            if(wordId2 == 10000) {
                for (int i = 0; i < 50; i++) {
                    wordId2 = random.nextInt(mech.wordsAvailable(this));
                    if (dbHelper.isWordInVocab2(db, wordId2)) {
                            Log.i("PPP", "no good, try another" + wordId2);
                    } else {
                        if(wordId2 == wordId1) {
                            Log.i("PPP", "no good, try another" + wordId2);
                        } else {
                            Log.i("PPP", "good" + wordId2);
                            break;
                        }
                    }
                }
            }

            if(wordId3 == 10000) {
                for (int i = 0; i < 50; i++) {
                    wordId3 = random.nextInt(mech.wordsAvailable(this));
                    if (dbHelper.isWordInVocab2(db, wordId3)) {
                        Log.i("PPP", "no good, try another" + wordId3);
                    } else {
                        if(wordId3 == wordId1 || wordId3 == wordId2) {
                            Log.i("PPP", "no good, try another" + wordId3);
                        } else {
                            Log.i("PPP", "good" + wordId3);
                            break;
                        }
                    }
                }
            }
            Log.i("PPP", "words" + wordId1 + wordId2 + wordId3);

            if(round == 1 && enemy.equals("")) {
                BWPutQCategoryIntoNewGame bwPutQCategoryIntoNewGame = new BWPutQCategoryIntoNewGame(this);
                bwPutQCategoryIntoNewGame.execute(categ, String.valueOf(wordId1), String.valueOf(wordId2), String.valueOf(wordId3));
            } else {
                //2,3 raunds ...
                BWPutQCategoryInExistingGame bwPutQCategoryInExistingGame = new BWPutQCategoryInExistingGame(this);
                bwPutQCategoryInExistingGame.execute(String.valueOf(_id), String.valueOf(round), categ, String.valueOf(wordId1), String.valueOf(wordId2), String.valueOf(wordId3));
            }
        }

    }
}
