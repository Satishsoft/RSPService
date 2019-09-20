package rs.publish.models;

public class UserProfile 
{
	 private String authorities;
	 private String username;
	 private String fullName;
	 private String userZone;
	 private String groups;
	 private String email;
	public String getAuthorities() {
		return authorities;
	}
	public void setAuthorities(String authorities) {
		this.authorities = authorities;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getUserZone() {
		return userZone;
	}
	public void setUserZone(String userZone) {
		this.userZone = userZone;
	}
	public String getGroups() {
		return groups;
	}
	public void setGroups(String groups) {
		this.groups = groups;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
}
