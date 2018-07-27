import scala.concurrent.Future

trait TodoMocks {

  class FailingRepository extends TodoRepository {
    override def all(): Future[Seq[Todo]] = Future.failed(new Exception("Mocked exception"))

    override def done(): Future[Seq[Todo]] = Future.failed(new Exception("Mocked exception"))

    override def pending(): Future[Seq[Todo]] = Future.failed(new Exception("Mocked exception"))
  }

}
