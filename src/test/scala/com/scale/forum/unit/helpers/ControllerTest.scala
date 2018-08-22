package com.scale.forum.unit.helpers

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.databind.{ObjectMapper, PropertyNamingStrategy}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.google.inject.Stage
import com.scale.forum.notifications.NotificationService
import com.scale.forum.replies.persistence.Replies
import com.scale.forum.server.Server
import com.scale.forum.topics.persistence.Topics
import com.twitter.finatra.http.{EmbeddedHttpServer, HttpTest}
import com.twitter.inject.server.FeatureTestMixin
import org.scalatest.FunSpec
import org.specs2.mock.Mockito

import scala.reflect.ClassTag
import scala.reflect.runtime.universe._


trait ControllerTest extends FunSpec with Mockito with FeatureTestMixin with HttpTest {

  object Json {
    private val mapper = (new ObjectMapper() with ScalaObjectMapper)
      .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
      .setSerializationInclusion(Include.NON_ABSENT)
    mapper.registerModule(DefaultScalaModule)

    def toJson(obj: Any): String = mapper.writeValueAsString(obj)

    def fromJson[T](string: String)(implicit m: Manifest[T]): T =
      mapper.readValue(string, m.runtimeClass).asInstanceOf[T]

  }

  def useMock[T](implicit tag: TypeTag[T]): T = {
    implicit val classTscaag: ClassTag[T] =
      ClassTag[T](typeTag[T].mirror.runtimeClass(typeTag[T].tpe))
    val mock = smartMock[T]
    bind(mock)
    mock
  }

  def bind[T](mock: T)(implicit tag: TypeTag[T]): Unit = server.bind[T].toInstance(mock)

  override val server: EmbeddedHttpServer = new EmbeddedHttpServer(
    twitterServer = new Server {
      override val modules = Seq()
      override def warmup(): Unit  = {}
    },
    disableTestLogging = true,
    stage = Stage.DEVELOPMENT,
    verbose = true,
    streamResponse = true,
  )

  def toJson(tuples: (String, Any)*): String = toJson(tuples.toMap)

  def toJson(obj: Any): String = Json.toJson(obj)

  override def beforeAll(): Unit = {
    server.assertHealthy()
  }

  protected val mockTopics: Topics = useMock[Topics]

  protected val mockReplies: Replies = useMock[Replies]

  protected val mockNotifications: NotificationService = useMock[NotificationService]

}
