package com.example.batterystats.core.batterystats;

import com.example.batterystats.core.batterystats.EnergyInfo;
import com.example.batterystats.core.batterystats.PowerProfile;
import com.example.batterystats.systrace.FrequencyData;

import java.util.List;

public class EnergyCalculator {
    public static void calculatePowerUsage(EnergyInfo EnergyInfo, FrequencyData FreqData){
        PowerProfile PowerProfile = new PowerProfile();
        EnergyUsed EnergyUsed = new EnergyUsed();
        System.out.println("now calculate Energy usage for \n");

        System.out.println("Bluetooth:");
        //Android uses: ((idleTimeMs * mIdleMa) + (rxTimeMs * mRxMa) + (txTimeMs * mTxMa))/(1000*60*60)
        EnergyUsed.bluetoothControllerIdleEnergy = PowerProfile.bluetoothControllerIdle*EnergyInfo.bluetoothData.idle;
        EnergyUsed.bluetoothControllerTxEnergy = PowerProfile.bluetoothControllerTx*EnergyInfo.bluetoothData.tx;
        EnergyUsed.bluetoothControllerRxEnergy = PowerProfile.bluetoothControllerRx*EnergyInfo.bluetoothData.rx;
        EnergyUsed.bluetoothControllerTotalEnergy = EnergyUsed.bluetoothControllerIdleEnergy +
                EnergyUsed.bluetoothControllerTxEnergy + EnergyUsed.bluetoothControllerRxEnergy;

        System.out.println("Per Uid Usage:");
        // for every uid we calculate the total power usage and the usage of each component inside

        // create the list of per uid Energy usage data


//        for uidEnergyStat in EnergyInfo.uidEnergyStatsList
//
//        EnergyUsed.uidEnergyUsedList.wifiEnergyPerUid.scan = PowerProfile.wifiScan*uidEnergyStat.wifiData.scan;
//        EnergyUsed.uidEnergyUsedList.wifiEnergyPerUid.idle = PowerProfile.wifiControllerIdle*uidEnergyStat.wifiData.idle;
//        EnergyUsed.uidEnergyUsedList.wifiEnergyPerUid.sleep = PowerProfile.wifiSleep*uidEnergyStat.wifiData.sleep;
//        EnergyUsed.uidEnergyUsedList.wifiEnergyPerUid.rx = PowerProfile.wifiRx*uidEnergyStat.wifiData.rx;
//        EnergyUsed.uidEnergyUsedList.wifiEnergyPerUid.tx = PowerProfile.wifiTx*uidEnergyStat.wifiData.tx;

        // then we also calculate the total WiFi, Camera and CPU usage by summing over all processes


        // calculate the total Energy used by the CPU taking into account the different frequencies
        System.out.println("\n ----------------------------- SILVER CLUSTER --------------------------------------");

        // we calculate the Energy per CPU
        for (int i = 0; i < FreqData.ClusterList.get(0).CPUList.size(); i++) {
            int mAhOfCPUTotal = 0;
            // we sum over all frequencies
            for (int j = 0; j < PowerProfile.mAhUsedAtFrequenciesSilver.size(); j++) {
                mAhOfCPUTotal += PowerProfile.mAhUsedAtFrequenciesSilver.get(j) * FreqData.ClusterList.get(0).CPUList.get(i).timeSpentAtFrequenciesSilver.get(j);
            }
            EnergyUsed.mAhPerCPUSilver.set(i, mAhOfCPUTotal);
            System.out.println("CPU" + FreqData.ClusterList.get(0).CPUList.get(i).CPUId + " used mAh: " + mAhOfCPUTotal);
        }

        System.out.println("\n ----------------------------- GOLD CLUSTER --------------------------------------");
        for (int i = 0; i < FreqData.ClusterList.get(1).CPUList.size(); i++) {
            int mAhOfCPUTotal = 0;
            // we sum over all frequencies
            for (int j = 0; j < PowerProfile.mAhUsedAtFrequenciesGold.size(); j++) {
                mAhOfCPUTotal += PowerProfile.mAhUsedAtFrequenciesGold.get(j) * FreqData.ClusterList.get(1).CPUList.get(i).timeSpentAtFrequenciesGold.get(j);
            }
            EnergyUsed.mAhPerCPUGold.set(i, mAhOfCPUTotal);
            System.out.println("CPU" + FreqData.ClusterList.get(1).CPUList.get(i).CPUId + " used mAh: " + mAhOfCPUTotal);
        }

        System.out.println("\n ----------------------------- TOTALS CLUSTER --------------------------------------");
        int totalSilverClustermAh = 0;
        int totalGoldClustermAh = 0;
        for (Integer mAhOfFreq : EnergyUsed.mAhPerCPUSilver){
            totalSilverClustermAh += mAhOfFreq;
        }
        for (Integer mAhOfFreq : EnergyUsed.mAhPerCPUGold){
            totalGoldClustermAh += mAhOfFreq;
        }
        System.out.println("total silver mAh " + totalSilverClustermAh);
        System.out.println("total gold mAh " + totalGoldClustermAh);

    }
}
