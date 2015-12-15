package hello;

import io.vertx.core.Vertx;

public class Application {

	public static void main(String[] args) {
		Vertx.vertx().deployVerticle(new VertxServer());
	}
}
