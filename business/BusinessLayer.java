package business;
import javax.xml.stream.*;
import java.io.*;
import components.data.*;
import service.*;
import java.text.SimpleDateFormat;
import java.text.Format;
import java.util.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import org.xml.sax.InputSource; 
import java.util.regex.*;
import java.text.ParseException;
import java.time.LocalTime;


public class BusinessLayer{
   
   public static DBSingleton dbSingleton = DBSingleton.getInstance() ;
   
   static ArrayList<String> error_list = new ArrayList<String>();
   
   private List<Object> findObjects(String type, String condition ){
   
        List<Object> objs = new ArrayList<Object>();
        objs = dbSingleton.db.getData(type, condition );
        
                        
        return objs;

   
   }
	
   /*
   * Get a patient with the id
   * @ return response in XML format
   */
   public String getPatient(String patientID) throws XMLStreamException{
   
        String condition = "id='" + patientID + "'";
        List<Object> objs = findObjects("Patient", condition);
        
        String response = "";
        
        if( objs.size() > 0){
             response += patientXMLString( objs );
           
        }else{
             response += "<Patient>patient id not found</Patient>";
             
                         

        }
        
                
        return response; 

   }
   
   /*
   * Add new patient to the system
   *
   * @return string patient info if successfully
   */
   public String addPatient(String xml )  throws XMLStreamException{
   
         String id = dom_patient( xml );   
         String response = ""; 
            
         if( !id.equals("") ){
         
             response += getPatient( id );
                            
         }else{
             response += "<Patient>";
             
             for( int i=0; i < error_list.size(); i++){
                 
                 response += "<error>" + error_list.get( i ) + "</error>";
            
             }        
                           
             response += "</Patient>";
             

         }
         error_list = new ArrayList<String>();
         return response; 

   
   }
	
   /*
    * Create patient XML string 
    * 
    * @return string of patients in XML format
    */
   public String patientXMLString(List<Object> objs) throws XMLStreamException{
   
       String xmlString ="";
            
       StringWriter stringWriter = new StringWriter();
      
       XMLOutputFactory factory = XMLOutputFactory.newInstance();
       XMLStreamWriter writer = factory.createXMLStreamWriter(stringWriter);
      
              
       writer.writeStartDocument("UTF-8","1.0");
       
       writer.writeStartElement("PatientList");
              
       for( Object obj : objs ){
       
          Patient patient = (Patient)obj;
          
          createPatientElement( writer, patient );
                    
                 
       
       
       } 
             
       
       // end PatientList      
       writer.writeEndElement();
       
       // end document 
       writer.writeEndDocument();
       writer.flush();
       writer.close();
      
             
       xmlString += stringWriter.getBuffer().toString();          
  
       return xmlString; 
   
   }
   
  /*
   * Get the physician with a id and return with a string in XML format
   *
   * @ String
   */
   public String getPhysician(String physicianID) throws XMLStreamException{
   
        String condition = "id='" + physicianID + "'";
        List<Object> objs = findObjects("Physician", condition);
        String response = "";
        
        if( objs.size() > 0){
           response += physicianXMLString( objs );
           
        }else{
           response += "<PhysicianList>";
           response += "<error>ERROR: PHYSICIAN id does not exist</error>" ;
           response += "</PhysicianList>";
        }
        
        return response; 

   }
   
   /*
   * Create string of physician(s) in XML format 
   *
   *
   * @return string in XML format
   */
   public String physicianXMLString(List<Object> objs) throws XMLStreamException{
   
       String xmlString ="";
            
       StringWriter stringWriter = new StringWriter();
      
       XMLOutputFactory factory = XMLOutputFactory.newInstance();
       XMLStreamWriter writer = factory.createXMLStreamWriter(stringWriter);
      
              
       writer.writeStartDocument("UTF-8","1.0");
       
       writer.writeStartElement("PhysicianList");
       
       if( objs.size() > 0){       
          for( Object obj : objs ){
          
             Physician physician = (Physician)obj;
             
             createPhysicianElement( writer, physician );
             
             if( physician.getPatientCollection().size() > 0  ){
             
                   writer.writeStartElement("PatientList");
                   List<Patient> all_objs = physician.getPatientCollection();
                   
                   for( Patient patinet_obj : all_objs ){
                   
                      Patient patient = patinet_obj;
                      
                      createPatientElement( writer, patient );
                                
                             
                   
                   
                   } 
                      
                
                // end PatientList      
                writer.writeEndElement();
             }//if
             
             // end physician
             writer.writeEndElement();
             
                    
          
          
          }//for 
       }else{
            writer.writeStartElement("Error");
            writer.writeCharacters( "Physician ID does not exist" );
       		writer.writeEndElement();

       }      
       
       // end PhysicianList      
       writer.writeEndElement();
       
       // end document 
       writer.writeEndDocument();
       writer.flush();
       writer.close();
      
             
       xmlString += stringWriter.getBuffer().toString();          
  
       return xmlString; 
   
   }
   
