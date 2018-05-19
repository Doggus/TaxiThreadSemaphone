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
    int currentBranchID;
    String time;
    int c = 0;
    List<Person> people = new ArrayList();
    
    public Taxi(List<Person> p)
    {
        people = p;
        currentBranchID = 0;
        time = "9:00";
    }
    
    public void run()
    {
        System.out.println("Taxi thread created");
        try
        {
            for (int i = 0; i < 5; i++)
            {
                Thread.sleep(2000);
                System.out.println("Hi this is taxi thread");
            }
        }
        catch(Exception ex)
        {
            System.out.println(ex);
        }
        
    }
    
    public void depart(int branchID)
    {
        try
        {
            System.out.println(time + ": Departing from branch: " + currentBranchID);
            Thread.sleep(17*2);
            currentBranchID = branchID;
            c+=2;
            String newTime = time.substring(0,2) + "0" + c;
            System.out.println(newTime + ": Arriving at branch: " + currentBranchID);
        } 
        catch (Exception ex)
        {
            System.out.println(ex);
        }
    }
    
    public void stopTaxi()
    {
        
    }
            
}
