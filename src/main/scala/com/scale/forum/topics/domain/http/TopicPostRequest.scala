package com.scale.forum.topics.domain.http

import com.scale.forum.topics.domain.Topic
import com.twitter.finatra.validation.NotEmpty

case class TopicPostRequest(@NotEmpty email: String, @NotEmpty title: String, body: String) {

  def toDomain: Topic = {
    Topic(email = email, title = title, body = body)
  }
}
