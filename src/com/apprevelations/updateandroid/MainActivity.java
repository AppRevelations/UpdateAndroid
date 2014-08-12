package com.apprevelations.updateandroid;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements OnClickListener, ConnectionCallbacks, OnConnectionFailedListener {
	
	/* Track whether the sign-in button has been clicked so that we know to resolve
	 * all issues preventing sign-in without waiting.
	 */
	private boolean mSignInClicked;

	/* Store the connection result from onConnectionFailed callbacks so that we can
	 * resolve them when the user clicks sign-in.
	 */
	private ConnectionResult mConnectionResult;

	
	/* Request code used to invoke sign in user interactions. */
	private static final int RC_SIGN_IN = 0;

	/* Client used to interact with Google APIs. */
	private GoogleApiClient mGoogleApiClient;

	/* A flag indicating that a PendingIntent is in progress and prevents
	 * us from starting further intents.
	*/
	private boolean mIntentInProgress;
	
	private int NO_OF_PROPERTIES = 3;
	
	private boolean isRooted = false;
	
	private Button update, reset;
	private EditText name, version, model;
	private File orig = new File("/system", "build.prop");
	private File temp = new File("/system",	"build1.prop");
	//File temp = new File(MainActivity.this.getFilesDir(), "build.prop");
	
	private Runtime r = Runtime.getRuntime();
	private Process suProcess;
	private static DataOutputStream dos;
	
	private Scanner scanner;
	private List<String> lines;
	private String string;
	private String PRODUCT_MODEL = "ro.product.model=";
	private String BUILD_ID = "ro.build.display.id=";
	private String VERSION = "ro.build.version.release=";
	private BufferedWriter writer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mGoogleApiClient = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(Plus.API)
        .addScope(Plus.SCOPE_PLUS_LOGIN)
        .build();
		
		setContentView(R.layout.activity_main);
		
		update = (Button) findViewById(R.id.button1);
		reset = (Button) findViewById(R.id.button2);
		
		name = (EditText) findViewById(R.id.editText1);
		version = (EditText) findViewById(R.id.editText2);
		model = (EditText) findViewById(R.id.editText3);
		
		findViewById(R.id.google_plus_sign_in_button).setOnClickListener(this);
		findViewById(R.id.google_plus_logout_button).setOnClickListener(this);
		
		try {
			suProcess = r.exec("su");
			isRooted = true;
			dos = new DataOutputStream(suProcess.getOutputStream());
			
			backupOriginal();
			
			update.setOnClickListener(this);
			reset.setOnClickListener(this);
			
			showCurrentProperties();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			isRooted = false;
			showCurrentProperties();
			e1.printStackTrace();
			Toast.makeText(getApplicationContext(), e1.toString(), Toast.LENGTH_SHORT).show();
			//make toast to report error
		}
	}

	private void showCurrentProperties() {
		// TODO Auto-generated method stub
		
		int flag = NO_OF_PROPERTIES;
		
		if(isRooted){
			try {
				dos.writeBytes("chmod 777 /system/build.prop\n");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
			
		try {
			scanner = new Scanner(new FileInputStream(orig));
			while((flag != 0) && (scanner.hasNextLine())){
				
				string = scanner.nextLine();
				
				if(string.contains(VERSION)){
					version.setHint(string.substring(string.indexOf("=") + 1));
					flag--;
					continue;
				}
				
				if(string.contains(BUILD_ID)){
					name.setHint(string.substring(string.indexOf("=") + 1));
					flag--;
					continue;
				}
				
				if(string.contains(PRODUCT_MODEL)){
					model.setHint(string.substring(string.indexOf("=") + 1));
					flag--;
					continue;
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		if(isRooted){
			try {
				dos.writeBytes("chmod 644 /system/build.prop\n");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	private void backupOriginal() {
		// TODO Auto-generated method stub
		if(!(temp.exists())){
			try {
				dos.writeBytes("mount -o rw,remount /system\n");
				dos.writeBytes("cp /system/build.prop /system/build1.prop\n");
				dos.writeBytes("mount -o ro,remount /system\n");
				Toast.makeText(getApplicationContext(), "Original file backuped", Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
			}
		}
		
		else{
			Toast.makeText(getApplicationContext(), "Original file already exists", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void restoreOriginal() {
		// TODO Auto-generated method stub
		
		if(temp.exists()){
			try {
				dos.writeBytes("mount -o rw,remount /system\n");
				dos.writeBytes("mv /system/build1.prop /system/build.prop\n");
				dos.writeBytes("mount -o ro,remount /system\n");
				Toast.makeText(getApplicationContext(), "Original file restored", Toast.LENGTH_SHORT).show();
				
				new MyCustomDialog().show(getSupportFragmentManager(), "reboot_dialog");
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch(v.getId()){
		
			case R.id.button1 : 
			
				try {
					dos.writeBytes("mount -o rw,remount /system\n");
					dos.writeBytes("chmod 777 /system/build.prop\n");
					scanner = new Scanner(new FileInputStream(orig));
					lines = new LinkedList<String>();
			    
					while(scanner.hasNextLine()){
			        
						string = scanner.nextLine();
			    	
						if(string.contains(VERSION)){
							lines.add(VERSION + version.getText().toString() + "\n");
							continue;
						}
						
						if(string.contains(BUILD_ID)){
							lines.add(BUILD_ID + name.getText().toString() + "\n");
							continue;
						}
						
						if(string.contains(PRODUCT_MODEL)){
							lines.add(PRODUCT_MODEL + model.getText().toString() + "\n");
							continue;
						}
			    	
						lines.add(string + "\n");
					}
			    
/*build.prop*/  	/*dos.writeBytes("chmod 777 /system/build.prop\n");*/ //this line has been writen above since writing here leaves no time for changing the permission of build.prop to w (writable) before the just next line is executed 
			    
					writer = new BufferedWriter(new FileWriter(orig, false));
					for(final String line : lines){
						writer.write(line);
					}
					writer.flush();
					writer.close();
					
					dos.writeBytes("chmod 644 /system/build.prop\n");    /*build.prop*/
					dos.writeBytes("mount -o ro,remount /system\n");
					
					Toast.makeText(getApplicationContext(), "Changes Commited!!!", Toast.LENGTH_SHORT).show();

				} catch (IOException e) {
					e.printStackTrace();
					string = e.toString();
					Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();
				}
				
				//------------------
				
				new MyCustomDialog().show(getSupportFragmentManager(), "reboot_dialog");
				
				//------------------
				
//				updateVisibility();			not needed here as this only do changes in build.prop
			
				break;
		
			case R.id.button2 : 
			
				restoreOriginal();
			
				break;

			case R.id.google_plus_sign_in_button :
				
				mGoogleApiClient.connect();
				if(!mGoogleApiClient.isConnecting()){
					mSignInClicked = true;
					if(!mGoogleApiClient.isConnected()){
						resolveSignInError();
					}else{
						Toast.makeText(getApplicationContext(), "Already Connected", Toast.LENGTH_SHORT).show();
					}
				}
				
				break;
			
			case R.id.google_plus_logout_button : 
				
				if (mGoogleApiClient.isConnected()) {
					Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
					mGoogleApiClient.disconnect();
					mGoogleApiClient.connect();
					Toast.makeText(getApplicationContext(), "Signed Out", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getApplicationContext(), "Already Signed Out", Toast.LENGTH_SHORT).show();
				}
				
				break;
		
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		if(isRooted){
			try {
				dos.writeBytes("chmod 644 /system/build.prop\n");	/*build.prop*/
				dos.writeBytes("mount -o ro,remount /system\n");	// reboot
				dos.writeBytes("exit\n");
				suProcess.waitFor();
				Toast.makeText(getApplicationContext(), "Exiting Application", Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (!mIntentInProgress && result.hasResolution()) {
			try {
				mIntentInProgress = true;
				startIntentSenderForResult(result.getResolution().getIntentSender(),
						RC_SIGN_IN, null, 0, 0, 0);
			} catch (SendIntentException e) {
				// The intent was canceled before it was sent.  Return to the default
				// state and attempt to connect to get an updated ConnectionResult.
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		}
		
		if (!mIntentInProgress) {
			// Store the ConnectionResult so that we can use it later when the user clicks
			// 'sign-in'.
			mConnectionResult = result;

		    if (mSignInClicked) {
		      // The user has already clicked 'sign-in' so we attempt to resolve all
		      // errors until the user is signed in, or they cancel.
		      resolveSignInError();
		    }
		  }
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// We've resolved any connection errors.  mGoogleApiClient can be used to
		// access Google APIs on behalf of the user.
		mSignInClicked = false;
		Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
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
	      startIntentSenderForResult(mConnectionResult.getResolution().getIntentSender(),
	          RC_SIGN_IN, null, 0, 0, 0);
	    } catch (SendIntentException e) {
	      // The intent was canceled before it was sent.  Return to the default
	      // state and attempt to connect to get an updated ConnectionResult.
	      mIntentInProgress = false;
	      mGoogleApiClient.connect();
	    }
	  }
	}
	
	public static class MyCustomDialog extends DialogFragment {
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setMessage("Commiting these changes require you to reboot your device.")
	               .setPositiveButton("Reboot", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                	   try {
							dos.writeBytes("mount -o ro,remount /system\n");
							dos.writeBytes("reboot\n");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
						}
	                   }
	               })
	               .setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                       // User cancelled the dialog
	                   }
	               });
	        // Create the AlertDialog object and return it
	        return builder.create();
	    }
	}

}

