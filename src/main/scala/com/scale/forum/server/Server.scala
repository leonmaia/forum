package com.scale.forum.server

import com.scale.forum.provider.DatabaseProvider
import com.scale.forum.replies.ReplyController
import com.scale.forum.server.migration.MigrationHandler
import com.scale.forum.topics.TopicController
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.{CommonFilters, LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.logging.Logging

object ServerMain extends Server

class Server extends HttpServer with Logging {

  override val modules = Seq(DatabaseProvider)

  override def configureHttp(router: HttpRouter): Unit = {
    router
      .filter[LoggingMDCFilter[Request, Response]]
      .filter[TraceIdMDCFilter[Request, Response]]
      .filter[CommonFilters]
      .add[TopicController]
      .add[ReplyController]
  }

  override def warmup(): Unit = {
    handle[MigrationHandler]()
  }
}