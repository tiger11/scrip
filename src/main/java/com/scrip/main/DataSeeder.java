package com.scrip.main;

import java.util.Calendar;
import java.util.Date;

import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.SessionExpiryHook;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.User;

public class DataSeeder {
	
	public static void main(String[] args) throws Exception, KiteException{
		
		KiteConnect kiteSdk = new KiteConnect("2wt9irxkrndqayws", true);
		kiteSdk.setUserId("ZN8831");

		/* First you should get request_token, public_token using kitconnect login and then use request_token, public_token, api_secret to make any kiteconnect api call.
		Get login url. Use this url in webview to login user, after authenticating user you will get requestToken. Use the same to get accessToken. */
		String url = kiteSdk.getLoginURL();
		System.out.println(url);
		
		// Get accessToken as follows,
		User user =  kiteSdk.generateSession("Rgwf65fFcQ5AanjmGLd0QWC20oTLUxVH", "rvp8edbmhkqegq7ra31di2kgzxyonl9k");

		// Set request token and public token which are obtained from login process.
		kiteSdk.setAccessToken(user.accessToken);
		kiteSdk.setPublicToken(user.publicToken);
		
		System.out.println(user.accessToken);
		System.out.println(user.publicToken);
		
		// Set session expiry callback.
		kiteSdk.setSessionExpiryHook(new SessionExpiryHook() {
		    public void sessionExpired() {
		        System.out.println("session expired");
		    }
		});
		
		Calendar cal = Calendar.getInstance();
		Date toDate = cal.getTime();
		
		cal.add(Calendar.YEAR, -1);
		Date fromDate = cal.getTime();
		
		
		/*
		 * HistoricalData data = kiteSdk.getHistoricalData(fromDate, toDate, "408065",
		 * "day", false); for(HistoricalData dataVal : data.dataArrayList) {
		 * System.out.println(dataVal.timeStamp); }
		 */
	}

}
