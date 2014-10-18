package com.apprevelations.updateandroid;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;

public class ProfileUtils {

	private Context mContext;

	public ProfileUtils(Context context) {
		this.mContext = context;
	}

	synchronized public boolean updateObject(Profile objectToBeUpdated,
			Profile modifiedObject) {
		ArrayList<Profile> profileObjects = new ArrayList<Profile>();

		for (Profile tempProfile : profileObjects) {
			if (tempProfile.getProfileName().equals(
					modifiedObject.getProfileName())) {
				return false;
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

	synchronized public boolean writeToFile(Profile newProfile) {

		ArrayList<Profile> profileObjects = new ArrayList<Profile>();
		profileObjects = readFromFile();

		for (Profile tempProfile : profileObjects) {
			if (tempProfile.getProfileName()
					.equals(newProfile.getProfileName())) {
				return false;
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
}
