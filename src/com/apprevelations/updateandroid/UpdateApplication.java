package com.apprevelations.updateandroid;

import java.io.DataOutputStream;
import java.io.IOException;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

public class UpdateApplication extends Application {

	private static final String TAG = "UpdateApplication";

	private static boolean isRooted = false;

	private static Runtime r = Runtime.getRuntime();
	private static Process suProcess;
	private static DataOutputStream dos;
	
	private static Thread suResult = new Thread(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			
			int result = 0;
			try {
			    result = suProcess.exitValue();
			} catch (IllegalThreadStateException e) {
			    e.printStackTrace();
			}
			if (result == 0) {
			    // Your process p is root and running
				isRooted = true;
			} else {
			    // su failed
				isRooted = false;
			}
		}
		
	};

	@Override
	public void onCreate() {
		super.onCreate();

		try {
			suProcess = r.exec("su");
			dos = new DataOutputStream(suProcess.getOutputStream());
			suResult.run();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			isRooted = false;
			Toast.makeText(this, "Phone not rooted!!!", Toast.LENGTH_SHORT)
					.show();
			e1.printStackTrace();
			Log.e(TAG, e1.getMessage());
		}
	}

	public static boolean isRooted() {
		return isRooted;
	}

	public static Runtime getRuntime() {
		return r;
	}

	public static Process getSuProcess() {
		return suProcess;
	}

	public static DataOutputStream getDataOutputStream() {
		return dos;
	}

}