   /*
   * Create physician element in XML format
   * @return void 
   */
   private void createPhysicianElement( XMLStreamWriter writer, Physician physician ) throws XMLStreamException {
      
      	         
		writer.writeStartElement("physician");
		writer.writeAttribute("id", physician.getId() );
		
                writer.writeStartElement("name");
		writer.writeCharacters( physician.getName() );
		writer.writeEndElement();
      
         
   }



   /*
   * Get the appointment with id
   * @return string of an appointment in XML format
   */
   public String getAppointment(String apptID) throws XMLStreamException{
        
        String condition = "id='" + apptID + "'";
        List<Object> objs = findObjects("Appointment", condition);
        
        String response = "";
        
        if( objs.size() > 0){
           response += appointmentXMLString( objs );
           
        }else{
           response += "<AppointmentList>";
                            

           response += "<error>ERROR: appointment id does not exist</error>" ;
           response += "</AppointmentList>";
        }
        
        return response; 
      
   }
   
   
   /*
   * Create a string of appointment(s) in XML format 
   *
   * @return string 
   */
   public String appointmentXMLString(List<Object> objs) throws XMLStreamException{
   
       String xmlString ="";
            
       StringWriter stringWriter = new StringWriter();
      
       XMLOutputFactory factory = XMLOutputFactory.newInstance();
       XMLStreamWriter writer = factory.createXMLStreamWriter(stringWriter);
      
              
       writer.writeStartDocument("UTF-8","1.0");
       
       writer.writeStartElement("AppointmentList");
              
       for( Object obj : objs ){
       
          Appointment appointment = (Appointment)obj;
          
          createAppointmentElement( writer, appointment);
          createPatientElement( writer, appointment.getPatientid());
          createPhlebotoElement( writer, appointment.getPhlebid() );
          createPSCElement( writer, appointment.getPscid());
          createAllLabTestElement( writer, appointment.getAppointmentLabTestCollection() );
          
          // end appointment
          writer.writeEndElement();
       
       
       
       } 
             
       
       // end AppointmentList      
       writer.writeEndElement();
       
       // end document 
       writer.writeEndDocument();
       writer.flush();
       writer.close();
      
             
       xmlString += stringWriter.getBuffer().toString();          
  
       return xmlString; 
   
   }
   
     
   /*
   * Create lab tests XML with XMLStreamWriter
   */
   private void createAllLabTestElement(XMLStreamWriter writer, List<AppointmentLabTest> labTestList ) throws XMLStreamException {
      
      writer.writeStartElement("allLabTests");
      
      for( AppointmentLabTest test : labTestList ){
         
         createEachLabTestElement(writer, test);
      
      }
      
      writer.writeEndElement();
      
      
   
   }
   
   /*
   * Create lab test element with XMLStreamWriter
   *
   */
   private void createEachLabTestElement(XMLStreamWriter writer, AppointmentLabTest labTest ) throws XMLStreamException {
   
       writer.writeEmptyElement("appointmentLabTest"); 
                  
        
       writer.writeAttribute("id", labTest.getAppointmentLabTestPK().getApptid()  ); 
       writer.writeAttribute("dxcode", labTest.getAppointmentLabTestPK().getDxcode() );
       writer.writeAttribute("labTestId", labTest.getAppointmentLabTestPK().getLabtestid()  ); 
       
   
   }

   
     

   
   /*
   * Create appointment element with XMLStreamWriter
   */
   private void createAppointmentElement(XMLStreamWriter writer, Appointment appointment) throws XMLStreamException {
      
      	         
       writer.writeStartElement("appointment"); 
       
      
       String date_format = dateTransform( appointment.getApptdate() );
      
       writer.writeAttribute("date", date_format); 
       writer.writeAttribute("id", appointment.getId() ); 
       writer.writeAttribute("time", appointment.getAppttime().toString()  ); 

   
   }
  
