package com.example.batterystats.systrace;

import com.example.batterystats.core.batterystats.EnergyInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FrequencyParse {
    // function to parse a trace including CPU frequency information
    public static FrequencyData parseSystrace(String fileName) throws IOException {
        File file = new File(fileName);

        // we use the cluster data from power_profile.xml of the XR1 to create two clusters in the cluster list
        //      <value>6</value> <!-- cluster 0 has cpu0, cpu1, cpu2, cpu3, cpu4, cpu5 -->
        //      <value>2</value> <!-- cluster 1 has cpu6, cpu7 -->
        // additional information: https://gadgetversus.com/processor/qualcomm-snapdragon-xr1-specs/
        FrequencyData FreqData = new FrequencyData();
        // Cluster with CPUs with Kryo Silver cores (Energy cores)
        FrequencyData.CPUCluster ClusterSilver = new FrequencyData.CPUCluster();
        // Cluster with CPUs with Kryo Gold cores (High performance cores)
        FrequencyData.CPUCluster ClusterGold = new FrequencyData.CPUCluster();
        FreqData.ClusterList.add(ClusterSilver);
        FreqData.ClusterList.add(ClusterGold);

        FrequencyData.CPUCluster.CPU CPU0 = new FrequencyData.CPUCluster.CPU("0");
        FrequencyData.CPUCluster.CPU CPU1 = new FrequencyData.CPUCluster.CPU("1");
        FrequencyData.CPUCluster.CPU CPU2 = new FrequencyData.CPUCluster.CPU("2");
        FrequencyData.CPUCluster.CPU CPU3 = new FrequencyData.CPUCluster.CPU("3");
        FrequencyData.CPUCluster.CPU CPU4 = new FrequencyData.CPUCluster.CPU("4");
        FrequencyData.CPUCluster.CPU CPU5 = new FrequencyData.CPUCluster.CPU("5");
        FrequencyData.CPUCluster.CPU CPU6 = new FrequencyData.CPUCluster.CPU("6");
        FrequencyData.CPUCluster.CPU CPU7 = new FrequencyData.CPUCluster.CPU("7");

        // Silver Cluster with CPU0, CPU1, CPU2, CPU3, CPU4, CPU5
        FreqData.ClusterList.get(0).CPUList.add(CPU0);
        FreqData.ClusterList.get(0).CPUList.add(CPU1);
        FreqData.ClusterList.get(0).CPUList.add(CPU2);
        FreqData.ClusterList.get(0).CPUList.add(CPU3);
        FreqData.ClusterList.get(0).CPUList.add(CPU4);
        FreqData.ClusterList.get(0).CPUList.add(CPU5);

        // Gold Cluster with CPU6 and CPU7
        FreqData.ClusterList.get(1).CPUList.add(CPU6);
        FreqData.ClusterList.get(1).CPUList.add(CPU7);

        // <idle>-0     (-----) [002] .n.1  5823.901096: cpu_idle: state=4294967295 cpu_id=2
        // sugov:0-1832  ( 1832) [002] ....  5823.901134: cpu_frequency: state=748800 cpu_id=1
        // more information on format: https://unix.stackexchange.com/questions/130624/how-to-interpret-cpu-idle-and-cpu-frequency-events-trace-logged-by-ftrace
        Pattern freqRowPattern = Pattern.compile(".* \\[\\d{3}].* (.*): cpu_frequency: state=(\\d*) cpu_id=(\\d)");
        Pattern idleRowPattern = Pattern.compile(".* \\[.*] .* (.*): cpu_idle: state=\\d* cpu_id=(\\d)");

        Document doc = Jsoup.parse(file, "UTF-8", fileName);
        // we have  the following class in three places. The relevant information is in the second element (scriptElements.get(1))
        // <script class="trace-data" type="application/text">
        Elements scriptElements = doc.getElementsByClass("trace-data");
        String sysTraceText = scriptElements.get(1).dataNodes().get(0).getWholeData();



        for (String line : sysTraceText.split("\n")) {
            Matcher freqMatcher = freqRowPattern.matcher(line);
            Matcher idleMatcher = idleRowPattern.matcher(line);

            if (freqMatcher.find()) {
                // must find which CPU it was to save the value in the correct object
                String cpuId = freqMatcher.group(3);
                FreqData.lastTimestamp = Integer.parseInt(toMillisec(freqMatcher.group(1))); // is overwritten every time, in the end its equal to the last timestamp

                // for every cluster
                for (int i = 0; i < FreqData.ClusterList.size(); i++) {
                    FreqData.ClusterList.get(i).CPUList.stream().filter(o -> o.CPUId.equals(cpuId)).forEach(
                            o -> {
                                o.frequenciesList.add(Integer.parseInt(freqMatcher.group(2)));
                                o.timeStampsList.add(Integer.parseInt(toMillisec(freqMatcher.group(1))));
                            }
                    );
                }

            }

            if (idleMatcher.find()) {
                // must find which CPU it was to save the value in the correct object
                String cpuId = idleMatcher.group(2);
                FreqData.lastTimestamp = Integer.parseInt(toMillisec(idleMatcher.group(1))); // is overwritten every time, in the end its equal to the last timestamp

                for (int i = 0; i < FreqData.ClusterList.size(); i++) {
                    FreqData.ClusterList.get(i).CPUList.stream().filter(o -> o.CPUId.equals(cpuId)).forEach(
                            o -> {
                                o.frequenciesList.add(0); // idle means frequency is 0
                                o.timeStampsList.add(Integer.parseInt(toMillisec(idleMatcher.group(1))));
                            }
                    );
                }
            }
        }

        // append the last timestamp to every time list
        for (FrequencyData.CPUCluster Cluster : FreqData.ClusterList) {
            for (FrequencyData.CPUCluster.CPU CPU : Cluster.CPUList){
                CPU.timeStampsList.add(FreqData.lastTimestamp);
            }
        }

        FreqData.ClusterList.add(ClusterGold);
        FreqData.ClusterList.add(ClusterSilver);

        return FreqData;
    }


    private static String toMillisec(String time) {
        int s = 0;
        int dec = 0;
        int totaltime;
        Pattern pattern = Pattern.compile("(\\d*).(\\d{3}).*");
        Matcher matcher1 = pattern.matcher(time);
        if (matcher1.find()) {
            s = Integer.parseInt(matcher1.group(1));
            dec = Integer.parseInt(matcher1.group(2));
        }
        if (s != 0) {
            s = s * 1000;
        }
        totaltime = s + dec;
        return Integer.toString(totaltime);
    }
}