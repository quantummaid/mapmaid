package de.quantummaid.mapmaid.specs.examples.serializedobjects.success.one_string_parameter_in_constructor

data class UseCaseRequestKotlin(val messageId: String, val payload: UserDtoKotlin)
data class UseCaseResponseKotlin(val correlationId: String, val payload: UserDtoKotlin)
data class UserDtoKotlin(val firstName: FirstName, val lastName: LastName)
data class FirstName(val value: String)
data class LastName(val value: String)
