package model;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Hongbao {
    //private String hongbaoId;
    static AtomicInteger id = new AtomicInteger(0);
    private boolean isRandom = false;   // "0" false   other: true
    private int leftMoney;
    private int totalMoney;
    private int count;
    private Map<String, Integer> hbUsrMap = Collections.synchronizedMap(new HashMap<>());
    private String roomName;
    private String sentUserName;

    public Hongbao(int totalMoney) {
        //hongbaoId = Integer.toString(id.incrementAndGet());
        id.incrementAndGet();
        leftMoney = totalMoney;
    }

    public void getInfoOrMax(){
        Iterator<Map.Entry<String, Integer>> iter = Collections.unmodifiableMap(hbUsrMap).entrySet().iterator();
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

    public synchronized void qiang(String nameQiang) {
        if (totalMoney < 1 || count < 1 || totalMoney < count)
            return;
        if (hbUsrMap.containsKey(nameQiang))
            return;

        int leftCount = count - hbUsrMap.size();
        if (leftCount < 1) {
            // 红包抢完
            leftMoney = 0;
        } else if (leftCount == 1) {
            // 只剩一个红包
            hbUsrMap.put(nameQiang, leftMoney);
            leftMoney = 0;
        } else {
            int getMoney = isRandom ? (new Random().nextInt(leftMoney - leftCount + 1) + 1) : getAverageMoney();
            leftMoney -= getMoney;
            hbUsrMap.put(nameQiang, getMoney);
        }

    }

    public int getAverageMoney() {
        return totalMoney / count;
    }

//    public String getHongbaoId() {
//        return hongbaoId;
//    }
//
//    public void setHongbaoId(String hongbaoId) {
//        this.hongbaoId = hongbaoId;
//    }

    public String getId() {
        return Integer.toString(id.get());
    }

    public boolean isRandom() {
        return isRandom;
    }

    public void setRandom(boolean random) {
        isRandom = random;
    }

    public int getLeftMoney() {
        return leftMoney;
    }

    public void setLeftMoney(int leftMoney) {
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

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public Map<String, Integer> getHbUsrMap() {
        return hbUsrMap;
    }

    public String getSentUserName() {
        return sentUserName;
    }

    public void setSentUserName(String sentUserName) {
        this.sentUserName = sentUserName;
    }

}
