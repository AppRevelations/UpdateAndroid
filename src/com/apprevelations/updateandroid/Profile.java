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
		if (o == this) {
			return true;
		}

		if (!(o instanceof Profile)) {
			return false;
		}

		Profile profile = (Profile) o;

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
