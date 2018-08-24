package com.scale.forum.replies

import com.scale.forum.http.pagination.{PagedResult, Pagination}
import com.scale.forum.notifications.NotificationService
import com.scale.forum.replies.domain.Reply
import com.scale.forum.replies.domain.http.ReplyPostRequest
import com.scale.forum.replies.persistence.Replies
import com.twitter.util.Future
import javax.inject.Inject

case class ReplyService @Inject()(replies: Replies, notificationService: NotificationService) {
  def list(topicId: Int, page: Pagination): Future[PagedResult[Reply]] = {
    for {
      (totalEntries, entries) <- replies.list(topicId, page)
    } yield {
      PagedResult[Reply](page, totalEntries, entries)
    }
  }

  def save(reply: ReplyPostRequest): Future[Int] = {
    replies.add(reply.toDomain)
  }.onSuccess { _ =>
    notificationService.add(reply.topicId, reply.email)
  }
}
