package com.scale.forum.topics

import com.scale.forum.topics.domain.http.TopicPostRequest
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import javax.inject.Inject

class TopicsController @Inject()(topics: TopicsService) extends Controller {

  post("/topics") { topic: TopicPostRequest =>
    topics.save(topic).map(response.created)
  }

  get("/topics") { _: Request =>
    topics.list().map(response.ok(_))
  }
}
