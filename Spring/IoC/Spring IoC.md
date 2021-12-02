# Spring IoC容器

## 1. 概述

​	在面向对象系统中，对象封装了数据和对数据的处理，对象的依赖关系可以通过把对象的依赖注入交给框架或IoC容器来完成，这种将具体对象手中交出控制的做法称为控制反转。

​	如此，对象之间的相互依赖关系由IoC容器进行管理，并由IoC容器完成对象的注入。如A依赖B，B依赖C，通过IoC容器，可以直接创建A，B,C将会自动创建。

​	依赖控制反转有很多实现方式，在Spring中，IoC是实现这个模式的载体

- 可以在对象生成或初始化时直接将数据注入到对象中
- 通过对象引用注入到对象数据域中的方式来注入对方法调用的依赖

​	当应用控制反转后，对象被创建时，**由一个调控系统内的所有对象的外界实体将其所依赖的对象的引用传递给它**，即依赖注入。	可以认为这个调控系统是IoC容器。

​	在面向对象系统中，大部分对象只是用来处理数据，这些对象并不常发生变化，以单件的形式起作用就可以满足应用的需求。

​	另外，对象之间的相互依赖关系也是比较稳定，一般不会随着应用的运行状态的改变而改变。

**IoC的应用场景**

1. 解耦组件之间复杂关系
2. 在开发中，需要引用和调用其他组件的服务，使用IoC可以去管理组件中的一些依赖服务

## 2. IoC容器系列的设计与实现：BeanFactory和ApplicationContext

​	IoC容器设计有两种主要容器系列：一种是实现BeanFactory接口的简单容器系列，这系列容器只实现了容器的最`基本`功能。另一个是ApplicationContext应用上下文，作为容器的`高级`形态存在。

### 2.1 Spring的IoC容器系列

​	IoC容器可以理解为一个水桶，出售的水桶有大有小，有金属的，塑料的等，但共同作用都是用来装水，根据不同场景可以选择不同的水桶。

​	SpringFramework的IoC核心就是其中一个“水桶”，它是开源的。对于IoC容器的使用者来说，平常接触的BeanFactory和ApplicationContext都可以看成容器的具体表现形式。

​	在Spring提供的基本IoC容器的接口定义和实现的基础上

![image-20211202152239569](image/image-20211202152239569.png)

​	Spring通过定义`BeanDefinition`来管理基于Spring的应用中的各种对象以及它们之间的相互依赖关系。

​	`BeanDefinition`抽象了我们对`Bean`的定义，是让容器起作用的主要数据类型。对于IoC容器来说，`BeanDefinition`就是对依赖反转模式中**管理的对象依赖关系**的数据抽象。

​	可以将`BeanDefinition`理解为木桶中的水，有了这些基本数据，容器才能够发挥作用。

![image-20211202152955856](image/image-20211202152955856.png)

### 2.2 Spring IoC容器的设计![image-20211202153011931](image/image-20211202153011931.png)

​	上图是IoC容器的接口设计图，下面对该图作一些简要分析：

- 接口`BeanFactory`=>接口`HierarchicalBeanFactory`，再=>`ConfigurableBeanFactory`，是一条主要的`BeanFactory`设计路径。

    1. 在`BeanFactory`中，定义了getBean()这样的基本方法，通过这个方法可以从容器中获取Bean
    2. `HierarchicalBeanFactory`接口继承了`BeanFactory`接口，增加了`getParentBeanFactory()`的接口功能，使`BeanFactory`具备了双亲IoC容器的管理功能
    3. `ConfigurableBeanFactory`定义了对`BeanFactory`的配置功能

