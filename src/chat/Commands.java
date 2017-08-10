package chat;

public enum Commands {
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
    ENTER_CHAT_ROOM,
    EXIT_CHAT_ROOM,
}
