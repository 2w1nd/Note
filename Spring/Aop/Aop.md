拦截器的运行

```java
@Override
@Nullable
public Object proceed() throws Throwable {
    //	We start with an index of -1 and increment early.
    // 从索引为-1的拦截器开始调用，并按序递增
    // 如果拦截器中的拦截器迭代调用完成，这里开始调用target的函数
    if (this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size() - 1) {
        return invokeJoinpoint();
    }
	
    // interceptorOrInterceptionAdvice是获得的拦截器，它通过拦截器机制对目标对象的行为增强起作用
    Object interceptorOrInterceptionAdvice =
        this.interceptorsAndDynamicMethodMatchers.get(++this.currentInterceptorIndex);
    if (interceptorOrInterceptionAdvice instanceof InterceptorAndDynamicMethodMatcher) {
        // 这里对拦截器进行动态匹配的判断
        // Evaluate dynamic method matcher here: static part will already have
        // been evaluated and found to match.
        // 触发进行匹配的地方，如果和定义的Pointcut匹配，那么这个advice将会得到执行
        InterceptorAndDynamicMethodMatcher dm =
            (InterceptorAndDynamicMethodMatcher) interceptorOrInterceptionAdvice;
        Class<?> targetClass = (this.targetClass != null ? this.targetClass : this.method.getDeclaringClass());
        if (dm.methodMatcher.matches(this.method, targetClass, this.arguments)) {
            return dm.interceptor.invoke(this);
        }
        else {
            // Dynamic matching failed.
            // Skip this interceptor and invoke the next in the chain.
            // 如果不匹配，则proceed会被递归调用，直到所有拦截器都被运行过为止
            return proceed();
        }
    }
    else {
        // 如果是一个interceptor，直接调用这个interceptor对应的方法
        // It's an interceptor, so we just invoke it: The pointcut will have
        // been evaluated statically before this object was constructed.
        return ((MethodInterceptor) interceptorOrInterceptionAdvice).invoke(this);
    }
}
```



**DefaultAdvisorChainFactory生成拦截器链**

```java
@Override
public List<Object> getInterceptorsAndDynamicInterceptionAdvice(
    Advised config, Method method, @Nullable Class<?> targetClass) {

    // This is somewhat tricky... We have to process introductions first,
    // but we need to preserve order in the ultimate list.
    // advisor链已经在config中持有了，这里可以直接调用
    AdvisorAdapterRegistry registry = GlobalAdvisorAdapterRegistry.getInstance();
    Advisor[] advisors = config.getAdvisors();
    List<Object> interceptorList = new ArrayList<>(advisors.length);
    Class<?> actualClass = (targetClass != null ? targetClass : method.getDeclaringClass());
    Boolean hasIntroductions = null;

    for (Advisor advisor : advisors) {
        if (advisor instanceof PointcutAdvisor) {
            // Add it conditionally.
            PointcutAdvisor pointcutAdvisor = (PointcutAdvisor) advisor;
            if (config.isPreFiltered() || pointcutAdvisor.getPointcut().getClassFilter().matches(actualClass)) {
                // 拦截器链是通过AdvisorAdapterRegistry来加入的，这个AdvisorAdpterRegistry
                // 对advice织入起了很大的作用
                MethodMatcher mm = pointcutAdvisor.getPointcut().getMethodMatcher();
                boolean match;
                if (mm instanceof IntroductionAwareMethodMatcher) {
                    if (hasIntroductions == null) {
                        hasIntroductions = hasMatchingIntroductions(advisors, actualClass);
                    }
                    match = ((IntroductionAwareMethodMatcher) mm).matches(method, actualClass, hasIntroductions);
                }
                else {
                    match = mm.matches(method, actualClass);
                }
                if (match) {
                    MethodInterceptor[] interceptors = registry.getInterceptors(advisor);
                    if (mm.isRuntime()) {
                        // Creating a new object instance in the getInterceptors() method
                        // isn't a problem as we normally cache created chains.
                        for (MethodInterceptor interceptor : interceptors) {
                            interceptorList.add(new InterceptorAndDynamicMethodMatcher(interceptor, mm));
                        }
                    }
                    else {
                        interceptorList.addAll(Arrays.asList(interceptors));
                    }
                }
            }
        }
        else if (advisor instanceof IntroductionAdvisor) {
            IntroductionAdvisor ia = (IntroductionAdvisor) advisor;
            if (config.isPreFiltered() || ia.getClassFilter().matches(actualClass)) {
                Interceptor[] interceptors = registry.getInterceptors(advisor);
                interceptorList.addAll(Arrays.asList(interceptors));
            }
        }
        else {
            Interceptor[] interceptors = registry.getInterceptors(advisor);
            interceptorList.addAll(Arrays.asList(interceptors));
        }
    }

    return interceptorList;
}

// 判断Advisors是否符合配置要求
private static boolean hasMatchingIntroductions(Advisor[] advisors, Class<?> actualClass) {
    for (Advisor advisor : advisors) {
        if (advisor instanceof IntroductionAdvisor) {
            IntroductionAdvisor ia = (IntroductionAdvisor) advisor;
            if (ia.getClassFilter().matches(actualClass)) {
                return true;
            }
        }
    }
    return false;
}
```



