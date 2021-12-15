package com.example.batterystats.core.batterystats;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;


public class BatteryStatsParserCSV {
    public static EnergyInfo parseFile(String fileName) throws IOException {
        EnergyInfo EnergyInfo = new EnergyInfo();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");

                // remove \ from the last String in the array if present
                String lastString = values[values.length-1];
                StringBuffer sb = new StringBuffer(lastString);
                if (sb.length() > 1 && sb.charAt(sb.length()-1) == '\\') {
                    sb.deleteCharAt(sb.length()-1);
                    values[values.length-1] = sb.toString();
                }

                if (values[0].equals("9") && values[1].equals("0") && values[2].equals("i") && values[3].equals("uid")) {
                    // in this case we have a line like: 9,0,i,uid,10034,com.android.mms
                    // we enter each uid-package name pair of such lines in the list in the EnergyInfo object
                    EnergyInfo.uidToPackage pair = new EnergyInfo.uidToPackage();
                    pair.uid = values[4];
                    pair.packageName = values[5];
                    EnergyInfo.uidToPackageList.add(pair);
                } else if (values[0].equals("9") && values[1].equals("0") && values[2].equals("l") && values[3].equals("gble")){
                    // global usage of bluetooth: 9,0,l,gble,5095,3186,0,0 = idle, rx, tx, battery
                    EnergyInfo.bluetooth triplet = new EnergyInfo.bluetooth();
                    triplet.idle = Integer.parseInt(values[4]);
                    triplet.rx = Integer.parseInt(values[5]);
                    triplet.tx = Integer.parseInt(values[7]);
                    EnergyInfo.bluetoothData = triplet;
                } else if (values[0].equals("9")  && values[2].equals("l") &&
                        (values[3].equals("cpu") || values[3].equals("pr") || values[3].equals("wfcd") || values[3].equals("aud") || values[3].equals("cam"))){
                    // add the per uid values
                    addDataToUidObject(EnergyInfo, values);
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
    // https://stackoverflow.com/questions/18852059/java-list-containsobject-with-field-value-equal-to-x
    private static void addDataToUidObject(EnergyInfo EnergyInfo, String[] values){
        if (!(EnergyInfo.uidEnergyStatsList.stream().filter(o -> o.uid.equals(values[1])).findFirst().isPresent())){
            // if there is no object in the list for this uid yet we add one
            EnergyInfo.uidEnergyStats newUidEnergyStats = new EnergyInfo.uidEnergyStats();
            newUidEnergyStats.uid = values[1];
            EnergyInfo.uidEnergyStatsList.add(newUidEnergyStats);
        }

        // values[3] gives the short description of this line's data type
        switch (values[3]) {
            case "wfcd":
                // add the info to the right object (same uid)
                EnergyInfo.uidEnergyStatsList.stream().filter(o -> o.uid.equals(values[1])).forEach(
                        o -> {
                            o.wifiData = new EnergyInfo.uidEnergyStats.wifiPerUid();
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
                // add the info to the right object (same uid)
                EnergyInfo.uidEnergyStatsList.stream().filter(o -> o.uid.equals(values[1])).forEach(
                        o -> {
                            o.audioPerUid = Integer.parseInt(values[4]);
                        }
                );
                break;
            case "cam":
                // add the info to the right object (same uid)
                EnergyInfo.uidEnergyStatsList.stream().filter(o -> o.uid.equals(values[1])).forEach(
                        o -> {
                            o.cameraPerUid = Integer.parseInt(values[4]);
                        }
                );
                break;
            case "cpu":
                // add the info to the right object (same uid)
                EnergyInfo.uidEnergyStatsList.stream().filter(o -> o.uid.equals(values[1])).forEach(
                        o -> {
                            o.CPUData = new EnergyInfo.uidEnergyStats.totalCPUUid();
                            o.CPUData.totalUserCPUTime = Integer.parseInt(values[4]);;
                            o.CPUData.totalKernelCPUTime = Integer.parseInt(values[5]);
                        }
                );
                break;
            case "pr":
                // we already created a processPerUidList when the uidEnergyStats object was created for this uid
                // add the info to the right object (same uid)
                EnergyInfo.uidEnergyStatsList.stream().filter(o -> o.uid.equals(values[1])).forEach(
                        o -> {
                            EnergyInfo.uidEnergyStats.processPerUid process = new EnergyInfo.uidEnergyStats.processPerUid(); //create new object
                            process.processName = values[4];
                            process.userCPUTime = Integer.parseInt(values[5]);
                            process.kernelCPUTime = Integer.parseInt(values[6]);
                            o.processPerUidList.add(process);
                        }
                );
                break;
            default:
                break;
        }

    }

}
