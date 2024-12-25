package example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class HandlerIntegerJava17 implements RequestHandler<IntegerRecord, Integer>{

	@Override
	/*
	 * Takes in an InputRecord, which contains two integers and a String.
	 * Logs the String, then returns the sum of the two Integers.
	 */
	public Integer handleRequest(IntegerRecord event, Context context)
	{
		System.out.println("been here, done that. ");
		return 10;
	}
}

