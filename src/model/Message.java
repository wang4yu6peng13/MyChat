package model;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import utils.SerializeHelper;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    private Map<FieldType, String> fields = new HashMap<>();
    private Commands command;

    public Message(Commands command) {
        this.command = command;
    }

    public Commands getCommand() {
        return this.command;
    }

    public void set(FieldType key, String value) {
        if (key != null && value != null) {
            fields.put(key, value);
        }
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