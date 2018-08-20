package com.scale.forum.integration.topics.persistence

import com.scale.forum.integration.helpers.DatabaseTest
import com.scale.forum.topics.domain.Topic
import com.scale.forum.topics.persistence.Topics
import com.twitter.util.Await.result

class TopicTest extends DatabaseTest {

  setupDatabase()

  val topics = Seq(
    Topic(Option.empty, "leon@gmail.com", "Test Title", "Is this a test?"),
    Topic(Option.empty, "leon@gmail.com", "Something Fun", "What?")
  )

  override def beforeEach() = {
    resetDatabase()
  }

  private val repo: Topics = Topics(forumDB)

  describe("adding topics") {
    it("should add a topic") {
      result(repo.add(topics.head)) shouldBe 1
    }
  }


  describe("listing topics") {
    it("should list all topics") {
      topics.foreach(topic => result(repo.add(topic)))

      val resultTopics = result(repo.list())
      resultTopics.length shouldBe 2
    }

    it("should list all topics in descending order") {
      topics.foreach(topic => result(repo.add(topic)))

      val resultTopics = result(repo.list())
      resultTopics.length shouldBe 2
      (resultTopics.head.id.get > resultTopics.last.id.get) shouldBe true
    }
  }
}
