# MVVM架构指南

> 参考自：
>
> [言简意赅 Android 架构设计与挑选]: https://xiaozhuanlan.com/topic/4620985173



## 一、为什么要使用MVVM架构

目前市面上大多APP都仍使用MVP、甚至是MVC架构，为什么要使用MVVM架构呢？

------

1. 在一定量级的程序中，MVC架构中的View层与Controller层耦合过高，严重影响后期代码的维护以及复用，此外还容易因开发者水平问题遇到一致性等问题；

2. MVP架构是在MVC的基础上通过依赖导致来解决逻辑复用难、实现更替难的问题。MVP中Activity和Fragment充当View，而Presenter独吞表现层逻辑，通过接口与View、Model通信，这会导致Presenter层将原先View层的业务逻辑剥夺，极度容易代码冗余；

3. MVVM架构是专用于页面开发的设计架构，它通过Lifecycle、LiveData、ViewModel以及DataBinding这些Jetpack组件，切实解决了**View实例Null安全一致性、状态管理一致性以及消息分发一致性**的问题。

   > MVVM架构中的各个Jetpack组件作用：
   >
   > * Lifecycle 主要是为了解决 **生命周期管理 的一致性问题**。
   >
   > * LiveData 主要是为了解决 **消息分发结果同步 的一致性问题**。
   >
   > * ViewModel 主要是为了解决 **状态管理 和 页面通信 的问题**。
   >
   > * DataBinding主要是为了解决 **视图调用 的一致性问题**。
   >
   >   它们的存在大都是为了在软件工程的背景下解决一致性的问题、将容易出错的操作在后台封装好，**方便使用者快速、稳定、不产生预期外错误地编码**。



## 二、MVVM架构库的架构分层

那么MVVM架构的架构分层是怎样的呢？

------

以下为**MVVM架构库架构分层**：

