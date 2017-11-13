package english.pj;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import org.json.JSONArray;
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
import java.util.ArrayList;


public class BWGetRankingTable extends AsyncTask<String, Void, String> {
    Context context;
    ArrayList<String> rankingTableArray = new ArrayList<String>();
    public static JSONArray json_data;
    public static String[] arrayNames = new String[50];
    public static String[] arrayAvatars = new String[50];
    public static String[] arrayScores = new String[50];
    int i;
    JSONObject jsonObject0;

    BWGetRankingTable(Context ctx) {
        context = ctx;
    }
    @Override
    protected String doInBackground(String... params) {


        String phpfile = "http://q99710ny.bget.ru/get_ranking_table.php";

        try {
            URL url = new URL(phpfile);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("name","UTF-8")+"="+URLEncoder.encode("","UTF-8");
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
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    return null;
    }


    @Override
    protected void onPreExecute() {

    }


    @Override
    protected void onPostExecute(String result) {

        i = 0;
        rankingTableArray.clear();
        try {
            json_data = new JSONArray(result);

            while (i < 50) {
                jsonObject0 = new JSONObject(json_data.getString(i));
                rankingTableArray.add((i + 1) + ". " + jsonObject0.getString("login") + " - " + jsonObject0.getString("points"));
                arrayNames[i] = jsonObject0.getString("login");
                arrayAvatars[i] = jsonObject0.getString("avatar");
                arrayScores[i] = jsonObject0.getString("points");
                i++;
            }
        } catch (Exception e) {
        }
        Intent intent = new Intent(context, RankTableActivity.class);
        context.startActivity(intent);

    }
    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

}
