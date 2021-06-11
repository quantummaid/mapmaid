package de.quantummaid.mapmaid.mapper.serialization;

import de.quantummaid.mapmaid.mapper.universal.Universal;
import de.quantummaid.mapmaid.mapper.universal.UniversalObject;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static de.quantummaid.mapmaid.debug.MapMaidException.mapMaidException;

public final class UniversalMerger {

    public static Universal mergeUniversal(final Universal target, final List<Universal> inputs) {
        if (inputs.isEmpty()) {
            return target;
        }
        final UniversalObject objectTarget = castToUniversalObject(target);
        final Map<String, Universal> targetMap = new LinkedHashMap<>(objectTarget.toUniversalMap());
        for (final Universal input : inputs) {
            mergeUniversal(targetMap, input);
        }
        return UniversalObject.universalObject(targetMap);
    }

    private static void mergeUniversal(final Map<String, Universal> target, final Universal input) {
        final UniversalObject objectInput = castToUniversalObject(input);
        target.putAll(objectInput.toUniversalMap());
    }

    private static UniversalObject castToUniversalObject(final Universal universal) {
        if (!(universal instanceof UniversalObject)) {
            throw mapMaidException("can only merge universal objects but found " + universal.getClass());
        }
        return (UniversalObject) universal;
    }
}
