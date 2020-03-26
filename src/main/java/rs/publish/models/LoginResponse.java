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
		
		public void setLoginFaultMessage(String loginFaultMessage) {
			this.loginFaultMessage = loginFaultMessage;
		}
		
		public void setSessionId(String sessionId) {
			this.sessionId = sessionId;
		}
		
		public void setServerVersion(String serverVersion) {
			this.serverVersion = serverVersion;
		}
		
		public void setUserProfile(UserProfile userProfile) {
			this.userProfile = userProfile;
		}
}
