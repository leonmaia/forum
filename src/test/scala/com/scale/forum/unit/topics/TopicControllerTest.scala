package com.scale.forum.unit.topics

import com.scale.forum.http.pagination.{PagedResult, Pagination}
import com.scale.forum.replies.domain.Reply
import com.scale.forum.topics.domain.Topic
import com.scale.forum.unit.helpers.ControllerTest
import com.twitter.finagle.http.Status._
import com.twitter.util.Future

class TopicControllerTest extends ControllerTest {

  val topics = List(
    Topic(Option(1), "leon@gmail.com", "Test Title", "Is this a test?"),
    Topic(Option(2), "leon@gmail.com", "Serious Title", "Why so serious?")
  )

  describe ("getting a topic") {
    it("should return http 200 with topic when topic exist") {
      mockTopics.get(1) returns Future(Option(topics.head))

      server.httpGet(
        path = "/topics/1",
        andExpect = Ok,
        withJsonBody = toJson(topics.head)
      )
    }

    it("should return http 200 with no topic when topic does not exist") {
      mockTopics.get(any) returns Future(Option.empty)

      server.httpGet(
        path = "/topics/102303",
        andExpect = NotFound
      )
    }
  }

  describe("adding topics") {
    it("should return http 201 with topic") {
      mockTopics.add(any) returns Future(topics.head)

      server.httpPost(
        path = "/topics",
        postBody =
          """
            {
              "email": "leon@gmail.com",
              "title": "Test Subject",
              "body": "Just a test"
            }
            """,
        andExpect = Created,
        withJsonBody = toJson(topics.head)
      )
    }

    it("should return http 400 when topic is not valid") {
      val topic = Topic(email = "", title = "", body = "")

      server.httpPost(
        path = "/topics",
        postBody = toJson(topic),
        andExpect = BadRequest,
        withJsonBody =
          """
          {
            "errors": [
              "email: cannot be empty",
              "title: cannot be empty"
            ]
          }
          """
      )
    }

    it("should return http 400 with errors object") {
      server.httpPost(
        path = "/topics",
        postBody =
          """
          {
            "email": "leon@gmail.com",
            "title": "Test Subject"
          }
          """,
        andExpect = BadRequest,
        withJsonBody =
          """
          {
            "errors": [
              "body: field is required"
            ]
          }
          """
      )
    }
  }

  describe("listing topics") {
    it("should return http 200 with topics") {
      val pagination = Pagination()
      val pagedResult = PagedResult[Topic](pagination, topics.length, topics)
      mockTopics.list(pagination) returns Future((topics.length, topics))

      server.httpGet(
        path = "/topics?page=1",
        andExpect = Ok,
        withJsonBody = toJson(pagedResult)
      )
    }
  }

  describe("listing replies for a topic") {
    it("should return http 200 with replies") {
      val replies = Seq(
        Reply(email = "leon@gmail.com", body = "I agree!", topicId = 1),
        Reply(email = "leon@gmail.com", body = "I disagree!", topicId = 1)
      )
      val pagination = Pagination()
      val pagedResult = PagedResult[Reply](pagination, topics.length, replies)

      mockReplies.list(1, pagination) returns Future((replies.length, replies))

      server.httpGet(
        path = "/topics/1/replies?page=1",
        andExpect = Ok,
        withJsonBody = toJson(pagedResult)
      )
    }
  }
}
