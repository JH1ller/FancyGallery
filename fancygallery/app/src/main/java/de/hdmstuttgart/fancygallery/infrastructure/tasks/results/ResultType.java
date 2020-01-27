package de.hdmstuttgart.fancygallery.infrastructure.tasks.results;

/**
 * Used to differentiate the cause of an unsuccessful task.
 * If a task could not be performed because of missing permission the app can ask for
 * permissions.
 * If a task could not be performed because of an error we can notify the user about it.
 */
public enum ResultType {
    success,
    noPermission,
    error,
}
