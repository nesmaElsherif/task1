/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package magedbeanupload;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import org.primefaces.model.UploadedFile;
import org.apache.commons.io.FilenameUtils;
/**
 *
 * @author Nesma
 */
@ManagedBean
@RequestScoped
public class FileUploadView extends javaconnectionDB implements Serializable {

    private String name;
    private String address;
    private UploadedFile file;
    private int id;
    ResultSet rs = null;
    Connection con;
    private DataModel<Person> model;
    ArrayList<Person> listdb;
    
    
   
    private static final File LOCATION = new File("C:\\Users\\Nesma\\Desktop");

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public DataModel<Person> getModel() {
        return model;
    }

    public void setModel(DataModel<Person> model) {
        this.model = model;
    }

    
    
    public FileUploadView(int id) {
        this.id = id;
    }

    public FileUploadView() {
//        try {
//            listdb=getDataInDataBase();
//            // model = new ListDataModel<Person>(listdb);
//            
//            
//        } catch (SQLException ex) {
//            Logger.getLogger(FileUploadView.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(FileUploadView.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        
        
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;

    }
    public ArrayList<Person> getDataInDataBase() throws SQLException, FileNotFoundException {
        ArrayList<Person> list = new ArrayList<Person>();
        try {
            con =javaconnectionDB.openConnection();
            PreparedStatement pst = null;
            pst = con.prepareStatement("select * from  emp ");
            rs = pst.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getInt("id"));
                Person p = new Person(rs.getInt("id"), rs.getString("name"), rs.getString("address"));
                list.add(p);
                model = new ListDataModel<Person>(list);
            }
            closeConnection();
            return list;
        } catch (SQLException ex) {
            closeConnection();
            ex.printStackTrace();
            return null;
        } finally {
            closeConnection();
        }

    }
    public void readFile() throws IOException {
        if (file != null) {
            String prefix = FilenameUtils.getBaseName(file.getFileName());
            System.out.println("prefix" + prefix);
            String suffix = FilenameUtils.getExtension(file.getFileName());
            System.out.println("suffix" + suffix);
            File save = File.createTempFile(prefix + "-", "." + suffix, LOCATION);
            Files.write(save.toPath(), file.getContents());
            System.out.println(save.toPath());
            try (BufferedReader br = new BufferedReader(new FileReader(save))) {
                String sCurrentLine;
                Person person = new Person();
                while ((sCurrentLine = br.readLine()) != null) {
                    if (sCurrentLine.startsWith("name")) {
                        String[] split = sCurrentLine.split(":");
                        name = split[1];
                        person.setName(name);
                    } else if (sCurrentLine.startsWith("address")) {
                        String[] split = sCurrentLine.split(":");
                        address = split[1];
                        person.setAddress(address);
                    } else if (sCurrentLine.startsWith("id")) {
                        String[] split = sCurrentLine.split(":");
                        id = Integer.parseInt(split[1].trim());
                         person.setId(id);
                    }
                }
                insertDataInDB(person);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public boolean insertDataInDB( Person person ) {
        try {
            con = javaconnectionDB. openConnection();
            PreparedStatement pst = null;
            System.out.println("my con" + con);
            pst = con.prepareStatement("insert into emp (id, name, address)Values (?,?,?)");
            pst.setInt(1, person.getId());
            pst.setString(2, person.getName());
            pst.setString(3, person.getAddress());
            int executeUpdate = pst.executeUpdate();
            closeConnection();
            if (executeUpdate > 0) {
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileUploadView.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;

    }
    
    public String execute() {
   
    return "/ShowData.xhtml?faces-redirect=true";
     }

}
