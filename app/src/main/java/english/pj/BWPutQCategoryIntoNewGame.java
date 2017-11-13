package english.pj;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

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


public class BWPutQCategoryIntoNewGame extends AsyncTask<String, Void, String> {
        Context context;
        AlertDialog alertDialog;
        String QCategory;

        BWPutQCategoryIntoNewGame(Context ctx) {
        context = ctx;
        }
    @Override
    protected String doInBackground(String... params) {
        String putInWaitURL = "http://q99710ny.bget.ru/setCategoryForNewGame.php";
        QCategory = params[0];
        String ava1 = "";
        Log.i("MMM2", "BW wording" + QCategory);


        try {
        URL url = new URL(putInWaitURL);

        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setDoInput(true);
        OutputStream outputStream = httpURLConnection.getOutputStream();
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
        String post_data = URLEncoder.encode("login", "UTF-8")+"="+URLEncoder.encode(mech.getLogin(context),"UTF-8")+"&"+
        URLEncoder.encode("qcat","UTF-8")+"="+URLEncoder.encode(QCategory,"UTF-8")+"&"+
        URLEncoder.encode("ava1","UTF-8")+"="+URLEncoder.encode(mech.getAvatar(context),"UTF-8")+"&"+
                URLEncoder.encode("q1","UTF-8")+"="+URLEncoder.encode(params[1],"UTF-8")+"&"+
                URLEncoder.encode("q2","UTF-8")+"="+URLEncoder.encode(params[2],"UTF-8")+"&"+
                URLEncoder.encode("q3","UTF-8")+"="+URLEncoder.encode(params[3],"UTF-8");
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
        JSONObject json_row;

        try {
        json_row = new JSONObject(result);
            Intent intent = new Intent(context, GamePicturesActivity.class);
            if (QCategory.equals("yesno")) {
                intent = new Intent(context, GameYesNoActivity.class);
            } else if (QCategory.equals("pictures")) {
                intent = new Intent(context, GamePicturesActivity.class);
            } else if (QCategory.equals("wordsEN")) {
                intent = new Intent(context, GameWordsEnActivity.class);
            } else if (QCategory.equals("wordsRU")) {
                intent = new Intent(context, GameWordsRuActivity.class);
            } else if (QCategory.equals("letters")) {
                intent = new Intent(context, GameLettersActivity.class);
            } else if (QCategory.equals("match")) {
                intent = new Intent(context, GameMatchActivity.class);
            } else if (QCategory.equals("picturesRU")) {
                intent = new Intent(context, GamePicturesRUActivity.class);
            }
            intent.putExtra("bot", 0);
            intent.putExtra("order", 1);
            intent.putExtra("qnumber", "1");
            intent.putExtra("_id", json_row.getInt("_id"));
            intent.putExtra("q1", json_row.getInt("q1"));
            intent.putExtra("q2", json_row.getInt("q2"));
            intent.putExtra("q3", json_row.getInt("q3"));
            context.startActivity(intent);
        } catch (Exception e) {
        Intent intent = new Intent(context, GamesListActivity.class);
        context.startActivity(intent);
        }


        }

@Override
protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        }


        }