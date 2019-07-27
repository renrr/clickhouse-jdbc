package com.cherry.jdbc.connect;

import com.cherry.jdbc.serializer.BinaryDeserializer;
import com.cherry.jdbc.serializer.BinarySerializer;
import com.cherry.jdbc.settings.ClickHouseConfig;
import com.cherry.jdbc.settings.ClickHouseDefines;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.sql.SQLException;

public class PhysicalConnection {
    private final Socket socket;
    private final SocketAddress address;
    private final BinarySerializer serializer;
    private final BinaryDeserializer deserializer;

    public PhysicalConnection(Socket socket, BinarySerializer serializer, BinaryDeserializer deserializer) {
        this.socket = socket;
        this.serializer = serializer;
        this.deserializer = deserializer;
        this.address = socket.getLocalSocketAddress();
    }

    public static PhysicalConnection openPhysicalConnection(ClickHouseConfig config) throws SQLException {

        try {
            SocketAddress endpoint = new InetSocketAddress(config.getAddress(), config.getPort());

            Socket socket = new Socket();
            socket.setTcpNoDelay(true);
            socket.setSendBufferSize(ClickHouseDefines.DEFAULT_BUFFER_SIZE);
            socket.setReceiveBufferSize(ClickHouseDefines.DEFAULT_BUFFER_SIZE);

            socket.connect(endpoint,config.getConnectTimeout());
            return  new PhysicalConnection(socket,new BinarySerializer(socket),new BinaryDeserializer(socket));
        } catch (IOException e) {
            throw  new SQLException(e.getMessage(),e);
        }
    }
}