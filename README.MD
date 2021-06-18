[toc]

# 项目结构

## Java

### main

#### adapter (RecyclerView适配器)

##### TokenAdapter.java

WebFragmentToken适配器，用于网页列表RecyclerView

#### client (WebView客户端)

##### MyWebChromeClient.java

重载的WebChromeClient，创建需要传递回调

##### MyWebViewClient.java

重载的WebViewClient，创建需要传递回调

#### dao (Room数据库接口)

##### BrowserDao.java

数据库接口

#### database (Room数据库)

##### BrowserDatabase.java

数据库

#### domain (Room数据库实体对象)

##### Bookmark.java

书签

##### Converters.java

数据转换器long与Date互转

##### History.java

历史

##### User.java

用户

#### model (视图和模型)

##### SuggestItem.java

建议视图，从历史记录和书签的联合视图

##### WebFragmentToken.java

存储WebFragment的标签、标题、url、图标

#### repository (数据库代理仓库)

##### BrowserRepository

代理访问数据库

#### ui (Fragment视图)

#### ui.blank

##### BlankFragment.java

空白Fragment，不要动

#### ui.bookmark

##### BookmarkFragment.java

书签Fragment

#### ui.download

##### DownloadFragment.java

下载Fragment

#### ui.history

##### HistoryFragment.java

历史Fragment

#### ui.setting

##### SettingFragment.java

设置Fragment

#### ui.web

##### WebFragment.java

网页Fragment

##### WebViewModel.java

数据持久化和接口

#### utils (工具类)

##### WebViewUtils.java

初始化WebView和销毁

#### view (自定义View)

##### PagePopup

标签管理弹出窗口

#### ContainerActivity.java

管理功能页面的Activity

#### MainActivity.java

浏览器Activity

#### MainViewModel.java

MainActivity持久化和接口

#### WebFragmentManager.java

WebFragment管理器



## assets

### home.html

首页

### photo.png

图片



## res

### layout

#### activity_container.xml

ContainerActivity布局文件

#### activity_main.xml

MainActivity布局文件

#### app_bar_main.xml

MainActivity的应用栏布局文件

#### fragment_bookmark.xml

BookmarkFragment布局文件

#### fragment_download.xml

DownloadFragment布局文件

#### fragment_history.xml

HistoryFragment布局文件

#### fragment_web.xml

WebFragment布局文件

#### item_page.xml

网页标签布局文件

#### item_suggest.xml

地址栏建议项布局文件

#### navigation_bar.xml

MainActivity导航栏

#### popup_page.xml

标签页弹出窗布局文件

#### suggest_search.xml

建议搜索

#### suggest_url.xml

建议通过url访问

#### suggest_view.xml

MainActivity建议视图

### navigation

#### navigation_container.xml

ContainerActivity导航文件，Fragment在这里注册导航id

### xml

#### preference_setting.xml

用于SettingFragment的设置视图
