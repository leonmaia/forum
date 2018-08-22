package com.scale.forum.replies.persistence

import com.scale.forum.replies.domain.Reply
import com.scale.forum.topics.domain.TopicNotFound
import com.twitter.finagle.postgres.codec.ServerError
import com.twitter.finagle.postgres.{PostgresClient, Row}
import com.twitter.inject.Logging
import com.twitter.util.Future
import javax.inject.{Inject, Named, Singleton}

@Singleton
case class Replies @Inject()(@Named("forumdb") client: PostgresClient) extends Logging {
  def add(t: Reply): Future[Int] = {
    client.prepareAndExecute(
      s"""
         | INSERT INTO reply(email, body, topic_id)
         | VALUES ('${t.email}', '${t.body}', '${t.topicId}')
      """.stripMargin)
  }.handle {
    case e: ServerError =>
      logger.error(e.message)
      throw TopicNotFound(t.topicId)
  }

  def list(topicId: Int): Future[Seq[Reply]] = {
    client.prepareAndQuery(
      s"""
         | SELECT * FROM reply WHERE topic_id = $topicId
      """.stripMargin
    )(rowToReply)
  }

  private def rowToReply(row: Row): Reply = {
    val id = row.getOption[Int]("id")
    val email = row.get[String]("email")
    val body = row.get[String]("body")
    val topicId = row.get[Int]("topic_id")
    Reply(id,
      email,
      body,
      topicId)
  }
}

