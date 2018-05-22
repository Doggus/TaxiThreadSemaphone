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

    private int ID;
    private Taxi taxi;
    private LinkedList<Job> jobs;
    private int currentLocation;
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
        
        while (!jobs.isEmpty())
        {
            lastJob = currentJob;
            currentJob = jobs.removeFirst();

            if (lastJob != null)
            {
                setDestination(currentJob.getBranchID());
                setCurrentLocation(lastJob.getBranchID());
                
                taxi.hail(this);

                //person will get off and work at the branch for a specified duration
                workAtBranch(currentJob.getDuration());
     
            } 
            else
            {
                setDestination(currentJob.getBranchID());
                setCurrentLocation(0);
                
                taxi.hail(this);

                //person will get off and work at the branch for a specified duration
                workAtBranch(currentJob.getBranchID());
 
            }
        }
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
        synchronized(this)
        {
            wait();
        }
    }
    
    //Used to make sure person can hail once the taxi has dropped the person off and left
    public void Available()
    {
        synchronized(this)
        {
            notify();
        }
    }

}
