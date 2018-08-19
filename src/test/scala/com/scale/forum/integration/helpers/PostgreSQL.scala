package com.scale.forum.integration.helpers

import org.testcontainers.containers.PostgreSQLContainer

case class PostgreSQL(databaseNames: String*) {
  type ContainerType = PostgreSQLContainer[T] forSome { type T <: PostgreSQLContainer[T] }
  val container: ContainerType = new PostgreSQLContainer("postgres:10-alpine")

  databaseNames.headOption.foreach(container.withDatabaseName)
  container.start()

  databaseNames.tail.foreach(name => {
    val result = container.execInContainer("psql",
      "-U",
      "postgres",
      "-c",
      s"create database $name",
      databaseNames.head)
    println(s"Creating database $name: ${result.getStdout} ${result.getStderr}")
  })

  def password: String = container.getPassword
  def username: String = container.getUsername
  def host: String     = container.getContainerIpAddress
  def port: Integer    = container.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT)

  def shutdown(): Unit = container.stop()
}
