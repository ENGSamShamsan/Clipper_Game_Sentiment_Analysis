Clipper game - Sentiment Analysis
Game played: Aug 06,2017
Files Used in This Project
Project archive: ​Clipper_Game_Sentiment_Analysis.zip 
Test data: ​clippers_5_1
Lookup table:​ AFINN-111
In this project, we are going to write a MapReduce MR2 job to scour Tweets for sentiment. we have a file, ​clippers_5_1​, containing tweets downloaded from Twitter. These tweets
were created on Sunday night, right after the Clippers game.
we also have a lookup table in a file, ​AFINN_111​, for scoring the “sentiment” expressed by a word. Here are the first few lines of the lookup file:
abandoned -2 abandons -2 abducted -2 abduction -2
Obviously, many words are chosen because they elicit an emotion, rather than simply expressing it. In general, the words are emotive.
Overview of processing
In the file, there are several hundred tweets. Tell me, how do people feel? Are they happy? Are they sad? We’re going to find the “happiest” and the “unhappiest” tweets.
our program will scan every word in every input tweet to find out. It will discover which words express or elicit a feeling by using a lookup table. The lookup table contains about 2500 words, each associated with an emotion.
In the lookup table, each emotive word has a score. For instance, the word1 ​‘lmao’​ is considered to be a very positive word with a score of ​4​.
1 yes, in our lookup table, things like ‘lmao’ are considered to be words. To learn more, check out AFIN-111 on Wikipedia.
   
 For each tweet, our program is going
1) to find the emotive words and get their score from the lookup table.
2) sum the scores for each tweet. A tweet that includes “lucky”, “good”, “amazing” will probably have a high score and another tweet that includes “choked”, “failed”, “bamboozled” will have a low score.
Once all the tweets are scored, our program will score all tweets for each input file and find the tweet that is the most positive (maximum score) and find the tweet that is the most negative (minimum score).
At the end, our job will write a file to HDFS containing the most positive and negative tweets, along with their scores.
Requirements for the Mapper:
In the setup method:
● Setup a lookup service for checking the score of a given word.
● Use the input split and find out the name of the file being processed.
In the map method:
read each line in the input file, clippers_5_1, and extract the tweet’s text. Process as follows:
● For each word in the tweet, get its score using the lookup table. If the lookup table does not contain the word, the score for the word should be zero.
○ Sum the scores to a total for each tweet
○ Count the number of words
○ Count the number of times a word in the tweet was found in the lookup table (called hits).
● For each tweet, output the following
○ key​: name of the input data file
○ value​: the tweet, the total score, the word count and the hit count

 Requirements for the Reducer:
In the reduce method:
● Read in the scored tweets - the ​key​ is the filename, the ​value​ is a list of scoredTweets.
● iterate through the list of scored tweets and find the tweet with the highest score and the tweet with the lowest score
● output the max scoring tweet and the min scoring tweet. our tasks
● Start our VM and start Eclipse (use the workspace already provided - /home/student/Desktop/workspace)
● Examine the contents of the ​Clipper_Game_Sentiment_Analysis.zip  ​project
○ hints​ - a package containing the src code that may help us with our project.
○ utilities​ - a package that contains two classes we can use: ​LookupService and ​TweetWritable
○ data​ - a folder containing input test data
○ lookup_tables​ - a folder containing lookup tables....
 
 Create a ​SentimentDriver
Before creating the Job object, add the name of the lookup file to the Configuration using
 ●
getConf().set(LOOKUP_PROPERTY_NAME, new File(lookupTable).getName());
Proceed to setup the job in the usual way, instantiating the Job with the configuration and then use the the standard Job setters, including
○ setJarByClass
○ setInputFormatClass
○ setMapperClass and setReducerClass
○ setMapOutputKeyClass and setMapOutputValueClass
○ setOutputKeyClass and setOutputValueClass
Cache the lookup table:
○
 URI[] cacheFiles = { new URI(“​/lookup_tables/AFIN-111”​) };
 job.setCacheFiles(cacheFiles);

 SentimentMapper​ extends Mapper.
setup: override the setup method in Mapper:
● create a ​LookupService​ for sentiment by following these steps:
○ get the name of the lookup table from the Configuration using:
          context.getConfiguration().get(<name of property>, "AFINN-111");
○ access the file cache and get the file for the lookupTable
URI files[] = context.getCacheFiles();
○ create a new ​LookupService​ and initialize the LookupService using the lookupTable’s file - see ​utilities.LookupService
● access the input split to get the name of the input data file as follows:
Path inputPath = ((FileSplit) context.getInputSplit()).getPath();
save the inputPath to use as a key in mpa map: override the map method in Mapper:
● Read a line into a String
● Use SentimentMapper.​parseText​ function to get the tweet.
○ return from map if the tweet returned by parseText is null.
● Split the tweet into words.
● For each word, use the lookupService to check if the word has a score. If so, add that word’s score to the total score for the tweet. If not, add nothing to the total score.
● Count the number of words and emotional words in the tweet.
● After all the words have been processed,
○ write out a Text key and a TweetWritable value.
○ key: a Text that contains the name of the input file - see ​setup​ function.
○ value: a new ​TweetWritable​. Create a new TweetWritable and use its setters: setTweet, setScore, setSize (the number of words in the tweet) and setNHits(the number of emotivel words found in the tweet).
   
 Create a ​SentimentReducer​ that extends Reducer.
● read in the key and the list of TweetWritables.
● initialize maxScore and minScore.
● iterate through the list of TweetWritable,
○ If the tweet’s score > maxScore,
■ set maxScore = score
■ maxTweet = new TweetWritable(tweetWritable)2
○ Similarly for minScore.
● After all the tweets are processed, use ​context.write​ to
○ write out the key and the most positive tweet
○ write out the key and the most negative tweet
 2 A common problem working with the input lists for reducer - we read through the list and decide to keep a value and we ​think​ you have stored a value and it is gone! That’s because, when you iterate through an Iterable in Hadoop, you are working with a ​transient​ list. That is, after an element in a list is read, it disappears into the ether. That means, as you process through a list, if you want to keep element in that list, you have to clone it into a new object.
Hence, instead of saying maxTweet = tweetWritable -- which is the usual way to do things, we store a newly created tweetWritable as follows: maxTweet=new TweetWritable(tweetWritable.)

 What to hand-in:
Submit your code to the official course website using the homework submission tool.
Submit your java code only - please do not turn in your class files or anything else. I expect three classes:
● SentimentDriver.java
● SentimentMapper.java ● SentimentReducer.java
If you decide to rewrite the TweetWritable.java or LookupService.java code, please turn in that, too. I hope you won’t need to.
If you are more sophisticated and want to put the Driver, Mapper and Reducer all in one file, no problem.
Finally.... what should the results be?
Don’t get too fussy about how your output looks.
Make sure the output shows two tweets, one with a positive score around 7 and the other with a negative score around -9.
Your output should just contain two lines... If you find more than one max or min, I’d worry.