   /*
   * Create Patient Service Center(PSC) element with XMLStreamWriter
   */
   private void createPSCElement(XMLStreamWriter writer, PSC psc) throws XMLStreamException {
      
      	         
	writer.writeStartElement("psc");
	writer.writeAttribute("id", psc.getId() );
		
        writer.writeStartElement("name");
	writer.writeCharacters( psc.getName() );
	writer.writeEndElement();
      
      
		      
       // end patient 
       writer.writeEndElement();

   
   }

   /*
   * Create phlebotomist element with XMLStreamWriter
   */
   private void createPhlebotoElement(XMLStreamWriter writer, Phlebotomist phlebotomist) throws XMLStreamException {
      
      	         
	writer.writeStartElement("phlebotomist");
	writer.writeAttribute("id", phlebotomist.getId() );
		
        writer.writeStartElement("name");
	writer.writeCharacters( phlebotomist.getName() );
	writer.writeEndElement();
      
      
		      
        // end patient 
        writer.writeEndElement();

   
   }
   
   /*
   * Create Patient element with XMLStreamWriter
   */
   private void createPatientElement(XMLStreamWriter writer, Patient patient) throws XMLStreamException {

			         
	writer.writeStartElement("patient");
	writer.writeAttribute("id", patient.getId() );
		
        writer.writeStartElement("name");
	writer.writeCharacters( patient.getName() );
	writer.writeEndElement();
      
      
	writer.writeStartElement("address");
	writer.writeCharacters( patient.getAddress()  );
	writer.writeEndElement();
      
        writer.writeStartElement("insurance");
        String insurance = "" + patient.getInsurance();
	writer.writeCharacters( insurance );
	writer.writeEndElement();
      
        writer.writeStartElement("dob");
	writer.writeCharacters( dateTransform(patient.getDateofbirth()));
	writer.writeEndElement();
      
        // end patient 
        writer.writeEndElement();
 		
	}
   
   /*
    * Transform date to string
    */
   private String dateTransform(Date date){
   
      SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
      String date_format = df.format( date );
      
      return date_format; 
   
   }
   
   /*
   * Check insurance input when adding new patient
   *
   * @return void
   */
   private void checkInsurance(String insurance_input){
   
       boolean valid = insurance_input.equalsIgnoreCase( "y" );
       boolean valid_no = insurance_input.equalsIgnoreCase( "n" );
       
       if( valid == true || valid_no == true){
           
       }else{
          error_list.add("ERROR: insurance input: y/n" );
       }
   
   }
    /*
    * Parse XML to retrieve patient information
    * @ return patient id
    */
    public String dom_patient( String xml ){
    
      String new_patient_id = "";
      
      try{
        
          DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
          DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
          Document doc = dBuilder.parse(new InputSource(new StringReader(xml)) ); 
          doc.getDocumentElement().normalize();
          
          
          NodeList nList = doc.getElementsByTagName( "patient" );
          
          if( nList.getLength() > 0 ){
              for(int temp = 0; temp < nList.getLength(); temp++){
              
                   Node nNode = nList.item(temp);
                   if(nNode.getNodeType() == Node.ELEMENT_NODE){
               
                      Element element = (Element) nNode;
                      
                      String name = element.getElementsByTagName("name").item(0).getTextContent();
                      String address = element.getElementsByTagName("address").item(0).getTextContent();
                      String insurance = element.getElementsByTagName("insurance").item(0).getTextContent();
                      char insurance_c = insurance.charAt(0);
                      String dob = element.getElementsByTagName("dob").item(0).getTextContent();
                      String physicianID =element.getElementsByTagName("physician").item(0).getTextContent(); 
                      
                      // validate yyyy-mm-dd format 
                      validateDateFormat( dob );
                      checkInsurance( insurance );
                      
                      // if no error on date format and valid insurance input
                      if( error_list.size() == 0){
                         
                         String condition = "id='" + physicianID + "'";
                         List<Object> physician = findObjects( "Physician", condition );
                         String response = "";
                              
                         if( physician.size() > 0 ){
                             // new patient id 
                             new_patient_id = findMaxID("Patient"); 
                             // create new Patient  
                             Patient newPatient = new Patient( new_patient_id, name, address, insurance_c, java.sql.Date.valueOf(dob));

                             Physician physician_obj = (Physician)physician.get(0);
                             newPatient.setPhysician( physician_obj );
                             //add to database 
                             dbSingleton.db.addData( newPatient );
                                   
                                   
                         }else{
                              error_list.add("ERROR: physician not found" );
                         }
      

                      }
                                            
                                           
                   }//if
              }//for
          }else{
              error_list.add("ERROR: XML input error" );
          }
       }catch( Exception e ){
           
       }
       
       return new_patient_id;
    }
   
