package edu.ucla.mednet.iss.automation;

import java.util.logging.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

@Configuration
public class ApplicationContext {
  private static Logger log = Logger.getLogger(ApplicationContext.class.getName());

  @Bean
  @Scope(
      value = WebApplicationContext.SCOPE_SESSION,
      proxyMode = ScopedProxyMode.TARGET_CLASS)
  public SendConnection sendConnection() {
    log.info("Getting a new sendConnection:  SendConnectionImpl()");
    return new SendConnectionImpl();
  }


  @Bean
  @Scope(
      value = WebApplicationContext.SCOPE_SESSION,
      proxyMode = ScopedProxyMode.TARGET_CLASS)
  public ReceiveConnection receiveConnection() {
    log.info("Getting a new receiveConnection:  ReceiveConnectionImpl()");
    return new ReceiveConnectionImpl();
  }

}
