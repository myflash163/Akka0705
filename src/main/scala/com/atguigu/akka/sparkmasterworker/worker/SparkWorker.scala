package com.atguigu.akka.sparkmasterworker.worker

import java.util.UUID

import akka.actor.{Actor, ActorRef, ActorSelection, ActorSystem, Props}
import com.atguigu.akka.sparkmasterworker.common.{HeartBeat, RegisterWorkerInfo, SendHeartBeat}
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._

class SparkWorker(masterHost: String, masterPort: Int,masterName:String) extends Actor {
  var masterProxy: ActorSelection = _
  val id = UUID.randomUUID().toString

  override def preStart(): Unit = {
    println("preStart启动")
    masterProxy = context.actorSelection(s"akka.tcp://SparkMaster@${masterHost}:${masterPort}/user/$masterName")
    println("masterProxy=" + masterProxy)
  }

  override def receive: Receive = {
    case "start" => {
      println("worker 启动了")
      masterProxy ! RegisterWorkerInfo(id, 16, 16 * 1024)
    }
    case RegisterWorkerInfo => {
      println("worker id =" + id + " 注册成功")
      import context.dispatcher
      context.system.scheduler.schedule(0 millis, 3000 millis, self, SendHeartBeat)
    }
    case SendHeartBeat => {
      println("worker=" + id + " 发送心跳")
      masterProxy ! HeartBeat(id)
    }


  }
}

object SparkWorker extends App {
  if(args.length != 6){
    println("请输入参数 workerHost workerPort workerName masterHost masterPort masterName")
    sys.exit()
  }
  val workerHost = args(0)
  val workerPort = args(1)
  val workerName = args(2)
  val masterHost = args(3)
  val masterPort = args(4)
  val masterName = args(5)
  val config = ConfigFactory.parseString(
    s"""
       |akka.actor.provider="akka.remote.RemoteActorRefProvider"
       |akka.remote.netty.tcp.hostname=$workerHost
       |akka.remote.netty.tcp.port=$workerPort
       |""".stripMargin)
  val sparkWorkerSystem: ActorSystem = ActorSystem("SparkWorker1", config)

  val customerActorRef: ActorRef = sparkWorkerSystem.actorOf(Props(new SparkWorker(masterHost, masterPort.toInt,masterName)), workerName)
  customerActorRef ! "start"

}