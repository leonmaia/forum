package com.scale.forum.topics.domain

case class TopicNotFound(id: Int) extends Exception {
  override def getMessage: String = s"Topic(${id.toString}) not found."
}

