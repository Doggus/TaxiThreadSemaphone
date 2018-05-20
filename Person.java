/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author tldlir001
 */

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Person implements Runnable
{
    int id;
    List<Branch> schedule;
    Trace trace;
    int TaxiLocation;
    
    public Person(int i, List<Branch> s, Trace t)
    {
        id = i;
        schedule = s;
        trace = t;
        //starts at branch 0
        TaxiLocation = 0;
    }
    
    public void run()
    {
        System.out.println("Person: " + id + " thread created");       
        
        String name = "Person: " + id;
        System.out.println(name + " has started");

           //run thread until person's schedule is empty
           while(!(schedule.isEmpty()))
           {
                synchronized (trace) 
                {
                    try
                    {
                        //if taxi is at current current schedule branch
                        if(TaxiLocation == schedule.get(0).id)
                        {
                            //Person working at branch
                            System.out.println("Person: " + id + " is working at branch " + schedule.get(0).id);
                            //work according to schedule duration
                            Thread.sleep(17*schedule.get(0).duration); 
                            //start waiting
                            System.out.println("Person waiting to get picked up at branch " + schedule.get(0).id + " : " + System.currentTimeMillis());

                            //waits until taxi fetches it
                            trace.wait();
                        }

                    }
                    catch(InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
           }
            
    }
    
    public void setTaxiLocation(int loc)
    {
        TaxiLocation = loc;
    }
}
