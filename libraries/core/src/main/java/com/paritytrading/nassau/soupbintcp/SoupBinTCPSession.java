package com.paritytrading.nassau.soupbintcp;

import static com.paritytrading.foundation.ByteBuffers.*;
import static com.paritytrading.nassau.soupbintcp.SoupBinTCP.*;

import com.paritytrading.nassau.time.Clock;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;

/**
 * The base for both the client and server side of the protocol.
 */
public abstract class SoupBinTCPSession implements Closeable {

    private static final long RX_HEARTBEAT_TIMEOUT_MILLIS  = 15000;
    private static final long TX_HEARTBEAT_INTERVAL_MILLIS =  1000;

    private Clock clock;

    private SocketChannel channel;

    /*
     * This variable is written on data reception and read on session
     * keep-alive. These two functions can run on different threads
     * without locking.
     */
    private volatile long lastRxMillis;

    /*
     * This variable is written on data transmission and read on session
     * keep-alive. These two functions can run on different threads but
     * require locking.
     */
    private long lastTxMillis;

    private ByteBuffer rxBuffer;

    private ByteBuffer txHeader;

    private ByteBuffer[] txBuffers;

    private byte heartbeatPacketType;

    protected SoupBinTCPSession(Clock clock, SocketChannel channel, int maxPayloadLength,
            byte heartbeatPacketType) {
        this.clock   = clock;
        this.channel = channel;

        this.lastRxMillis = clock.currentTimeMillis();
        this.lastTxMillis = clock.currentTimeMillis();

        this.rxBuffer = ByteBuffer.allocateDirect(3 + Math.min(maxPayloadLength, MAX_PACKET_LENGTH - 1));

        this.txHeader = ByteBuffer.allocateDirect(3);

        this.txBuffers = new ByteBuffer[2];

        this.txHeader.order(ByteOrder.BIG_ENDIAN);

        this.txBuffers[0] = txHeader;

        this.heartbeatPacketType = heartbeatPacketType;
    }

    /**
     * Get the underlying socket channel.
     *
     * @return the underlying socket channel
     */
    public SocketChannel getChannel() {
        return channel;
    }

    /**
     * Receive data from the underlying socket channel. For each packet
     * received, invoke the corresponding listener if applicable.
     *
     * @return The number of bytes read, possibly zero, or <code>-1</code>
     *   if the channel has reached end-of-stream
     * @throws IOException if an I/O error occurs
     */
    public int receive() throws IOException {
        int bytes = channel.read(rxBuffer);

        if (bytes <= 0)
            return bytes;

        rxBuffer.flip();

        while (parse());

        rxBuffer.compact();

        receivedData();

        return bytes;
    }

    private boolean parse() throws IOException {
        if (rxBuffer.remaining() < 2)
            return false;

        rxBuffer.mark();

        rxBuffer.order(ByteOrder.BIG_ENDIAN);

        int packetLength = getUnsignedShort(rxBuffer);
        if (packetLength > rxBuffer.capacity() - 2)
            throw new SoupBinTCPException("Packet length exceeds buffer capacity");

        if (rxBuffer.remaining() < packetLength) {
            rxBuffer.reset();
            return false;
        }

        byte packetType = rxBuffer.get();

        int limit = rxBuffer.limit();

        rxBuffer.limit(rxBuffer.position() + packetLength - 1);

        packet(packetType, rxBuffer);

        rxBuffer.position(rxBuffer.limit());
        rxBuffer.limit(limit);

        return true;
    }

    /**
     * Keep the session alive.
     *
     * <p>If the heartbeat interval duration has passed since the last packet
     * was sent, send a Heartbeat packet. If the heartbeat timeout duration
     * has passed since the last packet was received, invoke the corresponding
     * method on the status listener.</p>
     *
     * @throws IOException if an I/O error occurs
     */
    public void keepAlive() throws IOException {
        long currentTimeMillis = clock.currentTimeMillis();

        if (currentTimeMillis - lastTxMillis > TX_HEARTBEAT_INTERVAL_MILLIS)
            send(heartbeatPacketType);

        if (currentTimeMillis - lastRxMillis > RX_HEARTBEAT_TIMEOUT_MILLIS)
            handleHeartbeatTimeout();
    }

    /**
     * Close the underlying socket channel.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void close() throws IOException {
        channel.close();
    }

    protected abstract void heartbeatTimeout() throws IOException;

    protected abstract void packet(byte packetType, ByteBuffer payload) throws IOException;

    protected void send(byte packetType) throws IOException {
        txHeader.clear();
        putUnsignedShort(txHeader, 1);
        txHeader.put(packetType);
        txHeader.flip();

        do {
            channel.write(txHeader);
        } while (txHeader.hasRemaining());

        sentData();
    }

    protected void send(byte packetType, ByteBuffer payload) throws IOException {
        int packetLength = payload.remaining() + 1;

        if (packetLength > MAX_PACKET_LENGTH)
            throw new SoupBinTCPException("Packet length exceeds maximum packet length");

        txHeader.clear();
        putUnsignedShort(txHeader, packetLength);
        txHeader.put(packetType);
        txHeader.flip();

        txBuffers[1] = payload;

        int remaining = txHeader.remaining() + payload.remaining();

        do {
            remaining -= channel.write(txBuffers);
        } while (remaining > 0);

        sentData();
    }

    protected void unexpectedPacketType(byte packetType) throws SoupBinTCPException {
        throw new SoupBinTCPException("Unexpected packet type: " + (char)packetType);
    }

    private void handleHeartbeatTimeout() throws IOException {
        heartbeatTimeout();

        receivedData();
    }

    private void receivedData() {
        lastRxMillis = clock.currentTimeMillis();
    }

    private void sentData() {
        lastTxMillis = clock.currentTimeMillis();
    }

}
