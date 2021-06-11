package de.quantummaid.mapmaid.builder.recipes.throwablesupport;

import de.quantummaid.mapmaid.builder.MapMaidBuilder;
import de.quantummaid.mapmaid.builder.recipes.Recipe;
import de.quantummaid.reflectmaid.ReflectMaid;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import static de.quantummaid.mapmaid.builder.recipes.throwablesupport.StackTraceStateFactory.stackTraceStateFactory;
import static de.quantummaid.mapmaid.builder.recipes.throwablesupport.ThrowableSerializer.throwableSerializer;
import static de.quantummaid.mapmaid.builder.recipes.throwablesupport.ThrowableStateFactory.throwableStateFactory;
import static de.quantummaid.reflectmaid.typescanner.TypeIdentifier.typeIdentifierFor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ThrowableSupport implements Recipe {
    private final int maxStackFrameCount;

    public static ThrowableSupport throwableSupport(final int maxStackFrameCount) {
        return new ThrowableSupport(maxStackFrameCount);
    }

    @Override
    public void cook(final MapMaidBuilder builder) {
        final ReflectMaid reflectMaid = builder.reflectMaid();
        final TypeIdentifier throwableType = resolve(reflectMaid, Throwable.class);
        final TypeIdentifier stackTraceType = resolve(reflectMaid, StackTraceElement[].class);
        final ThrowableSerializer serializer = throwableSerializer(throwableType, stackTraceType);
        builder.withAdvancedSettings(advancedBuilder -> {
            advancedBuilder.withSuperTypeSerializer(throwableType, serializer);
            advancedBuilder.withStateFactory(throwableStateFactory(throwableType, serializer));
            advancedBuilder.withStateFactory(stackTraceStateFactory(reflectMaid, maxStackFrameCount));
        });
    }

    private static TypeIdentifier resolve(final ReflectMaid reflectMaid, final Class<?> type) {
        final ResolvedType resolvedType = reflectMaid.resolve(type);
        return typeIdentifierFor(resolvedType);
    }
}
