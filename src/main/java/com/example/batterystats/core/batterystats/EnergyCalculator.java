package com.example.batterystats.core.batterystats;

import com.example.batterystats.systrace.FrequencyData;
import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.PythonConfig;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

// this class calculates the actual energy used by components and uids
public class EnergyCalculator {
    static int MS_IN_HR = 1000*60*60;
    static float silverClusterTimeTotal = 0f;
    static float goldClusterTimeTotal = 0f;
    static int systrace_duration = 0;

    public static void calculatePowerUsage(EnergyInfo EnergyInfo, FrequencyData FreqData){
        PowerProfile PowerProfile = new PowerProfile();
        EnergyUsed EnergyUsed = new EnergyUsed();


        // use the frequency data of the CPUs to get a good estimate of power used by the CPUs
        calculateCPUPower(PowerProfile, FreqData, EnergyUsed);

        //----------------------------- BLUETOOTH --------------------------------------
        //Android uses: ((idleTimeMs * mIdleMa) + (rxTimeMs * mRxMa) + (txTimeMs * mTxMa))/(1000*60*60)
        EnergyUsed.bluetoothControllerIdleEnergy = (PowerProfile.bluetoothControllerIdle * EnergyInfo.bluetoothData.idle)/MS_IN_HR;
        EnergyUsed.bluetoothControllerTxEnergy = (PowerProfile.bluetoothControllerTx * EnergyInfo.bluetoothData.tx)/MS_IN_HR;
        EnergyUsed.bluetoothControllerRxEnergy = (PowerProfile.bluetoothControllerRx * EnergyInfo.bluetoothData.rx)/MS_IN_HR;
        EnergyUsed.bluetoothControllerTotalEnergy = EnergyUsed.bluetoothControllerIdleEnergy +
                EnergyUsed.bluetoothControllerTxEnergy + EnergyUsed.bluetoothControllerRxEnergy;

        //----------------------------- TOTAL REPORTED CPU TIME BY BATTERYSTATS --------------------------------------
        int totalCPUTimeBatterystats = 0;
        for (EnergyInfo.uidEnergyStats uidEnergyStat: EnergyInfo.uidEnergyStatsList){
            totalCPUTimeBatterystats += uidEnergyStat.CPUData.totalUserCPUTime + uidEnergyStat.CPUData.totalKernelCPUTime;
        }
//        System.out.println("totalCPUTimeBatterystats = " + totalCPUTimeBatterystats + "ms");

        //----------------------------- PER UID USAGE --------------------------------------
        float totalProcessesEnergy = 0;
        // create the list of per uid Energy usage data
        for (EnergyInfo.uidEnergyStats uidEnergyStat: EnergyInfo.uidEnergyStatsList){
            // create a new uidEnergyUsed object, then populate it and then add to the uidEnergyUsedList
            EnergyUsed.uidEnergyUsed uidEnergyUsed = new EnergyUsed.uidEnergyUsed();
            uidEnergyUsed.uid = uidEnergyStat.uid;

            //----------------------------- WIFI --------------------------------------
            // Android uses ((idleTimeMs * mIdleCurrentMa) + (txTimeMs * mTxCurrentMa) +
            //                (rxTimeMs * mRxCurrentMa) + (wifiScanTimeMs * mWifiPowerScan) )/ (1000*60*60)
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

            //----------------------------- SCREEN --------------------------------------
            EnergyUsed.screenEnergy = (PowerProfile.screenAvg * EnergyInfo.screen)/MS_IN_HR;

            //----------------------------- AUDIO --------------------------------------
            // Android uses (totalTime * mAudioAveragePowerMa) / MS_IN_HR;
            // depending on the use case must select audio from download/stream or audio from memory
//            uidEnergyUsed.audioEnergyPerUid = (PowerProfile.audioFromMemory * uidEnergyStat.audioPerUid)/MS_IN_HR;
            uidEnergyUsed.audioEnergyPerUid = (PowerProfile.audioFromMediaStream * uidEnergyStat.audioPerUid)/MS_IN_HR;
            EnergyUsed.totalAudioEnergy += uidEnergyUsed.audioEnergyPerUid;

            //----------------------------- TOTAL CPU USAGE --------------------------------------
            uidEnergyUsed.CPUEnergy.totalUserCPU = (float)uidEnergyStat.CPUData.totalUserCPUTime/totalCPUTimeBatterystats
                    *EnergyUsed.totalmAhAllClusterAdapted;
            uidEnergyUsed.CPUEnergy.totalKernelCPU = (float)uidEnergyStat.CPUData.totalKernelCPUTime/totalCPUTimeBatterystats
                    *EnergyUsed.totalmAhAllClusterAdapted;
            EnergyUsed.totalCPUEnergy += uidEnergyUsed.CPUEnergy.totalKernelCPU + uidEnergyUsed.CPUEnergy.totalUserCPU;

            uidEnergyUsed.totalEnergy = uidEnergyUsed.CPUEnergy.totalUserCPU + uidEnergyUsed.CPUEnergy.totalKernelCPU +
                    uidEnergyUsed.audioEnergyPerUid + uidEnergyUsed.cameraEnergyPerUid + uidEnergyUsed.totalWifiEnergy;
            totalProcessesEnergy += uidEnergyUsed.totalEnergy;

            EnergyUsed.uidEnergyUsedList.add(uidEnergyUsed);
        }

        // sort the uidEnergyStatsList according to the highest total power consumption
        Collections.sort(EnergyUsed.uidEnergyUsedList);
        Collections.reverse(EnergyUsed.uidEnergyUsedList);

        // print the 8 processes with the highest impact on battery life
        System.out.println("\n ----------------------------- PROCESSES WITH ELEVATED BATTERY USAGE --------------------------------------");
        System.out.println("Note: process name is equal to last process of a uid if several are present for one uid");

        System.out.println("\nBATTERYSTATS DURATION: " + EnergyInfo.testDuration + " ms\n");

        // if we want to reduce consumption and would for example fully remove the process using 50% CPU time
        // we will not reduce the CPU power by 50% since we only reduce the active part -> the gold/silverClusterTimeTotal
        // therefore for an improved estimation we would have to calculate power used by idle (no process responsible) and power in active and assign to processes percentage
//        System.out.println("\ntotal GOLD (" + goldClusterTimeTotal + " ms) + SILVER (" + silverClusterTimeTotal + " ms) cluster active CPU time (not idle) " + (goldClusterTimeTotal + silverClusterTimeTotal) +  " ms"+ "\n");

        System.out.println("The following ratio indicates the percentage of active CPU time over idle CPU time (max is 8): ");
        float fractionDirect = (goldClusterTimeTotal + silverClusterTimeTotal)/systrace_duration;
        float fractionPercentage = (fractionDirect/8*100);
        System.out.println("CPUActiveDuration/totalTestDuration = " + fractionDirect + " (" + fractionPercentage + "% of the max = 8)");
//        System.out.println("A higher ratio will mean reductions in CPU time will lead to higher reduction in CPU power \n");


        for (int i = 0; i < 10; i++){
            int finalI = i;
            AtomicReference<String> packageName = new AtomicReference<>("NA");
            EnergyInfo.uidToPackageList.stream().filter(o -> o.uid.equals(EnergyUsed.uidEnergyUsedList.get(finalI).uid)).forEach(
                    o -> {
                        if (!o.uid.equals("0") && !o.uid.equals("1000"))
                        packageName.set(o.packageName); // get the package name for the uid
                    }
            );

            System.out.println((float)EnergyUsed.uidEnergyUsedList.get(i).totalEnergy/totalProcessesEnergy*100 + "%: uid: " +
                    EnergyUsed.uidEnergyUsedList.get(i).uid + " process name:  " + packageName + " used " +
                    EnergyUsed.uidEnergyUsedList.get(i).totalEnergy + " mAh");

            //CPU usage per hour
            float time = (EnergyUsed.uidEnergyUsedList.get(i).CPUEnergy.totalKernelCPU+EnergyUsed.uidEnergyUsedList.get(i).CPUEnergy.totalUserCPU)/EnergyUsed.totalmAhAllClusterAdapted*totalCPUTimeBatterystats;
            System.out.println("CPU time/h = " + (time/systrace_duration*60) + " min");
//            (float)uidEnergyStat.CPUData.totalKernelCPUTime/totalCPUTimeBatterystats*EnergyUsed.totalmAhAllClusterAdapted;

            if(i<3){
                System.out.println("      capacity used by CPU by this uid = " + (EnergyUsed.uidEnergyUsedList.get(i).CPUEnergy.totalKernelCPU+
                        EnergyUsed.uidEnergyUsedList.get(i).CPUEnergy.totalUserCPU) + " mAh");
                if(EnergyUsed.uidEnergyUsedList.get(i).audioEnergyPerUid != 0){
                    System.out.println("      capacity used by audio by this uid = " + EnergyUsed.uidEnergyUsedList.get(i).audioEnergyPerUid + " mAh"); }
                if(EnergyUsed.uidEnergyUsedList.get(i).cameraEnergyPerUid != 0){
                    System.out.println("      capacity used by camera by this uid = " + EnergyUsed.uidEnergyUsedList.get(i).cameraEnergyPerUid + " mAh"); }
                if(EnergyUsed.uidEnergyUsedList.get(i).totalWifiEnergy != 0){
                    System.out.println("      capacity used by wifi by this uid = " + EnergyUsed.uidEnergyUsedList.get(i).totalWifiEnergy + " mAh"); }

            }
        }
//        System.out.println("\n total Charge used by all processes = " + totalProcessesEnergy + " mAh");



        System.out.println("\n ----------------------------- COMPONENT TOTAL USAGES --------------------------------------");
        System.out.println("NOTE: total used battery capacity reported for Camera, Bluetooth, WiFi and Audio " +
                "will include the additional consumption by the CPU for these processes");

        float baselineTotal = EnergyInfo.testDuration*PowerProfile.baselineCurrentDozemA/MS_IN_HR;

        //batterystats length EnergyInfo.testDuration
        //systrace length is systrace_duration
        // we need to adapt the CPU estimation if the measurement duration of systrace and batterystats are not the same
        float durationFactor = (float)EnergyInfo.testDuration/(float)systrace_duration;
        float additionalXR1Processing = (durationFactor*PowerProfile.XR1Factor*EnergyUsed.totalmAhAllClusterAdapted-baselineTotal);

        EnergyUsed.totalWifiEnergy += EnergyInfo.wifiIdle*PowerProfile.wifiControllerIdle/MS_IN_HR;

        float totalSBC = EnergyUsed.bluetoothControllerTotalEnergy + EnergyUsed.totalWifiEnergy +
                EnergyUsed.totalAudioEnergy + EnergyUsed.totalCameraEnergy + baselineTotal + EnergyUsed.screenEnergy + additionalXR1Processing;

        System.out.println((float)baselineTotal/totalSBC*100 + "%: Baseline power for system " + baselineTotal + " mAh");
        System.out.println("[" + (float)additionalXR1Processing/totalSBC*100 + "%: Processing above baseline " + additionalXR1Processing + " mAh] - components also include additional processing");
        System.out.println((float)EnergyUsed.bluetoothControllerTotalEnergy/totalSBC*100 + "%: total Bluetooth " +
                EnergyUsed.bluetoothControllerTotalEnergy + " mAh");
        System.out.println((float)EnergyUsed.totalWifiEnergy/totalSBC*100 + "%: total Wifi " + EnergyUsed.totalWifiEnergy + " mAh");
        System.out.println((float)EnergyUsed.totalAudioEnergy/totalSBC*100 + "%: total Audio " + EnergyUsed.totalAudioEnergy + " mAh");
        System.out.println((float)EnergyUsed.totalCameraEnergy/totalSBC*100 + "%: total Camera " + EnergyUsed.totalCameraEnergy + " mAh");
        System.out.println((float)EnergyUsed.screenEnergy/totalSBC*100 + "%: total Screen " + EnergyUsed.screenEnergy + " mAh");

        System.out.println("\nEstimated total = " + totalSBC + " mAh");

//        System.out.println("\n" + (float)EnergyUsed.totalCPUEnergy/totalSBC*100 +
//        "%: total CPU (already included in above measurements)" + adaptCPUPower(EnergyUsed.totalCPUEnergy) + " mAh");

        System.out.println("\n ----------------------------- README --------------------------------------");
        System.out.println(" ---> Remember to set correct screen current in PowerProfile.java");
        System.out.println(" ---> For higher accuracy use a systrace and batterystats trace of the same length");
        System.out.println(" ---> Remember to set correct systrace type in FrequencyParse.java (on device or from PC)");



    }

