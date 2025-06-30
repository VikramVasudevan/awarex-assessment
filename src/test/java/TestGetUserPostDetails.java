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
		while (iterations <= MAX_ITERATIONS) {
			String response = RestAPIHelper.invokeGetAPI("getUserPostDetails",
					"http://localhost:8080/awarex/api/v1/userposts/getUserPostDetails");
			iterations++;
		}
		iterations--;
		long endTime = System.nanoTime();
		System.out.println("Total Time Taken for " + (iterations) + " iterations = " + (endTime - startTime) + "ns");
		System.out.println("Average Time Taken = " + ((endTime - startTime) / 1000000) / iterations + "ms");

	}

}
