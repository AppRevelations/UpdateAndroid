package com.apprevelations.updateandroid;

import java.io.DataOutputStream;
import java.io.IOException;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	private static String APP_PACKAGE_NAME = "com.apprevelations.updateandroid";
	private static String PLAY_STORE_LINK = "market://details?id="
			+ APP_PACKAGE_NAME;

	private Process suProcess;
	private DataOutputStream dos;

	private ShareActionProvider mShareActionProvider;

	DemoCollectionPagerAdapter mDemoCollectionPagerAdapter;

	ViewPager mViewPager;

	private boolean isRooted;

	static OnBackPressed mCallback;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		UpdateApplication updateApplication = (UpdateApplication) getApplicationContext();
		suProcess = updateApplication.getSuProcess();
		dos = updateApplication.getDataOutputStream();
		isRooted = updateApplication.isRooted();

		mDemoCollectionPagerAdapter = new DemoCollectionPagerAdapter(
				getSupportFragmentManager());

		final ActionBar actionBar = getSupportActionBar();

		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor("#007236")));

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mDemoCollectionPagerAdapter);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void onBackPressed() {
		if (mCallback.onBackPressed()) {
			super.onBackPressed();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (isRooted) {
			try {
				dos.writeBytes("exit\n");
				suProcess.waitFor();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);

		MenuItem item = menu.findItem(R.id.action_share);

		mShareActionProvider = (ShareActionProvider) MenuItemCompat
				.getActionProvider(item);

		if (mShareActionProvider != null) {
			Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND);
			shareIntent.putExtra(Intent.EXTRA_TEXT,
					"Here is an app that lets you upgrade your android version."
							+ "\nhttp://market.android.com/search?q=pname:"
							+ APP_PACKAGE_NAME);
			shareIntent.setType("text/plain");
			mShareActionProvider.setShareIntent(shareIntent);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_rate_us) {
			MainActivity.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri
					.parse("market://details?id=" + APP_PACKAGE_NAME)));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public static class DemoCollectionPagerAdapter extends FragmentPagerAdapter {

		SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

		public DemoCollectionPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			Fragment fragment = null;
			switch (i) {
			case 0:
				fragment = new ChangeFragment();
				return fragment;

			case 1:
				fragment = new ProfileFragment();

				try {
					mCallback = (OnBackPressed) fragment;
				} catch (ClassCastException e) {
					throw new ClassCastException(fragment.toString()
							+ " must implement OnBackPressed");
				}

				return fragment;

			default:
				break;
			}

			return fragment;
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return ("Change");

			case 1:
				return ("Profiles");

			default:
				break;
			}
			return null;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			Fragment fragment = (Fragment) super.instantiateItem(container,
					position);
			registeredFragments.put(position, fragment);
			return fragment;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			registeredFragments.remove(position);
			super.destroyItem(container, position, object);
		}

		public Fragment getRegisteredFragment(int position) {
			return registeredFragments.get(position);
		}

	}

	public interface OnBackPressed {
		public boolean onBackPressed();
	}
}
