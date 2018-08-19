package com.scale.forum.topics

import java.util.UUID

import com.twitter.finagle.http.Status._

class TopicsControllerTest extends ControllerTest {

  val topics = List(
    Topic(UUID.randomUUID(), "leon@gmail.com", "Test Title", "Is this a test?"),
    Topic(UUID.randomUUID(), "leon@gmail.com", "Serious Title", "Why so serious?")
  )

  describe("adding topics") {
    it("should return http 201 with topic") {
      mockTopics.save(any) returns topics.head

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
        withJsonBody =
          s"""
            {
              "id": "${topics.head.id}",
              "email": "${topics.head.email}",
              "title": "${topics.head.title}",
              "body": "${topics.head.body}"
            }
            """)
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
      mockTopics.list() returns topics

      server.httpGet(
        path = "/topics",
        andExpect = Ok,
        withJsonBody = toJson(topics)
      )
    }
  }

}
