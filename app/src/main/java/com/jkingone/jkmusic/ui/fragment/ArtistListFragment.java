package com.jkingone.jkmusic.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jkingone.common.Utils.DensityUtils;
import com.jkingone.jkmusic.R;
import com.jkingone.jkmusic.adapter.HeadAndFootRecycleAdapter;
import com.jkingone.jkmusic.api.ArtistApi;
import com.jkingone.jkmusic.entity.ArtistList;
import com.jkingone.jkmusic.ui.activity.ArtistListActivity;
import com.jkingone.jkmusic.ui.base.BaseFragment;
import com.jkingone.jkmusic.ui.mvp.ArtistListPresenter;
import com.jkingone.jkmusic.ui.mvp.contract.ArtistListContract;
import com.jkingone.ui.customview.ContentLoadView;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ArtistListFragment extends BaseFragment<ArtistListPresenter> implements ArtistListContract.ViewCallback {

    public static ArtistListFragment newInstance(String... params) {
        ArtistListFragment fragment = new ArtistListFragment();
        fragment.setArguments(setParams(params));
        return fragment;
    }

    public static final String ARTIST_AREA = "area";
    public static final String ARTIST_SEX = "sex";

    private static String[] sTitles = {
            "华语男歌手", "华语女歌手", "华语组合",
            "欧美男歌手", "欧美女歌手", "欧美组合",
            "日本男歌手", "日本女歌手", "日本组合",
            "棒子男歌手", "棒子女歌手", "棒子组合",
            "其他男歌手", "其他女歌手", "其他组合",
    };

    private static Integer[] sAreas = {
            ArtistApi.AREA_CHINA, ArtistApi.AREA_CHINA, ArtistApi.AREA_CHINA,
            ArtistApi.AREA_EU, ArtistApi.AREA_EU, ArtistApi.AREA_EU,
            ArtistApi.AREA_JAPAN, ArtistApi.AREA_JAPAN, ArtistApi.AREA_JAPAN,
            ArtistApi.AREA_KOREA, ArtistApi.AREA_KOREA, ArtistApi.AREA_KOREA,
            ArtistApi.AREA_OTHER, ArtistApi.AREA_OTHER, ArtistApi.AREA_OTHER
    };

    private static int[] sSexs = {
            ArtistApi.SEX_MALE, ArtistApi.SEX_FEMALE, ArtistApi.SEX_GROUP,
            ArtistApi.SEX_MALE, ArtistApi.SEX_FEMALE, ArtistApi.SEX_GROUP,
            ArtistApi.SEX_MALE, ArtistApi.SEX_FEMALE, ArtistApi.SEX_GROUP,
            ArtistApi.SEX_MALE, ArtistApi.SEX_FEMALE, ArtistApi.SEX_GROUP,
            ArtistApi.SEX_MALE, ArtistApi.SEX_FEMALE, ArtistApi.SEX_GROUP,
    };


    private TextView mTextViewHot;
    private RecyclerView mRecyclerViewHot;
    @BindView(R.id.recycle_universal)
    RecyclerView mRecyclerViewContent;
    @BindView(R.id.content_universal)
    ContentLoadView mContentLoadView;

    private Unbinder mUnbinder;

    private ContentAdapter mContentAdapter;

    @Override
    public ArtistListPresenter createPresenter() {
        return new ArtistListPresenter(this);
    }

    @Override
    protected void onLazyLoadOnce() {
        mPresenter.getArtistList(0, 20, ArtistApi.AREA_ALL, ArtistApi.SEX_NONE, ArtistApi.ORDER_HOT);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_load_universal_notoolbar, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mRecyclerViewContent.setBackgroundColor(Color.parseColor("#efe6e9"));
        mRecyclerViewContent.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerViewContent.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int pos = parent.getChildAdapterPosition(view);
                if (pos % 3 == 0) {
                    outRect.bottom = DensityUtils.dp2px(ArtistListFragment.this.getContext(), 8);
                }
            }
        });

        mContentAdapter = new ContentAdapter(getContext());
        mRecyclerViewContent.setAdapter(mContentAdapter);

        initHeaderView();

        mContentLoadView.postLoadComplete();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void showArtistList(List<ArtistList> artistLists) {
        if (artistLists != null) {
            mRecyclerViewHot.setAdapter(new HotAdapter(getContext(), artistLists));
        }
    }

    private void initHeaderView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_list_artist_head, mRecyclerViewContent, false);
        mRecyclerViewHot = view.findViewById(R.id.recycle_hot);
        mRecyclerViewHot.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mTextViewHot = view.findViewById(R.id.tv_hot);
        mTextViewHot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ArtistListFragment.this.getContext(), ArtistListActivity.class);
                intent.putExtra(ARTIST_AREA, ArtistApi.AREA_ALL);
                intent.putExtra(ARTIST_SEX, ArtistApi.SEX_NONE);
                ArtistListFragment.this.startActivity(intent);
            }
        });
        mContentAdapter.addHeaderView(view);
    }

    class ContentAdapter extends HeadAndFootRecycleAdapter {

        private static final int TYPE_CONTENT = 2;

        private Context mContext;

        ContentAdapter(Context context) {
            mContext = context;
        }

        @Override
        public RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent, int viewType) {
            switch (viewType){

                case TYPE_CONTENT:
                    View convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_artist, parent, false);
                    return new ContentViewHolder(convertView);

                default:
                    throw new IllegalArgumentException("no Type");
            }
        }

        @Override
        public int getItemContentCount() {
            return sTitles.length;
        }

        @Override
        public int getItemContentViewType(int position) {
            return TYPE_CONTENT;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            if (holder instanceof ContentViewHolder) {
                ContentViewHolder contentViewHolder = (ContentViewHolder) holder;
                contentViewHolder.mTextView.setText(sTitles[position - 1]);
                contentViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, ArtistListActivity.class);
                        intent.putExtra(ARTIST_AREA, sAreas[position - 1]);
                        intent.putExtra(ARTIST_SEX, sSexs[position - 1]);
                        ArtistListFragment.this.startActivity(intent);
                    }
                });
            }
        }

        class ContentViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.tv_item)
            TextView mTextView;

            ContentViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }

    static class HotAdapter extends RecyclerView.Adapter<HotAdapter.HotViewHolder> {

        private int h;
        private int w;

        private Context mContext;
        private List<ArtistList> mData;

        HotAdapter(Context context, List<ArtistList> data) {
            mContext = context;
            mData = data;
            w = DensityUtils.dp2px(context, 96 + 8);
            h = DensityUtils.dp2px(mContext, 8 + 8) + DensityUtils.sp2px(mContext, 14);
        }

        @NonNull
        @Override
        public HotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new HotViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_universal, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull HotViewHolder holder, int position) {
            ArtistList artistList = mData.get(position);
            holder.mTextView.setText(artistList.getName());
            Picasso.get()
                    .load(artistList.getAvatarBig())
                    .centerCrop()
                    .resize(w, w)
                    .into(holder.mImageView);
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        class HotViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.iv_item)
            ImageView mImageView;
            @BindView(R.id.tv_item)
            TextView mTextView;

            HotViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                mTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, h));
                mTextView.setGravity(Gravity.CENTER);
                mTextView.setSingleLine(true);
                itemView.setLayoutParams(new LinearLayout.LayoutParams(w, w + h));
            }
        }
    }
}
