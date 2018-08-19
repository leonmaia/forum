package com.scale.forum.topics

import javax.inject.Inject

case class TopicsService @Inject()(topics: Topics) {

  def list(): List[Topic] = topics.list()

  def save(postedTopic: TopicPostRequest): Topic = {
    val topic = postedTopic.toDomain
    topics.save(topic)
  }
}
