package edu.uchicago.mpcs56420.Benchmark;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;

/**
 * Created by Anuved on 11/28/2016.
 */
public class Memory {

    public static void printMemStatus() {
        for (MemoryPoolMXBean mpBean: ManagementFactory.getMemoryPoolMXBeans()) {
            if (mpBean.getType() == MemoryType.HEAP) {
                System.out.printf(
                        "Name: %s: %s\n",
                        mpBean.getName(), mpBean.getUsage().getUsed()
                );
                mpBean.getUsage().getUsed();
            }
        }
        System.out.println();

    }

    public static long getMemUsageEstimate() {
        long memEstimate = 0;
        for (MemoryPoolMXBean mpBean: ManagementFactory.getMemoryPoolMXBeans()) {
            if (mpBean.getType() == MemoryType.HEAP) {
                memEstimate += mpBean.getUsage().getUsed();
            }
        }

        return memEstimate;
    }

}
