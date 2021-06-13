package view.pannel;

import javafx.fxml.FXML;

import java.awt.*;

public class PannelController {

    public Runnable onPlay, onPause, onStop,runForward,runBackward;


    public void play(){
        if(onPlay != null)
            onPlay.run();
    }

    public void pause(){
        if(onPause != null)
            onPause.run();
    }

    public void stop(){
        if(onStop != null)
            onStop.run();
    }

    public void forward() {
        if(runForward != null)
            runForward.run();
    }

    public void backward() {
        if(runBackward != null)
            runBackward.run();
    }

}
