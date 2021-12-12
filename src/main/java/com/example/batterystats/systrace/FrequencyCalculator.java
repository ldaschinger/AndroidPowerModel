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
//        plt.hist().add(FreqData.ClusterList.get(0).CPUList.get(0).frequenciesList);
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
        }


                switch(CPU.frequenciesList.get(i)) {
                    case 0:

                        break;
                    case 300000:
                        newValue = CPU.timeSpentAtFrequenciesSilver.get(1) + (CPU.timeStampsList.get(i+1) - CPU.timeStampsList.get(i));
                        CPU.timeSpentAtFrequenciesSilver.set(1, newValue);
                        break;
                    case 576000:
                        newValue = CPU.timeSpentAtFrequenciesSilver.get(2) + (CPU.timeStampsList.get(i+1) - CPU.timeStampsList.get(i));
                        CPU.timeSpentAtFrequenciesSilver.set(2, newValue);
                        break;
                    case 748800:
                        newValue = CPU.timeSpentAtFrequenciesSilver.get(3) + (CPU.timeStampsList.get(i+1) - CPU.timeStampsList.get(i));
                        CPU.timeSpentAtFrequenciesSilver.set(3, newValue);
                        break;
                    case 998400:
                        newValue = CPU.timeSpentAtFrequenciesSilver.get(4) + (CPU.timeStampsList.get(i+1) - CPU.timeStampsList.get(i));
                        CPU.timeSpentAtFrequenciesSilver.set(4, newValue);
                        break;
                    case 1209600:
                        newValue = CPU.timeSpentAtFrequenciesSilver.get(5) + (CPU.timeStampsList.get(i+1) - CPU.timeStampsList.get(i));
                        CPU.timeSpentAtFrequenciesSilver.set(5, newValue);
                        break;
                    case 1324800:
                        newValue = CPU.timeSpentAtFrequenciesSilver.get(6) + (CPU.timeStampsList.get(i+1) - CPU.timeStampsList.get(i));
                        CPU.timeSpentAtFrequenciesSilver.set(6, newValue);
                        break;
                    case 1516800:
                        newValue = CPU.timeSpentAtFrequenciesSilver.get(7) + (CPU.timeStampsList.get(i+1) - CPU.timeStampsList.get(i));
                        CPU.timeSpentAtFrequenciesSilver.set(7, newValue);
                        break;
                    case 1612800:
                        newValue = CPU.timeSpentAtFrequenciesSilver.get(8) + (CPU.timeStampsList.get(i+1) - CPU.timeStampsList.get(i));
                        CPU.timeSpentAtFrequenciesSilver.set(8, newValue);
                        break;
                    case 1708800:
                        newValue = CPU.timeSpentAtFrequenciesSilver.get(9) + (CPU.timeStampsList.get(i+1) - CPU.timeStampsList.get(i));
                        CPU.timeSpentAtFrequenciesSilver.set(9, newValue);
                        break;
                    default:
                        System.out.println("error: frequency is not found in our switch");
                }
            }
            System.out.println("CPU:" + CPU.CPUId + " spent [ms] in:");
            System.out.println("idle   300000    576000    748800     998400    1209600    1324800    1516800    1612800     1708800");

            System.out.println(CPU.timeSpentAtFrequenciesSilver.get(0) + "       " + CPU.timeSpentAtFrequenciesSilver.get(1) + "      " + CPU.timeSpentAtFrequenciesSilver.get(2) + "     " +
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
                int newValue = 0;
                // we use the formula: timeAtFrequency += timestamp(i+1) - timestamp (i)
                // Gold core frequencies: 300000 652800 825600 979200 1132800 1363200 1536000 1747200 1843200 1996800 2054400 2169600 2208000 2361600 2400000 2457600 2515200
                switch(CPU.frequenciesList.get(i)) {
                    case 0:
                        newValue = CPU.timeSpentAtFrequenciesGold.get(0) + (CPU.timeStampsList.get(i+1) - CPU.timeStampsList.get(i));
                        CPU.timeSpentAtFrequenciesGold.set(0, newValue);
                        break;
                    case 300000:
                        newValue = CPU.timeSpentAtFrequenciesGold.get(1) + (CPU.timeStampsList.get(i+1) - CPU.timeStampsList.get(i));
                        CPU.timeSpentAtFrequenciesGold.set(1, newValue);
                        break;
                    case 652800:
                        newValue = CPU.timeSpentAtFrequenciesGold.get(2) + (CPU.timeStampsList.get(i+1) - CPU.timeStampsList.get(i));
                        CPU.timeSpentAtFrequenciesGold.set(2, newValue);
                        break;
                    case 825600:
                        newValue = CPU.timeSpentAtFrequenciesGold.get(3) + (CPU.timeStampsList.get(i+1) - CPU.timeStampsList.get(i));
                        CPU.timeSpentAtFrequenciesGold.set(3, newValue);
                        break;
                    case 979200:
                        newValue = CPU.timeSpentAtFrequenciesGold.get(4) + (CPU.timeStampsList.get(i+1) - CPU.timeStampsList.get(i));
                        CPU.timeSpentAtFrequenciesGold.set(4, newValue);
                        break;
                    case 1132800:
                        newValue = CPU.timeSpentAtFrequenciesGold.get(5) + (CPU.timeStampsList.get(i+1) - CPU.timeStampsList.get(i));
                        CPU.timeSpentAtFrequenciesGold.set(5, newValue);
                        break;
                    case 1363200:
                        newValue = CPU.timeSpentAtFrequenciesGold.get(6) + (CPU.timeStampsList.get(i+1) - CPU.timeStampsList.get(i));
                        CPU.timeSpentAtFrequenciesGold.set(6, newValue);
                        break;
                    case 1536000:
                        newValue = CPU.timeSpentAtFrequenciesGold.get(7) + (CPU.timeStampsList.get(i+1) - CPU.timeStampsList.get(i));
                        CPU.timeSpentAtFrequenciesGold.set(7, newValue);
                        break;
                    case 1747200:
                        newValue = CPU.timeSpentAtFrequenciesGold.get(8) + (CPU.timeStampsList.get(i+1) - CPU.timeStampsList.get(i));
                        CPU.timeSpentAtFrequenciesGold.set(8, newValue);
                        break;
                    case 1843200:
                        newValue = CPU.timeSpentAtFrequenciesGold.get(9) + (CPU.timeStampsList.get(i+1) - CPU.timeStampsList.get(i));
                        CPU.timeSpentAtFrequenciesGold.set(9, newValue);
                        break;
                    case 1996800:
                        newValue = CPU.timeSpentAtFrequenciesGold.get(10) + (CPU.timeStampsList.get(i+1) - CPU.timeStampsList.get(i));
                        CPU.timeSpentAtFrequenciesGold.set(10, newValue);
                        break;
                    case 2054400:
                        newValue = CPU.timeSpentAtFrequenciesGold.get(11) + (CPU.timeStampsList.get(i+1) - CPU.timeStampsList.get(i));
                        CPU.timeSpentAtFrequenciesGold.set(11, newValue);
                        break;
                    case 2169600:
                        newValue = CPU.timeSpentAtFrequenciesGold.get(12) + (CPU.timeStampsList.get(i+1) - CPU.timeStampsList.get(i));
                        CPU.timeSpentAtFrequenciesGold.set(12, newValue);
                        break;
                    case 2208000:
                        newValue = CPU.timeSpentAtFrequenciesGold.get(13) + (CPU.timeStampsList.get(i+1) - CPU.timeStampsList.get(i));
                        CPU.timeSpentAtFrequenciesGold.set(13, newValue);
                        break;
                    case 2361600:
                        newValue = CPU.timeSpentAtFrequenciesGold.get(14) + (CPU.timeStampsList.get(i+1) - CPU.timeStampsList.get(i));
                        CPU.timeSpentAtFrequenciesGold.set(14, newValue);
                        break;
                    case 2400000:
                        newValue = CPU.timeSpentAtFrequenciesGold.get(15) + (CPU.timeStampsList.get(i+1) - CPU.timeStampsList.get(i));
                        CPU.timeSpentAtFrequenciesGold.set(15, newValue);
                        break;
                    case 2457600:
                        newValue = CPU.timeSpentAtFrequenciesGold.get(16) + (CPU.timeStampsList.get(i+1) - CPU.timeStampsList.get(i));
                        CPU.timeSpentAtFrequenciesGold.set(16, newValue);
                        break;
                    case 2515200:
                        newValue = CPU.timeSpentAtFrequenciesGold.get(17) + (CPU.timeStampsList.get(i+1) - CPU.timeStampsList.get(i));
                        CPU.timeSpentAtFrequenciesGold.set(17, newValue);
                        break;
                    default:
                        System.out.println("error: frequency is not found in our switch");
                }
            }
            System.out.println("CPU:" + CPU.CPUId + " spent [ms] in:");
            System.out.println("idle   300000 652800 825600 979200 1132800 1363200 1536000 1747200 1843200 1996800 2054400 2169600 2208000 2361600 2400000 2457600 2515200");

            System.out.println(CPU.timeSpentAtFrequenciesGold.get(0) + "       " + CPU.timeSpentAtFrequenciesGold.get(1) + "      " + CPU.timeSpentAtFrequenciesGold.get(2) + "     " +
                    CPU.timeSpentAtFrequenciesGold.get(3) + "         " + CPU.timeSpentAtFrequenciesGold.get(4) + "       " + CPU.timeSpentAtFrequenciesGold.get(5) + "        " +
                    CPU.timeSpentAtFrequenciesGold.get(6) + "        " + CPU.timeSpentAtFrequenciesGold.get(7) + "        " + CPU.timeSpentAtFrequenciesGold.get(8) + "          " +
                    CPU.timeSpentAtFrequenciesGold.get(9) + "        " + CPU.timeSpentAtFrequenciesGold.get(10) + "        " + CPU.timeSpentAtFrequenciesGold.get(11) + "        " +
                    CPU.timeSpentAtFrequenciesGold.get(12) + "        " + CPU.timeSpentAtFrequenciesGold.get(13) + "        " + CPU.timeSpentAtFrequenciesGold.get(14) + "        " +
                    CPU.timeSpentAtFrequenciesGold.get(15) + "        " +  CPU.timeSpentAtFrequenciesGold.get(16) + "        " + CPU.timeSpentAtFrequenciesGold.get(17) +"\n");
        }

    }
}
