package com.dd.acceptance;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;



public class ApplicationE2E{
	private final Logger logger = LoggerFactory.getLogger(ApplicationE2E.class);
	private String host="localhost";//"192.168.99.100";
    private int port = 8060;//31004
    private RestTemplate restTemplate;
    private URL baseURL;

    @Before()
    public void setUp() throws Exception {
    	if(null!=System.getProperty("host")) {
    		host = System.getProperty("host");
    	}
    	if(null!=System.getProperty("port")) {
    		port = Integer.parseInt(System.getProperty("port"));
    	}
      	logger.info("************ k8sHost:" + host + " , k8sSvcNodePort:" + port);
        // replace that with UAT server host
    	//http://192.168.99.100:31001/svcconsumer/consumer/brandclient
        this.baseURL = new URL("http://" + host + ":" + port + "/svcconsumer/");
        // disable proxy if you wanna run locally
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("userproxy.glb.ebc.local", port));
        requestFactory.setProxy(proxy);
        restTemplate = new RestTemplate();
    }
    // example of true end to end call which call UAT real endpoint
    
    @Test
    public void test_consume_brands() {
    	String url = baseURL + "consumer/brandclient";
    	logger.info("************ ApplicationE2E URL:" +url);
    	MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
         headers.add("Content-Type", "application/json");
         headers.add("Authorization", "tokenxxx");
         ResponseEntity<String> entity = restTemplate.exchange(
        		 url, HttpMethod.GET, new HttpEntity<Object>(headers),String.class);
         logger.info("************ ApplicationE2E entity:" +entity.getBody());
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        //assertTrue(restTemplate.getForEntity(url, String.class,httpEntity).getStatusCode().is2xxSuccessful());
    }


}