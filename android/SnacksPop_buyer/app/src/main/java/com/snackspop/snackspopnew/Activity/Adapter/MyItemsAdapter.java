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

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by suraj on 2/4/17.
 */

public class MyItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private boolean isLoading;
    private int visibleThreshold;
    private int lastVisibleItem, totalItemCount;

    private boolean isMyProducts = false;

    public void setIsMyProducts(boolean is)
    {
        isMyProducts = is;
    }
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

//                totalItemCount = linearLayoutManager.getItemCount();
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
        ImageView iv_item_view;
        TextView tv_name , tv_distance, tv_price;
        LinearLayout rl_main;

        PlacesViewHolder(View itemView) {
            super(itemView);
            rl_main = (LinearLayout) itemView.findViewById(R.id.rl_main);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_distance = (TextView) itemView.findViewById(R.id.tv_distance);
            tv_price = (TextView) itemView.findViewById(R.id.tv_price);
            iv_item_view = (ImageView) itemView.findViewById(R.id.iv_photo1);
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

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public void setLoaded() {
        isLoading = false;
    }

    public MyItemsAdapter(Context context, List<MyItemsModelClass> myItemsModelClassList) {
        this.eventsList = myItemsModelClassList;
        this.mContext = context;

    }


    public MyItemsAdapter(RecyclerView mRecyclerView) {
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
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.raw_home_list, viewGroup, false);
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

            //personViewHolder.tv_price.setText(AppUtils.displayCurrencyInfoForLocale(Locale.getDefault(),mContext) +eventsList.get(i).getPrice());
            personViewHolder.tv_price.setText(eventsList.get(i).getPrice_unit() + eventsList.get(i).getPrice());
            if (!TextUtils.isEmpty(eventsList.get(i).getImageUrl()))
            {
                String imageUrl = AppUtils.BASE_URL + eventsList.get(i).getImageUrl();
                Glide.with(mContext).load(imageUrl).placeholder(R.drawable.ic_logo_cir_red).into(personViewHolder.iv_item_view);
            }
            else
                personViewHolder.iv_item_view.setImageResource(R.drawable.ic_logo_cir_red);

            DecimalFormat df = new DecimalFormat("#.##");

            if (eventsList.get(i).getDistance() > -1 && !isMyProducts)
                personViewHolder.tv_distance.setText(df.format(eventsList.get(i).getDistance()) + " "+eventsList.get(i).getUnit());
            else
                personViewHolder.tv_distance.setText("");

        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }
}
