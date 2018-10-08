case class Todo(id: String, title: String, description: String, done: Boolean)
case class CreateTodo(title: String, description: String)
case class UpdateTodo(title: Option[String], description: Option[String], done: Option[Boolean])
