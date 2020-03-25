package com.atguigu.akka.sparkmasterworker.master

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.atguigu.akka.sparkmasterworker.common._
import com.typesafe.config.ConfigFactory

import scala.collection.mutable
import scala.concurrent.duration._

class SparkMaster extends Actor {
  val workers = mutable.Map[String, WorkerInfo]()

  override def receive: Receive = {
    case "start" => {
      println("master 服务器启动。。。。")
      self ! StartTimeOutWorker
    }
    case StartTimeOutWorker => {
      import context.dispatcher
      context.system.scheduler.schedule(0 millis, 9000 millis, self, RemoveTimeOutWorker)
    }
    case RemoveTimeOutWorker => {
      val workerInfos = workers.values
      val nowTime = System.currentTimeMillis()
      workerInfos.filter(workerInfo => (nowTime - workerInfo.lastHeartBeat) > 6000)
        .foreach(workerInfo => workers.remove(workerInfo.id))
      println("当前有" + workers.size + "个worker存活的")
    }
    case RegisterWorkerInfo(id, cpu, ram) => {
      if (!workers.contains(id)) {
        val workerInfo = new WorkerInfo(id, cpu, ram)
        workers += ((id, workerInfo))
        sender() ! RegisterWorkerInfo
      }
    }
    case HeartBeat(id) => {
      val workerInfo = workers(id)
      workerInfo.lastHeartBeat = System.currentTimeMillis()
      println("master更新了 " + id)
    }
  }
}

object SparkMaster {
  def main(args: Array[String]): Unit = {
    if(args.length != 3){
      println("请输入参数 host port sparkMasterActor名字")
      sys.exit()
    }
    val host = args(0)
    val port = args(1)
    val name = args(2)
    val config = ConfigFactory.parseString(
      s"""
         |akka.actor.provider="akka.remote.RemoteActorRefProvider"
         |akka.remote.netty.tcp.hostname=$host
         |akka.remote.netty.tcp.port=$port
         |""".stripMargin)
    val sparkMasterSystem: ActorSystem = ActorSystem("SparkMaster", config)
    val sparkMasterRef: ActorRef = sparkMasterSystem.actorOf(Props[SparkMaster], s"$name")
    //启动
    sparkMasterRef ! "start"
  }
}
