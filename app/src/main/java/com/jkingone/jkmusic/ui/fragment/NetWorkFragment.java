package com.jkingone.jkmusic.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jkingone.customviewlib.PagerSlidingTabStrip;
import com.jkingone.jkmusic.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class NetWorkFragment extends LazyFragment {
    private static final String TAG = "NetWorkFragment";
    
    private Unbinder mUnbinder;

    @BindView(R.id.pager_net)
    ViewPager pager;
    @BindView(R.id.pagerStrip)
    PagerSlidingTabStrip mTabStrip;

    private MyPagerAdapter adapter;

    public static NetWorkFragment newInstance(String... params) {
        NetWorkFragment fragment = new NetWorkFragment();
        fragment.setArguments(setParams(params));
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_network, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onLazyLoadOnce() {
        super.onLazyLoadOnce();
        adapter = new MyPagerAdapter(getChildFragmentManager()/*, false*/);
        pager.setAdapter(adapter);
        mTabStrip.setViewPager(pager);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    private static class MyPagerAdapter extends FragmentPagerAdapter {

        private Fragment[] mFragments = {
                ArtistFragment.newInstance("ArtistFragment"),
                AlbumListFragment.newInstance("AlbumListFragment"),
                SongListFragment.newInstance("SongListFragment"),
                TopListFragment.newInstance("TopListFragment")};
        private String[] titles = {"歌手", "唱片", "歌单", "排行榜"};

        MyPagerAdapter(FragmentManager fm/*, boolean isVisible*/) {
            super(fm/*, isVisible*/);
        }

        @Override
        public int getCount() {
            return mFragments.length;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments[position];
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//            super.destroyItem(container, position, object);
        }

    }

}
