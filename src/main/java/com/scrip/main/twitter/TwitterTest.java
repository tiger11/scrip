package com.scrip.main.twitter;

import java.io.File;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class TwitterTest {

	public static void main(String[] args) throws TwitterException {

		Twitter twitter = TwitterFactory.getSingleton();
		StatusUpdate status =  new StatusUpdate("New to Twitter API world!");
		status.setMedia(new File("C:\\Users\\gaubhard\\Downloads\\spring-tool-suite-4-4.1.1.RELEASE-e4.10.0-win32.win32.x86_64\\sts-4.1.1.RELEASE\\workspace\\scrip\\file.jpeg"));
		
		twitter.updateStatus(status);
		
	    System.out.println("Successfully updated the status to [" + status.getStatus() + "].");
	}

}
