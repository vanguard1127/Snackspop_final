package com.snackspop.snackspopnew.Activity.Adapter;

import android.content.Context;
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
import com.snackspop.snackspopnew.Model.MyItemsModelClass;
import com.snackspop.snackspopnew.R;
import com.snackspop.snackspopnew.Utils.AppUtils;
import com.snackspop.snackspopnew.Utils.LogCat;

import java.util.List;

/**
 * Created by suraj on 2/4/17.
 */

public class MyItemsHorizontalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private boolean isLoading;
    private int visibleThreshold;
    private int lastVisibleItem, totalItemCount;

    List<MyItemsModelClass> eventsList;
    Context mContext;

    private OnLoadMoreListener mOnLoadMoreListener;
    RecyclerView mRecyclerView;
    private OnEventsRecyclerItemClick mOnRecyclerItemClickListener;

    public void setmRecyclerView(RecyclerView mRecyclerView) {
        this.mRecyclerView = mRecyclerView;
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition() + 1;
                LogCat.e("befor Loadmore called --> isLoading" + isLoading + " totalItemCoun" + totalItemCount
                        + " lastVisibleItem" + lastVisibleItem + " visibleThreshold" + visibleThreshold + " categoriesList.size()" + eventsList.size());
                if (!isLoading && (eventsList.size() == lastVisibleItem)
                        && totalItemCount > eventsList.size()) {
                    LogCat.e("Loadmore called-->" + "totalItemCoun" + totalItemCount + " lastVisibleItem"
                            + lastVisibleItem + " visibleThreshold" + visibleThreshold + " categoriesList.size()" + eventsList.size());
                    if (mOnLoadMoreListener != null) {
                        mOnLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
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

    public void setTotalItemCount(int totalItemCount) {
        this.totalItemCount = totalItemCount;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    public void setVisibleThreshold(int visibleThreshold) {
        this.visibleThreshold = visibleThreshold;
    }

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    public class PlacesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView iv_item_image;
        TextView tv_item_name;
        LinearLayout ll_main;

        PlacesViewHolder(View itemView) {
            super(itemView);
            ll_main = (LinearLayout) itemView.findViewById(R.id.ll_main);
            tv_item_name = (TextView) itemView.findViewById(R.id.tv_item_name);
            iv_item_image = (ImageView) itemView.findViewById(R.id.iv_item_image);
            ll_main.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ll_main:
                    if (mOnRecyclerItemClickListener != null)
                        mOnRecyclerItemClickListener.onItemClick(ll_main);
                    break;
            }

        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public void setLoaded() {
        isLoading = false;
    }

    public MyItemsHorizontalAdapter(List<MyItemsModelClass> myItemsModelClassList, Context mContext) {
        this.eventsList = myItemsModelClassList;
        this.mContext = mContext;
    }

    public MyItemsHorizontalAdapter(RecyclerView mRecyclerView) {
        this.mRecyclerView = mRecyclerView;
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

//                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (mOnLoadMoreListener != null) {
                        mOnLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i == VIEW_TYPE_ITEM) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.raw_other_items, viewGroup, false);
            PlacesViewHolder pvh = new PlacesViewHolder(v);
            return pvh;
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
            PlacesViewHolder personViewHolder = (PlacesViewHolder) holder;
            personViewHolder.ll_main.setTag(i);
            personViewHolder.tv_item_name.setText(eventsList.get(i).getName());
            if (!TextUtils.isEmpty(eventsList.get(i).getImageUrl()) && !eventsList.get(i).getImageUrl().equals("null")) {
                String imageUrl = AppUtils.BASE_URL + eventsList.get(i).getImageUrl();
                Glide.with(mContext).load(imageUrl).placeholder(R.drawable.ic_logo_cir_red).into(personViewHolder.iv_item_image);
            }
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }
}
