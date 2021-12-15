package com.example.batterystats.systrace;

import com.example.batterystats.core.batterystats.EnergyInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// this class contains a list of CPU clusters
public class FrequencyData {
    public FrequencyData() {
        this.ClusterList = new ArrayList<>();
    }
    public List<FrequencyData.CPUCluster> ClusterList;

    // we want to save the last timestamp in the trace to calculate the last period of every frequency
    public int lastTimestamp;
    public int firstTimeStamp;
    public float totalAverageCPUCurrent;


    // this class contains a list of CPUs
    static public class CPUCluster{
        public CPUCluster() {
            this.CPUList = new ArrayList<>();
        }

        public List<FrequencyData.CPUCluster.CPU> CPUList;

        public float clusterAverageCurrent = 0;

        // this class holds the frequencies and respective timestamps of a CPU
        static public class CPU{
            public CPU(String CPUId) {
                this.frequenciesList = new ArrayList<>();
                this.timeStampsList = new ArrayList<>();
//                this.timeSpentAtFrequenciesSilver = Arrays.asList(new Integer[10]);
                this.timeSpentAtFrequenciesSilver = new ArrayList<>(Collections.nCopies(10, 0)); // 9 frequencies plus idle
//                this.timeSpentAtFrequenciesGold = Arrays.asList(new Integer[18]);
                this.timeSpentAtFrequenciesGold = new ArrayList<>(Collections.nCopies(18, 0)); // 17 frequencies plus idle
                this.CPUId = CPUId;
            }

            public String CPUId; // use string to be able to use methods on it (want a boxed type)
            List<Integer> timeStampsList;
            List<Integer> frequenciesList;

            // CPU freqs silver cores/cluster 0: 300000 576000 748800 998400 1209600 1324800 1516800 1612800 1708800
            public List<Integer> timeSpentAtFrequenciesSilver;
            // CPU freqs gold cores/cluster 1: 300000 652800 825600 979200 1132800 1363200 1536000 1747200 1843200 1996800 2054400 2169600 2208000 2361600 2400000 2457600 2515200
            public List<Integer> timeSpentAtFrequenciesGold;
        }
    }

    // CPU class for

}
