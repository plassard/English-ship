package english.pj;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class FindActivity extends Activity {

    ArrayAdapter<String> adapterFriends;

    ListView lvFriends;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);
        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "fonts/Raleway-SemiBold.ttf");
        fontChanger.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));
        TextView tvHeader = (TextView)findViewById(R.id.textViewFrHeader);
        tvHeader.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Raleway-Black.ttf"));



        lvFriends = (ListView)findViewById(R.id.listViewFriends);
        lvFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                             public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                                                 TextView textView = (TextView) itemClicked;
                                                 onShowPlayer(textView.getText().toString());
                                             }
                                         }
        );


    }

    @Override
    protected void onResume() {
        super.onResume();
        adapterFriends = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, BWUserStatistics.friendsList);
        lvFriends.setAdapter(adapterFriends);

    }


    public void onShowPlayer(String str) {
        BWGetAnPlayerInfo bwGetAnPlayerInfo = new BWGetAnPlayerInfo(this);
        bwGetAnPlayerInfo.execute(str, "showUserActivityStart");
    }




    public void onFind2(View v) {
        Intent intent = new Intent(this, Find2Activity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, GamesListActivity.class);
        startActivity(intent);
    }
}
