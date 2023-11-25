package com.example.ktor_server

import androidx.compose.runtime.mutableStateOf
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.gson.gson
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.authorization
import io.ktor.server.request.httpMethod
import io.ktor.server.request.receive
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.logging.Logger

class Server {
    enum class ServerState{
        STARTED,
        STOPPED,
        STOPPING,
    }
    private var port: Int = 4321;
    private val logger = Logger.getLogger("Server");
    var serverState = mutableStateOf(ServerState.STOPPED);
    private var users = mutableListOf<Contact>()

    private var server: NettyApplicationEngine? = null;

    fun initServer() {
        server = embeddedServer(Netty, port = this.port) {
            install(WebSockets)
            install(ContentNegotiation) {
                gson { }
            }
            routing {
                route("/public") {
                    route("/register") {
                        get {
                            //Это я тестил в браузере маршрут. На деле get запроса нет
                            logger.info("get request to /public/register")
                            logger.info(call.request.queryParameters.toString())
                            call.respond(TokenResponse("test-token"))
                        }
                        post {
                            //Тут я принимаю post запрос на регистрацию, паршу входные данные и отправляю в ответ "test-token"
                            logger.info("post request to /public/register")
                            logger.info(call.request.httpMethod.value)
                            try {
                                val register: RegistrationRequest = call.receive()
                                users.add(
                                    Contact(
                                        register.uniqueKey,
                                        register.userName,
                                        register.type,
                                        true,
                                        register.icon,
                                        register.charge
                                    )
                                );
                                logger.info(register.toString())
                                call.respond(Gson().toJson(TokenResponse("test-token")))
                            } catch (e: Exception) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                route("/api") {
                    route("/lis-object") {
                        post {
                            logger.info("post /api/lis-object")
                            var token: String;
                            try {
                                token = parseToken(call)
                            } catch (e: JsonSyntaxException) {
                                call.respond(HttpStatusCode.Unauthorized);
                                return@post;
                            } catch (e: NullPointerException) {
                                call.respond(HttpStatusCode.Unauthorized);
                                return@post;
                            }
                            logger.info("token: $token")
                        }
                    }
                }
                webSocket {
                    logger.info("socket...")
                    logger.info(this.call.request.uri);
                    val event = ContactsEvent(type = "CONTACTS_EVENT", payload = users)
                    val event_string = Gson().toJson(event);
                    logger.info(event_string)
                    send(content = event_string)
//                    send(content = "Connected!");
                    for (frame in incoming) {
                        frame as? Frame.Text ?: continue
                        logger.info("Receive ${frame.readText()}");
                        //тут можно отправить ответ на сообщение
                        //send(content = AnswerObject)
                    }
                }
            }
        }
    }

    @Throws(JsonSyntaxException::class, NullPointerException::class)
    fun parseToken(call: ApplicationCall): String {
        var auth = call.request.authorization() ?: throw NullPointerException();
        if (auth.contains("Bearer")) auth = auth.split("Bearer ")[1];
        val tokenModel = Gson().fromJson(auth, TokenResponse::class.java);
        return tokenModel.token;
    }

    fun startServer(port: Int) {
        logger.info("Starting server at port $port ...")
        this.port = port;
        initServer();
        serverState.value = ServerState.STARTED
        CoroutineScope(Dispatchers.IO).launch {
            server?.start(wait = true)
        }
    }

    fun stopServer() {
        logger.info("Stopping server...")
        serverState.value = ServerState.STOPPING;
        CoroutineScope(Dispatchers.IO).launch {
            server?.stop(1_000, 2_000)
            server = null;
            logger.info("STOPPED!");
            serverState.value = ServerState.STOPPED;
        }
    }

    companion object {
        val instance = Server()
    }
}