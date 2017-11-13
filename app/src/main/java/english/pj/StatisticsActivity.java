package english.pj;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class StatisticsActivity extends Activity {
    TextView tvWins, tvDraws, tvLosses, tvVocab, tvScore, tvIncorrAnswers, tvPercOfCorrect;
    TextView tvPlaceInTotalRank, tvPlaceInTotalRankFrom, tvVocabAvailable;
    ProgressBar pbWins, pbDraws, pbLosses;
    private dbHelper myDB;
    SQLiteDatabase db;
    LinearLayout linlaHeaderProgress;
    JSONObject jsonObject;
    Button btnOpenWords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        myDB = new dbHelper(this);

        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "fonts/Raleway-SemiBold.ttf");
        fontChanger.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));

        TextView tvHeader = (TextView)findViewById(R.id.textViewStatHeader);

        tvHeader.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Raleway-Black.ttf"));
        tvWins = (TextView)findViewById(R.id.textViewWinsStat);
        tvWins.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/OpenSans-ExtraBold.ttf"));
        tvDraws = (TextView)findViewById(R.id.textViewDrawsStat);
        tvDraws.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/OpenSans-ExtraBold.ttf"));
        tvLosses = (TextView)findViewById(R.id.textViewLossesStat);
        tvLosses.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/OpenSans-ExtraBold.ttf"));
        tvScore = (TextView)findViewById(R.id.textViewScore);
        tvScore.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/OpenSans-ExtraBold.ttf"));
        tvPlaceInTotalRank = (TextView)findViewById(R.id.textViewRankPlace);
        tvPlaceInTotalRank.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/OpenSans-ExtraBold.ttf"));
        tvPlaceInTotalRankFrom = (TextView)findViewById(R.id.textViewRankPlaceFrom);
        tvPlaceInTotalRankFrom.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/OpenSans-ExtraBold.ttf"));

        tvVocab = (TextView)findViewById(R.id.textViewVocab);
        tvVocab.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/OpenSans-ExtraBold.ttf"));
        tvVocabAvailable = (TextView)findViewById(R.id.textViewVocabAvailable);
        tvVocabAvailable.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/OpenSans-ExtraBold.ttf"));

        pbWins = (ProgressBar)findViewById(R.id.progressBarWins);
        pbDraws = (ProgressBar)findViewById(R.id.progressBarDraws);
        pbLosses = (ProgressBar)findViewById(R.id.progressBarLosses);
        linlaHeaderProgress = (LinearLayout)findViewById(R.id.linlaHeaderProgress);

        btnOpenWords = (Button)findViewById(R.id.buttonOpenWords);
        if(mech.isPremium(this)){
            btnOpenWords.setVisibility(View.GONE);
        } else {
            btnOpenWords.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        linlaHeaderProgress.setVisibility(View.GONE);
        db = myDB.getWritableDatabase();
        try {
            int wins = Integer.valueOf(BWUserStatistics.json_row.getString("wins"));
            int losses = Integer.valueOf(BWUserStatistics.json_row.getString("losses"));
            int draws = Integer.valueOf(BWUserStatistics.json_row.getString("draws"));
            int totalGames = wins + losses + draws;

            tvWins.setText(BWUserStatistics.json_row.getString("wins"));
            tvDraws.setText(BWUserStatistics.json_row.getString("draws"));
            tvLosses.setText(BWUserStatistics.json_row.getString("losses"));
            pbWins.setMax(totalGames);
            pbDraws.setMax(totalGames);
            pbLosses.setMax(totalGames);
            pbWins.setProgress(wins);
            pbDraws.setProgress(draws);
            pbLosses.setProgress(losses);
            tvScore.setText(BWUserStatistics.json_row.getString("points"));
            Cursor cursor = db.rawQuery("SELECT * FROM vocab2", null);
            Log.i("EEE", "cursor " + cursor.getCount());

            tvVocab.setText(String.valueOf(cursor.getCount()));
            tvVocabAvailable.setText(String.valueOf(mech.wordsAvailable(this)+1));
            BWUserStatistics bwGetUserStatistics = new BWUserStatistics(this);
            bwGetUserStatistics.execute(mech.getLogin(this));
        } catch (Exception e) {
            BWUserStatistics bwGetUserStatistics = new BWUserStatistics(this);
            bwGetUserStatistics.execute(mech.getLogin(this));
        }

        try {
            jsonObject = new JSONObject(BWGetPositionInRankingTable.json_data.getString(1));
            int integ = Integer.valueOf(jsonObject.getString("count(*)")) + 1;
            tvPlaceInTotalRank.setText(String.valueOf(integ));
            jsonObject = new JSONObject(BWGetPositionInRankingTable.json_data.getString(0));
            tvPlaceInTotalRankFrom.setText(jsonObject.getString("count(*)"));
        } catch (Exception e) {       }


    }

    public void onRankShow(View v) {
        linlaHeaderProgress.setVisibility(View.VISIBLE);
            BWGetRankingTable bwGetRankingTable = new BWGetRankingTable(this);
            bwGetRankingTable.execute();
    }

    public void onOpenWords(View v){
        Intent intent = new Intent(this, BuyActivity.class);
        startActivity(intent);
    }

    public void backToMain(View v){
        onBackPressed();
       // Intent intent = new Intent(this, MainActivity.class);
       // startActivity(intent);
    }
}
