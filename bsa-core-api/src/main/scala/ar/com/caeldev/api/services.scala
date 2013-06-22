package ar.com.caeldev.api

import spray.http.StatusCodes._
import spray.http._
import spray.routing._
import directives.{ CompletionMagnet, RouteDirectives }
import spray.util.LoggingContext
import util.control.NonFatal
import spray.httpx.marshalling.Marshaller
import spray.http.HttpHeaders.RawHeader
import akka.actor.Actor
import org.json4s.{ NoTypeHints, native, Formats }
import org.json4s.ext.JodaTimeSerializers
import akka.util.Timeout
import scala.concurrent.duration._

/** Holds potential error response with the HTTP status and optional body
 *
 *  @param responseStatus the status code
 *  @param response the optional body
 */
case class ErrorResponseException(responseStatus: StatusCode, response: Option[HttpEntity]) extends Exception

/** Provides a hook to catch exceptions and rejections from routes, allowing custom
 *  responses to be provided, logs to be captured, and potentially remedial actions.
 *
 *  Note that this is not marshalled, but it is possible to do so allowing for a fully
 *  JSON API (e.g. see how Foursquare do it).
 */
trait FailureHandling {
  this: HttpService =>

  // For Spray > 1.1-M7 use routeRouteResponse
  // see https://groups.google.com/d/topic/spray-user/zA_KR4OBs1I/discussion
  def rejectionHandler: RejectionHandler = RejectionHandler.Default

  def exceptionHandler(implicit log: LoggingContext) = ExceptionHandler {

    case e: IllegalArgumentException => ctx =>
      loggedFailureResponse(ctx, e,
        message = "The server was asked a question that didn't make sense: "+e.getMessage,
        error = NotAcceptable)

    case e: NoSuchElementException => ctx =>
      loggedFailureResponse(ctx, e,
        message = "The server is missing some information. Try again in a few moments.",
        error = NotFound)

    case t: Throwable => ctx =>
      // note that toString here may expose information and cause a security leak, so don't do it.
      loggedFailureResponse(ctx, t)
  }

  private def loggedFailureResponse(ctx: RequestContext,
                                    thrown: Throwable,
                                    message: String = "The server is having problems.",
                                    error: StatusCode = InternalServerError)(implicit log: LoggingContext): Unit = {
    log.error(thrown, ctx.request.toString)
    ctx.complete(error, message)
  }

}

/** Allows you to construct Spray ``HttpService`` from a concatenation of routes; and wires in the error handler.
 *  @param route the (concatenated) route
 */
class RoutedHttpService(route: Route) extends Actor with HttpService {

  implicit def actorRefFactory = context

  implicit val handler = ExceptionHandler {
    case NonFatal(ErrorResponseException(statusCode, entity)) => ctx =>
      ctx.complete(statusCode, entity)

    case NonFatal(e) => ctx =>
      ctx.complete(InternalServerError)
  }

  def receive = {
    runRoute(route)(handler, RejectionHandler.Default, context, RoutingSettings.default, LoggingContext.fromActorRefFactory)
  }

}

/** Constructs ``CompletionMagnet``s that set the ``Access-Control-Allow-Origin`` header for modern browsers' AJAX
 *  requests on different domains / ports.
 */
trait CrossLocationRouteDirectives extends RouteDirectives {

  implicit def fromObjectCross[T: Marshaller](origin: String)(obj: T) =
    new CompletionMagnet {
      def route: StandardRoute = new CompletionRoute(OK,
        RawHeader("Access-Control-Allow-Origin", origin) :: Nil, obj)
    }

  private class CompletionRoute[T: Marshaller](status: StatusCode, headers: List[HttpHeader], obj: T)
      extends StandardRoute {
    def apply(ctx: RequestContext): Unit = {
      ctx.complete(status, headers, obj)
    }
  }
}

object ResourceMap extends Enumeration {
  type ResourceMap = Value

  val role = Value("role").toString
  val roles = Value("roles").toString
  val member = Value("member").toString
  val members = Value("members").toString
  val group = Value("group").toString
  val groups = Value("groups").toString
  val notification = Value("notification").toString
  val notifications = Value("notifications").toString

}

trait CommonConcurrentFeature {
  implicit val timeout = Timeout(5 seconds)
}

trait CommonJson4sSerializationFeature {
  implicit def json4sFormats: Formats = native.Serialization.formats(NoTypeHints) ++ JodaTimeSerializers.all
}