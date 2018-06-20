package eu.theeconomystupid.engine;

public abstract class GameEngineAdapter implements GameEngineListener {
    public GameEngineAdapter() {}
    public void turnCompleted() {}
    public void gameCreated() {}
    public void gameAboutToBeCreated() {}
}
