package english.pj;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.GregorianCalendar;

public class BWGetPositionInRankingTable extends AsyncTask<String, Void, String> {
    Context context;
    public static JSONArray json_data;
    int i;
    AlertDialog alertDialog;
    String login, points;
    static String wheretogo = "";
    JSONObject jsonObject0;
    static long lastUpdateTime;


    BWGetPositionInRankingTable(Context ctx) {
        context = ctx;
    }
    @Override
    protected String doInBackground(String... params) {

        String phpfile = "http://q99710ny.bget.ru/get_postition_in_rank.php";

        try {
            login = mech.getLogin(context);
            points = params[0];
            URL url = new URL(phpfile);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("points","UTF-8")+"="+URLEncoder.encode(points,"UTF-8");
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
        } catch (IOException e) {
        }

    return null;
    }


    @Override
    protected void onPreExecute() {
        alertDialog = new AlertDialog.Builder(context).create();
    }


    @Override
    protected void onPostExecute(String result) {

        try {
            json_data = new JSONArray(result);
            jsonObject0 = new JSONObject(json_data.getString(0)); // total players in rank
            jsonObject0 = new JSONObject(json_data.getString(1)); // your place

            lastUpdateTime = GregorianCalendar.getInstance().getTimeInMillis();
            if(wheretogo.equals("stat_activity")){
                Intent intent = new Intent(context, StatisticsActivity.class);
                context.startActivity(intent);
                wheretogo = ""; }

            if(wheretogo.equals("main_activity")){
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
                wheretogo = ""; }
        } catch (Exception e) {
        }
    }
    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

}
