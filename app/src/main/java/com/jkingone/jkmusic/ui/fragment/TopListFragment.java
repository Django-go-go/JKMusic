package com.jkingone.jkmusic.ui.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
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

import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.jkingone.glide_transformation.RoundedCornersTransformation;
import com.jkingone.jkmusic.ui.base.LazyFragment;
import com.jkingone.jkmusic.viewmodels.TopListViewModel;
import com.jkingone.utils.DensityUtils;
import com.jkingone.jkmusic.GlideApp;
import com.jkingone.jkmusic.R;
import com.jkingone.jkmusic.entity.TopList;
import com.jkingone.jkmusic.ui.activity.DetailActivity;
import com.jkingone.ui.ContentLoadView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017/8/3.
 */

public class TopListFragment extends LazyFragment {

    private Unbinder mUnbinder;

    private TopListAdapter mTopListAdapter;
    private List<TopList> mTopLists = new ArrayList<>();

    @BindView(R.id.recycle_common)
    RecyclerView mRecyclerView;
    @BindView(R.id.content_common)
    ContentLoadView mContentLoadView;

    public static TopListFragment newInstance(String... params) {
        TopListFragment fragment = new TopListFragment();
        fragment.setArguments(setParams(params));
        return fragment;
    }

    private TopListViewModel mTopListViewModel;
    private Observer<List<TopList>> mTopListObserver = new Observer<List<TopList>>() {
        @Override
        public void onChanged(@Nullable List<TopList> topLists) {
            if (topLists == null) {
                mContentLoadView.postLoadFail();
                return;
            }
            if (topLists.size() == 0) {
                mContentLoadView.postLoadNoData();
                return;
            }
            mContentLoadView.postLoadComplete();
            for (TopList topList : topLists) {
                if (!topList.getType().equals("105")) {
                    mTopLists.add(topList);
                }
            }
            mTopListAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTopListViewModel = ViewModelProviders.of(this).get(TopListViewModel.class);
        mTopListViewModel.getTopListLiveData().observe(this, mTopListObserver);
    }

    @Override
    protected boolean onLazyLoadOnce() {
        mTopListViewModel.getTopList();
        return true;
    }

    @Override
    protected void onVisibleToUser() {
        if (mTopLists.size() > 0) {
            mContentLoadView.postLoadComplete();
        } else {
            mTopListViewModel.getTopList();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.common_root_none, container, false);

        mUnbinder = ButterKnife.bind(this, view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (mTopListAdapter == null) {
            mTopListAdapter = new TopListAdapter(getContext());
        }
        mRecyclerView.setAdapter(mTopListAdapter);

        mContentLoadView.setLoadRetryListener(() -> mTopListViewModel.getTopList());

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTopListViewModel.getTopListLiveData().removeObserver(mTopListObserver);
    }

    class TopListAdapter extends RecyclerView.Adapter<TopListAdapter.VH> {

        private LayoutInflater mInflater;
        private Context mContext;

        TopListAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
            mContext = context;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH(mInflater.inflate(R.layout.item_list_toplist, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull VH vh, final int position) {
            TopList topList = mTopLists.get(position);
            List<TopList.Content> contents = topList.getContent();
            List<String> strings = new ArrayList<>();
            for (TopList.Content content : contents) {
                strings.add(content.getTitle() + " - " + content.getTitle());
            }

            vh.textView1.setText(strings.get(0));
            vh.textView2.setText(strings.get(1));
            vh.textView3.setText(strings.get(2));
            vh.textView4.setText(strings.get(3));
            vh.itemView.setOnClickListener(v -> {
                if (mTopLists.get(position) != null){
                    Intent intent = new Intent(mContext, DetailActivity.class);
                    intent.putExtra(DetailActivity.TYPE_TOP_LIST, mTopLists.get(position));
                    startActivity(intent);
                }
            });

            GlideApp.with(TopListFragment.this)
                    .asBitmap()
                    .load(topList.getPicS260())
                    .override(DensityUtils.dp2px(mContext, 124))
                    .transition(BitmapTransitionOptions.withCrossFade())
                    .transform(new RoundedCorners(12))
                    .into(new BitmapImageViewTarget(vh.imageView));
        }

        @Override
        public int getItemCount() {
            return mTopLists.size();
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
