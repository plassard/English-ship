package english.pj;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class RankTableActivity extends Activity {

    Context context;
    boolean clicked;
    LinearLayout linlaHeaderProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank_table);
        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "fonts/Raleway-SemiBold.ttf");
        fontChanger.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));
        TextView tvHeader = (TextView)findViewById(R.id.textViewRankTableHeader);
        tvHeader.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Raleway-Black.ttf"));

        ArrayList<ListItem> listData = getListData();

        final ListView listView = (ListView) findViewById(R.id.listViewRankTable);
        listView.setAdapter(new CustomListAdapter(this, listData));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                if (clicked == false) {
                    clicked = true;
                    ListItem newsData = (ListItem) listView.getItemAtPosition(position);
                    ShowUser(newsData.toString());
                }

            }
        });
        linlaHeaderProgress = (LinearLayout)findViewById(R.id.linlaHeaderProgress);
    }

    @Override
    protected void onResume() {
        clicked = false;
        linlaHeaderProgress.setVisibility(View.GONE);
        super.onResume();
    }

    private ArrayList<ListItem> getListData() {
        ArrayList<ListItem> listRankData = new ArrayList<ListItem>();

        for (int i = 0; i < 50; i++) {
            try {
                if (!(BWGetRankingTable.arrayNames[i].isEmpty())) {
                    ListItem rankData = new ListItem();
                    rankData.setUrl(BWGetRankingTable.arrayAvatars[i]);
                    rankData.setName(BWGetRankingTable.arrayNames[i]);
                    rankData.setNumber(String.valueOf(i + 1));
                    rankData.setScore(BWGetRankingTable.arrayScores[i]);
                    listRankData.add(rankData);
                }
            } catch (Exception e) {

            }
        }
        return listRankData;
    }


    private void ShowUser(String str) {
        linlaHeaderProgress.setVisibility(View.VISIBLE);
        BWGetAnPlayerInfo bwGetAnPlayerInfo = new BWGetAnPlayerInfo(this);
        bwGetAnPlayerInfo.execute(str, "showUserActivityStart");
    }
}

