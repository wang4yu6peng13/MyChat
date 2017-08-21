package model;

import utils.SerializeHelper;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    private Map<MsgType, String> fields = new HashMap<>();
    private Command command;

    public Message(Command command) {
        this.command = command;
    }

    public Command getCommand() {
        return this.command;
    }

    public void set(MsgType key, String value) {
        if (key != null && value != null) {
            fields.put(key, value);
        }
    }

    public String get(MsgType key) {
        return fields.get(key);
    }

    public byte[] toBytes() {
        return SerializeHelper.serialize(this);
    }

    public ByteBuffer wrap() {
        byte[] frame = toBytes();
        return ByteBuffer.wrap(frame);
    }

    public void sendRawMessage(SocketChannel sc, Message message) {
        if (sc != null && message != null) {
            try {
                sc.write(message.wrap());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
