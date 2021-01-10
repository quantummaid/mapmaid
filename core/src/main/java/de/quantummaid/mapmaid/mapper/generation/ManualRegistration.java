package de.quantummaid.mapmaid.mapper.generation;

public interface ManualRegistration {
    static ManualRegistration emptyManualRegistration() {
        return new ManualRegistration() {
            @Override
            public String render() {
                return "// TODO";
            }

            @Override
            public ManualRegistration merge(final ManualRegistration manualRegistration) {
                return this;
            }
        };
    }

    String render();

    ManualRegistration merge(ManualRegistration manualRegistration);
}
