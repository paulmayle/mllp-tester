package edu.ucla.mednet.iss.automation.controllers;

import edu.ucla.mednet.iss.automation.ReceiveConnection;
import java.util.ArrayList;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class ControllerFuseMessageReceive {

  private static Logger log = Logger.getLogger(ControllerFuseMessageReceive.class.getName());

  @Autowired
  private ReceiveConnection receiveConnection;
  //private ApplicationContext appContext;



  @RequestMapping(value = "/listen", produces = {MediaType.TEXT_HTML_VALUE})
  public String start(

      HttpSession session,
      @RequestParam(name = "listenPort", required = false, defaultValue = "") String listenPort,
      @RequestParam(name = "submit", required = false, defaultValue = "none") ArrayList action,
      Model model) throws Exception {

    receiveConnection.processAction(action,listenPort, session.getId());
    model.addAttribute("listenPort", listenPort);
    return "mllp-receive-display";
  }


  @RequestMapping("/lastMessage")
  public String getlastMessage(Model model) {
    model.addAttribute("message", receiveConnection.getMessage());
    model.addAttribute("portMessage", receiveConnection.getPortMessage());

    return "fragments :: lastMessage";
  }

//  public void setAppContext(ApplicationContext appContext) {
//    this.appContext = appContext;
//  }

}
