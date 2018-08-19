package com.scale.forum.unit.topics.persistence

import java.util.UUID

import com.scale.forum.unit.topics.domain.Topic
import com.twitter.finagle.postgres.PostgresClient
import com.twitter.inject.Logging
import com.twitter.util.Future
import javax.inject.{Inject, Named, Singleton}

@Singleton
case class Topics @Inject()(@Named("forumdb") client: PostgresClient) extends Logging {
  def add(t: Topic): Future[Int] = {
    client.prepareAndExecute(
      s"""
         | INSERT INTO topic(id, email, title, body, timestamp)
         | VALUES ( '${t.id}', '${t.email}', '${t.title}', '${t.body}', NOW())
      """.stripMargin)
  }

  def list(): Future[Seq[Topic]] = {
    client.prepareAndQuery(s"SELECT * FROM topic ORDER BY timestamp DESC")(row =>
      Topic(
        row.get[UUID]("id"),
        row.get[String]("email"),
        row.get[String]("title"),
        row.get[String]("body"))
    )
  }
}
