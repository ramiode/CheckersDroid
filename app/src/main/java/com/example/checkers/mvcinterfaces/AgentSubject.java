package com.example.checkers.mvcinterfaces;

/**
 * An extension of subject for use in concrete Agents.
 *
 * @author Ramiar Odendaal
 */
public interface AgentSubject extends Subject{
    /**
     * Used to notify the controller when a move has been made.
     */
    void notifyMoveMade();
}
