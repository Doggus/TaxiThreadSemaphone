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
        Job currentInstruction = null;
        Job previousInstruction = null;
        while (!jobs.isEmpty())
        {
            previousInstruction = currentInstruction;
            currentInstruction = jobs.removeFirst();

            if (previousInstruction == null)
            {
                setCurrentLocation(0);
                setDestination(currentInstruction.getBranchID());
                taxi.hail(this);

                //System.out.println("Thread: "+identifier+" has gotten off at: "+nextLocation);
                work(currentInstruction.getBranchID());
                //System.out.println("Thread: "+identifier+" has worked at: "+nextLocation);
            } 
            else
            {
                setCurrentLocation(previousInstruction.getBranchID());
                setDestination(currentInstruction.getBranchID());
                taxi.hail(this);

                //System.out.println("Thread: "+identifier+" has gotten off at: "+nextLocation);
                work(currentInstruction.getDuration());
                //System.out.println("Thread: "+identifier+" has worked at: "+nextLocation);
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
    private void work(int duration)
    {  
        try 
        {
            //simulated time according to assignment sheet
            sleep(17*duration);
        } 
        catch (InterruptedException ex) 
        {
            Logger.getLogger(Person.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    //Used to make sure person cant get straight back into the taxi when it is still as the same branch
    public void notAvailable() throws InterruptedException
    {
        synchronized(this)
        {
            this.wait();
        }
    }
    
    //Used to make sure person can hail once the taxi has dropped the person off and left
    public void Available()
    {
        synchronized(this)
        {
            this.notify();
        }
    }

}
