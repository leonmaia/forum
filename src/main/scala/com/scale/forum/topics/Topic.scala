package com.scale.forum.topics

import java.util.UUID

import javax.inject.Singleton

import scala.collection.mutable

case class Topic(id: UUID, email: String, title: String, body: String)

@Singleton
class Topics {
  private[this] val db: mutable.Map[UUID, Topic] = mutable.Map.empty[UUID, Topic]

  def list(): List[Topic] = synchronized {
    db.values.toList
  }

  def save(t: Topic): Topic = synchronized {
    db += (t.id -> t)
    t
  }
}

