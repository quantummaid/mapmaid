package de.quantummaid.mapmaid.specs.changing_structure

data class Order(
        val address: Address,
        val status: ShippingStatus
)
sealed class ShippingStatus {
    data class ToBeShipped(val timeToShipping: Time) : ShippingStatus()
    data class Shipping(val timeToDelivery: Time, val location: Location) : ShippingStatus()
    data class Delivered(val success: DeliveryStatus) : ShippingStatus()
}
data class Time(val milliseconds: Long) {
    companion object {
        fun hours(hours: Long) = minutes(hours * 60)
        fun minutes(minutes: Long) = seconds(minutes * 60)
        fun seconds(seconds: Long) = Time(seconds * 1000)
    }
}

data class Location(val x: Long, val y: Long)
data class DeliveryStatus(val success: Boolean)

data class Address(
        val zip: Zip,
        val city: City,
        val street: Street,
        val number: StreetNumber
)
data class Zip(val value: String)
data class City(val value: String)
data class Street(val value: String)
data class StreetNumber(val value: String)
data class ProductId(val value: String)
data class User(val userId: UserId, val address: Address)
data class UserId(val value: String)
