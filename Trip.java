/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semaphoretaxi;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tldlir001
 */
public class Trip
{
    int pID;
    List<Branch> branches;
     
     public Trip(int id, List<Branch> b)
     {
         pID = id;
         branches = b;
     }
     
     //use for testing delete when done
     public void print()
     {
         for (int i = 0; i < branches.size(); i++)
         {
             System.out.println("pID: " + pID + " branch ID: " + branches.get(i).id + " branch dur: " + branches.get(i).duration);
         }
     }
}
