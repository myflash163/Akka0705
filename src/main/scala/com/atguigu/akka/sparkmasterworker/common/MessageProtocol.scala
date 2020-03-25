package com.atguigu.akka.sparkmasterworker.common

case class RegisterWorkerInfo(id: String, cpu: Int, ram: Int)
class WorkerInfo(val id: String, cpu: Int, ram: Int){
  var lastHeartBeat : Long = System.currentTimeMillis()
}
case object RegisteredWorkerInfo

case object SendHeartBeat
case class HeartBeat(id:String)

case object StartTimeOutWorker
case object RemoveTimeOutWorker
