import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}

import scala.util.{Failure, Success}

trait Router {
  def route: Route
}

class TodoRouter(todoRepository: TodoRepository) extends Router with Directives {
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  override def route: Route = pathPrefix("todos") {
    pathEndOrSingleSlash {
      get {
        onComplete(todoRepository.all()) {
          case Success(todos) =>
            complete(todos)
          case Failure(exception) =>
            println(exception.getMessage)
            complete(StatusCodes.InternalServerError)
        }
      }
    } ~ path("done") {
      get {
        complete(todoRepository.done())
      }
    } ~ path("pending") {
    get {
      complete(todoRepository.pending())
    }
  }
  }
}
