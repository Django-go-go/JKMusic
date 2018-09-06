package com.jkingone.jkmusic.ui.fragment;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.jkingone.jkmusic.GlideApp;
import com.jkingone.jkmusic.ui.base.LazyFragment;
import com.jkingone.jkmusic.viewmodels.AlbumListViewModel;
import com.jkingone.utils.ScreenUtils;
import com.jkingone.jkmusic.R;
import com.jkingone.jkmusic.entity.AlbumList;
import com.jkingone.ui.ContentLoadView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlbumListFragment extends LazyFragment {

    public static AlbumListFragment newInstance(String... params) {
        AlbumListFragment fragment = new AlbumListFragment();
        fragment.setArguments(setParams(params));
        return fragment;
    }

    @BindView(R.id.recycle_common)
    RecyclerView mRecyclerView;
    @BindView(R.id.content_common)
    ContentLoadView mContentLoadView;

    private List<AlbumList> mAlbumLists = new ArrayList<>();
    private AlbumAdapter mAlbumAdapter;

    private int offset = 0;
    private static final int LIMIT = 30;

    private AlbumListViewModel mAlbumListViewModel;
    private Observer<List<AlbumList>> mAlbumListObserver = new Observer<List<AlbumList>>() {
        @Override
        public void onChanged(List<AlbumList> albumLists) {
            if (albumLists == null) {
                mContentLoadView.postLoadFail();
                return;
            }

            mContentLoadView.postLoadComplete();

            mAlbumLists.addAll(albumLists);
            if (mAlbumAdapter == null) {
                mAlbumAdapter = new AlbumAdapter(getContext());
                mRecyclerView.setAdapter(mAlbumAdapter);
            } else {
                mAlbumAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAlbumListViewModel = ViewModelProviders.of(this).get(AlbumListViewModel.class);
        mAlbumListViewModel.getAlbumListLiveData().observe(this, mAlbumListObserver);
    }

    @Override
    protected void onLazyLoadOnce() {
        mAlbumListViewModel.getAlbumList(offset, LIMIT);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.common_root_none, container, false);
        ButterKnife.bind(this, view);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.bottom = 6;
                outRect.left = 6;
                outRect.right = 6;
                outRect.top = 6;
            }
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                StaggeredGridLayoutManager manager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int[] pos = new int[manager.getSpanCount()];
                    manager.findLastVisibleItemPositions(pos);
                    int max = pos[0];
                    for (int value : pos) {
                        if (value > max) {
                            max = value;
                        }
                    }
                    if (max == manager.getItemCount() - 1 && manager.getChildCount() > 0) {
                        offset += LIMIT;
                        mAlbumListViewModel.getAlbumList(offset, LIMIT);
                    }
                }
            }
        });
        mContentLoadView.setLoadRetryListener(() -> mAlbumListViewModel.getAlbumList(offset, LIMIT));
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAlbumListViewModel.getAlbumListLiveData().removeObserver(mAlbumListObserver);
    }

    class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {

        private Context mContext;
        private int mWidth;

        AlbumAdapter(Context context) {
            mContext = context;
            mWidth = ScreenUtils.getScreenWidth(context) / 3;
        }

        @NonNull
        @Override
        public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new AlbumViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_list_albumlist, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull final AlbumViewHolder holder, final int position) {
            AlbumList albumList = mAlbumLists.get(position);

            GlideApp.with(AlbumListFragment.this)
                    .asBitmap()
                    .load(albumList.getPicRadio())
                    .centerCrop()
                    .override(mWidth, mWidth)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            holder.mImageView.setImageBitmap(resource);
                            Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(@NonNull Palette palette) {
                                    int color = palette.getLightMutedColor(Color.LTGRAY);
                                    ((CardView) holder.itemView).setCardBackgroundColor(color);
                                }
                            });
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            ((CardView) holder.itemView).setCardBackgroundColor(Color.LTGRAY);
                        }
                    });
            holder.mTextViewTitle.setText(albumList.getTitle());
            holder.mTextViewAuthor.setText(albumList.getAuthor());
            holder.mTextViewPublish.setText(albumList.getPublishTime());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(AlbumListFragment.this.getContext(), AlbumAndArtistActivity.class);
//                    intent.putExtra(AlbumAndArtistActivity.TYPE_ALBUM, mAlbumLists.get(position));
//                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mAlbumLists.size();
        }

        class AlbumViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.iv_album)
            ImageView mImageView;
            @BindView(R.id.tv_publish)
            TextView mTextViewPublish;
            @BindView(R.id.tv_title)
            TextView mTextViewTitle;
            @BindView(R.id.tv_author)
            TextView mTextViewAuthor;
            @BindView(R.id.frame_item)
            View mView;

            AlbumViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                mView.setLayoutParams(new LinearLayout.LayoutParams(mWidth, mWidth));
                itemView.setLayoutParams(new RecyclerView.LayoutParams(mWidth, RecyclerView.LayoutParams.WRAP_CONTENT));
            }
        }
    }
}
