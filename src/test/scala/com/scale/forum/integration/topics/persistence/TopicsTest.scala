package com.scale.forum.integration.topics.persistence

import java.util.UUID

import com.scale.forum.integration.helpers.DatabaseTest
import com.scale.forum.unit.topics.domain.Topic
import com.scale.forum.unit.topics.persistence.Topics
import com.twitter.util.Await.result

class TopicsTest extends DatabaseTest {

  setupDatabase()

  override def beforeEach() = {
    resetDatabase()
  }

  private val repo: Topics = Topics(forumDB)

  describe("adding topics") {
    it("should add a topic") {
      val topic = Topic(UUID.randomUUID(), "leon@gmail.com", "Test Title", "Is this a test?")

      result(repo.add(topic)) shouldBe 1
    }
  }


  describe("listing topics") {
    it("should list all topics") {
      val topics = Seq(
        Topic(UUID.randomUUID(), "leon@gmail.com", "Test Title", "Is this a test?"),
        Topic(UUID.randomUUID(), "leon@gmail.com", "Something Fun", "What?")
      )
      topics.foreach(topic => result(repo.add(topic)))

      val resultTopics = result(repo.list())
      resultTopics.length shouldBe 2
      resultTopics.head shouldBe topics.last
      resultTopics.last shouldBe topics.head
    }
  }
}
