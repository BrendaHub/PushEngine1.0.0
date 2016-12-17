package com.binggou.sms.mission.core.about.util;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by XYZ on 16/12/15.
 */
public class ConnectionFactory {

    public ConnectionFactory() {}

    public static String repath= "datasource.properties";
    public static ComboPooledDataSource cpds = new ComboPooledDataSource();
    public static boolean CommitOnClose=false;
    public static void createDataSource(){
        FileInputStream in = null;
        InputStream inputStream = null;
        try{
//            repath=repath;
//            Properties p=new Properties();
//            in=new FileInputStream(repath);
//            p.load(in);

            ClassLoader cl = ConnectionFactory.class.getClassLoader();
            if  (cl !=  null ) {
                inputStream = cl.getResourceAsStream( "datasource.properties" );
            }  else {
                inputStream = ClassLoader.getSystemResourceAsStream( "datasource.properties" );
            }
            Properties p =  new  Properties();
            p.load(inputStream);

            System.out.println(">???? =" + p.getProperty("c3p0.DriverClass"));

            cpds.setDriverClass(p.getProperty("c3p0.DriverClass"));
            cpds.setJdbcUrl(p.getProperty("c3p0.JdbcUrl"));
            cpds.setUser(p.getProperty("c3p0.User"));
            cpds.setPassword(p.getProperty("c3p0.Password"));
            cpds.setInitialPoolSize(Integer.parseInt(p.getProperty("c3p0.InitialPoolSize")));
            cpds.setMinPoolSize(Integer.parseInt(p.getProperty("c3p0.minPoolSize")));
            cpds.setMaxPoolSize(Integer.parseInt(p.getProperty("c3p0.maxPoolSize")));
            cpds.setMaxIdleTime(Integer.parseInt(p.getProperty("c3p0.maxIdleTime")));
            cpds.setAcquireIncrement(Integer.parseInt(p.getProperty("c3p0.AcquireIncrement")));
            cpds.setIdleConnectionTestPeriod(Integer.parseInt(p.getProperty("c3p0.idleConnectionTestPeriod")));
            cpds.setPreferredTestQuery(p.getProperty("PreferredTestQuery"));
            String iscommit=p.getProperty("c3p0.autoCommitOnClose");
            if(iscommit.equals("false")){
                CommitOnClose=false;
            } else if(iscommit.equals("true")){
                CommitOnClose=true;
            }
//            in.close();
        } catch(Exception e){
            e.printStackTrace();
            System.exit(0);
        }finally {
            if(inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /*
    public static void createDataSource(){


    }
*/
    public static Connection getConnection(){
        try{
            Connection conn=cpds.getConnection();
            if(!CommitOnClose){
                conn.setAutoCommit(CommitOnClose);
            }
            return conn;
        } catch(SQLException e){
//            log.error(e.toString());
            e.printStackTrace();
            return null;
        }
    }

}
