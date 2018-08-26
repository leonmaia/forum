package com.scale.forum.integration.replies.persistence

import com.scale.forum.integration.helpers.DatabaseTest
import com.scale.forum.replies.domain.Reply
import com.scale.forum.replies.persistence.Replies
import com.scale.forum.topics.domain.{Topic, TopicNotFound}
import com.scale.forum.topics.persistence.Topics
import com.twitter.util.Await.result

class RepliesTest extends DatabaseTest {

  setupDatabase()
  private val repoReplies = Replies(forumDB)
  private val repoTopics = Topics(forumDB)

  private val replies = Seq(
    Reply(email = "leon@gmail.com", body = "I agree!", topicId = 1),
    Reply(email = "leon@gmail.com", body = "I disagree!", topicId = 1)
  )

  describe("adding replies") {
    it("should add a reply if topic exists") {
      val topic = Topic(Option.empty, "leon@gmail.com", "Test Title", "Is this a test?")
      val topicId = result(repoTopics.add(topic)).id

      val newReply = replies.head.copy(topicId = topicId.get)
      result(repoReplies.add(newReply)) shouldBe 1
    }

    it("should fail if topic does not exist") {
      val erroredReply = Reply(email = "leon@gmail.com", body = "I disagree!", topicId = 10)
      val thrown = intercept[IllegalArgumentException] {
        result(repoReplies.add(erroredReply))
      }
       thrown.getMessage shouldBe "topic with id: 10 does not exist."
    }
  }

  describe("listing replies") {
    it("should list replies") {
      val topic = Topic(Option.empty, "leon@gmail.com", "Test Title", "Is this a test?")
      result(repoTopics.add(topic))
      replies.foreach(reply => result(repoReplies.add(reply)))

      result(repoReplies.list(1))._2.length >= 2 shouldBe true
    }
  }
}
