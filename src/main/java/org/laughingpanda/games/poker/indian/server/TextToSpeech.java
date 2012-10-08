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

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

/**
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public class TextToSpeech implements IndianpokerCommentator {
	private Voice voice;

	public TextToSpeech() {
		voice = VoiceManager.getInstance().getVoice("kevin16");
		voice.allocate();
	}

	public void say(String speech) {
		voice.speak(speech);
	}

	@Override
	protected void finalize() throws Throwable {
		voice.deallocate();
	}

	TextToSpeech(Voice voice) {
		this.voice = voice;
	}
}
