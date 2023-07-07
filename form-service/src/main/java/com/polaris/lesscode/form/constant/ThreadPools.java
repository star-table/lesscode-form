package com.polaris.lesscode.form.constant;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadPools {

    public final static ThreadPoolExecutor POOLS = (ThreadPoolExecutor) Executors.newFixedThreadPool(200);
}
