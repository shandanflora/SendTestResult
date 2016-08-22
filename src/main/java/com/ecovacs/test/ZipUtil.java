package com.ecovacs.test;

import com.beust.jcommander.ParameterException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by ecosqa on 16/8/18.
 * utility of zip
 */
class ZipUtil {

    private static Logger logger = LoggerFactory.getLogger(ZipUtil.class);
    /**
     * recursion compress folder
     * @param srcRootDir path of source file
     * @param file destination file or folder to compress
     * @param zos ZipOutputStream
     */
    private static void zip(String srcRootDir, File file, ZipOutputStream zos) throws Exception
    {
        if (file == null)
        {
            return;
        }

        //if is file，compress file directly
        if (file.isFile())
        {
            int count, bufferLen = 1024;
            byte data[] = new byte[bufferLen];

            //get absolute path to destination folder
            String subPath = file.getAbsolutePath();
            int index = subPath.indexOf(srcRootDir);
            if (index != -1)
            {
                subPath = subPath.substring(srcRootDir.length() + File.separator.length());
            }
            ZipEntry entry = new ZipEntry(subPath);
            zos.putNextEntry(entry);
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            while ((count = bis.read(data, 0, bufferLen)) != -1)
            {
                zos.write(data, 0, count);
            }
            bis.close();
            zos.closeEntry();
        }
        //if is folder，compress all folder
        else
        {
            //compress file or subfolder in destination folder
                File[] childFileList = file.listFiles();
                if(childFileList != null){
                    for(File childFile:childFileList)
                    {
                        //childFile.getAbsolutePath().indexOf(file.getAbsolutePath());
                        zip(srcRootDir, childFile, zos);
                    }
                }
        }
    }

    /**
     * compress for file or folder
     * @param srcPath source path. if compress file, is absolute path; if compress folder, is top folder path
     * @param zipPath destination path. note: srcPath is not contain zipPath
     * @param zipFileName destination file name
     */
    static boolean zip(String srcPath, String zipPath, String zipFileName) throws Exception
    {
        if (StringUtils.isEmpty(srcPath) || StringUtils.isEmpty(zipPath) || StringUtils.isEmpty(zipFileName))
        {
            throw new ParameterException("Input parameter is null!!!");
        }
        CheckedOutputStream cos;
        ZipOutputStream zos = null;
        try
        {
            File srcFile = new File(srcPath);

            //if sub folder of source folder, throw exception(prevent endless loop)
            if (srcFile.isDirectory() && zipPath.contains(srcPath))
            {
                logger.error("srcFile is not directory or " +
                        "zipPath must not be the child directory of srcPath.");
                return false;
            }

            //if not exist folder, make directory
            File zipDir = new File(zipPath);
            if (!zipDir.exists() || !zipDir.isDirectory())
            {
                if(!zipDir.mkdirs()){
                    return false;
                }
            }

            //create object of destination file
            String zipFilePath = zipPath + File.separator + zipFileName;
            File zipFile = new File(zipFilePath);
            if (zipFile.exists())
            {
                //if not permit delete, throe SecurityException
                //SecurityManager securityManager = new SecurityManager();
                //securityManager.checkDelete(zipFilePath);
                //if exist repeat file, delete
                if(!zipFile.delete()){
                    return false;
                }
            }

            cos = new CheckedOutputStream(new FileOutputStream(zipFile), new CRC32());
            zos = new ZipOutputStream(cos);

            //if only compress one file, cut parent folder of the file
            String srcRootDir = srcPath;
            if (srcFile.isFile())
            {
                int index = srcPath.lastIndexOf(File.separator);
                if (index != -1)
                {
                    srcRootDir = srcPath.substring(0, index);
                }
            }
            //compress file or folder with recursion
            zip(srcRootDir, srcFile, zos);
            zos.flush();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (zos != null)
            {
                zos.close();
            }
        }
        return true;
    }

    /**
     * copy file or folder to destination folder
     * @param srcPath absolute path of source
     * @param destPath absolute path of destination
     */

    static boolean copyFolder(String srcPath, String destPath){

        File src = new File(srcPath);
        File dest = new File(destPath);
        if (src.isDirectory()) {
            if (!dest.exists()) {
                if(!dest.mkdir()){
                    return false;
                }
            }
            String files[] = src.list();
            if(files == null){
                return false;
            }
            for (String file : files) {
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);
                // 递归复制
                copyFolder(srcFile.getPath(), destFile.getPath());
            }
        } else {
            try {
                InputStream in = new FileInputStream(src);
                OutputStream out = new FileOutputStream(dest);

                byte[] buffer = new byte[1024];

                int length;

                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
                in.close();
                out.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return true;
    }


}
