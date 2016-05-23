package com.zapata.yerson.tcpexposicion;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {




    private EditText msg_send = null;
    private Button btn_send = null;
    private TextView msg_received=null;

    private String ip="192.168.1.11";
    Integer port=8081;

    //Definiciones para el socket
    Handler handler;
    TcpCliente tcpCliente;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        msg_send = (EditText) findViewById(R.id.msg);
        btn_send = (Button) findViewById(R.id.btn_enviar);
        msg_received = (TextView) findViewById(R.id.msg_receive);

        init(); //Para iniciar la escucha de los mensajes


//-----------------Conecta con el servidor
                tcpCliente = new TcpCliente(handler, ip, port);
                new Thread(tcpCliente).start();

//-----------------

        //Para enviar mensajes
        btn_send.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View arg0) {

                try
                {
                    Message msg = new Message();
                    msg.what = 0x852;
                    msg.obj = msg_send.getText().toString();
                    tcpCliente.sendHandler.sendMessage(msg);
                    msg_send.setText("");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }});
    }




    private void init()
    {
        handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                if(msg.what == 0x123)
                {
                    String comodin = msg_received.getText().toString();
                    msg_received.setText(comodin+msg.obj.toString()+"\n");
                }
            }
        };
    }

}