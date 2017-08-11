package model;

import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class User {
    private String username;
    private String password;
    private SocketChannel socketChannel;
    private Set<String> mJoinedRooms = Collections.synchronizedSet(new HashSet<String>());

    public User(String username, SocketChannel socketChannel) throws IllegalArgumentException {
        if (username == null)
            throw new IllegalArgumentException("用户名不能为空");
        this.username = username;
        this.socketChannel = socketChannel;
    }

    public String getUsername() {
        return this.username;
    }

    public void joinRoom(String roomName) {
        mJoinedRooms.add(roomName);
    }

    public void leaveRoom(String roomName) {
        mJoinedRooms.remove(roomName);
    }

    public Set<String> getJoinedRooms() {
        return Collections.unmodifiableSet(mJoinedRooms);
    }

    public int getJoinedRoomNum(){
        return getJoinedRooms().size();
    }

    public String getJoinedRoomName(){
        Set<String> set = getJoinedRooms();
        String roomName = null;
//        for(Iterator it = set.iterator(); it.hasNext();){
//            roomName = it.next().toString();
//        }
        for(String s : set){
            roomName = s;
        }
        return roomName;
    }

    public SocketChannel getSocketChannel() {
        return this.socketChannel;
    }

    @Override
    public boolean equals(Object user) {
        if (user instanceof User) {
            User entity = (User) user;
            return entity.username.equals(username);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }
}