package com.scale.forum.integration.helpers

import org.testcontainers.containers.GenericContainer


case class RedisContainer(redisName: String) {
  type ContainerType = GenericContainer[T] forSome { type T <: GenericContainer[T] }
  val container: ContainerType = new GenericContainer("redis:5.0-rc4-alpine")

  container.start()

  def host: String     = container.getContainerIpAddress
  def port: Integer    = container.getMappedPort(6379)

  def shutdown(): Unit = container.stop()
}