- `ApplicationContext`应用上下文接口为核心：

    1. `BeanFactory`=>`ListableBeanFactory`=>`ApplicationContext`=>`WebApplicationContext`或`ConfigurableApplicationContxt`接口，这是第二条接口设计主线。

    2. 常用的应用上下文基本上都是`ConfigurableApplicationContext`或者`WebApplicationContext`的实现

    3. `ListableBeanFactory`和`HierarchicalBeanFactory`两个接口，连接BeanFactory接口定义和`ApplicationConext`应用上下文的接口定义。

    4. 在`ListableBeanFactory`接口中，细化了许多`BeanFactory`的接口功能，比如定义了`getBeanDefinitionNames`()接口方法

    5. 对于`HierarchicalBeanFactory`接口，我们在前文中已经提到过；对于`ApplicationContext`接口，它通过继承`MessageSource`、`ResourceLoader`、`ApplicationEventPublisher`接口，在`BeanFactory`简单IoC容器的基础上添加了许多对高级容器的特性的支持。

- 接口系统是以`BeanFactory`和`ApplicationContext`为核心

- `BeanFactory`又是IoC容器的最基本接口，在`ApplicationContext`的设计中，一方面，可以看到它继承了`BeanFactory`接口体系中的`ListableBeanFactory`、`AutowireCapableBeanFactory`、`HierarchicalBeanFactory`等`BeanFactory`的接口，具备了`BeanFactory IoC`容器的基本功能；另一方面，通过继承`MessageSource`、`ResourceLoadr`、`ApplicationEventPublisher`这些接口，`BeanFactory`为`ApplicationContext`赋予了更高级的IoC容器特性

- 对于`ApplicationContext`而言，为了在Web环境中使用它，还设计了`WebApplicationContext`接口，而这个接口通过继承`ThemeSource`接口来扩充功能。

#### 2.2.1 BeanFactory的应用场景

​	`BeanFactory`接口定义了IoC容器最基本的形式，并且提供了IoC容器所要遵守的基本的服务契约，同时，也是使用IoC容器所要遵守的最底层和最基本的编程规范。

​	用户在使用容器时，可以使用转义符“&”来得到`FactoryBean`本身，用来区分通过容器来获取`FactoryBean`产生的对象和获取`FactoryBean`本身。举例，`MyJndiObject`是一个`FactoryBean`，那么使用`&myJndiObject`得到的是`FactoryBean`，而不是`myJndiObject`这个`FactoryBean`产生的对象。

> FactoryBean和BeanFactory这两个在Spring中使用频率很高的类
>
> FactoryBean是一个能产生或者修饰对象生成的工厂Bean
>
> BeanFactory是IoC容器或者对象工厂

​	`BeanFactory`接口设计了`getBean`方法，可以通过指定名字来索引。同时，`Beanfactory`接口定义了带有参数`getBean()`方法，可以在获取Bean时对Bean的类型进行检查。

​	用户可以通过`BeanFactory`接口方法中的`getBean`来使用Bean名字，从而在获取Bean时，如果需要获取的Bean是prototype类型的，用户还可以为这个protorype类型的bean生成指定构造函数的对应参数。用了BeanFactory的定义，用户可以执行以下操作：

- 通过接口方法`containsBean`让用户能够判断容器是否含有指定名字的Bean
- 通过接口方法`isSimgleton`来查询指定名字的Bean是否是Singleton类型的Bean
- 通过接口方法isPrototype来查询指定名字的Bean是否是prototype类型的
- 通过接口方法isTypeMatch来查询指定了名字的Bean的Class类型是否是特定的Class类型。
- 通过接口方法getAliases来查询指定了名字的Bean的所有别名

```java
package org.springframework.beans.factory;

import org.springframework.beans.BeansException;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

public interface BeanFactory {
    	String FACTORY_BEAN_PREFIX = "&";
    
    	Object getBean(String name) throws BeansException;
    
    	<T> T getBean(String name, Class<T> requiredType) throws BeansException;
    
    	Object getBean(String name, Object... args) throws BeansException;
    
    	<T> T getBean(Class<T> requiredType) throws BeansException;
    
    	<T> T getBean(Class<T> requiredType, Object... args) throws BeansException;
    
    	<T> ObjectProvider<T> getBeanProvider(Class<T> requiredType);
    
    	<T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType);

   		boolean containsBean(String name);
    
    	boolean isSingleton(String name) throws NoSuchBeanDefinitionException;
    
    	boolean isPrototype(String name) throws NoSuchBeanDefinitionException;
    
    	boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException;

    	boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException;

    	@Nullable
		Class<?> getType(String name) throws NoSuchBeanDefinitionException;

    	@Nullable
		Class<?> getType(String name, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException;

    	String[] getAliases(String name);
}
```

