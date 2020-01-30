package edu.ucla.mednet.iss.automation.controllers;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ControllerMenus {
  private static Logger log = Logger.getLogger(ControllerMenus.class.getName());

    @Value("${pac4j.centralLogout.defaultUrl:#{null}}")
     String defaultUrl;

    @Value("${pac4j.centralLogout.logoutUrlPattern:#{null}}")
     String logoutUrlPattern;



  @RequestMapping("/")
  public String root(final Map<String, Object> map)  {
    return index(map);
  }





  @RequestMapping("/menu")
  public String menu() {
    return "menu";
  }


  @RequestMapping("/load")
  public String load(Model model) {
    return "load";
  }

  @RequestMapping("/index")
  public String index() {
    return "index";
  }

  @RequestMapping("/index.html")
  public String index(final Map<String, Object> map)  {
//    map.put("profiles", profileManager.getAll(true));
//    map.put("sessionId", webContext.getSessionStore().getOrCreateSessionId(webContext));
    return "index";
  }

  @RequestMapping("/login")
  public String login() {
    return "index";
  }

  @GetMapping("/fragments")
  public String getHome() {
    return "fragments.html";
  }



}
