import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Entity;

import org.apache.commons.lang3.tuple.Pair;

import com.awarex.api.common.AwarexAPIException;
import com.awarex.api.common.RestAPIHelper;
import com.awarex.api.model.UserPostPayload;
import com.google.gson.Gson;

public class TestCreateUserPost {

	public TestCreateUserPost() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws AwarexAPIException {

		long startTime = System.nanoTime();
		final int MAX_ITERATIONS = 10;
		int iterations = 0;
		List<Long> executionTimes = new ArrayList();
		while (iterations <= MAX_ITERATIONS) {
			long executionStartTime = System.currentTimeMillis();
			UserPostPayload payload = new UserPostPayload("Test Name", "test004@ekahaa.com", "Female",
					"Title - " + iterations, "Body " + iterations);
			Pair<Integer, String> response = RestAPIHelper.invokePostAPI("createUserPost",
					"http://localhost:8080/awarex/api/v1/userposts/create", Entity.json(new Gson().toJson(payload)));
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