    /*
    * Parse XML to retrieve appointment information
    * @return appointment id
    */
    public String dom_getNodeValue( String xml ){
    
       String new_appt_id = "";
       try{
        
          DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
          DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
          Document doc = dBuilder.parse(new InputSource(new StringReader(xml)) ); 
          doc.getDocumentElement().normalize();
          
          // 
          NodeList nList = doc.getElementsByTagName( "appointment" );
          
          if( nList.getLength() > 0 ){
              for(int temp = 0; temp < nList.getLength(); temp++){
              
                   Node nNode = nList.item(temp);
                   if(nNode.getNodeType() == Node.ELEMENT_NODE){
               
                      Element element = (Element) nNode;
                      
                      String date = element.getElementsByTagName("date").item(0).getTextContent();
                      String time = element.getElementsByTagName("time").item(0).getTextContent();
                      System.out.println("date " + date );
                      System.out.println("time " + time );


                     
                      final_validate_date_time(date,time);
                                                 
                      String patientId = element.getElementsByTagName("patientId").item(0).getTextContent();
                      String physicianId = element.getElementsByTagName("physicianId").item(0).getTextContent();                                                
                      String pscId = element.getElementsByTagName("pscId").item(0).getTextContent();
                      String phlebotomistId = element.getElementsByTagName("phlebotomistId").item(0).getTextContent(); 
                                                        
                      check_duplicate_conflict(time, date, patientId, phlebotomistId );

                            
                      // check to see if the object exists in the system 
                      ArrayList<Object> objs = findAllObjects_by_id( patientId, physicianId, pscId, phlebotomistId );
                      System.out.println("find all objects by id  " + objs.size() );
                      NodeList test_List = doc.getElementsByTagName( "test" );
                            
                      //find the next appt id
                      new_appt_id = findMaxID("Appointment");
                            
                      // test array 
                      List<AppointmentLabTest> tests = new ArrayList<AppointmentLabTest>();
                            
                      if( test_List.getLength() > 0 ){
                               
                               for(int i = 0; i < test_List.getLength(); i++){
                                
                                     Node testNode = test_List.item(i);
                                     
                                     if(testNode.getNodeType() == Node.ELEMENT_NODE){
                                         System.out.println("ELEMENT " );

                                          Element element_1 = (Element) testNode;
                                          String id = element_1.getAttribute("id");
                                          String dxcode = element_1.getAttribute("dxcode");
                                          
                                          // check to see if the labtest and diagnosis code exist in the system 
                                          ArrayList<Object> lab_diag = findObject_in_Test(id, dxcode);
                                          
                                          if( error_list.size() == 0 ){
                                               AppointmentLabTest test = new AppointmentLabTest( new_appt_id, id, dxcode);
                                               test.setDiagnosis( (Diagnosis)lab_diag.get(1) );
                                               test.setLabTest( (LabTest)lab_diag.get(0) );
                                               tests.add(test);
                                          }
                                     }
                                }
                            }//if 

                                              
                         final_check_time_conflict(  date,  phlebotomistId,  time,  pscId );
                         System.out.println("ERROR LIST SIZE" + error_list.size() );
                         
                         if( error_list.size() == 0 ){
                                System.out.println("here" );
                                Appointment newAppt = new Appointment( new_appt_id, java.sql.Date.valueOf(date),
                                                                     java.sql.Time.valueOf(time + ":00"));
                                newAppt.setAppointmentLabTestCollection(tests);
                                newAppt.setPatientid((Patient)objs.get(0));
                                newAppt.setPhlebid( (Phlebotomist)objs.get(3));
                                newAppt.setPscid((PSC)objs.get(2));
                                
                                
                                
                                boolean good = dbSingleton.db.addData(newAppt);
                                
  
                         }else{
                              
                         }// if no error     
                                                                  
                   }  //if    
                                       
                  
                 
                  
                  
                  
              }//for
             
                       
          }//if
          
        }catch(Exception e){
        
        }
        return new_appt_id;
        
    
    }
   
