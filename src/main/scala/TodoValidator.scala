object TodoValidator {

  def validate(createTodo: CreateTodo): Option[ApiError] = {
    if (createTodo.title.isEmpty) Some(ApiError.emptyTitleField)
    else None
  }

}
