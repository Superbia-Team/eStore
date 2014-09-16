package com.dm.estore.common;

public abstract class ExecutionCallback<T> {
    public abstract void done(final T resilt);
    public abstract void failed(final Throwable e);
    
    public void complete(final boolean success, final T resilt, final Throwable e) {
        // stub
    }
}
