package com.example.batterystats.core.batterystats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EnergyUsed {

    public EnergyUsed() {
        this.uidEnergyUsedList = new ArrayList<>();
        this.mAhPerCPUSilver = new ArrayList<>(Collections.nCopies(6, 0)); // 6 CPUs
        this.mAhPerCPUGold = new ArrayList<>(Collections.nCopies(2, 0)); // 2 CPUs

    }

    int bluetoothControllerIdleEnergy = 0;
    int bluetoothControllerTxEnergy = 0;
    int bluetoothControllerRxEnergy = 0;
    int bluetoothControllerTotalEnergy = 0;

    List<Integer> mAhPerCPUSilver;
    List<Integer> mAhPerCPUGold;


    static class uidEnergyUsed {
        List<processEnergyPerUid> processEnergyPerUidList;
        wifiEnergyPerUid wifiEnergy;
        totalCPUEnergyUid CPUEnergy;
        int audioEnergyPerUid = 0;

        public uidEnergyUsed() {
            this.processEnergyPerUidList = new ArrayList<>();
        }

        // class of process for every uid with respective CPU user and kernel time
        static class processEnergyPerUid{
            String processName = "";
            int userCPUTime = 0;
            int kernelCPUTime = 0;
        }

        static class totalCPUEnergyUid{
            int totalUserCPUTime = 0;
            int totalKernelCPUTime = 0;
        }

        // class to store Wi-Fi usage
        static class wifiEnergyPerUid{
            int scan = 0;
            int sleep = 0;
            int idle = 0;
            int rx = 0;
            int tx = 0;
        }
    }

    static List<uidEnergyUsed> uidEnergyUsedList; //utilization stats for every uid

}
