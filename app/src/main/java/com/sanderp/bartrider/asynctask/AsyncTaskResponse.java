package com.sanderp.bartrider.asynctask;

/**
 * Callback interface to delegate async task results to another class.
 */
public interface AsyncTaskResponse {
    void processFinish(Object output);
}