#### 2.2.2 BeanFactory容器的设计原理

​	`BeanFactory`接口提供了使用IoC容器的规范，在此基础上，Spring还提供了符合这个IoC容器接口的一系列容器的实现供开发人员使用。下面以`XmlBeanFactory`的实现为例来说明简单IoC容器的设计原理![image-20211202162046733](image/image-20211202162046733.png)

​	`XmlBeanFacoty`继承自`DefaultListableBeanFactory`这个类，后者是作为一个默认的功能完整的IoC容器来使用的。`XmlBeanFactory`在继承的基础上又增加了新的功能，也就是可以读取xml文件的功能。

​	这种实现xml读取的功能是怎样实现的呢？首先，它并不是由`xmlBeanFactory`直接完成的，在`xmlBeanFactory`中，初始化了一个`xmlbeanDefinitionReader`对象，有了这个对象，那些以xml方式定义`BeanDefinition`就有了处理的地方。

​	构造xmlBeanFactory这个IoC容器时，需要指定`BeanDefinition`的信息来源，而这个信息来源需要封装成Spring中的`Resource`类来给出。`Resource`是`Spring`用来封装I/O操作的类。使用像“`ClassPath-Resource res =new ClassPathResource("beans.xml")`"这样具体的`ClassPathResource`来构造需要的`Resource`，然后将`Resource`作为构造参数传递给`XmlBeanFactory`构造函数。

```java
package org.springframework.beans.factory.xml;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.io.Resource;

@Deprecated
@SuppressWarnings({"serial", "all"})
public class XmlBeanFactory extends DefaultListableBeanFactory {

	private final XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(this);



	public XmlBeanFactory(Resource resource) throws BeansException {
		this(resource, null);
	}


	public XmlBeanFactory(Resource resource, BeanFactory parentBeanFactory) throws BeansException {
		super(parentBeanFactory);
		this.reader.loadBeanDefinitions(resource);
        // 调用启动从Resource中载入BeanDefinitions的过程
	}

}
```

​	以上是`xmlBeanFactory`的源码

​	`DefaultListableBeanFactory`是很重要的一个IoC实现，在其他IoC容器中，比如`ApplicationContext`，其实现的基本原理和`xmlBeanFactory`一样。

​	    编程式使用IoC容器

```java
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;

public class MyTest {
    public static void main(String[] args) {
        // 1. 创建IoC配置文件的抽象资源，包含了BeanDefinition的定义信息
        ClassPathResource res = new ClassPathResource("beans.xml");
        // 2. 创建一个BeanFactory
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
        // 创建一个载入Beandefinition的读取器
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
        // 从定义好的资源位置读入配置信息
        reader.loadBeanDefinitions(res);
        // 最终会载入和注册到Bean中
    }
}
```

#### 2.2.3 ApplicationContext的应用场景

​	`ApplicationContext`除了能够提供前面介绍的容器的基本功能外，还为用户提供了以下附加服务。

- 支持不同的信息源。扩展了`MessageSource`接口
- 访问资源。体现在`ResourceLoader`和`Resource`的支持上，这样就可以从不同地方得到Bean定义资源
- 支持应用事件，继承了`ApplicationEventPublisher`，从而在上下文中引入事件机制。

`ApplicationContext`比简单`BeanFactory`相比，使用的是一种面向框架的使用风格，推荐使用

#### 2.2.4 ApplicationContext容器的设计原理

​	下面以`FileSystemXmlApplicationContext`的实现为例来介绍`ApllicationContext`容器的设计原理

