package com.example.batterystats.core.batterystats;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BatteryStatsParserCSV {
    public static ArrayList<EnergyInfo> parseFile(String fileName, int traceviewStart) throws IOException {
        ArrayList<EnergyInfo> energyInfoArray = new ArrayList<>();

//        List<List<String>> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values[0].equals("9")) {
                    System.out.println("FUCKK:");
                }
//                energyInfoArray.add(Arrays.asList(values));
            }
        }

//        records.get(records.size() - 1).setExit(Integer.MAX_VALUE);

        return energyInfoArray;
    }
}
