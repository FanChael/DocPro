#!/usr/bin/python3
# -*- coding: UTF-8 -*-
# 文件名：xpath.py

from lxml import etree
from urllib import request

# 1. 先来个简单的吧 缺</body> </html>，后面用etree.tostring可以补全！
url = '''
<!DOCTYPE html>
<html>
<head lang="en">
    <meta charset="UTF-8">
    <title>网页标题</title>
</head>
<body>
<div id="content">
    <ul id="message">
        <li>这是第一条信息</li>
    </ul>
    <ul id="empty">
        <li>空数据</li>
    </ul>
    <div id="urls">
        <a href="http://www.neu.edu.cn/">东北大学</a>
    </div>
</div>
'''
data = url

# 2. 解析首页列表
if data:
    # print(data)
    # 转换为html对象，以便进行path查找
    html_object = etree.HTML(data)
    # 补全网页字符串
    last_html_data = etree.tostring(html_object)
    # 再次转换为html对象，以便进行path查找
    html_object = etree.HTML(last_html_data)
    # 利用浏览器插件Chrome插件XPath Helper获取标签格式，然后进行查找
    # 格式类似： '//*[@id="page"]/div/div/div[2]/div[2]/div[1]/div[2]'
    '''
    // 定位根节点
    / 往下层寻找
    / text()提取文本内容,  比如：/li/text() - 提取li的文本内容
    /@XXX提取XXX属性内容, 比如： a/@href - 提取a的href属性
    [@id="xx"]获取指定id属性内容, 比如: ul[@id="name_list"] - 提取id为name_list的ul列表
    '''
    result = html_object.xpath('//*')  # '//'表示获取当前节点子孙节点，'*'表示所有节点，'//*'表示获取当前节点下所有节点
    for item in result:
        print(item)
        ''' ......
            <Element div at 0x160cd84eb48>
            <Element ul at 0x160cdc90e08>
            ....
        '''
    # 获取ui的li的文本内容
    path_data = html_object.xpath('//div[@id="content"]/ul/li/text()')
    for item in path_data:
        print(item)
    # 获取指定ul的li的内容
    path_data = html_object.xpath('//div[@id="content"]/ul[@id="message"]/li/text()')
    for item in path_data:
        print(item)
    # 当前标签比较单一的时候，可以不用id指定，直接找想要的值 - 灵活应对吧
    path_data = html_object.xpath('//div/div/a/@href')
    for item in path_data:
        print(item)

# 再来个获取公开政策的网站 - 需要伪装成浏览器访问；不知道这样举例恰当不，政策网站还是不要乱爬吧！算了，改成大学的吧..我母校...
from fake_useragent import UserAgent
ua = UserAgent()
headers = {
    'User-Agent': ua.random
}
if True:
    url = 'http://www.neu.edu.cn/'
    req = request.Request(url, None, headers)
    data = ''
    with request.urlopen(req) as uf:
        while True:
            data_temp = uf.read(1024)
            if not data_temp:
                break
            data += data_temp.decode('utf-8', 'ignore')

    # 2. 解析获取需要内容
    if data:
        # 转换为html对象，以便进行path查找
        html_object = etree.HTML(data)
        # 补全网页吧
        last_html_data = etree.tostring(html_object)
        # 再次转换为html对象，以便进行path查找
        html_object = etree.HTML(last_html_data)
        # 从网页工具xPath里面去获取规则，根据路径一层层往下找就行（这样能精准一些，但是如果标识都一样，且很多，数据就会很多
        path_data = html_object.xpath('//*[@id="talented"]/div[2]/ul/li/span/a/@href')  # //*[@id="talented"]/div[2]/ul/li/span/a/@href
        # 专注人才培养[@id="talented"]下面的列表
        for item in path_data:
            print(item)  # 得到路径补全自己去特殊补全
