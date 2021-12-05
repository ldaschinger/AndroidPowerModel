package com.example.batterystats.core.batterystats;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class BatteryStatsParserCSV {
    public static EnergyInfo parseFile(String fileName, int traceviewStart) throws IOException {
        EnergyInfo EnergyInfo = new EnergyInfo();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values[0].equals("9") && values[1].equals("0") && values[2].equals("i") && values[3].equals("uid")) {
                    // in this case we have a line like: 9,0,i,uid,10034,com.android.mms
                    // we enter each uid-package name pair of such lines in the list in the EnergyInfo object
                    EnergyInfo.uidToPackage pair = new EnergyInfo.uidToPackage();
                    pair.uid = Integer.parseInt(values[4]);
                    pair.packageName = values[5];
                    EnergyInfo.uidToPackageList.add(pair);
                } else if (values[0].equals("9") && values[1].equals("0") && values[2].equals("l") && values[3].equals("gble")){
                    // global usage of bluetooth: 9,0,l,gble,5095,3186,0,0 = idle, rx, tx, battery
                    EnergyInfo.bluetooth triplet = new EnergyInfo.bluetooth();
                    triplet.idle = Integer.parseInt(values[4]);
                    triplet.rx = Integer.parseInt(values[5]);
                    triplet.tx = Integer.parseInt(values[6]);
                    EnergyInfo.bluetoothData = triplet;
                } else if (values[0].equals("9")  && values[2].equals("l")){
                    // TODO only enter here if we also have values[3] = cpu, wfcd, pr or aud
                    // add the per uid values
                    addDataToUidObject(EnergyInfo.uidEnergyStatsList, values);
                }

                // when we arrive at the point in the file
//                energyInfoArray.add(Arrays.asList(values));
            }
        }

//        records.get(records.size() - 1).setExit(Integer.MAX_VALUE);

        return EnergyInfo;
    }


//    public boolean containsUid(final List<EnergyInfo.uidEnergyStats> list, final int uid){
//        return list.stream().map(list.uid).filter(name::equals).findFirst().isPresent();
//    }
//    public static void addWifiToUidObject(final List<EnergyInfo.uidEnergyStats> list, final String uid){
    public static void addDataToUidObject(final List<EnergyInfo.uidEnergyStats> list, final String[] values){
        if (!(list.stream().filter(o -> o.uid.equals(values[1])).findFirst().isPresent())){
            // if there is no object in the list for this uid yet we add one
            EnergyInfo.uidEnergyStatsList.add(new EnergyInfo.uidEnergyStats());
        }

        // values[3] gives the short description of this line's data type
        switch (values[3]) {
            case "wfcd":
                // add the info to the right object (same uid)
                list.stream().filter(o -> o.uid.equals(values[1])).forEach(
                        o -> {
                            o.wifiData = new EnergyInfo.wifiPerUid();
                            // TODO must still determine exact mapping for scan and idle for sure
                            o.wifiData.scan = 0;
                            o.wifiData.sleep = 0;
                            o.wifiData.idle = 0;
                            o.wifiData.rx = Integer.parseInt(values[5]);
                            o.wifiData.tx = Integer.parseInt(values[7]);
                        }
                );
                break;
            case "aud":
                break;
            case "cpu":
                break;
            case "pr":
                break;
            default:
                break;
        }

    }

}
