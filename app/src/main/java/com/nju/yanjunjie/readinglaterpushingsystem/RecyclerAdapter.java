package com.nju.yanjunjie.readinglaterpushingsystem;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter {
    private List<ShareContent> mItemList;

//    private ItemClickListener mItemClickListener ;
//    public interface ItemClickListener{
//        public void onItemClick(int position) ;
//    }
//    public void setOnItemClickListener(ItemClickListener itemClickListener){
//        this.mItemClickListener = itemClickListener ;
//
//    }


    public RecyclerAdapter(List<ShareContent> itemList) {
        mItemList = itemList;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.share_content, parent, false);
//        final RecyclerView.ViewHolder holder = new RecyclerView.ViewHolder(view);
        return RecyclerItemViewHolder.newInstance(view);
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        RecyclerItemViewHolder holder = (RecyclerItemViewHolder) viewHolder;
        ShareContent shareContent = mItemList.get(position);
        String itemText = shareContent.getContent();
        holder.setItemText(itemText);

//        if (mItemClickListener != null){
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // 这里利用回调来给RecyclerView设置点击事件
//                    mItemClickListener.onItemClick(position);
//                }
//            });
//        }
//
//        holder.action_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(mContext , item.getButtons() +"position -> "+position , Toast.LENGTH_SHORT).show();
//            }
//        });



    }
    @Override
    public int getItemCount() {
        return mItemList == null ? 0 : mItemList.size();
    }
}
