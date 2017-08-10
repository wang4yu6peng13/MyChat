package client;

import utils.StringHelper;

import java.util.Scanner;

public class ClientMain {

    public static void main(String[] args) {
        ChatClient client = new ChatClient();
        Scanner scanner = new Scanner(System.in);
        try {
            client.connect("127.0.0.1", 8909);
            System.out.println(">0：登录；>1：获取用户列表；>2.获取聊天室列表"
                    + "；>3.创建聊天室；>4.加入聊天室；>5.查询房间成员；"
                    + ">6.退出聊天室；>7.退出登录；>8.退出客户端");
            System.out.println("发送消息格式为——#用户ID:消息正文 或者 $聊天室ID:消息正文");
            boolean isExit = false;
            while (!isExit) {
                String input = null;
                try {
                    input = scanner.nextLine();
                } catch (Exception e) {
                    // TODO: handle exception
                    continue;
                }
                if (StringHelper.isNullOrTrimEmpty(input))
                    continue;
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
                        String peerId = str.substring(0, splitIndex);
                        String message = str.substring(splitIndex + 1, str.length());
                        if (StringHelper.isNullOrTrimEmpty(peerId) || StringHelper.isNullOrTrimEmpty(message)) {
                            System.out.println("消息格式错误");
                            continue;
                        }
                        if (type.equals("#")) {
                            if (peerId.equals(client.username)) {
                                System.out.println("不能对自己发消息");
                                continue;
                            }
                            client.sendMsgToUser(peerId, message);
                        } else if (type.equals("$")) {
                            client.sentMsgToRoom(peerId, message);
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
                        String roomId = scanner.nextLine();
                        if (!StringHelper.isNullOrTrimEmpty(roomId)) {
                            client.createChatRoom(roomId);
                            System.out.println("正在创建聊天室...");
                        }
                        break;
                    }
                    case 4: {
                        System.out.print("输入你要加入的房间名称：");
                        String roomId = scanner.nextLine();
                        if (!StringHelper.isNullOrTrimEmpty(roomId)) {
                            client.joinChatRoom(roomId);
                            System.out.println("正在加入聊天室...");
                        }
                        break;
                    }
                    case 5: {
                        System.out.print("输入待查询成员的房间名称：");
                        String roomId = scanner.nextLine();
                        if (!StringHelper.isNullOrTrimEmpty(roomId)) {
                            client.queryChatRoomMembers(roomId);
                            System.out.println("正在查询聊天室所有成员...");
                        }
                        break;
                    }
                    case 6: {
                        System.out.print("输入你要退出的房间名称：");
                        String roomId = scanner.nextLine();
                        if (!StringHelper.isNullOrTrimEmpty(roomId)) {
                            client.leaveChatRoom(roomId);
                            System.out.println("正在退出聊天室[" + roomId + "]");
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
                }
            }
            scanner.nextLine();
            scanner.close();
            scanner = null;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            client.shutdown();
            if (scanner != null) {
                scanner.close();
            }
        }
    }

}
