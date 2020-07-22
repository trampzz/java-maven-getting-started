package com.okteto.helloworld;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

@Slf4j
@SpringBootApplication
public class Application implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	static ArrayList<TransPortData> mThreads = new ArrayList<TransPortData>();//线程管理map

	@Override
	public void run(String... args) throws Exception {
		try {
			if(args == null || args.length<3){
				log.error("输出参数不能为空，分别是 本地监听端口、远程IP、远程端口");
				return;
			}


			//获取本地监听端口、远程IP和远程端口
			//int localPort = Integer.parseInt(args[0].trim());
			//String remoteIp = args[1].trim();
			//int remotePort = Integer.parseInt(args[2].trim());
			int localPort = 444;
			String remoteIp = "220.249.11.142";
			int remotePort = 8080;

					//启动本地监听端口
			ServerSocket serverSocket = new ServerSocket(localPort);
			log.info("localPort="+localPort + ";remoteIp=" + remoteIp +
					";remotePort="+remotePort+";启动本地监听端口" + localPort + "成功！");
			while(true){
				Socket clientSocket = null;
				Socket remoteServerSocket = null;
				try {
					//获取客户端连接
					clientSocket = serverSocket.accept();

					log.error("accept one client");
					//建立远程连接
					remoteServerSocket = new Socket(remoteIp ,remotePort);

					log.error("create remoteip and port success");
					//启动数据转换接口
					TransPortData transPortData1 = (new TransPortData(clientSocket ,remoteServerSocket ,"1"));
					TransPortData transPortData2 = (new TransPortData(remoteServerSocket ,clientSocket,"2"));
					transPortData1.start();
					transPortData2.start();
					mThreads.add(transPortData1);
					mThreads.add(transPortData2);
					log.info("创建新的进程："+transPortData1.getName()+",threadId是："+transPortData1.getId());
					log.info("创建新的进程："+transPortData2.getName()+",threadId是："+transPortData2.getId());
				} catch (Exception ex) {
					log.error("",ex);
				}
				//建立连接远程
			}
		} catch (Exception e) {
			log.error("",e);
		}
	}

	public static void killThread() {
		for(TransPortData tmpThread: mThreads) {
			log.info("正在停止进程："+tmpThread.getId());
			tmpThread.stopThread();
		}
	}

	public static ArrayList<TransPortData> getmThreads() {
		return mThreads;
	}
}
