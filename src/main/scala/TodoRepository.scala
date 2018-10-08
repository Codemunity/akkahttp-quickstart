import java.util.UUID

import scala.concurrent.{ExecutionContext, Future}

import TodoRepository.TodoNotFound

trait TodoRepository {

  def all(): Future[Seq[Todo]]
  def done(): Future[Seq[Todo]]
  def pending(): Future[Seq[Todo]]

  def save(createTodo: CreateTodo): Future[Todo]
  def update(id: String, updateTodo: UpdateTodo): Future[Todo]
}

object TodoRepository {

  final case class TodoNotFound(id: String) extends Exception("")
}

class InMemoryTodoRepository(initialTodos: Seq[Todo] = Seq.empty)(implicit ec: ExecutionContext) extends TodoRepository {

  private var todos: Vector[Todo] = initialTodos.toVector

  override def all(): Future[Seq[Todo]] = Future.successful(todos)

  override def done(): Future[Seq[Todo]] = Future.successful(todos.filter(_.done))

  override def pending(): Future[Seq[Todo]] = Future.successful(todos.filterNot(_.done))

  override def save(createTodo: CreateTodo): Future[Todo] = Future.successful {
    val todo = Todo(
      UUID.randomUUID().toString,
      createTodo.title,
      createTodo.description,
      false
    )
    todos = todos :+ todo
    todo
  }

  override def update(id: String, updateTodo: UpdateTodo): Future[Todo] = {
    todos.find(_.id == id) match {
      case Some(foundTodo) =>
        val newTodo = updateHelper(foundTodo, updateTodo)
        todos = todos.map(t => if (t.id == id) newTodo else t)
        Future.successful(newTodo)
      case None =>
        Future.failed(TodoNotFound(id))
    }
  }

  private def updateHelper(todo: Todo, updateTodo: UpdateTodo): Todo = {
    val t1 = updateTodo.title.map(title => todo.copy(title = title)).getOrElse(todo)
    val t2 = updateTodo.description.map(description => t1.copy(description = description)).getOrElse(t1)
    updateTodo.done.map(done => t2.copy(done = done)).getOrElse(t2)
  }
}
