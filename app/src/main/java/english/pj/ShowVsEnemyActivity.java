package english.pj;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;

public class ShowVsEnemyActivity extends Activity {
    String categ;
    ImageView imAvatarPl1, imAvatarPl2;
    private Animation animFive1;
    private Animation animFive2;
    CountDownTimer countDownTimer;
    Button btnPlayers, btnShowCateg;
  //  TextView tvPl1, tvPl2;
    MediaPlayer playerGong;
    RelativeLayout LinearLayoutFive3;
    String enemy;
    int bot;
    int id;
    int round;
    int q1;
    int q2;
    int q3;
    String qnumber;
    int order;
    TextView tvp2vocab, tvp2points, tvp1info;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_vs_enemy);

        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "fonts/Raleway-Black.ttf");
        fontChanger.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));

        btnPlayers = (Button)findViewById(R.id.btnFivePlayers);
       // btnShowCateg = (Button)findViewById(R.id.btnFiveCateg);
        imAvatarPl1 = (ImageView)findViewById(R.id.imageViewAvatar1);
        imAvatarPl2 = (ImageView)findViewById(R.id.imageViewAvatar2);
        //tvPl1 = (TextView)findViewById(R.id.textViewVSPl1);
       // tvPl2 = (TextView)findViewById(R.id.textViewVSPl2);

       /* tvp2points = (TextView)findViewById(R.id.textViewVSPl2points);
        tvp2vocab = (TextView)findViewById(R.id.textViewVSPl2vocab);

        tvp2points.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/OpenSans-ExtraBold.ttf"));
        tvp2vocab.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/OpenSans-ExtraBold.ttf"));


        tvp1info = (TextView)findViewById(R.id.textViewVSPl1info);*/

        playerGong = MediaPlayer.create(ShowVsEnemyActivity.this, R.raw.gong);
        LinearLayoutFive3 = (RelativeLayout) findViewById(R.id.LinearLayoutFive3);
    }
    @Override
    protected void onResume() {
        super.onResume();

       // tvp1info.setText("Рейтинг: " + getIntent().getStringExtra("p1points") + ". Словарный запас: " + getIntent().getStringExtra("p1vocab"));

      /*  tvp2points.setText("" + getIntent().getStringExtra("p2points") + "");
//        tvp2points.setText("0");
        tvp2vocab.setText("" + getIntent().getStringExtra("p2vocab") + "");
*/
        //tvp1info.setText("Рейтинг: " + getIntent().getStringExtra("p2points") + ". Словарный запас: " + getIntent().getStringExtra("p2vocab"));

        enemy = getIntent().getStringExtra("enemy");
        bot = getIntent().getIntExtra("bot", 0);
        id = getIntent().getIntExtra("_id", 0);
        round = getIntent().getIntExtra("round", 0);
        q1 = getIntent().getIntExtra("q1", 0);
        q2 = getIntent().getIntExtra("q2", 0);
        q3 = getIntent().getIntExtra("q3", 0);
        qnumber = getIntent().getStringExtra("qnumber");
        order = getIntent().getIntExtra("order", 0);

        categ = getIntent().getStringExtra("category");
      //  btnShowCateg.setText(categ);

        String ava = "";

        if(order == 1) {
            try{
                if(enemy.equals(null) || enemy.equals("")) {
                    //tvPl1.setText(mech.getLogin(this));
                   // tvPl2.setText("???");
               //     btnShowCateg.setText(mech.getLogin(this) + " против " + "???");
                    ava = "";
                } else {
                   // tvPl1.setText(mech.getLogin(this));
                   // tvPl2.setText(enemy);
                 //   btnShowCateg.setText(mech.getLogin(this) + " против " + enemy);

                    ava = getIntent().getStringExtra("ava2");
                    if(enemy.equals("Ричард")){
                        ava = "http://o50390eh.bget.ru/uploads/Richard.jpg";
                    } else if(enemy.equals("Клавдия Степановна")){
                        ava = "http://o50390eh.bget.ru/uploads/CladvStep.jpg";
                    }else if(enemy.equals("Анжела")){
                        ava = "http://o50390eh.bget.ru/uploads/Angela.jpg";
                    }
                }
            } catch (Exception e) {
                enemy = "";
                //tvPl1.setText(mech.getLogin(this));
               // tvPl2.setText("???");
                ava = "";
            }

            try {
                Picasso.with(this).load(mech.getAvatar(this)).transform(new CircleTransform()).into(imAvatarPl1);
            } catch (Exception e) {
                Picasso.with(this).load(R.drawable.ava).resize(720, 0).onlyScaleDown().transform(new CircleTransform()).into(imAvatarPl1);
            }
            if(ava.equals("") || ava.equals(null)) {
                Picasso.with(this).load(R.drawable.ava).resize(720, 0).onlyScaleDown().transform(new CircleTransform()).into(imAvatarPl2);
            } else {
                Picasso.with(this).load(ava).resize(720, 0).onlyScaleDown().transform(new CircleTransform()).into(imAvatarPl2);
            }

        } else {
            try{
                if(enemy.equals(null)) {
                    //tvPl1.setText(mech.getLogin(this));
                    //tvPl2.setText("???");
              //      btnShowCateg.setText(mech.getLogin(this) + " против " + "???");
                    ava = "";
                } else if(enemy.equals("")) {
                    //tvPl1.setText(mech.getLogin(this));
                    //tvPl2.setText("???");
                    ava = "";
                } else {
                    //tvPl1.setText(enemy);
                    //tvPl2.setText(mech.getLogin(this));
               //     btnShowCateg.setText(mech.getLogin(this) + " против " + enemy);
                    ava = getIntent().getStringExtra("ava1");
                    if(enemy.equals("Ричард")){
                        ava = "http://o50390eh.bget.ru/uploads/Richard.jpg";
                    } else if(enemy.equals("Клавдия Степановна")){
                        ava = "http://o50390eh.bget.ru/uploads/CladvStep.jpg";
                    } else if(enemy.equals("Анжела")){
                        ava = "http://o50390eh.bget.ru/uploads/Angela.jpg";
                    }
                }
            } catch (Exception e) {
                enemy = "";
                //tvPl1.setText(mech.getLogin(this));
                //tvPl2.setText("???");
                ava = "";
            }

            try{
                Picasso.with(this).load(mech.getAvatar(this)).resize(720, 0).onlyScaleDown().transform(new CircleTransform()).into(imAvatarPl2);
            } catch (Exception e) {
                Picasso.with(this).load(R.drawable.ava).resize(720, 0).onlyScaleDown().transform(new CircleTransform()).into(imAvatarPl2);
            }

            try{
                ava.equals("");
            } catch (Exception e) { ava = "";}
            if(ava.equals("") || ava.equals(null)) {
                Picasso.with(this).load(R.drawable.ava).resize(720, 0).onlyScaleDown().transform(new CircleTransform()).into(imAvatarPl1);
            } else {
                Picasso.with(this).load(ava).resize(720, 0).onlyScaleDown().transform(new CircleTransform()).into(imAvatarPl1);
            }
        }

        animFive1 = AnimationUtils.loadAnimation(this, R.anim.showvsenemyeffect1);
        animFive2 = AnimationUtils.loadAnimation(this, R.anim.showvsenemyeffect2);
        imAvatarPl1.startAnimation(animFive1);
        imAvatarPl2.startAnimation(animFive2);
        try {
            if (mech.isSoundOn(this)) {
                playerGong.start();
            }
        } catch (Exception e) { }
    }




    public void onClickAny (View v) {
        Intent intent = new Intent(this, ChooseCategoryOfQuestionsActivity.class);

        if (categ.equals("pictures")){
            intent = new Intent(this, GamePicturesActivity.class);
        } else if (categ.equals("yesno")){
            intent = new Intent(this, GameYesNoActivity.class);
        } else if (categ.equals("letters")){
            intent = new Intent(this, GameLettersActivity.class);
        } else if (categ.equals("wordsEN")){
            intent = new Intent(this, GameWordsEnActivity.class);
        } else if (categ.equals("wordsRU")){
            intent = new Intent(this, GameWordsRuActivity.class);
        } else if (categ.equals("match")){
            intent = new Intent(this, GameMatchActivity.class);
        }else if (categ.equals("picturesRU")){
            intent = new Intent(this, GamePicturesRUActivity.class);
        }
        intent.putExtra("bot", bot);
        intent.putExtra("enemy", enemy);
        intent.putExtra("_id", id);
        intent.putExtra("round", round);
            intent.putExtra("q1", q1);
            intent.putExtra("q2", q2);
            intent.putExtra("q3", q3);
        intent.putExtra("qnumber", qnumber);  //1, 2, 3
        startActivity(intent);
    }
}
