package com.rose.blog

import com.rose.blog.utils.toHttpMethod
import io.github.cdimascio.dotenv.Dotenv
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.CorsHandler

class BlogService(private val dotEnv: Dotenv = Dotenv.load(), private val port: Int = dotEnv.get("APP_PORT").toInt()) :
    AbstractVerticle() {
    private val logger = LoggerFactory.getLogger(this.javaClass.simpleName)
    override fun start(startPromise: Promise<Void>?) {
        super.start(startPromise)
        val router = Router.router(this.vertx)
        this.vertx.createHttpServer()
            .requestHandler(router)
            .listen(this.port) {
                if (it.succeeded()) {
                    this.logger.info("Server started on port: $port")
                    startPromise?.future()?.succeeded()
                    this.setRoutes(router)
                } else {
                    this.logger.error("Server failed start.")
                    startPromise?.future()?.failed()
                }
            }
    }

    override fun stop(stopPromise: Promise<Void>?) {
        super.stop(stopPromise)
        this.vertx.close {
            if (it.succeeded()) {
                this.logger.info("Server stopped successfully.")
                stopPromise?.complete()
            } else {
                this.logger.error("Server failed to stop.")
                stopPromise?.fail(it.cause())
            }
        }
    }

    private fun setRoutes(router: Router) {
        router.route()
            .handler(
                CorsHandler.create()
                    .allowedHeaders(dotEnv.get("ALLOWED_HEADERS").split(",").map { it.trim() }
                        .also { this.logger.info("$it was added as allowed headers.") }.toSet())
                    .allowedMethods(dotEnv.get("ALLOWED_METHODS").split(",").map { it.toHttpMethod() }
                        .also { this.logger.info("$it methods were registered successfully.") }.toSet())
            )
        router.route().handler {
            this.logger.info("${it.request().method()} --> ${it.request().uri()}")
            it.next()
        }
        router.get(dotEnv.get("APP_ROUTE_PREFIX")).handler(::ping)
    }

    private fun ping(rc: RoutingContext) {
        rc.response().apply {
            statusCode = HttpResponseStatus.OK.code()
            statusMessage = HttpResponseStatus.OK.reasonPhrase()
            putHeader("Content-Type", "application/json")
            putHeader("Accept", "application/json")
            end(
                JsonObject.of(
                    "statusCode", HttpResponseStatus.OK.code(),
                    "statusMessage", HttpResponseStatus.OK.reasonPhrase(),
                    "message", "Server is up and running on port $port",
                    "data", null
                ).encodePrettily()
            )
        }
    }
}
