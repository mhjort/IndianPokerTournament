package org.laughingpanda.games.poker.indian.client;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

/**
 * @author Markus Hjort
 */
public class TestConfigurationReading extends TestCase {
	public void testReadsConfigurationFromInputStream() throws Exception {
		Configuration actual = new ConfigurationReader().readConfiguration(new InputStream() {
			StringBuffer in = new StringBuffer("botName\ntestClass\nname1\n1\nname2\n2\n");
			int index = 0;
			@Override
			public int read() throws IOException {
				if (index == in.length()) return -1;
				return in.charAt(index++);
			}});
		assertEquals("botName", actual.getBotName());
		assertEquals("testClass", actual.getBotClass());
		assertEquals("name1", actual.getServerHost().getName());
		assertEquals(1, actual.getServerHost().getPort());
		assertEquals("name2", actual.getBotHost().getName());
		assertEquals(2, actual.getBotHost().getPort());
	}
	
	public void testThrowsExceptionWhenInputStreamThrowsIOException() throws Exception {
		try {
			new ConfigurationReader().readConfiguration(new InputStream() {
				@Override
				public int read() throws IOException {
					throw new IOException("Simulated exception.");
				}});
			fail();
		} catch (RuntimeException expected) {
		}
	}
}
