package eu.deyanix.lorasupervisor.protocol.buffer;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class LoRaBuffer {
	public static final char[] BUFFER_DELIMITERS = {'\n', '\r'};

	public static boolean isDelimiter(char character) {
		return IntStream.range(0, BUFFER_DELIMITERS.length)
				.mapToObj(i -> BUFFER_DELIMITERS[i])
				.anyMatch(c -> c == character);
	}

	public static boolean hasDelimiter(CharSequence text) {
		return text.chars().anyMatch(c -> isDelimiter((char) c));
	}

	private final Duration timeout = Duration.ofSeconds(1);
	private final StringBuffer buffer = new StringBuffer();
	private final Lock lock = new ReentrantLock();
	private final Condition hasBuffer = lock.newCondition();
	private final LoRaBufferListener listener = new LoRaBufferListener();

	protected void write(CharSequence text) {
		lock.lock();
		try {
			buffer.append(text);
			hasBuffer.signalAll();
		} finally {
			lock.unlock();
		}
	}

	public String read(int minLength) {
		lock.lock();
		try {
			while (buffer.length() <= minLength) {
				if (hasBuffer.awaitNanos(timeout.toNanos()) <= 0) {
					return null;
				}
			}

			return buffer.toString();
		} catch (InterruptedException e) {
			return null;
		} finally {
			lock.unlock();
		}
	}

	public void clearAll() {
		buffer.setLength(0);
	}

	public void clear(int length) {
		buffer.delete(0, length);
	}

	public LoRaBufferListener getListener() {
		return listener;
	}

	public class LoRaBufferListener implements SerialPortDataListener {
		@Override
		public int getListeningEvents() {
			return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
		}

		@Override
		public void serialEvent(SerialPortEvent event) {
			if (event.getEventType() == SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
				try (InputStream in = event.getSerialPort().getInputStream()) {
					StringBuilder sb = new StringBuilder();
					while (in.available() > 0) {
						sb.append((char) in.read());
					}

					write(sb.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
