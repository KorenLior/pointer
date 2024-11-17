package router_app;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

//import workerVerticle.workerVerticle;

public class router_app extends AbstractVerticle {
  private Boolean loggedIn = false;
  private HashMap<String,String> usersMap = new HashMap<String, String>();
  @Override
  public void start(Future<Void> fut) {
	  try {
		initMap();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  vertx.deployVerticle(workerVerticle.class.getName());
	  // Create a router object.
	  Router router = Router.router(vertx);
	  router.route().handler(BodyHandler.create());
	  router.route("/").handler(routingContext -> {
	      HttpServerResponse response = routingContext.response();
	      response
	          .putHeader("content-type", "text/html")
	          .end("<h1>Hello from my first Vert.x 3 application</h1>");
	    });
	  router.put("/login").handler(this::handleLogin);
	  router.put("/logout").handler(this::handleLogout);
	  router.put("/addOrder").handler(this::handleAddOrder);
	  router.get("/getOrders").handler(this::handleListOrders);
	  // Create the HTTP server and pass the "accept" method to the request handler.
	  vertx
	      .createHttpServer()
	      .requestHandler(router::accept)
	      .listen(
	          // Retrieve the port from the configuration,
	          // default to 8080.
	          config().getInteger("http.port", 8080),
	          result -> {
	            if (result.succeeded()) {
	              fut.complete();
	            } else {
	              fut.fail(result.cause());
	            }
	          }
	      );
	 }
  
  private void initMap() throws FileNotFoundException {
	BufferedReader reader;
	
	try {
		reader = new BufferedReader(new FileReader("users.csv"));
		String line = reader.readLine();
	
		while (line != null) {
			String arr[] = line.split(",");
			usersMap.put(arr[0], arr[1]);
			// read next line
					line = reader.readLine();
				}
	
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
  }
  
  private void handleLogin(RoutingContext routingContext) {
	  JsonObject user = routingContext.getBodyAsJson();
	  String passwd = user.getString("passwd");
	  String uname = user.getString("uname");
	  if (uname.equals("")){
		  return;
	  }
	  if (usersMap.get(uname).equals(passwd)) {
		  this.loggedIn = true;
	  }
	  return;
  }
  
  private void handleLogout(RoutingContext routingContext) {
	  this.loggedIn = false;
	  return;
  }
  
  private void handleAddOrder(RoutingContext routingContext) {
	    String productID = routingContext.request().getParam("productID");
	    HttpServerResponse response = routingContext.response();
	    if (productID == null) {
	      sendError(400, response);
	    } else {
	      JsonObject product = routingContext.getBodyAsJson();
	      if (product == null) {
	        sendError(400, response);
	      } else {
	    	  if (this.loggedIn){
	    	  vertx.eventBus().send("workerVerticle",product, reply->{
	    		  routingContext.request().response().end((String)reply.result().body());
	    	  });}
	    	  response.end();
	      }
	    }
	  }
  
  private void handleListOrders(RoutingContext routingContext) {
	    String productID = routingContext.request().getParam("productID");
	    HttpServerResponse response = routingContext.response();
	    if (productID == null) {
	    	vertx.eventBus().send("workerVerticle","", reply->{
	    		  routingContext.request().response().end((String)reply.result().body());});
	    	response
  	        .putHeader("content-type", "text/html")
  	        .end("<h1>".concat(response.getStatusMessage()).concat("<h1>"));
	    	
	      //sendError(400, response);
	    } else {
	      JsonObject query = routingContext.getBodyAsJson();
	      if (query == null) {
	    	  response
  	        .putHeader("content-type", "text/html")
  	        .end("<h1>OrderLIST</h1>");
	    	  vertx.eventBus().send("workerVerticle","", reply->{
	    		  routingContext.request().response().end((String)reply.result().body());
	    	  });
	      } else {
	    	  // this doesnt work yet
	    	  response
	  	        .putHeader("content-type", "text/html")
	  	        .end("<h1>OrderLIST2222</h1>");
	    	  vertx.eventBus().send("workerVerticle",query, reply->{
	    		  routingContext.request().response().end((String)reply.result().body());
	    	  });
	    	  response.end();
	      }
	    }
	  }
  
  private void sendError(int statusCode, HttpServerResponse response) {
	    response.setStatusCode(statusCode).end();
	  }
}
