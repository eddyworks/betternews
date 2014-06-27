package kr.betterfuture.betternews.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import kr.betterfuture.betternews.R;
import kr.betterfuture.betternews.model.Article;

/**
 * Created by Edward on 2014-06-19.
 */
public class ArticleAdapter extends ArrayAdapter<Article> {
    Context context;
    int layoutResourceId;
    ArrayList<Article> data = null;

    public ArticleAdapter(Context context, int layoutResourceId,
                                     int layoutTextViewResourceId, ArrayList<Article> data) {
        super(context, layoutResourceId, layoutTextViewResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        ViewHolder holderT = null;
        if (convertView != null) {
            holderT = (ViewHolder) convertView.getTag();
        }
        if (holderT == null) {
            holderT = new ViewHolder();
            holderT.textViewTitle = (TextView) view
                    .findViewById(R.id.textViewTitle);
            holderT.textViewDesc = (TextView) view
                    .findViewById(R.id.textViewDesc);
            holderT.textViewViews = (TextView) view
                    .findViewById(R.id.textViewViews);
            holderT.textViewWhenCreated = (TextView) view
                    .findViewById(R.id.textViewWhenCreated);
            holderT.linearArticleListItem = (LinearLayout) view
                    .findViewById(R.id.linearArticleListItem);
            if (convertView != null) {

                convertView.setTag(holderT);
            }
        }

        final Article m = data.get(position);

        holderT.textViewTitle.setText(m.Title);
        holderT.textViewDesc.setText(m.Desc);
        String views = String.valueOf(m.Views);
        holderT.textViewViews.setText(views);
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd",
                Locale.getDefault());
        String whenVal = fmt.format(m.WhenCreated);
        holderT.textViewWhenCreated.setText(whenVal);

        holderT.linearArticleListItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String articleurl = "http://betterfuture.kr" + m.Url;
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(articleurl));
                context.startActivity(intent);
            }
        });

        return view;
    }

    static class ViewHolder {
        TextView textViewTitle;
        TextView textViewDesc;
        TextView textViewViews;
        TextView textViewWhenCreated;
        LinearLayout linearArticleListItem;
    }
}
