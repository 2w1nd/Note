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

/**
    * 从提供的配置实例config中获取advisor列表,遍历处理这些advisor.如果是IntroductionAdvisor,
    * 则判断此Advisor能否应用到目标类targetClass上.如果是PointcutAdvisor,则判断
    * 此Advisor能否应用到目标方法method上.将满足条件的Advisor通过AdvisorAdaptor转化成Interceptor列表返回.
	*/
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

[(82条消息) Spring AOP 实现原理_KevinJom的专栏-CSDN博客_aop原理](https://blog.csdn.net/MoreeVan/article/details/11977115#:~:text=一、对 AOP 的初印象  首先先给出一段比较专业的术语（来自百度）：  在软件业， AOP,AOP 可以对业务逻辑 的各个部分进行隔离，从而... Spring AOP IOC 实现原理 ，面试问到如何回答.)

> 面向对象编程更多的操作是在纵向部分(即继承，接口实现之类)，这就导致一些需要在横向上(即业务代码方法中的前后)嵌入的非核心代码得在每一个方法上都要去写(比如日志，权限，异常处理等）。它们散布在各方法的横切面上，造成代码重复，也不利于各个模块的重用（毕竟，不同方法还是有所区别）。 AOP就是为了解决这种男题而生的 从AOP这个英文缩写来看就好。。。A是一把刀，把P的突出部分切出来（类比于围绕方法设定的日志，权限等需求，它们都是属于核心方法外的通用服务），它们有一个共性----圆溜溜的（就像一个工具箱中的扳手，钳子，螺丝刀之类的），所以能把它们集合成一块儿（它们都具有’工具‘的属性），就是中间的O。重新给接回去的时候，就着不同的需求，用O中不同的工具就好(通过不同的方法或注解指明)。 概念陈列： 目标对象，AOP代理对象，连接点，切入点，拦截器，通知，织入， 假设有一个对象A（目标对象），外部的请求人B要想访问到A，需要通过一个安检过程(连接点，比如验证权限m1，登录密码m2，身份识别m3等)。B开始访问后，首先得经过第一层的安检（准备走谁（introductionInterceptor）的哪一层安检（PointCut--》指定到具体的安检流程），由你定义的interceptor拦截器决定），即权限验证m1(切入点)。通过这一层后，监控整个访问过程的你可以决定是否要向大家伙儿通报外部请求的访问情况【像：B那孙子进来啦 OR B那孙子带着贪玩蓝月系来嘞 OR B那小子是渣渣辉的部下】(在访问开始前，还是结束后，还是全程播报---->这就是’通知‘)。于A而言，他觉得直接跟B接触可能不太安全，所以A把自己的一些权限给到了代理对象Proxy_A，并让Proxy_A去正面’刚（也即织入，A间接的给自己加持了一副铠甲）‘B(或许是来者不善乜)。Proxy_A是怎样产生的呢？这就是AOP动态代理的辅助了。简单来讲，不论你是什么代理---》Proxy_某个目标对象，只要是通过JDK或者CGLib的代理副本传送门（类比于抽象）进入到刚B的’对战场景‘中，那么，他都算是A（或者其他目标对象）的代言人。



Spring使用JDKProxy和Cglib来生成代理对象。默认的策略是如果目标类是接口，则使用JDK动态代理技术，否则使用Cglib来生成代理。

生成的代理对象的方法调用会委任到invocationHandler.invoke()方法，该方法的主要流程是：获取可以应用到方法上的通知链，如果有，则应用通知，并执行连接点；没有，则直接放射执行joinpoint

通知链通过Advised.getInterceptorsAndDynamicInterceptionAdvice()这个方法来获取的，实际的获取工作其实是由AdvisorChainFactory. getInterceptorsAndDynamicInterceptionAdvice()。获取到的结果会被缓存

执行完上述的方法后，Advised中配置能够应用到连接点或者目标类的Advisor全部被转化成了MethodInterceptor.

那么拦截器链是如何起作用的？

如果得到的拦截器链为空，则直接反射调用目标方法，否则创建MethodInvocation，调用其proceed方法，触发拦截器链的执行



.
