package com.scale.forum.topics

import com.twitter.finatra.request.RouteParam

case class TopicGetRequest(@RouteParam id: Int)
