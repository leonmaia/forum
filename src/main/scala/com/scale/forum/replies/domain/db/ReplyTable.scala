package com.scale.forum.replies.domain.db

import com.scale.forum.replies.domain.Reply
import com.scale.forum.topics.domain.Topic
import com.scale.forum.topics.domain.db.TopicTable
import slick.jdbc.PostgresProfile.api._
import slick.lifted.ForeignKeyQuery

class ReplyTable(tag: Tag) extends Table[Reply](tag, "reply") {

  def id: Rep[Option[Int]] = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)

  def email: Rep[String] = column[String]("email")

  def body: Rep[String] = column[String]("body")

  def topicId: Rep[Int] = column[Int]("topic_id")

  def topicIdKey: ForeignKeyQuery[TopicTable, Topic] =
    foreignKey("TOPIC_FK", topicId, TableQuery[TopicTable])(
      _.id.get,
      onUpdate = ForeignKeyAction.Restrict,
      onDelete = ForeignKeyAction.Restrict)

  override def * =
    (id, email, body, topicId) <> ((Reply.apply _).tupled, Reply.unapply)
}
