package server;

import java.util.Scanner;
import java.util.Set;

public class ServerMain {

    public static void main(String[] args) {
        ChatServer server = new ChatServer();//在默认端口启动服务器
        Thread serverThread = new Thread(server, "聊天服务器");
        serverThread.setDaemon(true);//后台进程
        serverThread.start();
        System.out.println("===输入选择项===");
        System.out.println("1.获取用户列表；2.获取聊天室列表；3.关闭服务器");
        boolean isExit = false;
        Scanner scanner = new Scanner(System.in);
        while (!isExit) {
            String input = scanner.nextLine();
            int choose;
            try {
                choose = Integer.parseInt(input);
            } catch (Exception e) {
                continue;
            }
            switch (choose) {
                case 1:
                    Set<String> users = server.getUserList();
                    if (users.isEmpty()) {
                        System.out.println("当前无用户登录");
                    } else {
                        System.out.println("用户列表：" + users.toString());
                    }
                    break;
                case 2:
                    if (server.getChatRooms().isEmpty()) {
                        System.out.println("当前无聊天室");
                    } else {
                        System.out.println("聊天室列表：" + server.getChatRooms().toString());
                    }
                    break;
                case 3:
                    try {
                        serverThread.interrupt();
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
                    isExit = true;
                    break;
                default:
                    System.out.println("无效选项，请重新输入！");
                    break;
            }
        }
        scanner.close();
        System.out.print("服务器已成功退出！");
    }


}
