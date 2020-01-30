package edu.ucla.mednet.iss.automation;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import org.apache.camel.Route;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.spring.SpringCamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public class ReceiveConnectionImpl implements ReceiveConnection {
  @Autowired
  private ApplicationContext appContext;
  String routeId;
  String sessionIdFileName;
  int lastPort = 0;
  String portMessage;
  private static Logger log = Logger.getLogger(ReceiveConnection.class.getName());

  @Override
  public void processAction(List action, String listenPort, String sessionId) throws Exception {
    this.sessionIdFileName = "mllp-received-" + sessionId + ".hl7";
    this.routeId="mllp-in-" + sessionId;
    if (action.size() > 1) {
      switch (((String) action.get(1)).toLowerCase()) {

        case "none":
          log.info("Action=none");
          portMessage = "Select the port to use";
          break;
        case "stop":
          log.info("Action=stop");
          stopCamelRoute();
          portMessage = "Select the port to use";
          lastPort=0;
          break;
        case "port":
          int port = 0;
          try {
            port = Integer.parseInt(listenPort);
          } catch (NumberFormatException nfe) {
            port = 0;
          }
          log.info("Action=port->" + port);
          if (port < 1000) {
            portMessage = "Port " + port + " is reserved by the operation system - try a port over 1000";
          } else {
            if (port != lastPort) {
              lastPort = port;
              stopCamelRoute();
              if (isPortAvailable(port)) {
                startCamelRoute(port);
              } else {
                log.info("Port in use");
                portMessage = "Port " + port + " is in use. Try another port";
              }
            }
          }
      }
    }
  }


  @Override
  public String getPortMessage() {
    return portMessage;
  }

  @Override
  public void stopCamelRoute() throws Exception {
    SpringCamelContext camelContext = (SpringCamelContext) appContext.getBean("camelContext");
    Route route = camelContext.getRoute(routeId);
    log.info("stopping route " + routeId);
    if (route != null) {
      log.info("Stopping  listener on port " + lastPort);
      camelContext.stopRoute(routeId);
      camelContext.removeRoute(route);
    }
    deleteFile(sessionIdFileName);
  }

  @Override
  public void startCamelRoute(int port) throws Exception {
    SpringCamelContext camelContext = (SpringCamelContext) appContext.getBean("camelContext");
    ListenerRouteBuilder listenerRouteBuilder = new ListenerRouteBuilder();
    log.info("starting route " + routeId);
    listenerRouteBuilder.createRoute(camelContext, port, sessionIdFileName, routeId);
    Route route = camelContext.getRoute(routeId);
    log.info("Starting  listener on port " + port);
    portMessage = "Listening on port " + port;
    camelContext.addRoute(route);
    List<RouteDefinition> routeDefinitions = camelContext.getRouteDefinitions();
    for(RouteDefinition routeDefinition : routeDefinitions){
      System.out.println(routeDefinition.getShutdownRoute());
    }
    camelContext.startRoute(routeId);

  }

  boolean isPortAvailable(int port) {
    try (Socket ignored = new Socket("localhost", port)) {
      return false;
    } catch (IOException ignored) {
      return true;
    }
  }

  @Override
  public List getMessage() {
    List message = new ArrayList();
    message.add("---waiting---");
    try {
      message = readFileInList(sessionIdFileName);
    } catch (Exception ex) {

    }
    return message;

  }


  public static List<String> readFileInList(String fileName) {

    List<String> lines = Collections.emptyList();
    try {
      String dir="/tmp";
      lines =
          Files.readAllLines( Paths.get( dir, fileName), StandardCharsets.UTF_8);
    } catch (IOException e) {

    }
    return lines;
  }


  public static void deleteFile(String fileName) {

    new File("/tmp/" + fileName).delete();
  }


}
