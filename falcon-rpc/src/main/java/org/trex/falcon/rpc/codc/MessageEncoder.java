package org.trex.falcon.rpc.codc;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.trex.falcon.rpc.model.Message;

public class MessageEncoder extends MessageToByteEncoder<Message> {

    //包头标识
    public static int HEADER_FLAG = -21415431;

    @Override
    protected void encode(ChannelHandlerContext ctx, Message message, ByteBuf buffer) throws Exception {
        //包头
        buffer.writeInt(HEADER_FLAG);
        //序列化
        byte[] data = CodecUtils.encode(message);
        if (data.length <= 0) {
            buffer.writeInt(data.length);
        } else {
            buffer.writeInt(data.length);
            buffer.writeBytes(data);
        }
    }
}
