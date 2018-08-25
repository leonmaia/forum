package com.scale.forum.topics.domain

case class Topic(id: Option[Int] = None, email: String, title: String, body: String) {
}

