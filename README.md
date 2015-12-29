# YLListView
YLListView仿IOS弹簧效果的ListView

![](https://raw.githubusercontent.com/yll2wcf/YLListView/master/gif/1.gif)

    private YLListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (YLListView) findViewById(R.id.listView);
        // 不添加也有默认的头和底
        View topView=View.inflate(this,R.layout.top,null);
        listView.addHeaderView(topView);
        View bottomView=new View(getApplicationContext());
        listView.addFooterView(bottomView);

        // 顶部和底部也可以固定最终的高度 不固定就使用布局本身的高度
        listView.setFinalBottomHeight(100);
        listView.setFinalTopHeight(100);

        listView.setAdapter(new DemoAdapter());

        //YLListView默认有头和底  处理点击事件位置注意减去
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position=position-listView.getHeaderViewsCount();
            }
        });


    }
