package msa.AlertStateMachine;

public enum State {
    INITIAL,
    SANITY_CHECK,
    ADDITIONAL_CHECK,
    WAITING,
    DISTRIBUTION,
    INVALIDATED
}
