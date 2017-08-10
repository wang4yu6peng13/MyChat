package chat;

import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class UserEntity {
    private String username;
    private String password;
    private SocketChannel socketChannel;
    private Set<String> mJoinedRooms = Collections.synchronizedSet(new HashSet<String>());

    public UserEntity(String username, SocketChannel socketChannel) throws IllegalArgumentException {
        if (username == null)
            throw new IllegalArgumentException("用户名不能为空");
        this.username = username;
        this.socketChannel = socketChannel;
    }

    public String getUsername() {
        return this.username;
    }

    public void joinRoom(String roomId) {
        mJoinedRooms.add(roomId);
    }

    public void leaveRoom(String roomId) {
        mJoinedRooms.remove(roomId);
    }

    public Set<String> getJoinedRooms() {
        return Collections.unmodifiableSet(mJoinedRooms);
    }

    public SocketChannel getSocketChannel() {
        return this.socketChannel;
    }

    @Override
    public boolean equals(Object user) {
        if (user instanceof UserEntity) {
            UserEntity entity = (UserEntity) user;
            return entity.username.equals(username);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }
}