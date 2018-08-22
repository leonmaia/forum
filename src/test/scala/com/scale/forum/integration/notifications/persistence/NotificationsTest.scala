package com.scale.forum.integration.notifications.persistence

import com.scale.forum.integration.helpers.DatabaseTest
import com.scale.forum.notifications.domain.Notification
import com.scale.forum.notifications.persistence.Notifications
import com.scale.forum.replies.domain.Reply
import com.scale.forum.replies.persistence.Replies
import com.scale.forum.topics.domain.{Topic, TopicNotFound}
import com.scale.forum.topics.persistence.Topics
import com.twitter.util.Await.result

import scala.util.Random

class NotificationsTest extends DatabaseTest {

  setupDatabase()

  private val repoNotifications = Notifications(forumDB)
  private val repoTopics = Topics(forumDB)
  private val repoReplies = Replies(forumDB)

  describe("adding notifications") {
    it("should add notification") {
      val newTopic = Topic(Option.empty, "leon@gmail.com", "Something cool", "Why??")
      val resultTopic = result(repoTopics.add(newTopic))
      val notification = Notification(resultTopic.id.get, resultTopic.email)

      result(repoNotifications.add(notification))
      val notifications = result(repoNotifications.list(resultTopic.id.get))

      notifications.length shouldBe 1
      notifications.head.topicId shouldBe resultTopic.id.get
      notifications.head.email shouldBe resultTopic.email
    }

    it("should fail if topic does not exist") {
      val nonExistentTopicId = Random.nextInt
      val thrown = intercept[TopicNotFound] {
        val notification = Notification(nonExistentTopicId, "l@gmail.com")
        result(repoNotifications.add(notification))
      }
      thrown.getMessage shouldBe TopicNotFound(nonExistentTopicId).getMessage
    }
  }

  describe("listing notifications") {
    it("should list emails from topic") {
      val newTopic = Topic(Option.empty, "tracy@gmail.com", "Something cool", "Why??")
      val resultTopic = result(repoTopics.add(newTopic))
      result(repoNotifications.add(Notification(resultTopic.id.get, resultTopic.email)))
      val reply = Reply(email = "liz@gmail.com", body = "I agree!", topicId = resultTopic.id.get)
      val reply2 = reply.copy(email = "jack@gmail.com")

      List(reply, reply2).foreach(r => result(repoReplies.add(r)))
      List(reply, reply2).foreach(r => result(repoNotifications.add(Notification(r.topicId, r.email))))

      val resultNotifications = result(repoNotifications.list(resultTopic.id.get))

      resultNotifications.length shouldBe 3
      val filteredList = resultNotifications
        .filterNot(n => n.email == newTopic.email || n.email == reply.email || n.email == reply2.email)
      filteredList.length shouldBe 0
    }
  }

  it("should list only distinct emails from topic") {
    val newTopic = Topic(Option.empty, "tracy@gmail.com", "Something cool", "Why??")
    val resultTopic = result(repoTopics.add(newTopic))
    result(repoNotifications.add(Notification(resultTopic.id.get, resultTopic.email)))
    val reply = Reply(email = "tracy@gmail.com", body = "bump!", topicId = resultTopic.id.get)
    result(repoReplies.add(reply))
    result(repoNotifications.add(Notification(reply.topicId, reply.email)))

    val notifications = result(repoNotifications.list(resultTopic.id.get))

    notifications.length shouldBe 1
  }
}
