package english.pj;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class Find2Activity extends Activity {
    static ListView lvSearchResult;
    static ArrayAdapter<String> adapterSearchResult;
    EditText etSearchName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find2);
        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "fonts/Raleway-SemiBold.ttf");
        fontChanger.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));
        TextView tvHeader = (TextView)findViewById(R.id.textViewFindHeader);
        tvHeader.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Raleway-Black.ttf"));

        etSearchName = (EditText)findViewById(R.id.editTextFindFriends);
        lvSearchResult = (ListView)findViewById(R.id.listViewSearchP);
        lvSearchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                  public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                                                      TextView textView = (TextView) itemClicked;
                                                      onShowPlayer(textView.getText().toString());
                                                  }
                                              }
        );

    }

    public void onShowPlayer(String str) {
        BWGetAnPlayerInfo bwGetAnPlayerInfo = new BWGetAnPlayerInfo(this);
        bwGetAnPlayerInfo.execute(str, "showUserActivityStart");
    }

    static void UpdateSearchResults(){
        lvSearchResult.setAdapter(adapterSearchResult);
    }
    public void onSearchFriend(View v) {
        BWSearchForUsers bwSearchForUsers = new BWSearchForUsers(this);
        bwSearchForUsers.execute(etSearchName.getText().toString());
    }
    public void onFriendsGo(View v) {
        Intent intent = new Intent(this, FindActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, GamesListActivity.class);
        startActivity(intent);
    }
}
