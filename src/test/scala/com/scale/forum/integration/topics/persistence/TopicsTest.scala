package com.scale.forum.integration.topics.persistence

import com.scale.forum.integration.helpers.DatabaseTest
import com.scale.forum.topics.domain.Topic
import com.scale.forum.topics.persistence.Topics
import com.twitter.util.Await.result

class TopicsTest extends DatabaseTest {

  setupDatabase()

  val topics = Seq(
    Topic(Option.empty, "leon@gmail.com", "Test Title", "Is this a test?"),
    Topic(Option.empty, "leon@gmail.com", "Something Fun", "What?")
  )

  private val repo: Topics = Topics(forumDB)

  topics.foreach(item => result(repo.add(item)))

  describe("adding topics") {
    it("should add a topic") {
      val newTopic = Topic(Option.empty, "leon@gmail.com", "Something cool", "Why??")
      val resultTopic = result(repo.add(newTopic))

      newTopic.body shouldBe resultTopic.body
      newTopic.title shouldBe resultTopic.title
      newTopic.email shouldBe resultTopic.email
    }
  }


  describe("listing topics") {
    it("should list all topics") {
      val resultTopics = result(repo.list())
      resultTopics._2.length >= 2 shouldBe true
    }

    it("should list all topics in descending order") {
      val resultTopics = result(repo.list())
      (resultTopics._2.head.id.get > resultTopics._2.last.id.get) shouldBe true
    }
  }
}
