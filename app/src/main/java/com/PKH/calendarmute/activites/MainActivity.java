package com.PKH.calendarmute.activites;

import com.PKH.calendarmute.R;
import com.PKH.calendarmute.service.MuteService;

import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v13.app.FragmentPagerAdapter;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.view.ViewPager;
import android.support.design.widget.TabLayout;


import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
	
	public static final String TAB_CALENDARS = "Calendars";
	public static final String TAB_ACTIONS = "Actions";
	
	private static final String KEY_SAVED_CURRENT_TAB = "currentTab";
	
	public static final String ACTION_SHOW_ACTIONS = "showActions";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		int currentTab = 0;

		if (savedInstanceState != null) {
			currentTab = savedInstanceState.getInt(KEY_SAVED_CURRENT_TAB);
		}

		setContentView(R.layout.main_activity);

		// Activate/Add the Toolbar
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
		setupViewPager(viewPager);

		TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

		if (viewPager != null) {
			viewPager.setCurrentItem(currentTab);

		}

		if (tabLayout != null && viewPager != null) {
			tabLayout.setupWithViewPager(viewPager);
			viewPager.clearOnPageChangeListeners();
			viewPager.addOnPageChangeListener(new WorkaroundTabLayoutOnPageChangeListener(tabLayout));
		}
		
		// Start service
		MuteService.startIfNecessary(this);
	}

	public class WorkaroundTabLayoutOnPageChangeListener extends TabLayout.TabLayoutOnPageChangeListener {
		private final WeakReference<TabLayout> mTabLayoutRef;

		public WorkaroundTabLayoutOnPageChangeListener(TabLayout tabLayout) {
			super(tabLayout);
			this.mTabLayoutRef = new WeakReference<>(tabLayout);
		}

		@Override
		public void onPageSelected(int position) {
			super.onPageSelected(position);
			final TabLayout tabLayout = mTabLayoutRef.get();
			if (tabLayout != null) {
				final TabLayout.Tab tab = tabLayout.getTabAt(position);
				if (tab != null) {
					tab.select();
				}
			}
		}

	}

	private void setupViewPager(ViewPager viewPager) {
		ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
		adapter.addFragment(new ActionsFragment(), TAB_ACTIONS);
		adapter.addFragment(new CalendarsFragment(), TAB_CALENDARS);
		viewPager.setAdapter(adapter);
	}


	public class ViewPagerAdapter extends FragmentPagerAdapter {
		private final List<Fragment> mFragmentList = new ArrayList<>();
		private final List<String> mFragmentTitleList = new ArrayList<>();

		public ViewPagerAdapter(FragmentManager manager) { super(manager); }

		@Override
		public Fragment getItem(int position) { return mFragmentList.get(position); }

		@Override
		public int getCount() { return mFragmentList.size(); }

		public void addFragment(Fragment fragment, String title) {
			mFragmentList.add(fragment);
			mFragmentTitleList.add(title);
		}

		@Override
		public CharSequence getPageTitle(int position) { return mFragmentTitleList.get(position); }

	}

	
	@Override
	protected void onSaveInstanceState(Bundle b) {

		int position = 0;
		TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
		if (tabLayout != null) {
			position = tabLayout.getSelectedTabPosition();
		}
		b.putInt(KEY_SAVED_CURRENT_TAB, position);

		super.onSaveInstanceState(b);

	}

}
