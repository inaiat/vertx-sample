package hello.cache;

import com.hazelcast.core.IList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.twitter.api.SearchResults;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;

import java.util.List;

public class TwitterLoader {

    private Logger logger = LoggerFactory.getLogger(TwitterLoader.class);

    private IList<Tweet> tweetCache;

    public void loader() {
        logger.info("Start loading the cache from twitter");
        Twitter twitter = new TwitterTemplate("iOGpCj3OJ33n4aMJyBkb9vxWo", "Y5ni93pyIcYAfFWwgIomS0if3riOtwTTqaBrRRhMu4L0JoW1K3");
        final SearchResults search = twitter.searchOperations().search("#jhipster");

        final List<Tweet> tweets = search.getTweets();

        tweets.stream()
                .forEach(tweetCache::add);

        logger.info("The cache is loaded.");
    }

}
