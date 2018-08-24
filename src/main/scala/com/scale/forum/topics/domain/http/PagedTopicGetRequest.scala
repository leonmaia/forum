package com.scale.forum.topics.domain.http

import com.scale.forum.http.pagination.Pagination
import com.twitter.finatra.request.{QueryParam, RouteParam}

case class PagedTopicGetRequest(@RouteParam id: Int, @QueryParam() page: Int, @QueryParam() limit: Int = Pagination.LIMIT)
