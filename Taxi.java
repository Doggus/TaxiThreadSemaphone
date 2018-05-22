
/**
 *
 * @author liron
 */

import java.util.*;
import java.util.concurrent.Semaphore;

public class Taxi extends Thread
{
    //types of state the taxi can be in
    private enum State
    {
        INBOUND, OUTBOUND, IDLE
    };
    
    //people in riding in the taxi
    private List<Rider> Riders;
    //people waiting to be picked up
    private List<Rider> Waiting;
    //people who shouldn't be picked up ( so people cant get back on the taxi immediatly)
    private List<Person> Excluded;

    //list of semaphores that will be used to make people wait when necesary
    private Semaphore[] Blocks;

    //state of taxi
    private State state;

    //taxi's current location
    private int currentBranch;
    //taxi's destination location
    private int destinationBranch;
    
    //used to get initial pick up to work 
    private boolean initialStop;
    
    //simulated clock
    private Time time;
    
    //outbound boundry
    private int boundry;
    

    

    public Taxi(int numBranches)
    {
        
        initialStop = true;
        
        currentBranch = 0;
        boundry = numBranches - 1;
        destinationBranch = boundry;
             
        time = new Time();
           
        List<Rider> RidersTemp = new ArrayList();
        List<Rider> waitingTemp = new ArrayList();
        List<Person> excludedTemp = new ArrayList();

        //learnt about collections from docs
        Riders = Collections.synchronizedList(RidersTemp);
        Waiting = Collections.synchronizedList(waitingTemp);
        Excluded = Collections.synchronizedList(excludedTemp);
        
        // Creates an array of Semaphores for each stop
        Blocks = new Semaphore[numBranches];

        for (int i = 0; i < numBranches; i++)
        {
            
            //used for ordering (1 permit)
            Semaphore block = new Semaphore(1, true);
   
            Blocks[i] = block;
            

            // All the branch locks need to be aquired by the taxi(NB for later)
            try
            {
                Blocks[i].acquire();
            } 
            catch (InterruptedException ex)
            {
                ex.printStackTrace();
            }
        }

        
    }
     
    /* The hail method adds a person in the car (rider) to the waiting list.
       The first semaphore is acquired when the taxi gets released. the riders list
       will be added to after which the second semaphore is aquired (gets released
       when the taxi reaches its destination) */
    public void hail(Person p)
    {
        
        int id = p.getID();
        int curr = p.getCurrentLocation();
        int dest = p.getDestination();
        
        
        Rider hailed = new Rider(curr, p);
        
        Waiting.add(hailed);
            
        try 
        {
            // Person must wait if they attempt to get back in the taxi straight away
            if(Excluded.contains(p))
            {
                p.notAvailable();
            }
            
            System.out.println(time.getTime() + " branch " + curr + ": person " + id + " hail");
            
            //block this current branch
            Blocks[curr].acquire();
                               
            System.out.println(time.getTime() + " branch " + currentBranch + ": person " + id + " request " + dest);
            Rider r = new Rider(dest,p);
            Riders.add(r);
            
            Waiting.remove(hailed);
            
            Blocks[curr].release();
            Blocks[dest].acquire();
            
            Riders.remove(r);
            
            Excluded.add(p);
            
            Blocks[dest].release();
        }
        catch (InterruptedException ex)
        {
            ex.printStackTrace();
        }
    }
     
    //Used to see if a rider list contains a specific branch in it (in other words, when to dropOff or move on)
    private boolean containsBranch(int branchNum, List<Rider> r)
    {
        for(int i = 0; i < r.size(); i++)
        {
            if(r.get(i).getBranchID() == branchNum)
            {
                return true;
            }
        }
        return false;
    }
    
    //make sure these people can be picked up again (after intitial drop off)
    private void clearExcludedPeople()
    {
        for(int i = 0; i < Excluded.size();i++)
        {
            Excluded.get(i).Available();
            Excluded.remove(i);
        }
    }
    
