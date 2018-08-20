package com.scale.forum.replies.domain

case class Reply(id: Option[Int] = None,
                 email: String,
                 body: String,
                 topicId: Int) {

}
