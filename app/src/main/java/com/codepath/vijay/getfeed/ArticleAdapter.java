package com.codepath.vijay.getfeed;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.vijay.getfeed.Activity.ArticleBrowserActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by uttamavillain on 2/13/16.
 */

public class ArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Store a member variable for the contacts
    private List<Article> mArticles;
    private final int WITHIMAGE = 0;
    private final int WITHOUTIMAGE = 1;

    // Pass in the contact array into the constructor
    public ArticleAdapter(List<Article> articles) {
        mArticles = articles;
    }

    @Override
    public int getItemViewType(int position) {
        if (TextUtils.isEmpty(mArticles.get(position).getThumbNail())) {
            return WITHOUTIMAGE;
        } else {
            return WITHIMAGE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        RecyclerView.ViewHolder viewHolder;

        // Inflate the custom layout
        if(viewType == WITHIMAGE) {
            View contactView = inflater.inflate(R.layout.item_article_result1, parent, false);
            viewHolder = new ViewHolder1(contactView);
        } else {
            View contactView = inflater.inflate(R.layout.item_article_result2, parent, false);
            viewHolder = new ViewHolder2(contactView);
        }

        // Return a new holder instance
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Article article = mArticles.get(position);

        if(viewHolder.getItemViewType() == WITHIMAGE) {
            TextView tvTitle = ((ViewHolder1)viewHolder).tvTitle;
            ImageView ivImage = ((ViewHolder1)viewHolder).ivImage;
            tvTitle.setText(article.getHeadLines());
            String thumbNail = article.getThumbNail();

            if(!TextUtils.isEmpty(thumbNail)) {
                Picasso.with(viewHolder.itemView.getContext()).load(thumbNail).into(ivImage);
            }
        } else {
            TextView tvTitle = ((ViewHolder2)viewHolder).tvTitle;
            tvTitle.setText(article.getHeadLines());
        }

    }

    // Return the total count of items
    @Override
    public int getItemCount() {
        return mArticles.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder1 extends RecyclerView.ViewHolder implements View.OnClickListener{
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        TextView tvTitle;
        ImageView ivImage;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder1(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
            itemView.setOnClickListener(this);
        }

        // Handles the row being being clicked
        @Override
        public void onClick(View view) {
            int position = getLayoutPosition(); // gets item position
            Intent intent = new Intent(itemView.getContext(), ArticleBrowserActivity.class);
            intent.putExtra("url", mArticles.get(position).getArticleUrl());
            itemView.getContext().startActivity(intent);
        }
    }

    public class ViewHolder2 extends RecyclerView.ViewHolder implements View.OnClickListener{
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        TextView tvTitle;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder2(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            itemView.setOnClickListener(this);
        }

        // Handles the row being being clicked
        @Override
        public void onClick(View view) {
            int position = getLayoutPosition(); // gets item position
            Intent intent = new Intent(itemView.getContext(), ArticleBrowserActivity.class);
            intent.putExtra("url", mArticles.get(position).getArticleUrl());
            itemView.getContext().startActivity(intent);
        }
    }
}