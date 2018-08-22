package com.scale.forum.notifications.domain.db


import com.scale.forum.notifications.domain.Notification
import com.scale.forum.topics.domain.Topic
import com.scale.forum.topics.domain.db.TopicTable
import slick.jdbc.PostgresProfile.api._
import slick.lifted.ForeignKeyQuery

case class NotificationTable(tag: Tag) extends Table[Notification](tag, "notification") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def topicId = column[Int]("topic_id")

  def email = column[String]("email")

  def topicIdKey: ForeignKeyQuery[TopicTable, Topic] =
    foreignKey("TOPIC_FK", topicId, TableQuery[TopicTable])(
      _.id.get,
      onUpdate = ForeignKeyAction.Cascade,
      onDelete = ForeignKeyAction.Cascade)

  override def * =
    (topicId, email) <> ((Notification.apply _).tupled, Notification.unapply)

}
