package msa;

import java.util.List;

public interface BaseStateDefinition<S, T, Arg> {
    S getState();
    List<Transition<S, T, Arg>> getTransitions();
    List<T> ignoreTriggers();
}
