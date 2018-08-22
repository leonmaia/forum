package com.scale.forum.topics.persistence

import com.scale.forum.notifications.domain.Notification
import com.scale.forum.topics.domain.Topic
import com.twitter.finagle.postgres.{PostgresClient, Row}
import com.twitter.inject.Logging
import com.twitter.util.Future
import javax.inject.{Inject, Named, Singleton}

@Singleton
case class Topics @Inject()(@Named("forumdb") client: PostgresClient) extends Logging {

  def add(t: Topic): Future[Topic] = {
    client.prepareAndQuery(
      s"""
         | INSERT INTO topic(email, title, body)
         | VALUES ('${t.email}', '${t.title}', '${t.body}') RETURNING *
      """.stripMargin)(rowToTopic).map(_.head)
  }


  def list(): Future[Seq[Topic]] = {
    client.prepareAndQuery(s"SELECT * FROM topic ORDER BY id DESC")(rowToTopic)
  }

  private def rowToTopic(row: Row): Topic = {
    val id = row.getOption[Int]("id")
    val email = row.get[String]("email")
    val title = row.get[String]("title")
    val body = row.get[String]("body")
    Topic(id,
      email,
      title,
      body)
  }
}
