package com.apprevelations.updateandroid;

import java.io.Serializable;

public class Profile implements Serializable {

	public static final String FILE_NAME = "Profiles";

	private String profileName;
	private String buildId;
	private String version;
	private String model;

	public Profile(String profileName, String buildId, String version,
			String model) {
		this.profileName = profileName;
		this.buildId = buildId;
		this.version = version;
		this.model = model;
	}

	@Override
	public boolean equals(Object o) {
		// If the object is compared with itself then return true
		if (o == this) {
			return true;
		}

		/*
		 * Check if o is an instance of ProfileUtils or not
		 * "null instanceof [type]" also returns false
		 */
		if (!(o instanceof Profile)) {
			return false;
		}

		// typecast o to Complex so that we can compare data members
		Profile profile = (Profile) o;

		// Compare the data members and return accordingly
		return profile.getProfileName().equals(this.getProfileName())
				&& profile.getBuildId().equals(this.getBuildId())
				&& profile.getVersion().equals(this.getVersion())
				&& profile.getModel().equals(this.getModel());
	}

	public String getProfileName() {
		return profileName;
	}

	public String getBuildId() {
		return buildId;
	}

	public String getVersion() {
		return version;
	}

	public String getModel() {
		return model;
	}

	public String setProfileName(String profileName) {
		this.profileName = profileName;
		return profileName;
	}

	public String setBuildId(String buildId) {
		this.buildId = buildId;
		return buildId;
	}

	public String setVersion(String version) {
		this.version = version;
		return version;
	}

	public String setModel(String model) {
		this.model = model;
		return model;
	}

}
