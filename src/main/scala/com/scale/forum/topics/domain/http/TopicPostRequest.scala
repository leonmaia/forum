package com.scale.forum.topics.domain.http

import com.scale.forum.topics.domain.Topic

case class TopicPostRequest(email: String, title: String, body: String) {

  def toDomain: Topic = {
    Topic(email = email, title = title, body = body)
  }
}
