package com.ecovacs.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

/**
 * Created by lily.shan on 2016/6/2.
 * class for send e-mail
 */
class MailSender {

    private static Logger logger = LoggerFactory.getLogger(MailSender.class);
    //object MIME
    private MimeMessage mimeMsg;
    //system property
    private Properties props;
    //private boolean needAuth = false;
    private String username;
    private String password;
    //object of Multipart, including mail content,tile,attachment object MimeMessage
    private Multipart mp;

    /**
     * Constructor
     * @param strSmtpServer String
     */
    private MailSender(String strSmtpServer){
        setSmtpHost(strSmtpServer);
        createMimeMessage();
    }

    /**
     * Set mail server
     * @param hostName String
     */
    private void setSmtpHost(String hostName) {
        System.out.println("Config system property：mail.smtp.host = " + hostName);
        if(props == null){
            //get system property
            props = System.getProperties();
        }
        //set smtp host
        props.put("mail.smtp.host", hostName);
    }

    /**
     * Create MIME
     * @return boolean
     */
    private boolean createMimeMessage()
    {
        Session session;
        try {
            System.out.println("Ready to get object of mail session！");
            //get mail session
            session = Session.getDefaultInstance(props, null);
        }
        catch(Exception e){
            System.err.println("Getting mail session error！" + e);
            return false;
        }

        System.out.println("Ready to create object of MIME！");
        try {
            //create MiME
            mimeMsg = new MimeMessage(session);
            mp = new MimeMultipart();
            return true;
        } catch(Exception e){
            System.err.println("Create object of MIME failed!" + e);
            return false;
        }
    }

    /**
     * Config verify SMTP
     * @param bNeed boolean
     */
    private void setNeedAuth(boolean bNeed) {
        System.out.println("Config smtp identification：mail.smtp.auth = " + bNeed);
        if(props == null){
            props = System.getProperties();
        }
        if(bNeed){
            props.put("mail.smtp.auth","true");
        }else{
            props.put("mail.smtp.auth","false");
        }
    }

    /**
     * Set username/password
     * @param name String
     * @param pass String
     */
    private void setNamePass(String name, String pass) {
        username = name;
        password = pass;
    }

    /**
     * Set subject
     * @param mailSubject String
     * @return boolean
     */
    private boolean setSubject(String mailSubject) {
        System.out.println("Set mail subject!");
        try{
            mimeMsg.setSubject(mailSubject);
            return true;
        }catch(Exception e) {
            System.err.println("set mail subject error!");
            return false;
        }
    }

    /**
     * set mail body
     * @param mailBody String
     * @return boolean
     */
    private boolean setBody(String mailBody) {
        try{
            BodyPart bodyPart = new MimeBodyPart();
            bodyPart.setContent("" + mailBody, "text/html; charset=GBK");
            mp.addBodyPart(bodyPart);
            return true;
        } catch(Exception e){
            System.err.println("set mail body error!" + e);
            return false;
        }
    }
    /**
     * Adding attachment
     * @param fileName String
     * @return boolean
     */
    private boolean addFileAffix(String fileName) {

        System.out.println("Add attachment:" + fileName);
        try{
            BodyPart bodyPart = new MimeBodyPart();
            FileDataSource fileds = new FileDataSource(fileName);
            bodyPart.setDataHandler(new DataHandler(fileds));
            bodyPart.setFileName(fileds.getName());
            mp.addBodyPart(bodyPart);
            return true;
        } catch(Exception e){
            System.err.println("Add attachment:" + fileName + "error!" + e);
            return false;
        }
    }

