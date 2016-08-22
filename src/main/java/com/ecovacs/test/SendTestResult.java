package com.ecovacs.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by ecosqa on 16/8/19.
 * send e-mail for test result
 */
public class SendTestResult {

    private static Logger logger = LoggerFactory.getLogger(SendTestResult.class);

    /**
     * delete folder and subfolder
     * @param dir the folder to delete
     * @return if true delete successfully, otherwise is not
     */
    private boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            if(children == null){
                return false;
            }
            //recursion delete subfolder
            for(String strFile:children) {
                boolean success = deleteDir(new File(dir, strFile));
                if (!success) {
                    return false;
                }
            }
        }
        //delete empty folder or file
        return dir.delete();
    }

    /**
     * compress report
     * @param strToZipPath to compress folder
     * @return path of compressed file
     */

    private String compressReport(String strToZipPath){
        String strPath = "";
        boolean bResult = false;
        try {
            String strSrcPath = strToZipPath + "/Reports";
            //delete old folder, make new
            File fileFolder = new File(strSrcPath);
            if(fileFolder.exists()){
                if(!deleteDir(fileFolder)){
                    logger.error("****compressReport****delete folder failed!!!" + fileFolder);
                    return strPath;
                }
            }
            if(!fileFolder.mkdir()){
                logger.error("****compressReport****make folder failed!!!" + fileFolder);
                return strPath;
            }
            String strHtmlPath = strToZipPath + "/surefire-reports/html";
            String strLogPath = strToZipPath + "/logs";
            String strScreenShotPath = strToZipPath + "/screenShots";

            //copy log/screenshot/report to destination folder
            if(!ZipUtil.copyFolder(strHtmlPath, strSrcPath + "/html")){
                logger.error("****compressReport****copy folder failed: " + strHtmlPath);
            }
            if (!ZipUtil.copyFolder(strLogPath, strSrcPath + "/logs")){
                logger.error("****compressReport****copy folder failed: " + strLogPath);
            }
            if (!ZipUtil.copyFolder(strScreenShotPath, strSrcPath + "/screenShots")){
                logger.error("****compressReport****copy folder failed: " + strScreenShotPath);
            }
            //compress destination folder
            bResult = ZipUtil.zip(strSrcPath, strToZipPath, "zipReport.zip");
        }catch (Exception e){
            e.printStackTrace();
        }
        if (bResult){
            strPath = strToZipPath + "/" + "zipReport.zip";
        }else {
            logger.error("****compressReport****Compress report failed!!!");
        }
        return strPath;
    }

    /**
     * send e-mail for compress report
     * @param strToZipPath to compress source path
     * @return true if send successfully, otherwise is false
     */
    private boolean sendEmail(String strToZipPath){
        String strZipPath = compressReport(strToZipPath);
        if(strZipPath.length() == 0){
            logger.error("Can not get compressed path!!!");
            return false;
        }
        //smtp.sina.com
        String smtp = PropertyData.getProperty("mail-smtp");
        String from = PropertyData.getProperty("mail-from");
        String to = PropertyData.getProperty("mail-to");
        String subject = "test";
        String content = "test content";
        String username = PropertyData.getProperty("mail-username");
        String password = PropertyData.getProperty("mail-password");

        return MailSender.sendAndCcAndAttach(smtp, from, to, "", subject, content, username, password, strZipPath);
    }

    public static void main(String[] args){
        if(args.length == 0){
            logger.error("input parameter is null!!!");
            return;
        }
        SendTestResult sendTestResult = new SendTestResult();
        sendTestResult.sendEmail(args[0]);
    }
}
