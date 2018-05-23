/**
 *
 * @author liron
 */

//Took some inspiration from an online resource

//24 hour time style
public class Time
{
    private int hours;
    private int minutes;
    
    public Time()
    {
        //work day always begins at 9:00
        hours = 9;
        minutes = 0;
    }

    public String getTime()
    {
        String time = "";
        
        if (hours > 9 && minutes < 10)
        {
            time = hours + ":0" + minutes;
        } 
        else if (hours > 9 && minutes > 9)
        {
            time = hours + ":" + minutes;
        } 
        else if (hours < 10 && minutes < 10)
        {
            time = "0" + hours + ":0" + minutes;
        } 
        else if (hours < 10 && minutes > 9)
        {
            time = "0" + hours + ":" + minutes;
        } 
        else if (hours == 0)
        {
            time = "0" + hours + ":" + minutes;
        } 
        else if (minutes == 0)
        {
            time = hours + "0:" + minutes;
        }

        return time;
    }

    public void advanceTime(int min)
    {
        minutes += min;
        if (minutes >= 60)
        {
            minutes = minutes - 60;
            hours++;
        }
    }
}
