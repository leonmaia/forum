package com.scale.forum.provider

import com.google.inject.name.Names
import com.twitter.finagle.Postgres
import com.twitter.finagle.postgres.PostgresClient
import com.twitter.inject.TwitterModule

object DatabaseProvider extends TwitterModule {
  private val host = flag(s"forumdb.host", "", s"forum database host")
  private val port = flag(s"forumdb.port", "5432", s"forum database port")
  private val name = flag(s"forumdb.name", "forum", s"forum database name")
  private val user = flag(s"forumdb.user", s"forum database user")
  private val password = flag(s"forumdb.password", s"forum database password")

  protected override def configure(): Unit = {
    super.configure()
    bindSingleton[PostgresClient]
      .annotatedWith(Names.named(s"forumdb"))
      .toInstance(
        Postgres
          .Client()
          .withCredentials(user(), Some(password()))
          .database(name())
          .withSessionPool
          .minSize(5)
          .withSessionPool
          .maxSize(10)
          .withBinaryResults(true)
          .withBinaryParams(true)
          .withLabel(s"forumdb")
          .newRichClient(s"${host()}:${port()}")
      )
  }
}

