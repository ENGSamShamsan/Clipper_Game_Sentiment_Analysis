package GameSent.hints;

import java.io.File;
import java.net.URI;
import java.time.Instant;
import java.time.LocalDateTime;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;


import GameSent.utilities.TweetWritable;

/**
 * 
 * There are a few new things in this driver.
 * 
 * --------------------------------------------------------
 * 
 * First, you may notice that
 * 
 * SentimentDriver extends Configured implements Tool
 * 
 * The driver also contains a run method. I'm introducing a best practice in Hadoop MR2. This practice makes it possible
 * define a variety of different processing options when you are running at the command line. We will explore this more
 * later, but we might as well start implementing Drivers as Tools now..
 * 
 * --------------------------------------------------------
 * 
 * Second, there is now a LOG object available.
 * 
 * Whenever you want to leave a message in the Job's log files, you can do so in the standard way: LOG.info, Log.warn,
 * LOG.error, LOG.fatal and LOG.debug.
 * 
 * --------------------------------------------------------
 * 
 * Third, we are adding a parameter for the Mapper as a property to the Configuration.
 * 
 * To add a property, we have to use getConf() to get the Configuration and then use its setter. Below, we are setting a
 * Configuration property that indicates the location of the cached lookup table.
 * 
 * --------------------------------------------------------
 * 
 * Fourth, we are caching the lookup table so that Mappers and/or Reducers can use it.
 * 
 * Caching is simple, you merely pass the location of the file(s) you want to cache to the Job.setCacheFiles function.
 * 
 * 
 * 
 * 
 * @author <Sam Shamsan>
 *
 */
public class SentimentDriver extends Configured implements Tool {

	private static final String LOOKUP_PROPERTY_NAME = "lookupTable";

	private static final Log LOG = LogFactory.getLog(SentimentDriver.class);

	public static void main(String[] args) throws Exception {

		int exitCode = ToolRunner.run(new SentimentDriver(), args);
		System.out.println("Job completed with status:  " + exitCode);
	}

	@Override
	public int run(String[] args) throws Exception {

		// your program should take an input path, output path and lookup table
		if (args.length != 3) {
			for (String arg : args)
				System.err.println(arg);
			System.err.println("Make sure your Run Configuration has three arguments:"
					+ "\n\t<input path> <output path> <path to lookup table>" + "\nFor example:"
					+ "\n\t  data/sentiment/clippers_5_1   outout/sentiment_test   lookup_tables/AFINN-111");
			System.exit(0);
		}
		String input = args[0];

		/*
		 * add a timestamp to your output, so you can run several tests
		 */
		String output = args[1] + "_" + Instant.now().toEpochMilli();
		String lookupTable = args[2];

		LOG.info(
				"---- Sentiment Analysis using Tweets about the Clippers and Finn's affective valence lookup table ------- ");
				/*
				 * For extra credit, use the logger to log your name and group
				 */
				/*
				 * TODO implement
				 */
		LOG.info(" Name: Sam Shamsan in Group : Sam ");


		/*-
		 * Add a property to the Configuration table so the cached file can be found
		 * 
		 * Remember:
		 *    use "Configuration conf = getConf();" to get the Configuration object
		 *    use "conf.set(myPropertyName, lookupTableName);"
		 *
		 *               TODO implement
		 */
		 Configuration conf = getConf();
			File LookupFil= new File (lookupTable);
			conf.set("lookupTable", LookupFil.getName());
			 
		// define the job and set it up so the code for the job can be located by Hadoop
		Job job = Job.getInstance(getConf(), "Sentiment");
		job.setJarByClass(SentimentDriver.class);

		/* Set the job's Mapper and Reducer classes */
		/*
		 * TODO implement
		 */
		
		job.setMapperClass(SentimentMapper.class);
		 
		job.setReducerClass(SentimentReducer.class);
		/* Use the usual input format class - this will parse one record at a time out of the input data file */
		job.setInputFormatClass(TextInputFormat.class);

		/*-
		 * Cache the lookup table so the Mapper can access it.
		 * 
		 * Use the job.setCacheFiles(..) function. Here's an example:
		 * 
		 * URI[] cacheFiles = { new URI(lookupTable) }; 
		 * job.setCacheFiles(cacheFiles);
		 * 
		 * 
		 */
		/*
		 * TODO implement
		 */

		URI[] cacheFiles = { new URI(lookupTable) }; 
		 job.setCacheFiles(cacheFiles);

		/*
		 * Specify the job's output key and value classes.
		 * 
		 * Note, we have a new Writable, that was written for processing Tweets.
		 * 
		 * At the end of the job, your program should be writing out, for each topic given, the tweet information for
		 * the tweets that are most positive and most negative.
		 * 
		 */
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(TweetWritable.class);

		FileInputFormat.addInputPath(job, new Path(input));
		FileOutputFormat.setOutputPath(job, new Path(output));

		boolean result = job.waitForCompletion(true);
		return (result) ? 0 : 1;
	}

}