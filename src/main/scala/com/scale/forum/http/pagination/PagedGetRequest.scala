package com.scale.forum.http.pagination

import com.twitter.finatra.request.QueryParam

case class PagedGetRequest(@QueryParam() page: Int, @QueryParam() limit: Int = Pagination.LIMIT) {

  def toDomain: Pagination = {
    Pagination(page, limit)
  }
}
