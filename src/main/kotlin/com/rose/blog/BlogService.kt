package com.rose.blog

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.http.HttpClientResponse
import io.vertx.core.http.HttpMethod
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.CorsHandler

class BlogService(private val port:Int=System.getenv("APP_PORT").toInt()):AbstractVerticle() {
    private val logger = LoggerFactory.getLogger(this.javaClass.simpleName)
    override fun start(startPromise: Promise<Void>?) {
        super.start(startPromise)
        val router = Router.router(this.vertx)
        this.vertx.createHttpServer()
            .requestHandler(router)
            .listen(this.port){
                if (it.succeeded()){
                    this.logger.info("Server started on port: $port")
                    startPromise?.complete()
                    setRoutes(router)
                }else{
                    this.logger.error("Server failed start.")
                    startPromise?.fail(it.cause())
                }
            }
    }

    override fun stop(stopPromise: Promise<Void>?) {
        super.stop(stopPromise)
        this.vertx.close{
            if (it.succeeded()){
                this.logger.info("Server stopped successfully.")
                stopPromise?.complete()
            }else{
                this.logger.error("Server failed to stop.")
                stopPromise?.fail(it.cause())
            }
        }
    }

    private fun setRoutes(router: Router){
        router.route()
            .handler(
            CorsHandler.create()
                .allowCredentials(true)
                .allowedHeaders(System.getenv("ALLOWED_HEADERS").split(",").toSet())
                .allowedMethods(System.getenv("ALLOWED_METHODS").split(",").map { it.toHttpMethod() }.onEach { this.logger.info("${it?.name()} method registered successfully.") }.toSet()))

    }




}


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