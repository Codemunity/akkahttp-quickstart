trait Validator[T] {
  def validate(t: T): Option[ApiError]
}

object CreateTodoValidator extends Validator[CreateTodo] {

  def validate(createTodo: CreateTodo): Option[ApiError] = {
    if (createTodo.title.isEmpty) Some(ApiError.emptyTitleField)
    else None
  }
}

object UpdateTodoValidator extends Validator[UpdateTodo] {

  def validate(updateTodo: UpdateTodo): Option[ApiError] = {
    // TODO: implement
    None
  }
}