**在拦截器链的初始化中获取advisor通知器**

```java
// ProxyFactoryBean类中
@Override
@Nullable
public Object getObject() throws BeansException {
    initializeAdvisorChain();
    if (isSingleton()) {
        return getSingletonInstance();
    }
    else {
        if (this.targetName == null) {
            logger.info("Using non-singleton proxies with singleton targets is often undesirable. " +
                        "Enable prototype proxies by setting the 'targetName' property.");
        }
        return newPrototypeInstance();
    }
}

private synchronized void initializeAdvisorChain() throws AopConfigException, BeansException {
    if (this.advisorChainInitialized) {
        return;
    }

    if (!ObjectUtils.isEmpty(this.interceptorNames)) {
        if (this.beanFactory == null) {
            throw new IllegalStateException("No BeanFactory available anymore (probably due to serialization) " +
                                            "- cannot resolve interceptor names " + Arrays.asList(this.interceptorNames));
        }

        // Globals can't be last unless we specified a targetSource using the property...
        if (this.interceptorNames[this.interceptorNames.length - 1].endsWith(GLOBAL_SUFFIX) &&
            this.targetName == null && this.targetSource == EMPTY_TARGET_SOURCE) {
            throw new AopConfigException("Target required after globals");
        }

        // Materialize interceptor chain from bean names.
        for (String name : this.interceptorNames) {
            if (logger.isTraceEnabled()) {
                logger.trace("Configuring advisor or advice '" + name + "'");
            }

            if (name.endsWith(GLOBAL_SUFFIX)) {
                if (!(this.beanFactory instanceof ListableBeanFactory)) {
                    throw new AopConfigException(
                        "Can only use global advisors or interceptors with a ListableBeanFactory");
                }
                addGlobalAdvisor((ListableBeanFactory) this.beanFactory,
                                 name.substring(0, name.length() - GLOBAL_SUFFIX.length()));
            }

            else {
                // 需要对Bean的类型进行判断，是单例类型还是prototype类型
                // If we get here, we need to add a named interceptor.
                // We must check if it's a singleton or prototype.
                Object advice;
                if (this.singleton || this.beanFactory.isSingleton(name)) {
                    // 取到advisor的地方，是通过beanFactory取得到的
                    // 把interceptorName这个List中的interceptor名字
                    // 交给beanFactory，然后调用BeanFactory的getBean去获取
                    // Add the real Advisor/Advice to the chain.
                    advice = this.beanFactory.getBean(name);
                }
                else {
                    // 如果bean的类型是prototype类型
                    // It's a prototype Advice or Advisor: replace with a prototype.
                    // Avoid unnecessary creation of prototype bean just for advisor chain initialization.
                    advice = new PrototypePlaceholderAdvisor(name);
                }
                addAdvisorOnChainCreation(advice, name);
            }
        }
    }

    this.advisorChainInitialized = true;
}
```



**DefaultAdvisorChainFactory使用GlobalAdvisorAdapterRegistry得到AOP拦截器(DefaultAdvisorChainFactory生成拦截器链截取)**

```java
// 得到注册器GlobalAdvisorAdapterRegistry，是一个单例模式的实现
AdvisorAdapterRegistry   registry = GlobalAdvisorAdapterRegistry.getInstance();
Advisor[] advisors = config.getAdvisors();
List<Object> interceptorList = new ArrayList<>(advisors.length);
Class<?> actualClass = (targetClass != null ? targetClass : method.getDeclaringClass());
Boolean hasIntroductions = null;

for (Advisor advisor : advisors) {
    if (advisor instanceof PointcutAdvisor) {
        // Add it conditionally.
        PointcutAdvisor pointcutAdvisor = (PointcutAdvisor) advisor;
        if (config.isPreFiltered() || pointcutAdvisor.getPointcut().getClassFilter().matches(actualClass)) {
            // 从GlobalAdvisorAdapterRegistry中取得MethodInterceptor的实现
            // 拦截器链是通过AdvisorAdapterRegistry来加入的，这个AdvisorAdpterRegistry
            // 对advice织入起了很大的作用
            MethodMatcher mm = pointcutAdvisor.getPointcut().getMethodMatcher();
            boolean match;
            if (mm instanceof IntroductionAwareMethodMatcher) {
                if (hasIntroductions == null) {
                    hasIntroductions = hasMatchingIntroductions(advisors, actualClass);
                }
                match = ((IntroductionAwareMethodMatcher) mm).matches(method, actualClass, hasIntroductions);
            }
            else {
                match = mm.matches(method, actualClass);
            }
            if (match) {
                MethodInterceptor[] interceptors = registry.getInterceptors(advisor); // <====封装着advice织入实现的入口
                if (mm.isRuntime()) {
                    // 在getInterceptors()方法中创建新的对象实例
                    // Creating a new object instance in the getInterceptors() method
                    // isn't a problem as we normally cache created chains.
                    for (MethodInterceptor interceptor : interceptors) {
                        interceptorList.add(new InterceptorAndDynamicMethodMatcher(interceptor, mm));
                    }
                }
                else {
                    interceptorList.addAll(Arrays.asList(interceptors));
                }
            }
        }
    }
```



