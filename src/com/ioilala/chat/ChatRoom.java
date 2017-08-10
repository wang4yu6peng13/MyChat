package com.ioilala.chat;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.ioilala.utils.StringHelper;

final class ChatRoom {
    private String mRoomId = null;
    private Set<String> mUsers = Collections.synchronizedSet(new HashSet<String>());

    public ChatRoom(String id) throws IllegalArgumentException {
        if (StringHelper.isNullOrTrimEmpty(id))
            throw new IllegalArgumentException("room id不能为空");
        mRoomId = id;
    }

    private String getRoomId() {
        return new String(mRoomId);
    }

    public Set<String> getUsers() {
        return Collections.unmodifiableSet(mUsers);
    }

    public void addUser(String userId) {
        mUsers.add(userId);
    }

    public boolean hasUser(String userId) {
        return mUsers.contains(userId);
    }

    public void removeUser(String userId) {
        mUsers.remove(userId);
    }

    public int getUserCount() {
        return mUsers.size();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj instanceof ChatRoom) {
            ChatRoom room = (ChatRoom) obj;
            return room.mRoomId.equals(this.mRoomId);
        }
        return false;
    }

    ;

    @Override
    public int hashCode() {
        return mRoomId.hashCode();
    }

    ;

    @Override
    public String toString() {
        return new String(mRoomId);
    }
}
