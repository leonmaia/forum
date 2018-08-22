package com.scale.forum.notifications

import akka.actor.ActorSystem
import com.redis.RedisClient
import com.scale.forum.notifications.domain.Notification
import com.scale.forum.notifications.persistence.Notifications
import com.twitter.inject.Logging
import com.twitter.util.{Future, FuturePool}
import javax.inject.{Inject, Named, Singleton}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

@Singleton
case class NotificationService @Inject()(@Named("redisdb") client: RedisClient, notifications: Notifications) extends Logging {
  val LIMIT = 1000
  val TOPIC_QUEUE_NAME = "notify"
  val system = ActorSystem("NotificationSystem")

  system.scheduler.schedule(0 seconds, 1 minute, notifyUsers)

  def notifyUsers: Runnable = () => {
    FuturePool.unboundedPool {
      while (isQueuePopulated()) {
        client.blpop(1, TOPIC_QUEUE_NAME).map(_._2).map {
          topicId =>
            notifications.list(topicId.toInt) flatMap { emails =>
              logMessage(emails)
            }
        }
      }
    }
  }

  def isQueuePopulated(name: String = TOPIC_QUEUE_NAME): Boolean = {
    client.lrange(name, 0, LIMIT).get.nonEmpty
  }

  def add(topicId: Int, email: String): Future[Unit] = {
    notifications.add(Notification(topicId, email)).flatMap {
      _ => Future(client.rpush(TOPIC_QUEUE_NAME, topicId)).unit
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