package model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Hongbao {
    private int hongbaoId;
    private boolean isRandom = false;
    private AtomicInteger leftMoney;
    private int totalMoney;
    private int count;
    private Map<String, Integer> hbUsrMap = new HashMap<>();

    public void getInfoOrMax(){
        Iterator<Map.Entry<String, Integer>> iter = hbUsrMap.entrySet().iterator();
        Integer maxMoney = Integer.MIN_VALUE;
        String maxUser = "";
        //Map.Entry<String, Float> entryMax = null;
        while(iter.hasNext()){
            Map.Entry<String, Integer> entry = iter.next();
            String username = entry.getKey();
            int money = entry.getValue();
            System.out.println("@" + username + " 抢到红包 ￥" + money);
            if(money > maxMoney){
                maxMoney = money;
                //entryMax = entry;
                maxUser = username;
            }
        }
        if(isRandom){
            // 拼手气
            System.out.println("@" + maxUser + " 手气最佳，抢到红包 ￥" + maxMoney);
        }
    }

    public int getHongbaoId() {
        return hongbaoId;
    }

    public void setHongbaoId(int hongbaoId) {
        this.hongbaoId = hongbaoId;
    }

    public boolean isRandom() {
        return isRandom;
    }

    public void setRandom(boolean random) {
        isRandom = random;
    }

    public AtomicInteger getLeftMoney() {
        return leftMoney;
    }

    public void setLeftMoney(AtomicInteger leftMoney) {
        this.leftMoney = leftMoney;
    }

    public int getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(int totalMoney) {
        this.totalMoney = totalMoney;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
