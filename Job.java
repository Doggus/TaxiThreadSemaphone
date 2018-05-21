
/**
 *
 * @author tldlir001
 */
public class Job
{
    private int branchID;
    private int duration;
    
    public Job(int id, int d)
    {
        branchID = id;
        duration = d;
    }

    public int getBranchID()
    {
        return branchID;
    }

    public int getDuration()
    {
        return duration;
    }

    public void setId(int id)
    {
        branchID = id;
    }

    public void setDuration(int d)
    {
        duration = d;
    }
    
    
}
