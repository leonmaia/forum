package com.scale.forum.replies.domain.http

import com.twitter.finatra.request.RouteParam

case class ReplyGetRequest (@RouteParam topicId: Int)
