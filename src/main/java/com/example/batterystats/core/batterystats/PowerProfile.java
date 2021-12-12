package com.example.batterystats.core.batterystats;

import java.util.Arrays;
import java.util.List;

public class PowerProfile {
     // the following values are given in Watt [I*V]
     int bluetoothControllerIdle = 3;
     int bluetoothControllerTx = 3;
     int bluetoothControllerRx = 3;

     int wifiScan = 3;
     int wifiSleep = 3;
     int wifiControllerIdle = 3;
     int wifiControllerTx = 3;
     int wifiControllerRx = 3;

     public PowerProfile() {
          this.mAhUsedAtFrequenciesSilver = Arrays.asList(0, 14, 25, 31, 46, 57, 84, 96, 114, 139);
          // removed middle value: 659mA
          this.mAhUsedAtFrequenciesGold = Arrays.asList(0, 256, 307, 332, 382, 408, 448, 586, 641, 696, 876, 900, 924, 948, 1170, 1200, 1300, 1400);

          this.frequenciesSilver = Arrays.asList(0, 300000, 576000, 748800, 998400, 1209600, 1324800, 1516800, 1612800, 1708800);
          this.frequenciesGold = Arrays.asList(0, 300000, 652800, 825600, 979200, 1132800, 1363200, 1536000, 1747200, 1843200, 1996800, 2054400, 2169600, 2208000, 2361600, 2400000, 2457600, 2515200);
     }

     List<Integer> mAhUsedAtFrequenciesSilver;
     List<Integer> mAhUsedAtFrequenciesGold;

     public List<Integer> frequenciesSilver;
     public List<Integer> frequenciesGold;

}
