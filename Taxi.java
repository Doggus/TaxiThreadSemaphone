/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semaphoretaxi;

import java.util.*;

/**
 *
 * @author tldlir001
 */
public class Taxi implements Runnable
{
    List<Person> people = new ArrayList();
    
    public Taxi(List<Person> p)
    {
        people = p;
    }
    
    public void run()
    {
        System.out.println("Taxi thread created");
    }
    
    public void depart(Branch b)
    {
        
    }
    
    public void stopTaxi()
    {
        
    }
            
}
