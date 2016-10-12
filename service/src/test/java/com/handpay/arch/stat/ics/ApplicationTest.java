package com.handpay.arch.stat.ics;

import com.handpay.arch.stat.ics.repositories.StatRepositoryTester;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

public class ApplicationTest {
	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		TestSuite suite= new TestSuite("TestSuite for app");
		suite.addTest(new JUnit4TestAdapter(StatRepositoryTester.class));
		return suite;
	}
}
