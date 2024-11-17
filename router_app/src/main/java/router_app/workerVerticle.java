package router_app;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class workerVerticle extends AbstractVerticle {
  private List<Object> orders = new ArrayList<>();
  @Override
  public void start(Future<Void> fut) {
	  vertx.eventBus().consumer("workerVerticle", (message) -> {
		  if (message.body().equals(null)) {
			  System.out.println("whatwhatorderlistwhat2222");
			  vertx
		        .createHttpServer()
		        .requestHandler(r -> {
		        	r.response().end("<h1>zzzzzzzzzzzzzzzzzzzzzzzz</h1>");
		        })
		        .listen(8080, result -> {
		          if (result.succeeded()) {
		            fut.complete();
		          } else {
		            fut.fail(result.cause());
		          }
		        });
		  }
		  else {
			  // orders.add(message.body());
			  System.out.println("whatwhatorderlistwhat");
			  vertx
		        .createHttpServer()
		        .requestHandler(r -> {
		        	r.response().setStatusMessage("yay").end();
		        })
		        .listen(8080, result -> {
		          if (result.succeeded()) {
		            fut.complete();
		          } else {
		            fut.fail(result.cause());
		          }
		        });
			  
		  }
      });
  }
  
}