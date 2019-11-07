#!/usr/bin/python3
# -*- coding: UTF-8 -*-
# 文件名：net_test.py

from urllib import request as urlRequest
import requests
from fake_useragent import UserAgent

# 0.可以开始了，从官方入手还是可以滴
# https://docs.python.org/3/library/urllib.request.html#examples

# 1.获取git项目的一个readme吧
try:
    # with确保使用过程中不管是否发生异常都会执行必须的’ 清理 ‘操作，并释放资源
    with urlRequest.urlopen("https://heyclock.gitee.io/doc/Share/README.md") as jianshu:
        while True:
            data = jianshu.read(1024)
            if not data:
                break;
            print(len(data))
            print(data.decode('utf-8'))
except Exception as err:
    print(err)

# 2.获取简书一篇文章页面内容【简书爬取需要一些参数认证，猜想可能有cookie, user-agent之类的吧...】
# 伪装一个代理
ua = UserAgent()
headers = {
    'User-Agent': ua.random
}
# 方式一、 requests + fake_useragent - 伪装浏览器
if True:
    url = 'https://www.jianshu.com/p/2e190438bd9c'
    response = requests.get(url, headers=headers)
    print(response.content.decode('utf-8'))
    print(response.status_code)

# 方式二、python3.x的urllib+user-agent(从浏览器开发者工具Network/XHR/下找到的user-agent作为浏览器标识)
if False:
    url_jianshu = 'https://www.jianshu.com/p/85a3004b5c06'
    try:
        # with确保使用过程中不管是否发生异常都会执行必须的’ 清理 ‘操作，并释放资源
        # python爬虫中带上Cookie，Referer，User-Agent -> https://blog.csdn.net/qq_40187062/article/details/86356955
        # python爬虫之伪装User-Agent -> https://www.jianshu.com/p/74bce9140934
        headers_fromWeb_devloptool = {
            # 'Accept': '*/*',
            # 'Accept-Language': 'zh-CN,zh;q=0.9',
            # 'Accept-Encoding': 'gzip, deflate, br',
            # 'Connection': 'keep-alive',
            # 'Cookie': 'BIDUPSID=8640A1C37FE0690CCFD0ADC95CDD0614; PSTM=1573012288; BAIDUID=8640A1C37FE0690C2FF67C0B307E1236:FG=1; BDORZ=B490B5EBF6F3CD402E515D22BCDA1598; HMACCOUNT=67BE1EE84C6E8606; H_PS_PSSID=1427_21089_18560_29568_29220_28702; delPer=0; PSINO=7; HMVT=6bcd52f51e9b3dce32bec4a3997715ac|1573106717|',
            # 'Referer': 'https://www.jianshu.com/p/2e190438bd9c',
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.70 Safari/537.36',
        }
        req = urlRequest.Request(url_jianshu, None, headers_fromWeb_devloptool)
        # req.add_header("xxx", "xxxx") 还可以继续添加header参数
        with urlRequest.urlopen(req) as jianshu:
            while True:
                data = jianshu.read(1024)
                if not data:
                    break;
                print(len(data))
                print(data.decode('utf-8', 'ignore'))  # "ignore"解决 'utf-8' codec can't decode byte..
    except Exception as err:
        print(err)

# 请求带参数 - 来自官方案例
import urllib.parse
params = urllib.parse.urlencode({'wd': "皮皮虾", 'ie': "UTF-8"})  # 字典提供https://www.baidu.com/s?wd=皮皮虾&ie=UTF-8
# post方法
# params = params.encode('utf-8')
# with urlRequest.urlopen("https://www.baidu.com/s", params) as f:
#     print(f.read().decode('utf-8'))
# get方法
urlBds = "http://www.baidu.com/s?%s" % params  # 如果用https的话，爬到的是一个重定向的页面
with urlRequest.urlopen(urlBds) as f:
    print(f.read().decode('utf-8'))


# other1. 了解一下：认证简单实践HTTP Authentication - 来自官方案例。如果搞这个行业，有天你可能会去研究更深的用法，如何应对反爬虫等等
# Create an OpenerDirector with support for Basic HTTP Authentication...
if False:
    urlAuthent = 'http://pythonscraping.com/pages/auth/login.php'  # 目前没有搞服务器那头，也不知道哪里有这种url，写法就是这么个写法
    author_handler = urlRequest.HTTPBasicAuthHandler()
    author_handler.add_password(None, urlAuthent, user='ryan', passwd='password')
    opener = urlRequest.build_opener(author_handler)
    # ...and install it globally so it can be used with urlopen.
    urlRequest.install_opener(opener)
    with urlRequest.urlopen(urlAuthent) as f:
        print(f.read().decode('utf-8'))

# other2. 请求下公司的登录方法 - 今天基本就差不了，回头就去开始去xPath做分析实践
urlCompany = "https://www.xxxxx.com/"  # xxx/xxx/xxxx - 这个具体方法就不公布了
params = urllib.parse.urlencode({"login_name":"151xxxxxxx", "password": "123456"})
urlCompany = urlCompany + "?" + params
print("公司登录方法:" + urlCompany)
with urlRequest.urlopen(urlCompany) as f:
    jsonStr = f.read().decode('utf-8')
    print(jsonStr)
    # other3. 顺便解析下Json
    import json
    dataDic = json.loads(jsonStr)
    print(type(dataDic), dataDic)
    print("dataDic['code']: ", dataDic['code'])
    print("dataDic['data']: ", dataDic['data'])
    print(type(dataDic['data']))
    print(dataDic["data"]["display_name"])
    print(dataDic["data"]["user_phone"])