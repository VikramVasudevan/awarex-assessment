import java.util.ArrayList;
import java.util.List;

import com.awarex.api.common.AwarexAPIException;
import com.awarex.api.common.RestAPIHelper;

public class TestGetUserPostDetails {

	public TestGetUserPostDetails() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws AwarexAPIException {

		long startTime = System.nanoTime();
		final int MAX_ITERATIONS = 10;
		int iterations = 0;
		List<Long> executionTimes = new ArrayList();
		while (iterations <= MAX_ITERATIONS) {
			long executionStartTime = System.currentTimeMillis();
			String response = RestAPIHelper.invokeGetAPI("getUserPostDetails",
					"http://localhost:8080/awarex/api/v1/userposts/getUserPostDetails");
			long executionEndTime = System.currentTimeMillis();
			long executionTime = executionEndTime - executionStartTime;
			executionTimes.add(executionTime);
			iterations++;
		}
		iterations--;
		long endTime = System.nanoTime();
		System.out.println(
				"Total Time Taken for " + (iterations) + " iterations = " + ((endTime - startTime) / 1000000) + "ms");
		System.out.println("Average Time Taken = " + ((endTime - startTime) / 1000000) / iterations + "ms");
		System.out.println("Max Time Taken = " + executionTimes.stream().max(Long::compare).get() + "ms");
		System.out.println("Min Time Taken = " + executionTimes.stream().min(Long::compare).get() + "ms");

	}

}
