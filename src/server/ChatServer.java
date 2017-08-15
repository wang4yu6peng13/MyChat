package server;

import model.*;
import utils.SerializeHelper;
import utils.StringHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

public final class ChatServer implements Runnable {
    private Selector selector = null;//用于注册所有连接到服务器的SocketChannel对象
    //保存所有聊天室的Map
    private Map<String, ChatRoom> rooms = Collections.synchronizedMap(new HashMap<String, ChatRoom>());//聊天室
    //保存所有用户的Map
    private Map<String, User> users = Collections.synchronizedMap(new HashMap<String, User>());
    //private Bind<String, SocketChannel> usersocketBindMap=Collections.synchronizedMap(new BindMap<String, SocketChannel>());

    //红包
    private Map<String, Hongbao> hongbaos = Collections.synchronizedMap(new HashMap<String, Hongbao>());

    private int port;
    private String host;

    private final static String LOCAL_HOST = "127.0.0.1";
    private final static int DEFAULT_PORT = 8909;

    public ChatServer() {
        this(LOCAL_HOST, DEFAULT_PORT);
    }

    public ChatServer(int port) {
        this(LOCAL_HOST, port);
    }

    public ChatServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * 返回当前用户集合
     */
    public Set<String> getUserList() {
        return users.keySet();
    }

    /**
     * 返回当前聊天室集合
     */
    public Set<String> getChatRooms() {
        return rooms.keySet();
    }

    /**
     * 返回指定聊天室的用户成员列表
     */
    public Set<User> getUsersOfChatRoom(String roomName) {
        //TODO
        return null;
    }

    /**
     * 最底层的接口，给其他接口调用
     */
//    private void sendRawMessage(SocketChannel sc, Message message) throws IOException {
//        if (sc != null && message != null) {
//            sc.write(message.wrap());
//        }
//    }

