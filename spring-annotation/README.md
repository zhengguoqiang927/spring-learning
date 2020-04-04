# Spring注解驱动

## IOC

### 组件注册

#### @Configuration 

用于标注配置类

#### @ComponentScan

用于组件扫描，常用属性如下：

- String[]  value() default {}:指定要扫描的包，实际上是basePackages的别名

- String[]  basePackages() default {}:指定要扫描的包

- boolean useDefaultFilters() default true:表示将会自动检测标注@Component、@Repository、@Service、@Controller的类

- Filter[] includeFilters() default {}:除了useDefaultFilters指定的之外，满足指定过滤器规则的类也将被扫描

- Filter[] excludeFilters() default {}:指定哪些类型不进行扫描

例子：

```java
@ComponentScan(value = "com.zhengguoqiang",excludeFilters = {
				@ComponentScan.Filter(type = FilterType.ANNOTATION,classes = {Controller.class,Service.class})
})
```

类型过滤器的枚举值：

FilterType.ANNOTATION：基于注解

FilterType.ASSIGNABLE_TYPE：基于给定的类型

FilterType.ASPECTJ：基于Aspectj表达式

FilterType.REGEX：基于正则表达式

FilterType.CUSTOM：自定义规则，需要实现org.springframework.core.type.filter.TypeFilter接口

```java
@ComponentScan(includeFilters = {
				@ComponentScan.Filter(classes = {Controller.class,Service.class}), //type默认FilterType.ANNOTATION
				@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE，classes = {BookService.class}),
    			@ComponentScan.Filter(type=FilterType.CUSTOM,classes = {MyTypeFilter.class})
},useDefaultFilters = false)
```

```java
public class MyTypeFilter implements TypeFilter {

    /**
     *
     * @param metadataReader 当前目标类的类元数据信息Reader类
     * @param metadataReaderFactory 获取指定类的元数据信息Reader类
     * @return
     * @throws IOException
     */
    @Override
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
        //获取当前目标类的注解信息
        AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
        //获取当前目标类的类信息
        ClassMetadata classMetadata = metadataReader.getClassMetadata();
        String className = classMetadata.getClassName();
        System.out.println("----> " + className);
        if (className.contains("er")) return true;
        return false;
    }
}
```

- @Repeatable(ComponentScans.class)：可重复注解

```java
@ComponentScans(value = {
        @ComponentScan(value = "com.zhengguoqiang",includeFilters = {
                @ComponentScan.Filter(type = FilterType.ANNOTATION,classes = {Controller.class})
        },useDefaultFilters = false)
```

#### @Scope 

用于设置Bean的作用域

- singleton：默认值，单实例，容器启动的时候创建bean注入到ioc容器中
- prototype：多实例，获取实例时才创建bean
- request：同一个请求创建一个实例
- session：同一个session创建一个实例

#### @Lazy 

用于设置懒加载，singleton作用域下使用，设置为true表示容器启动的时候不创建bean，只有在获取bean时才创建Bean并将其注入到ioc容器中

#### @Conditional

当所有的条件满足时，Bean才会注入到ioc容器中；作用在方法或者类上

- Class<? extends Condition>[] value()：自定义条件需要实现org.springframework.context.annotation.Condition接口

#### @Import

用于快速向容器中注入组件

容器中注入组件的几种方式：

1. 包扫描 + 标注组件注解（@Controller、@Service）：适用于自己定义的组件
2. @Bean：适用于第三方导入的组件
3. @Import：快速导入组件，直接应用在配置类（@Configuration）
   - @Import({A.class,B.class})：快速注入A组件和B组件，id默认组件全类名
   - @Import({XXImportSelector.class})：需要实现ImportSelector接口，返回的全类名组件将会注入到容器中
   - @Import({XXImportBeanDefinitionRegistrar.class})：需要实现ImportBeanDefinitionRegistrar接口，重写registerBeanDefinitions方法，通过BeanDefinitionRegistry.registerBeanDefinition方法将Bean注入到容器中
4. 使用Spring提供的FactoryBean（工厂Bean）
   - 实现org.springframework.beans.factory.FactoryBean接口，实现getObject和getObjectType方法
   - 默认注册到容器中的是工厂Bean调用getObject方法得到的组件
   - 要想获得工厂Bean本身，需要在id前面加一个&，如`applicationContext.getBean("&colorFactoryBean")`

