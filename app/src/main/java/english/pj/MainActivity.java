package english.pj;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imagen = (ImageView)findViewById(R.id.imageViewM2);
        imagen.setImageResource(R.drawable.logo2);

        TextView tvLogo = (TextView)findViewById(R.id.tvMainLogo);
        tvLogo.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/TitanOne-Regular.ttf"));

        TextView tvLogo2 = (TextView)findViewById(R.id.tvMainLogo2);
        tvLogo2.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/TitanOne-Regular.ttf"));

        BWAllGamesWithMe bwAllGamesWithMe = new BWAllGamesWithMe(this);
        bwAllGamesWithMe.execute();

        BWUserStatistics bwUserStatistics = new BWUserStatistics(this);
        bwUserStatistics.execute();

        BWGetChatMessages bwGetChatMessages = new BWGetChatMessages(this);
        bwGetChatMessages.execute();
    }


    public void onNewGame(View view) {
        Intent intent = new Intent(this, GamesListActivity.class);
        startActivity(intent);
    }


    public void onStatButton(View view) {
     /*/   Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.exit_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();
*/
        Intent intent = new Intent(this, StatisticsActivity.class);
        startActivity(intent);
    }

    public void onSettingsBtn(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
}
