package english.pj;

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


public class BWAllGamesWithMe extends AsyncTask<String, Void, String> {
    Context context;
    String wheretogo;
    static String resultAllGames;
    static String invitation = "";

    BWAllGamesWithMe(Context ctx) {
        context = ctx;
    }
    @Override
    protected String doInBackground(String... params) {
//        wheretogo = params[0];
        Log.i("MMM", "start" + mech.getLogin(context));
        String phpfile = "http://q99710ny.bget.ru/all_games_with_me.php";

        try {
            URL url = new URL(phpfile);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("name","UTF-8")+"="+URLEncoder.encode(mech.getLogin(context),"UTF-8");
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

        try {
            resultAllGames = result;
            JSONArray json_data = new JSONArray(result);
            JSONObject jsonObject;
            int i = 0;
            while (i < 20) {
                jsonObject = new JSONObject(json_data.getString(i));
                if (jsonObject.getString("invitation").equals("1")) {
                    if (jsonObject.getString("player1").equals(mech.getLogin(context))) {
                        invitation = jsonObject.getString("player2");
                    } else {
                    }
                }
                i++;
            }
        }catch (Exception e) {    }
    }
    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

}

