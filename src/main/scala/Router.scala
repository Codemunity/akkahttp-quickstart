import akka.http.scaladsl.server.{Directives, Route}

trait Router {
  def route: Route
}

class TodoRouter(todoRepository: TodoRepository) extends Router with Directives with TodoDirectives with ValidatorDirectives {
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  override def route: Route = pathPrefix("todos") {
    pathEndOrSingleSlash {
      get {
        handleWithGeneric(todoRepository.all()) { todos =>
          complete(todos)
        }
      } ~ post {
        entity(as[CreateTodo]) { createTodo =>
          validateWith(CreateTodoValidator)(createTodo) {
            handleWithGeneric(todoRepository.save(createTodo)) { todos =>
              complete(todos)
            }
          }
        }
      }
    } ~ path("done") {
      get {
        handleWithGeneric(todoRepository.done()) { todos =>
          complete(todos)
        }
      }
    } ~ path("pending") {
      get {
        handleWithGeneric(todoRepository.pending()) { todos =>
          complete(todos)
        }
      }
    }
  }
}
