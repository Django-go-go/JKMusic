package com.jkingone.jkmusic.ui.fragment;

import android.content.Context;
import android.graphics.Rect;
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

import com.jkingone.commonlib.Utils.DensityUtils;
import com.jkingone.jkmusic.R;
import com.jkingone.jkmusic.entity.TopList;
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.recycle_universal, container, false);

        mUnbinder = ButterKnife.bind(this, view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

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
            vh.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (mList.get(position) != null){
//                        Intent intent = new Intent(mContext, SongListActivity.class);
//                        intent.putExtra(SongListActivity.TYPE_TOPLIST, mList.get(position));
//                        startActivity(intent);
//                    }
                }
            });

            int px = DensityUtils.dp2px(mContext, 128);

            Picasso.get().load(topList.getPicS260())
                    .resize(px, px)
                    .centerCrop()
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