### 生命周期

**Bean的生命周期**：就是Bean的创建、初始化、销毁的过程。

**容器管理Bean的生命周期**：我们可以自定义初始化和销毁方法，容器在Bean进行到当前生命周期时调用自定义的初始化和销毁方法。

**构造对象**：

- 单实例：容器启动的时候创建
- 多实例：每次获取的时候创建

**初始化**：对象创建完成，并将属性赋值完成后执行初始化方法

**销毁**：

- 单实例：容器关闭的时候销毁
- 多实例：容器不会管理Bean的销毁，需要自己手动销毁

#### 实现Bean的初始化和销毁的几种方式：

1. 指定初始化和销毁方法

   @Bean注解指定init-method="",destroy-method=""(与xml配置文件里指定的方式一样)

2. Bean实现InitializingBean接口定义初始化逻辑，实现DisposableBean接口定义销毁逻辑

   InitializingBean接口的afterPropertiesSet方法在Bean创建完成，且属性赋值和BeanFactoryAware、ApplicationContextAware执行完毕后在执行初始化逻辑

3. JSR250规范（javax.annotation）

   @PostConstruct：在Bean创建完成并且属性赋值完毕后执行初始化方法

   @PreDestroy：在容器销毁Bean之前通知我们进行清理工作

   > 注：Java EE在java 9中已经被废弃，在java 11中被移除，所以要想使用需要单独引入javax.annotation依赖

4. BeanPostProcessor后置处理器

   postProcessBeforeInitialization：在初始化（InitializingBean's afterPropertiesSet || a custom init-method）之前执行

   postProcessAfterInitialization：在初始化（InitializingBean's afterPropertiesSet || a custom init-method）之后执行

#### BeanPostProcessor原理

1. AbstractAutowireCapableBeanFactory.doCreateBean

```java
//AbstractAutowireCapableBeanFactory.doCreateBean(final String beanName, final RootBeanDefinition mbd, final @Nullable Object[] args)方法创建非懒加载的单例Bean
try {
   populateBean(beanName, mbd, instanceWrapper);//给Bean的属性赋值
   exposedObject = initializeBean(beanName, exposedObject, mbd);//执行Bean的初始化
}
```

2. initializeBean方法

该方法分成三个部分：Aware接口回调、Aware的BeanPostProcessor实现、BeanPostProcessor前置初始化/InitMethod初始化/BeanPostProcessor后置初始化

- Aware接口回调 (BeanNameAware、BeanClassLoaderAware、BeanFactoryAware)

```java
//给Bean注入Spring底层组件，这里只包含BeanNameAware、BeanClassLoaderAware、BeanFactoryAware
invokeAwareMethods(beanName, bean);

private void invokeAwareMethods(final String beanName, final Object bean) {
	if (bean instanceof Aware) {
		if (bean instanceof BeanNameAware) {
			((BeanNameAware) bean).setBeanName(beanName);
		}
		if (bean instanceof BeanClassLoaderAware) {
			ClassLoader bcl = getBeanClassLoader();
			if (bcl != null) {
				((BeanClassLoaderAware) bean).setBeanClassLoader(bcl);
			}
		}
		if (bean instanceof BeanFactoryAware) {
			((BeanFactoryAware) bean).setBeanFactory(AbstractAutowireCapableBeanFactory.this);
		}
	}
}
```

- Aware的BeanPostProcessor实现

典型实现：ApplicationContextAwareProcessor

