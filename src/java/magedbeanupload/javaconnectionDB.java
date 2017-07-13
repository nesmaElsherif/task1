/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package magedbeanupload;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Nesma
 */
public class javaconnectionDB {

    
    static Connection con = null;
    public  static Connection openConnection() throws FileNotFoundException {

        Properties props = new Properties();
     //   FileInputStream fis = null;
     	InputStream  fis = null;
       ServletContext servletContext = null;

        
        try {
     
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream input = classLoader.getResourceAsStream("db.properties");
            props.load(input); 
            // Class.forName("oracle.jdbc.driver.OracleDriver");
            Class.forName(props.getProperty("DB_DRIVER_CLASS"));
            // con = DriverManager.getConnection(
            //   "jdbc:oracle:thin:@localhost:1521:orcl", "efada", "efada123");
            con = DriverManager.getConnection(props.getProperty("DB_URL"),
                props.getProperty("DB_USERNAME"),
                props.getProperty("DB_PASSWORD"));
            return con;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            Logger.getLogger(javaconnectionDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public boolean closeConnection() {
        try {

            con.close();
            System.out.println("Closed");
            return true;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return false;
    }

}
