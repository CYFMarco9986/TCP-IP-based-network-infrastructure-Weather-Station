package ProjectPackage;

import static ProjectPackage.Server.screen;
import java.io.*; 
import java.net.*; 
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

class Server 
{      
    static Vector<Vector> WeatherStations = new Vector(0);
    static ServerGUI screen = new ServerGUI();        
    public static void main(String[] args) throws IOException
    {
       screen.setVisible(true);      
       ServerSocket ss = new ServerSocket(5050);
       while(true)
       {           
           ConnectionBuild(ss);
       }
    }     
    
    static void ConnectionBuild(ServerSocket _ss) throws IOException
    {
        Socket s = null;
         try 
            {                    
                s = _ss.accept();
                DataInputStream dis  = new DataInputStream( s.getInputStream()); 
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());                   
                HandleClientInfor t = new HandleClientInfor(s, dis, dos);                    
                t.start();                                                                
            } 
            catch (Exception e)
            { 
                s.close(); 
                e.printStackTrace(); 
            } 
    }
}

class HandleClientInfor extends Thread
{
    static String clientType = "None"; 
    final DataInputStream dis; 
    final DataOutputStream dos; 
    final Socket s;
   
    public static Vector<Float> stationData = new Vector(5);
    static int   Id = -1000;
    static float lattitude   = 0;
    static float longtitude  = 0;
    static float elevation   = 0;
    static float temperature = 0;
    static float humidity    = 0;
    static float barometricPressure = 0;
    static float windForce = 0;
    static String loginName = "";
    static String password  = ""; 
    
    public HandleClientInfor(Socket s, DataInputStream dis, DataOutputStream dos)  
    { 
        this.s = s; 
        this.dis = dis; 
        this.dos = dos;                
    } 
    
    @Override
    public void run() 
    {                
         do 
        {            
            try 
             {
                DefineClientType(this.dis);
             } 
             catch (IOException ex) 
             {
                 Logger.getLogger(HandleClientInfor.class.getName()).log(Level.SEVERE, null, ex);
             }
        }while(clientType.equals("None"));
         
        while("WeatherStation".equals(clientType))
        {               
          HandleWeatherStationInfor(this.dis, this.dos);          
        }
        while("User".equals(clientType))
        {
            HandleUserInfor(this.dis, this.dos, this.s);
        }
        
        while("Disconnect".equals(clientType))
        {
            try
           { 
               this.dis.close(); 
               this.dos.close(); 
               sleep(); 
            }
            catch(IOException e)
            { 
              e.printStackTrace(); 
            } 
        } 
    }
           
    static void DefineClientType(DataInputStream _dis) throws IOException 
     {                              
         clientType = _dis.readUTF();                             
     }
     
    static void HandleWeatherStationInfor(DataInputStream _dis, DataOutputStream _dos)
    {
      try 
        {                
            Id = Server.WeatherStations.size();
            _dos.writeUTF(Integer.toString(Id));
                       
            Id =  Integer.valueOf(_dis.readUTF());
            String currentWeatherStations =  screen.Server_onlineWeatherStationsList.getText();          
            if(currentWeatherStations.contains(Integer.toString(Id)) == false)
            {             
               screen.Server_onlineWeatherStationsList.append(Integer.toString(Id) + "\n");            
            }
            
            lattitude   = Float.valueOf(_dis.readUTF()); 
            longtitude  = Float.valueOf(_dis.readUTF()); 
            elevation   = Float.valueOf(_dis.readUTF()); 
            temperature = Float.valueOf(_dis.readUTF()); 
            humidity    = Float.valueOf(_dis.readUTF());
            barometricPressure = Float.valueOf(_dis.readUTF());
            windForce = Float.valueOf(_dis.readUTF());
                        
            stationData.insertElementAt(lattitude  , 0);
            stationData.insertElementAt(longtitude , 1);
            stationData.insertElementAt(elevation  , 2);
            stationData.insertElementAt(temperature, 3);
            stationData.insertElementAt(humidity   , 4);  
            stationData.insertElementAt(barometricPressure   , 5);  
            stationData.insertElementAt(windForce   , 6);  
            Server.WeatherStations.insertElementAt(stationData, Id);
        } 
        catch (IOException e)
        { 
            e.printStackTrace();    
        }          
     }    
    
    static void HandleUserInfor(DataInputStream _dis, DataOutputStream _dos, Socket _s) 
    {
        try
        {
            String service = _dis.readUTF(); 
            if(service.equals("LoginAccount"))    
            {
                loginName = _dis.readUTF();
                password  = _dis.readUTF(); 
                String accountfile = "src/main/java/AccountFolder/document.csv";
                String line = "";
                String nextData = ",";
                String result = "Failed";
                BufferedReader br = null;             
                br = new BufferedReader(new FileReader(accountfile));            
                while ((line = br.readLine()) != null)
                {
                    String[] userCredential = line.split(nextData);              

                    if(userCredential[0].equals(loginName) && userCredential[1].equals(password))
                    {                    
                        result = "Successful";
                        screen.Server_onlineClientsList.append(loginName + "\n");                        
                    }
                }          
                _dos.writeUTF(result);  
                
                if(result.equals("Successful"))
                {
                    int connectedStationsAmount = 0;
                    for (String currentLine : screen.Server_onlineWeatherStationsList.getText().split("\\n")) 
                    {              
                      connectedStationsAmount++;
                    }             
                    _dos.writeUTF(Integer.toString(connectedStationsAmount));
                    for(String currentLine : screen.Server_onlineWeatherStationsList.getText().split("\\n"))
                    {
                      _dos.writeUTF(currentLine);
                    }
                }  
                service = "waiting";
            }            
       
            if(service.equals("DownloadInformation"))
            {
              int FieldID = Integer.valueOf(_dis.readUTF());                           
              _dos.writeUTF(Server.WeatherStations.get(FieldID).get(0).toString());
              _dos.writeUTF(Server.WeatherStations.get(FieldID).get(1).toString());
              _dos.writeUTF(Server.WeatherStations.get(FieldID).get(2).toString());
              _dos.writeUTF(Server.WeatherStations.get(FieldID).get(3).toString());
              _dos.writeUTF(Server.WeatherStations.get(FieldID).get(4).toString());
              _dos.writeUTF(Server.WeatherStations.get(FieldID).get(5).toString());
              _dos.writeUTF(Server.WeatherStations.get(FieldID).get(6).toString());
               service = "waiting";
            }  
            
            if(service.equals("Disconnected"))
            {   
                _s.close();                 
                _dos.close();
                _dis.close();
                service = "waiting";
                clientType = "Disconnect";                       
            }
        }  
        catch(FileNotFoundException e)
         {
            e.printStackTrace();
         } 
        catch(IOException e)
         {
             e.printStackTrace();
         }
    }

    private void sleep()
    {
        try 
        {
            Thread.sleep(5000);
        }
        catch (InterruptedException e)
        {
            this.interrupt();
            return;
        }
    }
}




