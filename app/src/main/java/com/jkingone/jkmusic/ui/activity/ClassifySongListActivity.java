package com.jkingone.jkmusic.ui.activity;

import android.animation.Animator;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jkingone.jkmusic.viewmodels.SongListViewModel;
import com.jkingone.utils.DensityUtils;
import com.jkingone.utils.ScreenUtils;
import com.jkingone.jkmusic.GlideApp;
import com.jkingone.jkmusic.R;
import com.jkingone.jkmusic.ui.base.BaseActivity;
import com.jkingone.ui.ContentLoadView;
import com.jkingone.ui.WaveView;
import com.jkingone.jkmusic.entity.SongList;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ClassifySongListActivity extends BaseActivity {

    @BindView(R.id.recycle_common)
    RecyclerView mRecyclerView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.recycle_choose)
    RecyclerView mRecyclerView_choose;
    @BindView(R.id.recycle_classify)
    WaveView mClassifyView;
    @BindView(R.id.iv_back)
    ImageView mImageView_back;
    @BindView(R.id.content_common)
    ContentLoadView mContentLoadView;

    private String tag = "华语";

    private SongListViewModel mSongListViewModel;
    private Observer<List<SongList>> mSongListObserver = new Observer<List<SongList>>() {
        @Override
        public void onChanged(@Nullable List<SongList> songLists) {
            if (songLists == null) {
                mContentLoadView.postLoadFail();
                return;
            } else {
                mContentLoadView.postLoadComplete();
            }
            mRecyclerView.setAdapter(new ClassifySongListAdapter(ClassifySongListActivity.this, songLists));
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenUtils.setTranslucent(this);
        setContentView(R.layout.activity_classify_songlist);
        ButterKnife.bind(this);
        initView();
        mContentLoadView.postLoading();
        mSongListViewModel = ViewModelProviders.of(this).get(SongListViewModel.class);
        mSongListViewModel.getTagSongListLiveData().observe(this, mSongListObserver);
        mSongListViewModel.getTagSongList(tag);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSongListViewModel.getTagSongListLiveData().removeObserver(mSongListObserver);
    }

    private void initView(){
        mToolbar.setTitle("分类歌单•" + tag);
        mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_classify:
                        mClassifyView.setVisibility(View.VISIBLE);
                        mClassifyView.start();
                        break;
                }
                return true;
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mImageView_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClassifyView.startReverse();
            }
        });

        mRecyclerView_choose.setLayoutManager(new GridLayoutManager(this, 4));
        mRecyclerView_choose.setAdapter(new ChooseAdapter(this));
        mRecyclerView_choose.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.left = 4;
                outRect.right = 4;
                outRect.top = 4;
                outRect.bottom = 4;
            }
        });

        mClassifyView.post(new Runnable() {
            @Override
            public void run() {
                mClassifyView.setCircleCenter(mToolbar.getRight() - mToolbar.getPaddingEnd(), mToolbar.getPaddingTop());
                mClassifyView.setEndListener(new WaveView.WaveEndListener() {
                    @Override
                    public void onWaveEnd(Animator animation, boolean isReverse) {
                        if (isReverse) {
                            mClassifyView.setVisibility(View.INVISIBLE);
                        } else {
                            mClassifyView.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });

        mContentLoadView.setLoadRetryListener(new ContentLoadView.LoadRetryListener() {
            @Override
            public void onRetry() {
                mContentLoadView.postLoading();
                mSongListViewModel.getTagSongList(tag);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ac_classify, menu);
        return super.onCreateOptionsMenu(menu);
    }

    class ClassifySongListAdapter extends RecyclerView.Adapter<ClassifySongListAdapter.VH> {
        private LayoutInflater mInflater;
        private Context mContext;
        private List<SongList> mList;

        ClassifySongListAdapter(Context context, List<SongList> list) {
            mContext = context;
            mList = list;
            mInflater = LayoutInflater.from(mContext);
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH(mInflater.inflate(R.layout.item_list_classify, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, final int position) {

            final SongList songList = mList.get(position);

            if (songList != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            Intent intent = new Intent(mContext, DetailActivity.class);
                            intent.putExtra(DetailActivity.TYPE_SONG_LIST, mList.get(position));
                            startActivity(intent);
                    }
                });
                holder.textView_name.setText("标签: " + songList.getTag());
                holder.textView_title.setText(songList.getTitle());
                holder.textView_desc.setText(songList.getDesc());
                GlideApp.with(ClassifySongListActivity.this)
                        .asBitmap()
                        .load(songList.getPic300())
                        .override(DensityUtils.dp2px(mContext, 128))
                        .into(holder.imageView);
            }


        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        class VH extends RecyclerView.ViewHolder {
            @BindView(R.id.iv_item)
            ImageView imageView;
            @BindView(R.id.tv_title_item)
            TextView textView_title;
            @BindView(R.id.tv_name_item)
            TextView textView_name;
            @BindView(R.id.tv_desc_item)
            TextView textView_desc;

            VH(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }

    class ChooseAdapter extends RecyclerView.Adapter<ChooseAdapter.VH> {

        private Context mContext;
        private List<String> mAllList;

        ChooseAdapter(Context context) {
            mContext = context;
            mAllList = new ArrayList<>();
            mAllList.addAll(getTagForTheme());
            mAllList.addAll(getTagForLanguage());
            mAllList.addAll(getTagForMood());
            mAllList.addAll(getTagForPlace());
            mAllList.addAll(getTagForStyle());
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH(createTextView());
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, final int position) {
            TextView textView = (TextView) holder.itemView;
            textView.setText(mAllList.get(position));
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tag = mAllList.get(position);
                    mSongListViewModel.getTagSongList(tag);
                    mToolbar.setTitle("分类歌单•" + tag);
                    mClassifyView.startReverse();
                    mContentLoadView.postLoading();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mAllList.size();
        }

        private TextView createTextView() {
            TextView textView = new TextView(mContext);
            textView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT));
            textView.setTextColor(Color.BLACK);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(16);
            textView.setPadding(20, 20, 20, 20);
            textView.setBackgroundResource(R.drawable.selector_line_tag);
            return textView;
        }

        class VH extends RecyclerView.ViewHolder {

            VH(View itemView) {
                super(itemView);

            }
        }
    }

    //语种 风格 情感 场景 主题

    public static List<String> getTagForTheme(){
        List<String> tags = new ArrayList<>();
        tags.add("经典");
        tags.add("翻唱");tags.add("榜单");tags.add("现场");
        tags.add("KTV");tags.add("DJ");tags.add("网络歌曲");
        tags.add("器乐");
        return tags;
    }

    public static List<String> getTagForPlace(){
        List<String> tags = new ArrayList<>();

        tags.add("运动");tags.add("驾驶");
        tags.add("学习");tags.add("工作");
        tags.add("清晨");tags.add("夜晚");tags.add("午后");
        tags.add("游戏");tags.add("旅行");tags.add("散步");
        tags.add("酒吧");tags.add("夜店");
        tags.add("咖啡厅");tags.add("地铁");
        tags.add("校园");tags.add("婚礼");
        tags.add("约会");tags.add("休息");
        return tags;
    }

    public static List<String> getTagForMood(){
        List<String> tags = new ArrayList<>();

        tags.add("快乐");tags.add("美好");
        tags.add("安静");tags.add("伤感");tags.add("寂寞");
        tags.add("思念");tags.add("孤独");tags.add("怀旧");
        tags.add("悲伤");tags.add("感动");tags.add("治愈");
        tags.add("放松");tags.add("清新");tags.add("浪漫");
        tags.add("兴奋");tags.add("性感");
        tags.add("励志");
        return tags;
    }

    public static List<String> getTagForStyle(){
        List<String> tags = new ArrayList<>();
        tags.add("流行");
        tags.add("摇滚");tags.add("民谣");
        tags.add("电子");tags.add("影视原声");
        tags.add("ACG");tags.add("轻音乐");tags.add("新世纪");
        tags.add("爵士");tags.add("古典");
        tags.add("乡村");tags.add("说唱");tags.add("世界音乐");
        tags.add("古风");tags.add("儿歌");
        tags.add("朋克");tags.add("布鲁斯");
        tags.add("金属");tags.add("雷鬼");tags.add("英伦");
        tags.add("民族");tags.add("后摇");tags.add("拉丁");

        return tags;
    }

    public static List<String> getTagForLanguage(){
        List<String> tags = new ArrayList<>();
        tags.add("华语");tags.add("欧美");tags.add("粤语");
        tags.add("日语");tags.add("韩语");
        tags.add("纯音乐");tags.add("小语种");
        return tags;
    }
}
