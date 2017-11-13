package english.pj;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomListAdapter extends BaseAdapter {
    private ArrayList listData;
    private LayoutInflater layoutInflater;
    Context contextHere;


    public CustomListAdapter(Context context, ArrayList listData) {
        contextHere = context;
        this.listData = listData;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_row_in_ranking, null);
            //convertView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            holder = new ViewHolder();
            holder.number = (TextView) convertView.findViewById(R.id.textNumber);
            holder.imageView = (ImageView) convertView.findViewById(R.id.img);
            holder.name = (TextView) convertView.findViewById(R.id.textName);
            holder.score = (TextView) convertView.findViewById(R.id.textRankScore);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ListItem rankItem = (ListItem) listData.get(position);
        holder.name.setText(rankItem.getName());
        holder.number.setText(rankItem.getNumber());
        holder.score.setText(rankItem.getScore());

        if (holder.imageView != null) {
            if(rankItem.getUrl().equals("") || rankItem.getUrl().equals(null)) {
                Picasso.with(contextHere).load(R.drawable.ava).resize(720, 0).onlyScaleDown().transform(new CircleTransform()).into(holder.imageView);
               // holder.imageView.setImageResource(R.drawable.ava);
            } else {
                Picasso.with(contextHere).load(rankItem.getUrl()).resize(720, 0).onlyScaleDown().transform(new CircleTransform()).into(holder.imageView);
            }
        }
        return convertView;
    }

    static class ViewHolder {
        TextView number;
        TextView name;
        TextView score;
        ImageView imageView;
    }
}