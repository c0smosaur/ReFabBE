package cleanie.repatch.common.log.proxy;

import cleanie.repatch.common.log.model.LoggingForm;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;

@RequiredArgsConstructor
public class ConnectionProxyHandler implements MethodInterceptor {

    private static final String JDBC_PREPARE_STATEMENT_METHOD_NAME = "prepareStatement";

    private final Object connection;
    private final LoggingForm loggingForm;

    @Nullable
    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        final Object result = invocation.proceed();
        if (hasConnection(result) && hasPreparedStatementInvoked(invocation)) {
            final ProxyFactory proxyFactory = new ProxyFactory(result);
            proxyFactory.addAdvice(new PreparedStatementProxyHandler(loggingForm));
            return proxyFactory.getProxy();
        }
        return result;
    }

    private boolean hasConnection(final Object result) {
        return result != null;
    }

    private boolean hasPreparedStatementInvoked(final MethodInvocation invocation) {
        return JDBC_PREPARE_STATEMENT_METHOD_NAME.equals(invocation.getMethod().getName());
    }

    public Object getProxy() {
        final ProxyFactory proxyFactory = new ProxyFactory(connection);
        proxyFactory.addAdvice(this);
        return proxyFactory.getProxy();
    }
}