​	然而，`ApplicationContext`应用上下文的主要功能已经在`FileSystemXmlApplicationContext`的基类`AbstractXmlApplicationContext`中实现了

​    作为一个具体的应用上下文，需要实现与设计相关的两个功能

1. 如果应用直接使用`FileSystemXmlApplicationContext`,对于实例化这个应用上下文的支持，同时启动IoC容器的`refresh()`过程。

    ```java
    public FileSystemXmlApplicationContext(
        String[] configLocations, boolean refresh, @Nullable ApplicationContext parent)
        throws BeansException {
    
        super(parent);
        setConfigLocations(configLocations);
        if (refresh) {
            refresh();
        }
    }
    ```

    这个`refresh()`过程会牵扯到IoC容器启动的一系列复杂操作。

2. 与`FileSystemXmlApplicationContext`设计具体相关的功能，这部分与怎样从文件系统中加载XML的Bean定义资源有关。

    通过这个过程，可以为在文件系统中读取以XML形式存在的`BeanDefinition`做准备，因为不同的应用上下文实现对应着不同的读取`BeanDefinition`的方式

    ```java
    @Override
    protected Resource getResourceByPath(String path) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return new FileSystemResource(path);
    }
    ```

    这个方法，可以得到`FileSystemResource的资源定位`

    ## 3. IoC容器的初始化过程

​	简单来说，IoC容器的初始化是由前面说的`refresh()`来启动的

​	启动包含`BeanDefination`的`Resource`定位，载入和注册三个基本过程

​	Spring将这三个过程分开，并使用不同的模块来完成。

**第一个过程是Resource定位过程**

​	`BeanDefinition`的资源定位，由`ResourceLoader`通过统一的`Resource`接口来完成，这个`Resource`对各种形式的`BeanDefinition`的使用都提供了统一接口。

​	对于`BeanDefinition`的存在形式，就比如，文件系统中的Bean定义信息可以使用`FileSystemResource`来进行抽象；在类路径中的Bean定义信息可以使用`ClassPathResource`。

​	定位过程就类似于容器寻找数据的过程，就像用水桶装水先要把水找到一样

**第二个过程是BeanDefinition的载入**

​	这个载入过程是把用户定义好的Bean表示成IoC容器内部的数据结构，而这个容器内部的数据结构就是`BeanDefinition`。

​	具体来说，这个`BeanDefinition`实际就是POJO对象在IoC容器中的抽象，通过这个`BeanDefinition`定义的数据结构，使IoC容器能够方便地对POJO对象也就是Bean进行管理。

**第三个过程是向IoC容器注册这些BeanDefinition**

​	调用`BeanDefinitionRegistry`接口地实现来完成的。把载入过程中解析得到的`BeanDefinition`向IoC容器进行注册。会注入到一个`HashMap`中去。IoC容器就是通过这个`HashMap`来持有这些`BeanDefinition`数据的。

---

​	在IoC容器初始化过程，一般包含Bean依赖注入的实现，Bean定义的载入和依赖注入是两个独立的过程。

​	依赖注入一般发生在应用第一次通过`getBean`向容器索取Bean的时候，但如果用户设置了`lazyinit`属性，会导致Bean的依赖注入在IoC容器初始化时就完成了。

### 3.1 BeanDefinition的Resource定位

​	以下代码使用ClassPathResource，这意味着Spring会在类路径中去寻找以文件形式存在的`BeanDefinition`信息。

```java
ClassPathResource classPathResource = new ClassPathResource("beans.xml");
```

​	这里的`Resource`并不能被`DefaultListableBeanFactory`直接使用，Spring通过`BeanDefinitionReader`来对这些信息进行处理。

​	这里也可以使用`ApplicationContext`，相对于`DefaultListableBeanFactory`，它可以提供一系列加载不同`Resource`的读取器的实现。而`DefaultListableBeanFactory`只是一个纯粹的IoC容器，需要特定的读取器才能完成这些功能。

​	`ApplicationContext`比`BeanFactory`封装的更深

---

