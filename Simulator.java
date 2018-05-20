
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Simulator
{
    public static void main(String[] args)
    {
        int numPeople = 0;
        int numBranches = 0;
        
        Trace trace = new Trace("Initial trace");
        List<Person> people = new ArrayList();
        
        //---------------------------------------------------------------------------------------
        try
        {
            BufferedReader f = new BufferedReader(new FileReader("example.txt")); //should be args[0]
            numPeople = Integer.parseInt(f.readLine());
            numBranches = Integer.parseInt(f.readLine());
            String s = f.readLine();
      
            while(s!=null)
            {
                //resets branches
                List<Branch> branches = new ArrayList();
         
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
                    
                    branches.add(new Branch(bID,dur));
                            
                }
                
                //person ID
                int pID = Integer.parseInt(s.substring(0,1));
                people.add(new Person(pID,branches,trace));
                
                s = f.readLine(); //next line
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        
       //-------------------------------------------------------------------
       
       
        Taxi taxi = new Taxi(people, numBranches, trace);
        new Thread(taxi,"Taxi").start();
       
        new Thread(people.get(0),"Person").start();
        System.out.println("All the threads are started");


//        for (int i = 0; i < numPeople; i++)
//        {
//            jobs.add(new Job(i , people.get(i).schedule.get(0)));
//        }
       
        
        //Thread tx = new Thread(t);       

        //tx.start();
        
//        for (int i = 0; i < numPeople; i++)
//        {
//            new Thread(people.get(i)).start();
//        }

         //new Thread(people.get(0)).start();
      
    }
}