    private static void calculateCPUPower(PowerProfile PowerProfile, FrequencyData FreqData, EnergyUsed EnergyUsed){
        // calculate the total Energy used by the CPU taking into account the different frequencies
        System.out.println("\n ----------------------------- SILVER CLUSTER --------------------------------------");
        System.out.println("Note: some CPUs times spent in different frequencies might not add up " +
                "to test duration since the frequency at start is unknown \n");
        float averagemACPU = 0f;
        float silverClustermAhTotal = 0f;

        // we calculate the Energy per CPU
        for (int i = 0; i < FreqData.ClusterList.get(0).CPUList.size(); i++) {
            float mAhOfCPUTotal = 0f;
            int totalTime = 0;
            // we sum over all frequencies
            for (int j = 0; j < PowerProfile.mAUsedAtFrequenciesSilver.size(); j++) {
                mAhOfCPUTotal += (float) (PowerProfile.mAUsedAtFrequenciesSilver.get(j) *
                        FreqData.ClusterList.get(0).CPUList.get(i).timeSpentAtFrequenciesSilver.get(j))/MS_IN_HR;
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
            } else {
                averagemACPU = 0;
            }
            System.out.println("CPU" + FreqData.ClusterList.get(0).CPUList.get(i).CPUId + " on average uses (" +
                    mAhOfCPUTotal + "*MS_IN_HR)" +"/" + totalTime + " = " + averagemACPU + " mA when active");
        }

        System.out.println("average silver cluster current when active = " + (silverClustermAhTotal*MS_IN_HR)/silverClusterTimeTotal + " mA");
//        System.out.println("total silver cluster active CPU time (not idle) " + silverClusterTimeTotal+ "ms");


        System.out.println("\n ----------------------------- GOLD CLUSTER --------------------------------------");
        System.out.println("Note: some CPU times might not add up to test duration since the frequency at start is unknown\n");
        averagemACPU = 0f;
        float goldClustermAhTotal = 0f;

        for (int i = 0; i < FreqData.ClusterList.get(1).CPUList.size(); i++) {
            float mAhOfCPUTotal = 0f;
            int totalTime = 0;
            // we sum over all frequencies
            for (int j = 0; j < PowerProfile.mAUsedAtFrequenciesGold.size(); j++) {
                mAhOfCPUTotal += (float) (PowerProfile.mAUsedAtFrequenciesGold.get(j) *
                        FreqData.ClusterList.get(1).CPUList.get(i).timeSpentAtFrequenciesGold.get(j))/MS_IN_HR;
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
            System.out.println("CPU" + FreqData.ClusterList.get(1).CPUList.get(i).CPUId + " on average uses " +
                    mAhOfCPUTotal + "*MS_IN_HR)" + "/" + totalTime + " = " + averagemACPU + " mA when active");
        }
        System.out.println("average gold cluster current when active = " + (goldClustermAhTotal*MS_IN_HR)/goldClusterTimeTotal + " mA");
//        System.out.println("total gold cluster active CPU time (not idle) " + goldClusterTimeTotal + "ms");


        System.out.println("\n ----------------------------- TOTALS CLUSTER --------------------------------------");
        systrace_duration = (FreqData.lastTimestamp-FreqData.firstTimeStamp);
        System.out.println("SYSTRACE DURATION: " + systrace_duration+ " ms");

        System.out.println("\ntotal silver " + silverClustermAhTotal + " mAh");
        System.out.println("total gold " + goldClustermAhTotal + " mAh");
        EnergyUsed.totalmAhAllCluster = silverClustermAhTotal+goldClustermAhTotal;
        System.out.println("total both clusters " + EnergyUsed.totalmAhAllCluster + " mAh");

        FreqData.totalAverageCPUCurrent = ((silverClustermAhTotal+goldClustermAhTotal)*MS_IN_HR)/
                (goldClusterTimeTotal+silverClusterTimeTotal);
        System.out.println("average current both clusters " + FreqData.totalAverageCPUCurrent + " mA");

        EnergyUsed.totalmAhAllClusterAdapted = adaptCPUPower(EnergyUsed.totalmAhAllCluster, goldClusterTimeTotal +
                silverClusterTimeTotal);
        System.out.println("\nAfter applying non-linearity total both " + EnergyUsed.totalmAhAllClusterAdapted + " mAh");
    }

    private static float adaptCPUPower(float originalmAh, float totalTime){
        float power = 0.1655f;
        float factor = 3.7227f;
//        float factor = 2.834f;
        float offset = 0;

        // calculate total CPU time of measurement, then subtract total CPU time when CPU is active
        float idlePower = (systrace_duration*PowerProfile.numberCores - totalTime)*PowerProfile.idleCPU;

        return (float) Math.pow(originalmAh, power)*factor + offset + idlePower;
    }
}
