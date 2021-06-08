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

    private HashMap<String,Properties> hsm;
    // -- Properties
    private Properties properties;

    // -- IP & Port
    private String ip;
    private String port;

    public HashMap<String, Properties> getHsm() {
        return hsm;
    }

    public void setHsm(HashMap<String, Properties> hsm) {
        this.hsm = hsm;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
//
//    // -- Flight Control --
//    private String aileron;     // X joystick
//    private String elevator;    // Y joystick
//    private String rudder;      // horizontal
//    private String throttle;    // vertical
//    private String altitude;    // altimeter altitude(height)
//    private String airspeed;
//    private String heading;     // direction
//    private String roll;
//    private String pitch;
//    private String yaw;
//
//
//    // GETTERS & SETTERS
//
//    public Properties getProperties() {
//        return properties;
//    }
//
//    public void setProperties(Properties properties) {
//        this.properties = properties;
//    }
//
//    public String getIp() {
//        return ip;
//    }
//
//    public void setIp(String ip) { this.ip = ip; }
//
//    public String getPort() {
//        return port;
//    }
//
//    public void setPort(String port) {
//        this.port = port;
//    }
//
//    public String getAileron() {
//        return aileron;
//    }
//
//    public void setAileron(String aileron) {
//        this.aileron = aileron;
//    }
//
//    public String getElevator() {
//        return elevator;
//    }
//
//    public void setElevator(String elevator) {
//        this.elevator = elevator;
//    }
//
//    public String getRudder() {
//        return rudder;
//    }
//
//    public void setRudder(String rudder) {
//        this.rudder = rudder;
//    }
//
//    public String getThrottle() {
//        return throttle;
//    }
//
//    public void setThrottle(String throttle) {
//        this.throttle = throttle;
//    }
//
//    public String getAltitude() {
//        return altitude;
//    }
//
//    public void setAltitude(String altitude) {
//        this.altitude = altitude;
//    }
//
//    public String getAirSpeed() {
//        return airspeed;
//    }
//
//    public void setAirSpeed(String airSpeed) {
//        this.airspeed = airSpeed;
//    }
//
//    public String getHeading() {
//        return heading;
//    }
//
//    public void setHeading(String heading) {
//        this.heading = heading;
//    }
//
//    public String getRoll() {
//        return roll;
//    }
//
//    public void setRoll(String roll) {
//        this.roll = roll;
//    }
//
//    public String getPitch() {
//        return pitch;
//    }
//
//    public void setPitch(String pitch) {
//        this.pitch = pitch;
//    }
//
//    public String getYaw() {
//        return yaw;
//    }
//
//    public void setYaw(String yaw) {
//        this.yaw = yaw;
//    }
}

