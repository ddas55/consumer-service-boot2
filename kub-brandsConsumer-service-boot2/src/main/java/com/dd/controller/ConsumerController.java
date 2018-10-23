package com.dd.controller;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.dd.data.AppStatusInfo;
import com.dd.data.Brand;

@RestController
@RequestMapping("/consumer")
@CrossOrigin
public class ConsumerController {
	
	private static final Logger logger = LoggerFactory.getLogger(ConsumerController.class);
	 private static final String V2_CONTENT_TYPE = "application/vnd.consumer.appinfo.v2+json";
	
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
	    return String.valueOf(System.currentTimeMillis());
	}
	
	@RequestMapping("/rediness")
	public String rediness() {
		return String.valueOf(System.currentTimeMillis());
	}
	
	@RequestMapping("/appinfo")
	//public ResponseEntity<?> appinfo(@RequestHeader("Authorization") String access_token) {
	public ResponseEntity<?> appinfoRaw() {
		AppStatusInfo appstatus = getAppStatus(null);
    	logger.info("@@ HelloController.appinfo:" + random + "." + hit);
    	return new ResponseEntity<AppStatusInfo>(appstatus,HttpStatus.OK);
	}
	
	@RequestMapping(value="/appinfo" ,method = RequestMethod.GET,  produces = V2_CONTENT_TYPE)
	public ResponseEntity<?> appinfoV2() {
		AppStatusInfo appstatus = getAppStatus("New Version-V2");
    	logger.info("@@ HelloController.appinfo.V2:" + random + "." + hit);
    	return new ResponseEntity<AppStatusInfo>(appstatus,HttpStatus.OK);
	}
	
	@RequestMapping("/appinfomoved")
	public ResponseEntity<?> appinfoMoved() {
		String str="This version of the API is no longer supported, please use appinfo";
     	return new ResponseEntity<String>(str,HttpStatus.MOVED_PERMANENTLY);
	}
	 
	 @RequestMapping("/brandclient")
	 public ResponseEntity<?> hitAndBrands(
			 @RequestHeader Map<String, String> header
			 ) {
		 System.out.println("@@  ConsumerController Calling @@");
		 for (String key : header.keySet()) {
			 logger.info("@@ ConsumerController.header key:" + key + ",value=" + header.get(key));
			 System.out.println("@@ SOP ConsumerController.header key:" + key + ",value=" + header.get(key));
		 }
		 
		 String access_token=null;
		 //public ResponseEntity<?> hitAndBrands(@RequestHeader("Authorization") String access_token) {
		 //logger.info("@@ ConsumerController.@RequestHeader xreq:" + xreq + " ,xtraceid:" + xtraceid
		//		 + ",xspanid:" + xspanid + ",xparentspanid:" + xparentspanid
		//		 + ",xsampled:" + xsampled + ",xflags:" + xflags + ",xotspan:" + xotspan);
		 logger.info("@@ ConsumerController.hitAndBrands access_token:" + access_token);
	 	AppStatusInfo appstatus = getAppStatus(null);
	 	Brand[] brands=null;
		MultiValueMap<String, String> headers=null;
		try {
			headers= new LinkedMultiValueMap<>();
			/*
			headers.add("Authorization", "Bearer " + access_token);
			headers.add("x-request-id", xreq);
			headers.add("x-b3-traceid", xtraceid);
			headers.add("x-b3-spanid", xspanid);
			headers.add("x-b3-parentspanid", xparentspanid);
			headers.add("x-b3-sampled", xsampled);
			headers.add("x-b3-flags", xflags);
			headers.add("x-ot-span-context", xotspan);
			*/
		
			logger.info("@@ ConsumerController.hitAndBrands access_token:" + access_token);
			logger.info("@@ ConsumerController.hitAndBrands appinfo:" + appstatus);
			//brands=brandsFromProducer(headers);
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
    	AppStatusInfo appstatus = getAppStatus(null);
    	logger.info("## hw.calling svc-brands:" + svcBrandUrl);
    	logger.info("## Hit:" + hit);
    	logger.info("## hw.random:" + random);
    	Brand[] brands=brandsFromProducer(null);
    	appstatus.setBrands(brands);
       	return getHTML(appstatus);
    }
    
    private Brand[] brandsFromProducer(MultiValueMap<String, String> headers) {
    	Brand[] brands =null;
       	headers.add("Content-Type", "application/json");
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
    
    private AppStatusInfo getAppStatus(String ver) {
		hit++;
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    	String tm=ft.format(System.currentTimeMillis());
    	AppStatusInfo appstatus = new AppStatusInfo();
    	appstatus.setHit(String.valueOf(hit));
    	appstatus.setRandom(String.valueOf(random));
    	appstatus.setAppname(applicationName);
    	appstatus.setTime(tm);
    	if(null!=ver) {
    		appstatus.setVersion(ver);
    	}else {
    		appstatus.setVersion(version);
    	}
    	
    	return appstatus;
    }

}
