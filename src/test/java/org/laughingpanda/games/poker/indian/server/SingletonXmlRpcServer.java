/*
 * Copyright 2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.laughingpanda.games.poker.indian.server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.apache.xmlrpc.WebServer;

/**
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public abstract class SingletonXmlRpcServer implements Runnable {

	public static final int PORT = 5000;

	private static WebServer instance;

	private static CountDownLatch started = new CountDownLatch(1);

	private static WebServer getXmlRpcServer() {
		if (instance == null) {
			startWebServer();
		}
		return instance;
	}

	private static void startWebServer() {
		new Thread() {
			@Override
			public void run() {
				try {
					instance = new WebServer(PORT, InetAddress.getLocalHost());
					instance.start();
					started.countDown();
				} catch (UnknownHostException e) {
					throw new RuntimeException(e);
				}
			}
		}.start();
		waitFor(started);
	}

	private static void waitFor(CountDownLatch latch) {
		try {
			Assert.assertTrue("Joined proxy didn't communicate back within timeout period", latch.await(10, TimeUnit.SECONDS));
		} catch (InterruptedException e) {
			Assert.fail("Latch wait was interrupted during the timeout period");
		}
	}

	public static void setHandler(String name, Object handler) {
		getXmlRpcServer().removeHandler(name);
		getXmlRpcServer().addHandler(name, handler);
	}

}
