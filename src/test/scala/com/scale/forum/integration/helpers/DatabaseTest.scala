package com.scale.forum.integration.helpers

import com.google.inject.Key
import com.google.inject.name.Names
import com.scale.forum.provider.DatabaseProvider
import com.scale.forum.server.migration.MigrationHandler
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
    "forumdb.host" -> container.host,
    "forumdb.port" -> container.port.toString,
    "forumdb.user" -> container.username,
    "forumdb.password" -> container.password
  )

  val value: Injector =
    TestInjector(flags = dbConfig, modules = Seq(DatabaseProvider))
      .bind[StatsReceiver].toInstance(NullStatsReceiver)
      .create
}

trait DatabaseTest extends FunSpec with IntegrationTestMixin with Logging with Mockito {

  override lazy val injector: Injector = InjectorProvider.value

  protected lazy val forumDB: PostgresClient =
    injector.instance(Key.get(classOf[PostgresClient], Names.named("forumdb")))

  def setupDatabase(): Unit = {
    createSchema().handle()

  }

  private lazy val forumMigration = injector.instance[MigrationHandler]

  private def createSchema() = {
    forumMigration
  }
}
