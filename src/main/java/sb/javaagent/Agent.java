package sb.javaagent;

import java.lang.instrument.Instrumentation;

/**
 * Created by slwk on 01.06.16.
 */
public class Agent {

    public static void premain(String args, Instrumentation instrumentation) {
        System.out.println("---STARTING AGENT---");

        instrumentation.addTransformer(new MyLogTransformer());
    }
}
