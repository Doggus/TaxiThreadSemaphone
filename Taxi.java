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
    //refer to assignment notes regarding the time system
    
    int currentBranchID;
    int numBranches;
    List<Person> people = new ArrayList();
    
    int branchMax;
    int branchMin;
    
    //gobal variable used to help determine when to wait or not
    int wait = 0;
    //initial direction is outbound
    int direction = 0;
    
    //trace
    Trace trace;
    
    public Taxi(List<Person> p, int b , Trace t)
    {
        people = p;
        currentBranchID = 0; //always begins at current branch
        trace = t;
        numBranches = b;
        //Initialise (necesary for finding max)
        //branchMax = people.get(0).schedule.get(0).id;
        //branchMin = 0; //minimum branch is always 0
       
    }
    
                      //if i dont remove people taxi will run forever!!!!!!!!!!!!
//                    if (people.get(i).schedule.isEmpty())
//                    {
//                        people.remove(i);
//                    }
    
    
                      //if a passenger was dropped off
//                    if (wait>0)
//                    {
//                        //taxi waits for one minute
//                        Thread.sleep(17*1);
//                        //reset wait
//                        wait = 0;
//                    }
    
    public void run()
    {
        System.out.println("Taxi thread created");
        
        while(!(people.isEmpty()))
        {
           
            try
            {
                for (int i = 0; i < people.size(); i++)
                {
                    //if person is getting off at current branch
                    if (people.get(i).schedule.get(0).id == currentBranchID)
                    {
                        //increment wait int
                        wait++;
                       
                        //will cause person thread to do work and then wait for taxi
                        System.out.println("Taxi Dropping off Person " + people.get(i).id + "at Branch: " + currentBranchID);
                        people.get(i).setTaxiLocation(currentBranchID);
                        
                    }
                       
                }

                //leaves current branch
                System.out.println("Taxi travels from branch: " + currentBranchID);
                //travles for two minutes
                Thread.sleep(17*2);
                //arrives at next branch (depending on direction currentBranch will change)
                if (direction == 0) //outbound
                {
                    //advance to next branch
                    currentBranchID++;
                    if (currentBranchID == numBranches-1) //if current branch is furthest outbound
                    {
                        //start travelling inbound
                        direction = 1;
                    }
                }
                else //inbound
                {
                    //advance to next branch
                    currentBranchID--;
                    if (currentBranchID == 0)
                    {
                        //start travelling outbound
                        direction = 0;
                    }
                }
                
                System.out.println("Taxi Arrives at branch: " + currentBranchID);
                for (int i = 0; i < people.size(); i++)
                {
                    //update the taxi's location for people threads
                    people.get(i).setTaxiLocation(currentBranchID);
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
                        if(currentBranchID == people.get(i).schedule.get(0).id)
                        {
                            //remove branch from person's schedule
                            people.get(i).schedule.remove(0);
                            trace.setTrace("Taxi here to pick up person: " + people.get(i).id);
                            //stop Person thread from waiting
                            trace.notify();
                            //trace.notifyAll();
                            trace.setTrace("Taxi picks up person: " + people.get(i).id);
                        }
                    }

                    /* notifyAll() wakes up both the waiter threads
                       if notify() is used only one of them will wake up
                       and so the program will never end. */
                }
            } 
            catch (Exception ex) 
            {
                ex.printStackTrace();
            }
        }
        
       
        
        
        
    }
    
    public void stopTaxi()
    {
        
    }
            
}
