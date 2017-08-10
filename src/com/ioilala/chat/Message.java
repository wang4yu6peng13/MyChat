package com.ioilala.chat;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.ioilala.utils.SerializeHelper;

enum Commands{
	LOG_IN,
	LOG_OUT,
	QUERY_USERS,
	QUERY_ALL_CHAT_ROOMS,		
	QUERY_MY_CHAT_ROOMS,
	QUERY_ROOM_MEMBERS,
	HEART_BEAT,
	MSG_P2P,//个人对个人的消息
	MSG_P2R,//聊天室消息
	CREATE_CHAT_ROOM,
	JOIN_CHAT_ROOM,
	LEAVE_CHAT_ROOM,
	SET_USER_NAME; 
};
enum FieldType{
    USER_ID,
    USER_NAME,
    PASS_WD,
    PEER_ID,//单聊对象的ID
    ROOM_ID,//聊天室ID
    USER_LIST,//用户列表
    ROOM_LIST_ALL,//所有房间列表
    ROOM_LIST_ME,//我的聊天室列表
    ROOM_MEMBERS,//用户列表
    MSG_TXT,
    RESPONSE_STATUS,
    ENCODING;
};


class Message implements Serializable
{
	private static final long serialVersionUID = 1L;
	private Map<FieldType,String> fields=new HashMap<>();//TODO：泛型支持，任意消息类型，包括文本，图片，语音，视频，文件等
	private Commands command;
	public Message(Commands command)
	{
		this.command=command;
	}
	public Commands getCommand()
	{
		return this.command;
	}
	public Message set(FieldType key,String value)
	{
		if(key!=null&&value!=null)
		{
			fields.put(key,value);
		}
		return this;
	}
	public String get(FieldType key)
	{
		return fields.get(key);
	}
	
	public byte[] toBytes()
	{
		return SerializeHelper.serialize(this);
	}
	
	public ByteBuffer wrap()
	{
		byte[] frame=toBytes();
		return ByteBuffer.wrap(frame);
	}
}
