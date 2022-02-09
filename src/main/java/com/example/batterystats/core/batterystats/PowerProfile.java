package com.example.batterystats.core.batterystats;

import java.util.Arrays;
import java.util.List;

// this class describes the power coefficients for different components determined by measurements
public class PowerProfile {
//     static int durationMs = 105000;
     static int numberCores = 8;
     float baselineCurrentNoDozemA = 64; //with wifi and ble
     float baselineCurrentDozemA = 26; //no wifi and ble


     // the following values are given in mA (as in power_profile.xml)
     float bluetoothControllerIdle = 5; //neglected
     float bluetoothControllerTx = 50f;
     // selected this low since experiments have shown that tx has much higher impact on total power
     // the values includes the whole system impact, not only Rx/Tx of the Bluetooth module
     float bluetoothControllerRx = 30f;

     float screenAvg = 144f; //average value for white
//     float screenAvg = 84f; //average value for black
//     float screenAvg = 114f; //average value for mixed


     // typical values: https://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.72.4178&rep=rep1&type=pdf
     // sleep 20mW, idle 110mW, tx 2500 mW, rx 900 mW
     // data sheet states that whole wifi module at 11Mbps 733mW and at lowest rate 453mW
     float wifiScan = 350 ;
     float wifiSleep = 0; // neglected, note: only available globally - not per uid
     float wifiControllerIdle = 15; // note: only available globally - not per uid
     float wifiControllerTx = 2000;
     float wifiControllerRx = 400;

     float audioFromMemory = 42f; // 42mA for replay of audio
     float audioFromMediaStream = 19f;

     float cameraAvg = 113f; // 113mA for use of camera, excluding rendering

     // these values are provided by the manufacturer and can be found in the power_profile.xml
     public PowerProfile() {
          this.mAUsedAtFrequenciesSilver = Arrays.asList(0, 14, 25, 31, 46, 57, 84, 96, 114, 139);
          // removed middle value: 659mA
          this.mAUsedAtFrequenciesGold = Arrays.asList(0, 256, 307, 332, 382, 408, 448, 586, 641, 696, 876, 900, 924, 948, 1170, 1200, 1300, 1400);

          this.frequenciesSilver = Arrays.asList(0, 300000, 576000, 748800, 998400, 1209600, 1324800, 1516800, 1612800, 1708800);
          this.frequenciesGold = Arrays.asList(0, 300000, 652800, 825600, 979200, 1132800, 1363200, 1536000, 1747200, 1843200, 1996800, 2054400, 2169600, 2208000, 2361600, 2400000, 2457600, 2515200);
     }

     static float idleCPU = 0.00000015f;

     List<Integer> mAUsedAtFrequenciesSilver;
     List<Integer> mAUsedAtFrequenciesGold;

     public List<Integer> frequenciesSilver;
     public List<Integer> frequenciesGold;

}
