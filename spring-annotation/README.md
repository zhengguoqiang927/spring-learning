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



​		



