
/**
 *
 * @author liron
 */

import java.io.*;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Simulator
{
    public static void main(String[] args)
    {
        int numPeople = 0;
        int numBranches = 0;

        List<Person> people = new ArrayList();
        
        //---------------------------------------------------------------------------------------
        try
        {
            BufferedReader f = new BufferedReader(new FileReader("example.txt")); //should be args[2]
            numPeople = Integer.parseInt(f.readLine());
            numBranches = Integer.parseInt(f.readLine());
            String s = f.readLine();

            Taxi taxi = new Taxi(numBranches,numPeople);
      
            while(s!=null)
            {
                //resets branches
                LinkedList<Job> branches = new LinkedList();
         
                // number of times "(" appears (by extension, number of elements)
                int count = s.length() - s.replace("(", "").length(); 
                //temp variables used for reading branches
                int c1 = 0;
                int c2 = 0;
                //reads in branches 
                for (int i = 0; i < count; i++)
                {
                  
                    int r1 = s.indexOf("(", c1);
                    int r2 = s.indexOf(")", c2);
                    int bID = Integer.parseInt(s.substring(r1+1,r1+2));
                    int dur = Integer.parseInt(s.substring(r1+4,r2));

                    c1 = r1+1;
                    c2 = r2+1;
                    
                    branches.add(new Job(bID,dur));
                            
                }
                
                //person ID
                int pID = Integer.parseInt(s.substring(0,1));
                people.add(new Person(pID,taxi,branches));
                
                s = f.readLine(); //next line
            }
            
            //-------------------------------------------------------------------
       
       //start people threads 
        for (int i = 0; i < numPeople; i++)
        {
            people.get(i).start();
        }
        
        //start taxi thread
        taxi.start();
            
        }
        catch(Exception ex)
        {
           ex.printStackTrace();
        }
        

    }
}