​	下面以`FileSystemXmlApplicationContext`为例，看看`ApplicationContext`是如何完成`Resource`定位过程的。![image-20211202183625637](image/image-20211202183625637.png)

​		可以看到，`FileSystemXmlApplicationContext`已经通过继承`Abstract-ApplicationContext`具备了`ResourceLoader`读入以`Resource`定义的`BeanDefinition`的能力

```java
package org.springframework.context.support;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

public class FileSystemXmlApplicationContext extends AbstractXmlApplicationContext {

	public FileSystemXmlApplicationContext() {
	}

	public FileSystemXmlApplicationContext(ApplicationContext parent) {
		super(parent);
	}
	
    // 这个构造函数的configLocation包含的时BeanDefinition所在的文件路径
	public FileSystemXmlApplicationContext(String configLocation) throws BeansException {
		this(new String[] {configLocation}, true, null);
	}
	
    // 这个构造函数允许configLocations包含多个BeanDefinition所在的文件路径
	public FileSystemXmlApplicationContext(String... configLocations) throws BeansException {
		this(configLocations, true, null);
	}
	
    // 这个构造函数允许configLocations包含多个BeanDefinition所在的文件路径
    // 还允许指定自己的双亲IoC容器
	public FileSystemXmlApplicationContext(String[] configLocations, ApplicationContext parent) throws BeansException {
		this(configLocations, true, parent);
	}

	public FileSystemXmlApplicationContext(String[] configLocations, boolean refresh) throws BeansException {
		this(configLocations, refresh, null);
	}
	
    // 在对象的初始化过程中，调用refresh函数载入BeanDefinition，这个refresh启动
	public FileSystemXmlApplicationContext(
			String[] configLocations, boolean refresh, @Nullable ApplicationContext parent)
			throws BeansException {

		super(parent);
		setConfigLocations(configLocations);
		if (refresh) {
			refresh();
		}
	}

	// 这是应用于文件系统中Resource的实现，通过构造一个FileSystemResource来得到一个在文件系统中定位的BeanDefnition
    // 则个getResourceByPath是在BeanDefinitionReader的loadBeanDefinition中被调用的
    // loadBeanDefinition采用了模板模式，具体的定位实现实际上是由各个子类来完成的
	@Override
	protected Resource getResourceByPath(String path) {
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		return new FileSystemResource(path);
	}

}
```

​	在`FileSystemApplicationContext`,实现了对`configuration`进行处理的功能，让所有配置在文件系统中的，以XML文件方式存在的`BeanDefnition`都能够得到有效的处理

​	比如，实现了`getResourceByPath`方法，这个方法是一个模板方法，是为读取`Resource`服务的。

​    在构造函数中通过refresh来启动IoC容器的初始化，这个refresh方法非常重要

​    ![image-20211202190609404](image/image-20211202190609404.png)

​    上图是`getResourceByPath`的调用过程

​	上面大概是说怎么定位到资源文件的，那么要如何完成`BeanDefinition`信息的读入呢？

​    在IoC容器的初始化过程中，BeanDefinition资源的定位、读入和注册过程是分开进行的

​    关于这个读入器的配置，可以到`FileSystemXmlApplicationContext`的基类`AbstractRefreshableApplicationContext`中看看它是怎样实现的。

​	主要先来看看`AbstractRefreshableApplicationContext`的`refreshBeanFactory`方法的实现

```java
@Override
protected final void refreshBeanFactory() throws BeansException {
    if (hasBeanFactory()) {
        destroyBeans();
        closeBeanFactory();
    }
    try {
        //构建了一个IoC容器供ApplicationContext使用。这个IoC容器就是我们前面提到过的DefaultListableBeanFactory
        DefaultListableBeanFactory beanFactory = createBeanFactory();
        beanFactory.setSerializationId(getId());
        customizeBeanFactory(beanFactory);
        //启动了loadBeanDefinitions来载入BeanDefinition
        loadBeanDefinitions(beanFactory);
        this.beanFactory = beanFactory;
    }
    catch (IOException ex) {
        throw new ApplicationContextException("I/O error parsing bean definition source for " + getDisplayName(), ex);
    }
}
```

