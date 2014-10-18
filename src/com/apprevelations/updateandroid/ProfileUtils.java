package com.apprevelations.updateandroid;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

import android.content.Context;

public class ProfileUtils {

	private Context mContext;

	public ProfileUtils(Context context) {
		this.mContext = context;
	}

	synchronized public boolean updateObject(Profile objectToBeUpdated,
			Profile modifiedObject) throws ProfileException {
		ArrayList<Profile> profileObjects = new ArrayList<Profile>();

		if (modifiedObject.getProfileName().equals("")
				|| modifiedObject.getBuildId().equals("")
				|| modifiedObject.getVersion().equals("")
				|| modifiedObject.getModel().equals("")) {
			throw new ProfileException("None of the values can be empty");
		}

		for (Profile tempProfile : profileObjects) {
			if (tempProfile.getProfileName().equals(
					modifiedObject.getProfileName())) {
				throw new ProfileException(
						"Profile name cannot be same as any other profile name");
			}
		}

		try {
			ObjectInputStream ois = new ObjectInputStream(
					mContext.openFileInput(Profile.FILE_NAME));

			int noOfObjects = (Integer) ois.readObject();

			while (noOfObjects != 0) {
				Profile tempObject = (Profile) ois.readObject();
				noOfObjects--;
				if (tempObject.equals(objectToBeUpdated)) {
					profileObjects.add(modifiedObject);
					continue;
				}
				profileObjects.add(tempObject);
			}
			ois.close();

			ObjectOutputStream oos = new ObjectOutputStream(
					mContext.openFileOutput(Profile.FILE_NAME,
							Context.MODE_PRIVATE));
			oos.writeObject(profileObjects.size());
			while (!profileObjects.isEmpty()) {
				oos.writeObject(profileObjects.get(0));
				profileObjects.remove(0);
			}
			oos.close();
			return true;
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return false;
	}

	synchronized public boolean deleteObject(Profile objectToBeDeleted) {

		ArrayList<Profile> profileObjects = new ArrayList<Profile>();

		try {
			ObjectInputStream ois = new ObjectInputStream(
					mContext.openFileInput(Profile.FILE_NAME));

			int noOfObjects = (Integer) ois.readObject();

			while (noOfObjects != 0) {
				Profile tempObject = (Profile) ois.readObject();
				noOfObjects--;
				if (tempObject.equals(objectToBeDeleted)) {
					continue;
				}
				profileObjects.add(tempObject);
			}
			ois.close();

			ObjectOutputStream oos = new ObjectOutputStream(
					mContext.openFileOutput(Profile.FILE_NAME,
							Context.MODE_PRIVATE));
			oos.writeObject(profileObjects.size());
			while (!profileObjects.isEmpty()) {
				oos.writeObject(profileObjects.get(0));
				profileObjects.remove(0);
			}
			oos.close();
			return true;
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return false;
	}

	public ArrayList<Profile> readFromFile() {

		ArrayList<Profile> profileObjects = new ArrayList<Profile>();

		try {
			ObjectInputStream ois = new ObjectInputStream(
					mContext.openFileInput(Profile.FILE_NAME));

			int noOfObjects = (Integer) ois.readObject();

			while (noOfObjects != 0) {
				profileObjects.add((Profile) ois.readObject());
				noOfObjects--;
			}
			ois.close();
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return profileObjects;
	}

	synchronized public boolean writeToFile(Profile newProfile)
			throws ProfileException {

		ArrayList<Profile> profileObjects = new ArrayList<Profile>();
		profileObjects = readFromFile();
		
		if (newProfile.getProfileName().equals("")
				|| newProfile.getBuildId().equals("")
				|| newProfile.getVersion().equals("")
				|| newProfile.getModel().equals("")) {
			throw new ProfileException("None of the values can be empty");
		}

		for (Profile tempProfile : profileObjects) {
			if (tempProfile.getProfileName()
					.equals(newProfile.getProfileName())) {
				throw new ProfileException(
						"Profile name cannot be same as any other profile name");
			}
		}

		profileObjects.add(newProfile);

		try {
			ObjectOutputStream oos = new ObjectOutputStream(
					mContext.openFileOutput(Profile.FILE_NAME,
							Context.MODE_PRIVATE));
			oos.writeObject(profileObjects.size());
			while (!profileObjects.isEmpty()) {
				oos.writeObject(profileObjects.get(0));
				profileObjects.remove(0);
			}
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;

	}

	public class ProfileException extends Exception {

		String exceptionMessage;

		public ProfileException() {
			super();
		}

		public ProfileException(String exceptionMessage) {
			super(exceptionMessage);
			this.exceptionMessage = exceptionMessage;
		}

		@Override
		public String getMessage() {
			return exceptionMessage;
		}

	}
}
