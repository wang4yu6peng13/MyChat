package client;

import model.Commands;
import model.Message;
import model.MsgType;
import utils.SerializeHelper;
import utils.StringHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Iterator;

public final class ChatClient {
    private Selector selector = null;
    private SocketChannel socketChannel = null;
    private boolean isConnected = false;
    private boolean isLogin = false;
    public String username;

//    public void setStatus(int status) {
//        this.status = status;
//    }
//
//    public int getStatus() {
//        return status;
//    }

    /**
     * 连接到指定主机和端口
     *
     * @param host 主机地址
     * @param port 端口
     */
    public void connect(String host, int port) throws IOException {
        if (isConnected)
            return;//防止重复连接
        selector = Selector.open();
        InetSocketAddress remote = new InetSocketAddress(host, port);
        socketChannel = SocketChannel.open(remote);
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        isConnected = true;
        Thread clientThread = new ClientThread();
        clientThread.setDaemon(true);
        clientThread.start();
//		Scanner scanner=new Scanner(System.in);
//		while(scanner.hasNextLine())
//		{
//			String line=scanner.nextLine();
//			sc.write(charset.encode(line));//发送出去
//		}
//		scanner.close();
    }

    public boolean hasLogin() {
        return isLogin;
    }

    public void createChatRoom(String roomName, String roomInfo) {
        if (!isLogin || StringHelper.isNullOrTrimEmpty(roomName))
            return;
        Message message = new Message(Commands.CREATE_CHAT_ROOM);
        message.set(MsgType.USER_NAME, username);
        message.set(MsgType.ROOM_NAME, roomName);
        message.set(MsgType.ROOM_INFO, roomInfo);
        message.sendRawMessage(socketChannel, message);
    }

    public void enterChatRoom(String roomName) {
        if (!isLogin || StringHelper.isNullOrTrimEmpty(roomName))
            return;
        Message message = new Message(Commands.ENTER_CHAT_ROOM);
        message.set(MsgType.USER_NAME, username);
        message.set(MsgType.ROOM_NAME, roomName);
        message.sendRawMessage(socketChannel, message);
    }

    public void queryChatRoomMembers(String roomName) {
        if (!isLogin || StringHelper.isNullOrTrimEmpty(roomName))
            return;
        Message message = new Message(Commands.QUERY_ROOM_MEMBERS);
        message.set(MsgType.USER_NAME, username);
        message.set(MsgType.ROOM_NAME, roomName);
        message.sendRawMessage(socketChannel, message);
    }

    public void exitChatRoom(String roomName) {
        if (!isLogin || StringHelper.isNullOrTrimEmpty(roomName))
            return;
        Message message = new Message(Commands.EXIT_CHAT_ROOM);
        message.set(MsgType.USER_NAME, username);
        message.set(MsgType.ROOM_NAME, roomName);
        message.sendRawMessage(socketChannel, message);
    }

    public void exitChatRoom() {
        if (!isLogin)
            return;
        Message message = new Message(Commands.EXIT_CUR_CHAT_ROOM);
        message.set(MsgType.USER_NAME, username);
        message.sendRawMessage(socketChannel, message);
    }

    /**
     * 最底层的接口，给其他接口调用，用于发送最终的字节流
     */
//    private void sendRawMessage(Message message) {
//        if (socketChannel != null && message != null) {
//            try {
//                socketChannel.write(message.wrap());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

    public void login(String username, String passwd) {
        if (StringHelper.isNullOrTrimEmpty(username) || StringHelper.isNullOrTrimEmpty(passwd)) {
            throw new IllegalArgumentException("用户名或密码不能为空");
        }
        this.username = username;
        Message message = new Message(Commands.LOG_IN);
        message.set(MsgType.USER_NAME, username);
        message.set(MsgType.PASS_WD, passwd);
        message.sendRawMessage(socketChannel, message);
    }

    public void logout() throws IOException {
        if (!isLogin)
            return;
        Message message = new Message(Commands.LOG_OUT);
        message.set(MsgType.USER_NAME, username);
        message.sendRawMessage(socketChannel, message);
    }

    public void sendMsgToRoom(String roomName, String msg) {
        Message message = new Message(Commands.MSG_P2R);
        message.set(MsgType.USER_NAME, username);
        message.set(MsgType.ROOM_NAME, roomName);
        message.set(MsgType.MSG_TXT, msg);
        message.sendRawMessage(socketChannel, message);
    }

