package com.scale.forum.integration.notifications

import java.nio.charset.Charset

import com.scale.forum.integration.helpers.DatabaseTest
import com.scale.forum.notifications.NotificationService
import com.scale.forum.notifications.persistence.Notifications
import com.scale.forum.topics.domain.Topic
import com.scale.forum.topics.persistence.Topics
import com.twitter.io.Buf
import com.twitter.io.Buf.Utf8
import com.twitter.util.Await
import com.twitter.util.Await.result

import scala.util.Random

class NotificationServiceTest extends DatabaseTest {
  setupDatabase()
  val repo = Notifications(forumDB)
  val topicRepo = Topics(forumDB)
  val service = NotificationService(redisClient, repo)


  describe("checking queue for notification") {
    it("should return false if there's no items") {
      Await.result(service.isQueuePopulated("no_items")) shouldBe false
    }

    it("should return true if there's items") {
      Await.result(redisClient.rPush(Utf8("with_items"), List(Utf8(1.toString))))
      Await.result(service.isQueuePopulated("with_items")) shouldBe true
    }
  }

  describe("adding notifications to redis") {
    it("should add notification to redis list") {
      val topic = result(topicRepo.add(Topic(Option.empty, "leon@gmail.com", "Something cool", "Why??")))
      result(service.add(topic.id.get, topic.email))

      Await.result(redisClient.lRange(Utf8(service.TOPIC_QUEUE_NAME), 0l, 10l)).count(i => Buf.decodeString(i, Charset.defaultCharset()).toInt.equals(topic.id.get)) shouldBe 1
    }

    it("should not be able to add a notification to redis if topic does not exist") {
      val id = Random.nextInt
      result(service.add(id, "tracyjordan@30rock.com"))

      Await.result(redisClient.lRange(Utf8(service.TOPIC_QUEUE_NAME), 0l, 10l)).count(i => Buf.decodeString(i, Charset.defaultCharset()).toInt.equals(id)) shouldBe 0
    }
  }


}
