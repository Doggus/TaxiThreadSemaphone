/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author tldlir001
 */
public class Branch
{
    private int id;
    private int duration;
    
    public Branch(int i, int d)
    {
        id = i;
        duration = d;
    }

    public int getId()
    {
        return id;
    }

    public int getDuration()
    {
        return duration;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public void setDuration(int duration)
    {
        this.duration = duration;
    }
    
    
}
