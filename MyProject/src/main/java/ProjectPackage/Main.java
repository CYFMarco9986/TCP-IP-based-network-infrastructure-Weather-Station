package ProjectPackage;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;


public class Main 
{
   // Data Members
   public static LoginGUI loginPage = new LoginGUI(); 
   public static AfterLoginGUI clientPage = new AfterLoginGUI();    
   static String loginInput = null;
   static String passwordInput = null;
   static String clientType = "User";
   static String loginResponse = "";
   public static DataInputStream dis;
   public static DataOutputStream dos; 
   private static Socket s;   
   public static String state = "waiting";
    
   static String FieldID = null; 
   static String FieldLattitude;
   static String FieldLongtitude; 
   static String FieldElevation;
   static String FieldTemperature;
   static String FieldHumidity;    
   static String FieldBarometricPressure;
   static String FieldWindForce;
    
    public static void main(String[] args) throws IOException  
    {
         try
        {          
            loginPage.setVisible(true);         
           
            while(true)
            {        
                System.out.print(state + "\n");              
                if(state.equals("Connect"))
                {                          
                    Connect(); 
                    LoginAc();
                } 
                if(state.equals("Download"))
                {
                    DownloadFieldData();            
                }
                if(state.equals("Disconnect"))
                {
                   Disconnect(); 
                }                
            }            
        }
         catch(Exception e)
        {
          e.printStackTrace();             
        }    
    }
      
    static void Connect()
    {
        try
        {                       
            InetAddress ip = InetAddress.getByName("localhost");       
            s = new Socket(ip, 5050);       
            dis  = new DataInputStream(s.getInputStream()); 
            dos = new DataOutputStream(s.getOutputStream());              
            dos.writeUTF(clientType);
        }
        catch(IOException e)
        {
           e.printStackTrace();      
        }
    }
    
     static void LoginAc()
    {
        try
        {           
            dos.writeUTF("LoginAccount");
            loginInput    = loginPage.Login_username_box.getText();
            passwordInput = loginPage.Login_password_box.getText();
            dos.writeUTF(loginInput);
            dos.writeUTF(passwordInput);
            loginResponse =  dis.readUTF(); 

            if(loginResponse.equals("Successful"))
            {                       
                // Set starting values of the ClientSide Page                  
                clientPage.ShowUsername.setText(loginInput);

                 int amountOfConnectedStations = 0;
                 amountOfConnectedStations =  Integer.valueOf(dis.readUTF());                                                
                 for(int i = 0; i < amountOfConnectedStations; i++)
                 {                 
                      clientPage.OnlineWeatherStationList.add(dis.readUTF());
                 }                 
                 clientPage.setVisible(true); 
                 loginPage.setVisible(false);
                 state = "waiting";
            }
            else
            {
                Disconnect(); 
            }
        }
          catch(IOException e)
        {
           e.printStackTrace(); 
        }        
    }
    
     static void DownloadFieldData()
     {
        try
          {
           dos.writeUTF("DownloadInformation");
           FieldID =  clientPage.OnlineWeatherStationList.getItem(clientPage.OnlineWeatherStationList.getSelectedIndex());
           dos.writeUTF(FieldID);
           FieldLattitude = dis.readUTF(); 
           FieldLongtitude = dis.readUTF(); 
           FieldElevation = dis.readUTF(); 
           FieldTemperature = dis.readUTF(); 
           FieldHumidity = dis.readUTF();
           FieldBarometricPressure = dis.readUTF();
           FieldWindForce = dis.readUTF();
            
           clientPage.InformationBox.setText("Blank");    
            
           clientPage.InformationBox.setText("Weather Station ID: " +  FieldID  + "\n" + "//Field information//" +"\n" + "Lattitude    : " +  FieldLattitude  + "\n" + "Longtitude   : " +  FieldLongtitude + "\n" + "Elevation    : " +  FieldElevation  + "\n" + "//Weather information//" + "\n" + "Temperature: " +  FieldTemperature + "\n" + "Humidity   : " +  FieldHumidity + "\n" + "Barometric pressure   : " + FieldBarometricPressure + "\n" + "Wind force   : " + FieldWindForce);
           //reset
           state = "waiting";
           FieldLattitude = null; 
           FieldLongtitude = null; 
           FieldElevation = null; 
           FieldTemperature = null;  
           FieldHumidity = null;  
           FieldID = null; 
            
          }
        catch(IOException e)
          {
             e.printStackTrace(); 
          }  
     }
       
    static void Disconnect()
    {
        try
        {
            dos.writeUTF("Disconnected");
            clientPage.OnlineWeatherStationList.removeAll();             
            s.close();
            dis.close();
            dos.close();          
            state = "reseted";                         
        }
        catch(IOException e)
        {
           e.printStackTrace(); 
        }
           
    }
}
