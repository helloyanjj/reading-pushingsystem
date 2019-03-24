package com.nju.yanjunjie.readinglaterpushingsystem.readlater;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nju.yanjunjie.readinglaterpushingsystem.R;
import com.nju.yanjunjie.readinglaterpushingsystem.data.ShareContent;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerItemViewHolder> {
    private List<ShareContent> mItemList;

    public RecyclerAdapter(List<ShareContent> itemList) {
        mItemList = itemList;
    }

    @Override
    public RecyclerItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.share_content, parent, false);
        final RecyclerItemViewHolder holder = new RecyclerItemViewHolder(view);
        holder.mItemTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                ShareContent shareContent = mItemList.get(position);
                Toast.makeText(v.getContext(), "点击：" + shareContent.getContent(),
                        Toast.LENGTH_SHORT).show();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerItemViewHolder holder, final int position) {
        ShareContent shareContent = mItemList.get(position);
        String itemText = shareContent.getContent();
        holder.setItemText(itemText);
        holder.isReadOnClick(shareContent);
    }

    @Override
    public int getItemCount() {
        return mItemList == null ? 0 : mItemList.size();
    }
}
