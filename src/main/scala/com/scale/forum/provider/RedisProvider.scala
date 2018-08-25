package com.scale.forum.provider

import com.google.inject.name.Names
import com.twitter.finagle.Redis
import com.twitter.finagle.redis.Client
import com.twitter.inject.TwitterModule

object RedisProvider extends TwitterModule {
  private val host = flag(s"redisdb.host", "", s"forum redis host")
  private val port = flag(s"redisdb.port", "6379", s"forum redis port")

  protected override def configure(): Unit = {
    super.configure()
    bindSingleton[Client]
      .annotatedWith(Names.named(s"redisdb"))
      .toInstance(
        Redis.newRichClient(s"${host()}:${port()}")
      )
  }
}


