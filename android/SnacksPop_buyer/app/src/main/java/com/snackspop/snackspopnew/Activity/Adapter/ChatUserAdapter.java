package com.snackspop.snackspopnew.Activity.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.snackspop.snackspopnew.Model.ChatUserModelClass;
import com.snackspop.snackspopnew.R;
import com.snackspop.snackspopnew.Utils.AppUtils;

import java.util.List;

/**
 * Created by suraj on 2/4/17.
 */

public class ChatUserAdapter extends RecyclerView.Adapter<ChatUserAdapter.PlacesViewHolder> {

    private List<ChatUserModelClass> m_itemModelList;

    public ChatUserAdapter(List<ChatUserModelClass> list)
    {
        m_itemModelList = list;
    }

    private Context ctx;


    @NonNull
    @Override
    public ChatUserAdapter.PlacesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_chat_user, parent, false);
        return new PlacesViewHolder(view);

    }

    public void setContext(Context ctx) {
        this.ctx = ctx;
    }
    @Override
    public void onBindViewHolder(@NonNull ChatUserAdapter.PlacesViewHolder holder, int i) {
        if (holder instanceof PlacesViewHolder)
        {
            final PlacesViewHolder chatItemHolder = (PlacesViewHolder) holder;
            ChatUserModelClass model = m_itemModelList.get(i);
            chatItemHolder.ll_main.setTag(i);
            chatItemHolder.tv_user_name.setText(m_itemModelList.get(i).getUser_name());

            int unread_count = m_itemModelList.get(i).getUnread_count();
            if (unread_count == 0)
                chatItemHolder.tv_unread_cnt.setVisibility(View.GONE);
            else
            {
                chatItemHolder.tv_unread_cnt.setVisibility(View.VISIBLE);
                chatItemHolder.tv_unread_cnt.setText(String.format("%d" ,unread_count));
            }
            String message = m_itemModelList.get(i).getMessage();
            if (message.length() > 20)
            {
                message = message.substring(0, 20) + "...";
            }
            chatItemHolder.tv_text.setText(message);
            if (!TextUtils.isEmpty(m_itemModelList.get(i).getUser_image()))
            {
                String imageUrl = AppUtils.BASE_URL + AppUtils.AFTER_BASE_URL.IMAGE_URL + m_itemModelList.get(i).getUser_image();
                Glide.with(ctx).load(imageUrl).placeholder(R.drawable.ic_emptyuser).into(chatItemHolder.iv_photo1);
            }
            else
                chatItemHolder.iv_photo1.setImageResource(R.drawable.ic_emptyuser);
        }
    }
    public interface OnEventsRecyclerItemClick {
        void onItemClick(View View);
    }
    private OnEventsRecyclerItemClick mOnRecyclerItemClickListener;

    public void setmOnRecyclerItemClickListener(OnEventsRecyclerItemClick mOnRecyclerItemClickListener) {
        this.mOnRecyclerItemClickListener = mOnRecyclerItemClickListener;
    }

    @Override
    public int getItemCount() {
        return m_itemModelList == null ? 0 : m_itemModelList.size();
    }

    public class PlacesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tv_user_name , tv_text ,tv_unread_cnt;
        private ImageView iv_photo1;
        LinearLayout ll_main;
        PlacesViewHolder(View itemView) {
            super(itemView);
            tv_user_name = itemView.findViewById(R.id.tv_user_name);
            tv_text = itemView.findViewById(R.id.tv_text);
            tv_unread_cnt = itemView.findViewById(R.id.tv_unread_cnt);
            iv_photo1 = itemView.findViewById(R.id.iv_photo1);
            ll_main = (LinearLayout) itemView.findViewById(R.id.ll_main);

            ll_main.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ll_main:
                    mOnRecyclerItemClickListener.onItemClick(ll_main);
                    break;

            }

        }


    }
}
