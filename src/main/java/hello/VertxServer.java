package hello;


import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;

public class VertxServer extends AbstractVerticle {

	public void start() {
		HomerService homerService = new HomerService();

		HttpServer server = vertx.createHttpServer();
		Router router = Router.router(vertx);

		router.route("/async/").handler(routingContext -> {
			HttpServerResponse response = routingContext.response();

			homerService.process((r, throwable) -> {
				if (throwable == null) {
					response.end(r);
				} else {
					routingContext.fail(throwable);
				}
			});

		});
		server.requestHandler(router::accept).listen(8080);
	}

}