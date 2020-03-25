package com.atguigu.akka.yellowchicken.client

import akka.actor.{Actor, ActorRef, ActorSelection, ActorSystem, Props}
import com.atguigu.akka.yellowchicken.common.{ClientMessage, ServerMessage}
import com.typesafe.config.ConfigFactory

import scala.io.StdIn

class CustomerActor(serverHost: String, serverPort: Int) extends Actor {
  var serverActorRef: ActorSelection = _

  override def preStart(): Unit = {
    println("preStart()执行")
    serverActorRef = context.actorSelection(s"akka.tcp://Server@${serverHost}:${serverPort}/user/YelloChickenServer")
    println("serverActorRef=" + serverActorRef)

  }

  override def receive: Receive = {
    case "start" => println("start 客户端运行，可以咨询问题")
    case mes: String => {
      serverActorRef ! ClientMessage(mes)
    }
    case ServerMessage(mes) => {
      println(s"收到小黄鸡客服 的回复：$mes")
    }
  }
}

object CustomerActor extends App {
  val (clientHost, clientPort, serverHost, serverPort) = ("127.0.0.1", 9989, "127.0.0.1", 9999)
  val config = ConfigFactory.parseString(
    s"""
       |akka.actor.provider="akka.remote.RemoteActorRefProvider"
       |akka.remote.netty.tcp.hostname=$clientHost
       |akka.remote.netty.tcp.port=$clientPort
       |""".stripMargin)
  val clientActorSystem: ActorSystem = ActorSystem("client", config)

  val customerActorRef: ActorRef = clientActorSystem.actorOf(Props(new CustomerActor(serverHost, serverPort)), "CustomerActor")
  customerActorRef ! "start"
  while (true) {
    println("请输入要咨询的问题")
    val mes = StdIn.readLine()
    customerActorRef ! mes
  }
}