**GlobalAdvisorAdapterRegistry的实现**

```java
public final class GlobalAdvisorAdapterRegistry {

	private GlobalAdvisorAdapterRegistry() {
	}


	/**
	 * Keep track of a single instance so we can return it to classes that request it.
	 * 单例模式，静态类变量来保持一个唯一实例、
	 */
	private static AdvisorAdapterRegistry instance = new DefaultAdvisorAdapterRegistry();

	/**
	 * Return the singleton {@link DefaultAdvisorAdapterRegistry} instance.
	 */
	public static AdvisorAdapterRegistry getInstance() {
		return instance;
	}

	/**
	 * Reset the singleton {@link DefaultAdvisorAdapterRegistry}, removing any
	 * {@link AdvisorAdapterRegistry#registerAdvisorAdapter(AdvisorAdapter) registered}
	 * adapters.
	 */
	static void reset() {
		instance = new DefaultAdvisorAdapterRegistry();
	}

}
```



```java
public class DefaultAdvisorAdapterRegistry implements AdvisorAdapterRegistry, Serializable {
	//持有一个AdvisorAdapter的List，这个List中Adpter是与实现Spring AOP的advice增强功能相对应的
    private final List<AdvisorAdapter> adapters = new ArrayList<>(3);

    /**
	 * Create a new DefaultAdvisorAdapterRegistry, registering well-known adapters.
	 * 把已有的advice实现的Adpter加入进来
	 */
    public DefaultAdvisorAdapterRegistry() {
        registerAdvisorAdapter(new MethodBeforeAdviceAdapter());
        registerAdvisorAdapter(new AfterReturningAdviceAdapter());
        registerAdvisorAdapter(new ThrowsAdviceAdapter());
    }


    @Override
    public Advisor wrap(Object adviceObject) throws UnknownAdviceTypeException {
        if (adviceObject instanceof Advisor) {
            return (Advisor) adviceObject;
        }
        if (!(adviceObject instanceof Advice)) {
            throw new UnknownAdviceTypeException(adviceObject);
        }
        Advice advice = (Advice) adviceObject;
        if (advice instanceof MethodInterceptor) {
            // So well-known it doesn't even need an adapter.
            return new DefaultPointcutAdvisor(advice);
        }
        for (AdvisorAdapter adapter : this.adapters) {
            // Check that it is supported.
            if (adapter.supportsAdvice(advice)) {
                return new DefaultPointcutAdvisor(advice);
            }
        }
        throw new UnknownAdviceTypeException(advice);
    }
	//这里是在DefaultAdvisorChainFactory中启动的getInterceptors方法
    @Override
    public MethodInterceptor[] getInterceptors(Advisor advisor) throws UnknownAdviceTypeException {
        List<MethodInterceptor> interceptors = new ArrayList<>(3);
        // 从Advisor通知器配置中取得advice通知
        Advice advice = advisor.getAdvice();
        // 如果通知是MethodInterceptor类型的通知，直接加入interceptors的List中，不需要适配
        if (advice instanceof MethodInterceptor) {
            interceptors.add((MethodInterceptor) advice);
        }
        // 对通知进行适配，使用已经配置好的Adapter,然后从对应的adapter中取出封装好AOP编织功能的拦截器
        for (AdvisorAdapter adapter : this.adapters) {
            if (adapter.supportsAdvice(advice)) {
                interceptors.add(adapter.getInterceptor(advisor));
            }
        }
        if (interceptors.isEmpty()) {
            throw new UnknownAdviceTypeException(advisor.getAdvice());
        }
        return interceptors.toArray(new MethodInterceptor[0]);
    }

    @Override
    public void registerAdvisorAdapter(AdvisorAdapter adapter) {
        this.adapters.add(adapter);
    }
}
```

