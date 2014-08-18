package com.apprevelations.updateandroid;

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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChangeFragment extends Fragment implements OnClickListener{
	
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
	
	/**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "1";
	
	public ChangeFragment(){
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_change, container, false);
		
		update = (Button) rootView.findViewById(R.id.button1);
		reset = (Button) rootView.findViewById(R.id.button2);
		
		name = (EditText) rootView.findViewById(R.id.editText1);
		version = (EditText) rootView.findViewById(R.id.editText2);
		model = (EditText) rootView.findViewById(R.id.editText3);
				
		try {
			suProcess = r.exec("su");
			isRooted = true;
			dos = new DataOutputStream(suProcess.getOutputStream());
			
			backupOriginal();
			
			showCurrentProperties();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			isRooted = false;
			showCurrentProperties();
			e1.printStackTrace();
			Toast.makeText(getActivity(), "Phone not rooted!!!", Toast.LENGTH_SHORT).show();
			//make toast to report error
		}
		
		update.setOnClickListener(this);
		reset.setOnClickListener(this);
		
		return rootView;
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
				Toast.makeText(getActivity(), "Original file backuped", Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
			}
		}
		
		else{
			Toast.makeText(getActivity(), "Original file already exists", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void restoreOriginal() {
		// TODO Auto-generated method stub
		
		if(temp.exists()){
			try {
				dos.writeBytes("mount -o rw,remount /system\n");
				dos.writeBytes("mv /system/build1.prop /system/build.prop\n");
				dos.writeBytes("mount -o ro,remount /system\n");
				Toast.makeText(getActivity(), "Original file restored", Toast.LENGTH_SHORT).show();
				
				new MyCustomDialog().show(getChildFragmentManager(), "reboot_dialog");
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch(v.getId()){
		
			case R.id.button1 : 
				
				if(!isRooted){
					Toast.makeText(getActivity(), "Phone not rooted!!!", Toast.LENGTH_SHORT).show();
					break;
				}
			
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
					
					Toast.makeText(getActivity(), "Changes Commited!!!", Toast.LENGTH_SHORT).show();

				} catch (IOException e) {
					e.printStackTrace();
					string = e.toString();
					Toast.makeText(getActivity(), string, Toast.LENGTH_SHORT).show();
				}
				
				//------------------
				
				new MyCustomDialog().show(getChildFragmentManager(), "reboot_dialog");
				
				//------------------
				
//				updateVisibility();			not needed here as this only do changes in build.prop
			
				break;
		
			case R.id.button2 :
				
				if(!isRooted){
					Toast.makeText(getActivity(), "Phone not rooted!!!", Toast.LENGTH_SHORT).show();
					break;
				}
			
				restoreOriginal();
			
				break;
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		if(isRooted){
			try {
				dos.writeBytes("chmod 644 /system/build.prop\n");	/*build.prop*/
				dos.writeBytes("mount -o ro,remount /system\n");	// reboot
				dos.writeBytes("exit\n");
				suProcess.waitFor();
				Toast.makeText(getActivity(), "Exiting Application", Toast.LENGTH_SHORT).show();
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(Integer.parseInt(this.ARG_SECTION_NUMBER));
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
