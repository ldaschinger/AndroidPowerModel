package com.example.batterystats.core.batterystats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// this class stores the estimated used energy for different components and for every uid
public class EnergyUsed {

    public EnergyUsed() {
        this.uidEnergyUsedList = new ArrayList<>();
    }

    float bluetoothControllerIdleEnergy = 0;
    float bluetoothControllerTxEnergy = 0;
    float bluetoothControllerRxEnergy = 0;
    float bluetoothControllerTotalEnergy = 0;

    
    float totalWifiEnergy = 0;
    float totalAudioEnergy = 0;
    float totalCameraEnergy = 0;
    float totalCPUEnergy = 0;

    float totalmAhAllCluster = 0;

    // every uid gets an energy estimation
    static class uidEnergyUsed implements Comparable<uidEnergyUsed>{
        String uid = "9999";
        List<processEnergyPerUid> processEnergyPerUidList;
        wifiEnergyPerUid wifiEnergy;
        float totalWifiEnergy;
        totalCPUEnergyUid CPUEnergy;
        float audioEnergyPerUid = 0;
        float cameraEnergyPerUid = 0;

        float totalEnergy = 0;

        public uidEnergyUsed() {
            this.wifiEnergy = new wifiEnergyPerUid();
            this.CPUEnergy = new totalCPUEnergyUid();
            this.processEnergyPerUidList = new ArrayList<>();
        }

        // class of process for every uid with respective CPU user and kernel time
        static class processEnergyPerUid{
            String processName = "";
            int userCPUTime = 0;
            int kernelCPUTime = 0;
        }

        // CPU usage time is divided in user and kernel
        static class totalCPUEnergyUid{
            float totalUserCPU= 0;
            float totalKernelCPU = 0;
        }

        // class to store Wi-Fi usage
        static class wifiEnergyPerUid{
            float scan = 0;
            float sleep = 0;
            float idle = 0;
            float rx = 0;
            float tx = 0;
        }

        // custom compare function for class for two floats
        @Override
        public int compareTo(uidEnergyUsed u) {
            return Float.compare(totalEnergy, u.totalEnergy);
        }
    }

    List<uidEnergyUsed> uidEnergyUsedList; //utilization stats for every uid

}
