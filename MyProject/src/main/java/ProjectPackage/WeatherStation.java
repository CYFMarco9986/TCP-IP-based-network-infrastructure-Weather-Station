package ProjectPackage;

import java.io.*; 
import java.net.*; 
import java.util.Random;

public class WeatherStation 
{
    static String Id = null;
    static String clientType = "WeatherStation";
    static float lattitude   = 0;
    static float longtitude  = 0;
    static float elevation   = 0;
    static float temperature = 0;
    static float humidity    = 0;
    static float barometricPressure = 0;
    static float windForce = 0;
    
    static String state = "Runnning";     
     
     public static void main(String[] args) throws IOException  
    {
         try
        {                 
            InetAddress ip = InetAddress.getByName("localhost");       
            Socket s = new Socket(ip, 5050);       
            DataInputStream dis  = new DataInputStream(s.getInputStream()); 
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());   
            TypeOfServer(dos);
            Register(dis); 
            System.out.println("Station ID: " + Id + "\n");
            Random rand = new Random();    
            lattitude   = (rand.nextFloat() + 10f  );
            longtitude  = (rand.nextFloat() - 1.66f);
            elevation   = (rand.nextFloat() + 30.66f);   
            
            while(true)
            {
               GenerateData();
               System.out.println("Temperature = " + temperature + "\n" + "Humidity = " + humidity + "\n" + "Barometric pressure = " + barometricPressure + "\n" + "Wind force = " + windForce + "\n");               
               SendData(dos);                             
               
               Thread.sleep(5000);
            }                  
        }            
         catch(Exception e)
        { 
            e.printStackTrace(); 
        } 
    }
 
     static void TypeOfServer(DataOutputStream dos) throws IOException
     {
        dos.writeUTF(clientType);
     }
     
     static void Register(DataInputStream _dis) throws IOException
     {
        Id = _dis.readUTF(); 
     }
     
     static void GenerateData()
     {
        Random rand = new Random();                    
        temperature = (rand.nextInt(30) + 22);
        humidity = (rand.nextInt(50) + 30);
        barometricPressure = (rand.nextInt(800) + 1000);
        windForce = (rand.nextInt(30) + 10);
     }   
     
     static void SendData(DataOutputStream _dos) throws IOException
     {
         _dos.writeUTF(Id);   
        _dos.writeUTF(Float.toString(lattitude));
        _dos.writeUTF(Float.toString(longtitude));
        _dos.writeUTF(Float.toString(elevation));
        _dos.writeUTF(Float.toString(temperature));
        _dos.writeUTF(Float.toString(humidity));
        _dos.writeUTF(Float.toString(barometricPressure));
        _dos.writeUTF(Float.toString(windForce));
     }   
}
