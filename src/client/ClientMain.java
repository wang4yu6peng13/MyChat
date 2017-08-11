package client;

import utils.StringHelper;

import java.util.Scanner;

public class ClientMain {

    public static void main(String[] args) {
        ChatClient client = new ChatClient();
        Scanner scanner = new Scanner(System.in);
        try {
            client.connect("127.0.0.1", 8909);
//            System.out.println(">0：登录；>1：获取用户列表；>2.获取聊天室列表"
//                    + "；>3.创建聊天室；>4.加入聊天室；>5.查询房间成员；"
//                    + ">6.退出聊天室；>7.退出登录；>8.退出客户端");
//            System.out.println("发送消息格式为——#用户ID:消息正文 或者 $聊天室ID:消息正文");

            System.out.println("命令:");
            System.out.println("$create 房间名 房间简介 \t-- 创建房间");
            System.out.println("$enter 房间名 \t\t\t-- 进入聊天室");
            System.out.println("$xxx \t\t\t\t\t-- 在聊天室中直接发言");
            System.out.println("$@昵称 xxx \t\t\t\t-- 对另一人密语");
            System.out.println("$exit \t\t\t\t\t-- 退出房间，回到大厅");

            // 登录
            if (!client.hasLogin()) {
                System.out.print("用户名:");
                String username = scanner.nextLine();
                System.out.print("密码:");
                String passwd = scanner.nextLine();
                client.username = username;
                client.login(client.username, passwd);
                System.out.println("正在登录中...");
            } else {
                System.out.println("当前用户:"+ client.username);
            }

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

                // create enter // xxx @ exit
                input = input.trim();
                if(input.startsWith("$create ")){
                    String[] contents = input.split("\\s+");
                    if(contents.length == 3){
                        String roomName = contents[1];
                        String roomInfo = contents[2];
                        client.createChatRoom(roomName, roomInfo);
                        System.out.println("正在创建聊天室...");
                    }
                }else if(input.startsWith("$enter ")){
                    String[] contents = input.split("\\s+");
                    if(contents.length == 2){
                        String roomName = contents[1];
                        client.enterChatRoom(roomName);
                        System.out.println("正在加入聊天室...");
                    }
                }else if(input.equals("$exit")){
                    client.exitChatRoom();
                    System.out.println("正在退出聊天室");
                }else if(input.startsWith("$")){
                    if (!client.hasLogin()) {
                        System.out.println("尚未登录请先登录！");
                        continue;
                    }
                    if(input.startsWith("$@")){
                        String str = input.substring(2);
                        int splitIndex = str.indexOf(" ");
                        //System.out.println(splitIndex);
                        if(splitIndex < 1){
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
                    }else{
                        String message = input.substring(1);
                        if (StringHelper.isNullOrTrimEmpty(message)) {
                            System.out.println("消息格式错误");
                            continue;
                        }
                        client.sendMsgToRoom(message);
                    }
                }

/*



                int choose = -1;
                String type = input.substring(0, 1);
                switch (type) {
                    case ">":
                        try {
                            choose = Integer.parseInt(input.substring(1, input.length()));
                        } catch (NumberFormatException e) {
                            System.out.println("输入格式不对！>后面必须跟随整数菜单索引");
                            continue;
                        }
                        break;
                    case "#":
                    case "$":
                        if (!client.hasLogin()) {
                            System.out.println("尚未登录请先登录！");
                            continue;
                        }
                        if (input.length() < 4 || !input.contains(":")) {
                            System.out.println("发送消息格式为——#用户ID:消息正文 或者 $聊天室ID:消息正文");
                        }
                        String str = input.substring(1);
                        int splitIndex = str.indexOf(":");
                        String peerName = str.substring(0, splitIndex);
                        String message = str.substring(splitIndex + 1, str.length());
                        if (StringHelper.isNullOrTrimEmpty(peerName) || StringHelper.isNullOrTrimEmpty(message)) {
                            System.out.println("消息格式错误");
                            continue;
                        }
                        if (type.equals("#")) {
                            if (peerName.equals(client.username)) {
                                System.out.println("不能对自己发消息");
                                continue;
                            }
                            client.sendMsgToUser(peerName, message);
                        } else if (type.equals("$")) {
                            client.sendMsgToRoom(peerName, message);
                        }
                        break;
                    default:
                        continue;
                }
                switch (choose) {
                    case 0:
                        if (!client.hasLogin()) {
                            System.out.print("用户名:");
                            String username = scanner.nextLine();
                            System.out.print("密码:");
                            String passwd = scanner.nextLine();
                            client.username = username;
                            client.login(client.username, passwd);
                            System.out.println("正在登录中...");
                        } else {
                            System.out.println("您已登录，请退出当前账户");
                        }
                        break;
                    case 1:
                        client.queryUserList();
                        System.out.println("发送查询用户列表请求");
                        break;
                    case 2:
                        client.queryAllRoomList();
                        System.out.println("发送查询聊天室列表请求");
                        break;
                    case 3: {
                        System.out.print("输入房间名称：");
                        String roomName = scanner.nextLine();
                        if (!StringHelper.isNullOrTrimEmpty(roomName)) {
                            client.createChatRoom(roomName);
                            System.out.println("正在创建聊天室...");
                        }
                        break;
                    }
                    case 4: {
                        System.out.print("输入你要加入的房间名称：");
                        String roomName = scanner.nextLine();
                        if (!StringHelper.isNullOrTrimEmpty(roomName)) {
                            client.enterChatRoom(roomName);
                            System.out.println("正在加入聊天室...");
                        }
                        break;
                    }
                    case 5: {
                        System.out.print("输入待查询成员的房间名称：");
                        String roomName = scanner.nextLine();
                        if (!StringHelper.isNullOrTrimEmpty(roomName)) {
                            client.queryChatRoomMembers(roomName);
                            System.out.println("正在查询聊天室所有成员...");
                        }
                        break;
                    }
                    case 6: {
                        System.out.print("输入你要退出的房间名称：");
                        String roomName = scanner.nextLine();
                        if (!StringHelper.isNullOrTrimEmpty(roomName)) {
                            client.exitChatRoom(roomName);
                            System.out.println("正在退出聊天室[" + roomName + "]");
                        }
                        break;
                    }
                    case 7:
                        client.logout();
                        break;
                    case 8:
                        isExit = true;
                        client.logout();
                        client.shutdown();
                        System.out.println("退出客户端");
                        break;
                    default:
                        break;
                }*/
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
