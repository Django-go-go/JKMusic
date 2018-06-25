package com.jkingone.jkmusic.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2017/9/13.
 */

public abstract class HeadAndFootRecycleAdapter extends RecyclerView.Adapter {

    private View mHeaderLayout;
    private View mFooterLayout;

    private static final int TYPE_HEAD = 1;
    private static final int TYPE_FOOT = 3;

    public void addHeaderView(View view) {
        mHeaderLayout = view;
    }

    public void addFooterView(View view) {
        mFooterLayout = view;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case TYPE_HEAD:
                return new HeadViewHolder(mHeaderLayout);
            case TYPE_FOOT:
                return new FootViewHolder(mFooterLayout);
            default:
                return onCreateContentViewHolder(parent, viewType);
        }
    }

    public abstract RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent, int viewType) ;

    public abstract int getItemContentCount();

    public abstract int getItemContentViewType(int position);

    @Override
    public int getItemViewType(int position) {
        if (mHeaderLayout == null) {
            return getItemContentViewType(position);
        }
        if (position == 0) {
            return TYPE_HEAD;
        }
        if (mFooterLayout != null && position == getItemCount() - 1) {
            return TYPE_FOOT;
        }
        return getItemContentViewType(position - 1);

    }

    @Override
    public int getItemCount() {
        if (mHeaderLayout == null && mFooterLayout == null) {
            return getItemContentCount();
        }
        if (mHeaderLayout == null || mFooterLayout == null) {
            return getItemContentCount() + 1;
        }
        return getItemContentCount() + 2;
    }

    protected HeadOnClickListener mHeadOnClickListener;

    public void setHeadOnClickListener(HeadOnClickListener headOnClickListener) {
        mHeadOnClickListener = headOnClickListener;
    }

    public interface HeadOnClickListener{
        void headOnClick(View view);
    }

    private class HeadViewHolder extends RecyclerView.ViewHolder {

        HeadViewHolder(View itemView) {
            super(itemView);
            if (mHeaderLayout.getLayoutParams() == null) {
                mHeaderLayout.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT,
                        RecyclerView.LayoutParams.WRAP_CONTENT));
            }
        }
    }

    private class FootViewHolder extends RecyclerView.ViewHolder {

        FootViewHolder(View itemView) {
            super(itemView);
            if (mHeaderLayout.getLayoutParams() == null) {
                mHeaderLayout.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT,
                        RecyclerView.LayoutParams.WRAP_CONTENT));
            }
        }
    }
}
