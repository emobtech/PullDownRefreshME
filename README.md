# PullDownRefreshME
PullDownRefreshME is a LWUIT component that implements a pull-down gesture to refresh data.

##Articles
* [Pull Down to Refresh for LWUIT](http://j2megroup.blogspot.com.br/2013/07/pull-down-to-refresh-for-lwuit.html)
* [Pull Down to Refresh v1.1 for LWUIT](http://j2megroup.blogspot.com.br/2014/01/pull-down-to-refresh-v11-for-lwuit.html)

##Sample Code

```java
...
Form form = new Form("PullDownRefresh") {};
form.setLayout(new BorderLayout());
//
List list = new List(new String[] {...});
list.setRenderer(new DefaultListCellRenderer());
//
final PullDownRefresh refresher = new PullDownRefresh();
refresher.addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent evt) {
        new Thread() {
            public void run() {
                // Perform some process
                //
                refresher.endRefreshing();
            }
        }.start();
    }
});
//
form.addComponent(BorderLayout.NORTH, refresher);
form.addComponent(BorderLayout.CENTER, list);
//
form.show();
...
```
