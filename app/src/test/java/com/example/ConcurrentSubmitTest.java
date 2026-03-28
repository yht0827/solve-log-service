package com.example;

import static org.assertj.core.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class ConcurrentSubmitTest extends AppTestSupport {

	@Autowired
	TestRestTemplate restTemplate;

	@Test
	@DisplayName("동시에 같은 문제를 제출하면 하나만 성공하고 나머지는 409를 반환한다")
	void concurrent_submit_only_one_succeeds() throws InterruptedException {
		// given
		int threadCount = 10;
		CountDownLatch startLatch = new CountDownLatch(1);
		CountDownLatch doneLatch = new CountDownLatch(threadCount);
		AtomicInteger successCount = new AtomicInteger();
		AtomicInteger conflictCount = new AtomicInteger();

		HttpHeaders headers = new HttpHeaders();
		headers.set("X-User-Id", "1");
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>(
			"{\"problemId\": 2, \"userAnswers\": [\"1\"]}", headers);

		for (int i = 0; i < threadCount; i++) {
			new Thread(() -> {
				try {
					startLatch.await();

					// when
					var response = restTemplate.postForEntity("/api/v1/problems/submit", request, String.class);
					if (response.getStatusCode() == HttpStatus.OK) {
						successCount.incrementAndGet();
					} else if (response.getStatusCode() == HttpStatus.CONFLICT) {
						conflictCount.incrementAndGet();
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				} finally {
					doneLatch.countDown();
				}
			}).start();
		}

		startLatch.countDown(); // 모든 스레드 동시 출발
		doneLatch.await(10, TimeUnit.SECONDS);

		// then
		assertThat(successCount.get()).isEqualTo(1);
		assertThat(conflictCount.get()).isEqualTo(threadCount - 1);
	}
}
