/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author liron
 */

// This class is used as a medium between taxi and Person
// It also allows to print out neccesary trace messages 

public class Trace
{
    private String trace;
    
    public Trace(String str)
    {
        trace = str;
    }

    public String getTrace() 
    {
        return trace;
    }

    public void setTrace(String str) 
    {
        trace = str;
    }
}
