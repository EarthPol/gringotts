package org.gestern.gringotts;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public final class SchedulerUtil {

    private SchedulerUtil() {
    }

    public static void runGlobal(Runnable runnable) {
        Bukkit.getGlobalRegionScheduler().run(Gringotts.instance, task -> runnable.run());
    }

    public static void runNextTick(Location location, Runnable runnable) {
        if (location == null || location.getWorld() == null) {
            runGlobal(runnable);
            return;
        }

        Bukkit.getRegionScheduler().runDelayed(
                Gringotts.instance,
                location.getWorld(),
                location.getBlockX() >> 4,
                location.getBlockZ() >> 4,
                task -> runnable.run(),
                1L
        );
    }
}