​	通过IoC容器的初始化的`refersh`来启动整个调用，使用的IoC容器是`DefultListableBeanFactory`,具体的资源载入在`XmlBeanDefinitionReader`读入`BeanDefinition`时完成，在`XmlBeanDefinitionReader`的基类`AbstractBeanDefinitionReader`中可以看到这个载入过程的具体实现。

   ```java
   /**
   * AbstractRefreshableApplicationContext中
   这是上下文中创建DefaultListableBeanFactory的地方，getInternalParentBeanFactory
   的具体实现查看AbstractApplicationContext中的实现，根据容器已有的双亲IoC容器信息来生成
   */
   protected DefaultListableBeanFactory createBeanFactory() {
       return new DefaultListableBeanFactory(getInternalParentBeanFactory());
   }
   
   /**
   * AbstractRefreshableApplicationContext中
   
   这是使用BeanDefinitionReader载入Bean定义的地方
   */
   protected abstract void loadBeanDefinitions(DefaultListableBeanFactory beanFactory)
       throws BeansException, IOException;
   
   /**
   * AbstractBeanDefinitionReader类中
   */
   public int loadBeanDefinitions(String location, @Nullable Set<Resource> actualResources) throws BeanDefinitionStoreException {
       ResourceLoader resourceLoader = getResourceLoader();
       if (resourceLoader == null) {
           throw new BeanDefinitionStoreException(
               "Cannot load bean definitions from location [" + location + "]: no ResourceLoader available");
       }
   
       if (resourceLoader instanceof ResourcePatternResolver) {
           // Resource pattern matching available.
           try {
               Resource[] resources = ((ResourcePatternResolver) resourceLoader).getResources(location);
               int count = loadBeanDefinitions(resources);
               if (actualResources != null) {
                   Collections.addAll(actualResources, resources);
               }
               if (logger.isTraceEnabled()) {
                   logger.trace("Loaded " + count + " bean definitions from location pattern [" + location + "]");
               }
               return count;
           }
           catch (IOException ex) {
               throw new BeanDefinitionStoreException(
                   "Could not resolve bean definition resource pattern [" + location + "]", ex);
           }
       }
       else {
           // Can only load single resources by absolute URL.
           Resource resource = resourceLoader.getResource(location);
           int count = loadBeanDefinitions(resource);
           if (actualResources != null) {
               actualResources.add(resource);
           }
           if (logger.isTraceEnabled()) {
               logger.trace("Loaded " + count + " bean definitions from location [" + location + "]");
           }
           return count;
       }
   }
   
   
   /**
   * DefaultResourceLoader类中
   */
   @Override
   public Resource getResource(String location) {
       Assert.notNull(location, "Location must not be null");
   
       for (ProtocolResolver protocolResolver : this.protocolResolvers) {
           Resource resource = protocolResolver.resolve(location, this);
           if (resource != null) {
               return resource;
           }
       }
   
       if (location.startsWith("/")) {
           return getResourceByPath(location);
       }
       else if (location.startsWith(CLASSPATH_URL_PREFIX)) {
           return new ClassPathResource(location.substring(CLASSPATH_URL_PREFIX.length()), getClassLoader());
       }
       else {
           try {
               // Try to parse the location as a URL...
               URL url = new URL(location);
               return (ResourceUtils.isFileURL(url) ? new FileUrlResource(url) : new UrlResource(url));
           }
           catch (MalformedURLException ex) {
               // No URL -> resolve as resource path.
               return getResourceByPath(location);
           }
       }
   }
   ```

​	`getResourceByPath`会被子类`FileSystemXmlApplicationContext`实现，这个方法返回的是一个`FileSystemResource`对象，通过这个对象，Spring可以进行相关的I/O操作，完成`BeanDefinition`的定位。

```java
@Override
protected Resource getResourceByPath(String path) {
    if (path.startsWith("/")) {
        path = path.substring(1);
    }
    return new FileSystemResource(path);
}
```

