package com.scale.forum.unit.replies

import com.scale.forum.replies.domain.Reply
import com.scale.forum.unit.helpers.ControllerTest
import com.twitter.finagle.http.Status.{BadRequest, Created, Ok}
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
  }
}
