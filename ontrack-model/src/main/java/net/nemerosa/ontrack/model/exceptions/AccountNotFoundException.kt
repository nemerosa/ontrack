package net.nemerosa.ontrack.model.exceptions

class AccountNotFoundException(id: Int) : NotFoundException("Account with id = $id cannot be found.")