    public void run() {
        try {
            selector = Selector.open();
            ServerSocketChannel server = ServerSocketChannel.open();
            InetSocketAddress isa = new InetSocketAddress(host, port);
            server.bind(isa);//绑定指定端口
            server.configureBlocking(false);
            server.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("服务器在" + host + ":" + port + "启动成功");
            while (selector.select() > 0) {
                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey sk = keyIterator.next();
                    if (sk.isAcceptable()) {
                        SocketChannel sc = server.accept();//开始接收客户端连接
                        if (sc != null) {
                            sc.configureBlocking(false);
                            sc.register(selector, SelectionKey.OP_READ);
                            sk.interestOps(SelectionKey.OP_ACCEPT);
                        }
                    }
                    if (sk.isReadable()) {        //有数据
                        SocketChannel sc = (SocketChannel) sk.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        ByteArrayOutputStream boStream = new ByteArrayOutputStream();
                        try {
                            int len;
                            while ((len = sc.read(buffer)) > 0) {//TODO:性能问题
                                //System.out.println("长度:"+len);
                                buffer.flip();
                                boStream.write(Arrays.copyOfRange(buffer.array(), 0, buffer.limit()));
                            }
                            //System.out.println("长度:"+len);
                            byte[] frame = boStream.toByteArray();
                            boStream.close();
                            if (frame.length > 0) {
                                Message msg = (Message) SerializeHelper.deSerialize(frame);
                                if (msg != null) {
                                    String username = msg.get(MsgType.USER_NAME);
                                    switch (msg.getCommand()) {
                                        case LOG_IN: {
                                            System.out.println("用户" + username + "请求登录...");
                                            Message message = new Message(Commands.LOG_IN);
                                            //TODO:检查用户名密码，暂时没有注册功能，就只检测用户名是否重复
                                            if (!users.containsKey(username)) {
                                                message.set(MsgType.RESPONSE_STATUS, "成功");
                                                System.out.println("用户" + username + "登录成功");
                                                User user = new User(username, sc);
                                                users.put(username, user);
                                            } else {
                                                message.set(MsgType.RESPONSE_STATUS, "该帐号已经登录");
                                            }
                                            //发送登录结果
                                            message.sendRawMessage(sc, message);
                                            break;
                                        }
                                        case LOG_OUT: {
                                            System.out.println("用户" + username + "请求退出...");
                                            Message message = new Message(Commands.LOG_OUT);
                                            if (users.containsKey(username)) {
                                                message.set(MsgType.RESPONSE_STATUS, "成功");
                                                users.remove(username);
                                                System.out.println("用户" + username + "退出成功");
                                            } else {
                                                message.set(MsgType.RESPONSE_STATUS, "该帐号已经退出");
                                            }
                                            message.sendRawMessage(sc, message);
                                            break;
                                        }
                                        case MSG_P2P: {
                                            String toName = msg.get(MsgType.SINGLE_NAME);
                                            String txt = msg.get(MsgType.MSG_TXT);
                                            System.out.println("用户" + username + "发送消息给用户" + toName);
                                            Message message = new Message(Commands.MSG_P2P);
                                            if (users.containsKey(username)
                                                    && users.containsKey(toName)
                                                    && !StringHelper.isNullOrTrimEmpty(txt)) {
                                                SocketChannel scPeer = users.get(toName).getSocketChannel();
                                                message.set(MsgType.USER_NAME, username);
                                                message.set(MsgType.SINGLE_NAME, toName);
                                                message.set(MsgType.MSG_TXT, txt);
                                                message.set(MsgType.RESPONSE_STATUS, "成功");
                                                message.sendRawMessage(scPeer, message);
                                            } else {
                                                message.set(MsgType.USER_NAME, username);
                                                message.set(MsgType.SINGLE_NAME, toName);
                                                message.set(MsgType.RESPONSE_STATUS, "消息发送失败");
                                                message.sendRawMessage(sc, message);
                                            }
                                            break;
                                        }
                                        case MSG_P2R: {
                                            String roomName = msg.get(MsgType.ROOM_NAME);
                                            String txt = msg.get(MsgType.MSG_TXT);
                                            System.out.println("用户" + username + "发送消息到聊天室" + roomName);
                                            Message message = new Message(Commands.MSG_P2R);
                                            if (users.containsKey(username) && rooms.containsKey(roomName)
                                                    && rooms.get(roomName).hasUser(username)
                                                    && !StringHelper.isNullOrTrimEmpty(txt)) {
                                                message.set(MsgType.USER_NAME, username);
                                                message.set(MsgType.ROOM_NAME, roomName);
                                                message.set(MsgType.MSG_TXT, txt);
                                                message.set(MsgType.RESPONSE_STATUS, "成功");
                                                for (String user : rooms.get(roomName).getUsers()) {
                                                    if (!user.equals(username)) {
                                                        // 发送给其他人
                                                        SocketChannel socketChannel = users.get(user).getSocketChannel();
                                                        message.sendRawMessage(socketChannel, message);
                                                    }
                                                }
                                            } else {
                                                message.set(MsgType.USER_NAME, username);
                                                message.set(MsgType.ROOM_NAME, roomName);
                                                message.set(MsgType.RESPONSE_STATUS, "消息发送失败");
                                                message.sendRawMessage(sc, message);
                                            }
                                            break;
                                        }
                                        case MSG_CUR_P2R: {
                                            User curUser = users.get(username);
                                            String roomName = curUser.getJoinedRoomName();

                                            String txt = msg.get(MsgType.MSG_TXT);
                                            System.out.println("用户" + username + "发送消息到聊天室" + roomName);
                                            Message message = new Message(Commands.MSG_P2R);
                                            if (users.containsKey(username) && rooms.containsKey(roomName)
                                                    && rooms.get(roomName).hasUser(username)
                                                    && !StringHelper.isNullOrTrimEmpty(txt)) {
                                                message.set(MsgType.USER_NAME, username);
                                                message.set(MsgType.ROOM_NAME, roomName);
                                                message.set(MsgType.MSG_TXT, txt);
                                                message.set(MsgType.RESPONSE_STATUS, "成功");
                                                for (String user : rooms.get(roomName).getUsers()) {
                                                    if (!user.equals(username)) {
                                                        // 发送给其他人
                                                        SocketChannel socketChannel = users.get(user).getSocketChannel();
                                                        message.sendRawMessage(socketChannel, message);
                                                    }
                                                }
                                            } else {
                                                message.set(MsgType.USER_NAME, username);
                                                message.set(MsgType.ROOM_NAME, roomName);
                                                message.set(MsgType.RESPONSE_STATUS, "消息发送失败");
                                                message.sendRawMessage(sc, message);
                                            }
                                            break;
                                        }
                                        case CREATE_CHAT_ROOM: {
                                            System.out.println("用户" + username + "请求创建聊天室");
                                            String roomName = msg.get(MsgType.ROOM_NAME);
                                            String roomInfo = msg.get(MsgType.ROOM_INFO);
                                            Message message = new Message(Commands.CREATE_CHAT_ROOM);
                                            if (!StringHelper.isNullOrTrimEmpty(roomName)) {
                                                if (!rooms.containsKey(roomName)) {
                                                    ChatRoom room = new ChatRoom(roomName, roomInfo);
                                                    //room.addUser(username);
                                                    rooms.put(roomName, room);
                                                    //User user = users.get(username);
                                                    //if (user != null)
                                                    //    user.joinRoom(roomName);
                                                    message.set(MsgType.RESPONSE_STATUS, "成功");
                                                } else {
                                                    message.set(MsgType.RESPONSE_STATUS, "创建失败，已存在同名聊天室");
                                                }
                                            } else {//返回错误消息
                                                message.set(MsgType.RESPONSE_STATUS, "创建失败，聊天室名称不能为空");
                                            }
                                            message.sendRawMessage(sc, message);
                                            break;
                                        }
                                        case ENTER_CHAT_ROOM: {
                                            String roomName = msg.get(MsgType.ROOM_NAME);
                                            System.out.println("用户" + username + "请求加入聊天室" + roomName);
                                            Message message = new Message(Commands.ENTER_CHAT_ROOM);
                                            if (rooms.containsKey(roomName)) {  // 聊天室存在
                                                ChatRoom room = rooms.get(roomName);
                                                if (!room.hasUser(username)) {
                                                    User user = users.get(username);
                                                    int roomNum = user.getJoinedRoomNum();
                                                    if(roomNum == 0){
                                                        room.addUser(username);
                                                        if (user != null)
                                                            user.joinRoom(roomName);
                                                        message.set(MsgType.RESPONSE_STATUS, "成功");
                                                    }else{
                                                        message.set(MsgType.RESPONSE_STATUS, "您已经在聊天室" + user.getJoinedRooms().toString());
                                                    }

                                                } else {
                                                    message.set(MsgType.RESPONSE_STATUS, "您已经在本聊天室中");
                                                }
                                            } else {
                                                message.set(MsgType.RESPONSE_STATUS, "不存在该聊天室");
                                            }
                                            message.sendRawMessage(sc, message);
                                            break;
                                        }
                                        case EXIT_CHAT_ROOM: {
                                            String roomName = msg.get(MsgType.ROOM_NAME);
                                            System.out.println("用户" + username + "请求离开聊天室" + roomName);
                                            Message message = new Message(Commands.EXIT_CHAT_ROOM);
                                            if (rooms.containsKey(roomName)) {
                                                User user = users.get(username);
                                                if (user != null)
                                                    user.leaveRoom(roomName);
                                                ChatRoom room = rooms.get(roomName);
                                                room.removeUser(username);
                                                message.set(MsgType.RESPONSE_STATUS, "成功");
                                            } else {
                                                message.set(MsgType.RESPONSE_STATUS, "不存在该聊天室");
                                            }
                                            message.sendRawMessage(sc, message);
                                            break;
                                        }
                                        case EXIT_CUR_CHAT_ROOM: {
                                            User user = users.get(username);
                                            String roomName = user.getJoinedRoomName();
                                            System.out.println("用户" + username + "请求离开聊天室" + roomName);
                                            Message message = new Message(Commands.EXIT_CHAT_ROOM);
                                            if (rooms.containsKey(roomName)) {
                                                if (user != null)
                                                    user.leaveRoom(roomName);
                                                ChatRoom room = rooms.get(roomName);
                                                room.removeUser(username);
                                                message.set(MsgType.RESPONSE_STATUS, "成功");
                                            } else {
                                                message.set(MsgType.RESPONSE_STATUS, "不存在该聊天室");
                                            }
                                            message.sendRawMessage(sc, message);
                                            break;
                                        }
                                        case QUERY_ALL_CHAT_ROOMS: {
                                            System.out.println("用户" + username + "请求查询聊天室列表");
                                            Message message = new Message(Commands.QUERY_ALL_CHAT_ROOMS);
                                            Set<String> rooms = getChatRooms();
                                            if (rooms.isEmpty()) {
                                                message.set(MsgType.ROOM_LIST_ALL, "");
                                            } else {
                                                String roomsStr = rooms.toString();
                                                message.set(MsgType.ROOM_LIST_ALL, roomsStr.substring(1, roomsStr.length() - 1));
                                            }
                                            message.sendRawMessage(sc, message);
                                            System.out.println("已发送聊天室列表给" + username);
                                            break;
                                        }
//                                        case QUERY_MY_CHAT_ROOMS: {
//                                            System.out.println("用户" + username + "请求查询自己加入的所有聊天室列表");
//                                            Message message = new Message(Commands.QUERY_MY_CHAT_ROOMS);
//                                            Set<String> rooms = users.get(username).getJoinedRooms();
//                                            if (rooms.isEmpty()) {
//                                                message.set(MsgType.ROOM_LIST_ALL, "");
//                                            } else {
//                                                String roomsStr = rooms.toString();
//                                                message.set(MsgType.ROOM_LIST_ALL, roomsStr.substring(1, roomsStr.length() - 1));
//                                            }
//                                            message.sendRawMessage(sc, message);
//                                            System.out.println("已发送聊天室列表给" + username);
//                                            break;
//                                        }
                                        case QUERY_ROOM_MEMBERS: {
                                            String roomName = msg.get(MsgType.ROOM_NAME);
                                            System.out.println("用户" + username + "请求查询聊天室" + roomName + "的成员信息");
                                            Message message = new Message(Commands.QUERY_ROOM_MEMBERS);
                                            if (rooms.containsKey(roomName)) {
                                                Set<String> users = rooms.get(roomName).getUsers();
                                                message.set(MsgType.RESPONSE_STATUS, "成功");
                                                if (users.isEmpty()) {
                                                    message.set(MsgType.ROOM_MEMBERS, "");
                                                } else {
                                                    String usersStr = users.toString();
                                                    message.set(MsgType.ROOM_MEMBERS, usersStr.substring(1, usersStr.length() - 1));
                                                }
                                            } else {
                                                message.set(MsgType.RESPONSE_STATUS, "不存在该聊天室");
                                            }
                                            message.sendRawMessage(sc, message);
                                            System.out.println("已发送聊天室" + roomName + "成员列表给" + username);
                                            break;
                                        }
                                        case QUERY_USERS: {
                                            System.out.println("用户" + username + "请求获取用户列表");
                                            Message message = new Message(Commands.QUERY_USERS);
                                            Set<String> users = getUserList();
                                            if (users.isEmpty()) {
                                                message.set(MsgType.USER_LIST, "");
                                            } else {
                                                String usersStr = users.toString();
                                                message.set(MsgType.USER_LIST, usersStr.substring(1, usersStr.length() - 1));
                                            }
                                            message.sendRawMessage(sc, message);
                                            System.out.println("已发送用户列表给" + username);
                                            break;
                                        }
                                        case SEND_HONGBAO: {
                                            User curUser = users.get(username);
                                            String roomName = curUser.getJoinedRoomName();

                                            String totalMoney = msg.get(MsgType.HONGBAO_TOTAL);
                                            String count = msg.get(MsgType.HONGBAO_COUNT);
                                            String isRandom = msg.get(MsgType.HONGBAO_RANDOM);

                                            Message message = new Message(Commands.SEND_HONGBAO);
                                            if (users.containsKey(username) && rooms.containsKey(roomName)
                                                    && rooms.get(roomName).hasUser(username)) {
                                                int iTotalMoney = Integer.valueOf(totalMoney);
                                                int iCount = Integer.valueOf(count);

                                                if (iTotalMoney < 1 || iCount < 1 || iTotalMoney < iCount) {
                                                    message.set(MsgType.USER_NAME, username);
                                                    message.set(MsgType.ROOM_NAME, roomName);
                                                    message.set(MsgType.RESPONSE_STATUS, "发送失败，金额不够个数");
                                                    message.sendRawMessage(sc, message);
                                                } else {
                                                    Hongbao hongbao = new Hongbao(iTotalMoney, iCount);
                                                    String hongbaoId = hongbao.getId();

                                                    hongbao.setTotalMoney(Integer.valueOf(totalMoney));
                                                    hongbao.setCount(Integer.valueOf(count));
                                                    hongbao.setRandom(!isRandom.equals("0"));
                                                    hongbao.setRoomName(roomName);
                                                    hongbao.setSentUserName(username);

                                                    hongbaos.put(hongbaoId, hongbao);

                                                    System.out.println("用户" + username + "在聊天室" + roomName + "发了红包" + hongbaoId);
                                                    message.set(MsgType.USER_NAME, username);
                                                    message.set(MsgType.ROOM_NAME, roomName);
                                                    message.set(MsgType.HONGBAO_ID, hongbaoId);
                                                    message.set(MsgType.RESPONSE_STATUS, "成功");

                                                    ChatRoom room = rooms.get(roomName);
                                                    room.addHongbao(hongbaoId);
                                                    for (String user : rooms.get(roomName).getUsers()) {
                                                        //if (!user.equals(username)) {
                                                        // 发送给其他人
                                                        SocketChannel socketChannel = users.get(user).getSocketChannel();
                                                        message.sendRawMessage(socketChannel, message);
                                                        //}
                                                    }
                                                }

                                            } else {
                                                message.set(MsgType.USER_NAME, username);
                                                message.set(MsgType.ROOM_NAME, roomName);
                                                message.set(MsgType.RESPONSE_STATUS, "发送失败");
                                                message.sendRawMessage(sc, message);
                                            }
                                            break;
                                        }
                                        case QIANG_HONGBAO: {
                                            User curUser = users.get(username); // qiang
                                            String roomName = curUser.getJoinedRoomName();

                                            String hongbaoId = msg.get(MsgType.HONGBAO_ID);

                                            Message message = new Message(Commands.QIANG_HONGBAO);
                                            message.set(MsgType.USER_NAME, username);
                                            if (users.containsKey(username) && rooms.containsKey(roomName)
                                                    && rooms.get(roomName).hasUser(username)
                                                    && hongbaos.containsKey(hongbaoId)
                                                    && rooms.get(roomName).hasHongbao(hongbaoId)) {
                                                Hongbao hongbao = hongbaos.get(hongbaoId);
                                                String nameSentHb = hongbao.getSentUserName();

                                                if (hongbao.getHbUsrMap().containsKey(username)) {
                                                    message.set(MsgType.RESPONSE_STATUS, "您已抢过该红包");
                                                    message.sendRawMessage(sc, message);
                                                } else if (hongbao.getLeftMoney() <= 0 || hongbao.getCount() == hongbao.getHbUsrMap().size()) {
                                                    message.set(MsgType.RESPONSE_STATUS, "该红包被抢完了");
                                                    message.sendRawMessage(sc, message);
                                                } else {
                                                    boolean last = false;
                                                    if (hongbao.getLeftCount() == 1) {
                                                        last = true;
                                                    }
                                                    hongbao.qiang(username);
                                                    message.set(MsgType.SINGLE_NAME, nameSentHb);
                                                    message.set(MsgType.MSG_TXT, hongbao.getHbUsrMap().get(username).toString());
                                                    message.set(MsgType.RESPONSE_STATUS, "成功");
                                                    if (last) {
                                                        message.set(MsgType.HONGBAO_LIST, hongbao.getInfoOrMax());
                                                    }
                                                    for (String user : rooms.get(roomName).getUsers()) {
                                                        SocketChannel socketChannel = users.get(user).getSocketChannel();
                                                        message.sendRawMessage(socketChannel, message);
                                                    }
                                                }

                                            } else {
                                                message.set(MsgType.RESPONSE_STATUS, "抢红包失败");
                                                message.sendRawMessage(sc, message);
                                            }

                                            break;
                                        }
                                        default:
                                            System.out.println("未识别的指令：" + msg.getCommand().toString());
                                            break;
                                    }
                                } else {
                                    System.out.println("反序列化失败！");
                                }
                            }
                            if (len == -1) {
                                System.out.println("客户端断开");
                                closeClient(sk, sc);
                            } else {
                                sk.interestOps(SelectionKey.OP_READ);
                            }
                            //如果捕获到该sk对应的Channel出现的问题，即表明该Channel对应的Client出现了问题，
                            //所以从Selector中取消sk的注册
                        } catch (IOException e) {
                            closeClient(sk, sc);
                        }
                    }
                    keyIterator.remove();
                }
            }
        } catch (AlreadyBoundException e) {
            System.out.println("启动服务器失败，端口已被占用");
        } catch (IllegalArgumentException e) {
            System.out.print(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (selector != null) {
                try {
                    selector.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void closeClient(SelectionKey sk, SocketChannel sc) throws IOException {
        sk.cancel();
        if (sc != null) {
            //if (users.containsValue(sc)) {
                for (String key : users.keySet()) {
                    User user = users.get(key);
                    if (user.getSocketChannel() == sc) {
                        users.remove(key);//从用户列表中移除用户
                        for (String room : user.getJoinedRooms()) {
                            rooms.get(room).removeUser(user.getUsername());//从聊天室中移除用户
                        }
                    }
                }
            //}
            sc.close();
        }
    }

}
