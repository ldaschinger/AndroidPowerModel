package com.example.batterystats;

import com.example.batterystats.core.batterystats.BatteryStatsParserCSV;
import com.example.batterystats.core.batterystats.EnergyInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class BatteryStats {
    public static void main(String[] args) throws IOException {
        if (args[0].equals("--power_profile") && (!args[1].isEmpty())
        && args[2].equals("--batterystats") && (!args[3].isEmpty())) {
//            Terminal.run(args[1]);
            System.out.println("start calculatePowerUsage:" + args[3]);

            ArrayList<EnergyInfo> energyInfoArray = new ArrayList<>();

            energyInfoArray = BatteryStatsParserCSV.parseFile(args[3], 23);

            System.out.println(Arrays.toString(energyInfoArray.toArray()));

//            calculatePowerUsage();
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