```java
//AbstractAutowireCapableBeanFactory的applyBeanPostProcessorsBeforeInitialization方法
//与第三部分的applyBeanPostProcessorsBeforeInitialization是同一块代码
public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)
		throws BeansException {

	Object result = existingBean;
    //getBeanPostProcessors里会返回ApplicationContextAwareProcessor和自定义BeanPostProcessor
	for (BeanPostProcessor processor : getBeanPostProcessors()) {
		Object current = processor.postProcessBeforeInitialization(result, beanName);
		if (current == null) {
			return result;
		}
		result = current;
	}
	return result;
}

//ApplicationContextAwareProcessor的postProcessBeforeInitialization方法
public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
	if (!(bean instanceof EnvironmentAware || bean instanceof EmbeddedValueResolverAware ||
			bean instanceof ResourceLoaderAware || bean instanceof ApplicationEventPublisherAware ||
			bean instanceof MessageSourceAware || bean instanceof ApplicationContextAware)){
		return bean;
	}

	AccessControlContext acc = null;

	if (System.getSecurityManager() != null) {
		acc = this.applicationContext.getBeanFactory().getAccessControlContext();
	}

	if (acc != null) {
		AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
			invokeAwareInterfaces(bean);
			return null;
		}, acc);
	}
	else {
		invokeAwareInterfaces(bean);
	}

	return bean;
}
//ApplicationContextAwareProcessor的invokeAwareInterfaces方法
//回调Bean中重写的方法将Spring底层组件注入到Bean中
private void invokeAwareInterfaces(Object bean) {
	if (bean instanceof EnvironmentAware) {
		((EnvironmentAware) bean).setEnvironment(this.applicationContext.getEnvironment());
	}
	if (bean instanceof EmbeddedValueResolverAware) {
		((EmbeddedValueResolverAware) bean).setEmbeddedValueResolver(this.embeddedValueResolver);
	}
	if (bean instanceof ResourceLoaderAware) {
		((ResourceLoaderAware) bean).setResourceLoader(this.applicationContext);
	}
	if (bean instanceof ApplicationEventPublisherAware) {
		((ApplicationEventPublisherAware) bean).setApplicationEventPublisher(this.applicationContext);
	}
	if (bean instanceof MessageSourceAware) {
		((MessageSourceAware) bean).setMessageSource(this.applicationContext);
	}
	if (bean instanceof ApplicationContextAware) {
		((ApplicationContextAware) bean).setApplicationContext(this.applicationContext);
	}
}
```

getBeanPostProcessors方法包含的BeanPostProcessor如下：

