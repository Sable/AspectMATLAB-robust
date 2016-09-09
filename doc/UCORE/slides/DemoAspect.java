import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class DemoAspect{
    @Pointcut("call(* DemoTarget.*(..))")
    public void pCall() {}

    @Pointcut("execution(* DemoTarget.*(..))")
    public void pExec() {}

    @Pointcut("pCall() || pExec()")
    public void pComp() {}

    @Before("pComp()")
    public void pAdvice(JoinPoint joinPoint) {
        System.out.println(String.format(
            "[line:%3d] %s",
            joinPoint.getSourceLocation().getLine(),
            joinPoint.toLongString()
        ));
    }
}