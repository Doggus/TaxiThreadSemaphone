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
    
    public Person(int i, List<Branch> s)
    {
        id = i;
        schedule = s;
        
    }
    
    public void run()
    {
        System.out.println("Person: " + id + " thread created");
        try
        {
           
            
            for (int i = 0; i < schedule.size(); i++)
            {
                System.out.println("Person: " + id + " is Currently at branch " + schedule.get(i).id);
                Thread.sleep(17*schedule.get(i).duration); //person waits at branch 
            }
        }
        catch(Exception ex)
        {
            System.out.println(ex);
        }
        
    }
    
    public void travel(Branch b)
    {
        
    }
}
