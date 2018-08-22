package com.scale.forum.notifications.persistence

import com.scale.forum.notifications.domain.Notification
import com.scale.forum.topics.domain.TopicNotFound
import com.twitter.finagle.postgres.codec.ServerError
import com.twitter.finagle.postgres.{PostgresClient, Row}
import com.twitter.inject.Logging
import com.twitter.util.Future
import javax.inject.{Inject, Named, Singleton}

@Singleton
case class Notifications @Inject()(@Named("forumdb") client: PostgresClient) extends Logging {

  def add(n: Notification): Future[Int] = {
    client.prepareAndExecute(
      s"""
         | INSERT INTO notification(topic_id, email)
         | VALUES ('${n.topicId}', '${n.email}')
      """.stripMargin
    )
  }.handle {
    case e: ServerError =>
      logger.error(e.message)
      throw TopicNotFound(n.topicId)
  }

  def list(topicId: Int): Future[Seq[Notification]] = {
    client.prepareAndQuery(
      s"""
         | SELECT DISTINCT email, topic_id FROM notification WHERE topic_id = $topicId
      """.stripMargin
    )(rowToNotification)
  }

  private def rowToNotification(row: Row): Notification = {
    val id = row.get[Int]("topic_id")
    val email = row.get[String]("email")
    Notification(id, email)
  }
}
