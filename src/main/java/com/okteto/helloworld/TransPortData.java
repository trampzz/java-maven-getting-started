package com.okteto.helloworld;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class TransPortData  extends Thread {
    Socket getDataSocket;
    Socket putDataSocket;
    private AtomicBoolean running = new AtomicBoolean(false);


    public void stopThread() {
        running.set(false);
    }

    String type;

    public TransPortData(Socket getDataSocket , Socket putDataSocket ,String type){
        this.getDataSocket = getDataSocket;
        this.putDataSocket = putDataSocket;
        this.type = type;
    }

    public Boolean isClose(){
        if ("2".equals(type)) {//往回传数据
            return isServerClose(putDataSocket);
        } else if ("1".equals(type)) {
            return isServerClose(getDataSocket);
        }
        return false;
    }

    public void run(){
        running.set(true);
        try {
            while(running.get()){

                InputStream in = getDataSocket.getInputStream() ;
                OutputStream out = putDataSocket.getOutputStream() ;
                //读入数据
                byte[] data = new byte[1024];
                int readlen = in.read(data);

                //如果没有数据，则暂停
                if(readlen<=0){
                    Thread.sleep(300);
                    continue;
                }


                out.write(data ,0,readlen);
                out.flush();
                /*if ("2".equals(type)) {//往回传数据
                    if (isServerClose(putDataSocket)) {
                        running.set(false);
                    }
                } else if ("1".equals(type)) {
                    isServerClose(getDataSocket);
                    running.set(false);
                }*/
            }
        } catch (Exception e) {
            log.error("type:"+type,e);
        }
        finally{
            //关闭socket
            try {
                if(putDataSocket != null){
                    log.info("正在关闭type："+type+"的putDataSocket端口");
                    putDataSocket.close();
                }
            } catch (Exception exx) {
            }

            try {
                if(getDataSocket != null){
                    log.info("正在关闭type："+type+"的getDataSocket端口");
                    getDataSocket.close();
                }
            } catch (Exception exx) {
            }
            log.info("正在关闭thread："+this.getName()+"，id："+this.getId());
        }
    }

    /**
     * 判断是否断开连接，断开返回true,没有返回false
     * @param socket
     * @return
     */
    public Boolean isServerClose(Socket socket){
        try{
            socket.sendUrgentData(0xFF);//发送1个字节的紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常通信
            return false;
        }catch(Exception se){
            return true;
        }
    }
}
