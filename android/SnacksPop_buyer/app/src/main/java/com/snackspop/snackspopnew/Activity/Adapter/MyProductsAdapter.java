package com.snackspop.snackspopnew.Activity.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.snackspop.snackspopnew.Model.MyItemsModelClass;
import com.snackspop.snackspopnew.R;
import com.snackspop.snackspopnew.Utils.AppUtils;

import java.util.List;
import java.util.Locale;

/**
 * Created by suraj on 2/4/17.
 */

public class MyProductsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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
    Fragment fragment;
    Context mContext;

    RecyclerView mRecyclerView;
    private OnEventsRecyclerItemClick mOnRecyclerItemClickListener;
    private OnEventRecyclerItemRemoveBtnClick mOnEventRecyclerItemRemoveBtnClick;


    public void setmRecyclerView(RecyclerView mRecyclerView) {
        this.mRecyclerView = mRecyclerView;
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
    }

    public OnEventRecyclerItemRemoveBtnClick getmOnEventRecyclerItemRemoveBtnClick() {
        return mOnEventRecyclerItemRemoveBtnClick;
    }

    public void setmOnEventRecyclerItemRemoveBtnClick(OnEventRecyclerItemRemoveBtnClick mOnEventRecyclerItemRemoveBtnClick) {
        this.mOnEventRecyclerItemRemoveBtnClick = mOnEventRecyclerItemRemoveBtnClick;
    }

    public interface OnEventsRecyclerItemClick {
        void onItemClick(View View);
    }

    public interface OnEventRecyclerItemRemoveBtnClick
    {
        void onItemRemoveClick(View view);
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



    public void setVisibleThreshold(int visibleThreshold) {
        this.visibleThreshold = visibleThreshold;
    }

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    public class PlacesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView iv_item_view;
        TextView tv_name , tv_price;
        LinearLayout rl_main;
        Button bt_remove;
        PlacesViewHolder(View itemView) {
            super(itemView);
            rl_main = (LinearLayout) itemView.findViewById(R.id.rl_main);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_price = (TextView) itemView.findViewById(R.id.tv_price);
            iv_item_view = (ImageView) itemView.findViewById(R.id.iv_photo1);

            rl_main.setOnClickListener(this);

            bt_remove = (Button)itemView.findViewById(R.id.bt_remove);
            bt_remove.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.rl_main:
                    mOnRecyclerItemClickListener.onItemClick(rl_main);
                    break;
                case R.id.bt_remove:
                    mOnEventRecyclerItemRemoveBtnClick.onItemRemoveClick(rl_main);
                    break;
            }

        }
    }



    public MyProductsAdapter(List<MyItemsModelClass> myItemsModelClassList, Fragment fragment) {
        this.eventsList = myItemsModelClassList;
        this.fragment = fragment;
        setContext(fragment.getContext());

    }


    public MyProductsAdapter(RecyclerView mRecyclerView) {
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
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.raw_myproduct_list, viewGroup, false);
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
            String item_price_unit = eventsList.get(i).getPrice_unit();
            if (item_price_unit == null || item_price_unit.equals("null"))
                item_price_unit= AppUtils.displayCurrencyInfoForLocale(Locale.getDefault(),mContext);
            personViewHolder.tv_price.setText(item_price_unit +eventsList.get(i).getPrice());
            if (!TextUtils.isEmpty(eventsList.get(i).getImageUrl()))
            {
                String imageUrl = AppUtils.BASE_URL + eventsList.get(i).getImageUrl();
                Glide.with(mContext).load(imageUrl).placeholder(R.drawable.ic_logo_cir_red).into(personViewHolder.iv_item_view);
            }
            else
                personViewHolder.iv_item_view.setImageResource(R.drawable.ic_logo_cir_red);
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }
}
