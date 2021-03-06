package client;

import utils.StringHelper;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientMain {

    public static void main(String[] args) {
        ChatClient client = new ChatClient();
        Scanner scanner = new Scanner(System.in, "UTF-8");
        try {
            client.connect("127.0.0.1", 8909);

            System.out.println("命令:");
            System.out.println("$login 用户名 密码 \t\t-- 登录或注册 \t\t$rooms \t\t\t\t\t\t-- 聊天室列表");
            System.out.println("$logout \t\t\t\t-- 退出登录 \t\t\t$users( 房间名)\t\t\t\t-- （聊天室里）在线用户列表");
            System.out.println("$quit \t\t\t\t\t-- 退出客户端 \t\t$xxx \t\t\t\t\t\t-- 在聊天室中直接发言");
            System.out.println("$create 房间名 房间简介 \t-- 创建房间 \t\t\t$@昵称 xxx \t\t\t\t\t-- 对另一人密语");
            System.out.println("$enter 房间名 \t\t\t-- 进入聊天室 \t\t$hongbao 金额,个数(,拼手气) \t-- 发（拼手气）红包");
            System.out.println("$exit \t\t\t\t\t-- 退出房间，回到大厅 \t$qiang 红包编号 \t\t\t\t-- 抢红包");

            boolean isExit = false;
            while (!isExit) {
                // 命令
                String input;
                try {
                    input = scanner.nextLine();
                } catch (Exception e) {
                    continue;
                }
                if (StringHelper.isNullOrTrimEmpty(input))
                    continue;

                input = input.trim();
                if (input.startsWith("$login ")) {
                    String[] contents = input.split("\\s+");
                    if (contents.length == 3) {
                        String username = contents[1];
                        String passwd = contents[2];
                        // 登录
                        if (!client.hasLogin()) {
                            client.username = username;
                            client.login(client.username, passwd);
                            System.out.println(client.username + " 正在登录中...");
                        } else {
                            System.out.println("当前用户:" + client.username);
                        }
                    }
                } else if (input.equals("$quit")) {
                    isExit = true;
                    client.logout();
                    client.shutdown();
                    System.out.println("退出客户端");
                } else {
                    if (!client.hasLogin()) {
                        System.out.println("尚未登录请先登录！");
                        continue;
                    }
                    if (input.startsWith("$create ")) {
                        String[] contents = input.split("\\s+");
                        if (contents.length == 3) {
                            String roomName = contents[1];
                            String roomInfo = contents[2];
                            client.createChatRoom(roomName, roomInfo);
                            System.out.println("正在创建聊天室...");
                        }
                    } else if (input.startsWith("$enter ")) {
                        String[] contents = input.split("\\s+");
                        if (contents.length == 2) {
                            String roomName = contents[1];
                            client.enterChatRoom(roomName);
                            System.out.println("正在加入聊天室...");
                        }
                    } else if (input.equals("$exit")) {
                        client.exitChatRoom();
                        System.out.println("正在退出聊天室");
                    } else if (input.equals("$logout")) {
                        client.logout();
                        System.out.println("退出登录");
                    } else if (input.equals("$rooms")) {
                        client.queryAllRoomList();
                        System.out.println("发送查询聊天室列表请求");
                    } else if(input.startsWith("$users")) {
                        String[] contents = input.split("\\s+");
                        if (contents.length == 1) {
                            client.queryUserList();
                            System.out.println("发送查询用户列表请求");
                        }else if(contents.length == 2){
                            String roomName = contents[1];
                            if (!StringHelper.isNullOrTrimEmpty(roomName)) {
                                client.queryChatRoomMembers(roomName);
                                System.out.println("正在查询聊天室[" + roomName+ "]所有成员...");
                            }
                        }
                    } else if (input.startsWith("$hongbao ")) {
                        String regEx = "\\$hongbao\\s+(\\d+(\\.\\d{0,2})?)\\s*,\\s*(\\d+)\\s*(,\\s*(.+)\\s*)?$";
                        Pattern pat = Pattern.compile(regEx);
                        Matcher mat = pat.matcher(input);
                        if (mat.find()) {
                            //System.out.println(mat.group(1)+ "!" + mat.group(2) +"!" + mat.group(3) +"!" + mat.group(4));
                            String totalMoney = StringHelper.moneyMulti100(mat.group(1));   //(\d+(\.\d{0,2})?)
                            String count = mat.group(3);        //(\d+)
                            //System.out.println("[" + totalMoney+ "!" + count + "!" + mat.group(4) + "]" );
                            if (mat.group(5) == null) {         //(.+)
                                // 普通
                                client.sendHongbao(totalMoney, count, "0");
                                System.out.println("正在发红包...");
                            } else {
                                // 手气
                                client.sendHongbao(totalMoney, count, "1");
                                System.out.println("正在发拼手气红包...");
                            }
                        }
                    } else if (input.startsWith("$qiang ")) {
                        String regEx = "\\$qiang\\s+(\\d+)\\s*$";
                        Pattern pat = Pattern.compile(regEx);
                        Matcher mat = pat.matcher(input);
                        if (mat.find()) {
                            String hbId = mat.group(1);
                            client.qiangHongbao(hbId);
                            System.out.println("正在抢红包...");
                        }
                    } else if (input.startsWith("$")) {
                        if (input.startsWith("$@")) {
                            String str = input.substring(2);
                            int splitIndex = str.indexOf(" ");
                            //System.out.println(splitIndex);
                            if (splitIndex < 1) {
                                System.out.println("格式错误");
                                continue;
                            }
                            String peerName = str.substring(0, splitIndex);
                            String message = str.substring(splitIndex + 1);
                            if (StringHelper.isNullOrTrimEmpty(peerName) || StringHelper.isNullOrTrimEmpty(message)) {
                                System.out.println("消息格式错误");
                                continue;
                            }
                            if (peerName.equals(client.username)) {
                                System.out.println("不能对自己发消息");
                                continue;
                            }
                            client.sendMsgToUser(peerName, message);
                        } else {
                            String message = input.substring(1);
                            if (StringHelper.isNullOrTrimEmpty(message)) {
                                System.out.println("消息格式错误");
                                continue;
                            }
                            client.sendMsgToRoom(message);
                        }
                    }
                }

            }
            scanner.nextLine();
            scanner.close();
            scanner = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.shutdown();
            if (scanner != null) {
                scanner.close();
            }
        }
    }

}
