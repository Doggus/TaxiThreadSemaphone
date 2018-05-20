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
    private int id;
    private List<Branch> schedule;
    private Trace trace;
    private int TaxiLocation;
    
    //helps determine if a Person is allowed to be picked up or not
    private boolean available = false;
    
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
        System.out.println("Person " + id + " thread created");       

           //run thread until person's schedule is empty
           while(!(schedule.isEmpty()))
           {
                synchronized (trace) 
                {
                    try
                    {
                        //if taxi is at current current schedule branch
                        if(TaxiLocation == schedule.get(0).getId())
                        {
                            //set to false to make sure taxi doesn't pick up person until it returns to branch (not allowed to be picked up)
                            available = false;
                            
                            //Person dropped off at branch
                            System.out.println("Person: " + id + " was dropped of at branch " + schedule.get(0).getId());
                            //Person starts working
                            System.out.println("Person: " + id + " is working at branch " + schedule.get(0).getId());
                            //work according to schedule duration
                            Thread.sleep(17*schedule.get(0).getDuration()); 
                            //start waiting
                            System.out.println("Person: " + id +  " waiting to get picked up at branch " + schedule.get(0).getId() + " : " + System.currentTimeMillis());

                            //waits until taxi arrives at branch to fetch it
                            trace.wait();
                        }
                    }
                    catch(InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
           }
           
           System.out.println("Person " + id + " schedule is complete. Ending thread");
            
    }

    public int getId()
    {
        return id;
    }

    public List<Branch> getSchedule()
    {
        return schedule;
    }

    public Trace getTrace()
    {
        return trace;
    }

    public int getTaxiLocation()
    {
        return TaxiLocation;
    }

    public boolean isAvailable()
    {
        return available;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public void setSchedule(List<Branch> schedule)
    {
        this.schedule = schedule;
    }

    public void setTrace(Trace trace)
    {
        this.trace = trace;
    }

    public void setAvailable(boolean available)
    {
        this.available = available;
    }
    
    public void setTaxiLocation(int loc)
    {
        TaxiLocation = loc;
    }
}
