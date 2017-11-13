package english.pj;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.squareup.picasso.Picasso;

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

public class BWGetAnPlayerInfo extends AsyncTask<String, Void, String> {
    Context context;
    String name, wheretogo;
    JSONArray json_data1;
    static JSONObject jsonObjectAnPl;

    BWGetAnPlayerInfo(Context ctx) {
        context = ctx;
    }
    @Override
    protected String doInBackground(String... params) {
        name = params[0];
        wheretogo = params[1];
        String phpfile = "http://q99710ny.bget.ru/get_another_user_info.php";

        try {
            URL url = new URL(phpfile);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("name","UTF-8")+"="+URLEncoder.encode(name,"UTF-8");
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
        try {
            json_data1 = new JSONArray(result);
            jsonObjectAnPl = new JSONObject(json_data1.getString(0));
            //AnPlayerRank = jsonObjectAnPl.getString("correctanswers");
            if(wheretogo.equals("showUserActivityStart")) {
                Intent intent = new Intent(context, ShowProfileActivity.class);
                context.startActivity(intent);
            } else if(wheretogo.equals("downloadImageForEnemy")) {
                if(!jsonObjectAnPl.getString("avatar").isEmpty() && !jsonObjectAnPl.getString("avatar").equals("")) {
                    Picasso.with(context).load(jsonObjectAnPl.getString("avatar")).resize(720, 0).onlyScaleDown();
                }
            }


        } catch (Exception e) {

        }


    }
    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

}
