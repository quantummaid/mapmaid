# Injections

Injections allow enriching the original serialized information with contextual data that is available when a factory 
method of any Serialized Object is called. The intended use case for this feature is to allow web frameworks to map
authentication headers (JWT token) into actual AuthenticatedUser instances and inject them into the request DTO.

Example: Update a users shipping address.
The request json would look like this:
```json
{
  "street": "John Doe Street 23",
  "zip": "234223",
  "city": "Jane Doe"
}
```
The DTO may look like this:
```java
public final class UpdateShippingAddressRequest {
    public final AuthenticatedUser authenticatedUser;
    public final Street street;
    public final Zip zip;
    public final City city;
    //...
    public static UpdateShippingAddressRequest deserialize(
                        final AuthenticatedUser authenticatedUser,
                        final Street street,
                        final Zip zip,
                        final City city) {
        //...
    }
    //...
}
```
The usecase would look like this:
```java
public UpdateShippingAddressResult updateShippingAddress(final UpdateShippingAddressRequest request) {
    //...
    final User user = this.userRepository.byId(request.authenticatedUser.id);
    user.updateShippingAddress(request.street, request.zip, request.city);
    this.userRepository.update(user);
    //...
}
```
 
Outside of UI context, this aids creating descriptive log entries, that could be used as a means of reproducing and tracing the errors.  
