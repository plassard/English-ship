package english.pj;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;


public class dbHelper extends SQLiteOpenHelper {
    public dbHelper(Context context) {
        super(context, "dbEng", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }



    public static void insertNewCategoryRandQuestions(SQLiteDatabase db, String categ, int round, String enemy, Context context, boolean startRightAway) {
        int wordsNumber = mech.wordsAvailable(context);
        Random random = new Random();
        int rand1 = random.nextInt(wordsNumber);
        int rand2 = random.nextInt(wordsNumber);
        int rand3 = random.nextInt(wordsNumber);
        while (rand1 == rand2) {
            rand2 = random.nextInt(wordsNumber);
        }
        while (rand1 == rand3 || rand2 == rand3) {
            rand3 = random.nextInt(wordsNumber);
        }

        ContentValues values = new ContentValues();
        values.put("q" + String.valueOf(1 + ((round - 1) * 3)), String.valueOf(rand1));
        values.put("q" + String.valueOf(2 + ((round - 1) * 3)), String.valueOf(rand2));
        values.put("q" + String.valueOf(3 + ((round - 1) * 3)), String.valueOf(rand3));
        values.put("r" + String.valueOf(round) + "categ", categ);
        values.put("botname", enemy);
        Log.i("NULL1", "VALUES " + round + values.toString());
        if(round == 1) {
            db.insert("games", null, values);
        } else {
            String where = "botname='" + enemy + "'";
            db.update("games", values, where, null);
        }

        if(startRightAway) {
            Intent intent = new Intent(context, GameYesNoActivity.class);;
            if (categ.equals("yesno")) {
                intent = new Intent(context, GameYesNoActivity.class);
            } else if (categ.equals("pictures")) {
                intent = new Intent(context, GamePicturesActivity.class);
            } else if (categ.equals("wordsEN")) {
                intent = new Intent(context, GameWordsEnActivity.class);
            } else if (categ.equals("wordsRU")) {
                intent = new Intent(context, GameWordsRuActivity.class);
            } else if (categ.equals("letters")) {
                intent = new Intent(context, GameLettersActivity.class);
            } else if (categ.equals("match")) {
                intent = new Intent(context, GameMatchActivity.class);
            }else if (categ.equals("picturesRU")) {
                intent = new Intent(context, GamePicturesRUActivity.class);
            }
            if (round == 1) {
                intent.putExtra("qnumber", "1");
            } else if (round == 2) {
                intent.putExtra("qnumber", "4");
            } else if (round == 3) {
                intent.putExtra("qnumber", "7");
            } else if (round == 4) {
                intent.putExtra("qnumber", "10");
            }
            intent.putExtra("bot", 1);
            intent.putExtra("enemy", enemy);
            context.startActivity(intent);
    } else {
        Intent intent = new Intent(context, PreGameActivity.class);
        intent.putExtra("bot", 1);
        intent.putExtra("enemy", enemy);
        context.startActivity(intent);
    }
    }

    public static void insertNewCategoryWithBot(SQLiteDatabase db, String categ, int round, String enemy, Context context, boolean startRightAway) {
        int wordsNumber = mech.wordsAvailable(context);

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
                wordId1 = random.nextInt(mech.wordsAvailable(context));
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
                wordId2 = random.nextInt(mech.wordsAvailable(context));
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
                wordId3 = random.nextInt(mech.wordsAvailable(context));
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

        ContentValues values = new ContentValues();
        values.put("q" + String.valueOf(1 + ((round - 1) * 3)), String.valueOf(wordId1));
        values.put("q" + String.valueOf(2 + ((round - 1) * 3)), String.valueOf(wordId2));
        values.put("q" + String.valueOf(3 + ((round - 1) * 3)), String.valueOf(wordId3));
        values.put("r" + String.valueOf(round) + "categ", categ);
        values.put("botname", enemy);
        Log.i("NULL1", "VALUES " + round + values.toString());
        if(round == 1) {
            db.insert("games", null, values);
        } else {
            String where = "botname='" + enemy + "'";
            db.update("games", values, where, null);
        }

        if(startRightAway) {
            Intent intent = new Intent(context, GameYesNoActivity.class);;
            if (categ.equals("yesno")) {
                intent = new Intent(context, GameYesNoActivity.class);
            } else if (categ.equals("pictures")) {
                intent = new Intent(context, GamePicturesActivity.class);
            } else if (categ.equals("wordsEN")) {
                intent = new Intent(context, GameWordsEnActivity.class);
            } else if (categ.equals("wordsRU")) {
                intent = new Intent(context, GameWordsRuActivity.class);
            } else if (categ.equals("letters")) {
                intent = new Intent(context, GameLettersActivity.class);
            } else if (categ.equals("match")) {
                intent = new Intent(context, GameMatchActivity.class);
            } else if (categ.equals("picturesRU")) {
                intent = new Intent(context, GamePicturesRUActivity.class);
            }
            if (round == 1) {
                intent.putExtra("qnumber", "1");
            } else if (round == 2) {
                intent.putExtra("qnumber", "4");
            } else if (round == 3) {
                intent.putExtra("qnumber", "7");
            } else if (round == 4) {
                intent.putExtra("qnumber", "10");
            }
            intent.putExtra("bot", 1);
            intent.putExtra("enemy", enemy);
            context.startActivity(intent);
        } else {
            Intent intent = new Intent(context, PreGameActivity.class);
            intent.putExtra("bot", 1);
            intent.putExtra("enemy", enemy);
            context.startActivity(intent);
        }
    }

    public static void insertAnswer(SQLiteDatabase db, String qnumber, int points, String enemy, Context context, int maxPoints) {

        ContentValues cv = new ContentValues();
        cv.put(qnumber, String.valueOf(points));
        String where = "botname='" + enemy + "'";
        db.update("games", cv, where, null);
        int randBot = 0;

        int qnum = Integer.valueOf(qnumber.substring(1,2));
        int pointsPlayerTotal = 0;
        int pointsEnemyTotal = 0;
        JSONObject jsonGameRow = selectGameRowJSON(db, enemy);
        while (0 < qnum){
            try {
                pointsPlayerTotal = pointsPlayerTotal + jsonGameRow.getInt("q" + qnum + "p1");
                pointsEnemyTotal = pointsEnemyTotal + jsonGameRow.getInt("q" + qnum + "p2");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            qnum--;
        }
        Random random = new Random();
        if(pointsPlayerTotal > pointsEnemyTotal){
            randBot = random.nextInt(6); //0, 1, 2, 3, 4, 5
            randBot = maxPoints - 1 + randBot;
            if(randBot>maxPoints) { randBot = maxPoints;}
        } else {
            if(enemy.equals("Ричард")){
                randBot = random.nextInt(maxPoints + 1);
                if(randBot>maxPoints) { randBot = maxPoints;}
            }
            if(enemy.equals("Клавдия Степановна")){
                randBot = random.nextInt(maxPoints);
            }
            if(enemy.equals("Анжела")){
                randBot = random.nextInt(maxPoints + 3);
                if(randBot>maxPoints) { randBot = maxPoints;}
            }
        }

        cv = new ContentValues();
        if(qnumber.substring(1,3).equals("10") || qnumber.substring(1,3).equals("11") || qnumber.substring(1,3).equals("12")){
            cv.put(qnumber.substring(0, 4) + "2", String.valueOf(randBot));
        } else {
            cv.put(qnumber.substring(0, 3) + "2", String.valueOf(randBot));
        }

        where = "botname='" + enemy + "'";
        db.update("games", cv, where, null);

        if(qnumber.substring(1, 2).equals("3")){
            botChoosesCategory(db, enemy, 2, context);
        }
        if(qnumber.substring(1, 2).equals("9")){
            botChoosesCategory(db, enemy, 4, context);
        }
    }



    static void botChoosesCategory(SQLiteDatabase db, String enemy, int round, Context context){
        Random random = new Random();
        int rand1 = random.nextInt(10);
        switch (rand1){
            case 0: insertNewCategoryWithBot(db, "pictures", round, enemy, context, false);
                break;
            case 1: insertNewCategoryWithBot(db, "yesno", round, enemy, context, false);
                break;
            case 2: insertNewCategoryWithBot(db, "wordsEN", round, enemy, context, false);
                break;
            case 3: insertNewCategoryWithBot(db, "wordsRU", round, enemy, context, false);
                break;
            case 4: insertNewCategoryWithBot(db, "letters", round, enemy, context, false);
                break;
            case 5: insertNewCategoryWithBot(db, "match", round, enemy, context, false);
                break;
            case 6: insertNewCategoryWithBot(db, "picturesRU", round, enemy, context, false);
                break;
            case 7: insertNewCategoryWithBot(db, "picturesRU", round, enemy, context, false);
                break;
            case 8: insertNewCategoryWithBot(db, "picturesRU", round, enemy, context, false);
                break;
            case 9: insertNewCategoryWithBot(db, "pictures", round, enemy, context, false);
                break;
            case 10: insertNewCategoryWithBot(db, "pictures", round, enemy, context, false);
                break;
        }
    }


    public static void insertAnswerNo23zeroWithBot(SQLiteDatabase db, String qnumber, String enemy, Context context) {
        ContentValues cv = new ContentValues();
        cv.put(qnumber, "0");
        String where = "botname='" + enemy + "'";
        db.update("games", cv, where, null);

        cv = new ContentValues();
        if(qnumber.substring(1,3).equals("10") || qnumber.substring(1,3).equals("11") || qnumber.substring(1,3).equals("12")){
            cv.put(qnumber.substring(0, 4) + "2", "0");
        } else {
            cv.put(qnumber.substring(0, 3) + "2", "0");
        }

        where = "botname='" + enemy + "'";
        db.update("games", cv, where, null);

        if(qnumber.substring(1, 2).equals("3")){
            botChoosesCategory(db, enemy, 2, context);
           // dbHelper.insertNewCategoryRandQuestions(db, "pictures", 2, enemy, context, false);
        }
        if(qnumber.substring(1, 2).equals("9")){
            botChoosesCategory(db, enemy, 4, context);
            //dbHelper.insertNewCategoryRandQuestions(db, "pictures", 4, enemy, context, false);
        }
    }



    public static boolean ifBotAlreadyPlays(SQLiteDatabase db, String botName) {
        Cursor c = db.rawQuery("SELECT * FROM games WHERE botname = '" + botName + "'", null);
        if (c.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static JSONObject selectGameRowJSON(SQLiteDatabase db, String botName) {
        Cursor cursor = db.rawQuery("SELECT * FROM games WHERE botname = '" + botName + "'", null);
        cursor.moveToFirst();
        return mech.cursorToJson(cursor);
    }

    public static JSONObject selectQuestionJSON(SQLiteDatabase db, String questNumber) {
        Cursor cursor = null;
        cursor = db.rawQuery("SELECT * FROM wordsdb WHERE _id = '" + questNumber + "'", null);
        cursor.moveToFirst();
        return mech.cursorToJson(cursor);
    }

    public static void vocabAdd(SQLiteDatabase db, int wordID){
        Log.i("EEE", "vocabwork");
        //проверяем нет ли слова в уже один раз правильно отвеченных
        Cursor cursor = db.rawQuery("SELECT * FROM vocab2 WHERE wordID = '" + wordID + "'", null);
        if(cursor.getCount() == 0){
            cursor = db.rawQuery("SELECT * FROM vocab1 WHERE wordID = " + wordID, null);
            if(cursor.getCount() == 0){
                ContentValues values = new ContentValues();
                values.put("wordID", wordID);
                db.insert("vocab1", null, values);
            } else {
                ContentValues values = new ContentValues();
                values.put("wordID", wordID);
                db.insert("vocab2", null, values);
                db.delete("vocab1", "wordID = " + wordID, null);
            }

        }
    }

    public static void vocabDelete(SQLiteDatabase db, int wordID){
        Log.i("EEE", "vocabDelete");
        Cursor cursor = db.rawQuery("SELECT * FROM vocab2 WHERE wordID = '" + wordID + "'", null);
        if(cursor.getCount() != 0){
            db.delete("vocab2", "wordID = " + wordID, null);
        }
        cursor = null;
        cursor = db.rawQuery("SELECT * FROM vocab1 WHERE wordID = '" + wordID + "'", null);
        if(cursor.getCount() != 0){
            db.delete("vocab1", "wordID = " + wordID, null);
        }
    }

    public static void insertWordInVocab1(SQLiteDatabase db, int wordID) {
        ContentValues values = new ContentValues();
        values.put("wordID", wordID);
        db.insert("vocab1", null, values);
    }

    public static boolean isWordInVocab1(SQLiteDatabase db, int wordID) {
        Cursor cursor = db.rawQuery("SELECT * FROM vocab1 WHERE wordID = " + wordID, null);
        if(cursor.getCount() == 0){
            return false;
        } else {
            return true;
        }
    }

    public static boolean isWordInVocab2(SQLiteDatabase db, int wordID) {
        Cursor cursor = db.rawQuery("SELECT * FROM vocab2 WHERE wordID = " + wordID, null);
        if(cursor.getCount() == 0){
            return false;
        } else {
            return true;
        }
    }

}
