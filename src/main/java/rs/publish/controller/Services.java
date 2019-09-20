package rs.publish.controller;

import java.util.Optional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import rs.publish.models.FinalResult;
import rs.publish.models.LoginResponse;
import rs.publish.models.RSPublishConverters;

@RestController
public class Services {
	
	private final Logger log = LoggerFactory.getLogger(Services.class);
	
 @Autowired
 private RSPublishConverters rsPublishConverter;
 /*
  * search-> get all Entities details query by q
 */
	@RequestMapping("/services/search")
	public FinalResult search(@RequestParam("q") String q) {
		FinalResult finalresult = new FinalResult();
		try {
			if (q != null)
				if (q.indexOf('(') != -1 && q.indexOf(')') != -1) {
					q = q.substring(1, q.length() - 1);
				}
				finalresult = rsPublishConverter.EntitiesDetails(q);
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("------- FinalResult ------ Exception -----Result "+ex.getMessage());
		}
		return (finalresult);
	}
	/*
	  * login-> login validation by userNmae and password
	 */
	@RequestMapping("/services/login")
	public LoginResponse login(@RequestParam("returnProfile") Optional<Boolean> returnProfile,@RequestParam("username") String username, @RequestParam("password") String password) {
		LoginResponse loginResponse = new LoginResponse();
		try {
			loginResponse = rsPublishConverter.LoginDetails(username,password);
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("------- LoginResponse ---- Result "+ex.getMessage());
		}
		return (loginResponse);
	}
}
