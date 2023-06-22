package vo;

import java.io.Serializable;

public class ProfileSearchParameters implements Serializable {
	private String profileName;

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
}