    /**
     * Configure mail sender
     * @param strFrom String
     * @return boolean
     */
    private boolean setFrom(String strFrom) {
        System.out.println("Set mail sender!");
        try{
            //Set mail sender
            mimeMsg.setFrom(new InternetAddress(strFrom));
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    /**
     * set mail receiver
     * @param strTo String
     * @return boolean
     */

    private boolean setTo(String strTo){
        if(strTo == null){
            return false;
        }
        try{
            mimeMsg.setRecipients(Message.RecipientType.TO,InternetAddress.parse(strTo));
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    /**
     * Set CopyTo
     * @param strCopyTo String
     * @return boolean
     */

    private boolean setCopyTo(String strCopyTo)
    {
        if(strCopyTo == null || strCopyTo.length() == 0){
            return false;
        }
        try{
            mimeMsg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(strCopyTo));
            return true;
        }catch(Exception e){
            return false;
        }
    }

    /**
     * Send mail
     * @return boolean
     */
    private boolean sendOut()
    {
        try{
            mimeMsg.setContent(mp);
            mimeMsg.saveChanges();
            System.out.println("Sending mail....");
            logger.info("Sending mail....");
            Session mailSession = Session.getInstance(props, null);
            mailSession.setDebug(true);
            Transport transport = mailSession.getTransport("smtp");
            transport.connect((String)props.get("mail.smtp.host"), username, password);
            transport.sendMessage(mimeMsg, mimeMsg.getRecipients(Message.RecipientType.TO));
            //transport.sendMessage(mimeMsg, mimeMsg.getRecipients(Message.RecipientType.CC));
            //transport.send(mimeMsg);
            System.out.println("send email successfully!");
            logger.info("send email successfully!");
            transport.close();
            return true;
        } catch(Exception e) {
            System.err.println("Send mail failed!" + e);
            logger.info("Send mail failed!" + e);
            return false;
        }
    }

    /*
     * Send mail by sendOut()
     * @param strSmtp String
     * @param strFrom String
     * @param strTo String
     * @param strSubject String
     * @param strContent String
     * @param strUserName String
     * @param strPassword String
     * @return boolean
     */
    /*public static boolean send(String strSmtp, String strFrom, String strTo,
                               String strSubject, String strContent,
                               String strUserName,String strPassword) {
        MailSender mailSender = new MailSender(strSmtp);
        //Need to verify
        mailSender.setNeedAuth(true);

        if(!mailSender.setSubject(strSubject)){
            return false;
        }
        if(!mailSender.setBody(strContent)){
            return false;
        }
        if(!mailSender.setTo(strTo)) {
            return false;
        }
        if(!mailSender.setFrom(strFrom)) {
            return false;
        }
        mailSender.setNamePass(strUserName, strPassword);

        return mailSender.sendOut();
    }*/

    /*
     * Send mail with Copy to
     * @param strSmtp String
     * @param strFrom String
     * @param strTo String
     * @param strCopyto String
     * @param strSubject String
     * @param strContent String
     * @param strUsername String
     * @param strPassword String
     * @return boolean
     */
    /*public static boolean sendAndCc(String strSmtp, String strFrom, String strTo,
                                    String strCopyto, String strSubject, String strContent,
                                    String strUsername, String strPassword) {
        MailSender mailSender = new MailSender(strSmtp);
        //Need to verify
        mailSender.setNeedAuth(true);

        if(!mailSender.setSubject(strSubject)){
            return false;
        }
        if(!mailSender.setBody(strContent)) {
            return false;
        }
        if(!mailSender.setTo(strTo)) {
            return false;
        }
        if(!mailSender.setCopyTo(strCopyto)) {
            return false;
        }
        if(!mailSender.setFrom(strFrom)) {
            return false;
        }
        mailSender.setNamePass(strUsername, strPassword);

        return mailSender.sendOut();
    }*/

    /*
     * send mail with attachment
     * @param strSmtp String
     * @param strFrom String
     * @param strTo String
     * @param strSubject String
     * @param strContent String
     * @param strUsername String
     * @param strPassword String
     * @param strFilename String
     * @return boolean
     */
    /*public static boolean sendAndAttach(String strSmtp, String strFrom, String strTo,
                                        String strSubject, String strContent, String strUsername,
                                        String strPassword, String strFilename) {
        MailSender mailSender = new MailSender(strSmtp);
        //Need to verify
        mailSender.setNeedAuth(true);

        if(!mailSender.setSubject(strSubject)) {
            return false;
        }
        if(!mailSender.setBody(strContent)) {
            return false;
        }
        if(!mailSender.addFileAffix(strFilename)) {
            return false;
        }
        if(!mailSender.setTo(strTo)) {
            return false;
        }
        if(!mailSender.setFrom(strFrom)) {
            return false;
        }
        mailSender.setNamePass(strUsername, strPassword);

        return mailSender.sendOut();
    }*/

    /**
     * send mail with cc and attachment
     * @param strSmtp String
     * @param strFrom String
     * @param strTo String
     * @param strCopyto String
     * @param strSubject String
     * @param strContent String
     * @param strUsername String
     * @param strPassword String
     * @param strFilename String
     * @return boolean
     */
     static boolean sendAndCcAndAttach(String strSmtp, String strFrom, String strTo,
                                    String strCopyto, String strSubject, String strContent,
                                    String strUsername,String strPassword,String strFilename){
        MailSender mailSender = new MailSender(strSmtp);
        //Need to verify
        mailSender.setNeedAuth(true);

        if(!mailSender.setSubject(strSubject)){
            return false;
        }
        if(!mailSender.setBody(strContent)){
            return false;
        }
        if(strFilename.length() != 0){
            if(!mailSender.addFileAffix(strFilename)){
                return false;
            }
        }
        if(!mailSender.setTo(strTo)) {
            return false;
        }
        //if strCopyto is null, do not send cc
        if(strCopyto.length() != 0){
            if(!mailSender.setCopyTo(strCopyto)) {
                return false;
            }
        }
        if(!mailSender.setFrom(strFrom)) {
            return false;
        }
        mailSender.setNamePass(strUsername, strPassword);

        return mailSender.sendOut();
    }

}
