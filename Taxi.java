
import java.util.*;
import java.util.concurrent.Semaphore;

/**
 *
 * @author tldlir001
 */
public class Taxi extends Thread
{
    
    private enum State
    {
        INBOUND, OUTBOUND, IDLE
    };
    
    private List<Rider> riders;
    private List<Rider> waiting;
    private List<Person> excluded;

    private Semaphore[] blocks;

    private State status;

    private int currentBranch;
    private int destinationBranch;

    private final int end;
    private Time time;

    private boolean firstStop;

    public Taxi(int numberOfBranches)
    {
        time = new Time();
        firstStop = true;

        List<Rider> tempHailed = new ArrayList();
        List<Rider> tempPassengers = new ArrayList();
        List<Person> tempBanned = new ArrayList();

        waiting = Collections.synchronizedList(tempHailed);
        riders = Collections.synchronizedList(tempPassengers);
        excluded = Collections.synchronizedList(tempBanned);

        // Creates an array of Semaphores for each branch (will stop here)
        blocks = new Semaphore[numberOfBranches];

        for (int i = 0; i < numberOfBranches; i++)
        {
            // Fair semaphore for ordering

            Semaphore stop = new Semaphore(1, true);
            blocks[i] = stop;

            // Taxi needs to acquire all the locks for the branches
            try
            {
                blocks[i].acquire();
            } 
            catch (InterruptedException ex)
            {
                ex.printStackTrace();
            }
        }

        currentBranch = 0;
        end = numberOfBranches - 1;
        destinationBranch = end;
    }

}
