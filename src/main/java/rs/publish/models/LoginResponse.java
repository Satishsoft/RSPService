package rs.publish.models;

public class LoginResponse
{
	    private Boolean loginSuccess;
	    private String loginFaultMessage;
	    private String sessionId;
	    private String serverVersion;
	    private UserProfile userProfile;
	    public Boolean getLoginSuccess() {
			return loginSuccess;
		}
		public void setLoginSuccess(Boolean loginSuccess) {
			this.loginSuccess = loginSuccess;
		}
		public String getLoginFaultMessage() {
			return loginFaultMessage;
		}
		public void setLoginFaultMessage(String loginFaultMessage) {
			this.loginFaultMessage = loginFaultMessage;
		}
		public String getSessionId() {
			return sessionId;
		}
		public void setSessionId(String sessionId) {
			this.sessionId = sessionId;
		}
		public String getServerVersion() {
			return serverVersion;
		}
		public void setServerVersion(String serverVersion) {
			this.serverVersion = serverVersion;
		}
		public UserProfile getUserProfile() {
			return userProfile;
		}
		public void setUserProfile(UserProfile userProfile) {
			this.userProfile = userProfile;
		}
}
