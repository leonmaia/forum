package com.scale.forum.replies

import com.scale.forum.replies.domain.Reply
import com.scale.forum.replies.domain.http.{ReplyGetRequest, ReplyPostRequest}
import com.scale.forum.replies.persistence.Replies
import com.twitter.util.Future
import javax.inject.Inject

class ReplyService @Inject()(replies: Replies){
  def list(reply: ReplyGetRequest): Future[Seq[Reply]] = replies.list(reply.topicId)

  def save(reply: ReplyPostRequest): Future[Int] = replies.add(reply.toDomain)
}
