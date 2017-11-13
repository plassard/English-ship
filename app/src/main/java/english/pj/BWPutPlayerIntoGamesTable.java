package english.pj;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class BWPutPlayerIntoGamesTable extends AsyncTask<String, Void, String> {
    Context context;
    JSONObject json_row;


    BWPutPlayerIntoGamesTable(Context ctx) {
        context = ctx;
    }
    @Override
    protected String doInBackground(String... params) {
        String putInWaitURL = "http://q99710ny.bget.ru/put_player_into_games_table.php";
        Log.i("UUU", mech.getLogin(context) + " " + mech.getAvatar(context));
        try {
            URL url = new URL(putInWaitURL);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("login","UTF-8")+"="+URLEncoder.encode(mech.getLogin(context),"UTF-8")+"&"+
                    URLEncoder.encode("avatar","UTF-8")+"="+URLEncoder.encode(mech.getAvatar(context),"UTF-8");
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
            String result="";
            String line="";
            while((line = bufferedReader.readLine())!= null) {
                result += line;
            }
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();
            return result;
        } catch (MalformedURLException e) {

        } catch (Exception e) {

        }
        return null;
    }


    @Override
    protected void onPreExecute() {

    }


    @Override
    protected void onPostExecute(String result) {
        Log.i("KKK2", "r " + result.toString());

        if("none".equals(result)){
            Intent intent = new Intent(context, ChooseCategoryOfQuestionsActivity.class);
            intent.putExtra("round", 1);
            intent.putExtra("bot", 0);
            intent.putExtra("enemy", "");
            context.startActivity(intent);
        } else {
            try {
                BWAllGamesWithMe.resultAllGames = result;
                JSONArray json_all_games_array;
                json_all_games_array = new JSONArray(BWAllGamesWithMe.resultAllGames);
                json_row = new JSONObject(json_all_games_array.getString(0));
                Intent intent = new Intent(context, PreGameActivity.class);
                intent.putExtra("bot", 0);
                intent.putExtra("gameid", json_row.getInt("_id"));
                context.startActivity(intent);

            } catch (Exception e) {
                Log.i("KKK2", "exc " + e.toString());
                Intent intent = new Intent(context, GamesListActivity.class);
                context.startActivity(intent);
            }
        }

    }
    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }


}