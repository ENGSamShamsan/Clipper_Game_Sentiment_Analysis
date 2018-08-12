package GameSent.hints;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import GameSent.utilities.TweetWritable;

/**
 * The SentimentReducer processes scored tweet data and finds the highest and the lowest scoring tweets.
 * 
 * 
 * The goal of the SentimentReducer is to find the maximum and minimum scoring tweets for each topic.
 * 
 * In this first version of Sentiment processing, we are only processing one topic - tweets about the Clippers on the
 * evening of May 1, 2017
 * 
 * 
 * @author <Sam Shamsan>
 *
 */

public class SentimentReducer extends Reducer<Text, TweetWritable, Text, TweetWritable> {
	private static final Log LOG = LogFactory.getLog(SentimentReducer.class);

	public static int maxScore = Integer.MIN_VALUE;
	public static int minScore = Integer.MAX_VALUE;

	public static TweetWritable tweet, maxTweet, minTweet;
	public static boolean testing = true;

	/**
	 * Each time reduce runs it processes the scored tweets for one key
	 * 
	 * Keys are Tweet topics for a specific time-frame (an evening in May, for instance)
	 * 
	 */
	@Override
	protected void reduce(Text key, Iterable<TweetWritable> tweetList, Context context)
			throws IOException, InterruptedException {
			
		// Iterate through the TweetWritables in the tweetList
		/*
		 * TODO implement - start your loop here
		 */
		 
		for (TweetWritable Twt : tweetList) {
			// find the maximum and minimum scores and their associated tweets
			/*
			 * TODO implement - set maxScore and maxTweet as needed
			 * 
			 */
			int CurrentScore=(int)Twt.getScore();
			String CurrentTweet=Twt.getTweet();
			if (CurrentScore>maxScore) {
				maxScore=CurrentScore;
				maxTweet = new TweetWritable(Twt);
	           }
			   if (CurrentScore<minScore) {
				   minScore=CurrentScore;
				minTweet = new TweetWritable(Twt);
	           }
		   /*
			 * TODO implement - end your loop here
			 */
		}
		
		

		/*-
		 * Use context.write to write out 
		 * 
		 * 		1) the topic and the maxTweet 
		 * 		2) the topic and the minTweet
		 * 
		 */
		LOG.info("Minimum tweet"+ minTweet);

		String topic = key.toString();

		if (testing)
			System.err.println("Printing out max:  " + maxTweet.toString());
		key.set(topic + "(maximum score): ");
		context.write(key, maxTweet);

		if (testing)
			System.err.println("Printing out min:  " + minTweet.toString());
		key.set(topic + "(minimum score): ");
		context.write(key, minTweet);
	}

}
