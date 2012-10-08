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

import java.io.IOException;

import org.laughingpanda.games.poker.indian.server.TextToSpeech;

import junit.framework.TestCase;

import com.sun.speech.freetts.Tokenizer;
import com.sun.speech.freetts.UtteranceProcessor;
import com.sun.speech.freetts.Voice;


/**
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public class TestTextToSpeech extends TestCase {

	private TextToSpeech tts;

	private MockVoice mockVoice;

	@Override
	protected void setUp() throws Exception {
		mockVoice = new MockVoice();
		tts = new TextToSpeech(mockVoice);
	}

	public void testVoiceShouldSpeakWhatIsSaid() throws Exception {
		tts.say("Hello.");
		assertEquals("Hello.", mockVoice.spoke);
	}

	public void testVoiceShouldBeDeallocatedOnFinalization() throws Throwable {
		tts.finalize();
		assertTrue(mockVoice.isDeallocateCalled);
	}

	private static class MockVoice extends Voice {
		boolean isDeallocateCalled = false;

		String spoke;

		@Override
		public boolean speak(String spoke) {
			this.spoke = spoke;
			return false;
		}

		@Override
		public void deallocate() {
			isDeallocateCalled = true;
		}

		@Override
		protected UtteranceProcessor getAudioOutput() throws IOException {
			throw new UnsupportedOperationException("Method not implemented.");
		}

		@Override
		public Tokenizer getTokenizer() {
			throw new UnsupportedOperationException("Method not implemented.");
		}

		@Override
		protected void loader() throws IOException {
			throw new UnsupportedOperationException("Method not implemented.");
		}
	}
}
