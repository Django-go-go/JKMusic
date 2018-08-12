package com.jkingone.jkmusic.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jkingone.utils.DensityUtils;
import com.jkingone.jkmusic.GlideApp;
import com.jkingone.jkmusic.R;
import com.jkingone.jkmusic.entity.TopList;
import com.jkingone.jkmusic.ui.activity.SongAndTopListActivity;
import com.jkingone.jkmusic.ui.base.BaseFragment;
import com.jkingone.jkmusic.ui.mvp.contract.TopListFragContract;
import com.jkingone.jkmusic.ui.mvp.presenter.TopListFragPresenter;
import com.jkingone.ui.widget.ContentLoadView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017/8/3.
 */

public class TopListFragment extends BaseFragment<TopListFragPresenter> implements TopListFragContract.ViewCallback {

    private Unbinder mUnbinder;

    @BindView(R.id.recycle_common)
    RecyclerView mRecyclerView;
    @BindView(R.id.content_common)
    ContentLoadView mContentLoadView;

    public static TopListFragment newInstance(String... params) {
        TopListFragment fragment = new TopListFragment();
        fragment.setArguments(setParams(params));
        return fragment;
    }

    @Override
    protected void onLazyLoadOnce() {
        mPresenter.loadData();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.common_root_none, container, false);

        mUnbinder = ButterKnife.bind(this, view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

//        LinearSnapHelper linearSnapHelper = new LinearSnapHelper();
//        linearSnapHelper.attachToRecyclerView(mRecyclerView);

//        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
//        pagerSnapHelper.attachToRecyclerView(mRecyclerView);



        mContentLoadView.setLoadRetryListener(new ContentLoadView.LoadRetryListener() {
            @Override
            public void onRetry() {
                mPresenter.loadData();
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void showView(List<TopList> topLists) {
        if (topLists == null) {
            mContentLoadView.postLoadFail();
            return;
        }
        if (topLists.size() == 0) {
            mContentLoadView.postLoadNoData();
            return;
        }
        mContentLoadView.postLoadComplete();
        List<TopList> mList = new ArrayList<>();
        for (TopList topList : topLists) {
            if (!topList.getType().equals("105")) {
                mList.add(topList);
            }
        }
        TopListAdapter listAdapter = new TopListAdapter(getContext(), mList);
        mRecyclerView.setAdapter(listAdapter);
    }

    @Override
    public TopListFragPresenter createPresenter() {
        return new TopListFragPresenter(this);
    }

    class TopListAdapter extends RecyclerView.Adapter<TopListAdapter.VH> {

        private LayoutInflater mInflater;
        private Context mContext;
        private List<TopList> mList;

        TopListAdapter(Context context, List<TopList> list) {
            mInflater = LayoutInflater.from(context);
            mContext = context;
            mList = list;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH(mInflater.inflate(R.layout.item_list_toplist, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull VH vh, final int position) {
            TopList topList = mList.get(position);
            List<TopList.Content> contents = topList.getContent();
            List<String> strings = new ArrayList<>();
            for (TopList.Content content : contents) {
                strings.add(content.getTitle() + " - " + content.getTitle());
            }

            vh.textView1.setText(strings.get(0));
            vh.textView2.setText(strings.get(1));
            vh.textView3.setText(strings.get(2));
            vh.textView4.setText(strings.get(3));
            vh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mList.get(position) != null){
                        Intent intent = new Intent(mContext, SongAndTopListActivity.class);
                        intent.putExtra(SongAndTopListActivity.TYPE_TOP_LIST, mList.get(position));
                        startActivity(intent);
                    }
                }
            });

            GlideApp.with(TopListFragment.this)
                    .asBitmap()
                    .load(topList.getPicS260())
                    .override(DensityUtils.dp2px(mContext, 124))
                    .into(vh.imageView);
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        class VH extends RecyclerView.ViewHolder {
            @BindView(R.id.iv_item)
            ImageView imageView;
            @BindView(R.id.tv_item_1)
            TextView textView1;
            @BindView(R.id.tv_item_2)
            TextView textView2;
            @BindView(R.id.tv_item_3)
            TextView textView3;
            @BindView(R.id.tv_item_4)
            TextView textView4;

            VH(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }

}
