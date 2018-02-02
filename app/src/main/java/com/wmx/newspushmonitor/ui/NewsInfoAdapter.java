package com.wmx.newspushmonitor.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wmx.newspushmonitor.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangmingxing on 18-1-31.
 */

public class NewsInfoAdapter extends RecyclerView.Adapter<NewsInfoAdapter.ViewHolder> {
    private List<NewsInfoItem> mNewsInfoList = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mNewsInfo;

        public ViewHolder(TextView v) {
            super(v);
            mNewsInfo = v;
        }
    }

    public void addNewsInfoItem(NewsInfoItem newsInfoItem) {
        for (NewsInfoItem ni : mNewsInfoList) {
            if (ni.packageName.equals(newsInfoItem.packageName)) {
                ni.newsInfo = newsInfoItem.newsInfo;
                return;
            }
        }

        mNewsInfoList.add(newsInfoItem);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_info_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mNewsInfo.setText(mNewsInfoList.get(position).newsInfo);
    }

    @Override
    public int getItemCount() {
        return mNewsInfoList.size();
    }
}
