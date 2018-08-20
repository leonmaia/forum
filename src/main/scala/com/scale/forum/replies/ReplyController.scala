package com.scale.forum.replies

import com.scale.forum.replies.domain.http.{ReplyGetRequest, ReplyPostRequest}
import com.twitter.finatra.http.Controller
import javax.inject.Inject

class ReplyController @Inject()(replies: ReplyService) extends Controller {

  post("/replies") { reply: ReplyPostRequest =>
    replies.save(reply).map(response.created)
  }

  get("/replies/:topic_id") { replyGetRequest: ReplyGetRequest =>
    replies.list(replyGetRequest)
  }
}
