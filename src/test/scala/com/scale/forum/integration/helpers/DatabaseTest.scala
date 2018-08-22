package com.scale.forum.integration.helpers

import com.google.inject.Key
import com.google.inject.name.Names
import com.redis.RedisClient
import com.scale.forum.provider.{DatabaseProvider, RedisProvider}
import com.scale.forum.server.migration.MigrationHandler
import com.twitter.finagle.postgres.PostgresClient
import com.twitter.finagle.stats.{NullStatsReceiver, StatsReceiver}
import com.twitter.inject.app.TestInjector
import com.twitter.inject.{Injector, IntegrationTestMixin, Logging, Mockito}
import org.scalatest.FunSpec


object Postgres {
  val psqlContainer = PostgreSQL("forum")
}

object Redis {
  val redisContainer = RedisContainer("notification")
}

object InjectorProvider {

  import Postgres.psqlContainer
  import Redis.redisContainer

  lazy val dbConfig = Map(
    "forumdb.host" -> psqlContainer.host,
    "forumdb.port" -> psqlContainer.port.toString,
    "forumdb.user" -> psqlContainer.username,
    "forumdb.password" -> psqlContainer.password
  )

  lazy val redisConfig = Map(
    "redisdb.host" -> redisContainer.host,
    "redisdb.port" -> redisContainer.port.toString
  )

  val value: Injector =
    TestInjector(flags = dbConfig ++ redisConfig, modules = Seq(DatabaseProvider, RedisProvider))
      .bind[StatsReceiver].toInstance(NullStatsReceiver)
      .create
}

trait DatabaseTest extends FunSpec with IntegrationTestMixin with Logging with Mockito {

  override lazy val injector: Injector = InjectorProvider.value

  protected lazy val forumDB: PostgresClient =
    injector.instance(Key.get(classOf[PostgresClient], Names.named("forumdb")))

  protected lazy val redisClient: RedisClient =
    injector.instance(Key.get(classOf[RedisClient], Names.named("redisdb")))

  def setupDatabase(): Unit = {
    createSchema().handle()
  }

  private lazy val forumMigration = injector.instance[MigrationHandler]

  private def createSchema() = {
    forumMigration
  }
}
