package com.scale.forum.topics

import com.scale.forum.topics.domain.Topic
import com.scale.forum.topics.domain.http.TopicPostRequest
import com.scale.forum.topics.persistence.Topics
import com.twitter.util.Future
import javax.inject.Inject

case class TopicService @Inject()(topics: Topics) {

  def list(): Future[Seq[Topic]] = topics.list()

  def save(postedTopic: TopicPostRequest): Future[Topic] = {
    topics.add(postedTopic.toDomain)
  }
}
