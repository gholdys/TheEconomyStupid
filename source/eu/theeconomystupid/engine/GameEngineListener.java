package eu.theeconomystupid.engine;

import java.util.EventListener;

public interface GameEngineListener extends EventListener {
    void turnCompleted();
    void gameCreated();
    void gameAboutToBeCreated();
}
