package com.ioilala.chat;

import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

class UserEntity{
	private String mUserId;
	private String mPassWd;
	private SocketChannel mSocketChannel;
	private Set<String> mJoinedRooms=Collections.synchronizedSet(new HashSet<String>());
	
	public UserEntity(String userId,SocketChannel socketChannel) throws IllegalArgumentException
	{
		if(userId==null)
			throw new IllegalArgumentException("用户名不能为空");
		mUserId=userId;
		mSocketChannel=socketChannel;
	}
	
	public String getUserId() {
		return new String(mUserId);
	}
	
	public void joinRoom(String roomId)
	{
		mJoinedRooms.add(roomId);
	}
	
	public void leaveRoom(String roomId)
	{
		mJoinedRooms.remove(roomId);
	}

	public Set<String> getJoinedRooms() {
		return Collections.unmodifiableSet(mJoinedRooms);
	}
	
	public SocketChannel getSocketChannel() {
		return mSocketChannel;
	}
	
	@Override
	public boolean equals(Object user)
	{
		if(user instanceof UserEntity)
		{
			UserEntity entity=(UserEntity)user;
			return entity.mUserId.equals(mUserId);
		}
		return false;			
	}
	@Override
	public int hashCode()
	{
		return mUserId.hashCode();
	}
}