    /*
    * Validate the format of date and time
    * and the apointment date is valid(not in the past or beyond one year)
    *
    *@return true/false
    */
    private void final_validate_date_time(String date, String time ){
    
         validateAppointDate( date );
         validateAppointTime( time );
         
      
    
    }
    
        
    /*
    * Add a new appointment 
    * @param appointment in XML format
    * @return appointment in XML format
    */
    public String addAppointment(String xml) throws XMLStreamException{
    
       String errorMessage = "";
       
       String id = dom_getNodeValue(xml);
       
       if( error_list.size() == 0 ){
       
            errorMessage += getAppointment(id);   
                        

            
       }else{
            errorMessage += "<AppointmentList>";
                            
            
            for( int i=0; i < error_list.size(); i++){
                 
                 errorMessage += "<error>" + error_list.get( i ) + "</error>";
            
            }             
                           
                           
            errorMessage +=  "</AppointmentList>";
       }
       error_list = new ArrayList<String>();
       
                               
       return errorMessage; 
    }//end method
    
    
    /*
    * Check duplicate appointment
    */
    public void check_duplicate_conflict(String time, String date, String patientID, String phlebid ){
 
      
         // same date, time and patient id already exists in the system  
         String condition = "appttime='" +  java.sql.Time.valueOf( time + ":00" ) + "'";
         
         condition += " AND apptdate='" + java.sql.Date.valueOf(date) + "'";
         condition += " AND patientid='" + patientID + "'";
                                     
         Object obj = null;
         
         obj = findObject( "Appointment", condition );
        
         if ( obj != null ){
             

             error_list.add("ERROR:duplicate appointment"); 
             
         }         
                  
    
    }

    
     /*
     * Check availability of the time of a phlebotomist
     */
     public void timeConflict(String time, String date, String phlebid ){
 
      
         // check if phleb has appointment at the date and time  
         String condition = "appttime='" + java.sql.Time.valueOf(time + ":00") + "'" ;
         condition +=  " AND apptdate='" + java.sql.Date.valueOf(date) + "'";  
         condition += " AND phlebid='" + phlebid + "'";
                
         Object obj = findObject( "Appointment", condition ); 
                        
         if ( obj != null ){
            
              error_list.add("Phlebotomist is not available at that time");
            
         }
         
                  
    
    }
    
    
    /*
    * Find patient, physician, PSC, and phlebotomist by their id 
    * @return Patient, Physician, PSC, and Phlebotomist objects
    */
    private ArrayList<Object> findAllObjects_by_id(String patientID, String physicianID, String pscID, String phlebotomistID){
      
          ArrayList<Object> objs_list = new ArrayList<Object>();
          
          String[] arr = {"Patient","Physician","PSC","Phlebotomist"};
          String[] id = { patientID,physicianID,pscID,phlebotomistID };
          
          for(int i=0; i < arr.length; i++ ){
          
             Object obj = null;
             obj = findObject( arr[i], "id='"+ id[i] + "'" );
             
             if( obj  != null ){
                 objs_list.add(obj);
                
             }else{
               error_list.add( arr[i] + " not found.");
             }
          
          
          
          
          }
                    
          return objs_list;
    }
    
    /*
    * Validate if the date format is in yyyy-mm-dd format, and month is between 1-12,
    * date is between 1-31
    * @return void 
    */
    private void validateDateFormat(String date){
         Pattern p = Pattern.compile("(^\\d{4})-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])$" );
         
         Matcher m = p.matcher(date);
         
         if ( m.matches()) { 
         
         }else{
            error_list.add("Date is not in valid yyyy-mm-dd format");
         }
    
    
    }
	
