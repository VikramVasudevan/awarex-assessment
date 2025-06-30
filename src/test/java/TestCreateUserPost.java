import javax.ws.rs.client.Entity;

import org.apache.commons.lang3.tuple.Pair;

import com.awarex.api.common.AwarexAPIException;
import com.awarex.api.common.RestAPIHelper;
import com.awarex.api.model.UserPostPayload;

public class TestCreateUserPost {

	public TestCreateUserPost() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws AwarexAPIException {

		long startTime = System.nanoTime();
		final int MAX_ITERATIONS = 10;
		int iterations = 0;
		while (iterations <= MAX_ITERATIONS) {
			UserPostPayload payload = new UserPostPayload("Test Name", "test004@ekahaa.com", "Female",
					"Title - " + iterations, "Body " + iterations);
			Pair<Integer, String> response = RestAPIHelper.invokePostAPI("createUserPost",
					"http://localhost:8080/awarex/api/v1/userposts/create", Entity.json(payload));
			iterations++;
		}
		iterations--;
		long endTime = System.nanoTime();
		System.out.println("Total Time Taken for " + (iterations) + " iterations = " + (endTime - startTime) + "ns");
		System.out.println("Average Time Taken = " + ((endTime - startTime) / 1000000) / iterations + "ms");

	}

}
