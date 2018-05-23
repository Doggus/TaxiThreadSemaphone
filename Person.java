/**
 *
 * @author liron
 */

import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Person extends Thread
{
    //person's ID
    private int ID;
    //the taxi that the person uses
    private Taxi taxi;
    //a person's list of jobs to do (schedule)
    private LinkedList<Job> jobs;
    //a person's current location
    private int currentLocation;
    //a person's destination location
    private int destination;

    public Person(int id, Taxi t, LinkedList<Job> j)
    {
        ID = id;
        taxi = t;
        jobs = j;
    }

    /* Thread sets current branch of the thread as its pickup location and the branch it needs to get to as its destination.
      When a person wants to travel to a different branch they will call hail. */
    public void run()
    {
        //Reset job status
        Job lastJob = null;
        Job currentJob = null;    
        
        //while a person's schedule is not complete
        while (!jobs.isEmpty())
        {
            //if equal to null, it means person has just begun work and is at HQ
            lastJob = currentJob;       
            //current job = first job in linked list (then removes it from list)
            currentJob = jobs.removeFirst();

            //if not initial pickup from HQ
            if (lastJob != null)
            {
                //set person's current and destination locations appropriatly
                setDestination(currentJob.getBranchID());
                setCurrentLocation(lastJob.getBranchID());
                
                //person notifies taxi to fetch him/her (and waits to be picked up)
                taxi.hail(this);

                //person will wait to be deilverd to next branch and then get off and work at the branch for a specified duration
                workAtBranch(currentJob.getDuration());
     
            } 
            else 
            {
                //destination is first job in schedule
                setDestination(currentJob.getBranchID());
                //person's current location is HQ
                setCurrentLocation(0);
                
                //person notifies taxi to fetch him/her from HQ (and waits to be picked up)
                taxi.hail(this);

                //person will wait to be deilverd to next branch and then get off and work at the branch for a specified duration
                workAtBranch(currentJob.getBranchID());
 
            }
        }
        
        //when thread is done running (schedule is complete) notify taxi that there is one less person to worry about
        taxi.decrementPeople();
    }

    //--------------------------------------------------------------------------
    
    public int getID()
    {
        return ID;
    }

    public int getCurrentLocation()
    {
        return currentLocation;
    }

    public int getDestination()
    {
        return destination;
    }

    private void setCurrentLocation(int l)
    {
        currentLocation = l;
    }

    private void setDestination(int l)
    {
        destination = l;
    }

    //--------------------------------------------------------------------------
    
    //Simulates a person working at a branch for a specified amount of time
    private void workAtBranch(int duration)
    {  
        try 
        {
            //simulated time according to assignment sheet
            sleep(17*duration);
        } 
        catch (InterruptedException ex) 
        {
            System.out.println(ex);
        }
    }
    
    
    //Used to make sure person cant get straight back into the taxi when it is still as the same branch
    public void notAvailable() throws InterruptedException
    {
        //methods will make thread wait until notify is called (Available)
        synchronized(this)
        {
            wait();
        }
    }
    
    //Used to make sure person can hail once the taxi has dropped the person off and left
    public void Available()
    {
        //thread will resume when notify is called from this method
        synchronized(this)
        {
            notify();
        }
    }

}
