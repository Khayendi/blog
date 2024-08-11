package com.rose.blog.utils

import io.vertx.core.http.HttpMethod


fun String.toHttpMethod(): HttpMethod? {
    return if (this.uppercase() == "POST")
        HttpMethod.POST
    else if (this.uppercase() == "GET")
        HttpMethod.GET
    else if (this.uppercase() == "PUT")
        HttpMethod.PUT
    else if (this.uppercase() == "PATCH")
        HttpMethod.PATCH
    else if (this.uppercase() == "OPTIONS")
        HttpMethod.OPTIONS
    else if (this.uppercase() == "DELETE")
        HttpMethod.DELETE
    else HttpMethod.GET
}