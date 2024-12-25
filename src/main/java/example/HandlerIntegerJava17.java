package example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class HandlerIntegerJava17 implements RequestHandler<IntegerRecord, Integer>{

	@Override
	public Integer handleRequest(IntegerRecord event, Context context)
	{
		System.out.println("been here, done that. 2");
		return 10;
	}
}

