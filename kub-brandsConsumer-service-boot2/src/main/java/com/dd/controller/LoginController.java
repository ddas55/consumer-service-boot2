package com.dd.controller;

import java.util.Base64;
import java.util.Base64.Encoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.dd.data.Login;

import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;

@RestController
@RequestMapping("/login")
@CrossOrigin
public class LoginController {
	
	@Value("${consumerapp.authurl}")
	private String authurl;
		
	@Value("${consumerapp.service.producer.secret}")
	private String srvProducerScret;
	
	@Autowired
	RestTemplate restTemplate;
	
	
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	
	//{"username":"ddas","password":"password1"}
	//http://localhost:8090/svcone/login/auth
	@PostMapping(path="/auth")
	public ResponseEntity<?> login(@RequestBody Login login) {
		if(null==login || StringUtils.isEmpty(login.getUsername())
				|| StringUtils.isEmpty(login.getPassword())) {
			return new ResponseEntity(null,HttpStatus.UNAUTHORIZED);
		}
		logger.info("** LoginController.login.authurl:" + authurl + ",username:" + login.getUsername()
				+ ",password" + login.getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
		map.add("grant_type", "password");
		map.add("scope", "webclient");
		map.add("username", login.getUsername());//ddas
		map.add("password", login.getPassword());//password1

		String tokenDetails=null;
		try {
			byte[] plainCredsBytes = srvProducerScret.getBytes();
			Encoder encoder = Base64.getEncoder();
			byte[] base64CredsBytes = encoder.encode(plainCredsBytes);
			String base64Creds = new String(base64CredsBytes);
			headers.add("Authorization", "Basic " + base64Creds);
			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
					
			ResponseEntity<String> response = restTemplate.postForEntity( authurl, request , String.class );
			tokenDetails = response.getBody();
			
			logger.info("** LoginController.login.tokenDetails:" + tokenDetails);
			return new ResponseEntity<String>(tokenDetails,HttpStatus.OK);
		} catch (RestClientException e) {
			e.printStackTrace();
		}
    	return new ResponseEntity(null,HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	 

}