    public void sendMsgToRoom(String msg) {
        Message message = new Message(Commands.MSG_CUR_P2R);
        message.set(MsgType.USER_NAME, username);
        message.set(MsgType.MSG_TXT, msg);
        message.sendRawMessage(socketChannel, message);
    }

    public void sendMsgToUser(String toName, String msg) {
        Message message = new Message(Commands.MSG_P2P);
        message.set(MsgType.USER_NAME, username);
        message.set(MsgType.SINGLE_NAME, toName);
        message.set(MsgType.MSG_TXT, msg);
        message.sendRawMessage(socketChannel, message);
    }

    public void queryUserList() {
        Message message = new Message(Commands.QUERY_USERS);
        message.set(MsgType.USER_NAME, username);
        message.sendRawMessage(socketChannel, message);
    }

    public void queryAllRoomList() {
        Message message = new Message(Commands.QUERY_ALL_CHAT_ROOMS);
        message.set(MsgType.USER_NAME, username);
        message.sendRawMessage(socketChannel, message);
    }

    public void queryMyRoomList() {
        Message message = new Message(Commands.QUERY_MY_CHAT_ROOMS);
        message.set(MsgType.USER_NAME, username);
        message.sendRawMessage(socketChannel, message);
    }

    public void sendHongbao(String totalMoney, String count, String isRandom){
        Message message = new Message(Commands.SEND_HONGBAO);
        message.set(MsgType.USER_NAME, username);
        message.set(MsgType.HONGBAO_TOTAL, totalMoney);
        message.set(MsgType.HONGBAO_COUNT, count);
        message.set(MsgType.HONGBAO_RANDOM, isRandom);
        message.sendRawMessage(socketChannel, message);
    }

    public void qiangHongbao(String hongbaoId){
        Message message = new Message(Commands.QIANG_HONGBAO);
        message.set(MsgType.USER_NAME, username);
        message.set(MsgType.HONGBAO_ID, hongbaoId);
        message.sendRawMessage(socketChannel, message);
    }

