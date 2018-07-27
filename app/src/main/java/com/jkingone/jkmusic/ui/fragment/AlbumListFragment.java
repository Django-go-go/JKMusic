package com.jkingone.jkmusic.ui.fragment;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.jkingone.common.utils.ScreenUtils;
import com.jkingone.jkmusic.R;
import com.jkingone.jkmusic.Utils;
import com.jkingone.jkmusic.entity.AlbumList;
import com.jkingone.jkmusic.ui.activity.AlbumAndArtistActivity;
import com.jkingone.jkmusic.ui.base.BaseFragment;
import com.jkingone.jkmusic.ui.mvp.AlbumListPresenter;
import com.jkingone.jkmusic.ui.mvp.contract.AlbumListContract;
import com.jkingone.ui.widget.ContentLoadView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlbumListFragment extends BaseFragment<AlbumListPresenter> implements AlbumListContract.ViewCallback {

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

    private static final String TAG = "AlbumListFragment";

    @Override
    protected void onLazyLoadOnce() {
        mPresenter.getAlbumList(offset, LIMIT);
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

                    Picasso.get().resumeTag(TAG);

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
                        mPresenter.getAlbumList(offset, LIMIT);
                    }
                } else {
                    Picasso.get().pauseTag(TAG);
                }
            }
        });
        mContentLoadView.setLoadRetryListener(new ContentLoadView.LoadRetryListener() {
            @Override
            public void onRetry() {
                mPresenter.getAlbumList(offset, LIMIT);
            }
        });
        return view;
    }

    @Override
    public AlbumListPresenter createPresenter() {
        return new AlbumListPresenter(this);
    }

    @Override
    public void showAlbumList(List<AlbumList> albumLists) {
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
            if (Utils.checkStringNotNull(albumList.getPicRadio())) {
                Picasso.get().
                        load(albumList.getPicRadio())
                        .resize(mWidth, mWidth)
                        .centerCrop()
                        .tag(TAG)
                        .into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                holder.mImageView.setImageBitmap(bitmap);
                                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                                    @Override
                                    public void onGenerated(@NonNull Palette palette) {
                                        int color = palette.getLightMutedColor(Color.LTGRAY);
                                        ((CardView)holder.itemView).setCardBackgroundColor(color);
                                    }
                                });
                            }

                            @Override
                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                ((CardView)holder.itemView).setCardBackgroundColor(Color.LTGRAY);
                                holder.mImageView.setImageResource(R.drawable.music_large);
                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });
            }
            holder.mTextViewTitle.setText(albumList.getTitle());
            holder.mTextViewAuthor.setText(albumList.getAuthor());
            holder.mTextViewPublish.setText(albumList.getPublishTime());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AlbumListFragment.this.getContext(), AlbumAndArtistActivity.class);
                    intent.putExtra(AlbumAndArtistActivity.TYPE_ALBUM, mAlbumLists.get(position));
                    startActivity(intent);
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
