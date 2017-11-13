package english.pj;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class mech {

    public static int wordsAvailable(Context context){

        if(isPremium(context)){return 186;}
        else {
            return 186;
        }
    }

    static boolean isSoundOn(Context context){
        String sound = "";
        try{
            SharedPreferences settings = context.getSharedPreferences("DATA", Context.MODE_PRIVATE);
            sound = settings.getString("SOUND", null);
        } catch (Exception e){
            sound = "off";
        }
        if(sound == null) {sound = "";}
        if(sound.equals("off")){
            return false;
        } else {
            return true;
        }
    }


    static String getNextQuestionToBeAnswered(JSONObject jsonObject, Context context){
        String nextQ = "1";
        Log.i("LOL2", "turn is " + whosTurnNubmer(jsonObject));
        Log.i("LOL2", "are you pl1 " + areYouPlayer1(jsonObject, context));
        if(areYouPlayer1(jsonObject, context)){
            try {
                if(jsonObject.getString("q1p1").equals("null")) { return nextQ;} nextQ = "2";
                if(jsonObject.getString("q2p1").equals("null")) { return nextQ;} nextQ = "3";
                if(jsonObject.getString("q3p1").equals("null")) { return nextQ;} nextQ = "4";
                if(jsonObject.getString("q4p1").equals("null")) { return nextQ;} nextQ = "5";
                if(jsonObject.getString("q5p1").equals("null")) { return nextQ;} nextQ = "6";
                if(jsonObject.getString("q6p1").equals("null")) { return nextQ;} nextQ = "7";
                if(jsonObject.getString("q7p1").equals("null")) { return nextQ;} nextQ = "8";
                if(jsonObject.getString("q8p1").equals("null")) { return nextQ;} nextQ = "9";
                if(jsonObject.getString("q9p1").equals("null")) { return nextQ;} nextQ = "10";
                if(jsonObject.getString("q10p1").equals("null")) { return nextQ;} nextQ = "11";
                if(jsonObject.getString("q11p1").equals("null")) { return nextQ;} nextQ = "12";
                if(jsonObject.getString("q12p1").equals("null")) { return nextQ;} nextQ = "all";
                //все отвечены
                nextQ = null;
            } catch (JSONException e) {            }
        } else {
            try {
                if(jsonObject.getString("q1p2").equals("null")) { return nextQ;} nextQ = "2";
                if(jsonObject.getString("q2p2").equals("null")) { return nextQ;} nextQ = "3";
                if(jsonObject.getString("q3p2").equals("null")) { return nextQ;} nextQ = "4";
                if(jsonObject.getString("q4p2").equals("null")) { return nextQ;} nextQ = "5";
                if(jsonObject.getString("q5p2").equals("null")) { return nextQ;} nextQ = "6";
                if(jsonObject.getString("q6p2").equals("null")) { return nextQ;} nextQ = "7";
                if(jsonObject.getString("q7p2").equals("null")) { return nextQ;} nextQ = "8";
                if(jsonObject.getString("q8p2").equals("null")) { return nextQ;} nextQ = "9";
                if(jsonObject.getString("q9p2").equals("null")) { return nextQ;} nextQ = "10";
                if(jsonObject.getString("q10p2").equals("null")) { return nextQ;} nextQ = "11";
                if(jsonObject.getString("q11p2").equals("null")) { return nextQ;} nextQ = "12";
                if(jsonObject.getString("q12p2").equals("null")) { return nextQ;} nextQ = "all";
                //все отвечены
                nextQ = null;
            } catch (JSONException e) {        }
        }
        return nextQ;
    }

    static String getRoundName(JSONObject jsonObject, Context context) {
        String number = "";
        number = getNextQuestionToBeAnswered(jsonObject, context);
        try {
        if(number.equals("1") || number.equals("2") || number.equals("3")){
            return jsonObject.getString("r1categ");
        } else if(number.equals("4") || number.equals("5") || number.equals("6")){
            return jsonObject.getString("r2categ");
        } else if(number.equals("7") || number.equals("8") || number.equals("9")){
            return jsonObject.getString("r3categ");
        }else if(number.equals("10") || number.equals("11") || number.equals("12")){
            return jsonObject.getString("r4categ");
        } else {
            return "all";
        }
        } catch (JSONException e) {
            return "error";
        }
    }

    static int getRoundNumber(JSONObject jsonObject, Context context) {
        String number = "";
        number = getNextQuestionToBeAnswered(jsonObject, context);
            if(number.equals("1") || number.equals("2") || number.equals("3")){
                return 1;
            } else if(number.equals("4") || number.equals("5") || number.equals("6")){
                return 2;
            } else if(number.equals("7") || number.equals("8") || number.equals("9")){
                return 3;
            } else if(number.equals("10") || number.equals("11") || number.equals("12")){
                return 4;
            } else {
                return 5;
            }
    }

    static String whosTurn(JSONObject jsonObject, Context context){
        String turn = "";

        try {
            if (getLogin(context).equals(jsonObject.getString("player1"))) {
                if(!jsonObject.getString("seenbypl1").equals("1")) {
                    if (jsonObject.getString("invitation").equals("1")) {
                        turn = "invitation";
                    } else if (jsonObject.isNull("q3p1")) {
                        turn = "you";
                    } else if (jsonObject.isNull("r2categ")) {
                        turn = "notyou";
                    } else if (jsonObject.isNull("q9p1")) {
                        turn = "you";
                    } else if (jsonObject.isNull("r4categ")) {
                        turn = "notyou";
                    } else if (jsonObject.isNull("q12p1")) {
                        turn = "you";
                    } else if (jsonObject.isNull("q12p2")) {
                        turn = "notyou";
                    } else {
                        //все вопросы в игре отвечены
                        turn = "finish";
                    }
                }
                  }

            if (mech.getLogin(context).equals(jsonObject.getString("player2"))) { //разбиваю игры на играют и ждут от игрока 2
                if (!jsonObject.getString("seenbypl2").equals("1")) {
                    if (jsonObject.getString("invitation").equals("1")) {
                        turn = "nobody";
                    } else if (jsonObject.isNull("r1categ")) {
                        turn = "notyou";
                    } else if (jsonObject.isNull("q6p2")) {
                        turn = "you";
                    } else if (jsonObject.isNull("r3categ")) {
                        turn = "notyou";
                    } else if (jsonObject.isNull("q12p2")) {
                        turn = "you";
                    } else if (jsonObject.isNull("q12p1")) {
                        turn = "notyou";
                    } else {
                        //все вопросы в игре отвечены
                        turn = "finish";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return turn;
    }

    static int whosTurnNubmer(JSONObject jsonObject){
            if(jsonObject.isNull("q3p1")){
                return 1;
            } else if(!jsonObject.isNull("r2categ") && jsonObject.isNull("q9p1")){
                return 1;
            } else if(!jsonObject.isNull("r4categ") && jsonObject.isNull("q12p1")){
                return 1;
            } else if(!jsonObject.isNull("r4categ") && !jsonObject.isNull("q12p1") && !jsonObject.isNull("q12p2") ){
                return 3;
            } else {
                return 2;
            }
    }

    static boolean areYouPlayer1(JSONObject jsonObject, Context context) {
        try {
            if(getLogin(context).equals(jsonObject.getString("player1"))){
                return true;
            } else {
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    static String getLogin(Context context){
        SharedPreferences settings = context.getSharedPreferences("DATA", Context.MODE_PRIVATE);
        return settings.getString("LOGIN", null);
    }

    static String getAvatar(Context context){
        SharedPreferences settings = context.getSharedPreferences("DATA", Context.MODE_PRIVATE);
        String ava = "";
        try{
            ava = settings.getString("AVATAR", null);
            if(ava.equals(null)){
                ava = "";
            }
            return ava;
        } catch (Exception e) {
            return "";
        }
    }

    static boolean isPremium(Context context){
        SharedPreferences settings = context.getSharedPreferences("DATA", Context.MODE_PRIVATE);
        String subscription = "";
        try{
            subscription = settings.getString("PREMIUM", null);
            if(subscription.equals(null)){
                subscription = "";
            }
        } catch (Exception e) {
            subscription = "";
        }

        if(subscription.equals("yes")){
            return true;
        } else {
            return false;
        }

    }

    static JSONObject cursorToJson(Cursor c) {
        JSONObject retVal = new JSONObject();
        for(int i=0; i<c.getColumnCount(); i++) {
            String cName = c.getColumnName(i);
            try {
                switch (c.getType(i)) {
                    case Cursor.FIELD_TYPE_INTEGER:
                        retVal.put(cName, c.getInt(i));
                        break;
                    case Cursor.FIELD_TYPE_STRING:
                        retVal.put(cName, c.getString(i));
                        break;
                }
            }
            catch(Exception ex) {
                //Log.i("MLog", "Exception converting cursor column to json field: " + cName + ex);
            }
        }
        return retVal;
    }

    static boolean isBot(String enemy) {
        if(enemy.equals("Ричард") || enemy.equals("Клавдия Степановна") || enemy.equals("Анжела")) {
            return true;
        } else {
            return false;
        }
    }

    static boolean ifLastQinRound(String qNumber){
    if (qNumber.equals("3") || qNumber.equals("6") || qNumber.equals("9") || qNumber.equals("12")) {
        return true;
    } else {
        return false;
    }
}

    static JSONObject getJSONObjFromAllGamesByID(int id){
        JSONArray json_all_games_array = null;
        try {
            json_all_games_array = new JSONArray(BWAllGamesWithMe.resultAllGames);
            JSONObject jsonRowGame = null;
            int i = 0;
            while (i < json_all_games_array.length()) {
                jsonRowGame = new JSONObject(json_all_games_array.getString(i));
                if (jsonRowGame.getInt("_id") == (id)) {
                    return jsonRowGame;
                }
                i++;
            }
        } catch (Exception e) {
            Log.i("LL2", "except" + e);
        }
        return null;
    }

    static String translateCateg(String string){
        if(string.equals("pictures")){
            return "Картинки с английского";
        } else if(string.equals("yesno")){
            return "Да - нет";
        } else if(string.equals("wordsEN")){
            return "С английского на русский";
        } else if(string.equals("wordsRU")){
            return "С русского на английский";
        } else if(string.equals("letters")){
            return "По буквам";
        } else if (string.equals("match")){
            return "Сопоставить";
        } else if (string.equals("picturesRU")){
            return "Картинки с русского";
        } else {
            return "";
        }
    }
}