---

​	在`BeanDefinition`定位完成的基础上，就可以通过返回的`Resource`对象来进行`BeanDefinition`的载入了。

​	在定位过程完成以后，为`BeanDefinition`的载入创造了I/O操作的条件，但是具体的数据还没有开始读入。

​    这里就像用水桶去打水，要先找到水源。这里完成对`Resource`的定位，就类似于水源已经找到了，下面就是打水的过程了

   ### 3.2 BeanDefinition的载入和解析

​	这个载入过程，相当于把定义的`BeanDefinition`在IoC容器中转化成一个Spring内部表示的数据结构的过程。

​	IoC容器对Bean的管理和依赖注入功能的实现，是通过对其持有的`BeanDefinition`进行各种相关操作来完成的。

​	这些BeanDefinition数据在IoC容器中通过一个`HashMap`来保持和维护。

​	下面，从`DefaultListableBeanFactory`的设计入手，看看IoC容器是怎样完成`BeanDefinition`载入的。

​	首先，先回到`refresh`方法，它的调用标志着容器初始化的开始，这些初始化对象就是BeanDefinition数据

```java
public FileSystemXmlApplicationContext(
    String[] configLocations, boolean refresh, @Nullable ApplicationContext parent)
    throws BeansException {

    super(parent);
    setConfigLocations(configLocations);
    if (refresh) {
        refresh();
    }
}
```

下面介绍下它的实现，该方法在`AbstractApplicationContext`类，	

```java
@Override
public void refresh() throws BeansException, IllegalStateException {
    synchronized (this.startupShutdownMonitor) {
        StartupStep contextRefresh = this.applicationStartup.start("spring.context.refresh");

        // Prepare this context for refreshing.
        prepareRefresh();
		// 这里是在子类中启动refreshBeanFactory()的地方
        // Tell the subclass to refresh the internal bean factory.
        ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();

        // Prepare the bean factory for use in this context.
        prepareBeanFactory(beanFactory);

        try {
            // 设置beanfactory的后置处理
            // Allows post-processing of the bean factory in context subclasses.
            postProcessBeanFactory(beanFactory);
			
            StartupStep beanPostProcess = this.applicationStartup.start("spring.context.beans.post-process");
            // 调用beanfactory的后处理器，这些后处理器是在bean定义中向容器注册的
            // Invoke factory processors registered as beans in the context.
            invokeBeanFactoryPostProcessors(beanFactory);

            // Register bean processors that intercept bean creation.
            registerBeanPostProcessors(beanFactory);
            beanPostProcess.end();

            // Initialize message source for this context.
            // 对上下文中的消息源进行初始化
            initMessageSource();

            // Initialize event multicaster for this context.
            // 初始化上下文中的事件机制
            initApplicationEventMulticaster();

            // Initialize other special beans in specific context subclasses.
            // 初始化其他特殊的bean
            onRefresh();

            // Check for listener beans and register them.
            // 检查监听bean并且将这些bean向容器注册
            registerListeners();

            // Instantiate all remaining (non-lazy-init) singletons.
            // 实例化所有的(non-lazy-init)单件
            finishBeanFactoryInitialization(beanFactory);
			
            // Last step: publish corresponding event.
            // 发布容器事件，结束refresh过程
            finishRefresh();
        }

        catch (BeansException ex) {
            // 为防止Bean资源占用，在异常处理中，销毁已经在前面过程生成的单件bean
            if (logger.isWarnEnabled()) {
                logger.warn("Exception encountered during context initialization - " +
                            "cancelling refresh attempt: " + ex);
            }

            // Destroy already created singletons to avoid dangling resources.
            destroyBeans();

            // Reset 'active' flag.
            cancelRefresh(ex);

            // Propagate exception to caller.
            throw ex;
        }

        finally {
            // Reset common introspection caches in Spring's core, since we
            // might not ever need metadata for singleton beans anymore...
            resetCommonCaches();
            contextRefresh.end();
        }
    }
}
```