    /*
    *
    * validate date matching valid yyyy-mm-dd
    *
    */
    private void validateAppointDate(String date){
         
         
         validateDateFormat(date); 
                       
         // current date
         String currentDate = currentDate();
         
         //compare current date and appoint date 
         check_valid_date(date, currentDate);    
                                      
                  
    
    }
	
    /*
    * Check if the appointment date is later than the current date
    * and if the appointment date is not beyond one year 
    *
    * @return void 
    */
    private void check_valid_date( String appointDate, String currentDate ){
    
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        try{
           Date appoint_Date = sdf.parse(appointDate);
           Date current_Date = sdf.parse(currentDate);
           
           // add 1 year to the current date
           Calendar c = Calendar.getInstance();
           c.setTime(current_Date);
           
           // manipulate date
           c.add(Calendar.YEAR, 1);
           Date currentDatePlusOne = c.getTime();
           String formatted = sdf.format(currentDatePlusOne);
           Date current_Date_add_one_year = sdf.parse(formatted);
 
           System.out.println("appointmentDate : " + sdf.format(appoint_Date));
           System.out.println("current Date : " + sdf.format(current_Date));
           System.out.println("one year Date : " + sdf.format(current_Date_add_one_year));

           
           if ( appoint_Date.compareTo( current_Date ) > 0 ) {
           
               if( appoint_Date.compareTo( current_Date_add_one_year ) < 0 ){
               
                   System.out.println("appointment date is VALID");
               }else{
                   
                   error_list.add("appointment date is more than 1 year");


               }
               
               
           }else{
               error_list.add("appointment date is in the PAST");
           
           }  
           
        }catch(ParseException e){
            
        }
                 
    }
    
    
    
    /*
    * Current date
    * @return current date
    */
    private String currentDate(){
    
         SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
         Date date1 = new Date();
         String current_date = dateFormat.format(date1);
         
         return current_date;
         
    
    }
    
    
    /**
    * Check if appointment time is between 8 am to 5 pm 
    *
    * @boolean:true/false
    */
    private void validateAppointTime(String time){
    
          //regular expression to check if the time is between 8 - 17 
          Pattern p = Pattern.compile("(([0][8-9]|[1][0-6]):([0-5][0-9]))|(17:00)" );

          Matcher m = p.matcher(time);
          if (m.matches()) {   
         
              //System.out.println(m.group(1)); 
              //System.out.println(m.group(2));
              //System.out.println(m.group(3));
                            
           
          }else{
               error_list.add("appointment time is not valid, need to between 8-17 ");
          }   
          
         
    
    }
    
    /*
    * Find Labtest and diagnosis code in the system
    * @return labtest object and diagnosis object if found
    *
    */  
    public ArrayList<Object> findObject_in_Test(String testid, String dia_code){
    
        ArrayList<Object> testObjs = new ArrayList<Object>();
        
        Object lab = findObject("LabTest", "id='" + testid + "'" );
        Object diagnosis = findObject("Diagnosis", "code='" + dia_code + "'");
        
        if ( lab == null ){
           error_list.add("Lab test id is not valid");
        
        }else{
            testObjs.add(lab);
        }
        
         if ( diagnosis == null ){
           error_list.add("diagnosis id is not valid");
        
        }else{
            testObjs.add(diagnosis);
        }
        return testObjs;
    
    }
    
    /**
    * Check if the object exists in the system
    *
    * @return boolean true/false
    */
    public Object findObject( String type, String condition ){
   
           
        List<Object> objs = dbSingleton.db.getData(type, condition );
        
        if( objs.size()>0 ){
          return objs.get(0);
        }
        
        return null;
   
   
   }
   
     
    /*
    * Check if the desired appointment overlaps with previous or next appointment
    *
    * @return suggested time if overlapping is found
    */
    public LocalTime check_time_between_appt(
                                        String appt_pscID, LocalTime appt_time, 
                                        String b_a_pscID, LocalTime pre_later_appt_time, 
                                        int indicator ){
           LocalTime suggest_time = null;                             
           int timeBetween = 0; 
           String message = "";
           // check appt time to the appt after it 
           if( indicator == 1){
                //same PSC 
                if( appt_pscID.equals(b_a_pscID) ){
                   timeBetween = 15;
                                      
                }else{
                   timeBetween = 30;
                
                } 
                // appt ending time 
                LocalTime appt_time_end = appt_time.plusMinutes(timeBetween);
                
                LocalTime next_appt_end = pre_later_appt_time.plusMinutes(timeBetween);
                
                // appt ending time is after next appt start time 
                if( appt_time_end.isAfter( pre_later_appt_time ) || appt_time_end.equals( next_appt_end)){
                
                    return next_appt_end;
                }else{
                
                   return suggest_time;
                   
                }
                
           }else{
                // same PSC 
                if( appt_pscID.equals(b_a_pscID) ){
                   timeBetween = 15;
                                      
                }else{
                   timeBetween = 30;
                
                } 
                
                                
                // previous appt ending time 
                LocalTime preEndTime = pre_later_appt_time.plusMinutes(timeBetween);
                                
                // previous appt ends after appt start time 
                if( preEndTime.isAfter( appt_time )  ){
                    
                    return preEndTime;
                                        
                                        
                }else{
                  return suggest_time;
                }
                
           
           
           }
           
                      
                     
    
    
    }
    
