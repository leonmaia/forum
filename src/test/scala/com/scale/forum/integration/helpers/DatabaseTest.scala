package com.scale.forum.integration.helpers

import com.google.inject.Key
import com.google.inject.name.Names
import com.scale.forum.provider.DatabaseProvider
import com.twitter.finagle.postgres.PostgresClient
import com.twitter.finagle.stats.NullStatsReceiver
import com.twitter.finagle.stats.StatsReceiver
import com.twitter.inject.Injector
import com.twitter.inject.IntegrationTestMixin
import com.twitter.inject.Logging
import com.twitter.inject.Mockito
import com.twitter.inject.app.TestInjector
import com.twitter.util.Await
import org.scalatest.FunSpec


object Postgres {
  val container = PostgreSQL("forum")
}

object InjectorProvider {
  import Postgres.container

  lazy val dbConfig = Map(
    "forumdb.host"           -> container.host,
    "forumdb.port"           -> container.port.toString,
    "forumdb.user"           -> container.username,
    "forumdb.password"       -> container.password
  )

  val value: Injector =
    TestInjector(flags = dbConfig, modules = Seq(DatabaseProvider))
      .bind[StatsReceiver](NullStatsReceiver)
      .create
}

trait DatabaseTest extends FunSpec with IntegrationTestMixin with Logging with Mockito {

  override lazy val injector: Injector = InjectorProvider.value

  protected lazy val forumDB: PostgresClient =
    injector.instance(Key.get(classOf[PostgresClient], Names.named("forumdb")))

  def setupDatabase(): Unit = {
    Await.result(createSchema().handle {
      case e =>
    })
  }

  def resetDatabase(): Unit = {
    Await.result(deleteAllData().handle {
      case e =>
    })
  }

  private def createSchema() = {
    forumDB.prepareAndExecute("""
                                     |CREATE TABLE topic (
                                     |    id uuid,
                                     |    email VARCHAR(100),
                                     |    title VARCHAR(100),
                                     |    body VARCHAR(200),
                                     |    timestamp timestamp
                                     |);
                                   """.stripMargin)
  }

  private def deleteAllData() = {
    forumDB.prepareAndExecute("DELETE FROM topic;")
  }
}
