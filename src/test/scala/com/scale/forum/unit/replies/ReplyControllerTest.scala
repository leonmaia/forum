package com.scale.forum.unit.replies

import com.scale.forum.topics.domain.TopicNotFound
import com.scale.forum.unit.helpers.ControllerTest
import com.twitter.finagle.http.Status._
import com.twitter.util.Future

class ReplyControllerTest extends ControllerTest {

  describe("adding replies") {
    it("should return http 201 when creating a reply") {
      mockReplies.add(any) returns Future(1)

      server.httpPost(
        path = "/replies",
        postBody =
          """
            {
              "email": "leon@gmail.com",
              "body": "Just a test",
              "topic_id": 1
            }
            """,
        andExpect = Created
      )
    }

    it("should return http 404 when adding a reply that doesnt have a topic") {
      mockReplies.add(any) throws new  IllegalArgumentException(s"topic with id: 1 does not exist.")

      server.httpPost(
        path = "/replies",
        postBody =
          """
            {
              "email": "leon@gmail.com",
              "body": "Just a test",
              "topic_id": 1
            }
            """,
        andExpect = BadRequest,
        withJsonBody = toJson("topic with id: 1 does not exist.")
      )
    }

    it("should return http 400 with errors object") {
      server.httpPost(
        path = "/replies",
        postBody =
          """
            {
              "email": "leon@gmail.com",
              "body": "Just a test"
            }
            """,
        andExpect = BadRequest,
        withJsonBody =
          """
          {
            "errors": [
              "topic_id: field is required"
            ]
          }
          """
      )
    }

    it("should return http 400 with errors object if reply fails validation") {
      server.httpPost(
        path = "/replies",
        postBody =
          """
            {
              "email": "",
              "body": "Just a test",
              "topic_id": 10
            }
            """,
        andExpect = BadRequest,
        withJsonBody =
          """
          {
            "errors": [
              "email: cannot be empty"
            ]
          }
          """
      )
    }
  }
}
