package com.leyou.gateway.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SchedulingDemo {

    private Integer i = 0;

    /**
     * cron表达式定时任务
     * 每年，每月，1到6号，每天上午9点，从3分开始，每隔5分钟的第八秒执行一次
     */
//    @Scheduled(cron = "8 3/5 9 1-6 * ?")
//    public void cronJob(){
//        if(i==1){
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        log.info("cron表达式定时任务执行了！");
//        i++;
//    }

    /**
     * 固定延迟执行定时任务
     */
//    @Scheduled(fixedDelay = 2000)
//    public void delayJob(){
//        if(i==1){
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        log.info("固定延迟定时任务执行了！");
//        i++;
//    }

    /**
     * 固定频率执行定时任务
     */
//    @Scheduled(fixedRate = 2000)
//    public void rateJob(){
//        if(i==1){
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        log.info("固定频率定时任务执行了！");
//        i++;
//    }
}
