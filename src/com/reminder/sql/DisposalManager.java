package com.reminder.sql;

import java.util.*;

public class DisposalManager
{
    public static DisposalManager Instance = new DisposalManager();

    private static final ArrayList<AutoCloseable> _disposables = new ArrayList<>();

    public static <T extends AutoCloseable> T manage(T disposable)
    {
        _disposables.add(disposable);
        return disposable;
    }

    public void dispose()
    {
        for (AutoCloseable disposable : _disposables)
        {
            try
            {
                disposable.close();
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }

        _disposables.clear();
    }
}
