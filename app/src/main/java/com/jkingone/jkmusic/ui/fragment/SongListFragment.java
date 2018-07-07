package com.jkingone.jkmusic.ui.fragment;

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

import com.jkingone.jkmusic.R;
import com.jkingone.jkmusic.adapter.SongListAdapter;
import com.jkingone.jkmusic.data.entity.SongList;
import com.jkingone.jkmusic.ui.activity.ClassifySongListActivity;
import com.jkingone.jkmusic.ui.activity.SongListActivity;
import com.jkingone.jkmusic.ui.mvp.contract.SongListFragContract;
import com.jkingone.jkmusic.ui.mvp.SongListFragPresenter;

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

    private SongListAdapter mRecycleAdapter;

    private List<SongList> mLists;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLists = new ArrayList<>();
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.recycle_universal, container, false);

        mUnbinder = ButterKnife.bind(this, view);

        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.right = 8;
                outRect.top = 8;
                outRect.left = 8;
                outRect.bottom = 8;
            }
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                GridLayoutManager manager = (GridLayoutManager) recyclerView.getLayoutManager();
                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    int y = manager.findLastCompletelyVisibleItemPosition();
                    if (y == mLists.size() + 1) {
                        mRecycleAdapter.startLoad();
                        mPresenter.getSongList(20, offset);
                    }
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
        mLists.addAll(songLists);

        if (offset == 1){
            mRecycleAdapter = new SongListAdapter(getContext(), mLists);
            mRecyclerView.setAdapter(mRecycleAdapter);
            mRecycleAdapter.stopLoad();
        }else {
            mRecycleAdapter.stopLoad();
            mRecycleAdapter.notifyDataSetChanged();
        }

        offset++;

        mRecycleAdapter.setContentOnClickListener(new SongListAdapter.ContentOnClickListener() {
            @Override
            public void contentOnClick(int pos) {
                if (mLists.get(pos) != null){
                    Intent intent = new Intent(getContext(), SongListActivity.class);
                    intent.putExtra(SongListActivity.TYPE_SONGLIST, mLists.get(pos));
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
