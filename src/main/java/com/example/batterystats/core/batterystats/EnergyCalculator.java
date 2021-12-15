package com.example.batterystats.core.batterystats;

import com.example.batterystats.systrace.FrequencyData;
import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.PythonConfig;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

public class EnergyCalculator {
    static int MS_IN_HR = 1000*60*60;

    public static void calculatePowerUsage(EnergyInfo EnergyInfo, FrequencyData FreqData){
        PowerProfile PowerProfile = new PowerProfile();
        EnergyUsed EnergyUsed = new EnergyUsed();

        // use the frequency data of the CPUs to get a good estimate of power used by the CPUs
        calculateCPUPower(PowerProfile, FreqData);

        System.out.println("\n \n now calculate Energy usage per uid and per component \n");
        //----------------------------- BLUETOOTH --------------------------------------
        //Android uses: ((idleTimeMs * mIdleMa) + (rxTimeMs * mRxMa) + (txTimeMs * mTxMa))/(1000*60*60)
        EnergyUsed.bluetoothControllerIdleEnergy = (PowerProfile.bluetoothControllerIdle * EnergyInfo.bluetoothData.idle)/MS_IN_HR;
        EnergyUsed.bluetoothControllerTxEnergy = (PowerProfile.bluetoothControllerTx * EnergyInfo.bluetoothData.tx)/MS_IN_HR;
        EnergyUsed.bluetoothControllerRxEnergy = (PowerProfile.bluetoothControllerRx * EnergyInfo.bluetoothData.rx)/MS_IN_HR;
        EnergyUsed.bluetoothControllerTotalEnergy = EnergyUsed.bluetoothControllerIdleEnergy +
                EnergyUsed.bluetoothControllerTxEnergy + EnergyUsed.bluetoothControllerRxEnergy;

        //----------------------------- PER UID USAGE --------------------------------------
        float totalProcessesEnergy = 0;
        // create the list of per uid Energy usage data
        for (EnergyInfo.uidEnergyStats uidEnergyStat: EnergyInfo.uidEnergyStatsList){
            // create a new uidEnergyUsed object, then populate it and then add to the uidEnergyUsedList
            EnergyUsed.uidEnergyUsed uidEnergyUsed = new EnergyUsed.uidEnergyUsed();
            uidEnergyUsed.uid = uidEnergyStat.uid;

            //----------------------------- WIFI --------------------------------------
            // Android uses ((idleTimeMs * mIdleCurrentMa) + (txTimeMs * mTxCurrentMa) + (rxTimeMs * mRxCurrentMa) + (wifiScanTimeMs * mWifiPowerScan) )/ (1000*60*60)
            uidEnergyUsed.wifiEnergy.scan = (PowerProfile.wifiScan * uidEnergyStat.wifiData.scan)/MS_IN_HR;
            uidEnergyUsed.wifiEnergy.idle = (PowerProfile.wifiControllerIdle * uidEnergyStat.wifiData.idle)/MS_IN_HR;
            uidEnergyUsed.wifiEnergy.sleep = (PowerProfile.wifiSleep * uidEnergyStat.wifiData.sleep)/MS_IN_HR;
            uidEnergyUsed.wifiEnergy.rx = (PowerProfile.wifiControllerRx * uidEnergyStat.wifiData.rx)/MS_IN_HR;
            uidEnergyUsed.wifiEnergy.tx = (PowerProfile.wifiControllerTx * uidEnergyStat.wifiData.tx)/MS_IN_HR;
            uidEnergyUsed.totalWifiEnergy = uidEnergyUsed.wifiEnergy.scan + uidEnergyUsed.wifiEnergy.idle + uidEnergyUsed.wifiEnergy.sleep +
                    uidEnergyUsed.wifiEnergy.rx + uidEnergyUsed.wifiEnergy.tx;

            EnergyUsed.totalWifiEnergy += uidEnergyUsed.totalWifiEnergy;
            //----------------------------- CAMERA --------------------------------------
            // Android uses (totalTime * mCameraPowerOnAvg) / (1000*60*60);
            uidEnergyUsed.cameraEnergyPerUid = (PowerProfile.cameraAvg * uidEnergyStat.cameraPerUid)/MS_IN_HR;
            EnergyUsed.totalCameraEnergy += uidEnergyUsed.cameraEnergyPerUid;

            //----------------------------- AUDIO --------------------------------------
            // Android uses (totalTime * mAudioAveragePowerMa) / MS_IN_HR;
            uidEnergyUsed.audioEnergyPerUid = (PowerProfile.audio * uidEnergyStat.audioPerUid)/MS_IN_HR;
            EnergyUsed.totalAudioEnergy += uidEnergyUsed.audioEnergyPerUid;

            //----------------------------- TOTAL CPU USAGE --------------------------------------
            uidEnergyUsed.CPUEnergy.totalUserCPU = (FreqData.totalAverageCPUCurrent * uidEnergyStat.CPUData.totalUserCPUTime)/MS_IN_HR;
            uidEnergyUsed.CPUEnergy.totalKernelCPU = (FreqData.totalAverageCPUCurrent * uidEnergyStat.CPUData.totalKernelCPUTime)/MS_IN_HR;
            EnergyUsed.totalCPUEnergy += uidEnergyUsed.CPUEnergy.totalKernelCPU + uidEnergyUsed.CPUEnergy.totalUserCPU;

            uidEnergyUsed.totalEnergy = uidEnergyUsed.CPUEnergy.totalUserCPU + uidEnergyUsed.CPUEnergy.totalKernelCPU + uidEnergyUsed.audioEnergyPerUid + uidEnergyUsed.cameraEnergyPerUid + uidEnergyUsed.totalWifiEnergy;
            totalProcessesEnergy += uidEnergyUsed.totalEnergy;

            EnergyUsed.uidEnergyUsedList.add(uidEnergyUsed);
        }

        // sort the uidEnergyStatsList according to the highest total power consumption
        Collections.sort(EnergyUsed.uidEnergyUsedList);
        Collections.reverse(EnergyUsed.uidEnergyUsedList);

        // print the 8 processes with the highest impact on battery life
        System.out.println("\n ----------------------------- PROCESSES WITH ELEVATED BATTERY USAGE --------------------------------------");
        for (int i = 0; i < 8; i++){
            int finalI = i;
            AtomicReference<String> packageName = new AtomicReference<>("NA");
            EnergyInfo.uidToPackageList.stream().filter(o -> o.uid.equals(EnergyUsed.uidEnergyUsedList.get(finalI).uid)).forEach(
                    o -> {
                        packageName.set(o.packageName); // get the package name for the uid
                    }
            );

            System.out.println((float)EnergyUsed.uidEnergyUsedList.get(i).totalEnergy/totalProcessesEnergy*100 + "%: uid: " + EnergyUsed.uidEnergyUsedList.get(i).uid + " process name: (last if several per uid) " + packageName + " used " +
                    EnergyUsed.uidEnergyUsedList.get(i).totalEnergy + " mAh");
        }
        System.out.println("\n total Charge used by all processes = " + totalProcessesEnergy + " mAh");



        System.out.println("\n ----------------------------- COMPONENT TOTAL USAGES --------------------------------------");
        float totalSBC = EnergyUsed.bluetoothControllerTotalEnergy + EnergyUsed.totalWifiEnergy +
                EnergyUsed.totalAudioEnergy + EnergyUsed.totalCameraEnergy + EnergyUsed.totalCPUEnergy;

        System.out.println((float)EnergyUsed.bluetoothControllerTotalEnergy/totalSBC*100 + "%: bluetoothController Total " +
                EnergyUsed.bluetoothControllerTotalEnergy + " mAh");
        System.out.println((float)EnergyUsed.totalWifiEnergy/totalSBC*100 + "%: total Wifi " + EnergyUsed.totalWifiEnergy + " mAh");
        System.out.println((float)EnergyUsed.totalAudioEnergy/totalSBC*100 + "%: total Audio " + EnergyUsed.totalAudioEnergy + " mAh");
        System.out.println((float)EnergyUsed.totalCameraEnergy/totalSBC*100 + "%: total Camera " + EnergyUsed.totalCameraEnergy + " mAh");
        System.out.println((float)EnergyUsed.totalCPUEnergy/totalSBC*100 + "%: total CPU " + EnergyUsed.totalCPUEnergy + " mAh");

    }

