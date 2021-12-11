package com.example.batterystats.core.batterystats;

import com.example.batterystats.core.batterystats.EnergyInfo;
import com.example.batterystats.core.batterystats.PowerProfile;

import java.util.List;

public class EnergyCalculator {
    public static void calculatePowerUsage(EnergyInfo EnergyInfo){
        PowerProfile PowerProfile = new PowerProfile();
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
    }
}
