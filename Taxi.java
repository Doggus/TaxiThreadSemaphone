/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tldlir001
 */
public class Taxi implements Runnable
{
    //refer to assignment notes regarding the time system [Thread.wait()]
    
    private int currentBranchID;
    private int numBranches;
    private List<Person> people = new ArrayList();
    private Trace trace;
    
    private int branchMax;
    private int branchMin;
    
    //gobal variable used to help determine certaine things:
    
    //helps determine how to wait when stopping at branches
    private int wait = 0;
    //initial direction is outbound
    private int direction = 0;
    
    
    public Taxi(List<Person> p, int b , Trace t)
    {
        people = p;
        currentBranchID = 0; //always begins at current branch
        trace = t;
        numBranches = b;
        //Initialise (necesary for finding max)
        branchMax = people.get(0).getSchedule().get(0).getId();
        branchMin = 0; //minimum branch starts at 0
       
    }
    
  
    @Override
    public void run()
    {
        System.out.println("Taxi thread created");
        
        while(!(people.isEmpty()))
        {

            //---------------Drop off people--------------------\\
            try
            {
                for (int i = 0; i < people.size(); i++)
                {
                    //if a person stil has somewhere to go
                    if(!(people.get(i).getSchedule().isEmpty()))
                    {
                        //if person is getting off at current branch
                        if (people.get(i).getSchedule().get(0).getId() == currentBranchID)
                        {
                            //increment wait int
                            wait++;
                            
                            
                            //will cause person thread to do work and then wait for taxi
                            people.get(i).setTaxiLocation(currentBranchID);
                            

                        }
                    }
                    else // if person's schedule is complete
                    {
                        //remove that person
                        people.remove(i);
                    }
                       
                }
                
                //---------------If person is dropped off taxi waits one minute--------------------\\
                if (wait>0)
                {
                    //taxi waits for one minute
                    Thread.sleep(17*1);
                        
                    //reset wait
                    wait = 0;
                }

                    
                //---------------Find furthest outbound branch that has a person--------------------\\
                for (int i = 0; i < people.size()-1; i++)
                {
                    if(!(people.get(i).getSchedule().isEmpty()))
                    {
                        if (branchMax > people.get(i+1).getSchedule().get(0).getId())
                        {
                            branchMax = people.get(i).getSchedule().get(0).getId();
                        }
                        else
                        {
                            branchMax = people.get(i+1).getSchedule().get(0).getId();
                        }
                    }
                }
            
                //---------------Find furthest inbound branch that has a person--------------------\\
                for (int i = 0; i < people.size()-1; i++)
                {
                    if(!(people.get(i).getSchedule().isEmpty()))
                    {
                        if (branchMin < people.get(i+1).getSchedule().get(0).getId())
                        {
                            branchMin = people.get(i).getSchedule().get(0).getId();
                        }
                        else
                        {
                            branchMin = people.get(i+1).getSchedule().get(0).getId();
                        }
                    }
                }
                    
                    
                //if statement below used simply to eliminate some unneccesary print outs    
                if(!(people.isEmpty())) 
                {
                    //---------------Simulated Taxi Traversal--------------------\\
                    
                    //leaves current branch
                    System.out.println("Taxi travels from branch: " + currentBranchID);
                    //travles for two minutes
                    Thread.sleep(17*2);
                    //arrives at next branch (depending on direction currentBranch will change)
                    if (direction == 0 && currentBranchID != branchMax) //outbound
                    {
                        //advance to next branch
                        currentBranchID++;
                     
                    }
                    else //inbound
                    {
                        //start travelling inbound (change direction)
                        direction = 1;

                        //advance to next branch
                        currentBranchID--;
                        //if taxi reaches branchMin
                        if (currentBranchID == branchMin)
                        {
                            //start travelling outbound
                            direction = 0;
                        }
                    }

                    System.out.println("Taxi Arrives at branch: " + currentBranchID);
                    //update taxis current branch for all people
                    for (int i = 0; i < people.size(); i++)
                    {
                        //update the taxi's location for people threads
                        people.get(i).setTaxiLocation(currentBranchID);
                    }
                }
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
            
            //---------------Pick up any waiting Person threads--------------------\\
            try 
            {
                synchronized (trace) 
                {
                    for (int i = 0; i < people.size(); i++)
                    {
                        if(currentBranchID == people.get(i).getSchedule().get(0).getId() && people.get(i).isAvailable() == true)
                        {
                            //increment wait
                            wait++;

                            //remove branch from person's schedule
                            people.get(i).getSchedule().remove(0);
                            
                            trace.setTrace("Taxi Picks up Person: " + people.get(i).getId() + " from branch " + currentBranchID);
                            System.out.println(trace.getTrace());
                            
                            //stop Person thread from waiting (pick them up)
                            trace.notify();
                            //trace.notifyAll();
                        }
                        
                        //set to true to make sure taxi can now pick up the person when it returns to the branch
                        people.get(i).setAvailable(true);
                    }

                    /* notifyAll() wakes up all the waiting threads
                       if notify() is used only one of them will wake up. */
                }
                
                //---------------If person is picked up taxi waits one minute--------------------\\
                    if (wait>0)
                    {
                        //taxi waits for one minute
                        Thread.sleep(17*1);
                        
                        //reset wait
                        wait = 0;
                    }
                    
            } 
            catch (Exception ex) 
            {
                ex.printStackTrace();
            }
        }
        
        System.out.println("Taxi has completed all its duties. Ending thread");
        
        
        
    }
    
    public void stopTaxi()
    {
        
    }
            
}
