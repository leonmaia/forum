package com.scale.forum.topics

import com.scale.forum.http.pagination.{PagedGetRequest, PagedResult, Pagination}
import com.scale.forum.notifications.NotificationService
import com.scale.forum.replies.ReplyService
import com.scale.forum.replies.domain.Reply
import com.scale.forum.topics.domain.Topic
import com.scale.forum.topics.domain.http.{PagedTopicGetRequest, TopicPostRequest}
import com.scale.forum.topics.persistence.Topics
import com.twitter.util.Future
import javax.inject.Inject

case class TopicService @Inject()(topics: Topics, notificationService: NotificationService, replyService: ReplyService) {

  def getReplies(pagedTopicGetRequest: PagedTopicGetRequest): Future[PagedResult[Reply]] = {
    val page = Pagination(pagedTopicGetRequest.page, pagedTopicGetRequest.limit)
    replyService.list(pagedTopicGetRequest.id, page)
  }

  def get(topicGetRequest: TopicGetRequest): Future[Option[Topic]] = {
    topics.get(topicGetRequest.id)
  }

  def list(pagedGetRequest: PagedGetRequest): Future[PagedResult[Topic]] = {
    val page = pagedGetRequest.toDomain
    for {
      (totalEntries, entries) <- topics.list(page)
    } yield {
      PagedResult[Topic](page, totalEntries, entries)
    }
  }

  def save(postedTopic: TopicPostRequest): Future[Topic] = {
    topics.add(postedTopic.toDomain)
  }.onSuccess { topic =>
    notificationService.add(topic.id.get, topic.email)
  }
}
