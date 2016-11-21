package org.trex.falcon.rpc.codc;

import org.trex.falcon.rpc.model.Message;

import java.io.*;

public class CodecUtils {

    public static Message decode(byte[] bytes) {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            return (Message) objectInputStream.readObject();
        } catch (Exception e) {
            return null;
        }
    }

    public static byte[] encode(Message message) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(message);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            return null;
        }
    }

}
