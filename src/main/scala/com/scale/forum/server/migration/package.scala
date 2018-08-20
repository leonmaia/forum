package com.scale.forum.server

import com.scale.forum.topics.domain.db.TopicTable
import slick.lifted.TableQuery
import slick.migration.api.{Migration, PostgresDialect, TableMigration}

package object migration {

  implicit val dialect: PostgresDialect = new PostgresDialect

  val topicTable = TableQuery[TopicTable]

  val createTopicTable: Migration = TableMigration(topicTable).create
    .addColumns(_.id, _.email, _.title, _.body)

  val migrations: Seq[Migration] = Seq(
    createTopicTable
  )
}
