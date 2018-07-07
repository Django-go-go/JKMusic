package com.jkingone.jkmusic.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jkingone.commonlib.Utils.ScreenUtils;
import com.jkingone.jkmusic.R;
import com.jkingone.jkmusic.adapter.HeadAndFootRecycleAdapter;
import com.jkingone.jkmusic.data.entity.TopList;
import com.jkingone.jkmusic.ui.activity.SongListActivity;
import com.jkingone.jkmusic.ui.mvp.contract.TopListFragContract;
import com.jkingone.jkmusic.ui.mvp.TopListFragPresenter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017/8/3.
 */

public class TopListFragment extends BaseFragment<TopListFragPresenter> implements TopListFragContract.ViewCallback {

    private List<TopList> mList;

    private TopListAdapter mListAdapter;

    private Unbinder mUnbinder;

    @BindView(R.id.recycle_universal)
    RecyclerView mRecyclerView;

    public static TopListFragment newInstance(String... params) {
        TopListFragment fragment = new TopListFragment();
        fragment.setArguments(setParams(params));
        return fragment;
    }

    @Override
    protected void onLazyLoadOnce() {
        if (mList == null) {
            mPresenter.loadData();
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.recycle_universal, container, false);

        mUnbinder = ButterKnife.bind(this, view);

        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 6, GridLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.right = 8;
                outRect.top = 8;
                outRect.left = 8;
                outRect.bottom = 8;
            }
        });

        return view;
    }

    @Override
    public void showView(List<TopList> topLists) {
        if (mList == null) {
            mList = new ArrayList<>();
        } else {
            mList.clear();
        }
        for (TopList topList : topLists) {
            if (!topList.getType().equals("105")) {
                mList.add(topList);
            }
        }
        mListAdapter = new TopListAdapter(getContext(), mList);
        mRecyclerView.setAdapter(mListAdapter);
    }

    @Override
    public TopListFragPresenter createPresenter() {
        return new TopListFragPresenter(this);
    }

    class TopListAdapter extends HeadAndFootRecycleAdapter {

        private static final int TYPE_CONTENT = 2;

        private LayoutInflater mInflater;
        private Context mContext;
        private List<TopList> mList;

        TopListAdapter(Context context, List<TopList> list) {
            mInflater = LayoutInflater.from(context);
            mContext = context;
            mList = list;
        }

        @Override
        public RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {

                case TYPE_CONTENT:
                    return new VH(mInflater.inflate(R.layout.item_grid_toplist, parent, false));

                default:
                    throw new IllegalArgumentException("no Type");
            }
        }

        @Override
        public int getItemContentCount() {
            return mList.size();
        }

        @Override
        public int getItemContentViewType(int position) {
            return TYPE_CONTENT;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            if (holder instanceof VH) {
                VH vh = (VH) holder;
                TopList topList = mList.get(position);
                vh.textView.setText(topList.getName());
                vh.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mList.get(position) != null){
                            Intent intent = new Intent(mContext, SongListActivity.class);
                            intent.putExtra(SongListActivity.TYPE_TOPLIST, mList.get(position));
                            startActivity(intent);
                        }
                    }
                });

                int w = ScreenUtils.getScreenWidth(mContext);

                int h = w / 3;

                int pos = position + 1;

                if (pos % 5 == 1 || pos % 5 == 2) {
                    if (topList.getPic_s210() != null) {
                        Picasso.get().load(topList.getPic_s210())
                                .resize(w / 3 * 2, h)
                                .centerCrop()
                                .into(vh.imageView);
                    }
                } else {
                    if (topList.getPic_s260() != null) {
                        Picasso.get().load(topList.getPic_s260())
                                .resize(w / 3, h)
                                .centerCrop()
                                .into(vh.imageView);
                    }
                }
            }
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof GridLayoutManager){
                GridLayoutManager manager = (GridLayoutManager) layoutManager;
                manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        if (getItemViewType(position) == TYPE_CONTENT){
                            int pos = position + 1;
                            if (pos % 5 == 1 || pos % 5 == 2) {
                                return 3;
                            } else {
                                return 2;
                            }
                        }
                        return 6;
                    }
                });
            }
        }

        class VH extends RecyclerView.ViewHolder {
            @BindView(R.id.iv_item)
            ImageView imageView;
            @BindView(R.id.tv_item)
            TextView textView;

            VH(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }

}
