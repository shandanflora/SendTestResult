package com.ecovacs.test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by Administrator on 2015/10/27.
 * 
 */
class PropertyData {

    /*
    private static Properties props =  null;

    public static void setFile(String strFile){
        try {
            props =  new Properties();
            InputStream inputstream = PropertyData.class.getClassLoader().getResourceAsStream(strFile);
            props.load(inputstream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setProps(Properties props) {
        PropertyData.props = props;
    }

    public String getProperty(String key) {
        return props.getProperty(key);
    }
    */

    private static Properties props=new Properties();

    static{
        try {
            props.load(new InputStreamReader(PropertyData.class.getClassLoader()
                    .getResourceAsStream("commonData.properties"), "UTF-8"));
            /*props.load(PropertyData.class.getClassLoader()
                    .getResourceAsStream("commonData.properties"));*/
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private PropertyData(){}

    static String getProperty(String key){
        return props.getProperty(key);
    }

}
