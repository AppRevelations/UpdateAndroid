package com.apprevelations.updateandroid;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.Person.Image;

import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements OnClickListener,
		ConnectionCallbacks, OnConnectionFailedListener,
		NavigationDrawerFragment.NavigationDrawerCallbacks {

	private Process suProcess;
	private DataOutputStream dos;

	private ShareActionProvider mShareActionProvider;

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	/*
	 * Track whether the sign-in button has been clicked so that we know to
	 * resolve all issues preventing sign-in without waiting.
	 */
	private boolean mSignInClicked;

	/*
	 * Store the connection result from onConnectionFailed callbacks so that we
	 * can resolve them when the user clicks sign-in.
	 */
	private ConnectionResult mConnectionResult;

	/* Request code used to invoke sign in user interactions. */
	private static final int RC_SIGN_IN = 0;

	/* Client used to interact with Google APIs. */
	private GoogleApiClient mGoogleApiClient;

	/*
	 * A flag indicating that a PendingIntent is in progress and prevents us
	 * from starting further intents.
	 */
	private boolean mIntentInProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).addApi(Plus.API)
				.addScope(Plus.SCOPE_PLUS_LOGIN).build();

		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));

		UpdateApplication updateApplication = (UpdateApplication) getApplicationContext();
		suProcess = updateApplication.getSuProcess();
		dos = updateApplication.getDataOutputStream();
	}

	@Override
	protected void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}

	@Override
	protected void onStop() {
		super.onStop();

		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			Toast.makeText(this, "Exiting Application",
					Toast.LENGTH_SHORT).show();
			dos.writeBytes("exit\n");
			suProcess.waitFor();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		// Locate MenuItem with ShareActionProvider
		MenuItem item = menu.findItem(R.id.action_share);

		// Fetch and store ShareActionProvider
		mShareActionProvider = (ShareActionProvider) MenuItemCompat
				.getActionProvider(item);

		if (mShareActionProvider != null) {
			Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND);
			shareIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
			shareIntent.setType("text/plain");
			// startActivity(Intent.createChooser(shareIntent,
			// getResources().getText(R.string.send_to)));
			mShareActionProvider.setShareIntent(shareIntent);
		}

		// Return true to display menu
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_rate_us) {
			return true;
		} else if (id == R.id.action_about_us) {
			return true;
		} else if (id == R.id.action_logout) {

			if (mGoogleApiClient.isConnected()) {
				Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
				mGoogleApiClient.disconnect();
				mGoogleApiClient.connect();

				mNavigationDrawerFragment
						.showUserProfilePic(BitmapFactory.decodeResource(
								getResources(), R.drawable.ic_launcher));
				mNavigationDrawerFragment.showUserName("Name");
				mNavigationDrawerFragment.showEmailId("Email ID");
			}
			return true;

		} else if (id == R.id.action_revoke_access) {

			if (mGoogleApiClient.isConnected()) {
				Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
				Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
						.setResultCallback(new ResultCallback<Status>() {
							@Override
							public void onResult(Status arg0) {
								mGoogleApiClient.connect();
							}

						});
				mNavigationDrawerFragment
						.showUserProfilePic(BitmapFactory.decodeResource(
								getResources(), R.drawable.ic_launcher));
				mNavigationDrawerFragment.showUserName("Name");
				mNavigationDrawerFragment.showEmailId("Email ID");
			}
			return true;

		} else if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {

		case R.id.transparent_layout:

			// mGoogleApiClient.connect();
			if (!mGoogleApiClient.isConnecting()) {
				mSignInClicked = true;
				// if(!mGoogleApiClient.isConnected()){
				resolveSignInError();
				// }else{
				// Toast.makeText(getApplicationContext(), "Already Connected",
				// Toast.LENGTH_SHORT).show();
				// }
			}

			break;

		/*
		 * case R.id.google_plus_logout_button :
		 * 
		 * if (mGoogleApiClient.isConnected()) {
		 * Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
		 * mGoogleApiClient.disconnect(); //mGoogleApiClient.connect();
		 * Toast.makeText(getApplicationContext(), "Signed Out",
		 * Toast.LENGTH_SHORT).show(); } else {
		 * Toast.makeText(getApplicationContext(), "Already Signed Out",
		 * Toast.LENGTH_SHORT).show(); }
		 * 
		 * break;
		 */
		}
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getSupportFragmentManager();
		Fragment fragment = null;

		switch (position) {
		case R.id.navigation_drawer_item_0:
			fragment = new ChangeFragment();
			break;

		case R.id.navigation_drawer_item_1:
			// To be replaced afterwards with the fragment
			fragment = new ProfileFragment();
			break;

		case R.id.navigation_drawer_item_2:
			// To be replaced afterwards with the fragment
			fragment = new ChangeFragment();
			break;

		default:
			break;
		}

		fragmentManager.beginTransaction().replace(R.id.container, fragment)
				.commit();
	}

	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle = getString(R.string.title_section1);
			break;
		case 2:
			mTitle = getString(R.string.title_section2);
			break;
		case 3:
			mTitle = getString(R.string.title_section3);
			break;
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		/*
		 * if (!mIntentInProgress && result.hasResolution()) { try {
		 * mIntentInProgress = true;
		 * startIntentSenderForResult(result.getResolution().getIntentSender(),
		 * RC_SIGN_IN, null, 0, 0, 0); } catch (SendIntentException e) { // The
		 * intent was canceled before it was sent. Return to the default //
		 * state and attempt to connect to get an updated ConnectionResult.
		 * mIntentInProgress = false; mGoogleApiClient.connect(); } }
		 */

		if (!result.hasResolution()) {
			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
					0).show();
			return;
		}

		if (!mIntentInProgress) {
			// Store the ConnectionResult so that we can use it later when the
			// user clicks
			// 'sign-in'.
			mConnectionResult = result;

			if (mSignInClicked) {
				// The user has already clicked 'sign-in' so we attempt to
				// resolve all
				// errors until the user is signed in, or they cancel.
				resolveSignInError();
			}
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// We've resolved any connection errors. mGoogleApiClient can be used to
		// access Google APIs on behalf of the user.
		mSignInClicked = false;
		Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
		if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
			Person currentPerson = Plus.PeopleApi
					.getCurrentPerson(mGoogleApiClient);
			mNavigationDrawerFragment.showUserName(currentPerson
					.getDisplayName());
			new GetProfilePic().execute(currentPerson.getImage().getUrl());
			mNavigationDrawerFragment.showEmailId(Plus.AccountApi
					.getAccountName(mGoogleApiClient));
			// String personGooglePlusProfile = currentPerson.getUrl();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int responseCode,
			Intent intent) {
		if (requestCode == RC_SIGN_IN) {

			if (responseCode != RESULT_OK) {
				mSignInClicked = false;
			}

			mIntentInProgress = false;

			if (!mGoogleApiClient.isConnecting()) {
				mGoogleApiClient.connect();
			}
		}
	}

	@Override
	public void onConnectionSuspended(int cause) {
		mGoogleApiClient.connect();
	}

	/* A helper method to resolve the current ConnectionResult error. */
	private void resolveSignInError() {
		if (mConnectionResult.hasResolution()) {
			try {
				mIntentInProgress = true;
				// startIntentSenderForResult(mConnectionResult.getResolution().getIntentSender(),
				// RC_SIGN_IN, null, 0, 0, 0);
				mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
			} catch (SendIntentException e) {
				// The intent was canceled before it was sent. Return to the
				// default
				// state and attempt to connect to get an updated
				// ConnectionResult.
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		}
	}

	private class GetProfilePic extends AsyncTask<String, Void, Bitmap> {

		protected Bitmap doInBackground(String... params) {
			String profilePicUrl = params[0];
			Bitmap profilePic = null;

			InputStream in;
			try {
				in = new URL(profilePicUrl).openStream();
				profilePic = BitmapFactory.decodeStream(in);
			} catch (MalformedURLException e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}

			return profilePic;
		}

		protected void onPostExecute(Bitmap result) {
			mNavigationDrawerFragment.showUserProfilePic(result);
		}
	}

}
