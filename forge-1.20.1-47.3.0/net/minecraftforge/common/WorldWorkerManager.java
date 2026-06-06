//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common;

import java.util.ArrayList;
import java.util.List;

public class WorldWorkerManager {
    private static List<IWorker> workers = new ArrayList();
    private static long startTime = -1L;
    private static int index = 0;

    public WorldWorkerManager() {
    }

    public static void tick(boolean start) {
        if (start) {
            startTime = System.currentTimeMillis();
        } else {
            index = 0;
            IWorker task = getNext();
            if (task != null) {
                long time = 50L - (System.currentTimeMillis() - startTime);
                if (time < 10L) {
                    time = 10L;
                }

                time += System.currentTimeMillis();

                while(System.currentTimeMillis() < time && task != null) {
                    boolean again = task.doWork();
                    if (!task.hasWork()) {
                        remove(task);
                        task = getNext();
                    } else if (!again) {
                        task = getNext();
                    }
                }

            }
        }
    }

    public static synchronized void addWorker(IWorker worker) {
        workers.add(worker);
    }

    private static synchronized IWorker getNext() {
        return workers.size() > index ? (IWorker)workers.get(index++) : null;
    }

    private static synchronized void remove(IWorker worker) {
        workers.remove(worker);
        --index;
    }

    public static synchronized void clear() {
        workers.clear();
    }

    public interface IWorker {
        boolean hasWork();

        boolean doWork();
    }
}
