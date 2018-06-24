package com.jkingone.jkmusic.ui.activity;
import android.support.design.widget.NavigationView;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.jkingone.jkmusic.R;
import com.jkingone.jkmusic.ui.fragment.OkFragmentPagerAdapter;
import com.jkingone.jkmusic.ui.fragment.NetWorkFragment;
import com.jkingone.jkmusic.ui.fragment.PlaceholderFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

	private static final String TAG = "MainActivity";

	@BindView(R.id.container)
	ViewPager mViewPager;
	@BindView(R.id.tabs)
	TabLayout mTabLayout;
	@BindView(R.id.drawer_layout)
	DrawerLayout mDrawerLayout;
	@BindView(R.id.nav_view)
	NavigationView mNavigationView;
	@BindView(R.id.toolbar)
	Toolbar mToolbar;

	private MyPagerAdapter adapter;

	private Fragment[] fragment = {
			PlaceholderFragment.newInstance("~~~~~~~"),
			NetWorkFragment.newInstance("-------")};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_navigation_drawer);

		ButterKnife.bind(this);
//		mPresenter = createPresenter();
//		mPresenter.getMyUser();

		initView();
//		initDrawerLayout();

//		setPlayFragment(R.id.main_frame);
	}

//	private void setNavigationMenuLineStyle(NavigationView navigationView, @ColorInt final int color, final int height) {
//		try {
//			Field fieldByPressenter = navigationView.getClass().getDeclaredField("mPresenter");
//			fieldByPressenter.setAccessible(true);
//			NavigationMenuPresenter menuPresenter = (NavigationMenuPresenter) fieldByPressenter.get(navigationView);
//			Field fieldByMenuView = menuPresenter.getClass().getDeclaredField("mMenuView");
//			fieldByMenuView.setAccessible(true);
//			final NavigationMenuView mMenuView = (NavigationMenuView) fieldByMenuView.get(menuPresenter);
//			mMenuView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
//				@Override
//				public void onChildViewAttachedToWindow(View view) {
//					RecyclerView.ViewHolder viewHolder = mMenuView.getChildViewHolder(view);
//					if (viewHolder != null && "SeparatorViewHolder".equals(viewHolder.getClass().getSimpleName()) && viewHolder.itemView != null) {
//						if (viewHolder.itemView instanceof FrameLayout) {
//							FrameLayout frameLayout = (FrameLayout) viewHolder.itemView;
//							View line = frameLayout.getChildAt(0);
//							line.setBackgroundColor(color);
//							line.getLayoutParams().height = height;
//							line.setLayoutParams(line.getLayoutParams());
//						}
//					}
//				}
//
//				@Override
//				public void onChildViewDetachedFromWindow(View view) {
//				}
//			});
//		} catch (Throwable e) {
//			e.printStackTrace();
//		}
//	}
//
//	private void initDrawerLayout(){
//		mNavigationView.setItemIconTintList(null);
//		setNavigationMenuLineStyle(mNavigationView, Color.LTGRAY, 20);
//
////		headView = mNavigationView.getHeaderView(0);
////		headView.setOnClickListener(new View.OnClickListener() {
////			@Override
////			public void onClick(View v) {
//////				startActivityForResult(new Intent(MainActivity.this, InfoActivity.class), REQUEST_OK);
////			}
////		});
////
////		mTextView_username = (TextView) headView.findViewById(R.id.username);
////		mImageView_userimage = (ImageView) headView.findViewById(R.id.userimage);
////
////		mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
////			@Override
////			public boolean onNavigationItemSelected(@NonNull MenuItem item) {
////				mDrawerLayout.closeDrawer(Gravity.START, false);
////				switch (item.getItemId()){
////					case R.id.logout:
////						BmobUser.logOut();
////						break;
////					case R.id.change:
////						BmobUser.logOut();
////						startActivity(new Intent(MainActivity.this, LoginActivity.class));
////						break;
////					case R.id.recommend:
////						break;
////					case R.id.local:{
////						Intent intent = new Intent(MainActivity.this, LocalActivity.class);
////						Bundle bundle = new Bundle();
////						bundle.putInt("position", 0);
////						bundle.putString("title", "本地播放");
////						intent.putExtras(bundle);
////						startActivityForResult(intent, 1);
////						break;
////					}
////					case R.id.recorder:{
////						Intent intent = new Intent(MainActivity.this, LocalActivity.class);
////						Bundle bundle = new Bundle();
////						bundle.putInt("position", 1);
////						bundle.putString("title", "播放记录");
////						intent.putExtras(bundle);
////						startActivityForResult(intent, 1);
////						break;
////					}
////				}
////				return true;
////			}
////		});
//	}

//	@Override
//	public MainPresenter createPresenter() {
//		return new MainPresenter(this);
//	}


	private void initView(){

		setSupportActionBar(mToolbar);
		mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
					mDrawerLayout.closeDrawer(Gravity.START, true);
				} else {
					mDrawerLayout.openDrawer(Gravity.START, true);
				}
			}
		});
		mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				return false;
			}
		});

		adapter = new MyPagerAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(adapter);
		mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
		mTabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_ac_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onBackPressed() {
		if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
			mDrawerLayout.closeDrawer(Gravity.START);
		} else {
			super.onBackPressed();
		}
	}

	private class MyPagerAdapter extends FragmentPagerAdapter {

		MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return fragment.length;
		}

		@Override
		public Fragment getItem(int position) {
			return fragment[position];
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
//			super.destroyItem(container, position, object);
		}
	}

}