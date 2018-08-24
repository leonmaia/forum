package com.scale.forum.topics.persistence

import com.scale.forum.http.pagination.Pagination
import com.scale.forum.topics.domain.Topic
import com.twitter.finagle.postgres.{PostgresClient, Row}
import com.twitter.inject.Logging
import com.twitter.util.Future
import javax.inject.{Inject, Named, Singleton}

@Singleton
case class Topics @Inject()(@Named("forumdb") client: PostgresClient) extends Logging {
  def get(id: Int): Future[Topic] = {
    client.prepareAndQuery(s"SELECT * FROM topic WHERE id = $id LIMIT 1")(rowToTopic).map(_.head)
  }

  def add(t: Topic): Future[Topic] = {
    client.prepareAndQuery(
      s"""
         | INSERT INTO topic(email, title, body)
         | VALUES ('${t.email}', '${t.title}', '${t.body}') RETURNING *
      """.stripMargin)(rowToTopic).map(_.head)
  }


  def list(pagination: Pagination = Pagination()): Future[(Int, Seq[Topic])] = {
    for {
      entries <- client.prepareAndQuery(s"SELECT * FROM topic ORDER BY id DESC LIMIT ${pagination.limit} offset ${pagination.drops}")(rowToTopic)
      count <- client.prepareAndQuery("SELECT COUNT(id)::int4 AS count from topic")(row => row.get[Int]("count")).map(_.head)
    } yield (count, entries)
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
