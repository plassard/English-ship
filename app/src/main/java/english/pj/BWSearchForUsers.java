package english.pj;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;

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

public class BWSearchForUsers extends AsyncTask<String, Void, String> {
    Context context;
    ArrayList<String> searchResultArrayList = new ArrayList<String>();
    JSONArray json_data;
    int i;
    AlertDialog alertDialog;
    String login;
    JSONObject jsonObject0;

    BWSearchForUsers(Context ctx) {
        context = ctx;
    }
    @Override
    protected String doInBackground(String... params) {
        login = params[0];
        String phpfile = "http://q99710ny.bget.ru/search_for_users.php";

        try {
            URL url = new URL(phpfile);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("name","UTF-8")+"="+URLEncoder.encode(login,"UTF-8");
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
        alertDialog = new AlertDialog.Builder(context).create();
    }


    @Override
    protected void onPostExecute(String result) {

        i = 0;
        searchResultArrayList.clear();

        try {
            json_data = new JSONArray(result);

            while (i < 20) {
                jsonObject0 = new JSONObject(json_data.getString(i));
                searchResultArrayList.add(jsonObject0.getString("login"));
                i++;
            }
        } catch (Exception e) {
        }
        Find2Activity.adapterSearchResult = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, searchResultArrayList);
        Find2Activity.UpdateSearchResults();
    }
    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

}
