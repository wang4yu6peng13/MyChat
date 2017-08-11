package model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import utils.StringHelper;

public final class ChatRoom {
    private String roomName = null;
    private String roomInfo = null;
    private Set<String> users = Collections.synchronizedSet(new HashSet<String>());

    public ChatRoom(String roomName) throws IllegalArgumentException {
        this(roomName, "roominfo");
    }

    public ChatRoom(String roomName, String roomInfo) throws IllegalArgumentException {
        if (StringHelper.isNullOrTrimEmpty(roomName))
            throw new IllegalArgumentException("room name不能为空");
        this.roomName = roomName;
        this.roomInfo = roomInfo;
    }

    private String getRoomName() {
        return this.roomName;
    }

    private String getRoomInfo(){
        return this.roomInfo;
    }

    public Set<String> getUsers() {
        return Collections.unmodifiableSet(users);
    }

    public void addUser(String username) {
        users.add(username);
    }

    public boolean hasUser(String username) {
        return users.contains(username);
    }

    public void removeUser(String username) {
        users.remove(username);
    }

    public int getUserCount() {
        return users.size();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj instanceof ChatRoom) {
            ChatRoom room = (ChatRoom) obj;
            return room.roomName.equals(this.roomName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return roomName.hashCode();
    }

    @Override
    public String toString() {
        return roomName;
    }
}
