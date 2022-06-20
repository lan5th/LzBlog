---
title: SpringBoot中的Filter、Interceptor和AOP
categories: JavaEE
description: springboot中的拦截器问题
---

# AOP

谈到AOP，我们要清楚，AOP指的是面向切面的设计思想，而SpringAOP是AOP思想的一种具体实现，除此之外，常用到的还有AspectJAOP，两者的区别主要有：

1. 织入的时期不同

   Spring Aop采用的动态织入，而Aspectj是静态织入。静态织入：指在编译时期就织入，即：编译出来的class文件，字节码就已经被织入了。动态织入又分静动两种，静则指织入过程只在第一次调用时执行；动则指根据代码动态运行的中间状态来决定如何操作

2. 可以应用的对象不同

    Spring AOP的通知是基于该对象是SpringBean对象才可以，而AspectJ可以在任何Java对象上应用通知。

在初学AOP时，我曾将Filter、Interceptor和SpringAOP混淆不清，而这些其实都是AOP思想的一些具体的实现，根据具体情景选择不同的实现即可

+ Filter类似于JavaWeb中我们手动配置的过滤器，在Spring中实现javax.servlet下的Filter接口将其作为一个Bean注入到IOC容器中使用，实现doFilter方法即可

+ Interceptor是Spring自带的拦截器，实现HandlerInterceptor并实现preHandle、postHandle、afterCompletion方法就可以使用

+ 使用SpringAOP，需要标注`@Aspect`注解，指明切面类和方法，这里给出一个具体实例

  ```java
  @Component
  @Aspect
  public class LogAspect {
      //定义切面，其他方法直接引用这个方法作为切面
      @Pointcut("execution(public * com.example.mybatisplus.web.controller.TestController.*(..))")
      public void webLog(){}
  
      /**
       * 通过RequestContextHolder可以获取到请求上下文信息
       * @param joinPoint
       */
      @Before("webLog()")
      public void deBefore(JoinPoint joinPoint) {
          // 接收到请求，记录请求内容
          ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
          HttpServletRequest request = attributes.getRequest();
          // 记录下请求内容
          System.out.println("URL : " + request.getRequestURL().toString());
          System.out.println("HTTP_METHOD : " + request.getMethod());
          System.out.println("IP : " + request.getRemoteAddr());
          System.out.println("CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
          System.out.println("ARGS : " + Arrays.toString(joinPoint.getArgs()));
      }
  
      @AfterReturning(returning = "ret", pointcut = "webLog()")
      public void doAfterReturning(Object ret) {
          // 处理完请求，返回内容
          System.out.println("方法的返回值 : " + ret);
      }
  
      //后置异常通知
      @AfterThrowing("webLog()")
      public void throwsE(JoinPoint jp){
          System.out.println("方法异常时执行.....");
      }
  
      //后置最终通知,final增强，不管是抛出异常或者正常退出都会执行
      @After("webLog()")
      public void after(JoinPoint jp){
          System.out.println("方法最后执行.....");
      }
  
      //环绕通知,环绕增强，相当于MethodInterceptor
      @Around("webLog()")
      public Object arround(ProceedingJoinPoint pjp) {
          System.out.println("方法环绕start.....");
          try {
              Object o =  pjp.proceed();
              System.out.println("方法环绕proceed，结果是 :" + o);
              return o;
          } catch (Throwable e) {
              e.printStackTrace();
              return null;
          }
      }
  }
  ```

![](https://img2018.cnblogs.com/blog/1312982/201904/1312982-20190412203837811-1949443053.png)

推荐一篇文章详细介绍了三者的用法：[Filter，Interceptor和SpringAop - 爱跑步的星仔 - 博客园 (cnblogs.com)](https://www.cnblogs.com/caozx/p/10698433.html)

# RequestContextHolder

为了方便我们在任何方法中都能快速获取请求相关信息，SpringMVC封装了RequestContextHolder来帮助我们快速获取Request，在调用业务时就不需要将Request作为参数传入

基本使用

```java
//在没有使用JSF的项目中这两个方法是等效的
//RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
//获取session中的参数
String name = (String) attributes.getAttribute("name", RequestAttributes.SCOPE_SESSION);
//获取request和response
HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest();
HttpServletResponse response = ((ServletRequestAttributes) attributes).getResponse();
```

在使用时会想到几个问题：

+ RequestContextHolder是如何与当前请求绑定的？
+ request和response何时被设置进去？
+ 为什么RequestAttributes强转ServletRequestAttributes之后可以直接获取请求和响应？

1. RequestContextHolder中封装了两个ThreadLocal保存当前请求和请求参数，对当前请求进行绑定

   ```java
   private static final ThreadLocal<RequestAttributes> requestAttributesHolder = new NamedThreadLocal("Request attributes");
   private static final ThreadLocal<RequestAttributes> inheritableRequestAttributesHolder = new NamedInheritableThreadLocal("Request context");
   ```

2. 在接收请求的过程中，会进行HttpServletBean的初始化，最终调用到FrameworkServlet实现的对应方法，FrameworkServlet的相关依赖如图

   ![image-20211223204121984](https://cdn.jsdelivr.net/gh/lan5th/pics/blog_images/20220116232858.png)

   这里我们来看processRequest方法

   ```java
   protected final void processRequest(HttpServletRequest request, HttpServletResponse response)
       	throws ServletException, IOException {
   
       long startTime = System.currentTimeMillis();
       Throwable failureCause = null;
   	//获取上一个请求的LocaleContext
       LocaleContext previousLocaleContext = LocaleContextHolder.getLocaleContext();
       //建立新的LocaleContext
       LocaleContext localeContext = buildLocaleContext(request);
   	//获取上一个请求的RequestAttributes
       RequestAttributes previousAttributes = RequestContextHolder.getRequestAttributes();
       //建立新的RequestAttributes
       ServletRequestAttributes requestAttributes = buildRequestAttributes(request, response, previousAttributes);
   
       WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);
       asyncManager.registerCallableInterceptor(FrameworkServlet.class.getName(), new RequestBindingInterceptor());
   	//初始化LocaleContextHolder和RequestContextHolder
       initContextHolders(request, localeContext, requestAttributes);
   
       try {
           //调用子类的doService方法
           doService(request, response);
       }
       catch (ServletException | IOException ex) {
           failureCause = ex;
           throw ex;
       }
       catch (Throwable ex) {
           failureCause = ex;
           throw new NestedServletException("Request processing failed", ex);
       }
   
       finally {
           //恢复request
           resetContextHolders(request, previousLocaleContext, previousAttributes);
           if (requestAttributes != null) {
               requestAttributes.requestCompleted();
           }
           logResult(request, response, failureCause, asyncManager);
           //发布事件
           publishRequestHandledEvent(request, response, startTime, failureCause);
       }
   }
   ```

   至此对每个请求的RequestContextHolder进行了封装

3. 在buildRequestAttributes方法中返回了新的ServletRequestAttributes对象，这个类继承了RequestAttributes，拥有更多的方法

   ```java
   @Nullable
   protected ServletRequestAttributes buildRequestAttributes(HttpServletRequest request,
         @Nullable HttpServletResponse response, @Nullable RequestAttributes previousAttributes) {
   
      if (previousAttributes == null || previousAttributes instanceof ServletRequestAttributes) {
         return new ServletRequestAttributes(request, response);
      }
      else {
         return null;  // preserve the pre-bound RequestAttributes instance
      }
   }
   ```

