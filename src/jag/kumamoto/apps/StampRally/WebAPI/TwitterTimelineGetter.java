package jag.kumamoto.apps.StampRally.WebAPI;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterTimelineGetter {
	private final String ConsumerKey = "";
	private final String ConsumerSecret =""; 

	private final String mUserName;
	private long mMaxId;
	
	private final Twitter mTwitter;
	
	public TwitterTimelineGetter(String userName, long maxId) {
		this.mUserName = userName;
		this.mMaxId = maxId;
		
		ConfigurationBuilder builder = new ConfigurationBuilder();

		// Twitterにアプリケーション登録したら付与される、
		// Consumer keyとConsumer secretをBuilderにセット。
		builder.setOAuthConsumerKey(ConsumerKey);
		builder.setOAuthConsumerSecret(ConsumerSecret);
		
		mTwitter = new TwitterFactory(builder.build()).getInstance();
	}
	
	public long getMaxId() {
		return mMaxId;
	}
	
	public String getNewestTweet(boolean allowInReply, boolean allowReTweet) {
		try {
			ResponseList<Status> timeline = mTwitter.getUserTimeline(mUserName);
			for(Status status : timeline) {
				if(!allowInReply && status.getInReplyToStatusId() != -1) {
					//リプライのステータスは無視
					continue;
				} else if(!allowReTweet && status.isRetweet()) {
					//リツイートのステータスは無視
					continue;
				}
				if(status.getId() > mMaxId) {
					mMaxId = status.getId();
					return status.getText();
				}
			}
		} catch(TwitterException e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
