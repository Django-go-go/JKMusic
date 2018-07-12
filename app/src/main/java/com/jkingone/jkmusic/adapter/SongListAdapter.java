package com.jkingone.jkmusic.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jkingone.commonlib.Utils.DensityUtils;
import com.jkingone.commonlib.Utils.ScreenUtils;
import com.jkingone.customviewlib.LoadView;
import com.jkingone.jkmusic.R;
import com.jkingone.jkmusic.entity.SongList;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/6/22.
 */

public class SongListAdapter extends HeadAndFootRecycleAdapter {

    private static final String TAG = "SongListAdapter";

    private LayoutInflater mInflater;
    private Context mContext;
    private List<SongList> mList;

    private LoadView mLoadView;

    private static final int TYPE_CONTENT = 2;

    private int col = 2;
    private int w;
    private int h;

    public SongListAdapter(Context context, List<SongList> list) {
        mContext = context;
        mList = list;
        mInflater = LayoutInflater.from(mContext);

        w = ScreenUtils.getScreenWidth(mContext);
        h = DensityUtils.dp2px(mContext, 8) + DensityUtils.sp2px(mContext, 48);

        addHeaderView(createHeadView());
        addFooterView(createFootView());
    }

    private View createHeadView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_grid_songlist_head, null, false);
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
        return view;
    }

    private View createFootView() {
        mLoadView = new LoadView(mContext);
        mLoadView.setMinimumHeight(100);
        mLoadView.setVisibility(View.VISIBLE);
        mLoadView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, 120));
        return mLoadView;
    }

    public void startLoad() {
        mLoadView.setVisibility(View.VISIBLE);
        mLoadView.start();
    }

    public void stopLoad() {
        mLoadView.setVisibility(View.INVISIBLE);
        mLoadView.stop();
    }

    @Override
    public RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){

            case TYPE_CONTENT:
                View convertView = mInflater.inflate(R.layout.item_grid_songlist, parent, false);
                return new ContentViewHolder(convertView);

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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof ContentViewHolder){

            ContentViewHolder contentViewHolder = (ContentViewHolder) holder;
            SongList songList = mList.get(position - 1);

            contentViewHolder.mTextView.setText(songList.getTitle());

            if (songList.getPic_300() != null) {
                Picasso.get().load(songList.getPic_300())
                        .placeholder(R.drawable.music)
                        .resize(w/col, w/col)
                        .centerCrop()
                        .tag(mContext)
                        .into(contentViewHolder.mImageView);

            } else if (songList.getPic() != null){
                Picasso.get().load(songList.getPic())
                        .placeholder(R.drawable.music)
                        .resize(w/col, w/col)
                        .centerCrop()
                        .tag(mContext)
                        .into(contentViewHolder.mImageView);
            }

            contentViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mContentOnClickListener != null){
                        mContentOnClickListener.contentOnClick(position - 1);
                    }
                }
            });

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
                    if (getItemViewType(position) != TYPE_CONTENT){
                        return 2;
                    }
                    return 1;
                }
            });
        }
    }

    private ContentOnClickListener mContentOnClickListener;

    public void setContentOnClickListener(ContentOnClickListener contentOnClickListener) {
        mContentOnClickListener = contentOnClickListener;
    }

    public interface ContentOnClickListener{
        void contentOnClick(int pos);
    }

    class ContentViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_item)
        ImageView mImageView;
        @BindView(R.id.tv_item)
        TextView mTextView;

        ContentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, h));
            mImageView.setLayoutParams(new LinearLayout.LayoutParams(w / 2, w / 2));
        }
    }
}
