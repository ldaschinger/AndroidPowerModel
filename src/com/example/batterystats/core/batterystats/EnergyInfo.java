package com.example.batterystats.core.batterystats;

import java.util.ArrayList;
import java.util.List;

public class EnergyInfo {

    // class for the list of matching uid to package names
    static class uidToPackage {
        int uid = 9999;
        String packageName = "";
    }

    // class for the global utilization stats of Bluetooth
    static class bluetooth {
        int idle = 0;
        int rx = 0;
        int tx = 0;
    }



    // class of process for every uid with respective CPU user and kernel time
    static class processPerUid{
        String processName = "";
        int userCPUTime = 0;
        int kernelCPUTime = 0;
    }

    static class totalCPUUid{
        int userCPUTime = 0;
        int kernelCPUTime = 0;
    }

    // class to store Wi-Fi usage
    static class wifiPerUid{
        int scan = 0;
        int sleep = 0;
        int idle = 0;
        int rx = 0;
        int tx = 0;
    }



    // class containing all the information for one uid/package
    static class uidEnergyStats {
        String uid = "9999";
        // list of each process and its CPU usage
        List<processPerUid> processPerUidList;
        wifiPerUid wifiData;
        totalCPUUid CPUData;
        int audioPerUid = 0;
    }


//    private List<uidToPackage> uidToPackageList;

    // lists and values for one batterystat evaluation
    List<uidToPackage> uidToPackageList; //mapping package to uid
    bluetooth bluetoothData; //global bluetooth data
    static List<uidEnergyStats> uidEnergyStatsList; //utilization stats for every uid

    public EnergyInfo() {
//        this.cpuFrequencies = new ArrayList<>();
        this.uidToPackageList = new ArrayList<>();
        this.uidEnergyStatsList = new ArrayList<>();
    }


//    public EnergyInfo(EnergyInfo toClone) {
//        this.entrance = toClone.getEntrance();
//        this.exit = toClone.getExit();
//        this.volt = toClone.getVoltage();
//        this.devices = new ArrayList<>(toClone.getDevices());
//        this.cpuFrequencies = new ArrayList<>(toClone.getCpuFrequencies());
//        this.phoneSignalStrength = toClone.getPhoneSignalStrength();
//    }

//    public int getEntrance() {
//        return entrance;
//    }
//
//    public void setEntrance(int entrance) {
//        this.entrance = entrance;
//    }
//
//    public int getExit() {
//        return exit;
//    }
//
//    void setExit(int exit) {
//        this.exit = exit;
//    }
//
//    public int getVoltage() {
//        return volt;
//    }
//
//    void setVoltage(int volt) {
//        this.volt = volt;
//    }
//
//    public List<String> getDevices() {
//        return devices;
//    }
//
//    void addDevice(String device) {
//        this.devices.add(device);
//    }
//
//    void removeDevice(String device) {
//        for (int i = 0; i < this.getDevices().size(); i++) {
//            if (this.getDevices().get(i).equals(device)) {
//                this.devices.remove(i);
//            }
//        }
//    }

//    public List<Integer> getCpuFrequencies() {
//        return cpuFrequencies;
//    }
//
//    public void setCpuFrequencies(List<Integer> cpuFrequencies) {
//        this.cpuFrequencies = new ArrayList<>(cpuFrequencies);
//    }



//    public List<String> getuidToPackage() {
//        return uidToPackageList;
//    }
//
//    public void setuidToPackage(List<String> uidToPackage) {
//        this.uidToPackageList = new ArrayList<>(uidToPackage);
//    }

//    public int getPhoneSignalStrength() {
//        return phoneSignalStrength;
//    }

//    void setPhoneSignalStrength(int phoneSignalStrength) {
//        this.phoneSignalStrength = phoneSignalStrength;
//    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        EnergyInfo that = (EnergyInfo) o;
//
//        return entrance == that.entrance && exit == that.exit && volt == that.volt && cpuFrequencies.equals(that.cpuFrequencies) &&
//                devices.equals(that.devices) && phoneSignalStrength == that.phoneSignalStrength;
//    }
}