    /*
     * Check appointments of a day of a phlebotomist and check time conflict 
     */
    public void final_check_time_conflict( String date, String phlebid, String apptTime,String appt_pscID ){
            
          if( error_list.size() == 0){  
          
               // find all the appointments on the day          
               List<Object> objs = find_appointments_on_the_day(date,phlebid);
                              
               if( objs.size() > 0 ){
               
                  find_Previous_after_Appoint( objs, apptTime+":00", appt_pscID );
               }
          }
            
    }
    
    
    /*
    * Find the appointments of the phlembotomist on the day
    *
    * @return the list of appointments of the phlembotomist on the day 
    */ 
    public List<Object> find_appointments_on_the_day( String date, String phlebid ){
      
            String condition = "apptdate='" + java.sql.Date.valueOf(date) + "' AND phlebid='" + phlebid + "'";
            List<Object> objs = new ArrayList<Object>();
            objs = dbSingleton.db.getData( "Appointment", condition );
            
                                  
            return objs;
      
    }
      
      
      /*
       * Check if the desired appointment time has the time conflict 
       * with the previous/next appointment  
       */
      public void find_Previous_after_Appoint( List<Object> appointments, String apptTime,String appt_pscID ){
            
            //appointment time
            LocalTime original_time = LocalTime.parse(apptTime);
            LocalTime appoint_time = LocalTime.parse(apptTime);
            
            Appointment previous_appointment = null;
            Appointment after_appointment = null; 
            
            ArrayList<Appointment> appt_list = new ArrayList<Appointment>();
            
            LocalTime prevTime = null;
            LocalTime afterTime = null;
            
            // loop through the appointments on the day 
            for( int i = 0 ; i < appointments.size(); i++  ){
            
                  Appointment appt = (Appointment)appointments.get(i);
                  
                  String time = appt.getAppttime().toString();
                  
                  LocalTime currentTime = LocalTime.parse( time );
                  PSC current_PSC = (PSC)appt.getPscid();
                  String currentPSC = current_PSC.getId(); 
                  
                  boolean after = currentTime.isAfter(appoint_time);
                                    
                 
                  boolean before = currentTime.isBefore(appoint_time);
                 
         
                  if( currentTime.isBefore(appoint_time) ){
                        LocalTime validTime = check_time_between_appt(appt_pscID, appoint_time, currentPSC, currentTime, -1 );
                        if(validTime!=null){
                           System.out.println("i:" + i + ",current time"+currentTime.toString()+
                                           " is BEFORE appt time:" + validTime.toString() );
                        }
                        if( validTime != null){
                              appoint_time = validTime;
                        }
                                                                                          
                  }else if( currentTime.isAfter(appoint_time) ){
                        
                        LocalTime validTime = check_time_between_appt(appt_pscID, appoint_time, currentPSC, currentTime, 1 );
                                                
                        if( validTime != null){
                              appoint_time = validTime;
                        }


                  
                  }else{
                        LocalTime validTime = check_time_between_appt(appt_pscID, appoint_time, currentPSC, currentTime, 1 );
                                                
                        if( validTime != null){
                              appoint_time = validTime;
                        }

                  }            
            }
            
            if( original_time.equals(appoint_time) ){
               
            }else{
                  LocalTime start = LocalTime.parse("08:00");
                  LocalTime end = LocalTime.parse("17:00");
                  if(appoint_time.isBefore(start)|| appoint_time.isAfter(end ) ){
                     error_list.add("ERROR: appointmentTime is invalid. No available time on the date."  );
                  }else{
                     error_list.add("ERROR: appointmentTime is invalid. SUGGEST TIME:" + appoint_time.toString() );

                  }
            }
            
                        
             
      
      }
     
     
   /* 
   * Create XML string for testing purpose
   *
   *@return XML string 
   */
   public String createXML_addAppoint(String time,String patientID, String phlemID, String pscID) throws XMLStreamException {
      
       String xmlString ="";
            
       StringWriter stringWriter = new StringWriter();
      
       XMLOutputFactory factory = XMLOutputFactory.newInstance();
       XMLStreamWriter writer = factory.createXMLStreamWriter(stringWriter);
      
              
       writer.writeStartDocument("UTF-8","1.0");
       
       writer.writeStartElement("appointment");
       
       writer.writeStartElement("date");
		 writer.writeCharacters( "2018-04-18" );
		 writer.writeEndElement();
       
       writer.writeStartElement("time");
		 writer.writeCharacters( time );
		 writer.writeEndElement();
       
       //Patient element
       writer.writeStartElement("patientId");
       writer.writeCharacters( patientID );
       writer.writeEndElement();
       
       //Physician element
       writer.writeStartElement("physicianId");
       writer.writeCharacters( "10" );
       writer.writeEndElement();
       
       //PSC element
       writer.writeStartElement("pscId");
       writer.writeCharacters( pscID );
       writer.writeEndElement();
       
       //Phlebotomist element
       writer.writeStartElement("phlebotomistId");
       writer.writeCharacters( phlemID );
       writer.writeEndElement();
       
       // Lab test element	   
       writer.writeStartElement("labTests");
       

       writer.writeEmptyElement("test");
       writer.writeAttribute("id", "86900" );
       writer.writeAttribute("dxcode", "292.9" );
       
       // Lab test element
       writer.writeEmptyElement("test");
       writer.writeAttribute("id", "86609" );
       writer.writeAttribute("dxcode", "307.3" );
 
       writer.writeEndElement();//end labTests
       

       writer.writeEndElement();//end appointment
       
       writer.writeEndDocument();
       writer.flush();
       writer.close();
      
             
       xmlString += stringWriter.getBuffer().toString();   
       System.out.println("xml:"+xmlString);
       return xmlString; 
   
   }
   
