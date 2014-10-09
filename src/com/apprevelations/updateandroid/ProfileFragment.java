package com.apprevelations.updateandroid;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi.ContentsResult;
import com.google.android.gms.drive.DriveFolder.DriveFileResult;
import com.google.android.gms.drive.MetadataChangeSet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileFragment extends Fragment implements OnClickListener,
		ConnectionCallbacks, OnConnectionFailedListener {

	private static final String TAG_REBOOT_DIALOG = "dialog_reboot";
	private static final String TAG_ADD_PROFILE_DIALOG = "dialog_add_profile";

	private LinearLayout mainLayout;

	private boolean isRooted;
	private Process suProcess;
	private DataOutputStream dos;

	/* Client used to interact with Google APIs. */
	private GoogleApiClient mGoogleApiClient;

	/* Request code used to resolve connection. */
	private static final int RESOLVE_CONNECTION_REQUEST_CODE = 50;

	public ProfileFragment() {
		setHasOptionsMenu(true);
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);

		File file = new File(getActivity().getFilesDir() + "/"
				+ Profile.FILE_NAME);

		if (!file.exists()) {
			try {
				if (file.createNewFile()) {
					ObjectOutputStream oos = new ObjectOutputStream(
							getActivity().openFileOutput(Profile.FILE_NAME,
									Context.MODE_PRIVATE));
					int i = 0;
					oos.writeObject(i);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		UpdateApplication updateApplication = (UpdateApplication) getActivity()
				.getApplicationContext();
		isRooted = updateApplication.isRooted();
		suProcess = updateApplication.getSuProcess();
		dos = updateApplication.getDataOutputStream();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).addApi(Drive.API)
				.addScope(Drive.SCOPE_FILE).addScope(Drive.SCOPE_APPFOLDER)
				.build();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		mGoogleApiClient.connect();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		View rootView = inflater.inflate(R.layout.fragment_profile, container,
				false);

		mainLayout = (LinearLayout) rootView.findViewById(R.id.main_layout);

		refreshProfileLayout();

		return rootView;
	}

	private void refreshProfileLayout() {
		final ProfileUtils profileUtils = new ProfileUtils(getActivity());
		ArrayList<Profile> profilesList = new ArrayList<Profile>();
		profilesList.addAll(profileUtils.readFromFile());

		mainLayout.removeAllViews();

		int i = 0;

		for (Profile tempProfile : profilesList) {
			LayoutInflater inflater = LayoutInflater.from(getActivity());
			LinearLayout profileView = (LinearLayout) inflater.inflate(
					R.layout.single_profile_layout, null, false);
			profileView.setId(++i);
			
			mainLayout.addView(profileView, i-1);			

			String PROFILE_NAME = tempProfile.getProfileName();
			String BUILD_ID = tempProfile.getBuildId();
			String VERSION = tempProfile.getVersion();
			String MODEL = tempProfile.getModel();

			final TextView profileName = (TextView) profileView
					.findViewById(R.id.profile_name);
			final TextView buildId = (TextView) profileView
					.findViewById(R.id.build_id);
			final TextView version = (TextView) profileView
					.findViewById(R.id.version);
			final TextView model = (TextView) profileView
					.findViewById(R.id.model);

			LinearLayout applyLayout = (LinearLayout) profileView
					.findViewById(R.id.apply_layout);

			LinearLayout discardLayout = (LinearLayout) profileView
					.findViewById(R.id.discard_layout_transparent);

			applyLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					commitChanges(buildId.getText().toString(), version
							.getText().toString(), model.getText().toString());
				}
			});

			discardLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Profile profileToBeDeleted = new Profile(profileName
							.getText().toString(),
							buildId.getText().toString(), version.getText()
									.toString(), model.getText().toString());

					profileUtils.deleteObject(profileToBeDeleted);

					refreshProfileLayout();
				}
			});

			profileName.setText(PROFILE_NAME);
			buildId.setText(BUILD_ID);
			version.setText(VERSION);
			model.setText(MODEL);
		}
	}

	public void commitChanges(String versionText, String buildIdText,
			String modelText) {
		if (!isRooted) {
			Toast.makeText(getActivity(), "Phone not rooted!!!",
					Toast.LENGTH_SHORT).show();
			return;
		}

		try {
			dos.writeBytes("mount -o rw,remount /system\n");
			dos.writeBytes("chmod 777 /system/build.prop\n");
			Scanner scanner = new Scanner(new FileInputStream(
					ChangeFragment.orig));
			LinkedList<String> lines = new LinkedList<String>();

			String string;

			while (scanner.hasNextLine()) {

				string = scanner.nextLine();

				if (string.contains(ChangeFragment.VERSION)) {

					lines.add(ChangeFragment.VERSION + versionText + "\n");

					continue;
				}

				if (string.contains(ChangeFragment.BUILD_ID)) {

					lines.add(ChangeFragment.BUILD_ID + buildIdText + "\n");

					continue;
				}

				if (string.contains(ChangeFragment.PRODUCT_MODEL)) {

					lines.add(ChangeFragment.PRODUCT_MODEL + modelText + "\n");

					continue;
				}

				lines.add(string + "\n");
			}

			/* build.prop *//*
							 * dos.writeBytes("chmod 777 /system/build.prop\n"
							 * );
							 */
			// this line has been writen above since writing here leaves no
			// time
			// for changing the permission of build.prop to w (writable)
			// before
			// the just next line is executed

			Writer writer = new BufferedWriter(new FileWriter(
					ChangeFragment.orig, false));
			for (final String line : lines) {
				writer.write(line);
			}
			writer.flush();
			writer.close();

			dos.writeBytes("chmod 644 /system/build.prop\n"); /* build.prop */
			dos.writeBytes("mount -o ro,remount /system\n");

			Toast.makeText(getActivity(), "Changes Commited!!!",
					Toast.LENGTH_SHORT).show();

		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT)
					.show();
		}

		// ------------------

		new MyCustomDialog(getActivity()).show(getChildFragmentManager(),
				TAG_REBOOT_DIALOG);

		// ------------------

		// updateVisibility(); not needed here as this only do changes in
		// build.prop

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		}

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub

		inflater.inflate(R.menu.profile, menu);

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.action_add_profile:
			new AddProfileDialog().show(getChildFragmentManager(),
					TAG_ADD_PROFILE_DIALOG);
			return true;

		default:
			return false;
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		if (connectionResult.hasResolution()) {
			try {
				connectionResult.startResolutionForResult(getActivity(),
						RESOLVE_CONNECTION_REQUEST_CODE);
			} catch (IntentSender.SendIntentException e) {
				// Unable to resolve, message user appropriately
			}
		} else {
			GooglePlayServicesUtil.getErrorDialog(
					connectionResult.getErrorCode(), getActivity(), 0).show();
		}
	}

	@Override
	public void onActivityResult(final int requestCode, final int resultCode,
			final Intent data) {
		switch (requestCode) {

		case RESOLVE_CONNECTION_REQUEST_CODE:
			if (resultCode == Activity.RESULT_OK) {
				mGoogleApiClient.connect();
			}
			break;
		}
	}

	final private ResultCallback<ContentsResult> contentsCallback = new ResultCallback<ContentsResult>() {

		@Override
		public void onResult(ContentsResult result) {
			if (!result.getStatus().isSuccess()) {
				showMessage("Error while trying to create new file contents");
				return;
			}

			MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
					.setTitle("appconfig.txt").setMimeType("text/plain")
					.build();
			Drive.DriveApi
					.getAppFolder(mGoogleApiClient)
					.createFile(mGoogleApiClient, changeSet,
							result.getContents())
					.setResultCallback(fileCallback);
		}
	};

	final private ResultCallback<DriveFileResult> fileCallback = new ResultCallback<DriveFileResult>() {
		@Override
		public void onResult(DriveFileResult result) {
			if (!result.getStatus().isSuccess()) {
				showMessage("Error while trying to create the file");
				return;
			}
			showMessage("Created a file: " + result.getDriveFile().getDriveId());
		}
	};

	protected void showMessage(String string) {
		// TODO Auto-generated method stub
		Toast.makeText(getActivity(), string, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub

	}

	public class AddProfileDialog extends DialogFragment {

		EditText dialogProfileName, dialogBuildId, dialogVersion, dialogModel;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the Builder class for convenient dialog construction
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

			// Get the layout inflater
			LayoutInflater inflater = getActivity().getLayoutInflater();
			View addProfileLayout = inflater.inflate(
					R.layout.dialog_add_profile, null);

			dialogProfileName = (EditText) addProfileLayout
					.findViewById(R.id.dialog_profile_name);
			dialogBuildId = (EditText) addProfileLayout
					.findViewById(R.id.dialog_profile_build_id);
			dialogVersion = (EditText) addProfileLayout
					.findViewById(R.id.dialog_profile_version);
			dialogModel = (EditText) addProfileLayout
					.findViewById(R.id.dialog_profile_model);

			builder.setMessage("Add a new Profile.")
					.setView(addProfileLayout)
					.setPositiveButton("Create",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									ProfileUtils profileUtils = new ProfileUtils(
											getActivity());
									Profile newProfile = new Profile(
											dialogProfileName.getText()
													.toString(), dialogBuildId
													.getText().toString(),
											dialogVersion.getText().toString(),
											dialogModel.getText().toString());

									if (profileUtils.writeToFile(newProfile)) {
										refreshProfileLayout();
									}
								}
							})
					.setNegativeButton("Discard",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// User cancelled the dialog
								}
							});

			// Create the AlertDialog object and return it
			return builder.create();
		}
	}

}
