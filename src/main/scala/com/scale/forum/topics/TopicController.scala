package com.scale.forum.topics

import com.scale.forum.http.pagination.PagedGetRequest
import com.scale.forum.topics.domain.http.{PagedTopicGetRequest, TopicPostRequest}
import com.twitter.finatra.http.Controller
import javax.inject.Inject

class TopicController @Inject()(service: TopicService) extends Controller {

  post("/topics") { topic: TopicPostRequest =>
    service.save(topic).map(response.created.body)
  }

  get("/topics") { pagedGetRequest: PagedGetRequest =>
    service.list(pagedGetRequest).map(response.ok(_))
  }

  get("/topics/:id/replies") { pagedTopicGetRequest: PagedTopicGetRequest=>
    service.getReplies(pagedTopicGetRequest).map(response.ok(_))
  }
}