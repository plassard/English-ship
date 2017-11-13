package english.pj;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import java.util.StringTokenizer;

public class ShowProfileActivity extends Activity {
    String login, Nick;
    ImageView ivAvatar;
    TextView tvNick, tvLocation, tvRank, tvWins, tvDraws, tvLosses;
    boolean isFriends;
    int i;
    ProgressBar pbWins, pbDraws, pbLosses;
    Button btnFriends, btnInviteFriend, btnChat;
    boolean active = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profile);

        tvLocation = (TextView)findViewById(R.id.textViewLocation);
        tvNick = (TextView)findViewById(R.id.textViewNick);
        tvRank = (TextView)findViewById(R.id.textViewNickRank);
        btnFriends = (Button)findViewById(R.id.buttonAddDeleteFriend);
        btnInviteFriend = (Button)findViewById(R.id.btnInviteFriend);
        btnChat = (Button)findViewById(R.id.buttonChat);
        tvWins = (TextView)findViewById(R.id.textViewWinsShowUser);
        tvDraws = (TextView)findViewById(R.id.textViewDrawsShowUser);
        tvLosses = (TextView)findViewById(R.id.textViewLossesShowUser);
        pbWins = (ProgressBar)findViewById(R.id.progressBarWinsShowUser);
        pbDraws = (ProgressBar)findViewById(R.id.progressBarDrawsShowUser);
        pbLosses = (ProgressBar)findViewById(R.id.progressBarLossesShowUser);
        ivAvatar = (ImageView)findViewById(R.id.imageViewShowPlayerAvatar);
    }

    @Override
    protected void onResume() {
        active = true;
        btnInviteFriend.setText("Вызвать на бой");
        super.onResume();
        String ava = "";
        int totalGames = 0;
        int wins = 0;
        int losses = 0;
        int draws = 0;
        try {
            tvNick.setText(BWGetAnPlayerInfo.jsonObjectAnPl.getString("login"));
            try {
                if(BWGetAnPlayerInfo.jsonObjectAnPl.getString("city").equals("")){
                    tvLocation.setText(BWGetAnPlayerInfo.jsonObjectAnPl.getString("country"));
                } else {
                    tvLocation.setText(BWGetAnPlayerInfo.jsonObjectAnPl.getString("country") + ", " + BWGetAnPlayerInfo.jsonObjectAnPl.getString("city")); }
            } catch (Exception e) {tvLocation.setText(""); }
            tvRank.setText("  Рейтинг:  " + BWGetAnPlayerInfo.jsonObjectAnPl.getString("points"));
            ava = BWGetAnPlayerInfo.jsonObjectAnPl.getString("avatar");
            Nick = BWGetAnPlayerInfo.jsonObjectAnPl.getString("login");
        } catch (Exception e) {   }
        try {
            wins = Integer.valueOf(BWGetAnPlayerInfo.jsonObjectAnPl.getString("wins"));
            losses = Integer.valueOf(BWGetAnPlayerInfo.jsonObjectAnPl.getString("losses"));
            draws = Integer.valueOf(BWGetAnPlayerInfo.jsonObjectAnPl.getString("draws"));
            totalGames = wins + losses + draws;
            tvWins.setText(BWGetAnPlayerInfo.jsonObjectAnPl.getString("wins"));
            tvDraws.setText(BWGetAnPlayerInfo.jsonObjectAnPl.getString("draws"));
            tvLosses.setText(BWGetAnPlayerInfo.jsonObjectAnPl.getString("losses"));
        } catch (Exception e) {   }
        pbWins.setMax(totalGames);
        pbDraws.setMax(totalGames);
        pbLosses.setMax(totalGames);
        pbWins.setProgress(wins);
        pbDraws.setProgress(draws);
        pbLosses.setProgress(losses);

        if(ava.equals("")) {
            Picasso.with(this).load(R.drawable.ava).resize(720, 0).onlyScaleDown().transform(new CircleTransform()).into(ivAvatar);
        } else {
            Picasso.with(this).load(ava).resize(720, 0).onlyScaleDown().transform(new CircleTransform()).into(ivAvatar);
        }


        isFriend();

        //спрятать кнопки если профиль игрока твой собственный
        try {
            if(mech.getLogin(this).equals(BWGetAnPlayerInfo.jsonObjectAnPl.getString("login"))) {
                btnInviteFriend.setVisibility(View.GONE);
                btnFriends.setVisibility(View.GONE);
                btnChat.setVisibility(View.GONE);
            } else {
                btnInviteFriend.setVisibility(View.VISIBLE);
                btnFriends.setVisibility(View.VISIBLE);
                btnChat.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {

        }
    }

    private boolean isFriend() {
        if(BWUserStatistics.friendsList.contains(Nick)) {
            isFriends = true;
        } else {
            isFriends = false;
        }

        if(isFriends == true) {
            btnFriends.setBackgroundResource(R.drawable.btn_red_pressed);
            btnFriends.setText("Убрать из друзей");
            return true;
        } else {
            btnFriends.setBackgroundResource(R.drawable.btn_red_selector);
            btnFriends.setText("В друзья");
            return false; }
    }

    public void onPlayWithFriend (View v) {

        try {
            if(!(Nick.equals(mech.getLogin(this)))) {
                BWInviteToPlay bwInviteToPlay = new BWInviteToPlay(this);
                bwInviteToPlay.execute(mech.getLogin(this), Nick);
                btnInviteFriend.setText("Вызван");
               // BWSendPushNotification bwSendPushNotification = new BWSendPushNotification(this);
               // bwSendPushNotification.execute("invt", BWGetAnPlayerInfo.jsonObjectAnPl.getString("login"), "inv");
            }
        } catch (Exception e) {   }

    }



    public void onAddDeleteFriend(View v) {

        if(isFriends == true) {
            //убрать из друзей
            String allFriendsInRow = "";
            String checkFriend;
            String newFriendsRow = "";

            try {
                allFriendsInRow = BWUserStatistics.json_row.getString("friends");
                StringTokenizer stringTokenizer = new StringTokenizer(allFriendsInRow, ",");
                while (stringTokenizer.hasMoreTokens()) {
                    checkFriend = stringTokenizer.nextToken();
                    if(checkFriend.equals(Nick)) { }
                    else {
                        newFriendsRow = newFriendsRow + "," + checkFriend;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            BWDeleteFriend bwDeleteFriend = new BWDeleteFriend(this);
            bwDeleteFriend.execute(mech.getLogin(this), newFriendsRow);
            isFriends = false;
            btnFriends.setBackgroundResource(R.drawable.btn_red_selector);
            btnFriends.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
            btnFriends.setText("В друзья");
        } else {
            //добавить в друзья
            BWPutFriend bwPutFriend = new BWPutFriend(this);
            bwPutFriend.execute(mech.getLogin(this), Nick);
            isFriends = true;
            btnFriends.setBackgroundResource(R.drawable.btn_red_pressed);
            btnFriends.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            btnFriends.setText("Убрать из друзей");
        }
    }

    public void onChatButton(View v){
        Intent intent = new Intent(this, ChatActivity.class);
        try {
            ChatActivity.anotherPartnerInChat = BWGetAnPlayerInfo.jsonObjectAnPl.getString("login");
            intent.putExtra("ava", BWGetAnPlayerInfo.jsonObjectAnPl.getString("avatar"));
        } catch (Exception e) {
            ChatActivity.anotherPartnerInChat = "";
            intent.putExtra("ava", "");
        }
        startActivity(intent);

    }

    @Override
    protected void onStop() {
        active = false;
        super.onStop();
    }
}
