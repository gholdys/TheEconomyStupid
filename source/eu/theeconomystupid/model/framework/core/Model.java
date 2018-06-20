package eu.theeconomystupid.model.framework.core;

import java.io.Serializable;

public interface Model extends Serializable {
    Registry getRegistry();
    void step();
    void cleanup();
}
