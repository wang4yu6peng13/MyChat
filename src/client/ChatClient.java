package client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Scanner;

import chat.Commands;
import chat.FieldType;
import chat.Message;
import utils.SerializeHelper;
import utils.StringHelper;

public final class ChatClient {
    private Selector selector = null;
    private SocketChannel socketChannel = null;
    private boolean isConnected = false;
    private boolean isLogin = false;
    public String username;

    /**
     * 连接到指定主机和端口
     *
     * @param host 主机地址
     * @param port 端口
     * @throws IOException
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

    public void createChatRoom(String roomId) {
        if (!isLogin || StringHelper.isNullOrTrimEmpty(roomId))
            return;
        Message message = new Message(Commands.CREATE_CHAT_ROOM);
        message.set(FieldType.USER_ID, username);
        message.set(FieldType.ROOM_ID, roomId);
        sendRawMessage(message);
    }

    public void joinChatRoom(String roomId) {
        if (!isLogin || StringHelper.isNullOrTrimEmpty(roomId))
            return;
        Message message = new Message(Commands.JOIN_CHAT_ROOM);
        message.set(FieldType.USER_ID, username);
        message.set(FieldType.ROOM_ID, roomId);
        sendRawMessage(message);
    }

    public void queryChatRoomMembers(String roomId) {
        if (!isLogin || StringHelper.isNullOrTrimEmpty(roomId))
            return;
        Message message = new Message(Commands.QUERY_ROOM_MEMBERS);
        message.set(FieldType.USER_ID, username);
        message.set(FieldType.ROOM_ID, roomId);
        sendRawMessage(message);
    }

    public void leaveChatRoom(String roomId) {
        if (!isLogin || StringHelper.isNullOrTrimEmpty(roomId))
            return;
        Message message = new Message(Commands.LEAVE_CHAT_ROOM);
        message.set(FieldType.USER_ID, username);
        message.set(FieldType.ROOM_ID, roomId);
        sendRawMessage(message);
    }


    /**
     * 最底层的接口，给其他接口调用，用于发送最终的字节流
     *
     * @param message
     */
    private void sendRawMessage(Message message) {
        if (socketChannel != null && message != null) {
            try {
                socketChannel.write(message.wrap());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void login(String username, String passwd) {
        if (StringHelper.isNullOrTrimEmpty(username) || StringHelper.isNullOrTrimEmpty(passwd)) {
            throw new IllegalArgumentException("用户名或密码不能为空");
        }
        this.username = username;
        Message message = new Message(Commands.LOG_IN);
        message.set(FieldType.USER_ID, username);
        message.set(FieldType.PASS_WD, passwd);
        sendRawMessage(message);
    }


    public void setUserName(String username) {
        if (username == null)
            return;
        Message message = new Message(Commands.SET_USER_NAME);
        message.set(FieldType.USER_ID, username);
        message.set(FieldType.USER_NAME, username);
        sendRawMessage(message);
    }

    public void logout() throws IOException {
        if (!isLogin)
            return;
        Message message = new Message(Commands.LOG_OUT);
        message.set(FieldType.USER_ID, username);
        sendRawMessage(message);
    }


    public void sentMsgToRoom(String roomid, String msg) {
        Message message = new Message(Commands.MSG_P2R);
        message.set(FieldType.USER_ID, username);
        message.set(FieldType.ROOM_ID, roomid);
        message.set(FieldType.MSG_TXT, msg);
        sendRawMessage(message);
    }

    public void sendMsgToUser(String username, String msg) {
        Message message = new Message(Commands.MSG_P2P);
        message.set(FieldType.USER_ID, username);
        message.set(FieldType.PEER_ID, username);
        message.set(FieldType.MSG_TXT, msg);
        sendRawMessage(message);
    }

    public void queryUserList() {
        Message message = new Message(Commands.QUERY_USERS);
        message.set(FieldType.USER_ID, username);
        sendRawMessage(message);
    }

    public void queryAllRoomList() {
        Message message = new Message(Commands.QUERY_ALL_CHAT_ROOMS);
        message.set(FieldType.USER_ID, username);
        sendRawMessage(message);
    }

    public void queryMyRoomList() {
        Message message = new Message(Commands.QUERY_MY_CHAT_ROOMS);
        message.set(FieldType.USER_ID, username);
        sendRawMessage(message);
    }

    /**
     * 退出客户端
     */
    public void shutdown() {
        //TODO:
        if (!isConnected)
            return;
        isConnected = false;
        try {
            socketChannel.close();
            selector.close();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
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
                                            String result = msg.get(FieldType.RESPONSE_STATUS);
                                            if (result.equals("成功")) {
                                                System.out.println("恭喜你登录成功！");
                                                isLogin = true;
                                            } else {
                                                System.out.print(result);
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
                                            String result = msg.get(FieldType.RESPONSE_STATUS);
                                            String fromId = msg.get(FieldType.USER_ID);
                                            String toId = msg.get(FieldType.PEER_ID);
                                            if (result.equals("成功")) {
                                                String txt = msg.get(FieldType.MSG_TXT);
                                                System.out.println(fromId + "对你说：" + txt);
                                            } else {
                                                System.out.println("发送给" + toId + "的消息发送失败：" + result);
                                            }
                                            break;
                                        }
                                        case MSG_P2R: {
                                            String result = msg.get(FieldType.RESPONSE_STATUS);
                                            String fromId = msg.get(FieldType.USER_ID);
                                            String roomId = msg.get(FieldType.ROOM_ID);
                                            if (result.equals("成功")) {
                                                String txt = msg.get(FieldType.MSG_TXT);
                                                System.out.println("来自聊天室" + roomId + "的" + fromId + "说：" + txt);
                                            } else {
                                                System.out.println("发送到" + roomId + "消息发送失败：" + result);
                                            }
                                            break;
                                        }
                                        case CREATE_CHAT_ROOM: {
                                            String result = msg.get(FieldType.RESPONSE_STATUS);
                                            if (result.equals("成功")) {
                                                System.out.println("创建聊天室成功");
                                            } else {
                                                System.out.println("创建聊天室失败:" + result);
                                            }
                                            break;
                                        }
                                        case JOIN_CHAT_ROOM: {
                                            String result = msg.get(FieldType.RESPONSE_STATUS);
                                            if (result.equals("成功")) {
                                                System.out.println("加入聊天室成功");
                                            } else {
                                                System.out.println("加入聊天室失败:" + result);
                                            }
                                            break;
                                        }
                                        case LEAVE_CHAT_ROOM: {
                                            String result = msg.get(FieldType.RESPONSE_STATUS);
                                            if (result.equals("成功")) {
                                                System.out.println("离开聊天室成功");
                                            } else {
                                                System.out.println("离开聊天室失败:" + result);
                                            }
                                            break;
                                        }

                                        case QUERY_ALL_CHAT_ROOMS: {
                                            String roomList = msg.get(FieldType.ROOM_LIST_ALL);
                                            if (roomList.length() > 0) {
                                                System.out.println("聊天室列表为：" + roomList);
                                            } else {
                                                System.out.println("当前无聊天室");
                                            }
                                            break;
                                        }
                                        case QUERY_ROOM_MEMBERS: {
                                            String roomMembers = msg.get(FieldType.ROOM_MEMBERS);
                                            String result = msg.get(FieldType.RESPONSE_STATUS);
                                            if (roomMembers.length() > 0) {
                                                System.out.println("聊天室成员列表为：" + roomMembers);
                                            } else {
                                                System.out.println("请求失败：" + result);
                                            }
                                            break;
                                        }
                                        case QUERY_USERS: {
                                            String userList = msg.get(FieldType.USER_LIST);
                                            //System.out.println(users);
                                            if (userList.length() > 0) {
                                                //String[] userArray=userList.split(",");
                                                System.out.println("用户列表:" + userList);
                                            }
                                            break;
                                        }
                                        case SET_USER_NAME:
                                            break;
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


