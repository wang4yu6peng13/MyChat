package model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Hongbao {
    private int hongbaoId;
    private boolean isRandom = false;
    private float money;
    private int count;
    private Map<String, Float> hongbaoMap = new HashMap<>();

    public void getInfoOrMax(){
        Iterator<Map.Entry<String, Float>> iter = hongbaoMap.entrySet().iterator();
        Float maxMoney = Float.MIN_VALUE;
        String maxUser = "";
        //Map.Entry<String, Float> entryMax = null;
        while(iter.hasNext()){
            Map.Entry<String, Float> entry = iter.next();
            String username = entry.getKey();
            Float money = entry.getValue();
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

    public Map<String, Float> getHongbaoMap() {
        return hongbaoMap;
    }

    public void setHongbaoMap(Map<String, Float> hongbaoMap) {
        this.hongbaoMap = hongbaoMap;
    }

    public void setHongbaoId(int hongbaoId) {
        this.hongbaoId = hongbaoId;
    }

    public void setRandom(boolean random) {
        isRandom = random;
    }

    public void setMoney(float money) {
        this.money = money;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getHongbaoId() {
        return hongbaoId;
    }

    public boolean isRandom() {
        return isRandom;
    }

    public float getMoney() {
        return money;
    }

    public int getCount() {
        return count;
    }
}
