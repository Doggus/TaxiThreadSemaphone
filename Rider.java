/**
 *
 * @author liron
 */

public class Rider
{
    private int branchID;
    private Person person;

    public Rider(int id, Person p)
    {
        this.branchID = id;
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
