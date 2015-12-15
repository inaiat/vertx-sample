package hello;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.IList;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.social.twitter.api.Tweet;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class VertxServer extends AbstractVerticle {

	private IList<Tweet> tweetCache;
	private ApplicationContext context;

	public void start() {
		tweetCache = (IList<Tweet>) context.getBean("tweetCache");
		final ObjectMapper objectMapper = new ObjectMapper();

		HttpServer server = vertx.createHttpServer();
		Router router = Router.router(vertx);
		router.route("/").blockingHandler(routingContext -> {
			HttpServerResponse response = routingContext.response();
			try {
				String result = objectMapper.writeValueAsString(random().get());
				response.end(result);
			} catch (JsonProcessingException e) {
				response.end();
			}
		});
		router.route("/async/").handler(routingContext -> {
			HttpServerResponse response = routingContext.response();

			CompletableFuture
				.supplyAsync(() -> random())
				.whenComplete((tweet, throwable) -> {
					if (tweet.isPresent())
						try {
							response.end(objectMapper.writeValueAsString(tweet.get()));
						} catch (JsonProcessingException e) {
							routingContext.fail(e);
						}
					else
						routingContext.fail(HttpStatus.BAD_REQUEST.value());
				});
		});
		server.requestHandler(router::accept).listen(8081);
	}

	public Optional<Tweet> random() {
		long count = tweetCache.size();
		if (count == 0) return Optional.empty();
		Random r = new Random();
		long randomIndex = count <= Integer.MAX_VALUE ? r.nextInt((int) count) :
			r.longs(1, 0, count).findFirst().orElseThrow(AssertionError::new);
		return Optional.of(tweetCache.get((int) randomIndex));
	}
}