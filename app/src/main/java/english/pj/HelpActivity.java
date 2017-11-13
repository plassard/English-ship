package english.pj;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;

public class HelpActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "fonts/Raleway-Bold.ttf");
        fontChanger.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));
    }
}
