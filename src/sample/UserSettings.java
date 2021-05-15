package sample;

import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

public class UserSettings {

    public UserSettings() {}

    // -- Properties
    private Properties properties;

    // -- IP & Port
    private String ip;
    private String port;

    // -- Flight Control --
    private String aileron;
    private String elevator;
    private String rudder;
    private String throttle;
    private String altitude;
    private String airSpeed;
    private String heading;


    // GETTERS & SETTERS

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) { this.ip = ip; }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getAileron() {
        return aileron;
    }

    public void setAileron(String aileron) {
        this.aileron = aileron;
    }

    public String getElevator() {
        return elevator;
    }

    public void setElevator(String elevator) {
        this.elevator = elevator;
    }

    public String getRudder() {
        return rudder;
    }

    public void setRudder(String rudder) {
        this.rudder = rudder;
    }

    public String getThrottle() {
        return throttle;
    }

    public void setThrottle(String throttle) {
        this.throttle = throttle;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public String getAirSpeed() {
        return airSpeed;
    }

    public void setAirSpeed(String airSpeed) {
        this.airSpeed = airSpeed;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }
}

