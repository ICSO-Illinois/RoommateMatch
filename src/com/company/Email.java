package com.company;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class Email {
  // GMail user name (just the part before "@gmail.com")
  private static String USER_NAME = "illinois.chinese";
  private static String PASSWORD = "Icso03302020"; // GMail password
  private String RECIPIENT;
  private String subject;
  private String body;
  public Email(String recipient, String subject, String content) {
    this.RECIPIENT = recipient;
    this.subject = subject;
    this.body = content;
  }


  public void send() {
    String from = USER_NAME;
    String pass = PASSWORD;
    String[] to = { RECIPIENT }; // list of recipient email addresses
    if (!isEmailValid(RECIPIENT)) {
      sendFromGMail(from, pass, to, "Invalid Email", "Your email is invalid.");
      return;
    }
    sendFromGMail(from, pass, to, subject, body);
  }

  private static void sendFromGMail(String from, String pass, String[] to,
      String subject, String body) {
    Properties props = System.getProperties();
    String host = "smtp.gmail.com";
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", host);
    props.put("mail.smtp.user", from);
    props.put("mail.smtp.password", pass);
    props.put("mail.smtp.port", "587");
    props.put("mail.smtp.auth", "true");

    Session session = Session.getDefaultInstance(props);
    MimeMessage message = new MimeMessage(session);

    try {
      message.setFrom(new InternetAddress(from));
      InternetAddress[] toAddress = new InternetAddress[to.length];

      // To get the array of addresses
      for( int i = 0; i < to.length; i++ ) {
        toAddress[i] = new InternetAddress(to[i]);
      }

      for (InternetAddress address : toAddress) {
        message.addRecipient(Message.RecipientType.TO, address);
      }

      message.setSubject(subject);
      message.setText(body);
      Transport transport = session.getTransport("smtp");
      transport.connect(host, from, pass);
      transport.sendMessage(message, message.getAllRecipients());
      transport.close();
    } catch (MessagingException ae) {
      ae.printStackTrace();
    }
  }

  private static boolean isEmailValid(String email) {
    if (email == null || email.length() == 0) {
      return false;
    } else if (!email.contains("@illinois.edu")) {
      return false;
    }
    String netId = email.split("@")[0];
    return netId.length() <= 8;
  }
}