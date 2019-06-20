package ru.testingHttpBin;

import org.junit.Test;
import org.junit.Assert;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.net.URL;
import java.net.HttpURLConnection;
//import javax.net.ssl.HttpsURLConnection;
import java.net.SocketTimeoutException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileOutputStream;

import sun.awt.image.*;


public class HttpBinTester {
	
	private final String USER_AGENT = "Testing_Machine";
	
	@Test
	public void doGetDelay() throws Exception {
		// GET request, delayed for 5 seconds
		String delayURL = "https://httpbin.org/delay/5";
		URL url = new URL(delayURL);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		
		// fill in request fields
		connection.setRequestMethod("GET");
		connection.setRequestProperty("User-Agent", USER_AGENT);
		connection.setReadTimeout(5200); // 5200 - because of time spent to get to the httpbin.org and so on
		
		// do request and manage it
		// request and read response code
		try {
			int responceCode = connection.getResponseCode();
			Assert.assertTrue("Response code isn`t 'OK'!", responceCode == 200);
			System.out.println("responceCode: " + responceCode);
		} catch (SocketTimeoutException e) {
			Assert.fail("Delayed responce came too late!");
		}
		
		BufferedReader buffer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		// read response body
		String inputLine;
		StringBuffer responseLine = new StringBuffer();
		while ((inputLine = buffer.readLine()) != null) {
			responseLine.append(inputLine).append("\n");
		}
		buffer.close();
		// parse response
		JSONParser parser = new JSONParser();
		JSONObject response = (JSONObject) parser.parse(responseLine.toString());
				
		boolean isURLRight = delayURL.equals(response.get("url"));
		boolean isUserAgentRight = USER_AGENT.equals( ((JSONObject) response.get("headers")).get("User-Agent") );
		
		Assert.assertTrue("Sent back response has wrong parameters!", isURLRight & isUserAgentRight);
	}
	
	@Test
	public void doGetPNG() throws Exception {
		// GET request with PNG
		String pngURL = "https://httpbin.org/image/png";
		URL url = new URL(pngURL);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				
		// fill in request fields
		connection.setRequestMethod("GET");
		connection.setRequestProperty("User-Agent", USER_AGENT);
		
		// do request and manage it
		// request and read response code
		int responceCode = connection.getResponseCode();
		Assert.assertTrue("Response code isn`t 'OK'!", responceCode == 200);
		System.out.println("responceCode: " + responceCode);
		
		Assert.assertTrue("Content type is not 'image/png'!", ((String) connection.getContentType()).equals("image/png") );
		Assert.assertTrue("Reterned content type is not image!", connection.getContent() instanceof URLImageSource);
		
		// this code just writes the image on hard drive
		File dir = new File(".//received_images/");
		dir.mkdir();
		File image = new File(".//received_images/image_png.png");
		image.createNewFile();
		OutputStream imageStream = new FileOutputStream(image);
		BufferedInputStream inputStream = new BufferedInputStream(connection.getInputStream());
		
		int byteRead;
		while ( (byteRead = inputStream.read()) != -1 ) {
			imageStream.write(byteRead);
		}
		imageStream.close();
		inputStream.close();
				
	}
	
}
