package edu.ucla.mednet.iss.automation;


public interface SendConnection {


  void openConnection();

  /**
   * Tests the connection.
   * @return true if connected
   */
  boolean isConnected();


  void closeConnection();

  void setPort(String port);
  void setHost(String host);

  String sendMessage();
  void setHl7Message(String hl7Message);


  String getConnection();

  boolean getConnectedRequestState();
  void setConnectedRequestState(boolean c);
}
