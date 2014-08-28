package auth;

import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class ConfigBuilder {
	private static Configuration config;
	
	private static final String API_KEY = "HbxhQJoP136en1TT04E1wQ";
	private static final String API_SECRET = "DAcxFm3raXMm6bp7elDF9pzY4vKSssN18Be7fLEeJVY";
	private static final String ACCESS_TOKEN = "427596393-1LsZdu8HevjTb0GLZInRIrQQEFvnKh86PrtwX0B1";
	private static final String ACCESS_SECRET = "VfAutkEksMlSSznr8i5CDlVPjAmxBxyxFI2fH9peW4f2g";
	
	static {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true);
		cb.setOAuthConsumerKey(API_KEY);
		cb.setOAuthConsumerSecret(API_SECRET);
		cb.setOAuthAccessToken(ACCESS_TOKEN);
		cb.setOAuthAccessTokenSecret(ACCESS_SECRET);
		config = cb.build();
	}
	
	public static Configuration getConfig() {
		return config;
	}
}