    private static void calculateCPUPower(PowerProfile PowerProfile, FrequencyData FreqData){
        // calculate the total Energy used by the CPU taking into account the different frequencies
        System.out.println("\n ----------------------------- SILVER CLUSTER --------------------------------------");
        System.out.println("Note: some CPUs times spent in different frequencies might not add up to test duration since the frequency at start is unknown \n");
        float averagemACPU = 0f;
        float silverClustermAhTotal = 0f;
        float silverClusterTimeTotal = 0f;
        // we calculate the Energy per CPU
        for (int i = 0; i < FreqData.ClusterList.get(0).CPUList.size(); i++) {
            float mAhOfCPUTotal = 0f;
            int totalTime = 0;
            // we sum over all frequencies
            for (int j = 0; j < PowerProfile.mAUsedAtFrequenciesSilver.size(); j++) {
                mAhOfCPUTotal += (float) (PowerProfile.mAUsedAtFrequenciesSilver.get(j) * FreqData.ClusterList.get(0).CPUList.get(i).timeSpentAtFrequenciesSilver.get(j))/MS_IN_HR;
                if(j!= 0) {
                    // we don't want to consider the time spent in idle since this must not be considered for average while running
                    totalTime += FreqData.ClusterList.get(0).CPUList.get(i).timeSpentAtFrequenciesSilver.get(j);
                }
            }
//            EnergyUsed.mAhPerCPUSilver.set(i, mAhOfCPUTotal);
//            EnergyUsed.activeTimePerCPUSilver.set(i, totalTime);

            silverClustermAhTotal += mAhOfCPUTotal;
            silverClusterTimeTotal += totalTime;

            System.out.println("CPU" + FreqData.ClusterList.get(0).CPUList.get(i).CPUId + " used " + mAhOfCPUTotal+ " mAh");
            if (totalTime != 0){
                averagemACPU = (mAhOfCPUTotal*MS_IN_HR)/totalTime;
            }
            System.out.println("CPU" + FreqData.ClusterList.get(0).CPUList.get(i).CPUId + " used an average of (" + mAhOfCPUTotal + "*MS_IN_HR)" +"/" + totalTime + " = " + averagemACPU + " mA");
        }

        System.out.println("average silver cluster current " + (silverClustermAhTotal*MS_IN_HR)/silverClusterTimeTotal + " mA");


        System.out.println("\n ----------------------------- GOLD CLUSTER --------------------------------------");
        System.out.println("Note: some CPU times might not add up to test duration since the frequency at start is unknown\n");
        averagemACPU = 0f;
        float goldClustermAhTotal = 0f;
        float goldClusterTimeTotal = 0f;
        for (int i = 0; i < FreqData.ClusterList.get(1).CPUList.size(); i++) {
            float mAhOfCPUTotal = 0f;
            int totalTime = 0;
            // we sum over all frequencies
            for (int j = 0; j < PowerProfile.mAUsedAtFrequenciesGold.size(); j++) {
                mAhOfCPUTotal += (float) (PowerProfile.mAUsedAtFrequenciesGold.get(j) * FreqData.ClusterList.get(1).CPUList.get(i).timeSpentAtFrequenciesGold.get(j))/MS_IN_HR;
                if(j!= 0) {
                    // we don't want to consider the time spent in idle since this must not be considered for average while running
                    totalTime += FreqData.ClusterList.get(1).CPUList.get(i).timeSpentAtFrequenciesGold.get(j);
                }
            }

            goldClustermAhTotal += mAhOfCPUTotal;
            goldClusterTimeTotal += totalTime;

            System.out.println("CPU" + FreqData.ClusterList.get(1).CPUList.get(i).CPUId + " used " + mAhOfCPUTotal + " mAh");
            if (totalTime != 0){
                averagemACPU = (mAhOfCPUTotal*MS_IN_HR)/totalTime;
            }
            System.out.println("CPU" + FreqData.ClusterList.get(1).CPUList.get(i).CPUId + " used an average of " + mAhOfCPUTotal + "*MS_IN_HR)" + "/" + totalTime + " = " + averagemACPU + " mA");
        }
        System.out.println("average gold cluster current " + (goldClustermAhTotal*MS_IN_HR)/goldClusterTimeTotal + " mA");


        System.out.println("\n ----------------------------- TOTALS CLUSTER --------------------------------------");

        System.out.println("total silver " + silverClustermAhTotal + " mAh");
        System.out.println("total gold " + goldClustermAhTotal + " mAh");
        System.out.println("total both " + (silverClustermAhTotal+goldClustermAhTotal) + " mAh");
        FreqData.totalAverageCPUCurrent = ((silverClustermAhTotal+goldClustermAhTotal)*MS_IN_HR)/(goldClusterTimeTotal+silverClusterTimeTotal);
        System.out.println("average " + FreqData.totalAverageCPUCurrent + " mA");

        System.out.println(" \n \n ATTENTION SYSTRACE IS FOLLOWING LENGTH " + (FreqData.lastTimestamp-FreqData.firstTimeStamp) + " ms");
    }
}
