package view.pannel;

public class PannelController {

    public Runnable onPlay, onPause, onStop;

    public void play(){
        if(onPlay!=null)
            onPlay.run();
    }

    public void pause(){
        if(onPause!=null)
            onPause.run();
    }

    public void stop(){
        if(onStop!=null)
            onStop.run();
    }
}
