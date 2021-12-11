package com.example.batterystats.systrace;

import com.example.batterystats.core.batterystats.EnergyInfo;

import java.util.ArrayList;
import java.util.List;

// this class contains a list of CPU clusters
public class FrequencyData {
    public FrequencyData() {
        this.ClusterList = new ArrayList<>();
    }
    List<FrequencyData.CPUCluster> ClusterList;

    // this class contains a list of CPUs
    static class CPUCluster{
        public CPUCluster() {
            this.CPUList = new ArrayList<>();
        }

        List<FrequencyData.CPUCluster.CPU> CPUList;

        // this class holds the frequencies and respective timestamps of a CPU
        static class CPU{
            public CPU(String CPUId) {
                this.frequenciesList = new ArrayList<>();
                this.timeStampsList = new ArrayList<>();
                this.CPUId = CPUId;
            }

            String CPUId; // use string to be able to use methods on it (want a boxed type)
            List<Integer> timeStampsList;
            List<Integer> frequenciesList;
        }
    }
}