    /**
     * 退出客户端
     */
    public void shutdown() {
        if (!isConnected)
            return;
        isConnected = false;
        try {
            socketChannel.close();
            selector.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 是否已经连接到服务器上
     */
    public boolean isConnected() {
        return isConnected;
    }

    private class ClientThread extends Thread {
        public void run() {
            try {
                while (selector.select() > 0) {
                    Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                    while (keyIterator.hasNext()) {
                        SelectionKey sk = keyIterator.next();
                        if (sk.isReadable()) {
                            SocketChannel sc = (SocketChannel) sk.channel();
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            ByteArrayOutputStream boStream = new ByteArrayOutputStream();
                            while (sc.read(buffer) > 0) {
                                buffer.flip();
                                boStream.write(Arrays.copyOfRange(buffer.array(), 0, buffer.limit()));
                                buffer.clear();
                            }
                            byte[] frame = boStream.toByteArray();
                            boStream.close();
                            //System.out.println(content);
                            if (frame.length > 0) {
                                Message msg = (Message) SerializeHelper.deSerialize(frame);
                                if (msg != null) {
                                    switch (msg.getCommand()) {
                                        case LOG_IN: {
                                            String result = msg.get(MsgType.RESPONSE_STATUS);
                                            if (result.equals("成功")) {
                                                System.out.println("恭喜你登录成功！");
                                                isLogin = true;
                                            } else {
                                                System.out.println(result);
                                            }
                                            break;
                                        }
                                        case LOG_OUT: {
                                            //退出懒得检查了
                                            isLogin = false;
                                            System.out.println("退出成功！");
                                            break;
                                        }
                                        case MSG_P2P: {
                                            String result = msg.get(MsgType.RESPONSE_STATUS);
                                            String fromName = msg.get(MsgType.USER_NAME);
                                            String toName = msg.get(MsgType.SINGLE_NAME);
                                            if (result.equals("成功")) {
                                                String txt = msg.get(MsgType.MSG_TXT);
                                                System.out.println("@" + fromName + " 对你说：" + txt);
                                            } else {
                                                System.out.println("发送给 @" + toName + " 的消息发送失败：" + result);
                                            }
                                            break;
                                        }
                                        case MSG_P2R: {
                                            String result = msg.get(MsgType.RESPONSE_STATUS);
                                            String fromName = msg.get(MsgType.USER_NAME);
                                            String roomName = msg.get(MsgType.ROOM_NAME);
                                            if (result.equals("成功")) {
                                                String txt = msg.get(MsgType.MSG_TXT);
                                                System.out.println("来自聊天室" + roomName + "的@" + fromName + " 说：" + txt);
                                            } else {
                                                System.out.println("发送到" + roomName + "消息发送失败：" + result);
                                            }
                                            break;
                                        }
                                        case SEND_HONGBAO:{
                                            String result = msg.get(MsgType.RESPONSE_STATUS);
                                            String fromName = msg.get(MsgType.USER_NAME);
                                            String roomName = msg.get(MsgType.ROOM_NAME);
                                            String hongbaoId = msg.get(MsgType.HONGBAO_ID);
                                            if (result.equals("成功")) {
                                                //String txt = msg.get(MsgType.MSG_TXT);
                                                System.out.println("来自聊天室" + roomName + "的@" + fromName + " 发了红包" + hongbaoId);
                                            } else {
                                                System.out.println("发送到" + roomName + "红包发送失败：" + result);
                                            }
                                            break;
                                        }
                                        case QIANG_HONGBAO:{
                                            String result = msg.get(MsgType.RESPONSE_STATUS);
                                            String nameQiang = msg.get(MsgType.USER_NAME);
                                            if (result.equals("成功")) {
                                                String nameSentHb = msg.get(MsgType.SINGLE_NAME);
                                                String txt = msg.get(MsgType.MSG_TXT);
                                                String list = msg.get(MsgType.HONGBAO_LIST);
                                                System.out.println("@" + nameQiang + " 抢了 @" + nameSentHb + " 的红包，金额为：￥"
                                                        + StringHelper.moneyDivideBy100(Integer.valueOf(txt)));
                                                if (list != null)
                                                    System.out.print(list);
                                            } else {
                                                System.out.println("@" + nameQiang + " 抢红包失败：" + result);
                                            }
                                            break;
                                        }
                                        case CREATE_CHAT_ROOM: {
                                            String result = msg.get(MsgType.RESPONSE_STATUS);
                                            if (result.equals("成功")) {
                                                System.out.println("创建聊天室成功");
                                                //status = ClientStatus.HALL.getValue();
                                                //queryAllRoomList();
                                                //setStatus(0);
                                            } else {
                                                System.out.println("创建聊天室失败:" + result);
                                            }
                                            break;
                                        }
                                        case ENTER_CHAT_ROOM: {
                                            String result = msg.get(MsgType.RESPONSE_STATUS);
                                            if (result.equals("成功")) {
                                                System.out.println("加入聊天室成功");
                                                //status = ClientStatus.ROOM.getValue();
                                                //setStatus(1);
                                                //System.out.println(" " + status);
                                                //System.out.println(" " +getStatus());
                                            } else {
                                                System.out.println("加入聊天室失败:" + result);
                                            }
                                            break;
                                        }
                                        case EXIT_CHAT_ROOM: {
                                            String result = msg.get(MsgType.RESPONSE_STATUS);
                                            if (result.equals("成功")) {
                                                System.out.println("离开聊天室成功");
                                            } else {
                                                System.out.println("离开聊天室失败:" + result);
                                            }
                                            break;
                                        }

                                        case QUERY_ALL_CHAT_ROOMS: {
                                            String roomList = msg.get(MsgType.ROOM_LIST_ALL);
                                            if (roomList.length() > 0) {
                                                System.out.println("聊天室列表为：" + roomList);
                                            } else {
                                                System.out.println("当前无聊天室");
                                            }
                                            break;
                                        }
                                        case QUERY_ROOM_MEMBERS: {
                                            String roomMembers = msg.get(MsgType.ROOM_MEMBERS);
                                            String result = msg.get(MsgType.RESPONSE_STATUS);
                                            if (roomMembers.length() > 0) {
                                                System.out.println("聊天室成员列表为：" + roomMembers);
                                            } else {
                                                System.out.println("请求失败：" + result);
                                            }
                                            break;
                                        }
                                        case QUERY_USERS: {
                                            String userList = msg.get(MsgType.USER_LIST);
                                            //System.out.println(users);
                                            if (userList.length() > 0) {
                                                //String[] userArray=userList.split(",");
                                                System.out.println("用户列表:" + userList);
                                            }
                                            break;
                                        }

                                        default:
                                            System.out.println("未识别的服务器指令：" + msg.getCommand().toString());
                                            break;
                                    }
                                } else {
                                    System.out.println("反序列化失败！");
                                }
                            }
                            sk.interestOps(SelectionKey.OP_READ);
                        }
                        keyIterator.remove();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


