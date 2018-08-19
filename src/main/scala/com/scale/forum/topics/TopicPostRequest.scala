package com.scale.forum.topics

import java.util.UUID

case class TopicPostRequest(email: String, title: String, body: String) {

  def toDomain: Topic = {
    Topic(id = UUID.randomUUID(), email = email, title = title, body = body)
  }
}
