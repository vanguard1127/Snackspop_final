package com.snackspop.snackspopnew.Activity.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.snackspop.snackspopnew.Model.ChatItemsModelClass;
import com.snackspop.snackspopnew.R;
import com.snackspop.snackspopnew.Utils.AppUtils;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by suraj on 2/4/17.
 */

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;


    private int myUserId = -1;
    List<ChatItemsModelClass> eventsList;
    Fragment fragment;
    Context mContext;

    RecyclerView mRecyclerView;
    private OnEventsRecyclerItemClick mOnRecyclerItemClickListener;


    public void setmRecyclerView(RecyclerView mRecyclerView) {
        this.mRecyclerView = mRecyclerView;
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
    }


    public interface OnEventsRecyclerItemClick {
        void onItemClick(View View);
    }



    public void setmOnRecyclerItemClickListener(OnEventsRecyclerItemClick mOnRecyclerItemClickListener) {
        this.mOnRecyclerItemClickListener = mOnRecyclerItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        return eventsList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }


    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    public class PlacesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView iv_item_view;

        TextView tv_name ,tv_unread_cnt ,tv_message ;
        LinearLayout rl_main;
        PlacesViewHolder(View itemView) {
            super(itemView);
            rl_main = (LinearLayout) itemView.findViewById(R.id.rl_main);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            iv_item_view = (ImageView) itemView.findViewById(R.id.iv_photo1);
            tv_unread_cnt = (TextView) itemView.findViewById(R.id.tv_unread_cnt);
            tv_message = (TextView)itemView.findViewById(R.id.tv_message);
            rl_main.setOnClickListener(this);




        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.rl_main:
                    mOnRecyclerItemClickListener.onItemClick(rl_main);
                    break;

            }

        }
    }



    public ChatAdapter(List<ChatItemsModelClass> myItemsModelClassList, Fragment fragment) {
        this.eventsList = myItemsModelClassList;
        this.fragment = fragment;

        setContext(fragment.getContext());
        try
        {
            myUserId = Integer.parseInt(AppUtils.getUserId(mContext));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }


    public ChatAdapter(RecyclerView mRecyclerView) {
        this.mRecyclerView = mRecyclerView;
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();

    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i == VIEW_TYPE_ITEM) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.raw_chat_list, viewGroup, false);
            return new PlacesViewHolder(v);
        } else if (i == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.raw_loading_item, viewGroup, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int i) {
        if (holder instanceof PlacesViewHolder) {
            final PlacesViewHolder personViewHolder = (PlacesViewHolder) holder;
            personViewHolder.rl_main.setTag(i);
            personViewHolder.tv_name.setText(eventsList.get(i).getName());


            int unread_count = eventsList.get(i).getChat_unread_count();


            int userid = eventsList.get(i).getUserid();
            int mUserId =Integer.parseInt(AppUtils.getUserId(mContext));
            if (userid != mUserId)
            {
                personViewHolder.rl_main.setBackgroundColor(mContext.getResources().getColor(R.color.colorMyChatItem));
                personViewHolder.tv_unread_cnt.setVisibility(View.GONE);
            }
            else
            {
                personViewHolder.rl_main.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                if (unread_count == 0)
                    personViewHolder.tv_unread_cnt.setVisibility(View.GONE);
                else
                {
                    personViewHolder.tv_unread_cnt.setVisibility(View.VISIBLE);
                    personViewHolder.tv_unread_cnt.setText(String.format("%d",unread_count));
                }
            }

            String message = eventsList.get(i).getMessage();
            if (message.length() > 20)
            {
                message = message.substring(0, 20) + "...";
            }
            personViewHolder.tv_message.setText(message);
            if (!TextUtils.isEmpty(eventsList.get(i).getImageUrl()))
            {
                String imageUrl = AppUtils.BASE_URL + eventsList.get(i).getImageUrl();
                Glide.with(mContext).load(imageUrl).placeholder(R.drawable.ic_logo_cir_red).into(personViewHolder.iv_item_view);
            }
            else
                personViewHolder.iv_item_view.setImageResource(R.drawable.ic_logo_cir_red);

            DecimalFormat df = new DecimalFormat("#.##");



        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }
}
