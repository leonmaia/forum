package com.scale.forum.replies.domain.http

import com.scale.forum.replies.domain.Reply

case class ReplyPostRequest(email: String, body: String, topicId: Int) {

  def toDomain: Reply = {
    Reply(email = email, body = body, topicId = topicId)
  }
}

