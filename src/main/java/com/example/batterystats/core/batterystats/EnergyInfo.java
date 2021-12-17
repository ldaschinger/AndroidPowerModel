package com.example.batterystats.core.batterystats;

import java.util.ArrayList;
import java.util.List;

public class EnergyInfo {

    // class for the list of matching uid to package names
    static class uidToPackage {
        String uid = "9999";
        String packageName = "";
    }

    // class for the global utilization stats of Bluetooth
    static class bluetooth {
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
        int cameraPerUid = 0;

        public uidEnergyStats() {
            this.processPerUidList = new ArrayList<>();
            this.wifiData = new wifiPerUid();
            this.CPUData = new totalCPUUid();
        }

        // class of process for every uid with respective CPU user and kernel time
        static class processPerUid{
            String processName = "";
            int userCPUTime = 0;
            int kernelCPUTime = 0;
        }

        static class totalCPUUid{
            int totalUserCPUTime = 0;
            int totalKernelCPUTime = 0;
        }

        // class to store Wi-Fi usage
        static class wifiPerUid{
            int scan = 0;
            int sleep = 0;
            int idle = 0;
            int rx = 0;
            int tx = 0;
        }
    }

    // lists and values for one batterystat evaluation
    List<uidToPackage> uidToPackageList; //mapping package to uid
    bluetooth bluetoothData; //global bluetooth data
    int wifiIdle = 0;
    List<uidEnergyStats> uidEnergyStatsList; //utilization stats for every uid

    public EnergyInfo() {
        this.uidToPackageList = new ArrayList<>();
        this.uidEnergyStatsList = new ArrayList<>();
        this.bluetoothData = new bluetooth();
    }
}
