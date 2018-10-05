import java.util.UUID

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}

class TodoRouterCreateSpec extends WordSpec with Matchers with ScalatestRouteTest with TodoMocks {
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  val testCreateTodo = CreateTodo(
    "Test todo",
    "Test description"
  )

  "A TodoRouter" should {

    "create a todo with valid data" in {
      val repository = new InMemoryTodoRepository()
      val router = new TodoRouter(repository)

      Post("/todos", testCreateTodo) ~> router.route ~> check {
        status shouldBe StatusCodes.OK
        val resp = responseAs[Todo]
        resp.title shouldBe testCreateTodo.title
        resp.description shouldBe testCreateTodo.description
      }
    }

    "not create a todo with invalid data" in {
      val repository = new FailingRepository
      val router = new TodoRouter(repository)

      Post("/todos", testCreateTodo.copy(title = "")) ~> router.route ~> check {
        status shouldBe ApiError.emptyTitleField.statusCode
        val resp = responseAs[String]
        resp shouldBe ApiError.emptyTitleField.message
      }
    }

    "handle repository failure when creating todos" in {
      val repository = new FailingRepository
      val router = new TodoRouter(repository)

      Post("/todos", testCreateTodo) ~> router.route ~> check {
        status shouldBe ApiError.generic.statusCode
        val resp = responseAs[String]
        resp shouldBe ApiError.generic.message
      }
    }
  }

}
