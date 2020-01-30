package edu.ucla.mednet.iss.automation;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Logger;

public class SendConnectionImpl implements SendConnection {
  private static Logger log = Logger.getLogger(SendConnectionImpl.class.getName());
  Socket clientSocket;
  String hl7Message;
  String host;
  int port = -1;
  boolean connectedRequestState=false;

  public void setHl7Message(String hl7Message) {
    this.hl7Message = hl7Message;
  }

  @Override
  public void setHost(String host) {
    if(this.host != host) {
      // host has changed so disconnect
      closeConnection();
      this.host = host;
    }
  }


  @Override
  public void setPort(String port) {

    int iPort = 0;
    try {
      iPort = Integer.parseInt(port);

    } catch (NumberFormatException nfe) {

    }
    if(iPort!=this.port) {
      // port has changed so disconnect
      closeConnection();
      this.port = iPort;
    }
  }


  public String getConnection(){
    return host + ":" + port;
  }

  @Override
  public boolean getConnectedRequestState() {
    return connectedRequestState;
  }

  @Override
  public void setConnectedRequestState(boolean c) {
    connectedRequestState=c;
  }

  @Override
  public String sendMessage() {
    DataOutputStream out;
    BufferedReader in;
    String resp = "nothing received";
    openConnection();

    try {
      out = new DataOutputStream(clientSocket.getOutputStream());
      in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      log.info("Send message: " + hl7Message);
      out.write(wrapMessage(hl7Message));
      out.flush();
      log.info("Wait up to 15 seconds for reply");
      clientSocket.setSoTimeout(15000);  // wait for 15 seconds
      resp = unwrapResponse(in.readLine());
      log.fine("Got reply: " + resp);
    } catch (IOException e) {
      log.info("IO error " + e);
    }
    return resp;
  }

  void printInHex(String arg) {
    for (int i = 0; i < arg.length(); i++) {
      String ch = arg.substring(i, i + 1);
      System.out.print(String.format("%02x ", new BigInteger(1, ch.getBytes())));
    }

  }

  private String unwrapResponse(String readLine) {

    //printInHex(readLine);
    readLine = readLine.substring(1);
    return readLine;
  }

  private byte[] wrapMessage(String hl7Message) {
    byte[] b = hl7Message.replace("\n", "\r").replace("\r\r", "\r").getBytes();
    byte[] c = new byte[b.length + 3];
    c[0] = 0x0b;
    for (int i = 0; i < b.length; i++) {
      c[i + 1] = b[i];
    }
    c[b.length + 1] = 0x1c;
    c[b.length + 2] = '\r';
    return c;
  }




  @Override
  public void openConnection() {

    if (host == null || host.isEmpty() || port == -1) {
      log.fine("wait for both host and port to be set");
    } else {
      log.fine("=================open connection ===============   ");
      if (isConnected()) {
        log.fine("already connected.....   ");
      } else {
        try {
          log.fine("  opening a new connection .....   ");
          clientSocket = new Socket(host, port);
        } catch (IOException ex) {
          log.fine("CLOSE socket due to : " + ex);
          closeConnection();
        }
      }
    }
  }

  @Override
  public void closeConnection() {

    try {
      if (clientSocket != null) {
        clientSocket.close();
      }
    } catch (Exception e) {
      log.info("Close failed " + e);
    }
  }

  @Override
  public boolean isConnected() {

    InputStream ip = null;
    boolean connectState = true;
    log.fine(" checking connection ");
    if (clientSocket == null) {
      log.fine(" clientSocket is null  ");
      connectState = false;
    } else if (!clientSocket.isConnected()) {
      log.fine(" not connected ");
      connectState = false;
    } else if (clientSocket.isClosed()) {
      log.fine("clientSocket is closed  ");
      connectState = false;
    }

    if (connectState != false) {  // looks like we are connected so try a read to be sure ...
      try {
        ip = clientSocket.getInputStream();
        clientSocket.setSoTimeout(10);  // not really expecting to read anything so just wait a few milliseconds
        log.fine(" .. read .. ");
        int q = ip.read();
        if (q == -1) {  // this is the real test, as -1 means the other end has dropped the connection
          log.fine("connect state is down");
          connectState = false;
        }
        log.fine("read=" + q);
      } catch (Exception e) {
        log.fine("Exception=" + e);
      }
    }
    log.fine("Returning connect state = " + connectState);
    return connectState;
  }
}