![MVVM架构图](https://gitee.com/soulkun/typora_images/blob/master/images/MVVM架构图.png)

1. **UI**：

   ​	在MVVM架构中，View层通过Jetpack中的DataBinding将数据绑定到XML文件即View视图上，当数据（Observable...数据及LiveData数据）变化时，·DataBinding通知View视图及时发生变化。

2. **StateViewModel**：

   ​	StateViewModel的生命周期与依附的容器生命周期相同，StateViewModel的目标数据瞄准于容器的当前状态数据，例如MVC架构中的成员变量等都可存放于此，防止页面重建时状态数据的丢失。

   ​	**注意，所有ViewModel的使用都要绑定到容器中并获取其实例。**

   ​	StateViewModel状态ViewModel一般存放如下数据类：

   * 常用数据结构类
* `MvvmState`类：观察者模式的数据类，其拥有是否防抖动功能，防抖动可防止重复数据通知View。注意，**在初始化时必须传入非空值**。
   
   * `LiveData`及其子类：观察者模式的数据类，MVVM依赖库封装的子类赋予其是否粘性监听以及是否支持空值传入的功能，并根据业务提供了更多的功能。

     > `LiveData类`与`MvvmState`类本质区别：`LiveData`类可在容器中与生命周期结合，例如，DataBinding可在生命周期有效时监听其数据变化，生命周期销毁后是不会将变化传递给DataBinding的，而`MvvmState`没有生命周期的概念，更加轻量化

3. **MessageViewModel**：

   ​	MessageViewModel是MVVM架构中的消息总线类，负责**消息鉴权以及唯一可信源的消息订阅**，此外<u>使用LiveData防止内存泄露</u>。

   ​	**注意，所有ViewModel的使用都要绑定到容器中并获取其实例。**

   ​	★ 使用步骤：

   1. 继承抽象类并实现`protected abstract boolean authenticateMessage(@NonNull M message)`方法，返回true值才能通过鉴权允许发送至注册接收者；
   2. 使用者将消息总线ViewModel绑定至生命容器中并获取实例；
   3. 消息接收方使用下方的消息订阅方法订阅消息；
   4. 消息发送方使用下方的消息发送方法发送消息。

   ------

   ​	☆ 方法解析：

   * 消息订阅：

     * `public void observe(@NonNull LifecycleOwner lifecycleOwner, @NonNull Observer<M> observer)`：

       ​	观察生命周期敏感的粘性事件，注意，必须在主线程上调用。

     * `public void observeSticky(@NonNull LifecycleOwner lifecycleOwner, @NonNull Observer<M> observer)`：

       ​	观察生命周期敏感的粘性事件，注意，必须在主线程上调用。

     * `public void observeForever(@NonNull Observer<M> observer)`：
     
       ​	永久观察非粘性事件，需要手动解注册，否则造成内存泄露，注意，必须在主线程上调用。
     
     * `public void observeStickyForever(@NonNull Observer<M> observer)`：
     
       ​	永久观察粘性事件，需要手动解注册，否则造成内存泄露，注意，必须在主线程上调用。
     
     * `public void unobserve(@NonNull Observer observer)`：
     
       ​	解除观察者（永久和生命周期敏感），注意，必须在主线程上调用，一般用于永久观察者，生命周期敏感不做该处理。
     
     在MessageViewModel中，添加观察者是获取消息的唯一路径，根据观察时效需求不同，可分为永久与非永久（即生命周期敏感），又根据观察事件需求不同，可分为粘性与非粘性。
     
     > 1. 永久性的相关定义：
     >
     >    * 永久：
     >
     >      ​	不会根据生命周期自动解绑，下次返回至该页面时不需要重新注册。
     >
     >    * 生命周期敏感：
     >
     >      ​	根据生命周期自动解绑，下次返回至该页面时需要重新注册。
     >
     > 2. 粘性的相关定义：
     >
     >    * 粘性事件：
     >
     >      ​	在注册观察后会自动推送注册前接收的最近一次事件，再观察并推送之后的每一次事件。
     >
     >    * 非粘性事件：
     >
     >      ​	在注册观察后会自动观察并推送注册之后的每一次事件。
     
   * 消息发送：

     * `public void send(@NonNull M message)`：

       ​	发送Message，必须在主线程上调用，效率较高。

     * `public void post(@NonNull M message)`：

       ​	发送Message，可以在其他线程上调用，但效率较低。

   * 消息鉴权与额外设置：

     * `protected abstract boolean authenticateMessage(@NonNull M message)`：

       ​	**消息鉴权，开发者必须继承并实现该方法**，在方法中完成鉴权逻辑，仅返回true值方可通过鉴权，否则拦截非法消息。

     * `protected int initMessageQueueLength()`：

       ​	初始化设置消息队列长度，当消息队列填满时则自动排除第一个最初的元素，并执行清除逻辑，默认为16，**开发者一般不对该方法实现覆盖重写**。

     * `protected long initMessageClearTime()`：

       ​	初始化消息清空时间，当发送消息后到达设置的时间间隔，会自动清空消息实现内存消减的功能，默认5分钟，**开发者一般根据业务需求对其覆盖重写**。

4. **RequestViewModel**：

   ​	RequestViewModel是MVVM架构中的数据请求类，用于执行数据请求逻辑，通过Lifecycle实现**根据生命周期中断请求以及在有效生命周期内监听请求结果回调**的功能，<u>防止因页面离开后数据请求回调仍持有页面对象导致的内存泄露和网络请求造成的流量消耗</u>。

   ​	**注意，所有ViewModel的使用都要绑定到容器中并获取其实例。**

   ​	★ 使用步骤：

   1. 继承该抽象类；

   2. 在请求的页面中绑定容器作用域，获取该ViewModel，此时内部的**线程池生命**会绑定容器作用域，在容器销毁时线程池一同销毁；

   3. 在页面中调用`public void bindTaskLifecycle(@NonNull Lifecycle lifecycle, @NonNull MvvmLifecycleCouple lifecycleCouple)`方法，将**任务执行生命周期**与实际View生命周期绑定，在生命周期生效时线程池执行任务，失效时线程池直接关闭，放弃任务，尽量减少网络请求时的流量消耗以及不必要的JVM性能消耗；

      > 之所以这么设计，是因为容器的生命周期本质上与View的生命周期不一致，经常出现View已经销毁但容器仍然存在的情况，防止线程任务在View销毁后依然耗时执行。

   4. 在该ViewModel类内部创建返回<u>`LiveData<MvvmRequest<T>>`</u>类型的方法，方法内部必须执行`protected <T> void execute(MvvmAbstractRequestTask<T> task)`方法，由开发者决定在请求及请求取消时的返回逻辑；

   5. 在实际调用请求方法处调用ViewModel类方法并获取返回的LiveData对象，最后在LiveData对象上调用其observe方法，注意，**observe方法本质上是添加观察者，在LiveData对象设置结果新值时触发观察者回调，最终执行结果回调逻辑，防止用于数据请求一直持有页面对象导致的内存泄漏**。

   > RequestViewModel类严格限制了**线程池生命**、**任务执行生命周期**以及**结果回调生命周期**这三个生命周期。

5. **Remote Repository**：

   ​	Remote Repository是MVVM架构中的远程仓库模块（即网络请求模块），可直接套用Retrofit框架的网络请求逻辑。

6. **Local Repository**：

   ​	Local Repository是MVVM架构中的本地仓库模块（即本地文件和数据库请求模块），可直接套用Java IO流及Room数据库请求逻辑。

7. **Protocol Repository**：

   ​	Protocol Repository是MVVM架构中的协议仓库模块（即协议请求模块），可直接套用协议框架的请求逻辑。



## 三、MVVM架构库的具体使用

我不想知道这些花里胡哨的东西，我只想知道咋用？

------

### 1.DataBinding的基础使用

> 在MVVM架构眼里，万物都是数据类。

1. 继承库中的以下UI容器类，并实现对应抽象方法；

   ![image-20230130140141859](https://gitee.com/soulkun/typora_images/raw/master/images/image-20230130140141859.png)

2. 创建XML文件，根据业务场景设置需要传入事件类和数据类，使用方法如下：

   * 单向绑定：(不限任何数据类)

     ​	通过`@{}`表达式将数据绑定至视图控件上，当数据改变时视图也会根据绑定逻辑改变。`@{}`表达式支持参数的字符串构建、布尔值判断、二元表达式和三元表达式等基本表达式使用，但**不推荐使用太复杂的逻辑**，会导致报错难以定位。

   * 双向绑定：(数据类必须为`MvvmMutableLiveData`数据类和`MvvmState`数据类)

     ​	`@={}`表达式代表双向绑定，但只仅限于需要使用**特定的双向绑定方法**方可使用，双向绑定大大减少了代码量，达到了控件或数据改变时另一方的数据或属性同时改变的效果。


      * 绑定事件：(数据类为逻辑数据类)

        ​	使用Lambda表达式以及双冒号将DataBinding方法的参数转变为传入数据类的方法参数，隔离了容器和控件的接触，保证了控件生命周期的一致性。

3. 在UI容器类中创建对应数据类，并执行需要的逻辑，**注意，逻辑数据类必须以该UI容器类的类名为前缀**；

   * **StateViewModel状态VM**专门存放`LiveData`以及`MVVMState`数据，<u>数据的使用方面下文详细阐释</u>。

   * **Init初始化类**在需要时初始化控件时创建，在其中构建对应控件的初始化方法并设置所需控件参数，一般DataBinding执行到该方法不会超过创建View对象后一帧的时间。

   * **Click点击类**在需要时设置控件点击事件时创建，在其中构建对应控件的点击监听回调并设置所需控件参数（本质上点击类功能是初始化类功能的特化）。

   * 其余逻辑数据类创建思路类似如上相同。

4. 最后，在`getDataBindingFactory()`方法中必须使用`MvvmDataBindingFactory`工厂类的相关方法传入参数，创建并返回对象；

![image-20221122170234205](https://gitee.com/soulkun/typora_images/raw/master/images/image-20221122170234205.png)

​		注意，**VM中的LiveData数据类监听需要调用`viewDataBinding.setLifecycleOwner(lifecycleOwner)`方法**，若自定义ViewDataBinding出现LiveData数据改变但DataBinding不发生变化务必检查该项！

总结，MVVM之所以能解决视图空安全的一致性问题，DataBinding是最大的功臣。**DataBinding布局中我们一般将状态StateViewModel、Init初始化类以及Click点击事件类传入XML中，能够满足95%的业务场景，而其他数据则根据业务动态增减**。



### 2.DataBinding的高级使用

* MVVM依赖库的UI容器类中提供了`initView(final ViewDataBinding viewDataBinding)`方法，允许直接操作`DataBinding`对象执行需要较多控件组合的逻辑，此外还提供了`getDataBinding()`方法直接提供`ViewDataBinding`对象，但**极不推荐使用该方法，使用时必须判空**！

* `DataBindingUtil`类提供了DataBinding的工具方法，允许开发者在无法使用`MvvmDataBindingFactory`工厂类解决问题时使用其他方法处理场景。

* **DataBinding适配器**用法：

  DataBinding适配器设计起到了”**代码复用**“的作用，只需要定义一次方法即可在XML中套用DataBinding属性。

  * **@BindingAdapter**注解的使用：

    1. 编写该注解，预设DataBinding属性名数组填入`value()`；

       ![image-20230305161808673](https://raw.githubusercontent.com/soulkun926/typora-images/main/images/image-20230305161808673.png)

       > 该注解中`value()`为预设的DataBinding属性名数组，`requireAll()`为是否同一XML节点全部属性都绑定后方可生效，默认为true。

    2. 在注解下方编写静态方法，注意，第一个参数必须为想要绑定的控件类实例，之后的参数依次为**`value()`属性名对应的正确格式的对象**，再实现对应UI逻辑；

       ![image-20230305162108083](https://raw.githubusercontent.com/soulkun926/typora-images/main/images/image-20230305162108083.png)

    3. 在XML中对应控件节点下绑定写好的DataBinding属性，传入**正确格式的参数**即可完成代码复用。

  * **@BindingMethods**注解与**@BindingMethod**注解的使用：

    > @BindingMethods与@BindingMethod则解决**现有方法的绑定以及现有属性名与方法名不一致**的问题。

    1. 在自定义View类上方编写@BindingMethods注解并在其中填入@BindingMethod注解数组；

    2. @BindingMethod注解中`type()`填入控件Class类型，`attribute()`填入预设的DataBinding属性名，`method`则填入控件中的方法名（不需要填入括号以及方法参数）；

    3. 在XML中对应控件节点下绑定写好的DataBinding属性，传入**正确格式的参数**即可完成代码复用。

* DataBinding双向绑定的使用：

  1. 依次写好如下三个方法：

     ![image-20221122174713423](https://gitee.com/soulkun/typora_images/raw/master/images/image-20221122174713423.png)

     ![image-20221122174727323](https://gitee.com/soulkun/typora_images/raw/master/images/image-20221122174727323.png)

     ![image-20221122174809878](https://gitee.com/soulkun/typora_images/raw/master/images/image-20221122174809878.png)

     注意，**以上方法为必须的双向绑定方法**，第一个方法在获取数据后调用控件对应的方法，第二个方法在获取控件绑定的数据后将数据重新设置到`LiveData`上，第三个方法则是在控件监听器生效时及时通知数据改变。

  2. 在XML节点中绑定第一个方法对应的属性名，传入`MvvmMutableLiveData`或`MvvmState`数据，注意使用`@={}`绑定，即可完成双向绑定。

     ![image-20221122175205531](https://gitee.com/soulkun/typora_images/raw/master/images/image-20221122175205531.png)

* DataBinding中`include`标识符的使用：

  ​	XML中可使用`include`标识符动态使用DataBinding次级布局，此时可将次级布局中的variable参数名作为include的属性传递数据到次级布局，达到**布局复用**的效果。




### 3.Lifecycle的应用场景

> **Lifecycle**是用于帮助开发者管理Activity和Fragment 的生命周期，它是LiveData和ViewModel的基础。
>
> 它解决了这三个问题：
>
> * 实现 **“生命周期组件管理” 代码修改的一致性**。
> * 使第三方组件 **随时可在自己内部拿到生命周期状态**，以便执行 **及时叫停 “错过时机” 异步业务** 等操作。
> * 使第三方组件调试时，能 **安全便捷追踪到 “事故所在生命周期源”。**
>
> 随着业务场景的复杂度骤增，第三方组件更需要使用Lifecycle在自身内部拿到容器的生命周期达到专注于自身内部业务处理，而不是在过多外因的干扰下堆积自身业务代码。

在具体使用时，一般有两种注册Lifecycle观察者方式：

* 第三方组件实现`LifecycleObserver`接口，并在自身与生命周期对应的方法上添加`@OnLifecycleEvent`注解，指定在对应生命周期分发时执行该方法。具体写法如下所示：

  ![image-20230130155144908](https://gitee.com/soulkun/typora_images/raw/master/images/image-20230130155144908.png)

* 第三方组件实现`LifecycleEventObserver`接口，并重写`public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event)`方法，在方法中判断生命周期状态并执行对应逻辑。

在注册Lifecycle观察者后，则可在容器中获取对应的Lifecycle再执行`addObserver(lifecycleObserver)`方法添加第三方组件作为生命周期观察者，注意，**不需要手动删除观察者，在生命周期结束后会自动删除观察者**。



### 4.ViewModel在不同场景的使用

根据常见的业务场景，可以将其分为三种场景使用：

* 状态管理：(**StateViewModel**)

  ​	状态管理指的是**根据业务需求，将业务和页面完全隔离，专注于管理业务的状态**。在复杂的业务场景中，部分业务状态是跨页面的，而部分状态又是该页面才有的，此时可以使用StateViewModel的设计思路（详细见MVVM架构库的架构分层），创建继承于`ViewModel`的StateViewModel类，再根据业务对应的作用域调用`MvvmViewModelFactory`工厂类**获取作用域唯一的VM实例**，此时即可完成业务的状态管理，在较大的作用域中完全可以做到不同页面的相同业务状态共享。

  > `MvvmViewModelFactory`工厂类模板方法一览：(作用域指实例的生命范围，超出生命范围则会被销毁)
>
  > * `getActivityViewModel`方法：获取作用域为**Activity**的ViewModel。
  > * `getFragmentViewModel`方法：获取作用域为**Fragment**的ViewModel。
  > * `getServiceViewModel`方法：获取作用域为**Service**的ViewModel。
  > * `getGraphViewModel`方法：获取作用域为**Graph**的ViewModel。
  > * `getApplicationViewModel`方法：获取作用域为**Application**的ViewModel。
  > * `getViewModel`方法：获取作用域为**指定作用域**的ViewModel。
  
* 消息总线：

  ​	消息总线指的是**在不同页面中通过总线发送和订阅消息，可以降低代码耦合度**。在该业务场景中，可以使用MessageViewModel的设计思路（详细见MVVM架构库的架构分层），创建继承于`MvvmAbstractMessageViewModel`的MessageViewModel类，并在其中重写鉴权方法，目的是防止调用者发送不符合开发者要求的异常消息，此外，还可以自定义消息队列长度以及消息清空时间，防止消息数据在发送后一直存在于内存，达到内存裁剪的效果。在创建VM类后获取**作用域唯一**的VM实例，即可对其发送或订阅消息，达到消息总线的目的。

* 异步请求：

  ​	异步请求指的是**在页面业务中异步请求并执行回调的过程**。在该业务场景中，可以使用RequestViewModel的设计思路（详细见MVVM架构库的架构分层），创建继承于`MvvmAbstractRequestViewModel`的RequestViewModel类，并在其中实现异步请求方法。在创建VM类后获取**作用域唯一**的VM实例，绑定线程任务生命周期，最后调用异步方法，监听其结果回调，即可完成异步请求。RequestViewModel类相较于其他异步请求第三方库的优点有，一是使用ViewModel实现与MVVM架构更加契合，二是对线程生命周期及其回调生命周期的调度颗粒度更精细，减少网络请求的流量消耗和性能开销，也防止内存泄露的发生。



### 5.Navigation导航的基本使用

> Navigation导航是**支持用户导航、进入和退出应用中不同内容片段的交互**。
>
> 它主要解决了这三个问题：
>
> * 通过声明式编程，来确保 “应用内导航” 的一致性。
>
> * 通过可视化编程，来直观反映页面的路由关系。
>
> * 通过抽象，来整合 Activity 和 Fragment 的路由跳转代码。

Navigation导航的基本使用步骤如下：

1. 在navigation目录下创建navigation文件，在文件中我们需要声明如下元素：

   * **navigation元素**：(对应导航图)
     * id属性：声明导航图唯一id；
     * startDestination属性：声明起始目的地。

   * **fragment元素**：(对应源Fragment)

     * name属性：指明源地址；
     * id属性：声明唯一id，帮助其他 Fragment 的 action 元素链接到自己、从而帮助后台找到自己。

   * **include元素**：(引用其他导航图)

     * graph属性：声明引用的导航图id，需要与导航图中的navigation的id属性值相同。

   * **action元素**：(执行跳转的目标)

     * destination属性：指明前往的目标 id；

     * id属性：声明唯一id，帮助后台发现和执行这个 action；

     * popUpTo属性：指明在跳转调用`navigate()`的过程中从返回堆栈上弹出截止到的目标id，即应保留在堆栈中的最新目的地的id；

       > 注意是先弹出堆栈再压入目的地！

     * popUpToInclusive属性：指明popUpTo属性中指定的目的地是否也从返回堆栈中移除，默认为false；

     * launchSingleTop属性：表明页面的启动模式，是否为singleTop；

     * anim相关属性：声明转场动画。

   * argument元素：(由于StateViewModel可跨页面共享，不需要使用该元素传递参数，只是简单了解而已)

     * name属性：描述参数名；
     * argType属性：描述参数类型；
     * defaultValue属性：描述参数默认值。

   > 在完成navigation文件后我们会惊喜地发现Design会直观显示fragment之间的关联，这就是Navigation导航的可视化。

2. 在需要放置Navigation导航的容器（一般是Activity）对应的XML文件中放置`FragmentContainerView`节点，节点中需要注意两个属性：

   * `app:defaultNavHost`属性必须设为**true**；
   * `app:navGraph`属性填入需要托管导航的navigation文件资源id。

   此时，即可完成Fragment的托管导航，注意，**Navigation本质也是对FragmentManager操作的封装**！

3. 在Fragment容器中对于导航的跳转操作如下：

   * 跳转到另一fragment时：
     * 通用做法：调用`NavController#navigate(@IdRes int resId)`方法；
     * **推荐做法**：在继承`MvvmAbstarctFragment`的类中直接使用`navigateSafe`相关方法，防止因重复点击导致的Navigation跳转崩溃Bug。
   * 回退到上一fragment时：
     * 通用做法：调用`NavController#navigateUp()`方法；
     * **推荐做法**：在继承`MvvmAbstarctFragment`的类中直接使用`navigateUp()`方法。

   > 注意事项：**Navigation中进入及退出本质上都是`FragmentTransaction`中的replace操作**。
   >
   > 当fragment A跳转至fragment B时：(navigate方法)
   >
   > * fragment A执行截止到`onViewDestroyed()`的生命周期步骤，View虽然已经销毁，但fragment容器并未销毁；
   > * fragment B执行截止到`onResume()`的生命周期步骤，创建容器和View。
   >
   > 当fragment B回退至时fragment A：(navigateUp方法)
   >
   > * fragment B执行截止到`onDetach()`的生命周期步骤，View和容器都被销毁；
   > * fragment A重新执行从`onCreateView()`到`onViewCreate()`的生命周期步骤，容器不变，但View重新创建。
   >
   > **使用Navigation一定要牢记其生命周期流程！**

4. 在使用`BottomNavigationView`、`MvvmLeftNavigationView`等组件联动Navigation导航时需要如下操作：

   1. 在XML节点中的`app:menu`属性中设置Menu菜单资源id。注意，**菜单Id必须与Navigation中的fragment或navigation的Id相同，否则关联时无法找到对应菜单子项**。

   2. 在Init类中的初始化方法中先获取 `NavHostFragment`对象，该对象可通过控件id查找`FragmentManager`获取，之后获取`NavHostFragment`的`NavController`对象；

   3. 使用`MvvmNavigationExtensions`拓展类的`setupWithNavController`方法建立`NavController`对象和视图对象的关联。

   > 注意事项：**在调用setupWithNavController方法关联后，Navigation导航在同一菜单上的fragment之间的跳转的生命周期将发生如下变化**。
>
   > 当从菜单的主页fragment（即startDestination对应的fragment）跳转到同一菜单上的其他fragment时：（点击菜单其他图标）
>
   > * 主页fragment执行截止到`onViewDestroyed()`的生命周期步骤，View虽然已经销毁，但主页fragment容器并未销毁；
   > * 其他fragment执行截止到`onResume()`的生命周期步骤，创建容器和View。
   >
   > 当从同一菜单上的其他fragment跳转至菜单的主页fragment（即startDestination对应的fragment）时：（点击菜单主页图标）
   >
   > * 其他fragmen执行执行截止到`onDetach()`的生命周期步骤，View和容器都被销毁；
   > * 之前的主页fragment执行截止到`onDetach()`的生命周期步骤，View和容器都被销毁，之后堆栈重新压入新的主页fragment，执行截止到`onResume()`的生命周期步骤，创建容器和View。
   >
   > 当从同一菜单上的其他fragment跳转至菜单的另一其他fragment（即startDestination对应的fragment）时：（点击菜单另一其他图标）
   >
   > * 其他fragment执行截止到`onDetach()`的生命周期步骤，View和容器都被销毁；
   > * 另一其他fragment执行截止到`onResume()`的生命周期步骤，创建容器和View。
   >
   > 当从同一菜单上的其他fragment回退至菜单的主页fragment（即startDestination对应的fragment）时：（在其他fragment中回退）
   >
   > * 其他fragment执行截止到`onDetach()`的生命周期步骤，View和容器都被销毁；
   > * 主页fragment重新执行从`onCreateView()`到`onViewCreate()`的生命周期步骤，容器不变，但View重新创建。



### 6.MVVM架构中不同数据类的使用场景

> MVVM架构的本质是**数据驱动**，因此对于不同数据类的特点和使用场景开发者要有相当清楚的认知。

这里将MVVM架构中常用的数据类根据其特点和使用场景分为以下几种类型：

* Java和Android提供的基本、包装类型以及常见数据结构（不包含Jetpack额外提供的观察者数据类）：

  ​	基本所有场景都能使用，但改变时无法驱动UI变化，即无法通知DataBinding更新UI。

* ObservableXXX原生数据类和`MvvmState`数据类：

  ​	基本所有场景都能使用，特别是**VO视图数据类**，改变时会立即通知DataBinding驱动UI变化，但缺点是开发者监听其变化较难，而且该类型数据**缺乏生命周期**的概念，所有变化都会及时反馈，可能会破坏部分需要在生命周期生效后再发生变化的业务场景。

  ​	`MvvmState`数据类相较于ObservableXXX原生数据类提供了更多功能，强制要求提供非空初值，规避了空安全问题，同时也支持开启或关闭数据防抖功能，减少UI在数据抖动时重新绘制造成的性能损耗，因此**在MVVM架构中推荐使用`MvvmState`数据类替代ObservableXXX原生数据类**。

* LiveData的所有原生数据类及Mvvm依赖库中提供的所有相关数据类：

  ​	LiveData相关类的UML类图如下所示：

  ![LiveData类及其相关类类图](https://gitee.com/soulkun/typora_images/raw/master/images/LiveData类及其相关类类图.png)

  * `LiveData`抽象泛型类和`MvvmPerfectLiveData`抽象泛型类：

    ​	`LiveData`抽象泛型类和`MvvmPerfectLiveData`抽象泛型类是MVVM架构中LiveData相关类的核心类，其中实现了数据的赋值和获取，以及最为重要的结合生命周期监听数据变化，但**要牢记其数据赋值方法可见范围为`protected`**，即本身和子类才能访问，该类仅能获取值以及监听数据变化，外部想要赋值只能使用其子类，通过权限收缩完成了消息分发结果同步的一致性，防止唯一可信源的不可信。

    ​	`MvvmPerfectLiveData`抽象泛型类在继承`LiveData`抽象泛型类的基础上提供了更多功能，允许调用者决定是否监听粘性事件，还支持数据防空值观察、防抖处理以及获取上一次数值的功能，因此**MVVM架构在非双向绑定的其他场景中推荐使用`MvvmPerfectLiveData`抽象泛型类及其子类替代`LiveData`抽象泛型类及其子类**。

  * `MutableLiveData`可变数据类、`MvvmMutableLiveData`可变数据类和`MvvmMutablePerfectLiveData`可变数据类：

    ​	该类型的类相较于之前的抽象泛型类具有**更开放的权限力度**，即允许外部其对进行赋值，因此**一般我们在初始化时直接使用该类型的三种数据类，而对外提供数据类则根据业务权限力度提供仅观察的抽象父类或允许赋值的子类**。

    ​	`MutableLiveData`可变数据类直接继承于`LiveData`抽象泛型类，**`MvvmMutableLiveData`可变数据类是`MutableLiveData`可变数据类的子类，额外提供了数据防空值观察、防抖处理以及获取上一次数值的功能，但不能解决粘性事件**，而`MvvmMutablePerfectLiveData`可变数据类是继承于`MvvmPerfectLiveData`抽象泛型类，因此`MvvmMutablePerfectLiveData`可变数据类继承父类具有最多的功能，而`MvvmMutableLiveData`可变数据类在继承`MutableLiveData`类的基础上能够允许双向绑定。综上所述，**MVVM架构在双向绑定场景上推荐使用`MvvmMutableLiveData`可变数据类，其他场景一律推荐使用`MvvmMutablePerfectLiveData`可变数据类**。

  * `MediatorLiveData`中间数据类和`MvvmMediatorPerfectLiveData`中间数据类：

    ​	该类型的类都是直接继承于相应的可变数据类，因此在抽象类的权限上允许直接赋值，更重要的是**该类型拥有统一管理和监听其他LiveData相关类的功能**，即支持在自身绑定生命周期后监听其他LiveData相关类的数据变化并执行自身的逻辑。**在某一状态监听多个状态变化的场景中推荐使用`MvvmMediatorPerfectLiveData`中间数据类监听其他LiveData状态，最终决定自身数据。**

  * `MvvmAndMediatorPerfectLiveData`与门中间数据类：

    ​	该类是继承于`MvvmMediatorPerfectLiveData`中间数据类的与门逻辑数据类，在其父类的基础功能上额外支持监听旗下所有LiveData的数据变化，当且仅当全部数据的最近一次变化满足与门条件返回True时，该类才会返回True，否则为False，**适合在多个布尔值数据变化决定最终布尔值数据状态的场景中使用**。

* `MvvmRecyclerViewAdapterObservableList`接口类及其ArrayList子类：

  ​	该类及其子类是基于MVVM数据驱动的思想下设计的**专门适配于MVVM依赖库中RecyclerView适配器相关类的可观察更新的数据列表**，提供了一系列observableXXX方法，解决了平常使用时需要同时对于数据列表以及适配器进行更新的繁琐的操作，让开发者从中解脱出来，专注于对真正数据的操作。
