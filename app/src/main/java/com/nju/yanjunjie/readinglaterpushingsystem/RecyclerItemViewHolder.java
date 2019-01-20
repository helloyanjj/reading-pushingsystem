package com.nju.yanjunjie.readinglaterpushingsystem;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class RecyclerItemViewHolder extends RecyclerView.ViewHolder {
    private final TextView mItemTextView;
    private final View shareContentView;

//    public RecyclerItemViewHolder (View view) {
//        super(view);
//        mItemTextView = (TextView)view.findViewById(R.id.itemTextView);
//    }

    public RecyclerItemViewHolder(final View parent, TextView itemTextView) {
        super(parent);
        mItemTextView = itemTextView;
        shareContentView = parent;
    }
    public static RecyclerItemViewHolder newInstance(View parent) {
        TextView itemTextView = (TextView) parent.findViewById(R.id.itemTextView);
        return new RecyclerItemViewHolder(parent, itemTextView);
    }
    public void setItemText(CharSequence text) {
        mItemTextView.setText(text);
    }

}