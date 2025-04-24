package com.example.checkers.mvcinterfaces;

public interface Subject {
    /**
     * Adds a controller that will be updated by this model when a change occurs.
     *
     * @param c the controller to be added
     */
    void addController(Controller c);

    /**
     * Remove a controller so that it is no longer updated when a change occurs.
     *
     * @param c the controller to be added
     */
    void removeController(Controller c);

}
