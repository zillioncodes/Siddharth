package code;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

public class EnglishStatusListener implements StatusListener{

	private static final transient Logger LOG = LoggerFactory.getLogger(EnglishStatusListener.class);
	
	@Override
	public void onException(Exception arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDeletionNotice(StatusDeletionNotice arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScrubGeo(long arg0, long arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStallWarning(StallWarning arg0) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Use log4j to write the Tweet Text to disk. Note that 
	 * we only collect Tweet Text content. If you want to collect
	 * additional data, the Status object has several methods including
	 * getUser(), getGeolocation(), and so on. Twitter4j has documentation on
	 * their website on these.
	 */
	@Override
	public void onStatus(Status status) {
		String tweetText = status.getText();
	//	status.getGeoLocation();
		if(isEnglish(tweetText)&&status.getUser().getName()!=null) {
			LOG.info(status.getText()+","+status.getUser().getName()+","+status.getUser().getLocation()+","+status.getRetweetCount()+","+status.getUser().getFollowersCount());
		}
	}

	@Override
	public void onTrackLimitationNotice(int arg0) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Our hack for filtering English tweets. While we could get the language of the user
	 * object returned by status.getUser(), people with an English language set still tweet
	 * in other languages. We simply check if the Tweet text contains non-ASCII characters;
	 * if it does, we do not collect it.
	 * @param tweetText
	 * @return true if tweetText contains no non-ASCII characters, false otherwise
	 */
	public static boolean isEnglish(String tweetText) {
		for(int i = 0;i < tweetText.length();i++) {
			int c = tweetText.charAt(i);
			if(c > 127) {
				return false;
			}
		}
		return true;
	}

}
