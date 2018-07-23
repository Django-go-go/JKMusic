package com.jkingone.jkmusic.ui.activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jkingone.common.Utils.DensityUtils;
import com.jkingone.common.Utils.ScreenUtils;
import com.jkingone.jkmusic.Constant;
import com.jkingone.jkmusic.R;
import com.jkingone.jkmusic.Utils;
import com.jkingone.jkmusic.api.ArtistApi;
import com.jkingone.jkmusic.entity.ArtistList;
import com.jkingone.jkmusic.ui.base.BaseActivity;
import com.jkingone.jkmusic.ui.fragment.ArtistListFragment;
import com.jkingone.jkmusic.ui.mvp.ArtistListPresenter;
import com.jkingone.jkmusic.ui.mvp.contract.ArtistListContract;
import com.jkingone.ui.customview.ContentLoadView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArtistListActivity extends BaseActivity<ArtistListPresenter> implements ArtistListContract.ViewCallback {

    private static final String TAG = "ArtistListActivity";

    @BindView(R.id.content_universal)
    ContentLoadView mContentLoadView;
    @BindView(R.id.recycle_universal)
    RecyclerView mRecyclerView;
    @BindView(R.id.toolbar_universal)
    Toolbar mToolbar;

    private int mArea;
    private int mSex;

    private int mOffset = 0;
    private static final int LIMIT = 30;

    private ArtistAdapter mArtistAdapter;
    private List<ArtistList> mArtistLists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_load_universal);
        ScreenUtils.setTranslucent(this);
        ButterKnife.bind(this);

        mArea = getIntent().getIntExtra(ArtistListFragment.ARTIST_AREA, ArtistApi.AREA_ALL);
        mSex = getIntent().getIntExtra(ArtistListFragment.ARTIST_SEX, ArtistApi.SEX_NONE);

        mToolbar.setTitle("歌手");
        mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int px = DensityUtils.dp2px(ArtistListActivity.this, 4);
                outRect.set(px, px, px, px);
            }
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE && recyclerView.getLayoutManager().getChildCount() > 0) {
                    mContentLoadView.postLoading();
                    mPresenter.getArtistList(mOffset, LIMIT, mArea, mSex, ArtistApi.ORDER_HOT);
                }
            }
        });

        mPresenter.getArtistList(mOffset, LIMIT, mArea, mSex, ArtistApi.ORDER_HOT);
    }

    @Override
    public ArtistListPresenter createPresenter() {
        return new ArtistListPresenter(this);
    }

    @Override
    public void showArtistList(List<ArtistList> artistLists) {

        if (mArtistLists.size() == 0) {
            if (artistLists == null) {
                mContentLoadView.postLoadFail();
                return;
            }
            if (artistLists.size() == 0) {
                mContentLoadView.postLoadNoData();
                return;
            }
        } else {
            if (artistLists == null || artistLists.size() == 0) {
                return;
            }
        }

        mContentLoadView.postLoadComplete();

        mOffset += LIMIT;

        mArtistLists.addAll(artistLists);
        if (mArtistAdapter == null) {
            mArtistAdapter = new ArtistAdapter(mArtistLists, this);
            mRecyclerView.setAdapter(mArtistAdapter);
        } else {
            mArtistAdapter.notifyDataSetChanged();
        }
    }

    class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {

        private List<ArtistList> mArtistLists;
        private Context mContext;

        ArtistAdapter(List<ArtistList> artistLists, Context context) {
            mArtistLists = artistLists;
            mContext = context;
        }

        @NonNull
        @Override
        public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ArtistViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_list_artist_classify, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
            ArtistList artistList = mArtistLists.get(position);
            if (artistList != null) {
                holder.mTextView.setText(artistList.getName());
                if (Utils.checkStringNotNull(artistList.getAvatarBig())) {
                    Picasso.get()
                            .load(artistList.getAvatarBig())
                            .resize(DensityUtils.dp2px(mContext, 64), DensityUtils.dp2px(mContext, 64))
                            .centerCrop()
                            .into(holder.mImageView);
                }
            }
        }

        @Override
        public int getItemCount() {
            return mArtistLists.size();
        }

        class ArtistViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.iv_item)
            ImageView mImageView;
            @BindView(R.id.tv_item)
            TextView mTextView;

            ArtistViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
