package com.devshed42.meetingmute.activites;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.devshed42.meetingmute.R;
import com.devshed42.meetingmute.service.MuteService;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

//public class MainActivity extends Activity {
	public class MainActivity extends AppCompatActivity {
	
	public static final String TAG_TAB_CALENDARS = "calendars";
	public static final String TAG_TAB_ACTIONS = "actions";
	
	private static final String KEY_SAVED_CURRENT_TAB = "currentTab";
	
	public static final String ACTION_SHOW_ACTIONS = "showActions";

	ViewPager viewPager;
	TabLayout tabLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		// Activate/Add the Toolbar

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
		setupViewPager(viewPager);

		TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
		tabLayout.setupWithViewPager(viewPager);

		viewPager.clearOnPageChangeListeners();
		viewPager.addOnPageChangeListener(new WorkaroundTabLayoutOnPageChangeListener(tabLayout));

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
		ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
		adapter.addFragment(new ActionsFragment(), "Actions");
		adapter.addFragment(new CalendarsFragment(), "Calendars");
		viewPager.setAdapter(adapter);
	}

	public class ViewPagerAdapter extends FragmentPagerAdapter {
		private final List<Fragment> mFragmentList = new ArrayList<>();
		private final List<String> mFragmentTitleList = new ArrayList<>();

		public ViewPagerAdapter(FragmentManager manager) {
			super(manager);
		}

		@Override
		public Fragment getItem(int position) {
			return mFragmentList.get(position);
		}

		@Override
		public int getCount() {
			return mFragmentList.size();
		}

		public void addFragment(Fragment fragment, String title) {
			mFragmentList.add(fragment);
			mFragmentTitleList.add(title);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return mFragmentTitleList.get(position);
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle b) {
		super.onSaveInstanceState(b);

		b.putInt(KEY_SAVED_CURRENT_TAB, getSupportActionBar().getSelectedNavigationIndex());
	}

}
