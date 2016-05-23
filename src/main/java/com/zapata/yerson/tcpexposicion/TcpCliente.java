package com.zapata.yerson.tcpexposicion;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Created by Le Yerson on 15/05/2016.
 */
public class TcpCliente implements Runnable {

    private Socket socket;
    private String ip;
    private int port;
    private Handler receiveHandler;
    public Handler sendHandler;
    private InputStream inputStream;
    private OutputStream outputStream;
    public boolean isConnect = false;

    public TcpCliente(Handler handler, String ip, Integer port) {
        // TODO Auto-generated constructor stub
        this.receiveHandler = handler;
        this.ip = ip;
        this.port = port;

    }


    public void run()
    {
        try
        {
            socket = new Socket(ip, port);
            isConnect = socket.isConnected();
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            //Para monitoriar si se recibio mensajes del server
            new Thread()
            {
                @Override
                public void run()
                {
                    byte[] buffer = new byte[1024]; //Se crea un buffer

                    try
                    {
                        while(socket.isConnected())
                        {
                            int readSize = inputStream.read(buffer);
                            //Si el servidor es detenido
                            if(readSize == -1)
                            {
                                inputStream.close();
                                outputStream.close();
                            }

                            //Actualiza el mensaje en el TexView
                            try{
                                Message msg = new Message();
                                msg.what = 0x123;
                                msg.obj = new String(buffer, 0, readSize);
                                receiveHandler.sendMessage(msg);
                            }catch(StringIndexOutOfBoundsException e){
                                socket.close();
                            }
                        }
                    }
                    catch(IOException e)
                    {  e.printStackTrace(); }
                }
            }.start();

            //Para enviar mensajes al server
            Looper.prepare();
            sendHandler = new Handler()
            {
                @Override
                public void handleMessage(Message msg)
                {
                    if (msg.what == 0x852)
                    {
                        try
                        {
                            outputStream.write((msg.obj.toString() + "\r\n").getBytes()); //\r\n retorno de carro y cambio de linea
                            outputStream.flush();                                         //Vacia el buffer de salida
                        }
                        catch (Exception e)
                        {

                            e.printStackTrace();
                        }
                    }
                }
            };
            Looper.loop();

        } catch (SocketTimeoutException e)
        {
            // TODO Auto-generated catch block

            e.printStackTrace();
        }catch (UnknownHostException e)
        {
            // TODO Auto-generated catch block

            e.printStackTrace();
        } catch (IOException e)
        {
            // TODO Auto-generated catch block

            e.printStackTrace();
        }
    }
}