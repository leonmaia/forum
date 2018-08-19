package com.scale.forum.topics.domain

import java.util.UUID

case class Topic(id: UUID, email: String, title: String, body: String)

