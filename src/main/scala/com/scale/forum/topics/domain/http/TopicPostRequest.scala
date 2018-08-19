package com.scale.forum.topics.domain.http

import java.util.UUID

import com.scale.forum.topics.domain.Topic

case class TopicPostRequest(email: String, title: String, body: String) {

  def toDomain: Topic = {
    Topic(id = UUID.randomUUID(), email = email, title = title, body = body)
  }
}
