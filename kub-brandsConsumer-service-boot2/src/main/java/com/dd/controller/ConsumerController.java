package com.dd.controller;

import java.text.SimpleDateFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.dd.data.AppStatusInfo;
import com.dd.data.Brand;

@RestController
@RequestMapping("/consumer")
@CrossOrigin
public class ConsumerController {
	
	private static final Logger logger = LoggerFactory.getLogger(ConsumerController.class);
	
	private static int hit=0;
	private static int random=(int)(Math.random()*100);
	String newline="\\n";
	
	@Value("${spring.application.name}")
	private String applicationName;
	
	@Value("${consumerapp.version}")
	private String version;
	
	@Value("${consumerapp.svcbrands-url}")
	private String svcBrandUrl;
	
	@Autowired
	RestTemplate restTemplate;

	@RequestMapping("/healthz")
	public String healthz() {
		//logger.info("** healthz Called random:" + random);
	    return String.valueOf(System.currentTimeMillis());
	}
	
	@RequestMapping("/rediness")
	public String rediness() {
		//logger.info("** rediness Called random:" + random);
		//Load large data or configuration files during startup. 
		return String.valueOf(System.currentTimeMillis());
		//return new ResponseEntity(System.currentTimeMillis(),HttpStatus.OK);
	}
	 @RequestMapping("/appinfo")
	 public ResponseEntity<?> appinfo(@RequestHeader("Authorization") String access_token) {
		AppStatusInfo appstatus = getAppStatus();
    	logger.info("@@ HelloController.appinfo:" + random + "." + hit);
    	return new ResponseEntity<AppStatusInfo>(appstatus,HttpStatus.OK);
	
	 }
	 @RequestMapping("/brandclient")
	 public ResponseEntity<?> hitAndBrands(@RequestHeader("Authorization") String access_token) {
		 	System.out.println("@@ ConsumerController.hitAndBrands access_token:" + access_token);
		 	AppStatusInfo appstatus = getAppStatus();
	    	try {
	    		logger.info("@@ ConsumerController.hitAndBrands access_token:" + access_token);
	    		logger.info("@@ ConsumerController.hitAndBrands appinfo:" + appstatus);
	    		Brand[] brands=brandsFromProducer(access_token);
	    		if(null!=brands) {
	    			for (Brand brand : brands) {
	    				System.out.println("## HelloController.hitAndBrands.brand:" + brand);
					}
	    		}
		    	appstatus.setBrands(brands);
		    	logger.info("@@ HelloController.hitAndBrands:" + random + "." + hit);
				return new ResponseEntity<AppStatusInfo>(appstatus,HttpStatus.OK);
		
			} catch (RuntimeException e) {
				logger.error(" # allbrands.ERROR :" ,e.getMessage());
				e.printStackTrace();
			}
			return new ResponseEntity(null,HttpStatus.INTERNAL_SERVER_ERROR);
	 }
	
    @RequestMapping("/brandsui")
    public String brandsui() {
    //public String brandsui(@RequestHeader("Authorization") String access_token) {
    	AppStatusInfo appstatus = getAppStatus();
    	logger.info("## hw.calling svc-brands:" + svcBrandUrl);
    	logger.info("## Hit:" + hit);
    	logger.info("## hw.random:" + random);
    	Brand[] brands=brandsFromProducer(null);
    	appstatus.setBrands(brands);
       	return getHTML(appstatus);
    }
    
    private Brand[] brandsFromProducer(String access_token) {
    	Brand[] brands =null;
       	MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
       	headers.add("Content-Type", "application/json");
        headers.add("Authorization", "Bearer " + access_token);
        HttpEntity<String> request = new HttpEntity<String>( headers);
	   	try {
	   		ResponseEntity<Brand[]> responseEntity = restTemplate.exchange(
    				 svcBrandUrl, HttpMethod.GET, new HttpEntity<Object>(headers),Brand[].class);
    		brands = responseEntity.getBody();
    		MediaType contentType = responseEntity.getHeaders().getContentType();
    		HttpStatus statusCode = responseEntity.getStatusCode();
    		logger.info("## hw.brands:" + brands + " , contentType:" + contentType + ",statusCode:" + statusCode);
    	} catch (Exception e) {
			logger.error("## hw.ERROR :" ,e.getMessage());
			e.printStackTrace();
		}
	   	return brands;
    }
    
    private String getHTML(AppStatusInfo appstatus) {
    	Brand[] brands = appstatus.getBrands();
    	StringBuilder sb = new StringBuilder();
    	sb.append("<html>");
    		sb.append("<body>");
    		sb.append("<table>");
    		sb.append("<tr>");
    			sb.append("<td valign='top'>");
    			sb.append("<table bgcolor='#9cc3ed'");//##79ea9b
    			
    				sb.append("<tr>");
    					sb.append("<td>");sb.append("<b>HIT:</b>  " + appstatus.getHit() );sb.append("</td>");
    				sb.append("</tr>");
    				sb.append("<tr>");
						sb.append("<td>");sb.append("<b>TIME</b>:  " + appstatus.getTime() );sb.append("</td>");
					sb.append("</tr>");
					sb.append("<tr>");
						sb.append("<td>");sb.append("<b>STATIC RANDOM#</b> :  " + appstatus.getRandom());sb.append("</td>");
					sb.append("</tr>");
					sb.append("<tr>");
						sb.append("<td>");sb.append("<b>APP NAME</b> :  " + appstatus.getAppname());sb.append("</td>");
					sb.append("</tr>");
					sb.append("<tr>");
						sb.append("<td>");sb.append("<b>ADDED VERSION</b> : " + appstatus.getVersion());sb.append("</td>");
					sb.append("</tr>");
			
    			sb.append("</table>");
    			sb.append("</td>");
    			
    			sb.append("<td>");
    			sb.append("<table bgcolor='#dcccff'");
	    			if(null!=brands && brands.length>0) {
	    				
						for (Brand brand : brands) {
							sb.append("<tr>");sb.append("<td>");sb.append(brand.getBrand());sb.append("</td>");sb.append("</tr>");
						}

					}
    			sb.append("</table>");
    			sb.append("</td>");
    		
    		sb.append("</tr>");
    		sb.append("</table>");
    		sb.append("</body>");
    	sb.append("</html>");
    	return sb.toString();
    }
    
    private AppStatusInfo getAppStatus() {
		hit++;
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    	String tm=ft.format(System.currentTimeMillis());
    	AppStatusInfo appstatus = new AppStatusInfo();
    	appstatus.setHit(String.valueOf(hit));
    	appstatus.setRandom(String.valueOf(random));
    	appstatus.setAppname(applicationName);
    	appstatus.setTime(tm);
    	appstatus.setVersion(version);
    	return appstatus;
    }

}
