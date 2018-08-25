package com.scale.forum.replies.domain.http

import com.scale.forum.replies.domain.Reply
import com.twitter.finatra.validation.NotEmpty

case class ReplyPostRequest(@NotEmpty email: String, @NotEmpty body: String, topicId: Int) {

  def toDomain: Reply = {
    Reply(email = email, body = body, topicId = topicId)
  }
}