   /**
   *
   * Find the next id number in the table
   * @return string next id 
   */
   public String findMaxID( String tableName ){
   
     
            
        String condition = "id = (SELECT MAX(id) FROM " +  tableName +")";

        Object obj = findObject( tableName, condition );
        
        
        
        String nextID = "";
        
        if( obj != null ){
           
          String id = "";
          
          if( obj instanceof Appointment ){
             id = ((Appointment)obj).getId();
             
          }else if( obj instanceof Patient ){
            id = ((Patient)obj).getId();
          }
          
          int max = Integer.parseInt(id);
          max += 10; 
          nextID = String.valueOf( max );
        }
        
        return nextID;
       
   
   
   }
   
   /*
    * Cancel appointment 
    * @return response in string 
    */
   public String cancelAppointment( String appt_id ){
      
         String condition = "appointmentLabTestPK.apptid='" + appt_id + "'";
         String con_appt = "id='" + appt_id + "'";
         //find obj
         Object obj = findObject("AppointmentLabTest", condition);
         Object appt = findObject("Appointment", con_appt );
         
         String response = "<AppointmentList>";
                            


         
         if( obj != null && appt != null ){
         
            dbSingleton.db.deleteData("AppointmentLabTest", condition );
            dbSingleton.db.deleteData("Appointment", con_appt );
            
            if (findObject("AppointmentLabTest", condition)==null & findObject("Appointment", con_appt )==null){
            
               response += "<success>SUCCESS: appointment deletion succeeded</success>" ;
            
            }else{
               response += "<error>ERROR: appointment deletion FAILS</error>" ;

            }
            
         }else{
            response += "<error>ERROR: appointment deletion FAILS</error>" ;
         }
         
         response += "</AppointmentList>";

         return response;
                   
                  
   }
   
   

   
   
     
   
}
