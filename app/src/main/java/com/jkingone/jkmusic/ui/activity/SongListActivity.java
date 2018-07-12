package com.jkingone.jkmusic.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jkingone.commonlib.Utils.DensityUtils;
import com.jkingone.commonlib.Utils.ScreenUtils;
import com.jkingone.jkmusic.R;
import com.jkingone.jkmusic.adapter.HeadAndFootRecycleAdapter;
import com.jkingone.jkmusic.entity.SongInfo;
import com.jkingone.jkmusic.entity.SongList;
import com.jkingone.jkmusic.entity.TopList;
import com.jkingone.jkmusic.ui.mvp.contract.SongListContract;
import com.jkingone.jkmusic.ui.mvp.SongListPresenter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SongListActivity extends BaseActivity<SongListPresenter> implements SongListContract.ViewCallback {

    private static final String TAG = "SongListActivity";

    public static final String TYPE_SONGLIST = "songlist";
    public static final String TYPE_TOPLIST = "toplist";

    private List<SongInfo> mMp3Infos = new ArrayList<>();
    private TopList mTopList;
    private SongList mSongList;

    private SongAdapter mAdapter;


    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.frame_background)
    View mViewBackground;
    @BindView(R.id.iv_coverImage)
    ImageView mImageViewCover;
    @BindView(R.id.tv_desc)
    TextView mTextView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recycle_songinfo)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songlist);
        ScreenUtils.setTranslucent(this);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();

        if (intent != null) {
            mSongList = intent.getParcelableExtra(TYPE_SONGLIST);
            if (mSongList != null) {
                initSongList();
            }

            mTopList = intent.getParcelableExtra(TYPE_TOPLIST);
            if (mTopList != null){
                initTopList();
            }
        }
    }

    @Override
    public SongListPresenter createPresenter() {
        return new SongListPresenter(this);
    }

    private void initTopList() {
        mPresenter.getSongFromTopList(Integer.parseInt(mTopList.getType()));
        Picasso.get()
                .load(mTopList.getPic_s192())
                .resize(ScreenUtils.getScreenWidth(this), DensityUtils.dp2px(this, 256))
                .centerCrop()
                .into(mImageViewCover);
        mTextView.setText(mTopList.getComment());
    }

    private void initSongList() {
        mPresenter.getSongFromSongList(mSongList.getListid());
        Picasso.get()
                .load(mSongList.getPic_300())
                .resize(ScreenUtils.getScreenWidth(this), DensityUtils.dp2px(this, 256))
                .centerCrop()
                .into(mImageViewCover);
        mTextView.setText(mSongList.getTitle());
    }

    @Override
    public void showView(List<SongInfo> songInfos) {
        mRecyclerView.setAdapter(new SongAdapter(this, songInfos));
    }

    class SongAdapter extends HeadAndFootRecycleAdapter {
        private static final int TYPE_CONTENT = 2;

        private Context mContext;
        private LayoutInflater mInflater;
        private List<SongInfo> mSongs;

        public SongAdapter(Context context, List<SongInfo> songs) {
            mContext = context;
            mSongs = songs;
            mInflater = LayoutInflater.from(mContext);
            addHeaderView(createHeadView());
        }

        private View createHeadView() {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_list_songinfo_head, null, false);
            view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mHeadOnClickListener != null) {
                        mHeadOnClickListener.headOnClick(v);
                    }
                }
            });
            View childView = view.findViewById(R.id.tv_collect);
            childView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            return view;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof VH) {
                VH vh = (VH) holder;
                position -= 1;
                SongInfo songInfo = mSongs.get(position);
                vh.tv_singer.setText(songInfo.getArtist());
                vh.tv_songName.setText(songInfo.getTitle());
                vh.tv_position.setText(String.valueOf(position + 1));
                vh.iv_action.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
                vh.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {

                case TYPE_CONTENT:
                    return new VH(mInflater.inflate(R.layout.item_list_songinfo, parent, false));

                default:
                    throw new IllegalArgumentException("no Type");
            }
        }

        @Override
        public int getItemContentCount() {
            return mSongs.size();
        }

        @Override
        public int getItemContentViewType(int position) {
            return TYPE_CONTENT;
        }

        class VH extends RecyclerView.ViewHolder {
            @BindView(R.id.tv_songName)
            TextView tv_songName;
            @BindView(R.id.tv_singer)
            TextView tv_singer;
            @BindView(R.id.iv_action)
            ImageView iv_action;
            @BindView(R.id.tv_pos)
            TextView tv_position;

            VH(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                tv_position.setVisibility(View.VISIBLE);
                tv_singer.setVisibility(View.VISIBLE);
                tv_songName.setVisibility(View.VISIBLE);
                iv_action.setVisibility(View.VISIBLE);
            }
        }
    }

}
