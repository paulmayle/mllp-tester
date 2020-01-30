package edu.ucla.mednet.iss.automation.controllers;

import edu.ucla.mednet.iss.automation.SendConnection;
import java.util.ArrayList;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class ControllerFuseMessageSend {
  private static Logger log = Logger.getLogger(ControllerFuseMessageSend.class.getName());

  final static String cannedMessage = "MSH|^~\\&|||||20180420150320.028-0700||ADT^A01^ADT_A01|1101|T|2.6\r";

  @Autowired
 // private ApplicationContext appContext;
  private SendConnection sendConnection;


  public ControllerFuseMessageSend(SendConnection sendConnection) {
    log.fine("Constructor called");
    if (sendConnection != null) {
      log.fine("Set  sendConnection");
      this.sendConnection = sendConnection;
    }
  }


  @RequestMapping(value = "/send", produces = {MediaType.TEXT_HTML_VALUE})
  public String start(

      @RequestParam(name = "sendPort", required = false, defaultValue = "") String sendPort,
      @RequestParam(name = "sendHost", required = false, defaultValue = "") String sendHost,
      @RequestParam(name = "hl7Message", required = false, defaultValue = cannedMessage) String hl7Message,
      @RequestParam(name = "submit", required = false, defaultValue = "none") ArrayList action,
      Model model) throws Exception {

    sendConnection.setHost(sendHost);
    sendConnection.setPort(sendPort);
    sendConnection.setHl7Message(hl7Message);
    String response="";

    if (action.size() > 1) {
      switch (((String) action.get(1)).toLowerCase()) {
        case "connect":
          sendConnection.closeConnection();
          sendConnection.setConnectedRequestState(true);
          break;
        case "disconnect":
          sendConnection.closeConnection();
          sendConnection.setConnectedRequestState(false);
          break;
        case "send":
          sendConnection.setConnectedRequestState(true);
          response = sendConnection.sendMessage();
          log.info("Send request");
          break;
      }
    }

    model.addAttribute("sendPort", sendPort);
    model.addAttribute("sendHost", sendHost);
    model.addAttribute("hl7Message", hl7Message);
    model.addAttribute("hl7Response", response);
    return "mllp-send-display";
  }


  @RequestMapping("/sendUpdate")
  public String getsendUpdate(Model model) {

    String message = "not connected";
    String fragment="fragments :: disConnectState";

    if (sendConnection == null) {
      log.info("connection is NULL");
    } else {
      if (sendConnection.getConnectedRequestState()) {
          sendConnection.openConnection();
      } else {
        if (sendConnection.isConnected()) {
          sendConnection.closeConnection();
        }
      }
      if (sendConnection.isConnected()) {
        log.fine(" connected ");
        message = "connected";
        fragment="fragments :: connectState";
      } else {
        message = "not connected";
        fragment="fragments :: disConnectState";
      }
    }
    log.fine("setting message to  : " + message);
    model.addAttribute("sendMessage", message);
    log.fine(sendConnection.getConnection() + " is returning " + fragment);
    return fragment;
  }

//  public void setAppContext(ApplicationContext appContext) {
//    this.appContext = appContext;
//  }
}
