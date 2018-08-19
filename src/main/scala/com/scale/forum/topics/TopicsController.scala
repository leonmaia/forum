package com.scale.forum.topics

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import javax.inject.Inject

class TopicsController @Inject()(topics: TopicsService) extends Controller {

  post("/topics") { topic: TopicPostRequest =>
    response.created(topics.save(topic))
  }

  get("/topics") { _: Request =>
    response.ok(topics.list())
  }
}
