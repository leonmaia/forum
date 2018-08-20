package com.scale.forum.server.migration

import javax.inject.Inject
import com.twitter.finagle.stats.Stat
import com.twitter.finagle.stats.StatsReceiver
import com.twitter.inject.Logging
import com.twitter.inject.annotations.Flag
import com.twitter.inject.utils.Handler
import com.twitter.util.Try
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.resolver.MigrationResolver
import slick.migration.api.flyway.Resolver
import slick.migration.api.flyway.VersionedMigration

class MigrationHandler @Inject()(statsReceiver: StatsReceiver,
                                 @Flag("forumdb.host") host: String,
                                 @Flag("forumdb.port") port: Int,
                                 @Flag("forumdb.name") name: String,
                                 @Flag("forumdb.user") user: String,
                                 @Flag("forumdb.password") password: String)
  extends Handler
    with Logging {

  override def handle(): Unit = time("Migrations ran in %s ms") {
    Stat.time(statsReceiver.stat("dbmigration")) {
      Try(migrate()).onFailure { e: Throwable =>
        error("Could not run migration", e)
        System.exit(1)
      }
    }
  }

  private def migrate() = {
    val resolver: MigrationResolver = Resolver(migrations.zipWithIndex.map {
      case (m, i) => VersionedMigration(i.toString, m)
    }: _*)

    val flyway = new Flyway()
    flyway.setDataSource(s"jdbc:postgresql://$host:$port/$name", user, password)
    flyway.setLocations()
    flyway.setResolvers(resolver)
    flyway.migrate()
  }
}

