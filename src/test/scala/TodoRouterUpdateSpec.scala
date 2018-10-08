import java.util.UUID

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}

class TodoRouterUpdateSpec extends WordSpec with Matchers with ScalatestRouteTest with TodoMocks {
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  val todoId = UUID.randomUUID().toString
  val testTodo = Todo(
    todoId,
    "Original title",
    "Original description",
    done = false
  )
  val testUpdateTodo = UpdateTodo(
    Some("Test todo"),
    None,
    Some(true)
  )

  "A TodoRouter" should {

    "update a todo with valid data" in {
      val repository = new InMemoryTodoRepository(Seq(testTodo))
      val router = new TodoRouter(repository)

      Put(s"/todos/$todoId", testUpdateTodo) ~> router.route ~> check {
        status shouldBe StatusCodes.OK
        val resp = responseAs[Todo]
        resp.title shouldBe testUpdateTodo.title.get
        resp.description shouldBe testTodo.description
        resp.done shouldBe testUpdateTodo.done.get
      }
    }

    "return not found with non existent todo" in {
      val repository = new InMemoryTodoRepository(Seq(testTodo))
      val router = new TodoRouter(repository)

      Put("/todos/1", testUpdateTodo) ~> router.route ~> check {
        status shouldBe ApiError.todoNotFound("1").statusCode
        val resp = responseAs[String]
        resp shouldBe ApiError.todoNotFound("1").message
      }
    }

    // TODO: test for validation

    "handle repository failure when updating todos" in {
      val repository = new FailingRepository
      val router = new TodoRouter(repository)

      Put(s"/todos/$todoId", testUpdateTodo) ~> router.route ~> check {
        status shouldBe ApiError.generic.statusCode
        val resp = responseAs[String]
        resp shouldBe ApiError.generic.message
      }
    }
  }

}
