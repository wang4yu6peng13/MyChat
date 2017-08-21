package model;

import utils.StringHelper;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Hongbao {
    //private String hongbaoId;
    private static AtomicInteger id = new AtomicInteger(0);
    private boolean isRandom = false;   // "0" false   other: true
    private int leftMoney;
    private int totalMoney;
    private int count;
    private int leftCount;
    private Map<String, Integer> hbUsrMap = Collections.synchronizedMap(new HashMap<>());
    private String roomName;
    private String sentUserName;

    public Hongbao(int totalMoney, int count) {
        //hongbaoId = Integer.toString(id.incrementAndGet());
        id.incrementAndGet();
        leftMoney = totalMoney;
        leftCount = count;
    }

    public String getInfoOrMax() {
        Integer maxMoney = Integer.MIN_VALUE;
        String maxUser = "";
        List<Map.Entry<String, Integer>> list = new ArrayList<>(Collections.unmodifiableMap(hbUsrMap).entrySet());
        list.sort(new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        StringBuilder sb = new StringBuilder("↓↓↓↓↓↓↓↓抢红包情况↓↓↓↓↓↓↓↓\n");
        for (int i = 0; i < list.size(); ++i) {
            Map.Entry<String, Integer> entry = list.get(i);
            //System.out.println(e.getKey()+":"+e.getValue());
            String username = entry.getKey();
            int money = entry.getValue();
            sb.append("@").append(username).append(" 抢到红包 ￥").append(StringHelper.moneyDivideBy100(money));
            if(isRandom && i == 0)
                sb.append(" 手气最佳");
            sb.append("\n");
        }

//        for (Map.Entry<String, Integer> entry : Collections.unmodifiableMap(hbUsrMap).entrySet()) {
//            String username = entry.getKey();
//            int money = entry.getValue();
//            sb.append("@").append(username).append(" 抢到红包 ￥").append(StringHelper.moneyDivideBy100(money)).append("\n");
//            if(money > maxMoney){
//                maxMoney = money;
//                maxUser = username;
//            }
//        }
//        if(isRandom){
//            // 拼手气
//            //System.out.println("@" + maxUser + " 手气最佳，抢到红包 ￥" + maxMoney);
//            sb.append("@").append(maxUser).append(" 手气最佳，抢到红包 ￥").append(StringHelper.moneyDivideBy100(maxMoney)).append("\n");
//        }
        sb.append("↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑\n");
        return sb.toString();
    }

    public synchronized void qiang(String nameQiang) {
        if (totalMoney < 1 || count < 1 || totalMoney < count)
            return;
        if (hbUsrMap.containsKey(nameQiang))
            return;

        leftCount = updateLeftCount();
        if (leftCount < 1) {
            // 红包抢完
            leftMoney = 0;
        } else if (leftCount == 1) {
            // 只剩一个红包
            hbUsrMap.put(nameQiang, leftMoney);
            leftMoney = 0;
            //getInfoOrMax();
        } else {
            int getMoney = isRandom ? (new Random().nextInt(leftMoney - leftCount + 1) + 1) : getAverageMoney();
            leftMoney -= getMoney;
            hbUsrMap.put(nameQiang, getMoney);
        }
        leftCount = updateLeftCount();
    }

    private int getAverageMoney() {
        return totalMoney / count;
    }

    private int updateLeftCount() {
        return count - hbUsrMap.size();
    }

    public int getLeftCount() {
        return updateLeftCount();
    }

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