![getBeanPostProcessors](https://raw.githubusercontent.com/zhengguoqiang927/Figure-bed/master/img/spring-getbeanpostprocessor-method.png)

- BeanPostProcessor前置初始化/InitMethod初始化/BeanPostProcessor后置初始化

```java
if (mbd == null || !mbd.isSynthetic()) {
    //遍历容器中所有的BeanPostProcessor，逐个执行postProcessBeforeInitialization方法，如果该方法返回null则不在执行后面BeanPostProcessor的postProcessBeforeInitialization
    //注意：这里的BeanPostProcessor包含ApplicationContextAwareProcessor和自定义的BeanPostProcessor
	wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
}
try {
	invokeInitMethods(beanName, wrappedBean, mbd);//调用自定义初始化方法
}catch(Throwable ex){
    ...
}
if (mbd == null || !mbd.isSynthetic()) {
	//与postProcessBeforeInitialization同理
    wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
}
```

#### Spring底层对BeanPostProcessor的使用

- ApplicationContextAwareProcessor：实现给Bean的属性赋值ApplicationContext容器
- AutowiredAnnotationBeanPostProcessor：实现@Autowired注解

### 属性赋值

#### @Value

1. 基本数值，如张三、李四、18
2. SpEL表达式#{}，如#{20-2}
3. ${}读取外部配置文件或者环境变量中的值，如${person.name}

#### @PropertySource

读取外部配置文件中的k/v保存到运行的环境变量中，在使用${}读取配置文件的内容，实际上就从环境变量Environment中读取配置。如environment.getProperty("person.name");

- String[] value()：表示要加载的属性文件的位置，如value={"classpath:/com/myco/app.properties","file:/path/to/file.xml"}

- @Repeatable(PropertySources.class)：可重复注解

```java
@PropertySources(value = {
        @PropertySource(value = {"classpath:/person.properties"})
})
```

### 自动装配

Spring利用依赖注入（DI）完成对IOC容器中各个组件的依赖关系赋值

#### @Autowired

1. 默认优先按照类型去容器中查找组件`applicationContext.getBean(BookDao.class)`
2. 如果找到多个相同类型的组件时，在将属性名作为组件id进行查找`applicationContext.getBean("bookDao")`
3. 使用@Qualifier指定需要装配的组件id`@Qualifier("bookDao")`
4. @Primary表示首选的Bean，优先级低于@Qualifier
5. @Autowired(required=false)避免容器启动时的强制装配

作用位置：属性、构造器、参数、方法，无论哪种都是从容器中获取参数组件然后进行装配

- 标注在方法上
- 标注在构造器上：如果组件当且仅有一个有参构造器，这个有参构造器的@Autowired可以省略。当无参和有参构造器同时存在而又没有其他注入方式时，@Autowired一定要放在有参构造器的方法上才能注入，放到有参构造的参数上是无效的。
- 标注在参数上：@Bean + 方法参数，参数上@Autowired可以省略

#### @Resource

Java规范的注解，来源JSR250

可以和@Autowired一样实现自动装配功能，默认按照组件名称（属性名或者自定义名称）装配

不支持@Primary、@Qualifier注解也不支持@Autowired的required=false的功能

#### @Inject

Java规范的注解，来源JSR330

需要导入javax.inject包，和@Autowired功能完全一样，除了不支持required=false的功能

#### 自定义组件引入Spring容器底层组件

自定义组件想要使用ApplicationContext、BeanFactory、BeanName、Environment、StringValueResolver等Spring底层组件，只需要实现其对应的xxxAware接口，Aware接口通过回调的方式将Spring底层的组件传递给（或者说通知到）Bean

xxxAware的原理就是前面提到的xxxProcessor，比如

ApplicationContextAware=>ApplicationContextAwareProcessor

#### @Profile

指定组件在哪个环境下才能注册到容器中，如果不指定，所有Bean都能注册到容器

- 加了环境标识的Bean，只有在指定环境下才能注册到容器中。默认default环境
- 作用于类上表示在指定的环境下整个配置类中的Bean全部注入到容器中
- 没有指定环境标识的Bean，无论在哪个环境都能注册

## AOP

**面向切面编程（Aspect-Oriented Programming）**,作为面向对象编程模式的一种补充，使得横切关注点可以在程序运行期间动态的应用到主业务逻辑中去，实现横切关注点与业务逻辑对象之间的解耦。而DI实现的应用对象之间的解耦。

**横切关注点（cross-cutting concern）**就是散布于应用中多处的功能，比如日志、声明式事务、安全以及缓存等等。其可以被模块化为特殊的类，而这些类被成为**切面（aspect）**。

### 核心概念

**Aspect（切面）**：模块化的横切关注点

**Advice（通知）**：就是在特定的连接点处切面要采取的操作。通知与切入点表达式相关联，并在与切入点表达式匹配的连接点处运行。通知定义了切面要做什么以及何时（前后、返回、异常、环绕）做。

- Before（前置通知）：在连接点之前执行通知
- After（finally）（后置通知）：在连接点退出（无论正常返回还是抛出异常）之后执行通知
- After returning（返回通知）：在连接点正常完成之后执行通知
- After throwing（异常通知）：方法执行抛出异常时执行通知
- Around（环绕通知）：在方法调用的前后执行通知

**Join point（连接点）**：程序执行过程中的一个点，比如方法调用或者异常处理。Spring AOP只支持方法调用。

**Pointcut（切入点）**：匹配连接点的谓词或表达式。告诉切面在"何处"执行通知,而通知告诉切面做什么以及"何时"做。

Weaving（织入）：把切面应用到目标对象并创建新的代理对象的过程。织入时机分三种：

- 编译期（compile time）
- 类加载期（load time）
- 运行期（runtime）：Spring AOP就是以这种方式织入切面的。

![Spring AOP](https://raw.githubusercontent.com/zhengguoqiang927/Figure-bed/master/img/SpringAOP.png)

### 搭建过程

以MathCalculator类运行的过程中打印日志为例：

1. 导入Spring AOP依赖spring-aspects
2. 定义一个业务逻辑类MathCalculator
3. 定义一个日志切面类LogAspect，并创建通知方法及切入点
   1. 前置通知@Before：在目标方法之前执行
   2. 后置通知@After：在目标方法之后执行
   3. 返回通知@AfterReturning：在目标方法正常返回之后执行
   4. 异常通知@AfterThrowing：在目标方法出现异常之后执行
   5. 环绕通知@Around：动态代理，手动推进目标方法执行（joinPoint.procced()）
4. 给切面类的目标方法标注何时何地运行，也就是加上3中提到的通知注解和切入点注解
5. 将切面类和业务逻辑类注册到容器中
6. 告诉Spring哪个是切面类，即在切面类上加@Aspect注解
7. 配置类中加@EnableAspectJAutoProxy注解，启动基于注解的AOP模式

简化一下：

1. 定义切面类和业务逻辑类，并用@Aspect注解标注哪个是切面类
2. 切面类的方法上标注通知注解@Before及切入点注解@Pointcut，告诉Spring何时何地执行通知方法
3. 开启基于注解的AOP模式，配置类加@EnableAspectJAutoProxy

### 原理

@EnableAspectJAutoProxy：启动AOP注解模式

![Spring-EnableAspectJAutoProxy](https://raw.githubusercontent.com/zhengguoqiang927/Figure-bed/master/img/Spring-EnableAspectJAutoProxy.png)

@Import(AspectJAutoProxyRegistrar.class)：通过实现ImportBeanDefinitionRegistrar接口向容器中导入组件。

AspectJAutoProxyRegistrar类实现ImportBeanDefinitionRegistrar接口向容器中注册AnnotationAwareAspectJAutoProxyCreator组件，BeanName为org.springframework.aop.config.internalAutoProxyCreator

主要研究AnnotationAwareAspectJAutoProxyCreator类，去掉与调试无关接口及相关类，得到如下类图

![AnnotationAwareAspectJAutoProxyCreator](https://raw.githubusercontent.com/zhengguoqiang927/Figure-bed/master/img/AnnotationAwareAspectJAutoProxyCreator.png)

```java
//主要关注setBeanFactory、postProcessBeforeInitialization、postProcessAfterInitialization、postProcessBeforeInstantiation、postProcessAfterInstantiation这几个方法的实现及重写

//AbstractAutoProxyCreator抽象类实现setBeanFactory、postProcessBeforeInstantiation、postProcessAfterInitialization
AbstractAutoProxyCreator.setBeanFactory(BeanFactory)
AbstractAutoProxyCreator.postProcessBeforeInstantiation(Class, String)
AbstractAutoProxyCreator.postProcessAfterInitialization(Object, String)
//AbstractAdvisorAutoProxyCreator重写了setBeanFactory方法
AbstractAdvisorAutoProxyCreator.setBeanFactory(BeanFactory)
AbstractAdvisorAutoProxyCreator.initBeanFactory(BeanFactory)
//AnnotationAwareAspectJAutoProxyCreator重写了initBeanFactory方法
AnnotationAwareAspectJAutoProxyCreator.initBeanFactory(BeanFactory)
```

#### Bean创建流程

1. AbstractBeanFactory.getBean 

```java
@Override
public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
    return doGetBean(name, requiredType, null, false);
}
```

2. AbstractBeanFactory.doGetBean

```java
// Eagerly check singleton cache for manually registered singletons.
Object sharedInstance = getSingleton(beanName);//优先从单实例缓存中获取，如果有则直接返回

// Create bean instance.
if (mbd.isSingleton()) {
    sharedInstance = getSingleton(beanName, () -> {
        try {
            return createBean(beanName, mbd, args);//上面缓存中没有，才会进行创建
        }
        catch (BeansException ex) {
            // Explicitly remove instance from singleton cache: It might have been put there
            // eagerly by the creation process, to allow for circular reference resolution.
            // Also remove any beans that received a temporary reference to the bean.
            destroySingleton(beanName);
            throw ex;
        }
    });
    bean = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
}
```

3. AbstractAutowireCapableBeanFactory.createBean

```java
//1
// Give BeanPostProcessors a chance to return a proxy instead of the target bean instance.
Object bean = resolveBeforeInstantiation(beanName, mbdToUse);
if (bean != null) {
    return bean;
}

//2
//resolveBeforeInstantiation方法实现
@Nullable
protected Object resolveBeforeInstantiation(String beanName, RootBeanDefinition mbd) {
    Object bean = null;
    if (!Boolean.FALSE.equals(mbd.beforeInstantiationResolved)) {
        // Make sure bean class is actually resolved at this point.
        if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
            Class<?> targetType = determineTargetType(beanName, mbd);
            if (targetType != null) {
                bean = applyBeanPostProcessorsBeforeInstantiation(targetType, beanName);//Bean实例化之前执行
                if (bean != null) {
                    bean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
                }
            }
        }
        mbd.beforeInstantiationResolved = (bean != null);
    }
    return bean;
}

//3. 如果前面没有创建出来对象，才会执行doCreateBean
try {
    Object beanInstance = doCreateBean(beanName, mbdToUse, args);
    if (logger.isTraceEnabled()) {
        logger.trace("Finished creating instance of bean '" + beanName + "'");
    }
    return beanInstance;
}
```

4. AbstractAutowireCapableBeanFactory.doCreateBean

```java
if (instanceWrapper == null) {
    instanceWrapper = createBeanInstance(beanName, mbd, args);//创建Bean实例
}

// Initialize the bean instance.
Object exposedObject = bean;
try {
    populateBean(beanName, mbd, instanceWrapper);//Bean属性赋值
    exposedObject = initializeBean(beanName, exposedObject, mbd);//Bean初始化
}
```

5. AbstractAutowireCapableBeanFactory.initializeBean

```java
//1.回调Aware方法，这里的Aware只限BeanNameAware、BeanClassLoaderAware、BeanFactoryAware
invokeAwareMethods(beanName, bean);

//2.执行BeanPostProcessor的postProcessBeforeInitialization方法，初始化前逻辑处理，比如ApplicationContextAwareProcessor向业务bean中注入ApplicationContext容器
Object wrappedBean = bean;
if (mbd == null || !mbd.isSynthetic()) {
    wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
}

//3.自定义初始化
try {
    invokeInitMethods(beanName, wrappedBean, mbd);
}

//4.执行BeanPostProcessor的postProcessAfterInitialization方法，初始化后逻辑处理
if (mbd == null || !mbd.isSynthetic()) {
    wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
}

//5.返回创建好的Bean
return wrappedBean;
```

简化后：

1. getBean获取实例，调用doGetBean
2. 判断单实例缓存中是否存在，如果不存在，进行createBean
3. 判断BeanPostProcessor是否能创建出目标对象的代理对象，如果不能，进行doCreateBean
4. createBeanInstance创建Bean实例
5. populateBean(beanName, mbd, instanceWrapper)：Bean属性赋值
6. initializeBean(beanName, exposedObject, mbd)：Bean初始化
   1. invokeAwareMethods(beanName, bean)：处理Aware接口的方法回调
   2. applyBeanPostProcessorsBeforeInitialization：初始化前逻辑处理
   3. invokeInitMethods执行自定义初始化
   4. applyBeanPostProcessorsAfterInitialization初始化后逻辑处理
   5. Bean创建成功并返回
7. 返回Bean实例



BeanPostProcessor创建流程：

1. 传入配置类，创建IOC容器

```java
AnnotationConfigApplicationContext applicationContext =
        new AnnotationConfigApplicationContext(SpringConfigAOP.class);
```

2. 注册配置类，刷新容器

```java
register(componentClasses);
refresh();
```

3. 注册BeanPostProcessor后置处理器用来拦截Bean的创建
   1. 获取IOC容器中已经定义好的，且类型为BeanPostProcessor的BeanName
   2. 遍历BeanName，并按照PriorityOrdered，Ordered，the rest分组
   3. 优先注册实现PriorityOrdered接口的BeanPostProcessor
   4. 再注册实现Ordered接口的BeanPostProcessor，AnnotationAwareAspectJAutoProxyCreator就是实现此接口的后置处理器
   5. 最后注册其余的BeanPostProcessor
   6. 注册BeanPostProcessor，根据BeanName得到RootBeanDefinition信息，生成BeanPostProcessor实例
      1. 

```java
registerBeanPostProcessors(beanFactory);
```

​			获取IOC容器中已经定义好的，且类型为BeanPostProcessor的BeanName

 遍历BeanName，并按照PriorityOrdered，Ordered，the rest分组



​	获取IOC容器中已经定义好的，且类型为BeanPostProcessor的BeanName

1. 遍历BeanName，并按照PriorityOrdered，Ordered，the rest分组
2. 优先注册实现PriorityOrdered接口的BeanPostProcessor

```java
registerBeanPostProcessors(beanFactory);
```



​		



