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

import com.apprevelations.updateandroid.ProfileUtils.ProfileException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileFragment extends Fragment implements
		MainActivity.OnBackPressed {

	private static final String TAG_REBOOT_DIALOG = "dialog_reboot";
	private static final String TAG_ADD_PROFILE_DIALOG = "dialog_add_profile";

	private static ArrayList<LinearLayout> currentProfileBeingEdited = new ArrayList<LinearLayout>();

	public static final File temp = new File("/system", "build1.prop");

	private LinearLayout mainLayout;
	private LinearLayout addImage;
	private ScrollView mScrollView;

	private boolean isRooted;
	private DataOutputStream dos;

	Animation fadeOut;
	Animation fadeIn;

	private GestureDetector swipeDetector = new GestureDetector(
			new SwipeGesture());

	public ProfileFragment() {
		setHasOptionsMenu(true);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		File file = new File(activity.getFilesDir() + "/" + Profile.FILE_NAME);

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
				e.printStackTrace();
			}
		}

		UpdateApplication updateApplication = (UpdateApplication) activity
				.getApplicationContext();
		isRooted = updateApplication.isRooted();
		dos = updateApplication.getDataOutputStream();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_profile, container,
				false);

		addImage = (LinearLayout) rootView.findViewById(R.id.add_image);

		mainLayout = (LinearLayout) rootView.findViewById(R.id.main_layout);

		mScrollView = (ScrollView) rootView.findViewById(R.id.scroll_view);

		addImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new AddProfileDialog().show(getChildFragmentManager(),
						TAG_ADD_PROFILE_DIALOG);
			}
		});

		mScrollView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// mScrollView.dispatchTouchEvent(event);
				swipeDetector.onTouchEvent(event);
				// return true;

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					swipeDetector.onTouchEvent(event);
					return false;
				} else {
					swipeDetector.onTouchEvent(event);
					return false;
				}
			}
		});

		fadeOut = AnimationUtils.loadAnimation(getActivity(),
				R.anim.fadeout);

		fadeIn = AnimationUtils.loadAnimation(getActivity(),
				R.anim.fadein);

		refreshProfileLayout();

		return rootView;
	}

	class SwipeGesture extends SimpleOnGestureListener {

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {

			if (distanceY > 0) {
				if(addImage.getVisibility() != View.GONE) {
					addImage.startAnimation(fadeOut);
				}
				addImage.setVisibility(View.GONE);
			} else if (distanceY < 0) {
				if (addImage.getVisibility() != View.VISIBLE) {
					addImage.startAnimation(fadeIn);
				}
				addImage.setVisibility(View.VISIBLE);
			}

			return false;
		}

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

			mainLayout.addView(profileView, i - 1);

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

			final EditText profileNameEditText = (EditText) profileView
					.findViewById(R.id.profile_name_edit_text);
			final EditText buildIdEditText = (EditText) profileView
					.findViewById(R.id.build_id_edit_text);
			final EditText versionEditText = (EditText) profileView
					.findViewById(R.id.version_edit_text);
			final EditText modelEditText = (EditText) profileView
					.findViewById(R.id.model_edit_text);

			final LinearLayout applyLayout = (LinearLayout) profileView
					.findViewById(R.id.apply_layout);

			final LinearLayout discardLayout = (LinearLayout) profileView
					.findViewById(R.id.discard_layout);

			final ImageView editProfile = (ImageView) profileView
					.findViewById(R.id.edit_pofile);

			applyLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					commitChanges(buildId.getText().toString(), version
							.getText().toString(), model.getText().toString());
				}
			});

			discardLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Profile profileToBeDeleted = new Profile(profileName
							.getText().toString(),
							buildId.getText().toString(), version.getText()
									.toString(), model.getText().toString());

					profileUtils.deleteObject(profileToBeDeleted);

					refreshProfileLayout();
				}
			});

			editProfile.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					profileName.setVisibility(View.GONE);
					buildId.setVisibility(View.GONE);
					version.setVisibility(View.GONE);
					model.setVisibility(View.GONE);

					profileNameEditText.setVisibility(View.VISIBLE);
					buildIdEditText.setVisibility(View.VISIBLE);
					versionEditText.setVisibility(View.VISIBLE);
					modelEditText.setVisibility(View.VISIBLE);

					profileNameEditText.setText(profileName.getText()
							.toString());
					buildIdEditText.setText(buildId.getText().toString());
					versionEditText.setText(version.getText().toString());
					modelEditText.setText(model.getText().toString());

					currentProfileBeingEdited.add(discardLayout);

					applyLayout.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Profile objectToBeUpdated = new Profile(profileName
									.getText().toString(), buildId.getText()
									.toString(), version.getText().toString(),
									model.getText().toString());

							Profile modifiedObject = new Profile(
									profileNameEditText.getText().toString(),
									buildIdEditText.getText().toString(),
									versionEditText.getText().toString(),
									modelEditText.getText().toString());

							try {
								profileUtils.updateObject(objectToBeUpdated,
										modifiedObject);
								showMessage("Profile Updated");
							} catch (ProfileException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								showMessage(e.getMessage());
							}

							profileName.setVisibility(View.VISIBLE);
							buildId.setVisibility(View.VISIBLE);
							version.setVisibility(View.VISIBLE);
							model.setVisibility(View.VISIBLE);

							profileNameEditText.setVisibility(View.GONE);
							buildIdEditText.setVisibility(View.GONE);
							versionEditText.setVisibility(View.GONE);
							modelEditText.setVisibility(View.GONE);

							refreshProfileLayout();
						}
					});

					discardLayout.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							profileName.setVisibility(View.VISIBLE);
							buildId.setVisibility(View.VISIBLE);
							version.setVisibility(View.VISIBLE);
							model.setVisibility(View.VISIBLE);

							profileNameEditText.setVisibility(View.GONE);
							buildIdEditText.setVisibility(View.GONE);
							versionEditText.setVisibility(View.GONE);
							modelEditText.setVisibility(View.GONE);
						}
					});
				}
			});

			profileName.setText(PROFILE_NAME);
			buildId.setText(BUILD_ID);
			version.setText(VERSION);
			model.setText(MODEL);
		}
	}

	private void backupOriginal() {
		if (!(temp.exists())) {
			try {
				dos.writeBytes("mount -o rw,remount /system\n");
				dos.writeBytes("cp /system/build.prop /system/build1.prop\n");
				dos.writeBytes("mount -o ro,remount /system\n");
				showMessage("Original file backuped");
			} catch (IOException e) {
				e.printStackTrace();
				showMessage("Root permission required !!!");

			}
		}

		else {
			showMessage("Original file already exists");
		}
	}

	public void commitChanges(String versionText, String buildIdText,
			String modelText) {

		if (!isRooted) {
			showMessage("Phone not rooted!!!");
			return;
		}

		backupOriginal();

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

			scanner.close();

			/* build.prop */
			/*
			 * dos.writeBytes("chmod 777 /system/build.prop\n" );
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

			showMessage("Changes Commited!!!");

			new MyCustomDialog(getActivity()).show(getChildFragmentManager(),
					TAG_REBOOT_DIALOG);

		} catch (IOException e) {
			e.printStackTrace();
			showMessage("Root permission required !!!");
		}

		// updateVisibility(); not needed here as this only do changes in
		// build.prop

	}

	protected void showMessage(String string) {
		Toast.makeText(getActivity(), string, Toast.LENGTH_SHORT).show();
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

			builder.setView(addProfileLayout)
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

									try {
										profileUtils.writeToFile(newProfile);
										refreshProfileLayout();
									} catch (ProfileException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
										showMessage(e.getMessage());
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

			return builder.create();
		}
	}

	@Override
	public boolean onBackPressed() {

		if (!currentProfileBeingEdited.isEmpty()) {
			for (LinearLayout tempDiscardLayour : currentProfileBeingEdited) {
				tempDiscardLayour.performClick();
			}
			currentProfileBeingEdited.clear();

			return false;
		}

		return true;
	}
}
