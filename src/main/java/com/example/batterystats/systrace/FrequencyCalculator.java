package com.example.batterystats.systrace;

import com.example.batterystats.core.batterystats.PowerProfile;
import com.github.sh0nk.matplotlib4j.NumpyUtils;
import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.PythonConfig;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FrequencyCalculator {
    public static void calculateFrequencyUsage(FrequencyData FreqData) {

//        // visual representation in a histogram of the used CPU frequencies
//        Plot plt = Plot.create(PythonConfig.pythonBinPathConfig("/usr/bin/python")); //must use python2
////        plt.plot().add(FreqData.ClusterList.get(0).CPUList.get(0).timeStampsList,
////                FreqData.ClusterList.get(0).CPUList.get(0).frequenciesList, "o").label("frequencies");
//        // adapt the data source to see other CPUs
//        plt.hist().add(FreqData.ClusterList.get(1).CPUList.get(0).frequenciesList);
//        plt.legend().loc("upper right");
//        plt.title("CPU usage");
//        try {
//            plt.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (PythonExecutionException e) {
//            e.printStackTrace();
//        }

        // we need to know the available frequencies
        PowerProfile PowerProfile = new PowerProfile();



        System.out.println("\n----------------------------- SILVER CLUSTER --------------------------------------");
        // calculate the time each CPU in the Silver cluster has spent in a frequency
        for (FrequencyData.CPUCluster.CPU CPU : FreqData.ClusterList.get(0).CPUList) {
            // go through the frequencies used during the test run
            // since the timeStampsList is one longer than the frequenciesList timestamp(i+1) is accessible in the last iteration
            for (int i = 0; i < CPU.frequenciesList.size(); i++) {
                int newValue;
                // Silver core frequencies: 300000 576000 748800 998400 1209600 1324800 1516800 1612800 1708800
                for (int j = 0; j < PowerProfile.frequenciesSilver.size(); j++) {
                    // check for every frequency if our frequency is equal to it
                    if (Objects.equals(PowerProfile.frequenciesSilver.get(j), CPU.frequenciesList.get(i))) {
                        // we use the formula: timeAtFrequency += timestamp(i+1) - timestamp (i)
                        newValue = CPU.timeSpentAtFrequenciesSilver.get(j) + (CPU.timeStampsList.get(i + 1) - CPU.timeStampsList.get(i));
                        CPU.timeSpentAtFrequenciesSilver.set(j, newValue);
                    }
                }
            }
            System.out.println("CPU:" + CPU.CPUId + " spent [ms] in:");
            System.out.println("freq: idle   300000    576000    748800     998400    1209600    1324800    1516800    1612800     1708800");

            System.out.println("time: " + CPU.timeSpentAtFrequenciesSilver.get(0) + "       " + CPU.timeSpentAtFrequenciesSilver.get(1) + "      " + CPU.timeSpentAtFrequenciesSilver.get(2) + "     " +
                    CPU.timeSpentAtFrequenciesSilver.get(3) + "         " + CPU.timeSpentAtFrequenciesSilver.get(4) + "       " + CPU.timeSpentAtFrequenciesSilver.get(5) + "        " +
                    CPU.timeSpentAtFrequenciesSilver.get(6) + "        " + CPU.timeSpentAtFrequenciesSilver.get(7) + "        " + CPU.timeSpentAtFrequenciesSilver.get(8) + "          " +
                    CPU.timeSpentAtFrequenciesSilver.get(9) + "\n");
        }






        System.out.println("----------------------------- GOLD CLUSTER --------------------------------------");
        // calculate the time each CPU in the Gold cluster has spent in a frequency
        for (FrequencyData.CPUCluster.CPU CPU : FreqData.ClusterList.get(1).CPUList) {
            // go through the frequencies used during the test run
            // since the timeStampsList is one longer than the frequenciesList timestamp(i+1) is accessible in the last iteration
            for (int i = 0; i < CPU.frequenciesList.size(); i++) {
                int newValue;
                // Gold core frequencies: 300000 652800 825600 979200 1132800 1363200 1536000 1747200 1843200 1996800 2054400 2169600 2208000 2361600 2400000 2457600 2515200
                for (int j = 0; j < PowerProfile.frequenciesGold.size(); j++) {
                    // check for every frequency if our frequency is equal to it
                    if (Objects.equals(PowerProfile.frequenciesGold.get(j), CPU.frequenciesList.get(i))) {
                        // we use the formula: timeAtFrequency += timestamp(i+1) - timestamp (i)
                        newValue = CPU.timeSpentAtFrequenciesGold.get(j) + (CPU.timeStampsList.get(i + 1) - CPU.timeStampsList.get(i));
                        CPU.timeSpentAtFrequenciesGold.set(j, newValue);
                    }
                }
            }

            System.out.println("CPU:" + CPU.CPUId + " spent [ms] in frequency [Hz]");
            System.out.println("freq: idle   300000 652800 825600 979200 1132800 1363200 1536000 1747200 1843200 1996800 2054400 2169600 2208000 2361600 2400000 2457600 2515200");

            System.out.println("time: " + CPU.timeSpentAtFrequenciesGold.get(0) + "       " + CPU.timeSpentAtFrequenciesGold.get(1) + "      " + CPU.timeSpentAtFrequenciesGold.get(2) + "     " +
                    CPU.timeSpentAtFrequenciesGold.get(3) + "         " + CPU.timeSpentAtFrequenciesGold.get(4) + "       " + CPU.timeSpentAtFrequenciesGold.get(5) + "        " +
                    CPU.timeSpentAtFrequenciesGold.get(6) + "        " + CPU.timeSpentAtFrequenciesGold.get(7) + "        " + CPU.timeSpentAtFrequenciesGold.get(8) + "          " +
                    CPU.timeSpentAtFrequenciesGold.get(9) + "        " + CPU.timeSpentAtFrequenciesGold.get(10) + "        " + CPU.timeSpentAtFrequenciesGold.get(11) + "        " +
                    CPU.timeSpentAtFrequenciesGold.get(12) + "        " + CPU.timeSpentAtFrequenciesGold.get(13) + "        " + CPU.timeSpentAtFrequenciesGold.get(14) + "        " +
                    CPU.timeSpentAtFrequenciesGold.get(15) + "        " +  CPU.timeSpentAtFrequenciesGold.get(16) + "        " + CPU.timeSpentAtFrequenciesGold.get(17) +"\n");
        }

    }
}
