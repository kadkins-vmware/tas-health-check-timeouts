package com.vmware.tas.healthchecktimeouts;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class MyHealthIndicator implements HealthIndicator {

    static boolean appStarted = false;

    @Override
    public Health health() {
        int errorCode = check();
        if (errorCode != 0) {
            return Health.down().withDetail("Error Code", errorCode).build();
        }
        return Health.up().build();
    }

    private int check() {

        // Load thread sleep times from env vars
        Map<String, String> env = System.getenv();
        int iAppSleepAtStart = 1;
        int iAppSleepAfterStart = 1;
        String appSleepAtStart = env.get("HCK_SLEEP_AT_START");
        String appSleepAfterStart = env.get("HCK_SLEEP_AFTER_START");
        try {
            iAppSleepAtStart = Integer.parseInt(appSleepAtStart);
        } catch(NumberFormatException nfe) { /* ignore */ }
        try {
             iAppSleepAfterStart = Integer.parseInt(appSleepAfterStart);
        } catch(NumberFormatException nfe) { /* ignore */ }

        // perform health check sleep
        try {
            if(appStarted) {
                System.out.format("==== Sleeping after start for %d seconds... ====\n", iAppSleepAfterStart);
                Thread.sleep(iAppSleepAfterStart * 1000);
            } else {
                System.out.format("==== Sleeping at start for %d seconds... ====\n", iAppSleepAtStart);
                Thread.sleep(iAppSleepAtStart * 1000);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(!appStarted) { appStarted = true;}

        return 0;
    }
}

