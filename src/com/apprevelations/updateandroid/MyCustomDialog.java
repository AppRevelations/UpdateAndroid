package com.apprevelations.updateandroid;

import java.io.DataOutputStream;
import java.io.IOException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

public class MyCustomDialog extends DialogFragment {

	DataOutputStream dos;
	Context mContext;

	public MyCustomDialog(Context context) {
		mContext = context;
		UpdateApplication updateApplication = (UpdateApplication) mContext
				.getApplicationContext();
		dos = updateApplication.getDataOutputStream();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setMessage(
				"Commiting these changes require you to reboot your device.")
				.setPositiveButton("Reboot",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								try {
									dos.writeBytes("mount -o ro,remount /system\n");
									dos.writeBytes("reboot\n");
								} catch (IOException e) {
									e.printStackTrace();
									Toast.makeText(getActivity(), e.toString(),
											Toast.LENGTH_SHORT).show();
								}
							}
						})
				.setNegativeButton("Not Now",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// User cancelled the dialog
							}
						});
		return builder.create();
	}
}
