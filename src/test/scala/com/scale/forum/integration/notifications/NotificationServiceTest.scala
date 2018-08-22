package com.scale.forum.integration.notifications

import com.scale.forum.integration.helpers.DatabaseTest
import com.scale.forum.notifications.NotificationService
import com.scale.forum.notifications.persistence.Notifications
import com.scale.forum.topics.domain.Topic
import com.scale.forum.topics.persistence.Topics
import com.twitter.util.Await.result

import scala.util.Random

class NotificationServiceTest extends DatabaseTest {
  setupDatabase()
  val repo = Notifications(forumDB)
  val topicRepo = Topics(forumDB)
  val service = NotificationService(redisClient, repo)


  describe("checking queue for notification") {
    it("should return false if there's no items") {
      service.isQueuePopulated("no_items") shouldBe false
    }

    it("should return true if there's items") {
      redisClient.rpush("with_items", 1)
      service.isQueuePopulated("with_items") shouldBe true
    }
  }

  describe("adding notifications to redis") {
    it("should add notification to redis list") {
      val topic = result(topicRepo.add(Topic(Option.empty, "leon@gmail.com", "Something cool", "Why??")))
      result(service.add(topic.id.get, topic.email))

      redisClient.lrange(service.TOPIC_QUEUE_NAME, 0, 10).get.flatten.count(i => i.toInt.equals(topic.id.get)) shouldBe 1
    }

    it("should not be able to add a notification to redis if topic does not exist") {
      val id = Random.nextInt
      result(service.add(id, "tracyjordan@30rock.com"))

      redisClient.lrange(service.TOPIC_QUEUE_NAME, 0, 10).get.flatten.count(i => i.toInt.equals(id)) shouldBe 0
    }
  }


}
