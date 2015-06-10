package com.sleroux.credit.strategy;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ TestCompileSeries.class, TestLengthStrategy.class, TestSimplePret.class, TestSmoothingStrategy.class, TestSplitStrategy.class })
public class AllTests {

}
