package com.jkingone.jkmusic.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jkingone.jkmusic.R;
import com.jkingone.jkmusic.adapter.SongListAdapter;
import com.jkingone.jkmusic.entity.SongList;
import com.jkingone.jkmusic.ui.activity.ClassifySongListActivity;
import com.jkingone.jkmusic.ui.activity.SongAndTopListActivity;
import com.jkingone.jkmusic.ui.base.BaseFragment;
import com.jkingone.jkmusic.ui.mvp.contract.SongListFragContract;
import com.jkingone.jkmusic.ui.mvp.SongListFragPresenter;
import com.jkingone.ui.customview.ContentLoadView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017/8/6.
 */

public class SongListFragment extends BaseFragment<SongListFragPresenter> implements SongListFragContract.ViewCallback {

    public static final String TAG = "SongListFragment";

    @BindView(R.id.recycle_universal)
    RecyclerView mRecyclerView;
    @BindView(R.id.content_universal)
    ContentLoadView mContentLoadView;

    private SongListAdapter mRecycleAdapter;

    private List<SongList> mLists = new ArrayList<>();

    private int offset = 1;

    private Unbinder mUnbinder;

    public static SongListFragment newInstance(String... params) {
        SongListFragment fragment = new SongListFragment();
        fragment.setArguments(setParams(params));
        return fragment;
    }

    @Override
    protected void onLazyLoadOnce() {
        super.onLazyLoadOnce();
        if (mLists.size() == 0) {
            mPresenter.getSongList(10, offset);
        }
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_load_universal_notoolbar, container, false);
        view.setBackgroundColor(Color.WHITE);

        mUnbinder = ButterKnife.bind(this, view);

        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                GridLayoutManager manager = (GridLayoutManager) recyclerView.getLayoutManager();
                if (newState == RecyclerView.SCROLL_STATE_IDLE && manager.getChildCount() > 0) {
                    int y = manager.findLastCompletelyVisibleItemPosition();
                    if (y == mLists.size() + 1) {
                        mRecycleAdapter.getFootLoadView().postLoading();
                        mPresenter.getSongList(20, offset);
                    }
                }
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Picasso.get().resumeTag(SongListAdapter.TAG);
                } else {
                    Picasso.get().pauseTag(SongListAdapter.TAG);
                }
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
    public void showView(List<SongList> songLists) {
        if (mLists.size() == 0) {
            if (songLists == null) {
                mContentLoadView.postLoadFail();
                return;
            }
            if (songLists.size() == 0) {
                mContentLoadView.postLoadNoData();
                return;
            }
            mContentLoadView.postLoadComplete();
        } else {
            if (songLists == null) {
                mRecycleAdapter.getFootLoadView().postLoadFail();
                return;
            }
            if (songLists.size() == 0) {
                mRecycleAdapter.getFootLoadView().postLoadNoData();
                return;
            }
        }

        mLists.addAll(songLists);

        if (offset == 1) {
            mRecycleAdapter = new SongListAdapter(getContext(), mLists);
            mRecyclerView.setAdapter(mRecycleAdapter);
        } else {
            mRecycleAdapter.notifyDataSetChanged();
        }

        mRecycleAdapter.getFootLoadView().postLoadComplete();

        offset++;

        mRecycleAdapter.setContentOnClickListener(new SongListAdapter.ContentOnClickListener() {
            @Override
            public void contentOnClick(int pos) {
                if (mLists.get(pos) != null) {
                    Intent intent = new Intent(getContext(), SongAndTopListActivity.class);
                    intent.putExtra(SongAndTopListActivity.TYPE_SONG_LIST, mLists.get(pos));
                    startActivity(intent);
                }
            }
        });
        mRecycleAdapter.setHeadOnClickListener(new SongListAdapter.HeadOnClickListener() {
            @Override
            public void headOnClick(View view) {
                startActivity(new Intent(getContext(), ClassifySongListActivity.class));
            }
        });
    }

    @Override
    public SongListFragPresenter createPresenter() {
        return new SongListFragPresenter(this);
    }
}
