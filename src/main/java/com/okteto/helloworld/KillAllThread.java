package com.okteto.helloworld;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class KillAllThread {
    @Scheduled(cron = "0 01 0 * * ?")
    //@Scheduled(cron = "0/5 * * * * ?")
    public void killAllThread() {
        Application.killThread();
    }

    /**
     * 链接断开回收进程
     */
    @Scheduled(cron = "0/60 * * * * ?")
    public void checkThread() {
        ArrayList<TransPortData> mThread = Application.getmThreads();
        ArrayList<TransPortData> removeList = new ArrayList<>();
        for (TransPortData tmpThread : mThread) {
            if (tmpThread.isClose()) {//判断是否链接断开
                tmpThread.stopThread();
                //mThread.remove(tmpThread);
                removeList.add(tmpThread);
            }
        }
        for (TransPortData removeObj : removeList) {
            mThread.remove(removeObj);
        }
    }
}
