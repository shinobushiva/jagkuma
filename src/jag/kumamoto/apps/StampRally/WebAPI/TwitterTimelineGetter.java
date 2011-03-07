package jag.kumamoto.apps.StampRally.WebAPI;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterTimelineGetter {
	private final String ConsumerKey = "D4kDw9f2fDfr4FaiF2dbA";
	private final String ConsumerSecret = "cnlmYy52gnghiyqSRMJdduUAsZtr5TOwpgIowWz36c";

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

	private Queue<String> queue = new LinkedList<String>();

	public String getTweet() {
		if (!queue.isEmpty())
			return queue.poll();
		else
			return null;
	}

	public void requestTweet(boolean allowInReply, boolean allowReTweet) {
		try {
			ResponseList<Status> timeline = mTwitter.getUserTimeline(mUserName);
			Collections.reverse(timeline);
			queue.clear();

			for (Status status : timeline) {
				if (!allowInReply && status.getInReplyToStatusId() != -1) {
					// リプライのステータスは無視
					continue;
				} else if (!allowReTweet && status.isRetweet()) {
					// リツイートのステータスは無視
					continue;
				}
				if (status.getId() > mMaxId) {
					mMaxId = status.getId();
					queue.offer(status.getText());
				}
			}
		} catch (TwitterException e) {
			e.printStackTrace();
		}

	}

	public String getNewestTweet(boolean allowInReply, boolean allowReTweet) {
		try {
			ResponseList<Status> timeline = mTwitter.getUserTimeline(mUserName);
			for (Status status : timeline) {
				if (!allowInReply && status.getInReplyToStatusId() != -1) {
					// リプライのステータスは無視
					continue;
				} else if (!allowReTweet && status.isRetweet()) {
					// リツイートのステータスは無視
					continue;
				}
				if (status.getId() > mMaxId) {
					mMaxId = status.getId();
					return status.getText();
				}
			}
		} catch (TwitterException e) {
			e.printStackTrace();
		}

		return null;
	}

}
