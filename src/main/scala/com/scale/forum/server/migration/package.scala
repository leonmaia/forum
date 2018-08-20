package com.scale.forum.server

import com.scale.forum.replies.domain.db.ReplyTable
import com.scale.forum.topics.domain.db.TopicTable
import slick.lifted.TableQuery
import slick.migration.api.{Migration, PostgresDialect, TableMigration}

package object migration {

  implicit val dialect: PostgresDialect = new PostgresDialect

  val topicTable = TableQuery[TopicTable]

  val replyTable = TableQuery[ReplyTable]

  val createTopicTable: Migration = TableMigration(topicTable).create
    .addColumns(_.id, _.email, _.title, _.body)

  val createReplyTable: Migration = TableMigration(replyTable).create
    .addColumns(_.id, _.email, _.body, _.topicId)
    .addForeignKeys(_.topicIdKey)

  val migrations: Seq[Migration] = Seq(
    createTopicTable,
    createReplyTable
  )
}
