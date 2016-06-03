package sb.javaagent;

import javassist.*;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * Created by slwk on 01.06.16.
 */
public class MyLogTransformer implements ClassFileTransformer {

    public static final String PREFIX = "sb";

    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        System.out.println("--- LOADING CLASS: " + className);


        ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(new ByteArrayClassPath(className, classfileBuffer));

        CtClass ctClass = null;
        try {
            ctClass = classPool.get(className.replaceAll("/", "."));
        } catch (NotFoundException e) {
            e.printStackTrace();
        }


        if (!ctClass.isFrozen() && ctClass.getName().startsWith(PREFIX)) {

            CtMethod[] declaredMethods = ctClass.getDeclaredMethods();
            System.out.println("Method size: " + declaredMethods.length + " for class: " + ctClass.getName());

            for (CtMethod declaredMethod : declaredMethods) {

//                System.out.println("Processing method: " + ctClass.getName() + "::" + declaredMethod.getName());

                int modifiers = declaredMethod.getModifiers();
                if (Modifier.isPublic(modifiers)) {

                    String declaredMethodName = declaredMethod.getName();
//                    System.out.println("*** ENHANCING METHOD: " + declaredMethodName);

                    try {

//                        {
//                            StringBuilder sb = new StringBuilder();
//                            sb.append("*** Method invoked: ").append("\"").append(declaredMethodName).append("\"");
//                            System.out.println(sb.toString());
//                        }

                        declaredMethod.insertBefore("  {\n" +
                                "StringBuilder sb = new StringBuilder(\"====== Method invoked:\");\n" +
                                " sb.append(" + "\"" + declaredMethodName + "\"" + ");" +
                                "System.out.println(sb.toString());\n" +
                                "}");


                    } catch (CannotCompileException e) {
                        e.printStackTrace();
                    }

                }
            }

            byte[] bytes = null;

            try {
                bytes = ctClass.toBytecode();
            } catch (IOException | CannotCompileException e) {
                e.printStackTrace();
            }
            return bytes;
        }

        return null;
    }

}
