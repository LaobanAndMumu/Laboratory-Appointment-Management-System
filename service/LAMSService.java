package service;
import java.util.*;
import components.data.*;
import business.*;
import javax.xml.stream.*;
import javax.jws.*;

/**
* This class provides a web service
* in SOAP(XML based communication)
* for a appointment specialist to 
* manage appointments
*
* @author Yuching Sun 
*/

@WebService(serviceName="LAMSAppointmentService")
public class LAMSService{
   
   
    public static DBSingleton dbSingleton = null;
    public static BusinessLayer biz = new BusinessLayer();
    
    @WebMethod(operationName="initialize")
    public String initialize(){
   	 dbSingleton = DBSingleton.getInstance();
       dbSingleton.db.initialLoad("LAMS");
       return "Database initialized";
         
    }
    
    @WebMethod(operationName="getAllAppointments")
    public String getAllAppointments(){
        
        if( dbSingleton == null ){
           initialize();
        }

        
        List<Object> objs = new ArrayList<Object>();
        
        objs = dbSingleton.db.getData("Appointment", "");
        
        String total = "";
        try{
           if( objs.size() > 0 ){
               String xmlString = biz.appointmentXMLString( objs );
               total += xmlString; 
              
              
           }else{
               initialize();
           }
        }catch(XMLStreamException e){
        
        }
        
        return total;
    
    }
    
    @WebMethod(operationName="getAllPhysicians")
    public String getAllPhysicians(){
        
        if( dbSingleton == null ){
           initialize();
        }
        
        List<Object> objs = new ArrayList<Object>();
        
        objs = dbSingleton.db.getData("Physician", "");
        
        String total = "";
        try{
           if( objs.size() > 0 ){
               String xmlString = biz.physicianXMLString( objs );
               total += xmlString; 
              
              
           }else{
               initialize();
           }
        }catch(XMLStreamException e){
        
        }
        return total;
    
    }
    
    @WebMethod(operationName="getPhysician")
    public String getPhysician( String physician_id ){
        String message ="";
        try{
            message = biz.getPhysician( physician_id );
            
        }catch(XMLStreamException e){
        
        }
        return message;
    
    }
    
    @WebMethod(operationName="getAllPatients")
    public String getAllPatients(){
        
        if( dbSingleton == null ){
           initialize();
        }

        
                
        List<Object> objs = new ArrayList<Object>();
        
        objs = dbSingleton.db.getData("Patient", "");
        
        String total = "";
        try{
           if( objs.size() > 0 ){
               String xmlString = biz.patientXMLString( objs );
               total += xmlString; 
              
              
           }else{
               initialize();
           }
        }catch(XMLStreamException e ){
        
        }
        return total;
    
    }
    
    @WebMethod(operationName="getPatient")
    public String getPatient( String patient_id ){
        
        String message = ""; 
        try{         
            message = biz.getPatient( patient_id );
        }catch(XMLStreamException e ){
        
        }

        return message;
    
    }
    
    @WebMethod(operationName="addPatient")
    public String addPatient( String xml ){
        
       
        
        String message = ""; 
        try{         
            message = biz.addPatient( xml );
        }catch(XMLStreamException e ){
        
        }

        return message;
    
    }

    @WebMethod(operationName="addAppointment")
    public String addAppointment( String xmlString ){
        
       String error = "";
                 
       try{
            error = biz.addAppointment(xmlString);
       }catch(XMLStreamException e){
       }
       return error;
    
    }
    
    @WebMethod(operationName="getAppointment")
    public String getAppointment( String appointmentID ){
    
                  
        String appointment_id = "id='" + appointmentID + "'";
        
        String message = "";
        try{
            message = biz.getAppointment( appointmentID );
        }catch(XMLStreamException e){
        }

        return message;
    
    }
    
     @WebMethod(operationName="cancelAppointment")
     public String cancelAppointment(String apptID){
        
        String message = biz.cancelAppointment( apptID );
        return message;

         
    
     }
    
   
   
}
