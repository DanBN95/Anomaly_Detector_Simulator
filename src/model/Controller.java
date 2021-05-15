package model;

import PTM1.Helpclass.TimeSeries;


public interface Controller {

    public void setTimeSeries(TimeSeries ts);
    public void play(int start,int rate);
    public void pause();
    public void stop();
}
