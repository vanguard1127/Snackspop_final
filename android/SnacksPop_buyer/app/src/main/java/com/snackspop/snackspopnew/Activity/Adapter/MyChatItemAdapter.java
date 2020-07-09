package com.snackspop.snackspopnew.Activity.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.snackspop.snackspopnew.Model.MyChatItemModel;
import com.snackspop.snackspopnew.R;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by suraj on 2/4/17.
 */

public class MyChatItemAdapter extends RecyclerView.Adapter<MyChatItemAdapter.PlacesViewHolder> {

    private List<MyChatItemModel> m_itemModelList;

    public MyChatItemAdapter(List<MyChatItemModel> list)
    {
        m_itemModelList = list;
    }


    private Context ctx;


    @NonNull
    @Override
    public MyChatItemAdapter.PlacesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_chat_item, parent, false);
        return new PlacesViewHolder(view);

    }

    public void setContext(Context ctx) {
        this.ctx = ctx;
    }
    @Override
    public void onBindViewHolder(@NonNull MyChatItemAdapter.PlacesViewHolder holder, int position) {
        if (holder instanceof PlacesViewHolder)
        {
            PlacesViewHolder chatItemHolder = (PlacesViewHolder) holder;
            MyChatItemModel model = m_itemModelList.get(position);
            chatItemHolder.tv_text.setText(model.getMessage());
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            chatItemHolder.tv_time.setText(format.format(model.getDate()));
            int direction = model.getDirection();
            if (direction == 0)
            {
                chatItemHolder.tv_text.setBackground(ContextCompat.getDrawable(ctx, R.drawable.rounded_rectangle));
                chatItemHolder.iv_photo2.setVisibility(View.VISIBLE);
                chatItemHolder.iv_photo1.setVisibility(View.GONE);
                String url = model.getImage1Url();
                if (!TextUtils.isEmpty(model.getImage1Url()))
                {
                    String imageUrl = model.getImage1Url();
                    Glide.with(ctx).load(imageUrl).placeholder(R.drawable.ic_emptyuser).into(chatItemHolder.iv_photo2);
                }
                chatItemHolder.ll_main.setGravity(Gravity.RIGHT);
            }
            else
            {
                chatItemHolder.tv_text.setBackground(ContextCompat.getDrawable(ctx, R.drawable.rounded_rectangle_1));
                chatItemHolder.iv_photo2.setVisibility(View.GONE);
                chatItemHolder.iv_photo1.setVisibility(View.VISIBLE);
                chatItemHolder.ll_main.setGravity(Gravity.LEFT);
                if (!TextUtils.isEmpty(model.getImage2Url()))
                {
                    String imageUrl = model.getImage2Url();
                    Glide.with(ctx).load(imageUrl).placeholder(R.drawable.ic_emptyuser).into(chatItemHolder.iv_photo1);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return m_itemModelList == null ? 0 : m_itemModelList.size();
    }

    public class PlacesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tv_text , tv_time;
        private ImageView iv_photo1, iv_photo2;
        LinearLayout ll_main;
        PlacesViewHolder(View itemView) {
            super(itemView);
            tv_text = itemView.findViewById(R.id.tv_text);
            tv_time = itemView.findViewById(R.id.tv_time);
            iv_photo1 = itemView.findViewById(R.id.iv_photo1);
            iv_photo2 = itemView.findViewById(R.id.iv_photo2);
            ll_main = itemView.findViewById(R.id.ll_main);
        }
        @Override
        public void onClick(View v) {

        }
    }
}
