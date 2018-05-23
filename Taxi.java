
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
        INBOUND,
        OUTBOUND,
        IDLE
    };

    //people who are riding in the taxi
    private List<Rider> Riders;
    //people waiting to be picked up
    private List<Rider> Waiting;
    //people who shouldn't be picked up (so people cant get back on the taxi immediatly)
    private List<Person> Excluded;

    //list of semaphores that will lock and unlock branches, to simulate people/taxi waiting when neccesary
    //(initially tried to use list, but caused issues)
    private Semaphore[] Blocks;

    //state of taxi (NB)
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
    //number of people threads still running (will decrement when a person has completed their schedule)
    private int personCount;

    public Taxi(int numBranches, int numPeople)
    {
        //will always be true when a taxi is instantiated
        initialStop = true;
   
        personCount = numPeople;

        //taxi always begins at branch 0
        currentBranch = 0;
        //outbound boundry (e.g. 3 branchs including HQ furthest branch will be 2)
        boundry = numBranches - 1;
        //initial destination will be to drive outward to furthest branch
        destinationBranch = boundry;
       
        time = new Time();
        
        //used to synchronise the NB lists that the taxi uses
        List<Rider> RidersTemp = new ArrayList();
        List<Rider> waitingTemp = new ArrayList();
        List<Person> excludedTemp = new ArrayList();

        //Important that the lists are synchronised with all the different threads runnin
        //refer to docs about collections.syncrhonizedList
        Riders = Collections.synchronizedList(RidersTemp);
        Waiting = Collections.synchronizedList(waitingTemp);
        Excluded = Collections.synchronizedList(excludedTemp);

        // array of semaphores created with size = to the number of branches (lock for each branch)
        Blocks = new Semaphore[numBranches];

        for (int i = 0; i < numBranches; i++)
        {

            //this line is NB for ordering
            Semaphore block = new Semaphore(1, true);

            //populates array
            Blocks[i] = block;
 
            try
            {
                // All the branch locks need to be aquired by the taxi(so that all branches start with 0 permits)
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
       will be added to after which the second semaphore is aquired (it gets released
       when the taxi reaches its destination) */
    public void hail(Person p)
    {
        //get neccesary details from person who hailed
        int id = p.getID();
        int curr = p.getCurrentLocation();
        int dest = p.getDestination();
        
        //new rider (temp) who called hail
        Rider hailed = new Rider(curr, p);

        //added rider who hailed to the waiting list
        Waiting.add(hailed);

        try
        {
            // Person must wait if they attempt to get back in the taxi straight away
            if (Excluded.contains(p))
            {
                //make the person thread wait until taxi has left the branch
                p.notAvailable();
            }

            System.out.println(time.getTime() + " branch " + curr + ": person " + id + " hail");

            //block this current branch
            Blocks[curr].acquire();

            System.out.println(time.getTime() + " branch " + currentBranch + ": person " + id + " request " + dest);
            
            //adds a person as a rider with location = to the destination of hailed person (temporary)
            Rider r = new Rider(dest, p);
            Riders.add(r);
            
            //remove the hailed person from the waiting list (upon pick up)
            Waiting.remove(hailed);
            
            //unlock the person's current branch
            Blocks[curr].release();
            
            //locks the person's destination branch
            Blocks[dest].acquire();
            
            //remove the temorary rider
            Riders.remove(r);
            
            //add person to the excluded list so they won't be picked up immediatly after drop off
            Excluded.add(p);
            
            //release the destination branch's lock
            Blocks[dest].release();
        } 
        catch (InterruptedException ex)
        {
            ex.printStackTrace();
        }
    }

    //Used to see if a rider list contains a specific branch in it (in other words, when to drop off or continue to next branch)
    private boolean containsBranch(int branchNum, List<Rider> r)
    {
        //loop through all riders in the taxi
        for (int i = 0; i < r.size(); i++)
        {
            //if any of them contain a specific branchs then return true
            if (r.get(i).getBranchID() == branchNum)
            {
                return true;
            }
        }
        
        //otherwise return false
        return false;
    }

    //make sure these people can be picked up again (after intitial drop off)
    private void clearExcludedPeople()
    {
        for (int i = 0; i < Excluded.size(); i++)
        {
            //set to available for pickUp
            Excluded.get(i).Available();
            //remove them from people who are excluded from pickup
            Excluded.remove(i);
        }
    }

    //If no people need to get on or off at current branch the taxi simply moves on
    //to the next branch (outbound or inbound)
    private void continueTravelling()
    {
        if (state == State.OUTBOUND)
        {
            //advance to next outbound branch
            currentBranch++;

        } else if (state == State.INBOUND)
        {
            //advance to next inbound branch
            currentBranch--;
        }
    }

    /* this method is used to simulate picking up and dropping off people.
       depending on whether a taxi is inbound or outbound it arrives as the branch
       and releases that branch's sempahore block and then attempts to reacquire that
       semaphore again, it will only acquire that semaphore if all riders being dropped 
       off have disembarked and all riders getting on the taxi have gotten on succesfully */
    private void pickUpOrDropOff()
    {
        if (state == State.OUTBOUND)
        {
            try
            {
                //NB for initial pick up from HQ 
                if (initialStop == true)
                {
                    //will remain false for the rest of the simulation
                    initialStop = false;
                    //no travelling because taxi should start at HQ with the rest of the people
                } 
                else
                {
                    //travel for two minutes to a branch
                    this.sleep(17 * 2);
                    time.advanceTime(2);
                }

                System.out.println(time.getTime() + " branch " + currentBranch + ": taxi arrive");

                //unlock current branch to pick up or drop off people
                Blocks[currentBranch].release();

                //wait for 1 minute
                this.sleep(17 * 1);
                time.advanceTime(1);

                //relock it upon leaving
                Blocks[currentBranch].acquire();

                System.out.println(time.getTime() + " branch " + currentBranch + ": taxi depart.");

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
                this.sleep(17 * 2);
                time.advanceTime(2);

                System.out.println(time.getTime() + " branch " + currentBranch + ": taxi arrive");

                //unlock current branch to pick up or drop off people
                Blocks[currentBranch].release();

                //wait for 1 minute at branch
                this.sleep(17 * 1);
                time.advanceTime(1);

                //relock it upon leaving
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

    //the taxi travels to a destination
    private void travel()
    {
        //if someone is waiting to be picked up or someone needs to get off
        if (containsBranch(currentBranch, Waiting) || containsBranch(currentBranch, Riders))
        {
            //will pick and/or drop off people
            pickUpOrDropOff();
        } 
        else
        {
            //will continue to next branch
            continueTravelling();
        }
    }

    //is called when a person thread is done running
    public void decrementPeople()
    {
        personCount--;
    }
    
    public Time getClock()
    {
        return time;
    }

    //thread will run according to the taxis current state
    public void run()
    {
        //while there are still people threads running (their schedule isn't over)
        while (personCount > 0)
        {
            
            //immediatly check state
            State();
            
            //while taxi is not idle (check state and continue to 'travel')
            while (state != State.IDLE)
            {
                State();
                travel();
            }
            
            //if taxi is idle, continually check if there is a change in state
            while (personCount > 0 && state == State.IDLE)
            {
                State();
            }

        }

    }

    //The following method changes the taxi's states when neccesary
    //(idea to use states was given to me by a friend - has helped me tremendously)
    private void State()
    {
        //NB that first check is to see if idle (causes thread to never end otherwise)
        if (Waiting.isEmpty() && Riders.isEmpty())
        {
            //taxi is idle
            state = State.IDLE;
            //clear list of excluded people
            clearExcludedPeople();
        } 
        else if (destinationBranch == boundry && currentBranch < destinationBranch)
        {
            //taxi is inbound
            state = State.OUTBOUND;
        } 
        else if (destinationBranch == 0 && currentBranch == destinationBranch)
        {
            //switches from inbound to outbound
            state = State.OUTBOUND;
            destinationBranch = boundry;
        } 
        else if (destinationBranch == 0 && currentBranch > destinationBranch)
        {
            //taxi is inbound
            state = State.INBOUND;
        } 
        else if (currentBranch == destinationBranch && destinationBranch == boundry)
        {
            //switches from outbound to inbound
            state = State.INBOUND;
            destinationBranch = 0;
        }

    }

}
