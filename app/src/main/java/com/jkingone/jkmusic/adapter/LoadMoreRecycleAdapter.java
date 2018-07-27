package com.jkingone.jkmusic.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.jkingone.common.utils.DensityUtils;
import com.jkingone.ui.widget.FootLoadView;

/**
 * Created by Administrator on 2017/9/13.
 */

public abstract class LoadMoreRecycleAdapter extends RecyclerView.Adapter {

    private static final int TYPE_FOOT = 100;

    public FootLoadView mFootLoadView;

    protected Context mContext;

    public LoadMoreRecycleAdapter(Context context) {
        mContext = context;
        mFootLoadView = new FootLoadView(mContext);
        mFootLoadView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, DensityUtils.dp2px(mContext, 48)));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case TYPE_FOOT: {
                return new FootViewHolder(mFootLoadView);
            }
            default:
                return onCreateContentViewHolder(parent, viewType);
        }
    }

    public abstract RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent, int viewType) ;

    public abstract int getItemContentCount();

    public abstract int getItemContentViewType(int position);

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return TYPE_FOOT;
        }
        return getItemContentViewType(position);

    }

    @Override
    public int getItemCount() {
        return getItemContentCount() + 1;
    }

    private class FootViewHolder extends RecyclerView.ViewHolder {

        FootViewHolder(View itemView) {
            super(itemView);
        }
    }
}
