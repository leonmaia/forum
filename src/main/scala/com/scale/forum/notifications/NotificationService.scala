package com.scale.forum.notifications

import java.nio.charset.Charset

import akka.actor.ActorSystem
import com.scale.forum.notifications.domain.Notification
import com.scale.forum.notifications.persistence.Notifications
import com.twitter.finagle.redis.Client
import com.twitter.inject.Logging
import com.twitter.io.Buf
import com.twitter.util.{Await, Future, FuturePool}
import javax.inject.{Inject, Named, Singleton}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

@Singleton
case class NotificationService @Inject()(@Named("redisdb") client: Client, notifications: Notifications) extends Logging {
  val LIMIT = 1000l
  val TOPIC_QUEUE_NAME = "notify"
  val system = ActorSystem("NotificationSystem")

  system.scheduler.schedule(0 seconds, 1 minute, notifyUsers)

  def notifyUsers: Runnable = () => {
    FuturePool.unboundedPool {
      while (Await.result(isQueuePopulated())) {
        client.rPop(Buf.Utf8(TOPIC_QUEUE_NAME)) map { entry =>
          val topicId = Buf.decodeString(entry.get, Charset.defaultCharset()).toInt
          notifications.list(topicId) flatMap { emails =>
            logMessage(emails)
          }
        }
      }
    }
  }

  def isQueuePopulated(name: String = TOPIC_QUEUE_NAME): Future[Boolean]= {
    client.lRange(Buf.Utf8(name), 0l, LIMIT).map(_.nonEmpty)
  }

  def add(topicId: Int, email: String): Future[Unit] = {
    notifications.add(Notification(topicId, email)).flatMap {
      _ => Future(client.rPush(Buf.Utf8(TOPIC_QUEUE_NAME), List(Buf.Utf8(topicId.toString)))).unit
    }
  }.handle {
    case e: Exception =>
      logger.error(s"error while adding notification for topic: $topicId", e)
  }

  /**
    * For the sake of this exercise we are only logging the email instead of sending.
    */
  private def logMessage(items: Seq[Notification]): Future[Unit] = {
    Future(items.foreach(item => logger.info(s"Topic updated ${item.topicId} - Email sent to: ${item.email}")))
  }
}