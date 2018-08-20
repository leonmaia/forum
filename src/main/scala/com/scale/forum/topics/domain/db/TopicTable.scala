package com.scale.forum.topics.domain.db

import com.scale.forum.topics.domain.Topic
import slick.jdbc.PostgresProfile.api._

class TopicTable(tag: Tag) extends Table[Topic](tag, "topic") {

  def id: Rep[Option[Int]] = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)

  def email: Rep[String] = column[String]("email")

  def title: Rep[String] = column[String]("title")

  def body: Rep[String] = column[String]("body")

  override def * =
    (id, email, title, body) <> ((Topic.apply _).tupled, Topic.unapply)
}
