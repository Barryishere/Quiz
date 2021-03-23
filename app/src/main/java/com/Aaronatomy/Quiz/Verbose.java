package com.Aaronatomy.Quiz;

/**
 * Created by AaronAtomy on 2018/4/5.
 * Android
 */

public class Verbose {
    // 网页不完整

    // OKHttp的Response的body和header访问一次后会自动调用close()，要多次使用先复制给其他变量

    // OkHttp出现跳转/调用次数过多，可能是因为网页中包含了iFrame，这个时候就需要在请求头中包
    // 含Referer信息（即 包含这个iFrame的原网页的地址）就可以避免这种错误

    // LinearLayout的orientation属性会导致子控件设置的layout_gravity部分失效；比如orientation为
    // vertical则子控件的layout_gravity设置为center_vertical，top，bottom，start，end是无效的；
    // 反过来orientation为horizontal，子控件设置layout_gravity为center_horizontal，left，bottom，
    // start，end是无效的

    // RelativeLayout内两个子控件占据了对角线位置后会导致RelativeLayout的wrap_content属性失效

    // 防止EditText自动获得焦点，可以将focusable，focusableInTouchMode，clickable设置为true

    // EditText/TextView的ellipsize属性有bug，单行文本在某些情况下不显示省略号，目前比较好的方法
    // 只有重写了

    // 做RecyclerView点击事件的时候千万不要再onBindViewHolder()直接获取Position，这样获得的值
    // 基本是错的，应该在点击事件获取，比如在View.onClick中获取，然后把值传给自己写的回调接口，
    // 再在调用方实现这个回调就可以获取正确的值，且将具体实现交给调用方了

    // 防止键盘遮挡选择activity为adjustToFit时，需要保证布局中被调整的部分被ScrollView包裹且至少
    // 某个父级容器设置了fitSystemWindow属性

    // 想让嵌套在 NestedScrollView 中的AppBar仅仅在某几个特定的页面中展开仅仅实现 ViewPager 的
    // OnScrollChangeListener 并在里面调用 AppBar的isExpanded是不够的，因为这样用户向下滑动还
    // 是能展开AppBar；可行的做法是将AppBar的内容用一个容器包裹起来（推荐ConstraintLayout，可
    // 以通过设置bias实现类似视差滑动的效果）并设置Appbar的layout_height为wrap_content，然后在
    // OnScrollChangeListener内通过ValueAnimator对容器的高度动态设置，就可以实现只在特定页面才
    // 能展开AppBar的效果了，还顺便把动画搞定了

    // 控件如果设置了wrap_content，那么他wrap的不仅仅是其子控件，还包括他的背景图（bitmap）简单来
    // 说控件会被背景图撑大，这显然不是我们想要的效果，通过设置属性基本无解，可以在外面在套个FrameLayout
    // 或者把高度写死，但效果都不是很好，或者干脆重写View

    // 某些机型某些安卓版本会因为使用了特定版本PhotoShop修过的图而崩溃，一开始以为是Fresco的
    // 锅，不知道是不是个例

    // 对集合进行增删时尽量避免foreach进行遍历，否则很容易抛ConcurrentModificationException异
    // 常

    // 获取当前系统时间
    // long currentTime = System.currentTimeMillis();
    // SimpleDateFormat formatter = new SimpleDateFormat("yyyy年-MM月dd日-HH时mm分ss秒", Locale.CHINA);
    // Date date = new Date(currentTime);
    // Toast.makeText(this, date.toString(), Toast.LENGTH_SHORT).show();
}
