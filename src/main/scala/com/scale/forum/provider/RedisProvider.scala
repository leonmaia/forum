package com.scale.forum.provider


import com.google.inject.name.Names
import com.redis.RedisClient
import com.twitter.inject.TwitterModule

object RedisProvider extends TwitterModule {
  private val host = flag(s"redisdb.host", "", s"forum redis host")
  private val port = flag(s"redisdb.port", "6379", s"forum redis port")

  protected override def configure(): Unit = {
    super.configure()
    bindSingleton[RedisClient]
      .annotatedWith(Names.named(s"redisdb"))
      .toInstance(
        new RedisClient(host= host(), port = port().toInt)
      )
  }
}


