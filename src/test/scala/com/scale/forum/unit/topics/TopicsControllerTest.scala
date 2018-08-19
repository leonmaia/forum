package com.scale.forum.unit.topics

import java.util.UUID

import com.scale.forum.unit.topics.domain.Topic
import com.scale.forum.unit.topics.helpers.ControllerTest
import com.twitter.finagle.http.Status._
import com.twitter.util.Future

class TopicsControllerTest extends ControllerTest {

  val topics = List(
    Topic(UUID.randomUUID(), "leon@gmail.com", "Test Title", "Is this a test?"),
    Topic(UUID.randomUUID(), "leon@gmail.com", "Serious Title", "Why so serious?")
  )

  describe("adding topics") {
    it("should return http 201 with topic") {
      mockTopics.add(any) returns Future(1)

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
        andExpect = Created
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
      mockTopics.list() returns Future(topics)

      server.httpGet(
        path = "/topics",
        andExpect = Ok,
        withJsonBody = toJson(topics)
      )
    }
  }
}
