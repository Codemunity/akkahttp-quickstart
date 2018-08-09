import scala.concurrent.Future

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}

class TodoDirectivesSpec extends WordSpec with Matchers with ScalatestRouteTest with Directives with TodoDirectives {
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  private val testRoute = pathPrefix("test") {
    path("success") {
      get {
        handleWithDefault(Future.unit) { _ =>
          complete(StatusCodes.OK)
        }
      }
    } ~ path("failure") {
      handleWithDefault(Future.failed(new Exception("Failure!"))) { _ =>
        complete(StatusCodes.OK)
      }
    }
  }

  "TodoDirectives" should {

    "not return an error if the future succeeds" in {
      Get("/test/success") ~> testRoute ~> check {
        status shouldBe StatusCodes.OK
      }
    }

    "return an error if the future fails" in {
      Get("/test/failure") ~> testRoute ~> check {
        status shouldBe StatusCodes.InternalServerError
        val resp = responseAs[String]
        resp shouldBe ApiError.generic.message
      }
    }
  }
}
