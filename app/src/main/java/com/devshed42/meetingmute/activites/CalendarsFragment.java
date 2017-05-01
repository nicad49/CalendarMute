package com.devshed42.meetingmute.activites;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.devshed42.meetingmute.PreferencesManager;
import com.devshed42.meetingmute.R;
import com.devshed42.meetingmute.calendar.CalendarProvider;
import com.devshed42.meetingmute.models.Calendar;
import com.devshed42.meetingmute.service.MuteService;
import com.devshed42.meetingmute.views.CalendarAdapter;

//import android.app.Fragment;

public class CalendarsFragment extends Fragment {
	
	private static final String LOG_TAG = CalendarsFragment.class.getSimpleName();
	private ListView lstAgendas;

	private static final int CALENDAR_PERMISSION_REQUEST = 1;
	private static final int CALENDAR_PERMISSION_REQUEST_FORCE_REFRESH = 2;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View res = inflater.inflate(R.layout.layout_lst_agendas, container, false);
		
		lstAgendas = (ListView) res.findViewById(R.id.lst_calendars);
		
		// Fill calendars
		refreshCalendars(false);
		
		return res;
	}

	public void refreshCalendars(boolean forceRefresh) {

		Activity activity = getActivity();
		if(activity == null) {
			Log.d(LOG_TAG, "Activity is null");
			return;
		}

		Log.d(LOG_TAG, "Checking Permissions");
		if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
			// Not showing explanations, this should be very obvious
			Log.d(LOG_TAG, "Requesting Permissions");
			requestPermissions(new String[] {Manifest.permission.READ_CALENDAR}, CALENDAR_PERMISSION_REQUEST);
			//FragmentCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALENDAR}, CALENDAR_PERMISSION_REQUEST);
		}
		else {
			Log.d(LOG_TAG, "Permissions already existing, refreshing Calendars list");
			refreshCalendarsWithPermission(forceRefresh);
		}
	}
	
	public void refreshCalendarsWithPermission(boolean forceRefresh) {
		Calendar[] savedCalendars;
		if(!forceRefresh && (savedCalendars = CalendarProvider.getCachedCalendars()) != null)
			fillCalendars(savedCalendars);
		else
			new CalendarGetter().execute(true);
	}
	
	private class CalendarGetter extends AsyncTask<Boolean, Void, Calendar[]> {
		@Override
		protected Calendar[] doInBackground(Boolean... params) {
			// Fetch calendars
			Activity a = getActivity();
			if(a == null) // Fragment already detached
				return null;
			
			CalendarProvider provider = new CalendarProvider(a);

			return provider.listCalendars(params[0]);
		}
		
		@Override
		protected void onPostExecute(Calendar[] result) {
			fillCalendars(result);
		}
	}
	
	private void fillCalendars(Calendar[] calendars) {
		
		Activity a = getActivity();
		if(a == null) // Fragment already detached
			return;
		
		if(calendars == null) {
			Toast.makeText(a, R.string.erreur_listing_agendas, Toast.LENGTH_LONG).show();
			return;
		}
		
		CalendarAdapter adapter = new CalendarAdapter(getActivity(), calendars);
		lstAgendas.setAdapter(adapter);
		
		// Restore checked items in the list
		for(int i=0, max = lstAgendas.getCount(); i<max; i++) {
			lstAgendas.setItemChecked(i, adapter.getItem(i).isChecked());
		}
		
		adapter.setItemCheckedChangedListener(new CalendarAdapter.ItemCheckedChangedListener() {
			@Override
			public void onItemCheckedChanged() {
				
				Activity a = getActivity();
				PreferencesManager.saveCalendars(a, lstAgendas.getCheckedItemIds());
				
				// Remove cached calendars (now invalid)
				CalendarProvider.invalidateCalendars();
				
				// Launch service to check if there are events now
				MuteService.startIfNecessary(a);
			}
		});
	}

	public CalendarsFragment() {
		// Required Empty public constructor
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.menu_refresh_calendars:
			refreshCalendars(true);
			return true;
		default:
			return false;
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.main, menu);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch(requestCode) {
			case CALENDAR_PERMISSION_REQUEST:
			case CALENDAR_PERMISSION_REQUEST_FORCE_REFRESH:

				if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					refreshCalendarsWithPermission(requestCode == CALENDAR_PERMISSION_REQUEST_FORCE_REFRESH);
				}
				else {
					// The user wants to use CalendarMute without calendar (not really a genius)
					Activity activity = getActivity();
					if(activity == null) {
						return;
					}

					Toast.makeText(activity, "Permission Denied",
							Toast.LENGTH_LONG).show();
				}
				break;
		}
	}
}