    //If no people need to get on or off at current branch the taxi simply moves on
    //to the next branch (outbound or inbound)
    private void continueTravelling()
    {
        if(state == State.OUTBOUND)
        {       
            try 
            {
                //make thread sleep but don't advance time (for the simulations sake)
                this.sleep(17*2);
            } 
            catch (InterruptedException ex)
            {
                ex.printStackTrace();
            }
            //advance to next outbound branch
            currentBranch++;
            
        }
        else if(state == State.INBOUND)
        {      
            try 
            {
                //make thread sleep but don't advance time (for the simulations sake)
                this.sleep(17*2);
            } 
            catch (InterruptedException ex)
            {
                ex.printStackTrace();
            }
            
            //advance to next inbound branch
            currentBranch--;
        }
    }
      
    /* this method is used to simulate picking up and dropping off people.
       depending on whether if taxi is inbound or outbound it arrives as the branch
       and releases that branch's sempahore block and then attempts to reacquire that
       semaphore again, it will only acquire that semaphore if all riders being dropped 
       off have disembarked and all riders getting on the taxi have gotten on succesfully */
    private void pickupOrDropOff()
    {
        if(state == State.OUTBOUND)
        {
            try 
            {
                //NB for first pick up 
                if(initialStop == true)
                {
                    initialStop = false;
                }
                else
                {
                    //travel for two minutes
                    this.sleep(17*2);
                    time.advanceTime(2);
                }
                
                System.out.println(time.getTime() + " branch " + currentBranch + ": taxi arrive");
            
                Blocks[currentBranch].release();
                
                //wait for 1 minute
                this.sleep(17*1);
                time.advanceTime(1);
                
                Blocks[currentBranch].acquire();
                System.out.println(time.getTime() + " branch "+ currentBranch + ": taxi depart.");
                
                //Clear list of excluded people after leaving stop
                clearExcludedPeople();
            } 
            catch (InterruptedException ex)
            {
                ex.printStackTrace();
            }
            
            //advance current Branch outwards
            currentBranch++;
        }
        else
        {
            try 
            {
                //travel for 2 minutes
                this.sleep(17*2);
                time.advanceTime(2);
                
                System.out.println(time.getTime() + " branch " + currentBranch + ": taxi arrive");
            
                Blocks[currentBranch].release();
                
                //wait for 1 minute at branch
                this.sleep(17*1);
                time.advanceTime(1);
                
                Blocks[currentBranch].acquire();
                
                System.out.println(time.getTime() + " branch " + currentBranch + ": taxi depart.");
                
                //Clear list of excluded people after leaving stop
                clearExcludedPeople();
            } 
            catch (InterruptedException ex)
            {
                ex.printStackTrace();
            }
            
            //advance current branch backwards
            currentBranch--;
        }
    }
    
    
    // the taxi travels to a destination
    private void travel()
    {
        if(containsBranch(currentBranch,Waiting) || containsBranch(currentBranch,Riders))
        {
            //will pick and/or drop off people
            pickupOrDropOff();
        }
        else
        {
            //will continue to next branch
            continueTravelling();
        }
    }
    
    //thread will run according to the taxis current state
    public void run()
    {
        while(Thread.activeCount() > 2)
        {
            State();
            
            while(state != State.IDLE)
            {
                State();
                travel();
            }
            
            while(Thread.activeCount() > 2 && state == State.IDLE)
            {
                State();
            }
            
            
        }
    }
    
    //The following method changes the taxi's states when neccesary
    //(idea to use states was given to me by a friend - has helped me tremendously)
    private void State()
    {
        
        if(destinationBranch == boundry && currentBranch < destinationBranch)
        {
            //taxi is inbound
            state = State.OUTBOUND;
        }
        else if(destinationBranch==0 && currentBranch == destinationBranch)
        { 
            //switches from inbound to outbound
            state = State.OUTBOUND;
            destinationBranch = boundry;
        }
        else if(destinationBranch == 0 && currentBranch > destinationBranch)
        {
            //taxi is inbound
            state = State.INBOUND;
        }
        else if(currentBranch == destinationBranch && destinationBranch == boundry)
        {
            //switches from outbound to inbound
            state = State.INBOUND;
            destinationBranch = 0;
        }
        else if(Waiting.isEmpty() && Riders.isEmpty())
        {
            //taxi is idle
            state = State.IDLE;
            //clear list of excluded people
            clearExcludedPeople();
        }
        
    }

}
