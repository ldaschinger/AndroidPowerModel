package com.example.batterystats;

import com.example.batterystats.core.batterystats.BatteryStatsParserCSV;
import com.example.batterystats.core.batterystats.EnergyCalculator;
import com.example.batterystats.core.batterystats.EnergyInfo;
import com.example.batterystats.systrace.FrequencyCalculator;
import com.example.batterystats.systrace.FrequencyData;
import com.example.batterystats.systrace.FrequencyParse;

import java.io.IOException;


public class BatteryStats {
    public static void main(String[] args) throws IOException {
        if (args[0].equals("--systrace") && (!args[1].isEmpty())
        && args[2].equals("--batterystats") && (!args[3].isEmpty())) {
            // the total Energy for Android consumed by a component or process is calculated based on:
            // the utilization time in ms from the batterystats file
            // the power specification values given in mA from the power profile (PowerProfile.java)
            // Android calculates the used charge [mAh] = ms*A/(60*60*1000)
            // we calculate the used Energy [J] = ms*A*V/1000

            EnergyInfo EnergyInfo = new EnergyInfo();
            FrequencyData FreqData = new FrequencyData();

            System.out.println("start parsing of file:" + args[3]);
            EnergyInfo = BatteryStatsParserCSV.parseFile(args[3]);

            System.out.println("now calculate power usage:");
            EnergyCalculator.calculatePowerUsage(EnergyInfo);

            System.out.println("start parsing systrace file:" + args[1]);
            FreqData = FrequencyParse.parseSystrace(args[1]);

            System.out.println("now calculate frequency usages:");
            FrequencyCalculator.calculateFrequencyUsage(FreqData);

        } else {
            System.out.println("usage:");
            System.out.println("\tjava -jar BatteryStats.jar --power_profile path/power_profile.xml --batterystats path/batterystats\n");
        }
    }


//    private static void calculatePowerUsage(){
//        System.out.println("opening batterystats file:");
//
////        File file = new File("C:\\Users\\pankaj\\Desktop\\test.txt");
////        System.out.println("Run " + run + ": saving battery stats.");
////        this.executeCommand("adb shell dumpsys batterystats", new File(batteryStatsFilename));
//
//    }


}
