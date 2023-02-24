package com.example.skysys.connetion;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDP_server extends Thread{
    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[256];
    private int port = 14550;


    public void sendData(byte[] buffer, InetAddress address)
    {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, this.port);
        String received = new String(packet.getData(), 0, packet.getLength());

        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DatagramPacket receiveData()
    {
        running = true;

        DatagramPacket packet = new DatagramPacket(buf, buf.length);

        while (running) {
            try {
                socket.receive(packet);
            } catch (IOException e) {
                running = false;
            }
        }
        socket.close();
        return packet;
    }
}
