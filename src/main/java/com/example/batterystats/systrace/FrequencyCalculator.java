package com.example.batterystats.systrace;

import com.github.sh0nk.matplotlib4j.NumpyUtils;
import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.PythonConfig;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class FrequencyCalculator {
    public static void calculateFrequencyUsage(FrequencyData FreqData) {
        List<Double> x = NumpyUtils.linspace(-3, 3, 100);
        List<Double> y = x.stream().map(xi -> Math.sin(xi) + Math.random()).collect(Collectors.toList());

//        Plot plt = Plot.create();
        Plot plt = Plot.create(PythonConfig.pythonBinPathConfig("/usr/bin/python")); //must use python2
        plt.plot().add(x, y, "o").label("sin");
        plt.legend().loc("upper right");
        plt.title("scatter");
        try {
            plt.show();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (PythonExecutionException e) {
            e.printStackTrace();
        }

//        try {
//            plt.show();
//        }
//        catch(IOException | PythonExecutionException e) {
//            System.out.println("exception caught in plt.show()");
//            e.printStackTrace();
//        }

    }
}
