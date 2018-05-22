/**
 *
 * @author liron
 */

public class Rider
{
    private int branchID;
    private Person person;

    public Rider(int loc, Person p)
    {
        this.branchID = loc;
        this.person = p;
    }

    public int getBranchID()
    {
        return branchID;
    }

    public Person getPerson()
    {
        return person;
    }

    public void setBranchID(int branchID)
    {
        this.branchID = branchID;
    }

    public void setPerson(Person person)
    {
        this.person = person;
    }
    
    
    
}
