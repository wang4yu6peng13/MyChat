package chat;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import utils.SerializeHelper;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    private Map<FieldType, String> fields = new HashMap<>();//TODO：泛型支持，任意消息类型，包括文本，图片，语音，视频，文件等
    private Commands command;

    public Message(Commands command) {
        this.command = command;
    }

    public Commands getCommand() {
        return this.command;
    }

    public Message set(FieldType key, String value) {
        if (key != null && value != null) {
            fields.put(key, value);
        }
        return this;
    }

    public String get(FieldType key) {
        return fields.get(key);
    }

    public byte[] toBytes() {
        return SerializeHelper.serialize(this);
    }

    public ByteBuffer wrap() {
        byte[] frame = toBytes();
        return ByteBuffer.wrap(frame);
    }
}
