package util;

import models.Institution;
import models.User;

public class UserInstitutionParameter {
	
	public static final String Admin = null;
	public Institution institution;
	public User user;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Institution getInstitution() {
		return institution;
	}

	public void setInstitution(Institution institution) {
		this.institution = institution;
	}
}
